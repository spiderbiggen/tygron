package tygronenv.translators;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Java2Parameter;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
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

		for (Indicator indicator : EventManager.<Indicator> getItemMap(MapLink.INDICATORS).values()) {
			Double increase = actionLog.getIncrease(indicator);
			if (increase != null) {
				// TODO: Implement how you want to use it, and add to parameter
				// result

			}
		}

		return new Parameter[] { new Identifier(actionLog.getAction()), new Numeral(actionLog.getID()) };
	}

	@Override
	public Class<? extends ActionLog> translatesFrom() {
		return ActionLog.class;
	}

}
