package tygronenv.actions;

import java.util.LinkedList;
import java.util.List;

import eis.iilang.Numeral;
import eis.iilang.Parameter;
import eis.iilang.Percept;

/**
 * Example action to show how it should look like.
 * It increments a counter for each call, and prints the current value of the counter.
 * @author Max_G
 */
public class CounterTestAction implements CustomAction {

	private int counter = 0;

	@Override
	public List<Percept> call(final LinkedList<Parameter> parameters) {
		counter++;
		System.out.println("Action has been called " + counter + " times.");
		System.out.println(parameters);

		//create a percept
		List<Percept> percepts = new LinkedList<Percept>();
		Percept percept = new Percept("counter", new Numeral(counter));
		percepts.add(percept);
		return percepts;
	}

	@Override
	public String getName() {
		return "counter_test_action";
	}

	@Override
	public boolean returnsPercept() {
		// TODO Auto-generated method stub
		return true;
	}

}
