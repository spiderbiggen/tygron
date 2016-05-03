/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.net;

import nl.tytech.core.event.Event;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.data.core.item.Item;

/**
 * Lord
 * <p>
 * Object implementing Lord interface must provide access to the other controller lists.
 * <p>
 *
 *
 * @author Maxim Knepfle
 */
public interface Lord {

    /**
     * Get a specific game list
     *
     * @param type list type
     * @return The item list
     */
    public <I extends Item> ItemMap<I> getMap(final MapLink type);

    public Network.SessionType getSessionType();

    public long getSimTimeMillis();

    public boolean isServerSide();

    public boolean isShutdown();

    public boolean isTestRun();

    /**
     * Checks if data in event has correct links (only server side)
     * @param event
     * @return null when correct, otherwise erro message
     */
    public String validateEventItems(Event event);

}
