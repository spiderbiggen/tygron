package contextvh;

import eis.eis2java.translation.Java2Parameter;
import eis.eis2java.translation.Parameter2Java;
import eis.eis2java.translation.Translator;
import tygronenv.EisEnv;
import tygronenv.translators.HashMap2J;
import tygronenv.translators.J2ActionLog;
import tygronenv.translators.J2ActionMenu;
import tygronenv.translators.J2Answer;
import tygronenv.translators.J2Building;
import tygronenv.translators.J2Category;
import tygronenv.translators.J2ClientItemMap;
import tygronenv.translators.J2Function;
import tygronenv.translators.J2Indicator;
import tygronenv.translators.J2Land;
import tygronenv.translators.J2MultiPolygon;
import tygronenv.translators.J2PopupData;
import tygronenv.translators.J2Setting;
import tygronenv.translators.J2Stakeholder;
import tygronenv.translators.J2TimeState;
import tygronenv.translators.J2UpgradeType;
import tygronenv.translators.J2Zone;
import tygronenv.translators.MultiPolygon2J;
import tygronenv.translators.ParamEnum2J;
import tygronenv.translators.Stakeholder2J;

/**
 * Created by Stefan Breetveld on 3-6-2016.
 */
public class ContextEnv extends EisEnv {
    /**
     * General initialization: translators.
     */
    public ContextEnv() {
        installTranslators();
    }

    private Java2Parameter<?>[] j2p = new Java2Parameter<?>[]{new J2ClientItemMap(), new J2Stakeholder(),
            new J2Setting(), new J2Function(), new J2Category(), new J2Building(), new J2TimeState(),
            new J2ActionLog(), new J2ActionMenu(), new J2Zone(), new J2Land(), new J2MultiPolygon(),
            new J2PopupData(), new J2Answer(), new J2Indicator(), new J2UpgradeType()};

    private Parameter2Java<?>[] p2j = new Parameter2Java<?>[]{new ParamEnum2J(),
            new HashMap2J(), new Stakeholder2J(), new MultiPolygon2J()};

    /**
     * Installs the required EIS2Java translators.
     */
    private void installTranslators() {
        Translator translatorfactory = Translator.getInstance();

        for (Java2Parameter<?> translator : j2p) {
            translatorfactory.registerJava2ParameterTranslator(translator);
        }
        for (Parameter2Java<?> translator : p2j) {
            translatorfactory.registerParameter2JavaTranslator(translator);
        }
    }
}
