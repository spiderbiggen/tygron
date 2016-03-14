package tygronenv;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import eis.exceptions.ManagementException;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;

public class TestEnvironment {

	private EisEnv env;

	@Before
	public void before() {
		env = new EisEnv();

	}

	private void init() throws ManagementException {
		Map<String, Parameter> parameters = new HashMap<String, Parameter>();
		// use the settings as in the student's goal MAS code.
		parameters.put("stakeholder", new Numeral(new Integer(-1)));
		parameters.put("map", new Identifier("FilInMapNameHere"));
		parameters.put("slot", new Numeral(new Integer(-1)));
		env.init(parameters);

	}

	@Test
	public void testBasicConnect() throws ManagementException {
		init();
	}

}
