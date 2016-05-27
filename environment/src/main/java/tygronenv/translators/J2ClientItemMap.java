package tygronenv.translators;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Java2Parameter;
import eis.eis2java.translation.Translator;
import eis.iilang.Parameter;
import eis.iilang.ParameterList;
import nl.tytech.core.structure.ClientItemMap;

@SuppressWarnings("rawtypes")
public class J2ClientItemMap implements Java2Parameter<ClientItemMap> {

	private final Translator translator = Translator.getInstance();

	@Override
	public Parameter[] translate(ClientItemMap map) throws TranslationException {
		Parameter[] params = new Parameter[map.size()];
		int n = 0;
		for (Object element : map) {
			Parameter[] param = translator.translate2Parameter(element);
			if (param.length == 1) {
				params[n] = param[0];
			} else {
				params[n] = new ParameterList(param);
			}
			n++;
		}
		return params;
	}

	@Override
	public Class<? extends ClientItemMap> translatesFrom() {
		return ClientItemMap.class;
	}

}
