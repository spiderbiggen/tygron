package tygronenv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Translator;
import eis.iilang.Parameter;
import eis.iilang.Percept;
import nl.tytech.core.client.event.EventIDListenerInterface;
import nl.tytech.core.client.event.EventManager;
import nl.tytech.core.event.Event;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.event.EventListenerInterface;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.item.Setting;

/**
 * Listen to entity events and store them till they are needed. Thread safe
 * because callbacks and calls from GOAL will be asynchronous.
 * 
 * @author W.Pasman
 *
 */
public class EntityEventHandler implements EventListenerInterface, EventIDListenerInterface {

	private Translator translator = Translator.getInstance();
	/**
	 * The collected percepts. Access this always through
	 * {@link #addPercepts(EventTypeEnum, List)} and {@link #getPercepts()}.
	 * 
	 * FIXME collect Events and evaluate the percept lazy.
	 */
	private Map<EventTypeEnum, List<Percept>> collectedPercepts = new HashMap<>();

	public EntityEventHandler() {
		EventManager.addListener(this, MapLink.STAKEHOLDERS, MapLink.FUNCTIONS, MapLink.BUILDINGS);
		EventManager.addEnumListener(this, MapLink.SETTINGS, Setting.Type.MAP_WIDTH_METERS);
	}

	@Override
	public void notifyEnumListener(Event event, Enum<?> enhum) {
		EventTypeEnum type = event.getType();
		Object[] contents = event.getContents();
		switch (type.name()) {
		case "SETTINGS":
			createPercepts((List<?>) contents[1], type);
			break;
		default:
			System.out.println("WARNING. EntityEventHandler ENUM received unknown event:" + event);
			return;

		}
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
	public void notifyIDListener(Event event, Integer id) {
		System.out.println("ID EVENT:" + event);
	}

	@Override
	public void notifyListener(Event event) {
		EventTypeEnum type = event.getType();
		Object[] contents = event.getContents();

		switch (type.name()) {
		case "STAKEHOLDERS":
			createPercepts((List<?>) contents[1], type);
			break;
		case "FUNCTIONS":
			createPercepts((List<?>) contents[1], type);
			break;
		case "BUILDINGS":
			createPercepts((List<?>) contents[1], type);
			break;
		default:
			System.out.println("WARNING. EntityEventHandler received unknown event:" + event);
			return;

		}
	}

	/**
	 * Create percepts contained in a ClientItemMap array and add them to the
	 * {@link #collectedPercepts}.
	 * 
	 * @param map
	 *            list of ClientItemMap elements.
	 * @param type
	 *            the type of elements in the map.
	 */
	private void createPercepts(List<?> list, EventTypeEnum type) {
		List<Percept> percepts = new ArrayList<Percept>();
		Parameter[] parameters = null;
		try {
			parameters = translator.translate2Parameter(list);
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
