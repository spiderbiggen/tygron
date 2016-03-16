package tygronenv;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import eis.EnvironmentListener;
import eis.exceptions.AgentException;
import eis.exceptions.ManagementException;
import eis.exceptions.NoEnvironmentException;
import eis.exceptions.PerceiveException;
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

	private final String ENTITY = "entity";

	@Test
	public void testEntityAppears() throws ManagementException, RelationException, AgentException {

		EnvironmentListener envlistener = mock(EnvironmentListener.class);

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
	public void testStateChange() throws ManagementException, RelationException, AgentException, InterruptedException,
			PerceiveException, NoEnvironmentException {

		Map<String, Parameter> parameters = new HashMap<String, Parameter>();
		parameters.put("map", MAP);
		parameters.put("stakeholder", new Identifier("MUNICIPALITY"));
		// any slot so not specified.
		env.init(parameters);

		Thread.sleep(3000); // HACK wait some, to receive expected percepts.

		LinkedList<Percept> percepts = env.getAllPerceptsFromEntity(ENTITY);
		Percept expectedPercept = new Percept("stakeholders",
				new ParameterList(new Parameter[] { new Identifier("Municipality") }));
		assertTrue(percepts.contains(expectedPercept));

		env.kill();
	}

}