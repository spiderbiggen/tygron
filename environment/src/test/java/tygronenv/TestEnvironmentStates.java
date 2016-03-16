package tygronenv;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import eis.AgentListener;
import eis.EnvironmentListener;
import eis.exceptions.AgentException;
import eis.exceptions.ManagementException;
import eis.exceptions.RelationException;
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
	public void testStateChange() throws ManagementException, RelationException, AgentException {

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

		// try to connect
		env.registerAgent(AGENT);
		env.associateEntity(AGENT, ENTITY);
		env.attachAgentListener(AGENT, agentlistener);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// check that agent listener receives initial percepts.
		Percept expectedPercept = new Percept("stakeholders",
				new ParameterList(new Parameter[] { new Identifier("Municipality"), new Identifier("Inhabitants") }));
		verify(agentlistener).handlePercept(AGENT, expectedPercept);
		env.kill();
	}

}