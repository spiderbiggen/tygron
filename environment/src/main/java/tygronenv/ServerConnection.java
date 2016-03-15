package tygronenv;

import java.util.Collection;

import eis.exceptions.ManagementException;
import nl.tytech.core.client.event.EventIDListenerInterface;
import nl.tytech.core.client.event.EventManager;
import nl.tytech.core.client.net.ServicesManager;
import nl.tytech.core.event.Event;
import nl.tytech.core.event.EventListenerInterface;
import nl.tytech.core.net.Network;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.net.serializable.ProjectData;
import nl.tytech.core.net.serializable.User;
import nl.tytech.core.net.serializable.User.AccessLevel;
import nl.tytech.core.util.SettingsManager;
import nl.tytech.data.engine.item.Setting;
import nl.tytech.data.engine.item.Stakeholder;
import nl.tytech.util.logger.TLogger;
import tygronenv.settings.Settings;

/**
 * The top level connection with the server. Creates connection, checks
 * credentials, creates new project (if requested name is not yet available).
 * 
 * @author W.Pasman
 *
 */
public class ServerConnection {

	private Session session;
	/**
	 * True if project is created by us. False if someone else created the
	 * project.
	 */
	private boolean createdProject = false;
	private ProjectData project;

	private ProjectFactory factory = new ProjectFactory();

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

		project = factory.getProject(config.getMap());
		if (project == null) {
			project = factory.createProject(config.getMap());
			createdProject = true;
		}

		session = new Session(config, project);
	}

	/**
	 * @return the project that this connection is supporting
	 * 
	 */
	public ProjectData getProject() {
		return project;
	}

	/**
	 * Disconnect from server. After calling this, this object can not be used
	 * anymore.
	 * 
	 * @throws ManagementException
	 */
	public void disconnect() throws ManagementException {
		if (session != null) {
			session.close();
			session = null;
		}
		if (createdProject) {
			factory.deleteProject(project);
			project = null;
			createdProject = false;
		}

		ServicesManager.removeLoginCredentials();
	}
}

/**
 * Event handler that listens to the editor. Just internal, to hear when the
 * server is ready
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
