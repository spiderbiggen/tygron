package tygronenv.translators;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Java2Parameter;
import eis.eis2java.translation.Translator;
import eis.iilang.Function;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import nl.tytech.data.engine.item.Land;

/**
 * Translate {@link Land} into land(id,name, owner, poly).
 * 
 * @author W.Pasman
 *
 */
public class J2Land implements Java2Parameter<Land> {

	private final Translator translator = Translator.getInstance();

	@Override
	public Parameter[] translate(Land b) throws TranslationException {
		return new Parameter[] {
				new Function("land", new Numeral(b.getID()), translator.translate2Parameter(b.getOwner().getID())[0],
						translator.translate2Parameter(b.getMultiPolygon())[0]) };
	}

	@Override
	public Class<? extends Land> translatesFrom() {
		return Land.class;
	}

}
