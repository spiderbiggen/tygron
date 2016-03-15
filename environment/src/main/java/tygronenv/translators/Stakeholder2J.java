package tygronenv.translators;

import java.util.Arrays;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Parameter2Java;
import eis.iilang.Identifier;
import eis.iilang.Parameter;
import nl.tytech.data.engine.item.Stakeholder.Type;

public class Stakeholder2J implements Parameter2Java<Type> {

	public Stakeholder2J() {
		// Used for testing
	}

	@Override
	public Type translate(Parameter parameter) throws TranslationException {
		if (!(parameter instanceof Identifier)) {
			throw new TranslationException();
		}
		String id = ((Identifier) parameter).getValue();
		Type value;
		try {
			value = Type.valueOf(id);
		} catch (IllegalArgumentException e) {
			throw new TranslationException(
					"unknown stakeholder type " + id + ". Allowed are:" + Arrays.asList(Type.values()));
		}
		return value;
	}

	@Override
	public Class<Type> translatesTo() {
		return Type.class;
	}

}
