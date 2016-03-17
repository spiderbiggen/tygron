package tygronenv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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

	private void joinAsMunicipality() throws ManagementException, InterruptedException {
		Map<String, Parameter> parameters = new HashMap<String, Parameter>();
		parameters.put("map", MAP);
		parameters.put("stakeholder", new Identifier("MUNICIPALITY"));
		// any slot so not specified.
		env.init(parameters);

		Thread.sleep(1500); // HACK wait some, to receive expected percepts.
	}

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

		joinAsMunicipality();

		LinkedList<Percept> percepts = env.getAllPerceptsFromEntity(ENTITY);
		Percept expectedPercept = new Percept("stakeholders",
				new ParameterList(new Parameter[] { new Identifier("Municipality") }));
		assertTrue(percepts.contains(expectedPercept));

		env.kill();
	}

	@Test
	public void testFindMakeRoadFunction() throws ManagementException, RelationException, AgentException,
			InterruptedException, PerceiveException, NoEnvironmentException {

		joinAsMunicipality();

		LinkedList<Percept> percepts = env.getAllPerceptsFromEntity(ENTITY);

		// find the FUNCTIONS percept
		Percept function = null;
		for (Percept percept : percepts) {
			if (percept.getName().equals("functions")) {
				function = percept;
			}
		}
		assertNotNull("no FUNCTIONS percept found", function);
		assertEquals(1, function.getParameters().size());

		/**
		 * We should have received something like <code>
		 * functions([['Vacant Lot',0,[OTHER]],['Mid-Century affordable
		 * housing',1,[SOCIAL]],...])	 </code>
		 */
		Parameter functions = function.getParameters().get(0);
		assertEquals(ParameterList.class, functions.getClass());
		for (Parameter f : (ParameterList) functions) {
			System.out.println(f);
			// f is something like ['Vacant Lot',0,[OTHER]]
			assertEquals(ParameterList.class, f.getClass());
			Parameter categories = ((ParameterList) f).get(2);
			assertEquals(ParameterList.class, categories.getClass());
		}

		env.kill();
	}

}