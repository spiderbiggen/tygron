package contextvh.translators;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.Test;

import eis.eis2java.exception.TranslationException;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import eis.iilang.ParameterList;

/**
 * Test class for the HashMap to Java translator.
 * @author Max
 */
public class HashMap2JTest {

	private final HashMap2J translator = new HashMap2J();

	/**
	 * Tests if an exception is thrown when a single Parameter is used.
	 * @throws TranslationException Expected exception.
	 */
	@Test(expected = TranslationException.class)
	public void testTranslateBadWeather() throws TranslationException {
		Identifier parameter = new Identifier("SomeIdentifier");
		translator.translate(parameter);
	}

	/**
	 * Tests if an exception is thrown when a ParameterList with other
	 * than ParameterList parameters is used.
	 * @throws TranslationException Expected exception.
	 */
	@Test(expected = TranslationException.class)
	public void testTranslateBadparam() throws TranslationException {
		Identifier id = new Identifier("SomeIdentifier");
		ParameterList parameters = new ParameterList(id);

		translator.translate(parameters);
	}

	/**
	 * Tests if the translate function properly translates a proper
	 * argument.
	 * @throws TranslationException Thrown when the argument could
	 * not be translated.
	 */
	@Test
	public void testTranslateGoodWeather() throws TranslationException {
		Identifier id = new Identifier("SomeIdentifier");
		Numeral value = new Numeral(0);
		ParameterList parameters = new ParameterList(
				new ParameterList(id, value));

		HashMap<Identifier, Parameter> expected =
				new HashMap<Identifier, Parameter>();
		expected.put(id, value);
		HashMap<Identifier, Parameter> res =
				translator.translate(parameters);
		assertEquals(res, expected);
	}

	/**
	 * Tests if the translatesTo function returns the proper class.
	 */
	@Test
	public void testTranslatesTo() {
		assertEquals(translator.translatesTo(), HashMap.class);
	}
}
