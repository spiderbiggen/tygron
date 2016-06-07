package contextvh.translators;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Java2Parameter;
import eis.eis2java.translation.Translator;
import eis.iilang.Function;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import nl.tytech.data.engine.item.UpgradeType;

/**
 * Translate {@link UpgradeType} into {@code upgrade_type(<ID>, <SourceFunctionID>, <TargetFunctionID>)}.
 * UpgradeTypes without pairs are returned as {@code upgrade_type(<ID>)}.
 *
 * @author M.Houtman
 */
public class J2UpgradeType implements Java2Parameter<UpgradeType> {

    private final Translator translator = Translator.getInstance();

    /**
     * Translates the UpgradeType object in the form of:
     * {@code upgrade_type(<ID>, <SourceFunctionID>, <TargetFunctionID>)} or
     * {@code upgrade_type(<ID>)} if there is no pair available.
     */
    @Override
    public Parameter[] translate(final UpgradeType u) throws TranslationException {
            return new Parameter[]{
                    new Function("upgrade_type",
                            new Numeral(u.getID()),
                            translator.translate2Parameter(u.getPairs())[0]
                    )
            };
    }

    /**
     * Class used for translation.
     */
    @Override
    public Class<? extends UpgradeType> translatesFrom() {
        return UpgradeType.class;
    }

}
