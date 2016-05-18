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

		return new Parameter[] { new Identifier(actionMenu.getName().toLowerCase()), new Numeral(actionMenu.getID()),
				list };
	}

	@Override
	public Class<? extends ActionMenu> translatesFrom() {
		return ActionMenu.class;
	}

}
