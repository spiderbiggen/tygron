package tygronenv.translators;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Java2Parameter;
import eis.eis2java.translation.Translator;
import eis.iilang.Function;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import nl.tytech.data.engine.item.Building;

/**
 * Translate {@link Building} into building(ID, name, [categories], floors, stakeHolderId, ConstYear).
 *
 * @author S. Breetveld
 *
 */
public class J2ExtBuilding implements Java2Parameter<Building> {

    private final Translator translator = Translator.getInstance();

    @Override
    public Parameter[] translate(Building b) throws TranslationException {
        return new Parameter[] { new Function("extbuilding", new Numeral(b.getID()), new Identifier(b.getName()),
                translator.translate2Parameter(b.getCategories())[0],
                new Numeral(b.getFloors())),
                new Numeral(b.getOwnerID()),
                new Numeral(b.getConstructionYear())
        };
    }

    @Override
    public Class<? extends Building> translatesFrom() {
        return Building.class;
    }

}