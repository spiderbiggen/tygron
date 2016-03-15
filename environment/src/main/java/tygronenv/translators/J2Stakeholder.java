package tygronenv.translators;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Java2Parameter;
import eis.iilang.Identifier;
import eis.iilang.Parameter;
import nl.tytech.data.engine.item.Stakeholder;

public class J2Stakeholder implements Java2Parameter<Stakeholder> {

	@Override
	public Parameter[] translate(Stakeholder o) throws TranslationException {
		return new Parameter[] { new Identifier(o.getName()) };
	}

	@Override
	public Class<? extends Stakeholder> translatesFrom() {
		return Stakeholder.class;
	}

}
