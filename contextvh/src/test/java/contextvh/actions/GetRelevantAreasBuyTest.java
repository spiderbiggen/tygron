package contextvh.actions;

import contextvh.ContextEnv;
import eis.exceptions.ManagementException;
import eis.iilang.Identifier;
import eis.iilang.Parameter;
import eis.iilang.ParameterList;
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

	private static final double AREA_MUNICIPALITY = 1_000_000;
	private static final double MAX_DEVIATION = 0.0001;

	/**
	 * Test the getUsaubleLand function.
	 * @throws ManagementException {@link ManagementException}
	 * @throws InterruptedException {@link InterruptedException}
	 */
	@Test
	public void testGetUsableLand() throws ManagementException, InterruptedException {
		final ContextEnv env = new ContextEnv();
		final MyEnvListener listener = new MyEnvListener();
		env.attachEnvironmentListener(listener);

		final Map<String, Parameter> parameters = new HashMap<>();
		parameters.put(PROJECT, PROJECT_NAME);
		parameters.put(STAKEHOLDERS, new ParameterList(new Identifier(MUNICIPALITY)));
		env.init(parameters);
		assertEquals(MUNICIPALITY, listener.waitForEntity());

		final GetRelevantAreasBuy action = new GetRelevantAreasBuy(null);
		final double area = action.getUsableArea(env.getEntity(MUNICIPALITY), null).getArea();
		assertEquals(AREA_MUNICIPALITY, area, MAX_DEVIATION);
	}
}
