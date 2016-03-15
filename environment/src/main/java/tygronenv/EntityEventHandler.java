package tygronenv;

import nl.tytech.core.client.event.EventIDListenerInterface;
import nl.tytech.core.client.event.EventManager;
import nl.tytech.core.event.Event;
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

	public EntityEventHandler() {
		EventManager.addListener(this, MapLink.STAKEHOLDERS);
		EventManager.addEnumListener(this, MapLink.SETTINGS, Setting.Type.MAP_WIDTH_METERS);
	}

	@Override
	public void notifyEnumListener(Event event, Enum<?> enhum) {
		System.out.println("ENUM EVENT:" + event);
	}

	@Override
	public void notifyIDListener(Event event, Integer id) {
		System.out.println("ID EVENT:" + event);
	}

	@Override
	public void notifyListener(Event event) {
		System.out.println("EVENT:" + event);

	}

	public void stop() {
		EventManager.removeListener(this);
		EventManager.removeIDListener(this);
	}

	private boolean hasStakeholder() {
		ItemMap<Item> map = EventManager.getItemMap(MapLink.STAKEHOLDERS);
		return map != null && map.size() > 0;
	}

	/**
	 * Wait till critical elements are available: STAKEHOLDERS
	 */
	public void waitForReady() {
		int WAITTIME = 100;
		int totaltime = 10000; // milliseconds.
		while (!hasStakeholder()) {
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
