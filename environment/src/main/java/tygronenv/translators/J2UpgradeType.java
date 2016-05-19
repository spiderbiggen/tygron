package tygronenv.translators;

import java.util.List;
import java.util.Set;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Java2Parameter;
import eis.eis2java.translation.Translator;
import eis.iilang.Function;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import eis.iilang.ParameterList;
import nl.tytech.data.engine.item.Building;
import nl.tytech.data.engine.item.GlobalIndicator;
import nl.tytech.data.engine.item.UpgradeType;
import nl.tytech.data.engine.serializable.UpgradePair;

/**
 * Translate {@link UpgradeType} into UpgradeType(ID, [UpgradePair]).
 * 
 * @author W.Pasman
 *
 */
public class J2UpgradeType implements Java2Parameter<UpgradeType> {

	private final Translator translator = Translator.getInstance();

	@Override
	public Parameter[] translate(UpgradeType u) throws TranslationException {
		return new Parameter[] { new Function("upgrade_type", new Numeral(u.getID()), pairs(u.getPairs())) };
	}

	public ParameterList pairs(List<UpgradePair> pairs) {
		ParameterList pList = new ParameterList();
		for(UpgradePair p: pairs){
			pList.add(new Function("upgrade_pair", new Numeral(p.getSourceFunctionID()), new Numeral(p.getTargetFunctionID())));
		}
		return pList;
	}
	
	@Override
	public Class<? extends UpgradeType> translatesFrom() {
		return UpgradeType.class;
	}

}
