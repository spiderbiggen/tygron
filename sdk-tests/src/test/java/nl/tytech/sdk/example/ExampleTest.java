package nl.tytech.sdk.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.vividsolutions.jts.geom.MultiPolygon;

import nl.tytech.core.client.event.EventManager;
import nl.tytech.core.client.net.ServicesManager;
import nl.tytech.core.client.net.SlotConnection;
import nl.tytech.core.net.Network;
import nl.tytech.core.net.Network.AppType;
import nl.tytech.core.net.Network.SessionType;
import nl.tytech.core.net.event.IOServiceEventType;
import nl.tytech.core.net.serializable.JoinReply;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.net.serializable.ProjectData;
import nl.tytech.core.net.serializable.User;
import nl.tytech.core.net.serializable.User.AccessLevel;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.core.util.SettingsManager;
import nl.tytech.data.editor.event.EditorEventType;
import nl.tytech.data.editor.event.EditorSettingsEventType;
import nl.tytech.data.editor.event.EditorStakeholderEventType;
import nl.tytech.data.engine.event.ParticipantEventType;
import nl.tytech.data.engine.item.Function;
import nl.tytech.data.engine.item.Stakeholder;
import nl.tytech.data.engine.serializable.Category;
import nl.tytech.locale.TLanguage;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.SDKReadmeConfig;
import nl.tytech.util.ThreadUtils;
import nl.tytech.util.logger.TLogger;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExampleTest {

	// public final static String USER = SDKReadmeConfig.loadUser();

	public final static String SERVER = SDKReadmeConfig.loadServer();

	private static Integer slotID;

	private static JoinReply reply;

	private static ProjectData data;

	private static SlotConnection slotConnection;

	private static ExampleEventHandler eventHandler;

	private static Integer stakeholderID = 0;

	@Test
	public void test01Setup() throws Exception {
		// setup settings
		SettingsManager.setup(SettingsManager.class, Network.AppType.EDITOR);
		SettingsManager.setServerIP(SERVER);
	}

	@Test
	public void test02Connect() throws Exception {

		String result = ServicesManager.testServerAPIConnection();
		assertNull(result, result);

		/**
		 * Enter user password
		 */

		JPanel namepasspanel = new JPanel(new BorderLayout());
		JTextField name = new JTextField(20);
		JPasswordField pwd = new JPasswordField(20);
		namepasspanel.add(makeRow("name:", name), BorderLayout.NORTH);
		namepasspanel.add(makeRow("password:", pwd), BorderLayout.CENTER);
		JOptionPane.showConfirmDialog(null, namepasspanel, "Enter Name and Password", JOptionPane.OK_CANCEL_OPTION);

		ServicesManager.setSessionLoginCredentials(name.getText(), new String(pwd.getPassword()));
		User user = ServicesManager.getMyUserAccount();

		assertNotNull(user);
		assertEquals(user.getUserName(), name.getText());

		assertTrue("You need to be at least EDITOR to run these tests!",
				user.getMaxAccessLevel().ordinal() >= AccessLevel.EDITOR.ordinal());
	}

	/**
	 * Make a row with given label
	 * 
	 * @param label
	 * @param inputarea
	 *            the input area for user
	 * @return component
	 */
	private JPanel makeRow(String label, Component inputarea) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JLabel(label), BorderLayout.WEST);
		panel.add(inputarea, BorderLayout.EAST);
		return panel;
	}

	@Test
	public void test03CreateNewProject() throws Exception {

		String projectName = "test" + System.currentTimeMillis();
		data = ServicesManager.fireServiceEvent(IOServiceEventType.CREATE_NEW_PROJECT, projectName, TLanguage.EN);
		assertNotNull(data);
	}

	@Test
	public void test04StartEditSessionAsEditor() throws Exception {

		slotID = ServicesManager.fireServiceEvent(IOServiceEventType.START_NEW_SESSION, SessionType.EDITOR,
				data.getFileName(), TLanguage.EN);
		assertTrue(slotID != null && slotID >= 0);

		reply = ServicesManager.fireServiceEvent(IOServiceEventType.JOIN_SESSION, slotID, AppType.EDITOR);
		assertNotNull(reply);

		slotConnection = new SlotConnection();
		slotConnection.initSettings(AppType.EDITOR, SettingsManager.getServerIP(), slotID, reply.serverToken,
				reply.client.getClientToken());

		assertTrue(slotConnection.connect());

		// add event handler to receive updates on
		eventHandler = new ExampleEventHandler();
	}

	@Test
	public void test05doEditSession() throws Exception {

		int mapSizeM = 500;
		slotConnection.fireServerEvent(true, EditorEventType.SET_INITIAL_MAP_SIZE, mapSizeM);
		slotConnection.fireServerEvent(true, EditorSettingsEventType.WIZARD_FINISHED);

		/**
		 * Add a civilian stakeholder
		 */
		slotConnection.fireServerEvent(true, EditorStakeholderEventType.ADD_WITH_TYPE_AND_PLAYABLE,
				Stakeholder.Type.CIVILIAN, true);

		// wait on first updates (seperate thread)
		boolean updated = false;
		for (int i = 0; i < 60; i++) {
			if (eventHandler.isMapUpdated() && eventHandler.isStakeholderUpdated()) {
				updated = true;
				break;
			}
			ThreadUtils.sleepInterruptible(1000);
		}
		assertTrue(updated);
	}

	@Test
	public void test06closeEditSession() throws Exception {

		/**
		 * Save project in our slotID
		 */
		String result = ServicesManager.fireServiceEvent(IOServiceEventType.SAVE_PROJECT_INIT, slotID);
		assertNull(result, result);

		/**
		 * Disconnect from slot
		 */
		slotConnection.disconnect(false);
	}

	@Test
	public void test07startRegularSessionAsParticipant() throws Exception {

		slotID = ServicesManager.fireServiceEvent(IOServiceEventType.START_NEW_SESSION, SessionType.SINGLE,
				data.getFileName(), TLanguage.EN);
		assertTrue(slotID != null && slotID >= 0);

		reply = ServicesManager.fireServiceEvent(IOServiceEventType.JOIN_SESSION, slotID, AppType.PARTICIPANT);
		assertNotNull(reply);

		slotConnection = new SlotConnection();
		slotConnection.initSettings(AppType.PARTICIPANT, SettingsManager.getServerIP(), slotID, reply.serverToken,
				reply.client.getClientToken());

		assertTrue(slotConnection.connect());

		// add event handler to receive updates on
		eventHandler = new ExampleEventHandler();
	}

	@Test
	public void test08selectStakeholderToPlay() throws Exception {

		stakeholderID = 0;
		ItemMap<Stakeholder> stakeholders = EventManager.getItemMap(MapLink.STAKEHOLDERS);
		for (Stakeholder stakeholder : stakeholders) {
			stakeholderID = stakeholder.getID();
			TLogger.info("Selecting first stakeholder: " + stakeholder.getName() + " to play!");
			break;
		}
		slotConnection.fireServerEvent(true, ParticipantEventType.STAKEHOLDER_SELECT, stakeholderID,
				reply.client.getClientToken());
	}

	@Test
	public void test09planBuilding() throws Exception {

		/**
		 * Plan an new ROAD construction
		 */
		Integer functionID = 0;
		int floors = 1;
		ItemMap<Function> functions = EventManager.getItemMap(MapLink.FUNCTIONS);
		for (Function function : functions) {
			if (function.getCategories().contains(Category.ROAD)) {
				functionID = function.getID();
				TLogger.info("Selecting first road function: " + function.getName() + " to build!");
				break;
			}
		}

		/**
		 * Shape of my new road
		 */
		MultiPolygon roadMultiPolygon = JTSUtils.createSquare(10, 10, 200, 10);

		Integer newBuildingID = slotConnection.fireServerEvent(true, ParticipantEventType.BUILDING_PLAN_CONSTRUCTION,
				stakeholderID, functionID, floors, roadMultiPolygon);

		assertTrue(newBuildingID.intValue() >= 0);

	}

	@Test
	public void test10closeRegularSession() throws Exception {
		slotConnection.disconnect(false);
	}

	@Test
	public void test11deleteProject() throws Exception {
		assertTrue(ServicesManager.fireServiceEvent(IOServiceEventType.DELETE_PROJECT, data.getFileName()));
	}
}
