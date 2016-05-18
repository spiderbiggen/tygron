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

/**
 * Translator class from Stakeholder class into IndicatorLink Parameter.
 * IndicatorLink(<stakeholderID>, [(<indicatorID>, <indicatorName>, 
 * <indicatorWeight>), ..])
 * @author Haoming
 *
 */
public class J2Stakeholder implements Java2Parameter<Stakeholder> {

    /**
     * Method to translate the stakeholder into an indicatorLink percept.
     */
	@Override
	public Parameter[] translate(Stakeholder o) throws TranslationException {
	    if (o.getMyIndicators().isEmpty()) {
	        return new Parameter[] { new Identifier(o.getName()) };
	    }
		return new Parameter[] { new Function("IndicatorLink", new Numeral(o.getID()), 
		        indicator(o.getMyIndicators(), o)) };
	}

	/**
	 * Method which returns the class it translates.
	 */
	@Override
	public Class<? extends Stakeholder> translatesFrom() {
		return Stakeholder.class;
	}

	/**
	 * Method for creating the tupled list of indicators and weights.
	 * @param indicatorList List of indicators of the stakeholder.
	 * @param s Stakeholder that has these indicators.
	 * @return ParameterList with indicatorWeights.
	 */
	public ParameterList indicator(List<Indicator> indicatorList, Stakeholder s) {
	    ParameterList pList = new ParameterList();
	    for (Indicator ind : indicatorList) {
	        pList.add(new Function("IndicatorWeights",  new Numeral(ind.getID()), 
	                new Identifier(ind.getName()), new Numeral(s.getCurrentIndicatorWeight(ind))));
	    }
	    return pList;
	}
	
}
