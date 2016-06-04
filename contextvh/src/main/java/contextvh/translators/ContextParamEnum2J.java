package contextvh.translators;

import contextvh.configuration.ContextParamEnum;
import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Parameter2Java;
import eis.iilang.Identifier;
import eis.iilang.Parameter;
import tygronenv.configuration.ParamEnum;

public class ContextParamEnum2J implements Parameter2Java<ContextParamEnum> {

    public ContextParamEnum2J() {
        // Used for testing
    }

    @Override
    public ContextParamEnum translate(Parameter parameter) throws TranslationException {
        if (!(parameter instanceof Identifier)) {
            throw new TranslationException();
        }
        String id = ((Identifier) parameter).getValue();

        for (ContextParamEnum params : ContextParamEnum.values()) {
            if (params.getParam().equals(id)) {
                return params;
            }
        }

        throw new TranslationException("Unknown init parameter " + parameter);
    }

    @Override
    public Class<ContextParamEnum> translatesTo() {
        return ContextParamEnum.class;
    }

}
