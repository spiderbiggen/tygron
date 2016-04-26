/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import nl.tytech.core.item.annotations.AssetDirectory;
import nl.tytech.core.item.annotations.Html;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.item.UniqueNamedItem;
import nl.tytech.data.engine.other.Action;
import nl.tytech.util.StringUtils;

/**
 * Dike
 * @author Maxim Knepfle
 */
public class Dike extends UniqueNamedItem implements Action {

    private static final String DEFAULT_DIKE_IMAGE = "climate_dike.png";

    private static final long serialVersionUID = -4253782210241563578L;

    @ItemIDField("FUNCTIONS")
    @XMLValue
    private Integer topFunctionID = Item.NONE;

    @ItemIDField("FUNCTIONS")
    @XMLValue
    private Integer sideFunctionID = Item.NONE;

    @XMLValue
    private Double defaultHeightM = 4d;

    @XMLValue
    private Double defaultWidthM = 20d;

    @XMLValue
    private boolean fixedSize = false;

    @Html
    @XMLValue
    private String description = StringUtils.EMPTY;

    @AssetDirectory(GUI_IMAGES_ACTIONS)
    @XMLValue
    private String imageName = DEFAULT_DIKE_IMAGE;

    // local value used to check if asset was updated in editor
    private int imageVersion = 0;

    public Dike() {

    }

    public Dike(Dike other) {
        setTopFunctionID(other.topFunctionID);
        setSideFunctionID(other.sideFunctionID);
        setDefaultHeightM(other.defaultHeightM);
        setDefaultWidthM(other.defaultWidthM);
        setDescription(other.description);
        setImageName(other.imageName);
        setName(other.getName());
    }

    @Override
    public double getConstructionTimeInMonths() {

        double months = 3;
        Function top = this.getTopFunction();
        if (top != null && top.getConstructionTimeInMonths() > months) {
            months = top.getConstructionTimeInMonths();
        }
        Function side = this.getSideFunction();
        if (side != null && side.getConstructionTimeInMonths() > months) {
            months = side.getConstructionTimeInMonths();
        }
        return months;
    }

    public double getDefaultHeightM() {
        return defaultHeightM;
    }

    public double getDefaultWidthM() {
        return defaultWidthM;
    }

    @Override
    public String getDescription() {

        if (!StringUtils.containsData(description)) {
            return getName();
        } else {
            return description;
        }
    }

    @Override
    public String getImageLocation() {

        if (StringUtils.containsData(imageName)) {
            return GUI_IMAGES_ACTIONS + imageName;
        } else {
            return StringUtils.EMPTY;
        }
    }

    public String getImageName() {
        return imageName;
    }

    public int getImageVersion() {
        return imageVersion;
    }

    @Override
    public MapLink getMapLink() {
        return MapLink.DIKES;
    }

    public Function getSideFunction() {
        return this.getItem(MapLink.FUNCTIONS, this.getSideFunctionID());
    }

    public Integer getSideFunctionID() {
        return sideFunctionID;
    }

    public Function getTopFunction() {
        return this.getItem(MapLink.FUNCTIONS, this.getTopFunctionID());
    }

    public Integer getTopFunctionID() {
        return topFunctionID;
    }

    @Override
    public boolean isBuildable() {
        return true;
    }

    @Override
    public boolean isFixedLocation() {
        return false;
    }

    public boolean isFixedSize() {
        return fixedSize;
    }

    public void setDefaultHeightM(double defaultHeightM) {
        this.defaultHeightM = defaultHeightM;
    }

    public void setDefaultWidthM(double defaultWidthM) {
        this.defaultWidthM = defaultWidthM;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFixedSize(boolean fixedSize) {
        this.fixedSize = fixedSize;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
        this.imageVersion++;
    }

    public void setSideFunctionID(Integer sideFunctionID) {
        this.sideFunctionID = sideFunctionID;
    }

    public void setTopFunctionID(Integer topFunctionID) {
        this.topFunctionID = topFunctionID;
    }

}
