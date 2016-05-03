package login;

import java.awt.BorderLayout;
import java.awt.Component;

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
	String username;
	String hashedPass;
	boolean isSaved;

	/**
	 * Log in to the server. Check if pass needs to be stored and store.
	 */
	public Login() {
		username = SettingsManager.getOnlineUserName();
		hashedPass = SettingsManager.getOnlinePassword();
		isSaved = StringUtils.containsData(username) && StringUtils.containsData(hashedPass);

		if (!isSaved) {
			passPrompt();
		}
		if (isSaved) {
			saveCredentials();
		}

		ServicesManager.setSessionLoginCredentials(username, hashedPass, true);
	}

	private void saveCredentials() {
		SettingsManager.setOnlineUserName(username);
		SettingsManager.setOnlinePassword(hashedPass);
		SettingsManager.setStayLoggedIn(true);
	}

	/**
	 * Ask user for the credentials.
	 * 
	 * @throws IllegalStateException
	 *             if user cancels login procedure.
	 */
	private void passPrompt() {
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
			throw new IllegalStateException("User cancelled login.");
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
}
