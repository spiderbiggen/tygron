package tygronenv.translators;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Java2Parameter;
import eis.iilang.Identifier;
import eis.iilang.Parameter;
import nl.tytech.data.engine.item.BaseFunction;

public class J2BaseFunction implements Java2Parameter<BaseFunction> {

	@Override
	public Parameter[] translate(BaseFunction bf) throws TranslationException {
		// HACK
		return new Parameter[] { new Identifier(bf.toString()) };
	}

	@Override
	public Class<? extends BaseFunction> translatesFrom() {
		return BaseFunction.class;
	}

}
