/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.client.event;

import nl.tytech.core.event.Event;

/**
 * SlotEvent
 * <p>
 * SlotEvent allow events to be fired for specific sessions. This is only usefull when running multiple sessions on one system. E.g. the
 * facilitator.
 * <p>
 *
 *
 * @author William van Velzen
 */
public class SlotEvent extends Event {

    private static final long serialVersionUID = -1934512728855774874L;

    /**
     * When true this event is related to the given connectionID.
     * @param event
     * @param filterConnectionID
     * @return
     */
    public static boolean filter(Event event, Integer filterConnectionID) {

        if (event instanceof SlotEvent) {
            SlotEvent sevent = (SlotEvent) event;
            return sevent.getConnectionID().equals(filterConnectionID);
        }
        return false;
    }

    /**
     * Extra var indicating the connection ID with the server. Note: not to be confused with the server's game ID.
     */
    private final Integer connectionID;

    public SlotEvent(Integer connectionID, EventTypeEnum type, final Object... contentsArgs) {
        super(type, contentsArgs);
        this.connectionID = connectionID;
    }

    public Integer getConnectionID() {
        return this.connectionID;
    }
}
