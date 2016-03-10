/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.item.Global.ReadOnly;

/**
 * Subsidence based on a grid
 *
 * @author Maxim Knepfle
 */
public class SubsidenceOverlay extends GroundWaterOverlay {

    /**
     *
     */
    private static final long serialVersionUID = -2572949484529750426L;

    @XMLValue
    @ItemIDField("GLOBALS")
    private Integer aID = Item.NONE;

    @XMLValue
    @ItemIDField("GLOBALS")
    private Integer bID = Item.NONE;

    @XMLValue
    @ItemIDField("GLOBALS")
    private Integer cID = Item.NONE;

    @XMLValue
    private String clayThicknessKey = "CLAY_THICKNESS";

    @XMLValue
    @ItemIDField("GLOBALS")
    private Integer yearsID = Item.NONE;

    public double getA() {
        Global global = this.getItem(MapLink.GLOBALS, this.aID);
        return global != null ? global.getActualValue() : ReadOnly.SUBSIDENCE_A.getDefaultValue();
    }

    public double getB() {
        Global global = this.getItem(MapLink.GLOBALS, this.bID);
        return global != null ? global.getActualValue() : ReadOnly.SUBSIDENCE_B.getDefaultValue();
    }

    public double getC() {
        Global global = this.getItem(MapLink.GLOBALS, this.cID);
        return global != null ? global.getActualValue() : ReadOnly.SUBSIDENCE_C.getDefaultValue();
    }

    public String getClayThicknessKey() {
        return clayThicknessKey;
    }

    @Override
    public double getDiagramMultiplier() {
        return Math.max(1, this.getYears());
    }

    public double getYears() {
        Global global = this.getItem(MapLink.GLOBALS, this.yearsID);
        return global != null ? global.getActualValue() : ReadOnly.SUBSIDENCE_YEARS.getDefaultValue();
    }

    public void setClayThicknessKey(String clayThicknessKey) {
        this.clayThicknessKey = clayThicknessKey;
    }

    public void setGlobalA(Integer id) {
        this.aID = id;
    }

    public void setGlobalB(Integer id) {
        this.bID = id;
    }

    public void setGlobalC(Integer id) {
        this.cID = id;
    }

    public void setGlobalYears(Integer id) {
        this.yearsID = id;
    }

    @Override
    public double toOriginalValue(byte overlayValue) {
        return super.toOriginalValue(overlayValue) * this.getYears();
    }
}
