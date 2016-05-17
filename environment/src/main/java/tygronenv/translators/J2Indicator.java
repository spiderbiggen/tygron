package tygronenv.translators;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Java2Parameter;
import eis.eis2java.translation.Translator;
import eis.iilang.Function;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import nl.tytech.data.engine.item.Indicator;
import nl.tytech.data.engine.serializable.MapType;

public class J2Indicator implements Java2Parameter<Indicator> {
	
	private final Translator translator = Translator.getInstance();
	
	@Override
	public Parameter[] translate(Indicator o) throws TranslationException {
		//Not sure on the differences with getValue, getExactNumberValue
		//Not sure if MAQUETTE OR CURRENT IS BETTER
		
		return new Parameter[] {new Function("indicator",
				new Numeral(o.getID()),
				new Numeral(o.getAbsoluteValue(MapType.MAQUETTE)),
				new Numeral(o.getTarget()))};
	}
	
	@Override
	public Class<? extends Indicator> translatesFrom() {
		return Indicator.class;
	}

}
