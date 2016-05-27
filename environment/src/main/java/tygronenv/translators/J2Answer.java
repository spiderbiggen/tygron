package tygronenv.translators;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Java2Parameter;
import eis.iilang.Function;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import nl.tytech.data.core.item.Answer;
import nl.tytech.data.engine.item.Building;

/**
 * Translate {@link Building} into building(ID, name, [categories], timestate).
 * 
 * @author W.Pasman
 *
 */
public class J2Answer implements Java2Parameter<Answer> {

	@Override
	public Parameter[] translate(Answer b) throws TranslationException {
		return new Parameter[] { new Function("answer", new Numeral(b.getID()), new Identifier(b.getContents())) };
	}

	@Override
	public Class<? extends Answer> translatesFrom() {
		return Answer.class;
	}

}
