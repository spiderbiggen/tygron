package contextvh.translators;

import static org.junit.Assert.assertEquals;

import contextvh.configuration.ContextParamEnum;
import org.junit.Test;

import eis.eis2java.exception.TranslationException;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;

/**
 * Test class for the ContextParamEnum to Java translator.
 * @author Max
 */
public class ContextParamEnum2JTest {

	private final ContextParamEnum2J translator = new ContextParamEnum2J();

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
	 * Tests if an exception is thrown when the parameter
	 * contains invalid content.
	 * @throws TranslationException Expected exception.
	 */
	@Test(expected = TranslationException.class)
	public void testTranslateBadWeather2() throws TranslationException {
		Parameter parameter = new Identifier("NoParam");
		translator.translate(parameter);
	}

	/**
	 * Tests if the translator returns the correct parameter for
	 * the correct input.
	 * @throws TranslationException Unexpected exception.
	 */
	@Test
	public void testTranslateGoodWeather() throws TranslationException {
		Parameter parameter = new Identifier("stakeholders");
		ContextParamEnum paramEnum = translator.translate(parameter);
		assertEquals(ContextParamEnum.STAKEHOLDERS, paramEnum);
	}

	/**
	 * Tests if the translatesTo function returns the proper class.
	 */
	@Test
	public void testTranslatesTo() {
		assertEquals(ContextParamEnum.class, translator.translatesTo());
	}
}
