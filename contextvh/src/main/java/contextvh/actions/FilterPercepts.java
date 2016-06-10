package tygronenv.actions;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import contextvh.ContextEntity;
import eis.eis2java.exception.TranslationException;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import eis.iilang.ParameterList;
import eis.iilang.Percept;
import nl.tytech.util.logger.TLogger;
import tygronenv.TygronEntity;

public class FilterPercepts implements CustomAction {

	@Override
	public Percept call(ContextEntity caller, LinkedList<Parameter> parameters) throws TranslationException {
	try {
		// Get and translate parameters.
		Iterator<Parameter> params = parameters.iterator();
		Number callID = ((Numeral) params.next()).getValue();
		String actionType = ((Identifier) params.next()).getValue();
		ParameterList filters = new ParameterList();
		if (params.hasNext()) {
			Parameter filterParam = params.next();
			// If the filter parameter is not a ParameterList, it is invalid.
			if (filterParam instanceof ParameterList) {
				filters = (ParameterList) filterParam;
			}
		}
		

		return createPercept(caller, actionType, callID, filters);
	} catch (Exception e) {
		TLogger.exception(e);
		throw e;
	}
	}

	@Override
	public String getName() {
		return "filter_percepts";
	}
	
	private Percept createPercept(final ContextEntity caller, final String actionType,
			final Number callID, final ParameterList parameters) throws TranslationException {
		Percept result = new Percept("relevant_areas");
		result.addParameter(new Numeral(callID));

		RelevantAreasAction action = internalActions.get(actionType);
		if (action == null) {
			throw new TranslationException("unknown action GetRelevantAreas(_, " + actionType + ", _)");
		} else {
			action.internalCall(result, caller, parameters);
		}

		return result;
	}
	
	public List<Percept> filterPercepts(List<Percept> percepts) {
		return null;
	}


}
