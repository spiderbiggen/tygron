package tygronenv.settings;

import java.util.logging.Logger;

/**
 * Contains a user's username and Password.
 */
public class Settings {
	private static final Logger logger = Logger.getLogger(Settings.class.getName());
	private String username;
	private String password;
	private String server;

	/**
	 * Set the username and password.
	 */
	public Settings() {
		try {
			SettingsLoader settingsLoader = getSettingsLoader("configuration.cfg");
			this.username = settingsLoader.getUsername();
			this.password = settingsLoader.getPassword();
			this.server = settingsLoader.getServerIp();
		} catch (Exception e) {
			logger.info("Could not load username and password.");
			throw new RuntimeException(e);
		}
	}

	protected SettingsLoader getSettingsLoader(String cfg) throws Exception {
		return new SettingsLoader(cfg);
	}

	/**
	 * Return the Tygron username.
	 * 
	 * @return Tygron username.
	 */
	public String getUserName() {
		return this.username;
	}

	/**
	 * Return the Tygon password.
	 * 
	 * @return Tygron password.
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * 
	 * @return the server IP.
	 */
	public String getServerIp() {
		return server;
	}

}
