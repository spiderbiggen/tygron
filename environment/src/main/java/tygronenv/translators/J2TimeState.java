package tygronenv.translators;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Java2Parameter;
import eis.iilang.Identifier;
import eis.iilang.Parameter;
import nl.tytech.data.engine.serializable.TimeState;

public class J2TimeState implements Java2Parameter<TimeState> {

	@Override
	public Parameter[] translate(TimeState state) throws TranslationException {
		return new Parameter[] { new Identifier(state.toString()) };
	}

	@Override
	public Class<? extends TimeState> translatesFrom() {
		return TimeState.class;
	}

}
