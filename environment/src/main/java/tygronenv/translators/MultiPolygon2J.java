package tygronenv.translators;

import java.util.LinkedList;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Parameter2Java;
import eis.iilang.Function;
import eis.iilang.Identifier;
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

	private static final Object MULTIPOLY = "multipolygon";

	@Override
	public MultiPolygon translate(Parameter parameter) throws TranslationException {
		if (!(parameter instanceof Function)) {
			throw new TranslationException("Multipolygon needs an EIS Function object, but got " + parameter);
		}
		Function f = (Function) parameter;
		if (!(f.getName().equals(MULTIPOLY))) {
			throw new TranslationException("'multipolygon' expected but got " + parameter);
		}
		LinkedList<Parameter> params = f.getParameters();
		if (params.size() != 1) {
			throw new TranslationException("Multipolygon square 1 parameter but got  " + params.size());
		}

		if (!(params.get(0) instanceof Identifier)) {
			throw new TranslationException("Multipolygon requires identifier but got  " + params.get(0));
		}

		String text = ((Identifier) params.get(0)).getValue();
		WKTReader reader = new WKTReader(JTSUtils.sourceFactory);
		Geometry geometry;
		try {
			geometry = reader.read(text);
		} catch (ParseException e) {
			throw new TranslationException("The multipolygon text '" + text + "' can not be parsed", e);
		}
		if (!(geometry instanceof MultiPolygon)) {
			throw new TranslationException(
					"The geometry '" + text + "' is not a multipolygon but a " + geometry.getClass());
		}
		return (MultiPolygon) geometry;
	}

	@Override
	public Class<MultiPolygon> translatesTo() {
		return MultiPolygon.class;
	}

}
