package contextvh.actions;

import contextvh.ContextEnv;
import eis.exceptions.ManagementException;
import eis.iilang.Function;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import eis.iilang.ParameterList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import tygronenv.MyEnvListener;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Test the GetRelevantAreasBuy class.
 * @author Stefan Breetveld
 *
 */
public class GetRelevantAreasBuyTest {

	private static final String STAKEHOLDERS = "stakeholders";
	private static final String MUNICIPALITY = "MUNICIPALITY";
	private static final String PROJECT = "project";
	private static final Identifier PROJECT_NAME = new Identifier("testutilsmap");

	private static final double AREA_MUNICIPALITY = 0;
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
	 * Test the getUsableLand function.
	 */
	@Test
	public void testGetUsableLand() {
		final GetRelevantAreasBuy action = new GetRelevantAreasBuy(null);
		final double area = action.getUsableArea(env.getEntity(MUNICIPALITY), null).getArea();
		assertEquals(AREA_MUNICIPALITY, area, MAX_DEVIATION);
	}

	/**
	 * Test the getUsableLand function.
	 */
	@Test
	public void testGetUsableLandWithZones() {
		final ParameterList zones = new ParameterList(new Numeral(0), new Numeral(1), new Numeral(2));
		final Parameters parameters = new Parameters(new ParameterList(new Function("zones", zones)));
		final GetRelevantAreasBuy action = new GetRelevantAreasBuy(null);
		final double area = action.getUsableArea(env.getEntity(MUNICIPALITY), parameters).getArea();
		assertEquals(AREA_MUNICIPALITY, area, MAX_DEVIATION);
	}
}
