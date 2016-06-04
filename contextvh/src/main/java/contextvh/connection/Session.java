package contextvh.connection;

import nl.tytech.core.client.net.ServicesManager;
import nl.tytech.core.net.Network.SessionType;
import nl.tytech.core.net.event.IOServiceEventType;
import nl.tytech.core.net.serializable.ProjectData;
import nl.tytech.core.net.serializable.SlotInfo;
import nl.tytech.locale.TLanguage;
import tygronenv.configuration.Configuration;
import tygronenv.connection.ServerConnection;

import java.util.logging.Logger;

/**
 * Creates a session according to the requested config. A session is a
 * connection of a team (multiple participants) with the server. A session runs
 * on an open project, see {@link ServerConnection#getProject()}.
 */
public class Session {
    private static final Logger logger = Logger.getLogger(Session.class.getName());
    private Integer slotID;

    public Session(Configuration config, ProjectData project) {

        slotID = config.getSlot();
        if (config.getSlot() == null) {
            slotID = ServicesManager.fireServiceEvent(IOServiceEventType.START_NEW_SESSION, SessionType.MULTI,
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
    }

    /**
     * @return the team's slot on the server, null if slot not available
     * anymore.
     */
    public Integer getTeamSlot() {
        return slotID;
    }

    /**
     * Return a joinable loaded session. If it does not exist yet, start a
     * session and return it.
     *
     * @param mapName      The mapname you are trying to join.
     * @param preferedSlot the preferred slot
     * @return a session
     */
    private SlotInfo findSession(Configuration config) {
        logger.info("Create or find a session with name: " + config.getProject());

        SlotInfo[] availableSessions = ServicesManager.fireServiceEvent(IOServiceEventType.GET_MY_JOINABLE_SESSIONS,
                SessionType.SINGLE, config.getProject(), TLanguage.EN);

        // Try to find the specified slot
        for (SlotInfo slot : availableSessions) {
            if (config.getSlot() == slot.id) {
                return slot;
            }
        }

        // The slot cannot be found, let's try on to find a session on the
        // project.
        for (SlotInfo slot : availableSessions) {
            if (config.getProject().equals(slot.name)) {
                return slot;
            }
        }

        return null;
    }

    /**
     * Close the session and clean up.
     */
    public void close() {
        if (slotID != null) {
            ServicesManager.fireServiceEvent(IOServiceEventType.KILL_SESSION, slotID);
        }
        slotID = null;
    }
}
