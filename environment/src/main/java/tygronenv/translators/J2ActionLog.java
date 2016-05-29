package tygronenv.translators;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Java2Parameter;
import eis.iilang.Function;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import eis.iilang.ParameterList;
import nl.tytech.core.client.event.EventManager;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.engine.item.ActionLog;
import nl.tytech.data.engine.item.Indicator;

/**
 *
 * @author Frank Baars
 *
 */
public class J2ActionLog implements Java2Parameter<ActionLog> {

	@Override
	public Parameter[] translate(ActionLog actionLog) throws TranslationException {
	    
	    ParameterList parList = new ParameterList();
	    
		for (Indicator indicator : EventManager.<Indicator> getItemMap(MapLink.INDICATORS).values()) {
			Double increase = actionLog.getIncrease(indicator);
			if (increase != null) {
				// TODO: Implement how you want to use it, and add to parameter
				// result
			    parList.add(new ParameterList(new Numeral(indicator.getID()), new Numeral(increase)));
			}
		}

		return new Parameter[] { new Function("actionlog", new Numeral(actionLog.getStakeholder().getID()), new Identifier(actionLog.getAction()), new Numeral(actionLog.getID()), parList) };
	}

	@Override
	public Class<? extends ActionLog> translatesFrom() {
		return ActionLog.class;
	}

}
