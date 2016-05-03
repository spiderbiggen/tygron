/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.event;

import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.net.Lord;
import nl.tytech.core.net.serializable.ItemID;
import nl.tytech.data.core.item.CodedEvent;
import nl.tytech.data.core.item.Item;
import nl.tytech.util.StringUtils;
import nl.tytech.util.logger.TLogger;

/**
 * ItemValidationUtils contains a number of methods used in various validation tasks in the Items.
 *
 * @author Marijn van Zanten & Maxim Knepfle
 */
public class EventValidationUtils {

    public static List<Object> convertCodedParams(CodedEvent codedEvent) {

        List<Object> event = codedEvent.getParameters();
        // Put the rest of the contents into a list
        List<Object> contents = new ArrayList<Object>();
        for (int i = 1; i < event.size(); i++) {
            Object par = event.get(i);
            // filter out itemID's
            if (par instanceof ItemID) {
                par = ((ItemID) par).getID();
            }
            contents.add(par);
        }
        return contents;
    }

    public static Event convertEvent(CodedEvent codedEvent) {
        if (codedEvent.getParameters().size() == 0) {
            return new Event();
        }
        return new Event((EventTypeEnum) codedEvent.getParameter(0), convertCodedParams(codedEvent));
    }

    /**
     *
     *
     *
     */
    public static String validateCodedEvents(Item source, List<CodedEvent> events, boolean serverSide) {

        String result = StringUtils.EMPTY;
        Lord lord = source.getLord();
        List<CodedEvent> removables = new ArrayList<CodedEvent>();

        for (CodedEvent codedEvent : events) {
            List<Object> event = codedEvent.getParameters();

            if (event.size() == 0) {
                TLogger.warning("Missing event data for CodedEvent in " + source.getClass().getSimpleName() + " " + source + " (ID: "
                        + source.getID() + "). Removing empty event to fix it!");
                removables.add(codedEvent);
                // result = false;
                continue;
            }
            // Put the rest of the contents into a list
            List<Object> contents = convertCodedParams(codedEvent);
            EventTypeEnum ete = null;
            try {
                ete = (EventTypeEnum) event.get(0);
            } catch (Exception e) {
                result += "\nThe event type should be an implementation of the EventTypeEnum interface, not "
                        + event.get(0).getClass().getSimpleName();
                continue;
            }

            if (ete.isServerSide() != serverSide) {
                if (serverSide) {
                    result += "\nExpected a server side event in: " + source.getClass().getSimpleName() + " " + source + " (ID: "
                            + source.getID() + "), but " + event.get(0).getClass().getSimpleName() + " is a client side event.";
                } else {
                    result += "\nExpected a client side event in: " + source.getClass().getSimpleName() + " " + source + " (ID: "
                            + source.getID() + "), but " + event.get(0).getClass().getSimpleName() + " is a server side event.";
                }
                continue;
            }

            Event dummyEvent = new Event(ete, contents);
            if (!validateEvent(dummyEvent)) {
                result += "\nFailing event is located in: " + source.getClass().getSimpleName() + " " + source + " (ID: " + source.getID()
                        + ").";

            } else if (lord != null) {
                String eventResult = lord.validateEventItems(dummyEvent);
                if (StringUtils.containsData(eventResult)) {
                    TLogger.warning(eventResult + "\nFailing event is located in: " + source.getClass().getSimpleName() + ": " + source
                            + " (ID: " + source.getID() + "). Removing event to fix this!");
                    removables.add(codedEvent);
                    continue;
                }
            }
        }

        for (CodedEvent removable : removables) {
            events.remove(removable);
        }
        return result;
    }

    /**
     * Validate events. An event is valid when it has the same classes in its contents as defined in EventTypeEnum.
     *
     * @param event
     * @return
     */
    public static boolean validateEvent(Event event) {
        return validateEvent(event.getType(), event.getContents());
    }

    public static boolean validateEvent(EventTypeEnum type, Object[] params) {

        List<Class<?>> classes = type.getClasses();
        if (classes == null) {
            TLogger.severe("Event of type " + type.getClass().getSimpleName() + "." + type + " is missing classes definition.");
            return false;
        }

        if (classes.size() == 0 && params.length == 0) {
            return true;
        }

        if (params.length != classes.size()) {
            String contentClasses = "( ";
            boolean first = true;

            for (Class<?> classz : classes) {
                if (classz != null) {
                    if (!first) {
                        contentClasses += ", ";
                    } else {
                        first = false;
                    }
                    contentClasses += classz.getSimpleName();
                }
            }
            contentClasses += " )";

            String[] data = new String[params.length];
            for (int i = 0; i < data.length; i++) {
                Object content = params[i];
                if (content != null) {
                    Class<?> classz = content.getClass();
                    data[i] = classz.getSimpleName();
                } else {
                    data[i] = "NULL";
                }
            }
            String sourceClasses = "( " + StringUtils.implode(data) + " )";
            TLogger.severe("Event of type " + type.getClass().getSimpleName() + "." + type + " has incorrect parameters, required are: "
                    + contentClasses + ". Present are: " + sourceClasses);
            return false;
        }

        for (int i = 0; i < params.length; i++) {
            Object content = params[i];
            Class<?> classz = classes.get(i);
            if (content != null && !classz.isAssignableFrom(content.getClass())) {

                /**
                 * Auto convert float to double
                 */
                if (content instanceof Float && classes.get(i).equals(Double.class)) {
                    content = (double) ((Float) content).floatValue();
                } else {
                    TLogger.severe("Parameter " + i + " of event " + type.getClass().getSimpleName() + "." + type + " is of class "
                            + content.getClass().getSimpleName() + ". Class " + classes.get(i).getSimpleName() + " is expected.");
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Validate a list of events
     *
     * @param events
     * @return ValidationType
     */

    public static boolean validateEvents(List<Event> events) {
        boolean result = true;
        for (Event event : events) {
            if (!validateEvent(event)) {
                result = false;
            }
        }
        return result;
    }
}
