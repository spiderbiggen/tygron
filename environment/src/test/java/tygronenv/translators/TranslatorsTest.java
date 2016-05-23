package tygronenv.translators;

import static org.junit.Assert.assertEquals;

import eis.iilang.*;
import nl.tytech.data.engine.item.Building;
import nl.tytech.data.engine.serializable.Category;
import org.junit.AfterClass;
import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.MultiPolygon;

import eis.eis2java.exception.NoTranslatorException;
import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Translator;
import org.junit.runners.ParentRunner;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

public class TranslatorsTest {

	Translator translatorfactory = Translator.getInstance();

	@Test
	public void MultiPolygon2JTest() throws NoTranslatorException, TranslationException {
		translatorfactory.registerParameter2JavaTranslator(new MultiPolygon2J());

		Function parameter = new Function("square", new Numeral(1.0), new Numeral(2.0), new Numeral(3.0),
				new Numeral(4.0));

		MultiPolygon polygon = translatorfactory.translate2Java(parameter, MultiPolygon.class);
		// check first coordinate, should be the (1,2)
		Coordinate c = polygon.getCoordinate();
		assertEquals(1.0, c.getOrdinate(0), 0.0001);
		assertEquals(2.0, c.getOrdinate(1), 0.0001);
	}

	@Test(expected = TranslationException.class)
	public void MultiPolygon2JTestMissingNumber() throws NoTranslatorException, TranslationException {
		translatorfactory.registerParameter2JavaTranslator(new MultiPolygon2J());

		Function parameter = new Function("square", new Numeral(1.0), new Numeral(3.0), new Numeral(4.0));

		MultiPolygon polygon = translatorfactory.translate2Java(parameter, MultiPolygon.class);
		// check first coordinate, should be the (1,2)
		Coordinate c = polygon.getCoordinate();
		assertEquals(1.0, c.getOrdinate(0), 0.0001);
		assertEquals(2.0, c.getOrdinate(1), 0.0001);
	}

	@Test(expected = TranslationException.class)
	public void MultiPolygon2JTestWrongArgType() throws NoTranslatorException, TranslationException {
		translatorfactory.registerParameter2JavaTranslator(new MultiPolygon2J());

		Function parameter = new Function("square", new Numeral(1.0), new Identifier("2.0"), new Numeral(3.0),
				new Numeral(4.0));

		MultiPolygon polygon = translatorfactory.translate2Java(parameter, MultiPolygon.class);
		// check first coordinate, should be the (1,2)
		Coordinate c = polygon.getCoordinate();
		assertEquals(1.0, c.getOrdinate(0), 0.0001);
		assertEquals(2.0, c.getOrdinate(1), 0.0001);
	}
}
