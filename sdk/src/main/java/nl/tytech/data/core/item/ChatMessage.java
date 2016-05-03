/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.core.item;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.util.StringUtils;

/**
 *
 * Messages between sessions in domains.
 *
 * @author Maxim Knepfle
 *
 */
public class ChatMessage extends Item {

    public enum Type {
        SYSTEM, MESSAGE
    }

    /**
     *
     */
    private static final long serialVersionUID = 6153041251983174279L;

    @XMLValue
    private String message = StringUtils.EMPTY;

    @XMLValue
    private String fullName = StringUtils.EMPTY;

    @XMLValue
    private String userName = StringUtils.EMPTY;

    @XMLValue
    private Type type = null;

    @XMLValue
    private long time = Item.NONE;

    public ChatMessage() {

    }

    public ChatMessage(String userName, String fullName, String message, Type type, long time) {
        this.userName = userName;
        this.fullName = fullName;
        this.message = message;
        this.type = type;
        this.time = time;
    }

    public String getDate() {
        Calendar calendar = Calendar.getInstance();
        if (time > 0) {
            calendar.setTimeInMillis(time);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss ");
        return sdf.format(calendar.getTime());
    }

    public String getFullName() {
        return fullName;
    }

    public String getMessage() {
        return message;
    }

    public String getText() {
        return type == ChatMessage.Type.MESSAGE ? getFullName() + ": " + StringUtils.capitalizeFirstLetter(message) : message;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public String toString() {
        return getText();
    }
}
