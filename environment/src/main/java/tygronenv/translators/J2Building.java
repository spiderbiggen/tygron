package tygronenv.translators;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Java2Parameter;
import eis.eis2java.translation.Translator;
import eis.iilang.Function;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import nl.tytech.data.engine.item.Building;

/**
 * Translate {@link Building} into building(ID, name, [categories], timestate).
 * 
 * @author W.Pasman
 *
 */
public class J2Building implements Java2Parameter<Building> {

	private final Translator translator = Translator.getInstance();

	@Override
	public Parameter[] translate(Building b) throws TranslationException {
		return new Parameter[] { new Function("building", new Numeral(b.getID()), new Identifier(b.getName()),
				translator.translate2Parameter(b.getCategories())[0],
				translator.translate2Parameter(b.getTimeState())[0]) };
	}

	@Override
	public Class<? extends Building> translatesFrom() {
		return Building.class;
	}

}
