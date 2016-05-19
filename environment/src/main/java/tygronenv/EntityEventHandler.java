package tygronenv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Translator;
import eis.exceptions.EntityException;
import eis.iilang.Parameter;
import eis.iilang.Percept;
import nl.tytech.core.client.event.EventManager;
import nl.tytech.core.event.Event;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.event.EventListenerInterface;
import nl.tytech.core.net.Network;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.structure.ClientItemMap;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.item.Building;
import nl.tytech.data.engine.item.Function;
import nl.tytech.data.engine.item.Setting;
import nl.tytech.data.engine.item.Stakeholder;
import tygronenv.translators.J2Building;
import tygronenv.translators.J2ExtBuilding;

/**
 * Listen to entity events and store them till they are needed. Thread safe
 * because callbacks and calls from GOAL will be asynchronous. The events that
 * are reported are set up in the constructor.
 * 
 * @author W.Pasman
 *
 */
public class EntityEventHandler implements EventListenerInterface {

	private Translator translator = Translator.getInstance();
	private static final String ENTITY = "entity";
	/**
	 * The collected percepts. Access this always through
	 * {@link #addPercepts(EventTypeEnum, List)} and {@link #getPercepts()}.
	 * 
	 * FIXME collect Events and evaluate the percept lazy.
	 */
	private Map<EventTypeEnum, List<Percept>> collectedPercepts = new HashMap<>();
	private TygronEntity entity;

	public EntityEventHandler(TygronEntity entity) {
		this.entity = entity;
		EventManager.addListener(this, MapLink.STAKEHOLDERS, MapLink.FUNCTIONS, MapLink.BUILDINGS, MapLink.SETTINGS);
		EventManager.addListener(this, Network.ConnectionEvent.FIRST_UPDATE_FINISHED);
	}

	/**
	 * Add new percept to the collection.
	 * 
	 * @param type
	 * @param percepts
	 */
	private synchronized void addPercepts(EventTypeEnum type, List<Percept> percepts) {
		collectedPercepts.put(type, percepts);
	}

	/**
	 * Get the percepts and clean our {@link #collectedPercepts}.
	 * 
	 * @return percepts collected since last call to this
	 */
	public synchronized Map<EventTypeEnum, List<Percept>> getPercepts() {
		Map<EventTypeEnum, List<Percept>> copy = collectedPercepts;
		collectedPercepts = new HashMap<>();
		return copy;
	}

	@Override
	public void notifyListener(Event event) {
		try {
			notifyListener1(event);
		} catch (EntityException e) {
			e.printStackTrace(); // can we do more?
		}
	}

	private void notifyListener1(Event event) throws EntityException {

		EventTypeEnum type = event.getType();

		if (type instanceof MapLink) {
			switch ((MapLink) type) {
			case STAKEHOLDERS:
				createPercepts(event.<ItemMap<Stakeholder>> getContent(MapLink.COMPLETE_COLLECTION), type);
				break;
			case FUNCTIONS:
				createPercepts(event.<ItemMap<Function>> getContent(MapLink.COMPLETE_COLLECTION), type);
				break;
			case BUILDINGS:
				createBuildingPercepts(event.<ItemMap<Building>> getContent(MapLink.COMPLETE_COLLECTION), type);
				break;
			case SETTINGS:
				createPercepts(event.<ItemMap<Setting>> getContent(MapLink.COMPLETE_COLLECTION), type);
				break;
			default:
				System.out.println("WARNING. EntityEventHandler received unknown event:" + event);
				return;
			}
		} else if (type == Network.ConnectionEvent.FIRST_UPDATE_FINISHED) {
			// entity is ready to run! Report to EIS
			entity.notifyReady(ENTITY);
		}
	}

    /**
     * Create percepts pertaining to buildings contained in a ClientItemMap array and add them to the
     * {@link #collectedPercepts}.
     *
	 * @param itemMap
	 *            list of ClientItemMap elements.
	 * @param type
	 *            the type of elements in the map.
     */
    private <T extends Building> void createBuildingPercepts(ItemMap<T> itemMap, EventTypeEnum type) {

        List<Percept> percepts = new ArrayList<>();
        String typeString = type.name().toLowerCase();
        try {
            percepts.add(createBuildingPercept(new ArrayList<>(itemMap.values()), typeString));
        } catch (TranslationException e) {
            e.printStackTrace();
        }
        String extTypeString = "ext" + typeString;
        try {
            // Set translator to the extended building translator to get more info in the percept
            translator.registerJava2ParameterTranslator(new J2ExtBuilding());
            percepts.add(createBuildingPercept(new ArrayList<>(itemMap.values()), extTypeString));
        } catch (TranslationException e) {
            e.printStackTrace();
        }
        // Reset translator to use the standard building translator.
        translator.registerJava2ParameterTranslator(new J2Building());
        addPercepts(type, percepts);
    }

    /**
     * Method to create A Building percept based on the given \<T\>
     * @param items all items that should be put in the percept
     * @param type Lowercase string representation of the event type
     * @param <T> The type of the items in the list. Should extend {@link Building}
     * @return a new {@link Percept} containing all the info from The items
     * @throws TranslationException
     */
    private <T extends Building> Percept createBuildingPercept(List<T> items, String type) throws TranslationException {
        Parameter[] parameters = translator.translate2Parameter(items);
        return new Percept(type, parameters);
    }

	/**
	 * Create percepts contained in a ClientItemMap array and add them to the
	 * {@link #collectedPercepts}.
	 * 
	 * @param itemMap
	 *            list of ClientItemMap elements.
	 * @param type
	 *            the type of elements in the map.
	 */
	private <T extends Item> void createPercepts(ItemMap<T> itemMap, EventTypeEnum type) {
		ArrayList<T> items = new ArrayList<T>(itemMap.values());
		List<Percept> percepts = new ArrayList<Percept>();
		Parameter[] parameters = null;
		try {
			parameters = translator.translate2Parameter(items);
		} catch (TranslationException e) {
			e.printStackTrace();
		}
		if (parameters != null) {
			percepts.add(new Percept(type.name().toLowerCase(), parameters));
		}
		addPercepts(type, percepts);

	}

	public void stop() {
		EventManager.removeAllListeners(this);
	}

	/**
	 * @return true if cache is ready for use (currently it must have
	 *         STAKEHOLDERS).
	 */
	private boolean isReady() {
		ItemMap<Item> map = EventManager.getItemMap(MapLink.STAKEHOLDERS);
		return map != null && map.size() > 0;
	}

	/**
	 * Wait till critical elements are available: see {@link #isReady()}. But
	 * wait at most 10 seconds.
	 */
	public void waitForReady() {
		int WAITTIME = 100;
		int totaltime = 10000; // milliseconds.
		while (!isReady()) {
			totaltime -= WAITTIME;
			if (totaltime < 0) {
				throw new IllegalStateException("EventManager initialization timed out.");
			}
			try {
				Thread.sleep(WAITTIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
