package tygronenv.translators;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Java2Parameter;
import eis.eis2java.translation.Translator;
import eis.iilang.Function;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import nl.tytech.data.engine.item.Building;
import nl.tytech.data.engine.item.PopupData;

/**
 * Translate {@link Building} into building(ID, name, [categories], timestate).
 * 
 * @author W.Pasman
 *
 */
public class J2PopupData implements Java2Parameter<PopupData> {

	private final Translator translator = Translator.getInstance();

	@Override
	public Parameter[] translate(PopupData b) throws TranslationException {

		return new Parameter[] { new Function("request", new Identifier(b.getType().toString()), new Numeral(b.getID()),
				translator.translate2Parameter(b.getAnswers())[0]) };
	}

	@Override
	public Class<? extends PopupData> translatesFrom() {
		return PopupData.class;
	}

}
