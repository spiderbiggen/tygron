package contextvh.translators;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Translator;
import eis.iilang.Function;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import nl.tytech.data.engine.item.Building;
import nl.tytech.data.engine.serializable.MapType;

/**
 * Translate {@link Building} into building(ID, name, ownerID, constructionYear, [categories], FunctionID, numFloors).
 *
 * @author W.Pasman
 */
public class ContextJ2Building extends tygronenv.translators.J2Building {

    private final Translator translator = Translator.getInstance();

    @Override
    public Parameter[] translate(final Building b) throws TranslationException {
        return new Parameter[] {
                new Function("building",
                        new Numeral(b.getID()),
                        new Identifier(b.getName()),
                        new Numeral(b.getOwnerID()),
                        new Numeral(b.getConstructionYear()),
                        translator.translate2Parameter(b.getCategories())[0],
                        new Numeral(b.getFunctionID()),
                        new Numeral(b.getFloors()),
                        translator.translate2Parameter(b.getMultiPolygon(MapType.MAQUETTE))[0]),
        };
    }

}
