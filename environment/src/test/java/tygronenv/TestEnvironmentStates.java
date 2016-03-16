package tygronenv;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import eis.AgentListener;
import eis.EnvironmentListener;
import eis.exceptions.AgentException;
import eis.exceptions.ManagementException;
import eis.exceptions.RelationException;
import eis.iilang.EnvironmentState;
import eis.iilang.Identifier;
import eis.iilang.Parameter;
import eis.iilang.ParameterList;
import eis.iilang.Percept;

public class TestEnvironmentStates {

	private EisEnv env;
	private static Identifier MAP = new Identifier("testmap");

	@Before
	public void before() {
		env = new EisEnv();
	}

	private final String AGENT = "agent";
	private final String ENTITY = "entity";

	@Test
	public void testEntityAppears() throws ManagementException, RelationException, AgentException {

		EnvironmentListener envlistener = mock(EnvironmentListener.class);
		AgentListener agentlistener = mock(AgentListener.class);

		env.attachEnvironmentListener(envlistener);
		Map<String, Parameter> parameters = new HashMap<String, Parameter>();
		parameters.put("map", MAP);
		parameters.put("stakeholder", new Identifier("MUNICIPALITY"));
		// any slot so not specified.
		env.init(parameters);

		// after the init, a new entity should appear that we can connect to.
		verify(envlistener).handleNewEntity(ENTITY);

		env.kill();
	}

	@Test
	public void testStateChange() throws ManagementException, RelationException, AgentException, InterruptedException {
		// try to connect
		AgentListener agentlistener = mock(AgentListener.class);

		MyEnvListener envlistener = new MyEnvListener(agentlistener);

		env.attachEnvironmentListener(envlistener);
		Map<String, Parameter> parameters = new HashMap<String, Parameter>();
		parameters.put("map", MAP);
		parameters.put("stakeholder", new Identifier("MUNICIPALITY"));
		// any slot so not specified.
		env.init(parameters);

		while (!envlistener.connected) {
			Thread.sleep(100);
		}
		assertNull(envlistener.exitcondition); // check connect went ok.

		Thread.sleep(3000); // wait some more, to receive expected percept.

		// check that agent listener receives initial percepts.
		// there later will be another stakeholders percept with , new
		// Identifier("Inhabitants")
		Percept expectedPercept = new Percept("stakeholders",
				new ParameterList(new Parameter[] { new Identifier("Municipality") }));
		verify(agentlistener).handlePercept(AGENT, expectedPercept);

		env.kill();
	}

	/**
	 * Custom listener, so that we can immediately attach the agent when the
	 * entity appears. This makes sure that we do not miss percepts.
	 *
	 */
	class MyEnvListener implements EnvironmentListener {
		public boolean connected = false;
		public Exception exitcondition = null;
		private AgentListener agentlistener;

		public MyEnvListener(AgentListener agentlistener) {
			this.agentlistener = agentlistener;
		}

		@Override
		public void handleStateChange(EnvironmentState newState) {
		}

		@Override
		public void handleNewEntity(String entity) {
			try {
				connectAgent(entity);
			} catch (Exception e) {
				exitcondition = e;
			}
			connected = true;
		}

		@Override
		public void handleFreeEntity(String entity, Collection<String> agents) {
		}

		@Override
		public void handleDeletedEntity(String entity, Collection<String> agents) {
		}

		private void connectAgent(String entity) throws AgentException, RelationException {
			env.registerAgent(AGENT);
			env.associateEntity(AGENT, ENTITY);
			env.attachAgentListener(AGENT, agentlistener);
		}

	};

}