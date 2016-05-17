package tygronenv.translators;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Java2Parameter;
import eis.eis2java.translation.Translator;
import eis.iilang.Function;
import eis.iilang.Parameter;
import nl.tytech.data.engine.item.Indicator;
import nl.tytech.data.engine.serializable.MapType;

public class J2Indicator implements Java2Parameter<Indicator> {
	
	private final Translator translator = Translator.getInstance();
	
	@Override
	public Parameter[] translate(Indicator o) throws TranslationException {
		//Not sure on the differences with getValue, getExactNumberValue
		//Not sure if MAQUETTE OR CURRENT IS BETTER
		return new Parameter[] {new Function("indicator"),
				translator.translate2Parameter(o.getAbsoluteValue(MapType.MAQUETTE))[0],
				translator.translate2Parameter(o.getTarget())[0]};
	}

	@Override
	public Class<? extends Indicator> translatesFrom() {
		return Indicator.class;
	}

}
