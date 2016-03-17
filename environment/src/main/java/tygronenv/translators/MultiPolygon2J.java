package tygronenv.translators;

import java.util.LinkedList;

import com.vividsolutions.jts.geom.MultiPolygon;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Parameter2Java;
import eis.iilang.Function;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import nl.tytech.util.JTSUtils;

/**
 * Currently only supports squares. format: use {@link Function} to create
 * object square(double x, double y, double width, double height).
 * 
 * @author W.Pasman
 *
 */
public class MultiPolygon2J implements Parameter2Java<MultiPolygon> {

	private static final Object SQUARE = "square";

	@Override
	public MultiPolygon translate(Parameter parameter) throws TranslationException {
		if (!(parameter instanceof Function)) {
			throw new TranslationException("Multipolygon needs an EIS Function object, but got " + parameter);
		}
		Function f = (Function) parameter;
		if (!(f.getName().equals(SQUARE))) {
			throw new TranslationException("Multipolygon supports only 'square' but got " + parameter);
		}
		LinkedList<Parameter> params = f.getParameters();
		if (params.size() != 4) {
			throw new TranslationException("Multipolygon square requires 4 parameters bot got " + params.size());
		}
		double[] values = new double[4];
		for (int n = 0; n < 4; n++) {
			Parameter param = params.get(n);
			if (!(param instanceof Numeral)) {
				throw new TranslationException("Multipolygon parameter " + n + " must be a Numeral, but got " + param);
			}
			values[n] = ((Numeral) param).getValue().doubleValue();
		}

		return JTSUtils.createSquare(values[0], values[1], values[2], values[3]);
	}

	@Override
	public Class<MultiPolygon> translatesTo() {
		return MultiPolygon.class;
	}

}
