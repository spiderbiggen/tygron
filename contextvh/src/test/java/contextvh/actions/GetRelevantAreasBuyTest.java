package contextvh.actions;

import contextvh.ContextEnv;
import eis.exceptions.ManagementException;
import eis.iilang.Function;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import eis.iilang.ParameterList;
import eis.iilang.Percept;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import tygronenv.MyEnvListener;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test the GetRelevantAreasBuy class.
 * @author Stefan Breetveld
 *
 */
public class GetRelevantAreasBuyTest {

	private static final String STAKEHOLDERS = "stakeholders";
	private static final String MUNICIPALITY = "GEMEENTE";
	private static final String PROJECT = "project";
	private static final Identifier PROJECT_NAME = new Identifier("testbuymap");

	private static final double MAX_DEVIATION = 0.0001;

	private ContextEnv env;

	/**
	 * Initialize the tests with a new environment.
	 * @throws ManagementException {@link ManagementException}
	 * @throws InterruptedException {@link InterruptedException}
	 */
	@Before
	public void init() throws ManagementException, InterruptedException {
		env = new ContextEnv();
		final MyEnvListener listener = new MyEnvListener();
		env.attachEnvironmentListener(listener);

		final Map<String, Parameter> parameters = new HashMap<>();
		parameters.put(PROJECT, PROJECT_NAME);
		parameters.put(STAKEHOLDERS, new ParameterList(new Identifier(MUNICIPALITY)));
		env.init(parameters);
		assertEquals(MUNICIPALITY, listener.waitForEntity());
	}

	/**
	 * Shuts down the test environment.
	 * @throws ManagementException {@link ManagementException}
	 */
	@After
	public void tearDown() throws ManagementException {
		env.kill();
	}

	/**
	 *
	 */
	@Test
	public void testInternalCall() {
		final int zoneNum = 5;
		final GetRelevantAreasBuy action = new GetRelevantAreasBuy(null);
		final Percept result = new Percept("relevant_areas");
		action.internalCall(result, env.getEntity(MUNICIPALITY), null);
		assertFalse(result.getParameters().isEmpty());
		assertTrue(((ParameterList) result.getParameters().get(0)).size() >= zoneNum);
	}

	/**
	 *
	 */
	@Test
	public void testInternalCallWithZones() {
		final int zoneNum = 2;
		final GetRelevantAreasBuy action = new GetRelevantAreasBuy(null);
		final Percept result = new Percept("relevant_areas");
		final Parameters parameters = new Parameters(new ParameterList(
				new Function("zones", new Numeral(0), new Numeral(1))));
		action.internalCall(result, env.getEntity(MUNICIPALITY), parameters);
		assertFalse(result.getParameters().isEmpty());
		System.out.println(((ParameterList) result.getParameters().get(0)).size());
		assertTrue(((ParameterList) result.getParameters().get(0)).size() >= zoneNum);
	}

	/**
	 * Test the getUsableLand function.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGetUsableLandBadWeather() {
		final GetRelevantAreasBuy action = new GetRelevantAreasBuy(null);
		action.getUsableArea(env.getEntity(MUNICIPALITY), null);
	}

	/**
	 * Test the getUsableLand function.
	 */
	@Test
	public void testGetUsableLandWithZone() {
		final GetRelevantAreasBuy action = new GetRelevantAreasBuy(null);
		final double area = action.getUsableArea(env.getEntity(MUNICIPALITY), 0).getArea();
		final double expectedArea = 241_900;
		assertEquals(expectedArea, area, MAX_DEVIATION);
	}
}
