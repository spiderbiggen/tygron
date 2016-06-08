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
import nl.tytech.core.client.event.SlotEvent;
import nl.tytech.core.event.Event;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.event.EventListenerInterface;
import nl.tytech.core.net.Network;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.item.ActionLog;
import nl.tytech.data.engine.item.ActionMenu;
import nl.tytech.data.engine.item.Building;
import nl.tytech.data.engine.item.Function;
import nl.tytech.data.engine.item.Land;
import nl.tytech.data.engine.item.PopupData;
import nl.tytech.data.engine.item.Setting;
import nl.tytech.data.engine.item.Stakeholder;
import nl.tytech.data.engine.item.Zone;
import nl.tytech.util.logger.TLogger;

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
	private EntityEventListener entity;
	private Integer connectionID;

	public EntityEventHandler(EntityEventListener entity, Integer connectionID) {
		this.entity = entity;
		this.connectionID = connectionID;

		EventManager.addListener(this, MapLink.STAKEHOLDERS, MapLink.ACTION_MENUS, MapLink.ACTION_LOGS,
				MapLink.FUNCTIONS, MapLink.BUILDINGS, MapLink.SETTINGS, MapLink.ZONES, MapLink.LANDS, MapLink.POPUPS);
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
			if (!isForMe(event)) {
				return;
			}
			notifyListener1(event);
		} catch (EntityException e) {
			e.printStackTrace(); // can we do more?
		}
	}

	private boolean isForMe(Event event) {
		if (event instanceof SlotEvent) {
			SlotEvent slotEvent = (SlotEvent) event;
			return connectionID.equals(slotEvent.getConnectionID());
		}
		return true;
	}

	private void notifyListener1(Event event) throws EntityException {

		EventTypeEnum type = event.getType();

		if (type instanceof MapLink) {
			switch ((MapLink) type) {
			case ACTION_LOGS:
				createPercepts(event.<ItemMap<ActionLog>> getContent(MapLink.COMPLETE_COLLECTION), type);
				break;
			case ACTION_MENUS:
				createPercepts(event.<ItemMap<ActionMenu>> getContent(MapLink.COMPLETE_COLLECTION), type, "actions");
				break;
			case BUILDINGS:
				createPercepts(event.<ItemMap<Building>> getContent(MapLink.COMPLETE_COLLECTION), type);
				break;
			case FUNCTIONS:
				createPercepts(event.<ItemMap<Function>> getContent(MapLink.COMPLETE_COLLECTION), type);
				break;
			case SETTINGS:
				createPercepts(event.<ItemMap<Setting>> getContent(MapLink.COMPLETE_COLLECTION), type);
				break;
			case STAKEHOLDERS:
				createPercepts(event.<ItemMap<Stakeholder>> getContent(MapLink.COMPLETE_COLLECTION), type);
				break;
			case ZONES:
				createPercepts(event.<ItemMap<Zone>> getContent(MapLink.COMPLETE_COLLECTION), type);
				break;
			case LANDS:
				createPercepts(event.<ItemMap<Land>> getContent(MapLink.COMPLETE_COLLECTION), type);
				break;
			case POPUPS:
				// TODO filter out only popups for the entity.
				createPercepts(event.<ItemMap<PopupData>> getContent(MapLink.COMPLETE_COLLECTION), type, "requests");
				break;
			default:
				TLogger.warning("EntityEventHandler received unknown event:" + event);
				return;

			}
		} else if (type == Network.ConnectionEvent.FIRST_UPDATE_FINISHED) {
			System.out.println("received  FIRST_UPDATE_FINISHED in " + this);
			// entity is ready to run! Report to EIS
			entity.notifyReady(ENTITY);
		}
	}

	/**
	 * see {@link #createPercepts(ItemMap, EventTypeEnum, String)}. The
	 * perceptname is {@link EventTypeEnum#name()}.
	 *
	 * @param itemMap
	 * @param type
	 */
	private <T extends Item> void createPercepts(ItemMap<T> itemMap, EventTypeEnum type) {
		createPercepts(itemMap, type, type.name().toLowerCase());
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

	private <T extends Item> void createPercepts(ItemMap<T> itemMap, EventTypeEnum type, String perceptname) {
		ArrayList<T> items = new ArrayList<T>(itemMap.values());
		List<Percept> percepts = new ArrayList<Percept>();
		Parameter[] parameters = null;
		try {
			parameters = translator.translate2Parameter(items);
		} catch (TranslationException e) {
			e.printStackTrace();
		}
		if (parameters != null) {
			percepts.add(new Percept(perceptname, parameters));
		}
		addPercepts(type, percepts);

	}

	public void stop() {
		EventManager.removeAllListeners(this);
	}

}
