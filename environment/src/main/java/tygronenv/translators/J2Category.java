package tygronenv.translators;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Java2Parameter;
import eis.iilang.Identifier;
import eis.iilang.Parameter;
import nl.tytech.data.engine.serializable.Category;

public class J2Category implements Java2Parameter<Category> {

	@Override
	public Parameter[] translate(Category c) throws TranslationException {
		return new Parameter[] { new Identifier(c.toString()) };
	}

	@Override
	public Class<? extends Category> translatesFrom() {
		return Category.class;
	}

}
