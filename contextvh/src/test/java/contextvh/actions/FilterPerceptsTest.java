package contextvh.actions;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.LinkedList;

import org.junit.Test;
import org.powermock.reflect.Whitebox;

import contextvh.ContextEntity;
import eis.eis2java.exception.TranslationException;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import eis.iilang.ParameterList;
import eis.iilang.Percept;

/**
 * Test the FilterPercepts class.
 * @author Dennis van Peer
 *
 */
public class FilterPerceptsTest {

	private FilterPercepts mockFilterPercepts = mock(FilterPercepts.class);
	private ContextEntity mockEntity = mock(ContextEntity.class);
	
	@Test
	public void testCall() throws TranslationException {
		FilterPercepts action = new FilterPercepts();
		LinkedList<Parameter> parameters = new LinkedList<Parameter>();
		ParameterList percepts = new ParameterList();
		Parameter percept = new Identifier("stakeholders");
		percepts.add(percept);
		parameters.add(percepts);
		action.call(mockEntity, parameters);
		ArrayList<String> disabledPercepts = Whitebox.getInternalState(action, "disabledPercepts");
		assertTrue(disabledPercepts.contains("stakeholders"));
	}

	@Test
	public void testGetName() {
		FilterPercepts action = new FilterPercepts();
		assertTrue(action.getName().equals("filter_percepts"));
	}

	@Test
	public void testFilterPercepts() throws TranslationException {
		FilterPercepts action = new FilterPercepts();
		LinkedList<Parameter> parameters = new LinkedList<Parameter>();
		ParameterList filter = new ParameterList();
		Parameter perceptParameter1 = new Identifier("stakeholders");
		Parameter perceptParameter2 = new Identifier("indicators");
		Parameter perceptParameter3 = new Identifier("buildings");
		filter.add(perceptParameter1);
		filter.add(perceptParameter2);
		filter.add(perceptParameter3);
		parameters.add(filter);
		
		LinkedList<Percept> percepts = new LinkedList<Percept>();
		Percept percept1 = new Percept("functions");
		Percept percept2 = new Percept("stakeholders");
		Percept percept3 = new Percept("buildings");
		percepts.add(percept1);
		percepts.add(percept2);
		percepts.add(percept3);
		
		
		action.call(mockEntity, parameters);
		LinkedList<Percept> filteredPercepts = action.filterPercepts(percepts);
		LinkedList<String> filteredPerceptNames = getPerceptNames(filteredPercepts);
		
		assertTrue(filteredPerceptNames.contains("functions")&&!(filteredPerceptNames.contains("stakeholders")));
		filteredPerceptNames.remove("functions");
		assertTrue(filteredPerceptNames.isEmpty());
		
	}

	private LinkedList<String> getPerceptNames(LinkedList<Percept> percepts) {
		LinkedList<String> result = new LinkedList<String>();
		for (Percept percept : percepts)
			result.add(percept.getName());
		return result;
	}

}
