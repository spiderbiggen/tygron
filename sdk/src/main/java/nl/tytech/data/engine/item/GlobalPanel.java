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

    @XMLValue
    @ItemIDField("GLOBALS")
    private ArrayList<Integer> globalIDs = new ArrayList<>();

    public List<Integer> getGlobalIDs() {
        return globalIDs;
    }

    public List<Global> getGlobals() {
        return this.getItems(MapLink.GLOBALS, getGlobalIDs());
    }

}
