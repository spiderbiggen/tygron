/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.event;

/**
 * @author Jeroen Warmerdam
 * @Specialism
 */
public interface EventInterceptor {

    public boolean interceptEvent(Event event);
}
