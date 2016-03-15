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

	@Test
	public void testBasicConnect() throws ManagementException {
		Map<String, Parameter> parameters = new HashMap<String, Parameter>();
		parameters.put("map", new Identifier("givemesomemap"));
		// any stakeholder so not specified.
		// any slot so not specified.
		env.init(parameters);
	}

	@Test
	public void testConnectAndKill() throws ManagementException {
		Map<String, Parameter> parameters = new HashMap<String, Parameter>();
		parameters.put("map", new Identifier("givemesomemap"));
		// any stakeholder so not specified.
		// any slot so not specified.
		env.init(parameters);
		env.kill();
	}

	@Test
	public void testGetStakeHolder() throws ManagementException {
		Map<String, Parameter> parameters = new HashMap<String, Parameter>();
		parameters.put("map", new Identifier("givemesomemap"));
		parameters.put("stakeholder", new Identifier("MUNICIPALITY"));
		// any slot so not specified.
		env.init(parameters);
		env.kill();
	}

	@Test(expected = ManagementException.class)
	public void testGetWrongStakeHolder() throws ManagementException {
		Map<String, Parameter> parameters = new HashMap<String, Parameter>();
		parameters.put("map", new Identifier("givemesomemap"));
		parameters.put("stakeholder", new Identifier("BADSTAKEHOLDER"));
		// any slot so not specified.
		env.init(parameters);
		env.kill();
	}

}
