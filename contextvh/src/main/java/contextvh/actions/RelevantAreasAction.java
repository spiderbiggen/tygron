package contextvh.actions;

import eis.iilang.ParameterList;
import eis.iilang.Percept;
import tygronenv.TygronEntity;

/**
 * A sub-action for GetRelevantAreas.
 * @author Max Groenenboom
 */
public interface RelevantAreasAction extends CustomAction {
	/**
	 * This method is called internally by a GetRelevantAreas.
	 * @param createdPercept The Percept that has been created, add parameter to this percept.
	 * @param caller The TygronEntity representing the agent that called the action.
	 * @param parameters The parameters the getRelevantAreas action was called with.
	 */
	void internalCall(Percept createdPercept, TygronEntity caller, ParameterList parameters);

	/**
	 * The internal name for the action, it is used by GetRelevantAreas.
	 * @return The internal name.
	 */
	String getInternalName();
}
