package tygronenv.actions;

import java.util.LinkedList;

import eis.iilang.Numeral;
import eis.iilang.Parameter;
import eis.iilang.Percept;
import tygronenv.TygronEntity;

/**
 * Example action to show how it should look like.
 * It increments a counter for each call, and prints the current value of the counter.
 * @author Max_G
 */
public class CounterExample implements CustomAction {

	private int counter = 0;

	@Override
	public Percept call(TygronEntity caller, final LinkedList<Parameter> parameters) {
		counter++;
		System.out.println("Action has been called " + counter + " times.");
		System.out.println(parameters);

		//create a percept
		Percept percept = new Percept("counter", new Numeral(counter));
		return percept;
	}

	@Override
	public String getName() {
		return "count";
	}
}
