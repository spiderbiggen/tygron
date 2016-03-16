package tygronenv.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.logging.Logger;

import nl.tytech.util.StringUtils;

/**
 * Contains a user's username and Password that are loaded from
 * configuration.cfg file .. This file must be in same directory as the jar
 * file. When running the straight code (the class files, no jar), the jar file
 * must be available in the root of the class path.
 */
public class Settings {
	private static final String CONFIGURATION_FILE = "configuration.cfg";
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
		} catch (IOException | URISyntaxException e) {
			logger.info("Could not load username and password.");
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Groups can individually decide what username they will fall back on if
	 * the loading or reading of the cfg file fails.
	 * 
	 * @param path
	 *            the path of the config file
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private void read() throws IOException, URISyntaxException {
		InputStream stream;
		File jarFile = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
		if (jarFile.exists() && !jarFile.isDirectory()) {
			// running from the jar, user must provide external config file next
			// to the env jar file.
			File configFile = new File(jarFile.getParent(), CONFIGURATION_FILE);
			stream = new FileInputStream(configFile);
		} else {
			// use built-in (only available in testing conditions).
			stream = StringUtils.class.getClassLoader().getResourceAsStream(CONFIGURATION_FILE);
			if (stream == null) {
				throw new IOException("configuration.cfg file not in resources");
			}
		}

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
