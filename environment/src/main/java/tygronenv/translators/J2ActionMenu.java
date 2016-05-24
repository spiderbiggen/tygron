package tygronenv.translators;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Java2Parameter;
import eis.eis2java.translation.Translator;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import eis.iilang.ParameterList;
import nl.tytech.data.engine.item.ActionMenu;
import nl.tytech.data.engine.item.Function;
import nl.tytech.data.engine.item.SpecialOption;

/**
 *
 * @author Frank Baars
 *
 */
public class J2ActionMenu implements Java2Parameter<ActionMenu> {

	private final Translator translator = Translator.getInstance();

	@Override
	public Parameter[] translate(ActionMenu actionMenu) throws TranslationException {
		ParameterList list = new ParameterList();
		for (Function function : actionMenu.getFunctionTypeOptions()) {
			list.add(new ParameterList(translator.translate2Parameter(function)));
		}
		ParameterList specialOptions = new ParameterList();
		for (SpecialOption specialOption : actionMenu.getSpecialOptions()) {
			list.add(new Identifier(specialOption.getType().name()));
		}

		return new Parameter[] { new Identifier("actions"), new Numeral(actionMenu.getID()), list, specialOptions };
	}

	@Override
	public Class<? extends ActionMenu> translatesFrom() {
		return ActionMenu.class;
	}

}
