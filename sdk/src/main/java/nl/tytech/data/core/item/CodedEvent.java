/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.core.item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.ItemID;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.structure.ItemNamespace;
import nl.tytech.util.ObjectUtils;
import nl.tytech.util.StringUtils;
import nl.tytech.util.logger.TLogger;

/**
 * CodedEvent
 * <p>
 * Contains the event type and the required parameters.
 * <p>
 *
 *
 * @author Maxim Knepfle
 */
public class CodedEvent implements Serializable {

    private static final int TYPE_PARAMETER_INDEX = 0;

    /**
     *
     */
    private static final long serialVersionUID = 7896165758278561410L;

    public static final CodedEvent createUniqueIDEvent(List<CodedEvent> events, EventTypeEnum type, Object[] objects) {
        int uniqueID = 0;
        // Find next unique id for this list
        for (CodedEvent event : events) {
            if (event.getID().intValue() >= uniqueID) {
                uniqueID = event.getID() + 1;
            }
        }
        CodedEvent event = new CodedEvent(type, objects);
        event.setID(uniqueID);
        return event;
    }

    @XMLValue
    private ArrayList<Object> parameters = new ArrayList<>();

    @XMLValue
    private Integer id = Item.NONE;

    public CodedEvent() {

    }

    public CodedEvent(CodedEvent other) {
        this.id = other.getID();
        this.parameters = new ArrayList<Object>(other.parameters);
    }

    public CodedEvent(EventTypeEnum type, final Object... contentsArgs) {
        parameters.add(type);
        if (contentsArgs != null && contentsArgs.length > 0) {
            parameters.addAll(Arrays.asList(contentsArgs));
        }
    }

    public CodedEvent(List<Object> event) {
        parameters = new ArrayList<Object>(event);
    }

    public CodedEvent(Object[] parameters) {
        // wrap to ArrayList, instead of not item support Arrays.ArrayList
        this.parameters = new ArrayList<Object>(Arrays.asList(parameters));
    }

    public CodedEvent(String eventType, String eventTypeValue) {

        Class<?> classz = ItemNamespace.getClass(eventType);
        EventTypeEnum eventTypeEnum = null;

        if (classz != null) {
            for (Object enumerator : classz.getEnumConstants()) {
                if (enumerator.toString().equals(eventTypeValue)) {

                    if (enumerator instanceof EventTypeEnum) {
                        eventTypeEnum = (EventTypeEnum) enumerator;
                    }
                }
            }
        }

        if (eventTypeEnum == null) {
            TLogger.severe("Failed to create event object from " + eventType + " " + eventTypeValue);
            return;
        }
        parameters.add(eventTypeEnum);
        List<Class<?>> eventClasses = eventTypeEnum.getClasses();
        EventIDField idFields = null;
        try {
            idFields = eventTypeEnum.getClass().getField(((Enum<?>) eventTypeEnum).name()).getAnnotation(EventIDField.class);
        } catch (Exception e) {
            TLogger.exception(e);
        }

        for (int i = 0; i < eventClasses.size(); i++) {
            Class<?> eventClass = eventClasses.get(i);
            Object object = ObjectUtils.constructObject(eventClass, new Class<?>[0]);

            boolean itemIDadd = false;
            if (idFields != null) {
                for (int j = 0; j < idFields.params().length; j++) {
                    if (idFields.params()[j] == i) {
                        String eventname = idFields.links()[j];

                        MapLink controlType = MapLink.valueOf(eventname);
                        if (object instanceof Integer) {
                            ItemID itemID = new ItemID(Item.NONE, controlType);
                            parameters.add(itemID);
                            itemIDadd = true;
                            break;
                        }
                        TLogger.severe("Warning cannot add " + controlType + " for object: " + object + " must be an Integer!");
                    }
                }
            }
            if (!itemIDadd) {
                parameters.add(object);
            }
        }
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
        CodedEvent other = (CodedEvent) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (parameters == null) {
            if (other.parameters != null) {
                return false;
            }
        } else if (!parameters.equals(other.parameters)) {
            return false;
        }
        return true;
    }

    public Integer getID() {
        return this.id;
    }

    @SuppressWarnings("unchecked")
    public <T> T getParameter(int i) {
        return (T) parameters.get(i);
    }

    public ArrayList<Object> getParameters() {
        return parameters;
    }

    public EventTypeEnum getType() {
        if (parameters.size() > 0) {
            return (EventTypeEnum) parameters.get(TYPE_PARAMETER_INDEX);
        }
        return null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
        return result;
    }

    public void set(CodedEvent event) {
        this.parameters = event.getParameters();
        this.id = event.getID();
    }

    public void setID(Integer id) {
        this.id = id;

    }

    public void setType(EventTypeEnum type) {

        // XXX (Frank) Clean up this class!
        if (this.parameters.size() > 0 && this.parameters.get(0) != null) {
            Object obj = this.parameters.get(0);
            if (obj.toString().equals(type.toString())) {
                this.parameters.set(0, type);
                return;
            }

        }

        if (parameters.size() > 0) {
            parameters.clear();
        }
        parameters.add(type);
        // Prefill with defaults

        EventIDField eventIDField = ObjectUtils.getEnumAnnotation((Enum<?>) type, EventIDField.class);
        List<Integer> paramsList = new ArrayList<Integer>();
        if (eventIDField != null) {
            int[] params = eventIDField.params();

            for (int param : params) {
                paramsList.add(param);
            }
        }

        for (int i = 0; i < type.getClasses().size(); i++) {
            Class<?> parameterClass = type.getClasses().get(i);
            if (parameterClass.equals(Integer.class)) {
                int paramIndex = paramsList.indexOf(i);
                if (paramIndex >= 0) {
                    String linkString = eventIDField.links()[paramIndex];
                    MapLink link = MapLink.valueOf(linkString);
                    parameters.add(new ItemID(Item.NONE, link));
                } else {
                    parameters.add(new Integer(-1));
                }
            } else if (parameterClass.equals(Boolean.class)) {
                parameters.add(true);
            } else if (parameterClass.equals(String.class)) {
                parameters.add(StringUtils.EMPTY);
            } else if (parameterClass.equals(Float.class)) {
                parameters.add(new Float(0));
            } else if (parameterClass.isEnum()) {
                Enum<?> constant = ((Class<Enum<?>>) parameterClass).getEnumConstants()[0];
                parameters.add(constant);
            } else {

                parameters.add(null);
            }
        }

    }

    @Override
    public String toString() {
        String content = CodedEvent.class.getSimpleName() + StringUtils.WHITESPACE;

        EventTypeEnum type = getType();
        if (type != null) {
            content += type.toString();
        }

        content += " { ";
        for (int i = 1; i < parameters.size(); i++) {
            content += " | " + parameters.get(i);
        }
        content += " } ";
        return content;

    }
}
