/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.event.EventValidationUtils;
import nl.tytech.core.item.annotations.DoNotSaveToInit;
import nl.tytech.core.item.annotations.EventList;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.CodedEvent;
import nl.tytech.data.core.item.Item;
import nl.tytech.util.ObjectUtils;

/**
 * ClientEventPackage
 * <p>
 * Wrapper class that allows the server to fire client events on all client machines by wrapping the event in an item.
 * </p>
 * @author Maxim Knepfle
 */
public class ClientEventPackage extends Item {

    /**
     *
     */
    private static final long serialVersionUID = 6960182092368840006L;

    @DoNotSaveToInit
    @XMLValue
    private boolean active = false;

    @EventList(serverSide = false)
    @XMLValue
    @ListOfClass(CodedEvent.class)
    private ArrayList<CodedEvent> clientEvents = new ArrayList<CodedEvent>();

    @XMLValue
    private Long fireDate = null;

    public ClientEventPackage() {

    }

    public ClientEventPackage(List<CodedEvent> clientEvents, Long fireDate) {
        this.clientEvents = ObjectUtils.toArrayList(clientEvents);
        this.fireDate = fireDate;

    }

    public List<CodedEvent> getClientEvents() {
        return clientEvents;
    }

    public Long getFireDate() {
        return fireDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "" + this.getID();
    }

    @Override
    public String validated(boolean startNewSession) {
        return EventValidationUtils.validateCodedEvents(this, clientEvents, false);
    }
}
