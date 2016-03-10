/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.editor.item;

import java.util.Set;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.item.Function;
import nl.tytech.data.engine.item.Stakeholder.Type;
import nl.tytech.data.engine.serializable.Category;
import nl.tytech.util.color.TColor;

/**
 * @author Frank Baars
 */
public class TNLFunctionTerrainLink extends TNLTerrainLink {

    /**
     *
     */
    private static final long serialVersionUID = 6663391308498001874L;

    @ItemIDField("FUNCTIONS")
    @XMLValue
    private Integer functionID = Item.NONE;

    @XMLValue
    private boolean mustContainHouses = false;

    public Set<Category> getCategories() {
        return this.getFunction().getCategories();
    }

    @Override
    public TColor getColor() {
        return getFunction().getColor();
    }

    @Override
    public Type getDefaultStakeholderType() {
        return FunctionGeoLink.getDefaultStakeholderType(getCategories());
    }

    public Function getFunction() {
        return this.getItem(MapLink.FUNCTIONS, functionID);
    }

    @Override
    public String getName() {
        return getFunction().getName();
    }

    @Override
    public boolean isRoad() {
        for (Category cat : getFunction().getCategories()) {
            if (cat.isRoad()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isWater() {
        return false;
    }

    @Override
    public boolean mustContainHouses() {
        return mustContainHouses;
    }

}
