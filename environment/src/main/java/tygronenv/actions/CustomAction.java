package tygronenv.actions;

import java.util.LinkedList;

import eis.iilang.Parameter;

public interface CustomAction {
	public void call(LinkedList<Parameter> parameters);
	public String getName();
}
