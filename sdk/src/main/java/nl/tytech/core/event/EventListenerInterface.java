/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.event;

import java.util.EventListener;

/**
 * EventListenerInterface
 * <p>
 * Implement this to be notified of events.
 * <p>
 * 
 * 
 * @author Jeroen Warmerdam
 */
public interface EventListenerInterface extends EventListener {

    /**
     * Handle the updated event
     * 
     * @param event
     */
    public void notifyListener(Event event);
}
