package contextvh.translators;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import eis.eis2java.exception.TranslationException;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import tygronenv.configuration.ParamEnum;

/**
 * Test class for the ParamEnum to Java translator.
 * @author Max
 */
public class ParamEnum2JTest {

	private final ParamEnum2J translator = new ParamEnum2J();

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
		ParamEnum paramEnum = translator.translate(parameter);
		assertEquals(paramEnum, ParamEnum.STAKEHOLDERS);
	}

	/**
	 * Tests if the translatesTo function returns the proper class.
	 */
	@Test
	public void testTranslatesTo() {
		assertEquals(translator.translatesTo(), ParamEnum.class);
	}
}
