/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;

/**
 * @author Frank Baars
 */
public class GlobalPanel extends Panel {

    private static final long serialVersionUID = 6150792888335566809L;

    private final static double DEFAULT_GLOBAL_WIDTH = 250;
    private final static double DEFAULT_GLOBAL_HEIGHT = 468;

    @XMLValue
    @ItemIDField("GLOBALS")
    private ArrayList<Integer> globalIDs = new ArrayList<>();

    public List<Integer> getGlobalIDs() {
        return globalIDs;
    }

    public List<Global> getGlobals() {
        return this.getItems(MapLink.GLOBALS, getGlobalIDs());
    }

    @Override
    public double getHeight() {
        return height == null ? DEFAULT_GLOBAL_HEIGHT : height.doubleValue();
    }

    @Override
    public double getWidth() {
        return width == null ? DEFAULT_GLOBAL_WIDTH : width.doubleValue();
    }

}
