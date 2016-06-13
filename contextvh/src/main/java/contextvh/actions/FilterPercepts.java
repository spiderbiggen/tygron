package contextvh.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import contextvh.ContextEntity;
import eis.eis2java.exception.TranslationException;
import eis.iilang.Identifier;
import eis.iilang.Parameter;
import eis.iilang.ParameterList;
import eis.iilang.Percept;
import nl.tytech.util.logger.TLogger;

public class FilterPercepts implements CustomAction {
	
	public static final FilterPercepts instance = new FilterPercepts();
	
	private ArrayList<String> disabledPercepts = new ArrayList<String>();

	@Override
	public Percept call(ContextEntity caller, LinkedList<Parameter> parameters) throws TranslationException {
	try {
		// Get and translate parameters.
		Iterator<Parameter> params = parameters.iterator();
		ParameterList filterParamList = new ParameterList();
		if (params.hasNext()) {
			Parameter filterParam = params.next();
			// If the filter parameter is not a ParameterList, it is invalid.
			if (filterParam instanceof ParameterList) {
				filterParamList = (ParameterList) filterParam;
				processFilter(filterParamList);
			}
		}
		

		return createPercept(caller, filterParamList);
	} catch (Exception e) {
		TLogger.exception(e);
		throw e;
	}
	}

	private void processFilter(ParameterList filterParam) {
		disabledPercepts = new ArrayList<String>();
		for (int i = 0; i < filterParam.size(); i++){
			Parameter param = filterParam.get(i);
			if (param instanceof Identifier) {
				Identifier percept = (Identifier) param;
				String perceptname = percept.getValue();
				disabledPercepts.add(perceptname);
			}
				
		}
		
	}

	@Override
	public String getName() {
		return "filter_percepts";
	}
	
	private Percept createPercept(final ContextEntity caller, final ParameterList parameters) throws TranslationException {
		Percept result = new Percept("filtered_percepts");
		
		if (identifierCheck(parameters))
			result.addParameter(parameters);

		return result;
	}
	
	private boolean identifierCheck(final ParameterList parameters) {
		for (int i = 0; i < parameters.size(); i++){
			Parameter param = parameters.get(i);
			if (!(param instanceof Identifier)) {
				return false;
			}	
		}
		return true;
	}

	public LinkedList<Percept> filterPercepts(List<Percept> percepts) {
		LinkedList<Percept> result = new LinkedList<Percept>();
		
		for (Percept percept : percepts) {
			if (!disabledPercepts.contains(percept.getName()))
				result.add(percept);
		}
		
		return result;
		
	}

	public static CustomAction getinstance() {
		return instance;
	}


}
