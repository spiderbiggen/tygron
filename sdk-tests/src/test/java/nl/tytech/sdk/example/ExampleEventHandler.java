package nl.tytech.sdk.example;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import nl.tytech.core.client.event.EventIDListenerInterface;
import nl.tytech.core.client.event.EventManager;
import nl.tytech.core.event.Event;
import nl.tytech.core.event.EventListenerInterface;
import nl.tytech.core.net.Network;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.item.Setting;
import nl.tytech.util.logger.TLogger;

public class ExampleEventHandler implements EventListenerInterface, EventIDListenerInterface {

	private boolean mapUpdate = false;
	private Map<MapLink, Boolean> mapLinkUpdated = new HashMap<>();
	/**
	 * goes true after FIRST_UPDATE_FINISHED comes
	 **/
	private boolean firstUpdate = false;

	public ExampleEventHandler() {
		EventManager.addListener(this, MapLink.class);
		EventManager.addEnumListener(this, MapLink.SETTINGS, Setting.Type.MAP_WIDTH_METERS);
		EventManager.addListener(this, Network.ConnectionEvent.FIRST_UPDATE_FINISHED);
	}

	public boolean isMapUpdated() {
		return mapUpdate;
	}

	public boolean isUpdated(MapLink mapLink) {
		Boolean result = mapLinkUpdated.get(mapLink);
		if (result == null) {
			result = Boolean.FALSE;
		}
		return result;
	}

	public boolean isUpdated(MapLink... mapLinks) {
		boolean result = true;
		for (MapLink mapLink : mapLinks) {
			result &= isUpdated(mapLink);
		}
		return result;
	}

	public void resetUpdate(MapLink... mapLinks) {
		for (MapLink mapLink : mapLinks) {
			resetUpdate(mapLink);
		}
	}

	public void resetUpdate(MapLink mapLink) {
		mapLinkUpdated.put(mapLink, false);
	}

	@Override
	public void notifyEnumListener(Event event, Enum<?> enhum) {

		if (enhum == Setting.Type.MAP_WIDTH_METERS) {
			Setting setting = EventManager.getItem(MapLink.SETTINGS, Setting.Type.MAP_WIDTH_METERS);
			TLogger.info("Map Width is set to: " + setting.getIntValue());
			mapUpdate = true;
		}
	}

	@Override
	public void notifyIDListener(Event arg0, Integer arg1) {

	}

	@Override
	public void notifyListener(Event event) {

		if (event.getType() instanceof MapLink) {
			mapLinkUpdated.put((MapLink) event.getType(), true);

			// (Frank) Print for debug
			Collection<Item> updates = event.getContent(MapLink.UPDATED_COLLECTION);
			if (event.getType() == MapLink.STAKEHOLDERS) {
				TLogger.info("Updated " + event.getType().name() + ": " + updates);
			}
		} else if (event.getType() == Network.ConnectionEvent.FIRST_UPDATE_FINISHED) {
			firstUpdate();
		}
	}

	/**
	 * Called when first update comes in
	 */
	private void firstUpdate() {
		firstUpdate = true;
	}

	/**
	 * Wait for FIRST_UPDATE_FINISHED
	 * 
	 * @param timeoutMs
	 *            the max time to wait (ms)
	 * @throws InterruptedException
	 *             if time-out occurs.
	 */
	public void waitForFirstUpdate(int timeoutMs) throws InterruptedException {
		// time to sleep if firstUpdate not yet
		final int SLEEPTIME = 100;

		while (!firstUpdate && timeoutMs > 0) {
			Thread.sleep(SLEEPTIME);
			timeoutMs -= SLEEPTIME;
		}
		if (!firstUpdate) {
			throw new InterruptedException("Timed out on waiting for FIRST_UPDATE_FINISHED.");
		}
	}

}
