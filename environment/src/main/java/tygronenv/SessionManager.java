package tygronenv;

import java.util.logging.Logger;

import nl.tytech.core.client.net.ServicesManager;
import nl.tytech.core.client.net.SlotConnection;
import nl.tytech.core.net.Network.AppType;
import nl.tytech.core.net.Network.SessionType;
import nl.tytech.core.net.event.IOServiceEventType;
import nl.tytech.core.net.serializable.JoinReply;
import nl.tytech.core.net.serializable.ProjectData;
import nl.tytech.core.net.serializable.SlotInfo;
import nl.tytech.core.util.SettingsManager;
import nl.tytech.locale.TLanguage;

/**
 * SessionManager creates a session according to the requested config.
 */
public class SessionManager {
	private static final Logger logger = Logger.getLogger(SessionManager.class.getName());
	private SlotConnection slotConnection;

	public SessionManager(Configuration config, ProjectData project) {

		Integer slotID = config.getSlot();
		if (config.getSlot() == null) {
			slotID = ServicesManager.fireServiceEvent(IOServiceEventType.START_NEW_SESSION, SessionType.SINGLE,
					project.getFileName(), TLanguage.EN);
			if (slotID == null || slotID < 0) {
				throw new IllegalStateException("Failed to create new session slot: received slot ID =" + slotID);
			}
		} else {
			// join the given slot
			SlotInfo slot = findSession(config);
			if (slot == null) {
				throw new IllegalStateException(
						"Failed to find existing session with given slot ID" + config.getSlot());
			}
			slotID = slot.id;
		}

		JoinReply reply = ServicesManager.fireServiceEvent(IOServiceEventType.JOIN_SESSION, slotID,
				AppType.PARTICIPANT);
		if (reply == null) {
			throw new IllegalStateException("Failed to join session " + slotID + " as participant");
		}

		slotConnection = new SlotConnection();
		slotConnection.initSettings(AppType.PARTICIPANT, SettingsManager.getServerIP(), slotID, reply.serverToken,
				reply.client.getClientToken());

		if (!slotConnection.connect()) {
			throw new IllegalStateException("Failed to connect slotConnection");
		}

		// FIXME add event handler to the slot
		// add event handler to receive updates on
		// eventHandler = new ExampleEventHandler();
	}

	/**
	 * Return a joinable loaded session. If it does not exist yet, start a
	 * session and return it.
	 *
	 * @param mapName
	 *            The mapname you are trying to join.
	 * @param preferedSlot
	 *            the preferred slot
	 * @return a session
	 */
	public SlotInfo findSession(Configuration config) {
		logger.info("Create or find a session with name: " + config.getMap());

		SlotInfo[] availableSessions = ServicesManager.fireServiceEvent(IOServiceEventType.GET_MY_JOINABLE_SESSIONS,
				SessionType.SINGLE, config.getMap(), TLanguage.EN);

		// Try to find the specified slot
		for (SlotInfo slot : availableSessions) {
			if (config.getSlot() == slot.id) {
				return slot;
			}
		}

		// The slot cannot be found, let's try on to find a session on the
		// mapname.
		for (SlotInfo slot : availableSessions) {
			if (config.getMap().equals(slot.name)) {
				return slot;
			}
		}

		return null;
	}

	public void close() {
		// TODO Auto-generated method stub
		throw new IllegalStateException("NOT IMPLEMENTED");
	}
}
