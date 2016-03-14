package tygronenv.settings;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import nl.tytech.util.StringUtils;

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
			read();
		} catch (IOException e) {
			logger.info("Could not load username and password.");
			throw new RuntimeException(e);
		}
	}

	/**
	 * Groups can individually decide what username they will fall back on if
	 * the loading or reading of the cfg file fails.
	 * 
	 * @param path
	 *            the path of the config file
	 * @throws IOException
	 */
	private void read() throws IOException {
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
	 * @throws IOException
	 *             Exception for when read fails or if file is not found.
	 */
	private void readConfig(InputStream stream) throws IOException {
		Properties config = new Properties();
		config.load(stream);
		username = config.getProperty("username");
		password = config.getProperty("password");
		server = config.getProperty("server");

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
