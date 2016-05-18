package tygronenv.translators;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Java2Parameter;
import eis.eis2java.translation.Translator;
import eis.iilang.Function;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import nl.tytech.data.engine.item.Zone;

/**
 *
 * @author Frank Baars
 *
 */
public class J2Zone implements Java2Parameter<Zone> {

	private final Translator translator = Translator.getInstance();

	@Override
	public Parameter[] translate(Zone zone) throws TranslationException {
		return new Parameter[] { new Function("zone", new Numeral(zone.getID()), new Identifier(zone.getName())) };
	}

	@Override
	public Class<? extends Zone> translatesFrom() {
		return Zone.class;
	}

}
