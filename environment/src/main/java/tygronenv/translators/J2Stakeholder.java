package tygronenv.translators;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Java2Parameter;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import nl.tytech.data.engine.item.Stakeholder;

/**
 * Translator for the Stakeholder percept.
 * @author Rico
 *
 */
public class J2Stakeholder implements Java2Parameter<Stakeholder> {

	/**
	 * Translate the stakeholder object in the form of:
	 * stakeholder/4 - stakeholders([<ID>, <Name>, <Budget>, <Income>])
	 */
	@Override
	public Parameter[] translate(final Stakeholder stakeholder) throws TranslationException {
		return new Parameter[] {
				new Numeral(stakeholder.getID()),
				new Identifier(stakeholder.getName()),
				new Numeral(stakeholder.getStartBudget()),
				new Numeral(stakeholder.getYearlyIncome())};
	}
	
	/**
	 * Class from which it is translated.
	 */
	@Override
	public Class<? extends Stakeholder> translatesFrom() {
		return Stakeholder.class;
	}

}
