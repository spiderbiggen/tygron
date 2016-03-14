package tygronenv.settings;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import nl.tytech.util.StringUtils;

/**
 * Loads the user/password for the Tygron API from the configuration file.
 * 
 */
public class SettingsLoader {
	private static final Logger logger = Logger.getLogger(SettingsLoader.class.getName());
	private Properties config;

	private String username;
	private String password;
	private String server;

	/**
	 * Groups can individually decide what username they will fall back on if
	 * the loading or reading of the cfg file fails.
	 * 
	 * @param path
	 *            the path of the config file
	 * @throws Exception
	 *             when fails
	 */
	public SettingsLoader(String path) throws Exception {
		InputStream stream = StringUtils.class.getClassLoader().getResourceAsStream("configuration.cfg");

		logger.info("Using config file " + stream);
		readConfig(stream);
		stream.close();
	}

	/**
	 * Read in the file from filepath and assign values to variables.
	 * 
	 * @param stream
	 *            the inputstream of the file
	 * @throws Exception
	 *             Exception for when read fails or if file is not found.
	 */
	public void readConfig(InputStream stream) throws Exception {
		config = new Properties();
		config.load(stream);
		username = config.getProperty("username");
		password = config.getProperty("password");
		server = config.getProperty("server");

	}

	/**
	 * Return Tygron Username.
	 * 
	 * @return Tygron Username.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Return Tygron Password.
	 * 
	 * @return Tygron Password.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @return the server IP.
	 */
	public String getServerIp() {
		return server;
	}
}
