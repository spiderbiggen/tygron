package tygronenv;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eis.exceptions.ManagementException;
import eis.iilang.Identifier;
import eis.iilang.Parameter;

public class TestEnvironment {

	private EisEnv env;
	private static Identifier MAP = new Identifier("testmap");

	@Before
	public void before() {
		env = new EisEnv();
	}

	@After
	public void after() throws ManagementException, InterruptedException {
		env.kill();
		env = null;
	}

	@Test
	public void testBasicConnect() throws ManagementException {
		Map<String, Parameter> parameters = new HashMap<String, Parameter>();
		parameters.put("map", MAP);
		// any stakeholder so not specified.
		// any slot so not specified.
		env.init(parameters);
	}

	@Test
	public void testConnectAndKill() throws ManagementException {
		Map<String, Parameter> parameters = new HashMap<String, Parameter>();
		parameters.put("map", MAP);
		// any stakeholder so not specified.
		// any slot so not specified.
		env.init(parameters);
	}

	@Test
	public void testGetStakeHolder() throws ManagementException {
		Map<String, Parameter> parameters = new HashMap<String, Parameter>();
		parameters.put("map", MAP);
		parameters.put("stakeholder", new Identifier("MUNICIPALITY"));
		// any slot so not specified.
		env.init(parameters);
	}

	@Test(expected = ManagementException.class)
	public void testGetWrongStakeHolder() throws ManagementException {
		Map<String, Parameter> parameters = new HashMap<String, Parameter>();
		parameters.put("map", MAP);
		parameters.put("stakeholder", new Identifier("BADSTAKEHOLDER"));
		// any slot so not specified.
		env.init(parameters);
	}

	@Test(expected = ManagementException.class)
	public void testGetUnavailableStakeHolder() throws ManagementException {
		Map<String, Parameter> parameters = new HashMap<String, Parameter>();
		parameters.put("map", MAP);
		parameters.put("stakeholder", new Identifier("FARMER"));
		// any slot so not specified.
		env.init(parameters);
	}

}
