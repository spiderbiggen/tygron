package tygronenv.translators;

import java.util.List;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Java2Parameter;
import eis.iilang.Function;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import eis.iilang.ParameterList;
import nl.tytech.data.engine.item.Indicator;
import nl.tytech.data.engine.item.Stakeholder;

public class J2Stakeholder implements Java2Parameter<Stakeholder> {

	@Override
	public Parameter[] translate(Stakeholder o) throws TranslationException {
	    if (o.getMyIndicators().isEmpty()) {
	        return new Parameter[] { new Identifier(o.getName()) };
	    }
		return new Parameter[] { new Function("IndicatorLink", new Numeral(o.getID()), indicator(o.getMyIndicators(), o)) };
	}

	@Override
	public Class<? extends Stakeholder> translatesFrom() {
		return Stakeholder.class;
	}

	public ParameterList indicator(List<Indicator> indicatorList, Stakeholder s) {
	    ParameterList pList = new ParameterList();
	    for (Indicator ind : indicatorList) {
	        pList.add(new Function("IndicatorWeights",  new Numeral(ind.getID()), 
	                new Identifier(ind.getName()), new Numeral(s.getCurrentIndicatorWeight(ind))));
	    }
	    return pList;
	}
	
}
