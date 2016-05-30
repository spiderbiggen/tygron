package tygronenv.actions;

import java.util.LinkedList;
import java.util.List;

import eis.iilang.Parameter;
import eis.iilang.Percept;

/**
 * Interface for custom actions.
 * @author Max_G
 */
public interface CustomAction {
	/**
	 * This method is called when the action was called by an agent.
	 * @param parameters The parameters provided by the agent.
	 * @return List of percepts resulting from the action.
	 */
	List<Percept> call(LinkedList<Parameter> parameters);

	/**
	 * This method provides the name of the action, which the agent
	 * will have to call.
	 * @return The name of the action.
	 */
	String getName();
	
	/**
	 * Specifies if this actions should return a percept.
	 * @return
	 * 		True if this actions returns a percept,
	 * 		otherwise false.
	 */
	boolean returnsPercept();
}
