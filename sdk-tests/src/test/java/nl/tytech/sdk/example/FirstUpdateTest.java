package nl.tytech.sdk.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.security.auth.login.LoginException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import login.ProjectException;
import login.Login;
import login.ProjectFactory;
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

	private ProjectData project;
	private SlotConnection slotConnection;
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

		Thread.sleep(1000);
		factory.deleteProject(project);
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

	@Test
	public void checkInitialStakeholders() throws InterruptedException {
		// add event handler to receive updates on
		ExampleEventHandler eventHandler = new ExampleEventHandler();

		participate();
		eventHandler.waitForFirstUpdate(5000);

		ItemMap<Item> map = EventManager.getItemMap(MapLink.STAKEHOLDERS);
		assertEquals(2, map.size());
		slotConnection.disconnect(false);
	}

	@Test
	public void checkExactlyOneFirstUpdate() throws InterruptedException {
		// add event handler to receive updates on
		ExampleEventHandler eventHandler = new ExampleEventHandler();
		ExampleEventHandler eventHandler2 = new ExampleEventHandler();

		participate();
		eventHandler.waitForFirstUpdate(5000);
		eventHandler2.waitForFirstUpdate(5000);

		assertEquals(1, eventHandler.getNumberOfFirstUpdates());
		assertEquals(1, eventHandler2.getNumberOfFirstUpdates());

		slotConnection.disconnect(false);
	}

	/******************** SUPPORT FUNCS ***********************/

	private void participate() {
		Integer slotID = ServicesManager.fireServiceEvent(IOServiceEventType.START_NEW_SESSION, SessionType.MULTI,
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

}
