package tygronenv;

import static org.junit.Assert.assertNull;

import eis.exceptions.ManagementException;
import nl.tytech.core.client.net.ServicesManager;
import nl.tytech.core.net.Network;
import nl.tytech.core.net.serializable.User;
import nl.tytech.core.net.serializable.User.AccessLevel;
import nl.tytech.core.util.SettingsManager;
import tygronenv.settings.Settings;

/**
 * The top level connection with the server. Creates connection, checks
 * credentials.
 * 
 * @author W.Pasman
 *
 */
public class ServerConnection {

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
		assertNull(result, result);

		ServicesManager.setSessionLoginCredentials(credentials.getUserName(), credentials.getPassword());
		User user = ServicesManager.getMyUserAccount();

		if (user == null) {
			throw new ManagementException("failed to attach user" + credentials.getUserName()
					+ ". Wrong name/pass? Please check the configuration.cfg file");
		}

		if (user.getMaxAccessLevel().ordinal() < AccessLevel.EDITOR.ordinal()) {
			throw new ManagementException("You need to have at least EDITOR access level");
		}

	}

	public void disconnect() {
		throw new IllegalStateException("NOT IMPLEMENTED");
		// assertTrue(ServicesManager.fireServiceEvent(IOServiceEventType.DELETE_PROJECT,
		// data.getFileName()));
	}
}
