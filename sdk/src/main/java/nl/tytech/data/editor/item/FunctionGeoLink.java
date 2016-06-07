/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.editor.item;

import java.util.List;
import java.util.Set;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.item.Function;
import nl.tytech.data.engine.item.Function.Region;
import nl.tytech.data.engine.item.Stakeholder;
import nl.tytech.data.engine.serializable.Category;
import nl.tytech.data.engine.serializable.CategoryValue;
import nl.tytech.data.engine.serializable.ConstructionPeriod;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;

/**
 * @author Jurrian Hartveldt
 */
public class FunctionGeoLink extends GeoLink {

    private static final long serialVersionUID = 3227809616030984800L;

    public final static int DEFAULT_PRIORITY = 55;

    public static Stakeholder.Type getDefaultStakeholderType(Iterable<Category> categories) {
        for (Category cat : categories) {
            switch (cat) {
                case AGRICULTURE:
                    return Stakeholder.Type.FARMER;
                case SHOPPING:
                case LEISURE:
                case INDUSTRY:
                case OFFICES:
                    return Stakeholder.Type.COMPANY;
                case STUDENT:
                case SOCIAL:
                    return Stakeholder.Type.HOUSING_CORPORATION;
                case NORMAL:
                case LUXE:
                case SENIOR:
                    return Stakeholder.Type.CIVILIAN;
                case EDUCATION:
                    return Stakeholder.Type.EDUCATION;
                case HEALTHCARE:
                    return Stakeholder.Type.HEALTHCARE;
                default:
            }
        }
        /**
         * All other is by default owned by the municipality
         */
        return Stakeholder.Type.MUNICIPALITY;
    }

    @ItemIDField("FUNCTIONS")
    @XMLValue
    private Integer functionID = Item.NONE;

    public double getAverageResidenceSurfaceArea() {
        return getFunction().getValue(CategoryValue.UNIT_SIZE_M2);
    }

    public Set<Category> getCategories() {
        return this.getFunction().getCategories();
    }

    @Override
    public TColor getColor() {
        return getFunction().getColor();
    }

    public List<ConstructionPeriod> getConstructionPeriods() {
        return getFunction().getConstructionPeriods();
    }

    @Override
    public Stakeholder.Type getDefaultStakeholderType() {
        return getDefaultStakeholderType(getCategories());
    }

    public Function getFunction() {
        return this.getItem(MapLink.FUNCTIONS, functionID);
    }

    public Integer getFunctionID() {
        return functionID;
    }

    public int getMaxFloors() {
        return getFunction().getMaxFloorsFunction();
    }

    public int getMinFloors() {
        return getFunction().getMinFloorsFunction();
    }

    @Override
    public String getName() {
        return StringUtils.EMPTY + getFunction().getName();
    }

    public List<Region> getRegions() {
        return getFunction().getRegions();
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

    public void setFunctionID(Integer functionID) {
        this.functionID = functionID;
    }
}
