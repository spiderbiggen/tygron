package contextvh.actions;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import contextvh.ContextEnv;
import eis.exceptions.ManagementException;
import eis.iilang.Function;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import eis.iilang.ParameterList;
import nl.tytech.util.JTSUtils;
import org.junit.After;
import org.junit.Test;
import tygronenv.MyEnvListener;

import java.util.Arrays;
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
	public void connect() throws ManagementException, InterruptedException {
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
	 * shuts down any remaining environments.
	 * @throws ManagementException {@link ManagementException}
	 */
	@After
	public void tearDown() throws ManagementException {
		if (env != null) {
			env.kill();
		}
		env = null;
	}

	/**
	 * Test the getUsableLand function.
	 * @throws ManagementException {@link ManagementException}
	 * @throws InterruptedException {@link InterruptedException}
	 */
	@Test
	public void testGetUsableLand() throws ManagementException, InterruptedException {
		connect();
		final GetRelevantAreasBuy action = new GetRelevantAreasBuy(null);
		final double area = action.getUsableArea(env.getEntity(MUNICIPALITY), null).getArea();
		assertEquals(AREA_MUNICIPALITY, area, MAX_DEVIATION);
		env.kill();
	}

	/**
	 * Test the getUsableLand function.
	 * @throws ManagementException {@link ManagementException}
	 * @throws InterruptedException {@link InterruptedException}
	 */
	@Test
	public void testGetUsableLandWithZones() throws ManagementException, InterruptedException {
		connect();
		final ParameterList zones = new ParameterList(new Numeral(0), new Numeral(1), new Numeral(2));
		final Parameters parameters = new Parameters(new ParameterList(new Function("zones", zones)));
		final GetRelevantAreasBuy action = new GetRelevantAreasBuy(null);
		final double area = action.getUsableArea(env.getEntity(MUNICIPALITY), parameters).getArea();
		assertEquals(AREA_MUNICIPALITY, area, MAX_DEVIATION);
		env.kill();
	}

	/**
	 * Placeholder test for the internal call.
	 */
	@Test
	public void internalCall() {
		assert true;
	}


	/**
	 *
	 */
	@Test
	public void geometryToRectangle() {
		final Geometry shape = JTSUtils.createPolygon(Arrays.asList(
				new Coordinate(0, 50),
				new Coordinate(-50, 30),
				new Coordinate(3, 500),
				new Coordinate(2, -50)
		));
		final Geometry rectangle = JTSUtils.createPolygon(Arrays.asList(
				new Coordinate(-50, -50),
				new Coordinate(3, -50),
				new Coordinate(3, 500),
				new Coordinate(-50, 500),
				new Coordinate(-50, -50)
		));
		final GetRelevantAreasBuy action = new GetRelevantAreasBuy(null);
		final Geometry result = action.geometryToRectangle(shape);
		assertEquals(rectangle, result);
	}
}
