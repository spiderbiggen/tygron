package nl.tytech.sdk.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.security.auth.login.LoginException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import login.Login;
import nl.tytech.core.client.event.EventManager;
import nl.tytech.core.client.net.ServicesManager;
import nl.tytech.core.client.net.SlotConnection;
import nl.tytech.core.net.Network.AppType;
import nl.tytech.core.net.Network.SessionType;
import nl.tytech.core.net.event.IOServiceEventType;
import nl.tytech.core.net.serializable.JoinReply;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.net.serializable.ProjectData;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.core.util.SettingsManager;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.editor.event.EditorEventType;
import nl.tytech.data.editor.event.EditorSettingsEventType;
import nl.tytech.data.editor.event.EditorStakeholderEventType;
import nl.tytech.data.engine.item.Stakeholder;
import nl.tytech.locale.TLanguage;
import nl.tytech.util.ThreadUtils;

public class FirstUpdateTest {

	private ProjectData project;
	private SlotConnection slotConnection;

	/**
	 * login, create test project.
	 * 
	 * @throws LoginException
	 */
	@Before
	public void before() throws LoginException {
		login();
	}

	@After
	public void after() {

		slotConnection.disconnect(false);

		/**
		 * FIXME this throws and I have no idea why. Can be many problems:
		 * 
		 * De client niet de eigenaar is van het project
		 * 
		 * Het project nog draait op de server en nog niet is afgesloten
		 * 
		 * Het project niet meer bestaat
		 * 
		 * Het project kan nog draaien indien:
		 * 
		 * Het afsluiten langer duurt doordat er nog voor het afsluiten een
		 * bewerking is uitgevoerd die eerst afgehandeld moet worden.
		 * 
		 * Een ander client zich nog bevind in de draaiende sessie.
		 */
		// assertTrue(ServicesManager.fireServiceEvent(IOServiceEventType.DELETE_PROJECT,
		// project.getFileName()));
	}

	private void login() throws LoginException {
		Login login = new Login();
		login.doLogin();

		String projectName = "test" + System.currentTimeMillis();
		project = ServicesManager.fireServiceEvent(IOServiceEventType.CREATE_NEW_PROJECT, projectName, TLanguage.EN);
		assertNotNull(project);

	}

	private void connect() {

		Integer slotID = ServicesManager.fireServiceEvent(IOServiceEventType.START_NEW_SESSION, SessionType.EDITOR,
				project.getFileName(), TLanguage.EN);
		assertTrue("Could not get slot for project " + project.getFileName(), slotID != null && slotID >= 0);
		edit(slotID);
		participate(slotID);
	}

	private void participate(Integer slotID) {
		slotID = ServicesManager.fireServiceEvent(IOServiceEventType.START_NEW_SESSION, SessionType.MULTI,
				project.getFileName(), TLanguage.EN);
		assertTrue(slotID != null && slotID >= 0);
		JoinReply reply = ServicesManager.fireServiceEvent(IOServiceEventType.JOIN_SESSION, slotID,
				AppType.PARTICIPANT);
		assertNotNull(reply);

		slotConnection = new SlotConnection();
		slotConnection.initSettings(AppType.PARTICIPANT, SettingsManager.getServerIP(), slotID, reply.serverToken,
				reply.client.getClientToken());

		assertTrue(slotConnection.connect());
	}

	/**
	 * Set up a basic map suited for simple testing.
	 * 
	 * @param slotID
	 */
	public void edit(Integer slotID) {

		JoinReply reply = ServicesManager.fireServiceEvent(IOServiceEventType.JOIN_SESSION, slotID, AppType.EDITOR);
		assertNotNull(reply);

		slotConnection = new SlotConnection();
		slotConnection.initSettings(AppType.EDITOR, SettingsManager.getServerIP(), slotID, reply.serverToken,
				reply.client.getClientToken());

		assertTrue(slotConnection.connect());

		// add event handler to receive updates on
		ExampleEventHandler eventHandler = new ExampleEventHandler();

		int mapSizeM = 500;
		slotConnection.fireServerEvent(true, EditorEventType.SET_INITIAL_MAP_SIZE, mapSizeM);
		slotConnection.fireServerEvent(true, EditorSettingsEventType.WIZARD_FINISHED);

		/**
		 * Add a civilian and farmer stakeholder. This adds to default
		 * Inhabitants, making a total of 3 stakeholders.
		 */
		slotConnection.fireServerEvent(true, EditorStakeholderEventType.ADD_WITH_TYPE_AND_PLAYABLE,
				Stakeholder.Type.CIVILIAN, true);
		slotConnection.fireServerEvent(true, EditorStakeholderEventType.ADD_WITH_TYPE_AND_PLAYABLE,
				Stakeholder.Type.FARMER, true);

		// wait on first updates (seperate thread)
		boolean updated = false;
		for (int i = 0; i < 60; i++) {
			if (eventHandler.isMapUpdated() && eventHandler.isUpdated(MapLink.STAKEHOLDERS)) {
				updated = true;
				break;
			}
			ThreadUtils.sleepInterruptible(1000);
		}
		assertTrue(updated);
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
	public void checkInitialStakeholders() throws InterruptedException {
		// add event handler to receive updates on
		ExampleEventHandler eventHandler = new ExampleEventHandler();

		connect();
		eventHandler.waitForFirstUpdate(5000);

		ItemMap<Item> map = EventManager.getItemMap(MapLink.STAKEHOLDERS);
		assertEquals(3, map.size());
	}

	@Test
	public void checkExactlyOneFirstUpdate() throws InterruptedException {
		// add event handler to receive updates on
		ExampleEventHandler eventHandler = new ExampleEventHandler();
		ExampleEventHandler eventHandler2 = new ExampleEventHandler();

		connect();
		eventHandler.waitForFirstUpdate(5000);
		eventHandler2.waitForFirstUpdate(5000);

		assertEquals(1, eventHandler.getNumberOfFirstUpdates());
		assertEquals(1, eventHandler2.getNumberOfFirstUpdates());
	}

}
