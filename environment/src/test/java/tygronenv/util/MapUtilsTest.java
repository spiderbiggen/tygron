package tygronenv.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;

import eis.exceptions.ManagementException;
import eis.iilang.Identifier;
import eis.iilang.Parameter;
import eis.iilang.ParameterList;
import nl.tytech.util.JTSUtils;
import tygronenv.EisEnv;
import tygronenv.MyEnvListener;

/**
 * Test for the MapUtils class.
 * @author Rico Tubbing
 *
 */
public class MapUtilsTest {

	private static final String STAKEHOLDERS = "stakeholders";
	private static final String MUNICIPALITY = "MUNICIPALITY";
	private static final String INHABITANTS = "INHABITANTS";
	private EisEnv env;
	private static final String PROJECT = "project";
	private static final Identifier PROJECTNAME = new Identifier("testmaputils");

	private static final int INHABITANTS_ID = 1;
	private static final int MUNICIPALITY_ID = 0;

	private static final double AREA_MUNICIPALITY = (1000 - 0) * (1000 - 0);

	private static final int NUM_ZONES = 4;

	private static final int ZONE_WITH_WATER = 0;
	private static final int ZONE_ONLY_GRASS = 1;



	/**
	 * Set up the environment.
	 */
	@Before
	public void before() {
		env = new EisEnv();
	}

	/**
	 * Kill the environment.
	 * @throws ManagementException Management exception
	 * @throws InterruptedException Interrupted exception.
	 */
	@After
	public void after() throws ManagementException, InterruptedException {
		env.kill();
	}


	/**
	 * Test if the getMyLands returns zero land for the inhabitans.
	 * @throws ManagementException Management exception
	 * @throws InterruptedException Interrupted exception.
	 */
	@Test
	public void testGetMyLandsInhabitant() throws ManagementException, InterruptedException {
		joinAsInhabitants();
		MultiPolygon mp = MapUtils.getMyLands(INHABITANTS_ID);
		assertTrue(mp.isEmpty());
		assertEquals(0, mp.getArea(), 0);
	}

	/**
	 * Test if the getMyLands returns all the land for the municipality.
	 * @throws ManagementException Management exception
	 * @throws InterruptedException Interrupted exception.
	 */
	@Test
	public void testGetMyLandsMunicipality() throws ManagementException, InterruptedException {
		 joinAsMunicipality();
		 MultiPolygon mp = MapUtils.getMyLands(MUNICIPALITY_ID);
		 assertEquals(AREA_MUNICIPALITY, mp.getArea(), 0);
	}


	/**
	 * Test if all the zones of the municipality is the whole map.
	 * @throws ManagementException Management exception
	 * @throws InterruptedException Interrupted exception.
	 */
	@Test
	public void testGetAllZonesCombined() throws ManagementException, InterruptedException {
		joinAsMunicipality();
		Geometry geo = MapUtils.getZonesCombined();
		assertEquals(AREA_MUNICIPALITY, geo.getArea(), 0);
	}

	/**
	 * Test for municipality to see if two zones combined is correct.
	 * @throws ManagementException Management exception
	 * @throws InterruptedException Interrupted exception.
	 */
	@Test
	public void testGetTwoZonesCombined() throws ManagementException, InterruptedException {
		joinAsMunicipality();
		Geometry geo = MapUtils.getZonesCombined(0, 1);
		assertEquals(AREA_MUNICIPALITY / NUM_ZONES  * 2, geo.getArea(), 0);
	}

	/**
	 * Test for municipality to see if one zone combined is correct.
	 * @throws ManagementException Management exception
	 * @throws InterruptedException Interrupted exception.
	 */
	@Test
	public void testGetOneZonesCombined() throws ManagementException, InterruptedException {
		joinAsMunicipality();
		Geometry geo = MapUtils.getZonesCombined(1);
		assertEquals(AREA_MUNICIPALITY / NUM_ZONES, geo.getArea(), 0);
	}

	/**
	 * Test if a bad zone returns a empty Geometry object.
	 * @throws ManagementException Management exception
	 * @throws InterruptedException Interrupted exception.
	 */
	@Test
	public void testGetZonesCombinedBad() throws ManagementException, InterruptedException {
		joinAsMunicipality();
		final int badZone = 10;
		Geometry geo = MapUtils.getZonesCombined(badZone);
		assertEquals(0, geo.getArea(), 0);
	}

	/**
	 * Test if removing water works with no water.
	 * @throws ManagementException Management exception
	 * @throws InterruptedException Interrupted exception.
	 */
	@Test
	public void testRemoveZeroWater() throws ManagementException, InterruptedException {
		joinAsMunicipality();
		Geometry geo = MapUtils.getZonesCombined(ZONE_ONLY_GRASS);
		MultiPolygon mp = JTSUtils.createMP(geo);
		mp = MapUtils.removeWater(mp);
		assertEquals(AREA_MUNICIPALITY / NUM_ZONES, mp.getArea(), 0);
	}

