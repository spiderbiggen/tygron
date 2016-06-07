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
import nl.tytech.util.color.TColor;

/**
 * Special overlay that can show several areas
 *
 * @author Frank Baars
 */
public class AreaOverlay extends Overlay {

    /**
     *
     */
    private static final long serialVersionUID = 2278041675288910921L;

    @XMLValue
    @ItemIDField("AREAS")
    private ArrayList<Integer> areaIDs = new ArrayList<>();

    @XMLValue
    private TColor restColor = TColor.WHITE;

    public List<Integer> getAreaIDs() {
        return areaIDs;
    }

    public List<Area> getAreas() {
        return this.getItems(MapLink.AREAS, getAreaIDs());
    }

    public TColor getRestColor() {
        return restColor;
    }

    public void setRestColor(TColor restColor) {
        this.restColor = restColor;
    }
}
