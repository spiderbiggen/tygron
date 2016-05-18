package tygronenv.translators;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import eis.eis2java.exception.TranslationException;
import nl.tytech.data.engine.item.Indicator;
import nl.tytech.data.engine.serializable.MapType;

/**
 * Test for the J2Indicator class.
 * 
 * @author Stefan de Vringer
 *
 */
public class J2IndicatorTest {
	J2Indicator translator = new J2Indicator();
	Indicator indicator;
	
	/**
	 * Initialise before every test.
	 */
	@Before
	public void init() {
		indicator = mock(Indicator.class);
	}
	
	/**
	 * Test whether the translation method asks for the correct properties of the indicator.
	 * @throws TranslationException thrown if the translate method fails.
	 */
	@Test
	public void tranlatorTest1() throws TranslationException {
		translator.translate(indicator);
		verify(indicator, times(1)).getExactNumberValue(MapType.MAQUETTE);
		verify(indicator, times(1)).getTarget();
	}
}
