package nl.tytech.sdk.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.prep.PreparedGeometry;
import com.vividsolutions.jts.geom.prep.PreparedGeometryFactory;

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
import nl.tytech.core.net.serializable.User;
import nl.tytech.core.net.serializable.User.AccessLevel;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.core.util.SettingsManager;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.editor.event.EditorEventType;
import nl.tytech.data.editor.event.EditorSettingsEventType;
import nl.tytech.data.editor.event.EditorStakeholderEventType;
import nl.tytech.data.engine.event.ParticipantEventType;
import nl.tytech.data.engine.item.ActionMenu;
import nl.tytech.data.engine.item.Building;
import nl.tytech.data.engine.item.Function;
import nl.tytech.data.engine.item.Function.PlacementType;
import nl.tytech.data.engine.item.Land;
import nl.tytech.data.engine.item.Setting;
import nl.tytech.data.engine.item.Stakeholder;
import nl.tytech.data.engine.item.Terrain;
import nl.tytech.data.engine.item.Zone;
import nl.tytech.data.engine.serializable.MapType;
import nl.tytech.locale.TLanguage;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.ThreadUtils;
import nl.tytech.util.logger.TLogger;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PlanBuildingsTest {

	private static Integer slotID;

	private static JoinReply reply;

	private static ProjectData data;

	private static SlotConnection slotConnection;

	private static ExampleEventHandler eventHandler;

	private static Login login;

	private static Integer stakeholderID = Item.NONE;

	private static Integer actionLogID = Item.NONE;

	@Test
	public void test01Setup() throws Exception {
		login = new Login();
		login.doLogin();
	}

	@Test
	public void test02Connect() throws Exception {

		String result = ServicesManager.testServerAPIConnection();
		assertNull(result, result);

		User user = ServicesManager.getMyUserAccount();

		assertNotNull(user);
		assertEquals(user.getUserName(), login.getUserName());

		assertTrue("You need to be at least EDITOR to run these tests!",
				user.getMaxAccessLevel().ordinal() >= AccessLevel.EDITOR.ordinal());
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
			if (eventHandler.isMapUpdated() && eventHandler.isUpdated(MapLink.STAKEHOLDERS, MapLink.LANDS)) {
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

		boolean updated = false;
		for (int i = 0; i < 60; i++) {
			if (eventHandler.isMapUpdated() && eventHandler.isUpdated(MapLink.STAKEHOLDERS, MapLink.LANDS)) {
				updated = true;
				break;
			}
			ThreadUtils.sleepInterruptible(1000);
		}
		assertTrue(updated);
	}

	@Test
	public void test08selectStakeholderToPlay() throws Exception {

		// TODO: (Frank) Enable this to be able to log into the session.
		// Prerequisite is that the session is started as SessionType.MULTI
		// slotConnection.fireServerEvent(false,
		// LogicEventType.SETTINGS_ALLOW_INTERACTION, true);

		ItemMap<Stakeholder> stakeholders = EventManager.getItemMap(MapLink.STAKEHOLDERS);
		for (Stakeholder stakeholder : stakeholders) {
			stakeholderID = stakeholder.getID();
			break;
		}
		assertTrue("Expected at least 2 stakeholders", !Item.NONE.equals(stakeholderID));
	}

	private List<Polygon> getBuildableLand(Integer stakeholderID, Integer zoneID, PlacementType placementType) {
		MapType mapType = MapType.MAQUETTE; // we are in planning mode

		Zone zone = EventManager.getItem(MapLink.ZONES, zoneID);

		//
		MultiPolygon constructableLand = zone.getMultiPolygon();
		for (Terrain terrain : EventManager.<Terrain> getItemMap(MapLink.TERRAINS)) {
			if (placementType == PlacementType.LAND && terrain.getType().isWater()) {
				constructableLand = JTSUtils.difference(constructableLand, terrain.getMultiPolygon(mapType));

			} else if (placementType == PlacementType.WATER && !terrain.getType().isWater()) {
				constructableLand = JTSUtils.difference(constructableLand, terrain.getMultiPolygon(mapType));

			}
		}

		// Reserved land is land currently awaiting land transaction
		Setting reservedLandSetting = EventManager.getItem(MapLink.SETTINGS, Setting.Type.RESERVED_LAND);
		MultiPolygon reservedLand = reservedLandSetting.getMultiPolygon();
		if (JTSUtils.containsData(reservedLand)) {
			constructableLand = JTSUtils.difference(constructableLand, reservedLand);
		}

		List<Geometry> myLands = new ArrayList<>();
		for (Land land : EventManager.<Land> getItemMap(MapLink.LANDS)) {
			if (land.getOwnerID().equals(stakeholderID)) {
				MultiPolygon mp = JTSUtils.intersection(constructableLand, land.getMultiPolygon());
				if (JTSUtils.containsData(mp)) {
					myLands.add(mp);
				}
			}
		}

		MultiPolygon myLandsMP = JTSUtils.createMP(myLands);
		// (Frank) For faster intersection checks, used prepared geometries.
		PreparedGeometry prepMyLand = PreparedGeometryFactory.prepare(myLandsMP);
		for (Building building : EventManager.<Building> getItemMap(MapLink.BUILDINGS)) {
			if (prepMyLand.intersects(building.getMultiPolygon(mapType))) {
				myLandsMP = JTSUtils.difference(myLandsMP, building.getMultiPolygon(mapType));
			}
		}

		List<Polygon> buildablePolygons = JTSUtils.getPolygons(myLandsMP);
		for (Polygon polygon : buildablePolygons) {
			TLogger.info(polygon.toString());
		}
		return buildablePolygons;
	}

	@Test
	public void test09GetConstructableLand() throws Exception {

		Integer zoneID = 0;

		Function function = null;
		actionMenuLoop: for (ActionMenu actionMenu : EventManager.<ActionMenu> getItemMap(MapLink.ACTION_MENUS)) {
			if (actionMenu.isBuildable(stakeholderID))
				for (Function buildableFunction : actionMenu.getFunctionTypeOptions()) {
					if (buildableFunction.getPlacementType() != PlacementType.WATER) {
						function = buildableFunction;
					}
					break actionMenuLoop;
				}
		}

		assertTrue("No applicable land function found!", function != null);

		List<Polygon> buildablePolygons = getBuildableLand(stakeholderID, zoneID, function.getPlacementType());

		assertTrue("No buildable polygons found!", buildablePolygons.size() > 0);

		MultiPolygon selectedPlot = JTSUtils.createMP(buildablePolygons.get(0));
		int floors = function.getDefaultFloors();
		actionLogID = slotConnection.fireServerEvent(true, ParticipantEventType.BUILDING_PLAN_CONSTRUCTION,
				stakeholderID, function.getID(), floors, selectedPlot);

		assertTrue("Action was not succesfull!", !Item.NONE.equals(actionLogID));

	}

	@Test
	public void test10GetConstructableLand() throws Exception {
		boolean updated = false;
		for (int i = 0; i < 60; i++) {
			if (eventHandler.isUpdated(MapLink.POPUPS, MapLink.LANDS)) {
				updated = true;
				break;
			}
			ThreadUtils.sleepInterruptible(1000);
		}
		assertTrue(updated);

	}

	@Test
	public void test13closeRegularSession() throws Exception {
		slotConnection.disconnect(false);
	}

	@Test
	public void test14deleteProject() throws Exception {
		assertTrue(ServicesManager.fireServiceEvent(IOServiceEventType.DELETE_PROJECT, data.getFileName()));
	}
}
