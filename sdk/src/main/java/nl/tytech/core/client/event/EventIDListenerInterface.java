/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.client.event;

import java.util.EventListener;
import nl.tytech.core.event.Event;

/**
 * @author Jeroen Warmerdam
 * @Specialism
 */
public interface EventIDListenerInterface extends EventListener {

    public void notifyEnumListener(Event event, Enum<?> enhum);

    public void notifyIDListener(Event event, Integer id);
}
