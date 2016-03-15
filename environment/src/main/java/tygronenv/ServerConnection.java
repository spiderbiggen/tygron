package tygronenv;

import eis.exceptions.ManagementException;
import nl.tytech.core.client.net.ServicesManager;
import nl.tytech.core.net.Network;
import nl.tytech.core.net.event.IOServiceEventType;
import nl.tytech.core.net.serializable.ProjectData;
import nl.tytech.core.net.serializable.User;
import nl.tytech.core.net.serializable.User.AccessLevel;
import nl.tytech.core.util.SettingsManager;
import nl.tytech.locale.TLanguage;
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
	 */
	private ProjectData getProject(String name) {
		String[] projects = ServicesManager.fireServiceEvent(IOServiceEventType.GET_PROJECT_NAMES);
		if (projects != null) {
			for (String existing : projects) {
				if (existing.equals(name)) {
					return ServicesManager.fireServiceEvent(IOServiceEventType.GET_PROJECT_DATA, name);
				}
			}
		}

		// not an existing project. Create new one.
		createdProject = true;
		return ServicesManager.fireServiceEvent(IOServiceEventType.CREATE_NEW_PROJECT, name, TLanguage.EN);
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
