package tygronenv.connection;

import javax.security.auth.login.LoginException;

import eis.exceptions.ManagementException;
import login.Login;
import nl.tytech.core.client.net.ServicesManager;
import nl.tytech.core.net.Network;
import nl.tytech.core.net.serializable.ProjectData;
import nl.tytech.core.net.serializable.User;
import nl.tytech.core.net.serializable.User.AccessLevel;
import nl.tytech.core.util.SettingsManager;
import tygronenv.configuration.Configuration;

/**
 * The top level connection with the server. Creates connection, checks
 * credentials, creates new project (if requested name is not yet available).
 * 
 * @author W.Pasman
 *
 */
public class ServerConnection {

	private final static String SERVER = "preview.tygron.com";

	/**
	 * True if project is created by us. False if someone else created the
	 * project.
	 */
	private boolean createdProject = false;
	private ProjectData project;

	/**
	 * Session can be used by all team members.
	 */
	private Session session = null;
	private ProjectFactory factory = new ProjectFactory();

	/**
	 * Connect with the server, using the {@link Settings}.
	 * 
	 * @param config
	 *            the {@link Configuration} to use
	 * @throws ManagementException
	 */
	public ServerConnection(Configuration config) throws ManagementException {

		// setup settings
		SettingsManager.setup(SettingsManager.class, Network.AppType.EDITOR);
		SettingsManager.setServerIP(SERVER);

		String result = ServicesManager.testServerConnection();
		if (result != null) {
			throw new ManagementException("Server is actively refusing to connect:" + result);
		}

		Login login;
		try {
			login = new Login();
		} catch (LoginException e) {
			throw new ManagementException("login failed", e);
		}

		User user = ServicesManager.getMyUserAccount();

		if (user == null) {
			throw new ManagementException("failed to attach user" + login.getUserName()
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

	/**
	 * @return the session that we are working on. All team members can use this
	 *         session
	 */
	public Session getSession() {
		return session;
	}
}
