package tygronenv.actions;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;

import eis.eis2java.exception.TranslationException;
import eis.iilang.Numeral;
import eis.iilang.ParameterList;
import nl.tytech.util.JTSUtils;
import tygronenv.translators.J2MultiPolygon;

/**
 * Test for the getRelevantAreas class.
 * @author Rico
 *
 */
public class GetRelevantAreasTest {

	/**
	 * Register the multipolygon translator.
	 */
	@Before
	public void setUp() {
		GetRelevantAreas.TRANSLATOR.registerJava2ParameterTranslator(new J2MultiPolygon());
	}

	/**
	 * Test if convertMPtoPL function gives the correct area.
	 * @throws TranslationException A translate exception.
	 */
	@Test
	public void convertMPtoPLTest() throws TranslationException {
		Geometry triangle = GetRelevantAreasBuildTest.createTriangle();
		MultiPolygon triangleMP = JTSUtils.createMP(triangle);
		ParameterList list = GetRelevantAreas.convertMPtoPL(triangleMP);
		Numeral area = (Numeral) list.get(1);
		final double expected = 0.5;
		assertEquals(expected, area.getValue().doubleValue(), 0);
	}

}
