package tygronenv;

import java.util.ArrayList;
import java.util.List;

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
 * Listen to entity events and forward into EIS
 * 
 * @author W.Pasman
 *
 */
public class EntityEventHandler implements EventListenerInterface, EventIDListenerInterface {

	private PerceptPipe pipe;
	private Translator translator = Translator.getInstance();

	public EntityEventHandler(PerceptPipe perceptPipe) {
		pipe = perceptPipe;
		EventManager.addListener(this, MapLink.STAKEHOLDERS, MapLink.FUNCTIONS);
		EventManager.addEnumListener(this, MapLink.SETTINGS, Setting.Type.MAP_WIDTH_METERS);
	}

	@Override
	public void notifyEnumListener(Event event, Enum<?> enhum) {
		EventTypeEnum type = event.getType();
		Object[] contents = event.getContents();
		List<Percept> percepts = null;
		switch (type.name()) {
		case "SETTINGS":
			percepts = createPercepts((List<?>) contents[1], type);
			break;
		default:
			System.out.println("WARNING. EntityEventHandler ENUM received unknown event:" + event);
			return;

		}
		pushAll(percepts);

	}

	@Override
	public void notifyIDListener(Event event, Integer id) {
		System.out.println("ID EVENT:" + event);
	}

	@Override
	public void notifyListener(Event event) {
		EventTypeEnum type = event.getType();
		Object[] contents = event.getContents();
		List<Percept> percepts;

		switch (type.name()) {
		case "STAKEHOLDERS":
			percepts = createPercepts((List<?>) contents[1], type);
			break;
		case "FUNCTIONS":
			percepts = createPercepts((List<?>) contents[1], type);
			break;
		default:
			System.out.println("WARNING. EntityEventHandler received unknown event:" + event);
			return;

		}

		pushAll(percepts);

	}

	/**
	 * Push all percepts into the percepts pipe.
	 * 
	 * @param percepts
	 *            list of percepts. Ignored if null.
	 */
	void pushAll(List<Percept> percepts) {
		if (percepts == null)
			return;
		for (Percept percept : percepts) {
			try {
				pipe.push(percept);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Create percepts contained in a ClientItemMap array
	 * 
	 * @param map
	 *            list of ClientItemMap elements.
	 * @param type
	 *            the type of elements in the map.
	 */
	private List<Percept> createPercepts(List<?> list, EventTypeEnum type) {
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
		return percepts;
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
