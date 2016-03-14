package tygronenv.translators;

import java.util.HashMap;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Parameter2Java;
import eis.iilang.Identifier;
import eis.iilang.Parameter;
import eis.iilang.ParameterList;

@SuppressWarnings("rawtypes")
public class HashMapTranslator implements Parameter2Java<HashMap> {

	public HashMapTranslator() {
		// Used for testing
	}

	@Override
	public HashMap translate(Parameter parameter) throws TranslationException {
		if (!(parameter instanceof ParameterList)) {
			throw new TranslationException("Hashmap Translation on an object that is not a list");
		}

		return translateEntries((ParameterList) parameter);
	}

	private HashMap translateEntries(ParameterList parameter) throws TranslationException {
		HashMap<Identifier, Parameter> map = new HashMap<>();

		for (Parameter entry : parameter) {
			if (!(entry instanceof ParameterList)) {
				throw new TranslationException("Hashmap Translation on an object that is not a list");
			}

			ParameterList entryList = (ParameterList) entry;

			Parameter key = entryList.get(0);
			Parameter value = entryList.get(1);

			map.put((Identifier) key, value);
		}

		return map;
	}

	@Override
	public Class<HashMap> translatesTo() {
		return HashMap.class;
	}

}
