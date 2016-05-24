package tygronenv;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eis.EnvironmentListener;
import eis.exceptions.ManagementException;
import eis.exceptions.NoEnvironmentException;
import eis.exceptions.PerceiveException;
import eis.iilang.EnvironmentState;
import eis.iilang.Identifier;
import eis.iilang.Parameter;
import eis.iilang.ParameterList;

public class TestEnvironment {

	private static final String STAKEHOLDERS = "stakeholders";
	private EisEnv env;
	private static String PROJECT = "project";
	private static Identifier PROJECTNAME = new Identifier("testmap");

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
		parameters.put(PROJECT, PROJECTNAME);
		// any stakeholder so not specified.
		// any slot so not specified.
		env.init(parameters);
	}

	@Test
	public void testConnectAndKill() throws ManagementException {
		Map<String, Parameter> parameters = new HashMap<String, Parameter>();
		parameters.put(PROJECT, PROJECTNAME);
		// any stakeholder so not specified.
		// any slot so not specified.
		env.init(parameters);
	}

	@Test
	public void testGetStakeHolder() throws ManagementException {
		Map<String, Parameter> parameters = new HashMap<String, Parameter>();
		parameters.put(PROJECT, PROJECTNAME);
		// parameters.put(STAKEHOLDER, new Identifier("MUNICIPALITY"));
		parameters.put(STAKEHOLDERS, new ParameterList(new Identifier("MUNICIPALITY")));
		// any slot so not specified.
		env.init(parameters);
	}

	@Test(expected = ManagementException.class)
	public void testGetWrongStakeHolder() throws ManagementException {
		Map<String, Parameter> parameters = new HashMap<String, Parameter>();
		parameters.put(PROJECT, PROJECTNAME);
		parameters.put(STAKEHOLDERS, new Identifier("BADSTAKEHOLDER"));
		// any slot so not specified.
		env.init(parameters);
	}

	enum StateType {
		WAITING, DONE, ERROR
	};

	class State {
		private StateType type;
		private Exception exc;

		public void setState(StateType t, Exception e) {
			exc = e;
			type = t;
		}

		public void waitTillDone() throws Exception {
			while (type == StateType.WAITING) {
				Thread.sleep(100);
			}
			if (type == StateType.ERROR) {
				throw exc;
			}
		}
	}

	@Test
	public void testEntityReady() throws Exception {
		State state = new State();

		env.attachEnvironmentListener(new EnvironmentListener() {

			@Override
			public void handleStateChange(EnvironmentState newState) {
			}

			@Override
			public void handleNewEntity(String entity) {
				try {
					env.getAllPerceptsFromEntity(entity);
					state.setState(StateType.DONE, null);
				} catch (PerceiveException | NoEnvironmentException e) {
					state.setState(StateType.ERROR, e);
				}
			}

			@Override
			public void handleFreeEntity(String entity, Collection<String> agents) {
			}

			@Override
			public void handleDeletedEntity(String entity, Collection<String> agents) {
			}
		});

		Map<String, Parameter> parameters = new HashMap<String, Parameter>();
		parameters.put(PROJECT, PROJECTNAME);
		parameters.put(STAKEHOLDERS, new ParameterList(new Identifier("MUNICIPALITY")));
		env.init(parameters);

		state.waitTillDone();
	}

}
