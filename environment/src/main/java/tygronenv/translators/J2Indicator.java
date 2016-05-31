package tygronenv.translators;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Java2Parameter;
import eis.iilang.Function;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import eis.iilang.ParameterList;
import nl.tytech.data.engine.item.Indicator;


/**
 * Translates {@link Indicator} into indicator(ID, Current, Target).
 *
 * @author Stefan de Vringer
 */
public class J2Indicator implements Java2Parameter<Indicator> {

	/**
	 * Translate the indicator into a parameter.
	 */
	@Override
	public Parameter[] translate(final Indicator indicator) throws TranslationException {
		String explanation = indicator.getExplanation();
		int indID = indicator.getID();
		double target = indicator.getTarget();
		double currentValue = 0;
		ParameterList pl = new ParameterList();

		if (explanation.contains("<p hidden>")) {
			explanation = indicator.getExplanation().split("<p hidden>")[1].split("</p>")[0];
			if (explanation.contains("multi")) {
				currentValue = Double.parseDouble(explanation.split("multi")[0]);
				pl = zoneLink(indID, target, explanation.split("multi")[1]);
			} else if (explanation.contains("multiT")) {
				String[] targetValues = explanation.split("MultiT")[0].split("\\\\t");
				currentValue = Double.parseDouble(targetValues[0]);
				target = Double.parseDouble(targetValues[1]);
				pl = zoneLink(indID, target, explanation.split("multi")[1]);
			}
		}
		return new Parameter[]{new Function("indicator",
				new Numeral(indID),
				new Numeral(currentValue),
				new Numeral(target), pl
		)};
	}

	/**
	 * Translates a list of items into a ParameterList of zonelinks
	 *
	 * @param id       The id of the indicator.
	 * @param target   The target of the indicator.
	 * @param itemList The list of items to parse.
	 * @return ParameterList of zoneLinks
	 */
	public ParameterList zoneLink(int id, double target, String itemList) {
		ParameterList pList = new ParameterList();
		String[] items = itemList.split("\\\\n");
		for (String item : items) {
			String[] types = item.split("\\\\t");
			if (types.length == 2) {
				pList.add(new Function("zone_link", new Numeral(Integer.parseInt(types[0])),
						new Numeral(id), new Numeral(Double.parseDouble(types[1])), new Numeral(target)));
			} else if (types.length == 3) {
				pList.add(new Function("zone_link", new Numeral(Integer.parseInt(types[0])),
						new Numeral(id), new Numeral(Double.parseDouble(types[1])), new Numeral(Double.parseDouble(types[2]))));
			}
		}
		return pList;
	}

	/**
	 * Get the class which is translated from.
	 */
	@Override
	public Class<? extends Indicator> translatesFrom() {
		return Indicator.class;
	}

}
