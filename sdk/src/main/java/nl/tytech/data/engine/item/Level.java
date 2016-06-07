/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.event.EventValidationUtils;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.DoNotSaveToInit;
import nl.tytech.core.item.annotations.EventList;
import nl.tytech.core.item.annotations.Html;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.CodedEvent;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.item.UniqueNamedItem;
import nl.tytech.util.StringUtils;

/**
 *
 * Level activates certain functionality, e.g. more zones to work in. New measures.
 *
 * @author Maxim Knepfle
 *
 */
public class Level extends UniqueNamedItem {

    private static final long serialVersionUID = -1507601543672853064L;

    @Html
    @XMLValue
    private String description = StringUtils.EMPTY;

    @XMLValue
    private int orderIndex = -1;

    @EventList(serverSide = true)
    @XMLValue
    @ListOfClass(CodedEvent.class)
    private ArrayList<CodedEvent> levelServerEvents = new ArrayList<>();

    @DoNotSaveToInit
    @XMLValue
    private boolean disabled = false;

    public Level() {

    }

    public CodedEvent addEvent(EventTypeEnum type, Object... objects) {
        CodedEvent event = CodedEvent.createUniqueIDEvent(levelServerEvents, type, objects);
        levelServerEvents.add(event);
        return event;
    }

    @Override
    public int compareTo(Item other) {
        if (other == null) {
            return 0;
        }

        if (!(other instanceof Level)) {
            return super.compareTo(other);
        }

        Level otherLevel = (Level) other;
        return (getOrderIndex() < otherLevel.getOrderIndex() ? -1 : 1);
    }

    public CodedEvent getCodedEventForID(Integer subID) {
        for (CodedEvent codedEvent : levelServerEvents) {
            if (codedEvent.getID().equals(subID)) {
                return codedEvent;
            }
        }
        return null;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public List<CodedEvent> getServerEventList() {
        return levelServerEvents;
    }

    public boolean isActivated() {
        Setting setting = this.getItem(MapLink.SETTINGS, Setting.Type.ACTIVE_LEVEL);
        return setting.getIntegerValue().equals(this.getID());
    }

    public boolean isDisabled() {
        return disabled;
    }

    public boolean replaceEvent(boolean isServerSide, CodedEvent codedEvent) {
        List<CodedEvent> eventList = levelServerEvents;

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

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    @Override
    public String toString() {
        return (getOrderIndex() + 1) + ": " + getName();
    }

    @Override
    public String validated(boolean startNewSession) {
        this.description = StringUtils.removeHTMLTags(this.description);
        return EventValidationUtils.validateCodedEvents(this, levelServerEvents, true);

    }
}
