package tygronenv.translators;

import java.util.ArrayList;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Java2Parameter;
import eis.eis2java.translation.Translator;
import eis.iilang.Function;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.engine.item.Zone;
import nl.tytech.data.engine.serializable.Category;
/**
 * Translate {@link Zone} into zones(ID, name, maxFloors, size, [categories], [FunctionIDs]).
 * 
 * @author T.Brunner, J.N de Vries 
 *
 */
public class J2Zone implements Java2Parameter<Zone>{

	private final Translator translator = Translator.getInstance();

	/**
	 * @return a parameter for the zone, 
	 * containing zone id, zone name, maximum allowed floor size, 
	 * the total size of the zone, the allowed categories 
	 * and the allowed function IDs within the zone.
	 */
	@Override
	public Parameter[] translate(Zone z) throws TranslationException {
		return new Parameter[] { new Function("Zone", new Numeral(z.getID()), new Identifier(z.getName()), new Numeral(z.getMaxAllowedFloors()) 
				, new Numeral(size(z))
				, translator.translate2Parameter(z.getAllowedCategories())[0]
				, translator.translate2Parameter(getfunctionsID(z))[0]
				)};
	}
	
	/**
     * @return list of functions id's allowed for this zone.
     */
    public Numeral[] getfunctionsID(Zone z) {
    	ArrayList<Numeral> FunctionIds = new ArrayList<Numeral>();
    	ArrayList<nl.tytech.data.engine.item.Function> functions = z.getfunctions();
    	int length = FunctionIds.size();
    	Numeral[] numerals = new Numeral[length];
    	int pos = 0;
    	for (nl.tytech.data.engine.item.Function function: functions){
    		FunctionIds.add(new Numeral(function.getID()));
    		numerals[pos] = new Numeral(function.getID());
    		pos++;
    	}
    	
    	
    	return numerals;
    }
	
    /**
     * @return size of this zone.
     */
	public double size(Zone z){
		return z.getMultiPolygon().getArea();
	}
	

	@Override
	public Class<? extends Zone> translatesFrom() {
		return Zone.class;
	}
}
