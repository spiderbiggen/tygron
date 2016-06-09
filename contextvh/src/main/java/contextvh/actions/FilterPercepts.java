package tygronenv.actions;

import java.util.Iterator;
import java.util.LinkedList;

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
	public Percept call(TygronEntity caller, LinkedList<Parameter> parameters) throws TranslationException {
		throws TranslationException {
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

	@Override
	public String getName() {
		return "filter_percepts";
	}

}
