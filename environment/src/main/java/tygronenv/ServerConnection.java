package tygronenv;

import java.util.Collection;

import eis.exceptions.ManagementException;
import nl.tytech.core.client.event.EventIDListenerInterface;
import nl.tytech.core.client.event.EventManager;
import nl.tytech.core.client.net.ServicesManager;
import nl.tytech.core.client.net.SlotConnection;
import nl.tytech.core.event.Event;
import nl.tytech.core.event.EventListenerInterface;
import nl.tytech.core.net.Network;
import nl.tytech.core.net.Network.AppType;
import nl.tytech.core.net.Network.SessionType;
import nl.tytech.core.net.event.IOServiceEventType;
import nl.tytech.core.net.serializable.JoinReply;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.net.serializable.ProjectData;
import nl.tytech.core.net.serializable.User;
import nl.tytech.core.net.serializable.User.AccessLevel;
import nl.tytech.core.util.SettingsManager;
import nl.tytech.data.editor.event.EditorEventType;
import nl.tytech.data.editor.event.EditorSettingsEventType;
import nl.tytech.data.editor.event.EditorStakeholderEventType;
import nl.tytech.data.engine.item.Setting;
import nl.tytech.data.engine.item.Stakeholder;
import nl.tytech.locale.TLanguage;
import nl.tytech.util.ThreadUtils;
import nl.tytech.util.logger.TLogger;
import tygronenv.settings.Settings;

/**
 * The top level connection with the server. Creates connection, checks
 * credentials.
 * 
 * @author W.Pasman
 *
 */
public class ServerConnection {

	private SessionManager session;
	/**
	 * True if project is created by us. False if someone else created the
	 * project.
	 */
	private boolean createdProject = false;
	private ProjectData project;

	/**
	 * Connect with the server, using the {@link Settings}.
	 * 
	 * @param config
	 *            the {@link Configuration} to use
	 * @throws ManagementException
	 */
	public ServerConnection(Configuration config) throws ManagementException {
		Settings credentials = new Settings();

		// setup settings
		SettingsManager.setup(SettingsManager.class, Network.AppType.EDITOR);
		SettingsManager.setServerIP(credentials.getServerIp());

		String result = ServicesManager.testServerConnection();
		if (result != null) {
			throw new ManagementException("Server is actively refusing to connect:" + result);
		}

		ServicesManager.setSessionLoginCredentials(credentials.getUserName(), credentials.getPassword());
		User user = ServicesManager.getMyUserAccount();

		if (user == null) {
			throw new ManagementException("failed to attach user" + credentials.getUserName()
					+ ". Wrong name/pass? Please check the configuration.cfg file");
		}

		if (user.getMaxAccessLevel().ordinal() < AccessLevel.EDITOR.ordinal()) {
			throw new ManagementException("You need to have at least EDITOR access level");
		}

		project = getProject(config.getMap());
		session = new SessionManager(config, project);
	}

	/**
	 * Join existing or create new project.
	 * 
	 * @param name
	 *            the project name to join/make.
	 * @return project with given name
	 * @throws ManagementException
	 */
	private ProjectData getProject(String name) throws ManagementException {
		ProjectData[] projects = ServicesManager.fireServiceEvent(IOServiceEventType.GET_MY_STARTABLE_PROJECTS);
		if (projects != null) {
			for (ProjectData existing : projects) {
				if (existing.getFileName().equals(name)) {
					return ServicesManager.fireServiceEvent(IOServiceEventType.GET_PROJECT_DATA, name);
				}
			}
		}

		return createProject(name);
	}

