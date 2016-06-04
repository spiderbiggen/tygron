package contextvh.translators;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.MultiPolygon;
import eis.eis2java.exception.TranslationException;
import eis.iilang.Function;
import eis.iilang.Identifier;

/**
 * Tests all of the possible interactions with {@link MultiPolygon2J}.
 * @author Max
 */
public class MultiPolygon2JTest {

	private final MultiPolygon2J translator = new MultiPolygon2J();

	/**
	 * Tests if an exception is thrown when the parameter isn't
	 * the correct type.
	 * @throws TranslationException Expected exception.
	 */
	@Test(expected = TranslationException.class)
	public void testTranslateBadWeather1() throws TranslationException {
		Identifier parameter = new Identifier("SomeIdentifier");
		translator.translate(parameter);
	}

	/**
	 * Tests if an exception is thrown when the parameter contains
	 * invalid content.
	 * @throws TranslationException Expected exception.
	 */
	@Test(expected = TranslationException.class)
	public void testTranslateBadWeather2() throws TranslationException {
		Function parameter = new Function("notAmultipolygon");
		translator.translate(parameter);
	}

	/**
	 * Tests if an exception is thrown when the parameter doesn't contain
	 * enough content.
	 * @throws TranslationException Expected exception.
	 */
	@Test(expected = TranslationException.class)
	public void testTranslateBadWeather3() throws TranslationException {
		Function parameter = new Function("multipolygon");
		translator.translate(parameter);
	}

	/**
	 * Tests if an exception is thrown when the parameter function contains
	 * invalid content.
	 * @throws TranslationException Expected exception.
	 */
	@Test(expected = TranslationException.class)
	public void testTranslateBadWeather4() throws TranslationException {
		Function parameter = new Function("multipolygon",
				new Identifier("InvalidPolygon"));
		translator.translate(parameter);
	}

	/**
	 * Tests if the translate function creates the correct polygon.
	 * @throws TranslationException Unexpected exception.
	 */
	@Test
	public void testTranslateGoodWeather() throws TranslationException {
		Function parameter = new Function("multipolygon",
				new Identifier("MULTIPOLYGON (((0 0, 0 10, 10 10, 0 0)))"));
		MultiPolygon result = translator.translate(parameter);
		assertEquals(result.getCoordinate(), new Coordinate(0, 0));
	}

	/**
	 * Tests if the translatesTo function returns the proper class.
	 */
	@Test
	public void testTranslatesTo() {
		assertEquals(translator.translatesTo(), MultiPolygon.class);
	}
}
