/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.event;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.net.serializable.ItemID;
import nl.tytech.util.StringUtils;

/**
 * Event
 * <p>
 * Basic event object. It has a source, priority (used by server to facilitate the important clients) and contents. The contents can be one
 * or multiple objects. The event takes a EventType to identify itself.
 * <p>
 *
 *
 * @author Jeroen Warmerdam & Maxim Knepfle
 */
public class Event implements Serializable {

    /**
     * Enum defining the type of the event. All used enums should implement this.
     */
    public interface EventTypeEnum {

        /**
         * When true this event can be placed inside CodedEvents in XML files before the game is started. NOTE: when true this Enum must be
         * inside the Item Namespace!
         * @return
         */
        public boolean canBePredefined();

        /**
         * Returns the list of classes that the event expects (used for event content checking).
         *
         * @return List<Class<?>>
         */
        public List<Class<?>> getClasses();

        /**
         * When not NULL we expect this class type back
         * @return
         */

        public Class<?> getResponseClass();

        /**
         * When true this event must be fired server side, false means it's an client event, that must be fired clientside.
         * @return
         */
        public boolean isServerSide();

        /**
         * Enum name
         * @return
         */
        public String name();
    }

    /**
     * These events can cause the game to trigger a test run when running in gameMode editor.
     * @author Maxim
     *
     */
    public interface SessionEventTypeEnum extends EventTypeEnum {

        /**
         * When true this event triggers a test run in editor mode.
         * @return
         */
        public boolean triggerTestRun();

    }

    /**
     * When this interface is implemented the event always starts with his personal stakeholder ID as first content value! This values can
     * be Overridden.
     * @author Maxim
     *
     */
    public interface StartWithMyStakeholderEvent {

    }

    /**
     * Serial
     */
    private static final long serialVersionUID = -2306024519680983784L;

    public final static String getDescription(EventTypeEnum event) {

        String result = "Event: " + event.getClass().getSimpleName() + "/" + event.toString() + " with parameters: ";
        for (int i = 0; i < event.getClasses().size(); i++) {
            Class<?> classz = event.getClasses().get(i);
            result += classz.getSimpleName() + (i < event.getClasses().size() - 1 ? ", " : StringUtils.EMPTY);
        }
        return result;
    }

    /**
     * List carrying the contents of the event.
     */
    private List<Object> contents;

    private Object requestLoad = null;

    private boolean requestEvent = false;

    /**
     * The event type. To identify the event. Can be any kind of enumeration.
     */
    private EventTypeEnum type = null;

    public Event() {
    }

    public Event(EventTypeEnum type, final List<Object> contents) {
        this.contents = contents;
        this.type = type;
    }

    /**
     * Construct an event.
     *
     * @param priority Priority of the event. Default is 1, can range from 0-10.
     * @param type Type of event
     * @param contents Contents can be multiple objects.
     */
    public Event(EventTypeEnum type, final Object... contentsArgs) {

        this.contents = new ArrayList<Object>();
        for (Object newContent : contentsArgs) {
            if (newContent != null && newContent.getClass().equals(ItemID.class)) {
                ItemID iid = (ItemID) newContent;
                Integer value = iid.getID();
                contents.add(value);
            } else {
                contents.add(newContent);
            }
        }
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Event other = (Event) obj;
        if (contents == null) {
            if (other.contents != null) {
                return false;
            }
        } else if (!contents.equals(other.contents)) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        return true;
    }

    /**
     * @return the first entry
     */
    public <T> T getContent() {
        return this.<T> getContent(0);
    }

    /**
     * Get the content of the event
     *
     * @param <T>
     * @param i Index
     * @return Object at i-th place
     */
    @SuppressWarnings("unchecked")
    public <T> T getContent(int i) {

        if (i >= contents.size()) {
            // no content
            return null;
        }

        // return the i-th entry
        return (T) contents.get(i);
    }

    /**
     * @return the content
     */
    public Object[] getContents() {
        return contents.toArray();
    }

    /**
     * @return the payLoad
     */
    @SuppressWarnings("unchecked")
    public final <T> T getRequestLoad() {
        return (T) this.requestLoad;
    }

    /**
     * @return the type
     */
    public EventTypeEnum getType() {
        return this.type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((contents == null) ? 0 : contents.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    /**
     * When this is a request event, DO NOT SWITCH THREAD
     */
    public boolean isRequestEvent() {
        return requestEvent;
    }

    /**
     * @param storage
     * @param b
     */
    public void setContent(Object... contentsArgs) {

        contents.clear();
        for (Object newContent : contentsArgs) {
            // Strips ItemID from the contents and replaces it with the Integer ID
            if (newContent != null && newContent instanceof ItemID) {
                ItemID iid = (ItemID) newContent;
                Integer value = iid.getID();
                contents.add(value);
            } else {
                contents.add(newContent);
            }
        }
    }

    public void setRequestEvent(boolean requestEvent) {
        this.requestEvent = requestEvent;
    }

    /**
     * @param payLoad the payLoad to set
     */
    public final void setRequestLoad(Object payLoad) {
        this.requestLoad = payLoad;
    }

    @Override
    public String toString() {

        StringBuilder string = new StringBuilder("Event: " + type.getClass().getSimpleName() + "." + type + " with contents: ");
        for (Object content : contents) {
            string.append(content + ", ");
        }
        return string.toString();
    }
}
