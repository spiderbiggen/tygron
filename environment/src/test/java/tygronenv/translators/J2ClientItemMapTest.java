package tygronenv.translators;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import eis.eis2java.exception.TranslationException;
import eis.iilang.Parameter;
import nl.tytech.core.structure.ClientItemMap;
import nl.tytech.data.core.item.Item;

/**
 * Test for the Java to ClientItemMap translator.
 * @author Max
 */
public class J2ClientItemMapTest {

	private final J2ClientItemMap translator = new J2ClientItemMap();

	/**
	 * Tests if a call with a parameter of length 0 returns an
	 * empty array.
	 * @throws TranslationException Unexpected exception.
	 */
	@Test
	public void testTranslateGoodWeather1() throws TranslationException {
		ClientItemMap<Item> map = new ClientItemMap<Item>();
		Parameter[] result = translator.translate(map);
		assertEquals(result.length, 0);
	}

	/**
	 * Tests if the translatesFrom function returns the proper class.
	 */
	@Test
	public void testTranslatesTo() {
		assertEquals(translator.translatesFrom(), ClientItemMap.class);
	}
}
