package tygronenv.translators;

import com.vividsolutions.jts.geom.MultiPolygon;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Java2Parameter;
import eis.iilang.Function;
import eis.iilang.Identifier;
import eis.iilang.Parameter;

/**
 * Translate {@link MultiPolygon} into square(double x, double y, double width,
 * double height)
 * 
 * @author W.Pasman
 *
 */
public class J2MultiPolygon implements Java2Parameter<MultiPolygon> {

	@Override
	public Parameter[] translate(MultiPolygon b) throws TranslationException {
		return new Parameter[] { new Function("multipolygon", new Identifier(b.toText())) };
	}

	@Override
	public Class<? extends MultiPolygon> translatesFrom() {
		return MultiPolygon.class;
	}

}
