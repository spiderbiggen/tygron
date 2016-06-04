package contextvh.translators;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import eis.eis2java.exception.TranslationException;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import nl.tytech.data.engine.item.Stakeholder;

/**
 * Tests the stakeholder to java translator.
 * @author Max
 */
public class Stakeholder2JTest {

	private final Stakeholder2J translator = new Stakeholder2J();

	/**
	 * Tests if an exception is thrown when the parameter
	 * isn't the correct type.
	 * @throws TranslationException Expected exception.
	 */
	@Test(expected = TranslationException.class)
	public void testTranslateBadWeather1() throws TranslationException {
		Parameter parameter = new Numeral(0);
		translator.translate(parameter);
	}

	/**
	 * Tests if an exception is thrown when no map link
	 * has been established.
	 * @throws TranslationException Expected exception.
	 */
	@Test(expected = NullPointerException.class)
	public void testTranslateBadWeather2() throws TranslationException {
		Parameter parameter = new Identifier("");
		translator.translate(parameter);
	}

	/**
	 * Test if the translatesTo function returns the proper class.
	 */
	@Test
	public void testTranslatesTo() {
		assertEquals(translator.translatesTo(), Stakeholder.class);
	}
}