	/**
	 * There is no existing project with given name. Create one and initialize
	 * it.
	 * 
	 * @throws ManagementException
	 */
	private ProjectData createProject(String name) throws ManagementException {
		ProjectData proj = ServicesManager.fireServiceEvent(IOServiceEventType.CREATE_NEW_PROJECT, name, TLanguage.EN);

		Integer slotID = ServicesManager.fireServiceEvent(IOServiceEventType.START_NEW_SESSION, SessionType.EDITOR,
				proj.getFileName(), TLanguage.EN);
		if (slotID == null || slotID < 0) {
			throw new ManagementException("Failed to create edit slot to create new project");
		}

		SlotConnection editSlot = editProject(slotID);
		addCivilianMap(editSlot);

		String result = ServicesManager.fireServiceEvent(IOServiceEventType.SAVE_PROJECT_INIT, slotID);
		if (result != null) {
			throw new ManagementException("Failed to save new project" + result);
		}

		/**
		 * Disconnect from slot
		 */
		editSlot.disconnect(false);

		createdProject = true;
		return proj;
	}

	/**
	 * @param proj
	 *            the project to open an edit slot for
	 * @return a {@link SlotConnection} that can be used for editing the
	 *         project.
	 * @throws ManagementException
	 */
	private SlotConnection editProject(Integer slotID) throws ManagementException {

		JoinReply reply = ServicesManager.fireServiceEvent(IOServiceEventType.JOIN_SESSION, slotID, AppType.EDITOR);
		if (reply == null) {
			throw new ManagementException("failed to edit project " + project + ":" + reply);
		}

		SlotConnection slotConnection = new SlotConnection();
		slotConnection.initSettings(AppType.EDITOR, SettingsManager.getServerIP(), slotID, reply.serverToken,
				reply.client.getClientToken());
		if (!slotConnection.connect()) {
			throw new ManagementException("Failed to connect a slot for editing the new project");
		}

		return slotConnection;
	}

	/**
	 * Add map and civilian stakeholder
	 * 
	 * @param slotConnection
	 *            the connection with the editor slot
	 * @throws ManagementException
	 */
	private void addCivilianMap(SlotConnection slotConnection) throws ManagementException {
		// add event handler to receive updates on
		EditorEventHandler eventHandler = new EditorEventHandler();
		int mapSizeM = 500;
		slotConnection.fireServerEvent(true, EditorEventType.SET_INITIAL_MAP_SIZE, mapSizeM);
		slotConnection.fireServerEvent(true, EditorSettingsEventType.WIZARD_FINISHED);

		/**
		 * Add a civilian stakeholder
		 */
		slotConnection.fireServerEvent(true, EditorStakeholderEventType.ADD_WITH_TYPE_AND_PLAYABLE,
				Stakeholder.Type.CIVILIAN, true);

		// wait on first updates (seperate thread)
		boolean updated = false;
		for (int i = 0; i < 15; i++) {
			if (eventHandler.isMapUpdated() && eventHandler.isStakeholderUpdated()) {
				updated = true;
				break;
			}
			ThreadUtils.sleepInterruptible(1000);
		}
		if (!updated) {
			throw new ManagementException("Server is not responding on request to update the map");
		}
	}

	/**
	 * Disconnect from server. After calling this, this object can not be used
	 * anymore.
	 */
	public void disconnect() {
		if (session != null) {
			session.close();
		}
		if (createdProject) {
			Boolean result = ServicesManager.fireServiceEvent(IOServiceEventType.DELETE_PROJECT, project.getFileName());
			if (!result) {
				System.out.println("WARNING: failed to delete project " + project.getFileName() + " on the server");
			}
			project = null;
			createdProject = false;
		}

		ServicesManager.removeLoginCredentials();
	}
}

/**
 * Event handler that listens to the editor
 *
 */
class EditorEventHandler implements EventListenerInterface, EventIDListenerInterface {

	private boolean stakeholderUpdate = false, mapUpdate = false;

	public EditorEventHandler() {
		EventManager.addListener(this, MapLink.STAKEHOLDERS);
		EventManager.addEnumListener(this, MapLink.SETTINGS, Setting.Type.MAP_WIDTH_METERS);
	}

	public boolean isMapUpdated() {
		return mapUpdate;
	}

	public boolean isStakeholderUpdated() {
		return stakeholderUpdate;
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
		}
	}

}
