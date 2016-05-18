package tygronenv.translators;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.util.LinkedList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import eis.eis2java.exception.TranslationException;
import nl.tytech.data.engine.item.Indicator;
import nl.tytech.data.engine.item.Stakeholder;
import tygronenv.translators.J2Stakeholder;

/**
 * Test class for the J2Stakeholder translator
 * @author Haoming
 *
 */
public class J2StakeholderTest {
    private J2Stakeholder translator;
    private Stakeholder stakeholder;
    private Indicator indicator;
    
    /**
     * Initialization method called before every test.
     */
    @Before
    public void init() {
        translator = new J2Stakeholder();
        
        stakeholder = mock(Stakeholder.class);
        
        List<Indicator> list = new LinkedList<Indicator>();
        
        when(stakeholder.getMyIndicators()).thenReturn(list);
        
    }
    
    /**
     * Test method which verifies that the mocked object calls the getMyIndicators method.
     * The other methods cannot be verified, since they are final methods.
     * That does not work with mockito.
     * @throws TranslationException thrown if translating fails.
     */
    @Test
    public void testVerifyGetMyIndicators() throws TranslationException {
        translator.translate(stakeholder);
        verify(stakeholder).getMyIndicators();
        
    }

}