	/**
	 * Test if removing water works with only 1 square meter of water.
	 * @throws ManagementException Management exception
	 * @throws InterruptedException Interrupted exception.
	 */
	@Test
	public void testRemoveAllWater() throws ManagementException, InterruptedException {
		joinAsMunicipality();
		Geometry geo = MapUtils.getZonesCombined(ZONE_WITH_WATER);
		MultiPolygon mp = JTSUtils.createMP(geo);
		mp = MapUtils.removeWater(mp);
		double expected = AREA_MUNICIPALITY / NUM_ZONES - 1;
		assertEquals(expected, mp.getArea(), 0);
	}

	/**
	 * Test if removing land works with only 1 square meter of water in the multipolygon.
	 * @throws ManagementException Management exception
	 * @throws InterruptedException Interrupted exception.
	 */
	@Test
	public void testRemoveAlmostAllLand() throws ManagementException, InterruptedException {
		joinAsMunicipality();
		Geometry geo = MapUtils.getZonesCombined(ZONE_WITH_WATER);
		MultiPolygon mp = JTSUtils.createMP(geo);
		mp = MapUtils.removeLand(mp);
		double expected = 1;
		assertEquals(expected, mp.getArea(), 0);
	}

	/**
	 * Test if removing land removes all land if there is only land in the multipolygon.
	 * @throws ManagementException Management exception
	 * @throws InterruptedException Interrupted exception.
	 */
	@Test
	public void testRemoveAllLand() throws ManagementException, InterruptedException {
		joinAsMunicipality();
		Geometry geo = MapUtils.getZonesCombined(ZONE_ONLY_GRASS);
		MultiPolygon mp = JTSUtils.createMP(geo);
		mp = MapUtils.removeLand(mp);
		assertEquals(0, mp.getArea(), 0);
	}

	/**
	 * Test if an empty multipolygon (where nothing is build on) returns the total area
	 * of that multipolygon after calling removeReservedLand.
	 * @throws ManagementException Management exception
	 * @throws InterruptedException Interrupted exception.
	 */
	@Test
	public void testRemoveZeroReservedLand() throws ManagementException, InterruptedException {
		joinAsMunicipality();
		Geometry geo = MapUtils.getZonesCombined(ZONE_ONLY_GRASS);
		MultiPolygon mp = JTSUtils.createMP(geo);
		mp = MapUtils.removeReservedLand(mp);
		assertEquals(AREA_MUNICIPALITY / NUM_ZONES, mp.getArea(), 0);
	}


	/**
	 * Check if removeReservedLand can handle a empty multipolygon.
	 * @throws ManagementException Management exception
	 * @throws InterruptedException Interrupted exception.
	 */
	@Test
	public void testRemoveAllReservedLand() throws ManagementException, InterruptedException {
		joinAsMunicipality();
		Geometry geo = MapUtils.getZonesCombined(ZONE_ONLY_GRASS);
		MultiPolygon mp = JTSUtils.createMP(geo);
		mp = MapUtils.removeLand(mp); //this gives us zero land
		assertEquals(0, mp.getArea(), 0);
		mp = MapUtils.removeReservedLand(mp);
		assertEquals(0, mp.getArea(), 0);
	}





	// JOIN FUNCTIONS.
	/**
	 * Init env and ask for municipality as stakeholder.
	 *
	 * @throws ManagementException Managements exception
	 * @throws InterruptedException Interrupted exception.
	 */
	private void joinAsMunicipality() throws ManagementException, InterruptedException {
		MyEnvListener listener = new MyEnvListener();
		env.attachEnvironmentListener(listener);

		Map<String, Parameter> parameters = new HashMap<String, Parameter>();
		parameters.put(PROJECT, PROJECTNAME);
		parameters.put(STAKEHOLDERS, new ParameterList(new Identifier(MUNICIPALITY)));
		// any slot so not specified.
		env.init(parameters);

		assertEquals(MUNICIPALITY, listener.waitForEntity());
	}

	/**
	 * Init env and ask for inhabitant as stakeholder.
	 * @throws ManagementException {@link MangementExption}
	 * @throws InterruptedException {@link InterruptedException}
	 */
	private void joinAsInhabitants()
			throws ManagementException, InterruptedException {
		MyEnvListener listener = new MyEnvListener();
		env.attachEnvironmentListener(listener);

		Map<String, Parameter> parameters = new HashMap<String, Parameter>();
		parameters.put(PROJECT, PROJECTNAME);
		parameters.put(STAKEHOLDERS, new ParameterList(new Identifier(INHABITANTS)));

		// any slot so not specified.
		env.init(parameters);

		assertEquals(INHABITANTS, listener.waitForEntity());
	}


}
