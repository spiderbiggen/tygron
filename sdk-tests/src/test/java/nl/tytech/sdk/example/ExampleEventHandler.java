package nl.tytech.sdk.example;

import java.util.Collection;

import nl.tytech.core.client.event.EventIDListenerInterface;
import nl.tytech.core.client.event.EventManager;
import nl.tytech.core.event.Event;
import nl.tytech.core.event.EventListenerInterface;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.engine.item.Land;
import nl.tytech.data.engine.item.PopupData;
import nl.tytech.data.engine.item.Setting;
import nl.tytech.data.engine.item.Stakeholder;
import nl.tytech.util.logger.TLogger;

public class ExampleEventHandler implements EventListenerInterface, EventIDListenerInterface {

	private boolean stakeholderUpdate = false, mapUpdate = false;
	private boolean landsUpdate = false, popupsUpdate = false;

	public ExampleEventHandler() {
		EventManager.addListener(this, MapLink.STAKEHOLDERS, MapLink.LANDS, MapLink.POPUPS);
		EventManager.addEnumListener(this, MapLink.SETTINGS, Setting.Type.MAP_WIDTH_METERS);
	}

	public boolean isMapUpdated() {
		return mapUpdate;
	}

	public boolean isStakeholderUpdated() {
		return stakeholderUpdate;
	}

	public void resetLandUpdate() {
		landsUpdate = false;
	}

	public void resetPopupsUpdate() {
		popupsUpdate = false;
	}

	public boolean isLandUpdated() {
		return landsUpdate;
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

		if (event.getType() == MapLink.STAKEHOLDERS) {
			Collection<Stakeholder> updates = event.getContent(MapLink.UPDATED_COLLECTION);
			TLogger.info("Updated stakeholders: " + updates);
			stakeholderUpdate = true;
		} else if (event.getType() == MapLink.LANDS) {
			Collection<Land> updates = event.getContent(MapLink.UPDATED_COLLECTION);
			TLogger.info("Updated lands: " + updates);
			landsUpdate = true;
		} else if (event.getType() == MapLink.POPUPS) {
			Collection<PopupData> updates = event.getContent(MapLink.UPDATED_COLLECTION);
			TLogger.info("Updated popups: " + updates);
			popupsUpdate = true;
		}
	}

	public boolean isPopupUpdated() {
		return popupsUpdate;
	}

}
