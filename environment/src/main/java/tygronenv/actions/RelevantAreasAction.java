package tygronenv.actions;

import eis.iilang.ParameterList;
import eis.iilang.Percept;
import tygronenv.TygronEntity;

public interface RelevantAreasAction extends CustomAction {
	/**
	 * This method is called internally by a GetRelevantAreas.
	 * @param createdPercept The Percept that has been created, add parameter to this percept.
	 * @param parameters The parameters the getRelevantAreas action was called with.
	 */
	public void internalCall(Percept createdPercept, TygronEntity caller, ParameterList parameters);

	/**
	 * The internal name for the action, it is used by GetRelevantAreas.
	 * @return The internal name.
	 */
	public String getInternalName();
}
