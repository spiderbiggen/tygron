package tygronenv.actions;

import java.util.LinkedList;

import eis.iilang.Parameter;

/**
 * Interface for custom actions.
 * @author Max_G
 */
public interface CustomAction {
	/**
	 * This method is called when the action was called by an agent.
	 * @param parameters The parameters provided by the agent.
	 */
	void call(LinkedList<Parameter> parameters);

	/**
	 * This method provides the name of the action, which the agent
	 * will have to call.
	 * @return The name of the action.
	 */
	String getName();
}
