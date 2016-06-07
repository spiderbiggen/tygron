package tygronenv.translators;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Java2Parameter;
import eis.eis2java.translation.Translator;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import eis.iilang.ParameterList;
import nl.tytech.data.engine.item.Function;

/**
 * Translates BaseFunction into the {@link ParameterList} with (name of function
 * , item ID, categorylist)
 *
 * @author W.Pasman
 *
 */
public class J2Function implements Java2Parameter<Function> {

	private final Translator translator = Translator.getInstance();

	@Override
	public Parameter[] translate(Function bf) throws TranslationException {
		return new Parameter[] { new Identifier(bf.getName().toLowerCase()), new Numeral(bf.getID()),
				translator.translate2Parameter(bf.getCategories())[0] };
	}

	@Override
	public Class<? extends Function> translatesFrom() {
		return Function.class;
	}

}
