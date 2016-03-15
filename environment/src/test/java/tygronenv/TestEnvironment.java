package tygronenv;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import eis.exceptions.ManagementException;
import eis.iilang.Identifier;
import eis.iilang.Parameter;

public class TestEnvironment {

	private EisEnv env;

	@Before
	public void before() {
		env = new EisEnv();

	}

	private void init() throws ManagementException {
		Map<String, Parameter> parameters = new HashMap<String, Parameter>();
		parameters.put("map", new Identifier("givemesomemap"));
		// any stakeholder so not specified.
		// any slot so not specified.
		env.init(parameters);

	}

	@Test
	public void testBasicConnect() throws ManagementException {
		init();
	}

	@Test
	public void testConnectAndKill() throws ManagementException {
		init();
		env.kill();
	}

}
