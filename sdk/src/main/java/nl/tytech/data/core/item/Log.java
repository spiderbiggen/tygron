/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.core.item;

import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.util.StringUtils;

/**
 * LogEvent
 * <p>
 * This class keeps track of a log event
 * <p>
 *
 *
 * @author Maxim Knepfle
 */
public class Log extends Item {

    public enum Owner {
        SYSTEM("System"), FACILITATOR("Facilitator"), WORLD("World");

        private String enumDescription;

        private Owner(String enumDescription) {

            this.enumDescription = enumDescription;
        }

        public String getDescription() {

            return this.enumDescription;
        }
    };

    public enum Type {
        ERROR, UPDATE, EVENT
    }

    /**
     *
     */
    private static final long serialVersionUID = -3372065043390847492L;

    @XMLValue
    private String owner = "No Owner";

    @XMLValue
    private Type type;

    @XMLValue
    private String location = null;

    @XMLValue
    private String value = "No Value";

    @XMLValue
    private String title = "No Title";

    @XMLValue
    private Long sessionDate;

    @XMLValue
    private Long realDate;

    public Log() {

    }

    public Log(final String argOwner, final Enum<?> argLocation, final Type argType, final String argValue, final Long argGameDate,
            final Long argRealDate) {

        // set data
        owner = argOwner;
        type = argType;
        if (argLocation != null) {
            location = argLocation.toString();
        }
        value = argValue;
        sessionDate = argGameDate;
        realDate = argRealDate;
    }

    @Override
    public final String getDescription() {
        return owner + ": " + value;
    }

    /**
     * @return the location
     */
    public final <T extends Enum<T>> T getLocation(Class<T> enumerator) {
        if (this.location != null) {
            return Enum.valueOf(enumerator, this.location);
        }
        // return enumerator.getEnumConstants()[0];
        return null;
    }

    public final String getOwner() {
        return owner;
    }

    /**
     * @return the realDate
     */
    public final Long getRealDate() {
        return realDate;
    }

    public final Long getSessionDate() {
        return sessionDate;
    }

    public final String getSessionDateString() {
        return StringUtils.dateToHumanString(sessionDate, true);
    }

    /**
     * @return the title
     */
    public final String getTitle() {
        return title;
    }

    public final Type getType() {
        return type;
    }

    public final String getValue() {
        return value;
    }

    /**
     * @param title the title to set
     */
    public final void setTitle(String title) {
        this.title = title;
    }

    @Override
    public final String toString() {
        // FIXME: this is a really, really bad idea (used in tables as the first entry)
        return String.format("%tF", sessionDate);
    }

}
