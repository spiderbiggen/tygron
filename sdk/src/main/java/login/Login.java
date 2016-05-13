package login;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.prefs.Preferences;

import javax.security.auth.login.LoginException;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import nl.tytech.core.client.net.ServicesManager;
import nl.tytech.core.net.event.UserServiceEventType;
import nl.tytech.core.util.SettingsManager;
import nl.tytech.util.StringUtils;

/**
 * Execute login procedure. Store password if asked.
 * 
 * @author W.Pasman
 *
 */
public class Login {
	private static final String HASHEDPASS = "hashedpass";
	private static final String USERNAME = "username";
	String username;
	String hashedPass;
	boolean isSaved;

	Preferences prefs = Preferences.userNodeForPackage(Login.class);

	/**
	 * Log in to the server. Check if pass needs to be stored and store.
	 * 
	 * @throws LoginException
	 *             if login fails.
	 */
	public Login() throws LoginException {
		getCredentials();

		if (!isSaved) {
			passPrompt();
		}
		if (isSaved) {
			saveCredentials();
		}

		ServicesManager.setSessionLoginCredentials(username, hashedPass, true);
	}

	private void getCredentials() {
		username = prefs.get(USERNAME, "");
		hashedPass = prefs.get(HASHEDPASS, "");
		isSaved = StringUtils.containsData(username) && StringUtils.containsData(hashedPass);
	}

	private void saveCredentials() {
		prefs.put(USERNAME, username);
		prefs.put(HASHEDPASS, hashedPass);
		SettingsManager.setStayLoggedIn(true);
	}

	/**
	 * Ask user for the credentials.
	 * 
	 * @throws LoginException
	 * 
	 * @throws IllegalStateException
	 *             if user cancels login procedure.
	 */
	private void passPrompt() throws LoginException {
		JPanel namepasspanel = new JPanel(new BorderLayout());
		JTextField name = new JTextField(20);
		JPasswordField pwd = new JPasswordField(20);
		JCheckBox save = new JCheckBox();
		namepasspanel.add(makeRow("name:", name), BorderLayout.NORTH);
		namepasspanel.add(makeRow("password:", pwd), BorderLayout.CENTER);
		namepasspanel.add(makeRow("save password", save), BorderLayout.SOUTH);
		int choice = JOptionPane.showConfirmDialog(null, namepasspanel, "Enter Tygron Name and Password",
				JOptionPane.OK_CANCEL_OPTION);

		if (choice == JOptionPane.CANCEL_OPTION) {
			throw new LoginException("User cancelled login.");
		}
		username = name.getText();
		String pass = new String(pwd.getPassword());
		ServicesManager.setSessionLoginCredentials(username, pass);
		hashedPass = ServicesManager.fireServiceEvent(UserServiceEventType.GET_MY_HASH_KEY);
		isSaved = save.isSelected();

	}

	/**
	 * Make a row with given label, and an input area
	 * 
	 * @param label
	 * @param inputarea
	 *            the {@link Component} - input area for user
	 * @return component
	 */
	private JPanel makeRow(String label, Component inputarea) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JLabel(label), BorderLayout.WEST);
		panel.add(inputarea, BorderLayout.EAST);
		return panel;
	}

	public String getUserName() {
		return username;
	}

	public static String getServerIp() {
		return null;
	}

	public static void main(String[] args) throws LoginException {
		if (args.length == 2) {
			new Login().setCredentials(args[0], args[1]);
		} else {
			System.out.println("2 arguments required: name, password");
		}
	}

	private void setCredentials(String name, String pass) throws LoginException {
		this.username = name;
		ServicesManager.setSessionLoginCredentials(username, pass);
		hashedPass = ServicesManager.fireServiceEvent(UserServiceEventType.GET_MY_HASH_KEY);
		if (hashedPass == null) {
			// happens if the pass is wrong...
			throw new LoginException("incorrect name/password");
		}
		saveCredentials();
	}

}
