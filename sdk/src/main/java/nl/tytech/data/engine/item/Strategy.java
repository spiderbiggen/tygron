/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.Item;
import nl.tytech.util.StringUtils;

/**
 * Strategy
 * <p>
 * This class keeps track of the Strategy
 * </p>
 * @version $Revision: 1.1 $ <br>
 * @author Maxim Knepfle
 */
public class Strategy extends Item {

    static final long serialVersionUID = 13465L;

    @XMLValue
    private String name = "No Name";

    @XMLValue
    private String description = StringUtils.EMPTY;

    @XMLValue
    private boolean active;

    @XMLValue
    private boolean available = true;

    @XMLValue
    private String motivation;

    /**
     * @return the description
     */
    @Override
    public final String getDescription() {
        return description;
    }

    /**
     * @return the motivation
     */
    public final String getMotivation() {

        return motivation;
    }

    /**
     * @return the name
     */
    public final String getName() {

        return name;
    }

    /**
     * @return the active
     */
    public final boolean isActive() {

        return active;
    }

    /**
     * @return the available
     */
    public final boolean isAvailable() {

        return available;
    }

    /**
     * @param active the active to set
     */
    public final void setActive(boolean active) {

        this.active = active;
    }

    /**
     * @param available the available to set
     */
    public final void setAvailable(boolean available) {

        this.available = available;
    }

    /**
     * @param description the description to set
     */
    public final void setDescription(String description) {

        this.description = description;
    }

    /**
     * @param motivation the motivation to set
     */
    public final void setMotivation(String motivation) {

        this.motivation = motivation;
    }

    @Override
    public String toString() {

        return getName();
    }

}
