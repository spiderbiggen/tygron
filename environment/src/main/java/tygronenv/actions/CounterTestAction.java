package tygronenv.actions;

import java.util.LinkedList;

import eis.iilang.Parameter;

/**
 * Example action to show how it should look like.
 * It increments a counter for each call, and prints the current value of the counter.
 * @author Max_G
 */
public class CounterTestAction implements CustomAction {

	private int counter = 0;

	@Override
	public void call(final LinkedList<Parameter> parameters) {
		counter++;
		System.out.println("Action has been called " + counter + " times.");
		System.out.println(parameters);
	}

	@Override
	public String getName() {
		return "counter_test_action";
	}

}
