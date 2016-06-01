package tygronenv.actions;

import java.util.LinkedList;

import eis.iilang.Parameter;
import eis.iilang.Percept;
import nl.tytech.core.client.net.SlotConnection;
import tygronenv.TygronEntity;

/**
 * Interface for custom actions.
 * @author Max Groenenboom
 */
public interface CustomAction {
	/**
	 * This method is called when the action was called by an agent.
	 * @param caller The TygronEntity that called the action.
	 * @param parameters The parameters provided by the agent.
	 * @return List of percepts resulting from the action. This list can be empty or null.
	 */
	Percept call(TygronEntity caller,SlotConnection slotConnection, LinkedList<Parameter> parameters);

	/**
	 * This method provides the name of the action, which the agent
	 * will have to call.
	 * @return The name of the action.
	 */
	String getName();
}
