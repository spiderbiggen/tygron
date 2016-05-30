package tygronenv.actions;

import java.util.LinkedList;

import eis.iilang.Parameter;

public class CounterTestAction implements CustomAction {

	int counter = 0;

	@Override
	public void call(LinkedList<Parameter> parameters) {
		System.out.println("Action has been called " + counter++ + " times.");
		System.out.println(parameters);
	}

	@Override
	public String getName() {
		return "counter_test_action";
	}

}
