package contextvh.translators;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import eis.eis2java.translation.Translator;
import org.junit.Before;
import org.junit.Test;

import eis.eis2java.exception.TranslationException;
import nl.tytech.data.engine.item.UpgradeType;
import nl.tytech.data.engine.serializable.UpgradePair;

/**
 * Test for the J2UpgradeType class.
 *
 * @author Marco Houtman
 *
 */
public class J2UpgradeTypeTest {
	/**
	 * Translator of the J2Indicator to test.
	 */
	private Translator translator = Translator.getInstance();
	/**
	 * The indicators to translate.
	 */
	private UpgradeType upgradeType;
	private UpgradePair upgradePair;
	private ArrayList<UpgradePair> a;
	private List<UpgradePair> pairs;
	/**
	 * Initialise before every test.
	 */
	@Before
	public void init() {
		translator.registerJava2ParameterTranslator(new J2UpgradeType());
		translator.registerJava2ParameterTranslator(new J2UpgradePair());
		upgradeType = mock(UpgradeType.class);
		upgradePair = mock(UpgradePair.class);
		pairs = new ArrayList<>();
	}

	/**
	 * Tests if the translator returns the source and target of an upgradePair.
	 *
	 * @throws TranslationException
	 *             thrown if translation fails.
	 */
	@Test
	public void tranlatorTest1() throws TranslationException {
		pairs.add(upgradePair);
		when(upgradeType.getPairs()).thenReturn(pairs);
		translator.translate2Parameter(upgradeType);
		verify(upgradePair, times(1)).getSourceFunctionID();
		verify(upgradePair, times(1)).getTargetFunctionID();
	}

	/**
	 * Tests if the translator will not try to look for a source and target of
	 * an upgradePair if the translator has an empty list of pairs.
	 *
	 * @throws TranslationException
	 *             if translation fails.
	 */
	@Test
	public void translatorTest2() throws TranslationException {
		translator.translate2Parameter(upgradeType);
		verify(upgradeType, times(1)).getPairs();
		verify(upgradePair, times(0)).getSourceFunctionID();
		verify(upgradePair, times(0)).getTargetFunctionID();
	}

	/**
	 * Tests if the translator returns the source and target of an upgradePair.
	 * @throws TranslationException thrown if translation fails.
	 */
	@Test
	public void tranlatorTest3() throws TranslationException {
		pairs.add(upgradePair);
		pairs.add(upgradePair);
		when(upgradeType.getPairs()).thenReturn(pairs);
		translator.translate2Parameter(upgradeType);
		verify(upgradePair, times(2)).getSourceFunctionID();
		verify(upgradePair, times(2)).getTargetFunctionID();
	}

}
