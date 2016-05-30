package nl.tytech.sdk.example;

import static org.junit.Assert.assertNotNull;
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
import nl.tytech.locale.TLanguage;

public class FirstUpdateTest {

	private ProjectData data;
	private SlotConnection slotConnection;
	private ExampleEventHandler eventHandler;

	/**
	 * login, create test project.
	 * 
	 * @throws LoginException
	 */
	@Before
	public void before() throws LoginException {
		login();
		eventHandler = connect();
	}

	@After
	public void after() {
		slotConnection.disconnect(false);
		assertTrue(ServicesManager.fireServiceEvent(IOServiceEventType.DELETE_PROJECT, data.getFileName()));
	}

	private void login() throws LoginException {
		Login login = new Login();
		login.doLogin();

		String projectName = "test" + System.currentTimeMillis();
		data = ServicesManager.fireServiceEvent(IOServiceEventType.CREATE_NEW_PROJECT, projectName, TLanguage.EN);
		assertNotNull(data);

	}

	private ExampleEventHandler connect() {

		Integer slotID = ServicesManager.fireServiceEvent(IOServiceEventType.START_NEW_SESSION, SessionType.EDITOR,
				data.getFileName(), TLanguage.EN);
		assertTrue("Could not get slot for project " + data.getFileName(), slotID != null && slotID >= 0);

		// add event handler to receive updates on
		ExampleEventHandler eventHandler = new ExampleEventHandler();

		JoinReply reply = ServicesManager.fireServiceEvent(IOServiceEventType.JOIN_SESSION, slotID,
				AppType.PARTICIPANT);
		assertNotNull(reply);

		slotConnection = new SlotConnection();
		slotConnection.initSettings(AppType.PARTICIPANT, SettingsManager.getServerIP(), slotID, reply.serverToken,
				reply.client.getClientToken());

		assertTrue(slotConnection.connect());
		return eventHandler;
	}

	@Test
	public void checkInitialStakeholders() throws InterruptedException {
		eventHandler.waitForFirstUpdate(5000);

		ItemMap<Item> map = EventManager.getItemMap(MapLink.STAKEHOLDERS);
		assertTrue(map.size() > 0);
	}

}
