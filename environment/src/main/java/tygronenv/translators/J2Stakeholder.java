package tygronenv.translators;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Java2Parameter;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import nl.tytech.data.engine.item.Stakeholder;

public class J2Stakeholder implements Java2Parameter<Stakeholder> {

	@Override
	public Parameter[] translate(Stakeholder stakeholder) throws TranslationException {
		Double budget = stakeholder.getBudget();
		if (budget == null) {
			budget = 0d;
		}

		return new Parameter[] { new Identifier(stakeholder.getName()), new Numeral(stakeholder.getID()),
				new Numeral(budget) };
	}

	@Override
	public Class<? extends Stakeholder> translatesFrom() {
		return Stakeholder.class;
	}

}
