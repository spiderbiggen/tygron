package tygronenv.connection;

import eis.exceptions.ManagementException;
import nl.tytech.core.client.net.ServicesManager;
import nl.tytech.core.client.net.SlotConnection;
import nl.tytech.core.net.Network.AppType;
import nl.tytech.core.net.Network.SessionType;
import nl.tytech.core.net.event.IOServiceEventType;
import nl.tytech.core.net.serializable.JoinReply;
import nl.tytech.core.net.serializable.ProjectData;
import nl.tytech.core.util.SettingsManager;
import nl.tytech.data.editor.event.EditorEventType;
import nl.tytech.data.editor.event.EditorSettingsEventType;
import nl.tytech.data.editor.event.EditorStakeholderEventType;
import nl.tytech.data.engine.item.Stakeholder;
import nl.tytech.locale.TLanguage;
import nl.tytech.util.ThreadUtils;

/**
 * Factory to fetch existing and create new projects
 * 
 * @author W.Pasman
 *
 */
public class ProjectFactory {

	/**
	 * Join existing project.
	 * 
	 * @param name
	 *            the project name to join/make.
	 * @return project with given name, or null if no project with given name
	 *         exists.
	 * @throws ManagementException
	 */
	public ProjectData getProject(String name) throws ManagementException {
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
	 * it. Bit hacky, it does not do much for the map, it probably stays empty.
	 * 
	 * @throws ManagementException
	 */
	public ProjectData createProject(String name) throws ManagementException {
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
			throw new ManagementException("failed to edit project:" + reply);
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
	 * Deletes project on the server.
	 * 
	 * @param project
	 * @throws ManagementException
	 */
	public void deleteProject(ProjectData project) throws ManagementException {
		Boolean result = ServicesManager.fireServiceEvent(IOServiceEventType.DELETE_PROJECT, project.getFileName());
		if (!result) {
			throw new ManagementException("failed to delete project " + project.getFileName() + " on the server");
		}

	}

}
