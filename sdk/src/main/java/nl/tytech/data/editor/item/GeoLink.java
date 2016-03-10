/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.editor.item;

import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.item.Stakeholder;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;
import nl.tytech.util.logger.TLogger;

/**
 * @author Jurrian Hartveldt
 */
public abstract class GeoLink extends Item {

    private static final long serialVersionUID = 3227809616030984800L;

    private final static int DEFAULT_PRIORITY = 50;

    public static final GeoLink getBestPriority(GeoLink one, GeoLink two) {
        if (one == null) {
            return two;
        } else if (two == null) {
            return one;
        } else {
            return two.getPriority() > one.getPriority() ? two : one;
        }
    }

    @XMLValue
    private int priority = DEFAULT_PRIORITY;

    public abstract TColor getColor();

    public abstract Stakeholder.Type getDefaultStakeholderType();

    public abstract String getName();

    public Integer getPriority() {
        return priority;
    }

    public abstract boolean isRoad();

    public abstract boolean isWater();

    public void setPriority(int priority) {
        if (priority < 0) {
            TLogger.warning("Warning: cannot set GeoLink priority with id '" + this.getID() + "' to value: " + priority
                    + ". Keeping previous value '" + this.priority + "' instead.");
            return;
        }
        this.priority = priority;
    }

    @Override
    public String toString() {
        return StringUtils.EMPTY + priority + ") " + getName();
    }
}
