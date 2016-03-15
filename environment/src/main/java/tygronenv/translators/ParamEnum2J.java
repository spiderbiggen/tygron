package tygronenv.translators;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Parameter2Java;
import eis.iilang.Identifier;
import eis.iilang.Parameter;
import tygronenv.configuration.ParamEnum;

public class ParamEnum2J implements Parameter2Java<ParamEnum> {

	public ParamEnum2J() {
		// Used for testing
	}

	@Override
	public ParamEnum translate(Parameter parameter) throws TranslationException {
		if (!(parameter instanceof Identifier)) {
			throw new TranslationException();
		}
		String id = ((Identifier) parameter).getValue();

		for (ParamEnum params : ParamEnum.values()) {
			if (params.getParam().equals(id)) {
				return params;
			}
		}

		throw new TranslationException();
	}

	@Override
	public Class<ParamEnum> translatesTo() {
		return ParamEnum.class;
	}

}
