package tygronenv.translators;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Java2Parameter;
import eis.iilang.Identifier;
import eis.iilang.Parameter;
import nl.tytech.data.engine.item.Setting;

public class J2Setting implements Java2Parameter<Setting> {

	@Override
	public Parameter[] translate(Setting s) throws TranslationException {
		return new Parameter[] { new Identifier(s.toString()) };
	}

	@Override
	public Class<? extends Setting> translatesFrom() {
		return Setting.class;
	}

}
