package nl.tytech.sdk.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.security.auth.login.LoginException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import login.Login;
import login.ProjectException;
import login.ProjectFactory;
import nl.tytech.core.client.event.EventManager;
import nl.tytech.core.client.net.ServicesManager;
import nl.tytech.core.client.net.TSlotConnection;
import nl.tytech.core.net.Network.AppType;
import nl.tytech.core.net.Network.SessionType;
import nl.tytech.core.net.event.IOServiceEventType;
import nl.tytech.core.net.serializable.JoinReply;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.net.serializable.ProjectData;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.core.util.SettingsManager;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.event.LogicEventType;
import nl.tytech.locale.TLanguage;

public class FirstUpdateTest {

	private ProjectData project;
	private ProjectFactory factory;

	/**
	 * login, create test project.
	 *
	 * @throws LoginException
	 * @throws ProjectException
	 */
	@Before
	public void before() throws LoginException, ProjectException {
		Login login = new Login();
		login.doLogin();

		factory = new ProjectFactory();
		String projectName = "test" + System.currentTimeMillis();
		project = factory.createProject(projectName);
		assertNotNull(project);
	}

	@After
	public void after() throws InterruptedException, ProjectException {

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
		// factory.deleteProject(project);
	}

	@Test
	public void checkInitialStakeholders() throws InterruptedException {
		// add event handler to receive updates on
		TSlotConnection participant1 = participate();
		ExampleEventHandler eventHandler = new ExampleEventHandler(participant1);

		eventHandler.waitForFirstUpdate(5000);

		ItemMap<Item> map = EventManager.getItemMap(participant1.getConnectionID(), MapLink.STAKEHOLDERS);
		assertEquals(2, map.size());
		participant1.disconnect(false);
	}

	@Test
	public void checkExactlyOneFirstUpdate() throws InterruptedException {

		// add event handler to receive updates on
		TSlotConnection participant1 = participate();
		ExampleEventHandler eventHandler = new ExampleEventHandler(participant1);

		eventHandler.waitForFirstUpdate(5000);

		TSlotConnection participant2 = participate();
		ExampleEventHandler eventHandler2 = new ExampleEventHandler(participant2);
		eventHandler2.waitForFirstUpdate(5000);

		assertEquals(1, eventHandler.getNumberOfFirstUpdates());
		assertEquals(1, eventHandler2.getNumberOfFirstUpdates());

		participant1.disconnect(false);
		participant2.disconnect(false);
	}

	/******************** SUPPORT FUNCS ***********************/

	/**
	 *
	 * @return a slotconnection for a new participant.
	 */
	private TSlotConnection participate() {
		Integer slotID = ServicesManager.fireServiceEvent(IOServiceEventType.START_NEW_SESSION, SessionType.MULTI,
				project.getFileName(), TLanguage.EN);
		assertTrue(slotID != null && slotID >= 0);

		JoinReply reply = ServicesManager.fireServiceEvent(IOServiceEventType.JOIN_SESSION, slotID,
				AppType.PARTICIPANT);
		assertNotNull(reply);

		TSlotConnection slotConnection = TSlotConnection.createSlotConnection();
		slotConnection.initSettings(AppType.PARTICIPANT, SettingsManager.getServerIP(), slotID, reply.serverToken,
				reply.client.getClientToken());
		slotConnection.fireServerEvent(true, LogicEventType.SETTINGS_ALLOW_INTERACTION, true);
		assertTrue(slotConnection.connect());
		return slotConnection;
	}

}
