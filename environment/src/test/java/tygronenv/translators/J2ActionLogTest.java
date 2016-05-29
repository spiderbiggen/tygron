package tygronenv.translators;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import eis.eis2java.exception.TranslationException;
import nl.tytech.core.client.event.EventManager;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.data.engine.item.ActionLog;
import nl.tytech.data.engine.item.Indicator;

public class J2ActionLogTest {

    private J2ActionLog translator;
    private ActionLog actionLog;
    private Indicator indicator;
    
    @Before
    public void init() {
        translator = new J2ActionLog();
        actionLog = mock(ActionLog.class);
        indicator = mock(Indicator.class);
        ItemMap<Indicator> map = mock(ItemMap.class);
        LinkedList<Indicator> list = new LinkedList<Indicator>();
        list.add(indicator);
        when(map.values()).thenReturn(list);
        EventManager man = mock(EventManager.class);
        when(EventManager.<Indicator>getItemMap(MapLink.INDICATORS)).thenReturn(map);
    }
    
    @Test
    public void testTranslate() throws TranslationException {
        translator.translate(actionLog);
        verify(actionLog).getAction();
        verify(actionLog).getIncrease(indicator);
        verify(actionLog).getStakeholder();
        verify(actionLog).getID();
    }
}
