package tygronenv.translators;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Java2Parameter;
import eis.iilang.Function;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import nl.tytech.data.engine.item.Indicator;
import nl.tytech.data.engine.serializable.MapType;


/**
 * Translates {@link Indicator} into indicator(ID, Current, Target).
 * 
 * @author Stefan de Vringer
 *
 */
public class J2Indicator implements Java2Parameter<Indicator> {
	
	/**
	 * Translate the indicator into a parameter.
	 */
	@Override
	public Parameter[] translate(final Indicator indicator) throws TranslationException {
		//getExactNumberValue gives the actual current value of the indicator
		//in the sense that if you have a budget indicator
		//and currently 1,000,000 euros it will give back 1,000,000
		//(regardless of the target value).
		
		//The Current value returned can't be null.
		Double value = indicator.getExactNumberValue(MapType.MAQUETTE);
		if (value == null) {
			value = 0.0;
		}
		
		return new Parameter[] {new Function("indicator",
				new Numeral(indicator.getID()),
				new Numeral(value),
				new Numeral(indicator.getTarget()))};
	}
	
	/**
	 * Get the class which is translated from.
	 */
	@Override
	public Class<? extends Indicator> translatesFrom() {
		return Indicator.class;
	}

}
