/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import nl.tytech.core.event.EventValidationUtils;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.AssetDirectory;
import nl.tytech.core.item.annotations.EventList;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.CodedEvent;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.other.Action;
import nl.tytech.util.StringUtils;

/**
 * @author Jeroen Warmerdam
 */
public class EventBundle extends Item implements Action {

    private static final long serialVersionUID = -5779733422240767368L;

    @XMLValue
    private String description = StringUtils.EMPTY;

    @XMLValue
    @EventList(serverSide = true)
    @ListOfClass(CodedEvent.class)
    public ArrayList<CodedEvent> serverEvents = new ArrayList<>();

    @XMLValue
    @EventList(serverSide = false)
    @ListOfClass(CodedEvent.class)
    public ArrayList<CodedEvent> clientEvents = new ArrayList<>();

    @XMLValue
    private String name = StringUtils.EMPTY;

    @XMLValue
    @AssetDirectory(GUI_IMAGES_ACTIONS)
    private String imageName = DEFAULT_IMAGE;

    @ItemIDField("EVENT_BUNDLES")
    @XMLValue
    private ArrayList<Integer> compoundEventBundleIDs = new ArrayList<>();

    public EventBundle() {
        name = "New bundle";
    }

    public CodedEvent addCodedEvent(EventTypeEnum type, Object... objects) {
        boolean isServerSide = type.isServerSide();
        CodedEvent event = CodedEvent.createUniqueIDEvent(getCodedEvents(isServerSide), type, objects);
        getCodedEvents(isServerSide).add(event);
        return event;
    }

    public List<CodedEvent> getClientEvents() {
        if (compoundEventBundleIDs.size() > 0) {
            List<CodedEvent> events = new ArrayList<CodedEvent>();

            List<EventBundle> bundles = this.getItems(MapLink.EVENT_BUNDLES, compoundEventBundleIDs);
            for (EventBundle bundle : bundles) {
                events.addAll(bundle.getClientEvents());
            }

            events.addAll(clientEvents);
            return events;
        }
        return clientEvents;
    }

    public CodedEvent getCodedEventForID(boolean isServerSide, Integer codedEventID) {
        List<CodedEvent> events = getCodedEvents(isServerSide);
        for (CodedEvent codedEvent : events) {
            if (codedEvent.getID().equals(codedEventID)) {
                return codedEvent;
            }
        }
        return null;
    }

    public List<CodedEvent> getCodedEvents(boolean isServerSide) {
        return isServerSide ? serverEvents : clientEvents;
    }

    public List<Integer> getCompoundEventBundleIDs() {
        return compoundEventBundleIDs;
    }

    @Override
    public double getConstructionTimeInMonths() {
        return 0;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getImageLocation() {
        if (!StringUtils.containsData(imageName)) {
            return StringUtils.EMPTY;
        }
        return Action.GUI_IMAGES_ACTIONS + imageName;
    }

    public String getImageName() {
        return imageName;
    }

    @Override
    public MapLink getMapLink() {
        return MapLink.EVENT_BUNDLES;
    }

    @Override
    public String getName() {
        return name;
    }

    public List<CodedEvent> getServerEvents() {
        if (compoundEventBundleIDs.size() > 0) {
            List<CodedEvent> events = new ArrayList<CodedEvent>();

            List<EventBundle> bundles = this.getItems(MapLink.EVENT_BUNDLES, compoundEventBundleIDs);
            for (EventBundle bundle : bundles) {
                events.addAll(bundle.getServerEvents());
            }

            events.addAll(serverEvents);
            return events;
        }
        return serverEvents;
    }

    @Override
    public boolean isBuildable() {
        return true;
    }

    @Override
    public boolean isFixedLocation() {
        return false;
    }

    public CodedEvent removeCodedEvent(boolean isServerSide, Integer eventID) {
        CodedEvent codedEvent = null;
        Iterator<CodedEvent> eventIterator = getCodedEvents(isServerSide).iterator();
        while (eventIterator.hasNext()) {
            CodedEvent event = eventIterator.next();
            if (event.getID().equals(eventID)) {
                codedEvent = event;
                eventIterator.remove();
                break;
            }
        }
        return codedEvent;
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

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageName(String name) {
        this.imageName = name;

    }

    public void setName(String string) {
        this.name = string;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public String validated(boolean startNewSession) {
        return EventValidationUtils.validateCodedEvents(this, serverEvents, true)
                + EventValidationUtils.validateCodedEvents(this, clientEvents, false);

    }
}
