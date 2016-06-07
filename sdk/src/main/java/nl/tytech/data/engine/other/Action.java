/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.other;

import nl.tytech.core.net.serializable.MapLink;

/**
 * Action
 * <p>
 * A player can select a ActionMenu to build, then he can select an Action.
 * <p>
 *
 *
 * @author Maxim Knepfle
 */
public interface Action {

    public static final String GUI_IMAGES_ACTIONS = "Gui/Images/Actions/";
    public static final String DEFAULT_IMAGE = "empty.png";

    /**
     * Return the construction time in months
     * @return
     */
    public double getConstructionTimeInMonths();

    /**
     * Get the basic description for this action.
     * @return
     */
    public String getDescription();

    /**
     * Return action ID or Item.NONE when it has none.
     * @return
     */
    public Integer getID();

    /**
     * The name of the image for this action.
     * @return
     */
    public String getImageLocation();

    /**
     * The MapLink of this action.
     * @return
     */
    public MapLink getMapLink();

    /**
     * Get my name
     * @return
     */
    public String getName();

    /**
     * When true this action is available for this stakeholder.
     * @return
     */
    public boolean isBuildable();

    /**
     * Is the location of the action fixed on a certain location (e.g. a measure) or is the location defined by the user (e.g. a new house).
     * @return
     */
    public boolean isFixedLocation();

}
