/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.core.item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.EventList;
import nl.tytech.core.item.annotations.XMLValue;

/**
 * Answer
 * <p>
 * This class keeps track of the Info
 * <p>
 *
 *
 * @author Maxim Knepfle
 */
public class Answer implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -3341584447107211864L;

    @XMLValue
    private boolean selected = false;

    @XMLValue
    private Integer id = Item.NONE;

    @XMLValue
    private String contents = "No Contents";

    @XMLValue
    @EventList(serverSide = true)
    private ArrayList<CodedEvent> events = new ArrayList<>();

    @XMLValue
    @EventList(serverSide = false)
    private ArrayList<CodedEvent> clientEvents = new ArrayList<>();

    public Answer() {

    }

    public Answer(final String contents) {

        this.contents = contents;
    }

    public CodedEvent addEvent(EventTypeEnum type, Object... objects) {
        boolean isServerSide = type.isServerSide();
        CodedEvent event = CodedEvent.createUniqueIDEvent(getCodedEvents(isServerSide), type, objects);
        getCodedEvents(isServerSide).add(event);
        return event;
    }

    public List<CodedEvent> getClientEvents() {
        return clientEvents;
    }

    public CodedEvent getCodedEventForID(boolean isServerSide, Integer id) {

        for (CodedEvent codedEvent : getCodedEvents(isServerSide)) {
            if (codedEvent.getID().equals(id)) {
                return codedEvent;
            }
        }
        return null;
    }

    public List<CodedEvent> getCodedEvents(boolean isServerSide) {
        if (isServerSide) {
            return events;
        } else {
            return clientEvents;
        }
    }

    public final String getContents() {
        return this.contents;
    }

    public List<CodedEvent> getEvents() {
        return events;
    }

    public final Integer getID() {
        return this.id;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean removeEvent(boolean isServerSide, Integer eventID) {
        CodedEvent codedEvent = getCodedEventForID(isServerSide, eventID);
        if (codedEvent == null) {
            return false;
        }
        return getCodedEvents(isServerSide).remove(codedEvent);
    }

    public boolean replaceEvent(boolean isServerSide, CodedEvent codedEvent) {
        List<CodedEvent> eventList = getCodedEvents(isServerSide);

        int i = 0;
        for (; i < eventList.size(); ++i) {
            if (codedEvent.getID().equals(eventList.get(i).getID())) {
                break;
            }
        }

        if (i >= eventList.size()) {
            return false;
        }

        eventList.set(i, codedEvent);
        return true;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public void setID(Integer id) {
        this.id = id;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return getContents();
    }
}
