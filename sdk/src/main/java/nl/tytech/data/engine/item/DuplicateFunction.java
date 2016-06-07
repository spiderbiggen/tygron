/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import java.util.List;
import java.util.Map;
import java.util.Set;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.item.UnitData.TrafficType;
import nl.tytech.data.engine.serializable.Category;
import nl.tytech.data.engine.serializable.CategoryValue;
import nl.tytech.data.engine.serializable.ConstructionPeriod;
import nl.tytech.data.engine.serializable.FunctionValue;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;

/**
 * DuplicateFunction
 * <p>
 * Wrapper class around a base function.
 * </p>
 * @author Maxim Knepfle
 */
public class DuplicateFunction extends Function {

    private static final long serialVersionUID = 295091661000494566L;

    @XMLValue
    @ItemIDField("FUNCTIONS")
    private Integer orginalFunctionID = Item.NONE;

    public DuplicateFunction() {
    }

    public DuplicateFunction(Integer orginalFunctionID) {
        this.orginalFunctionID = orginalFunctionID;
    }

    @Override
    public final TColor getColor() {
        return getOrginalFunction().getColor();
    }

    @Override
    public List<ConstructionPeriod> getConstructionPeriods() {
        return getOrginalFunction().getConstructionPeriods();
    }

    @Override
    public String getDescription() {
        /**
         * Try override first
         */
        FunctionOverride functionOverride = this.getItem(MapLink.FUNCTION_OVERRIDES, this.getID());
        if (functionOverride != null && StringUtils.containsData(functionOverride.getDescription())) {
            return functionOverride.getDescription();
        }
        return getOrginalFunction().getDescription();
    }

    @Override
    public final int getDimension() {
        return getOrginalFunction().getDimension();
    }

    @Override
    public int getDistanceRoad() {
        return getOrginalFunction().getDistanceRoad();
    }

    @Override
    public String getExtraTexture() {
        return getOrginalFunction().getExtraTexture();
    }

    @Override
    public String getGroundTexture() {
        return getOrginalFunction().getGroundTexture();
    }

    @Override
    public String getImageLocation() {
        /**
         * Try override first
         */
        FunctionOverride functionOverride = this.getItem(MapLink.FUNCTION_OVERRIDES, this.getID());
        if (functionOverride != null && StringUtils.containsData(functionOverride.getImageName())) {
            return functionOverride.getImageLocation();
        }
        return getOrginalFunction().getImageLocation();
    }

    @Override
    public String getImageName() {
        /**
         * Try override first
         */
        FunctionOverride functionOverride = this.getItem(MapLink.FUNCTION_OVERRIDES, this.getID());
        if (functionOverride != null && StringUtils.containsData(functionOverride.getImageName())) {
            return functionOverride.getImageName();
        }
        return getOrginalFunction().getImageName();
    }

    @Override
    public MapLink getMapLink() {
        return MapLink.FUNCTIONS;
    }

    @Override
    public ModelSet getModelSet() {
        return getOrginalFunction().getModelSet();
    }

    @Override
    public final String getName() {

        /**
         * Try override first
         */
        FunctionOverride functionOverride = this.getItem(MapLink.FUNCTION_OVERRIDES, this.getID());
        if (functionOverride != null && StringUtils.containsData(functionOverride.getName())) {
            return functionOverride.getName();
        }
        return getOrginalFunction().getName();
    }

    @Override
    public Double getOrginalCategoryValue(Category cat, CategoryValue key) {
        return getOrginalFunction().getOrginalCategoryValue(cat, key);
    }

    private Function getOrginalFunction() {
        return this.getItem(MapLink.FUNCTIONS, orginalFunctionID);
    }

    @Override
    public Double getOrginalFunctionValue(FunctionValue key) {
        return getOrginalFunction().getOrginalFunctionValue(key);
    }

    @Override
    public Set<Category> getOriginalCategories() {
        return getOrginalFunction().getOriginalCategories();
    }

    @Override
    public PlacementType getPlacementType() {
        return getOrginalFunction().getPlacementType();
    }

    @Override
    public List<Region> getRegions() {
        return getOrginalFunction().getRegions();
    }

    @Override
    public TColor getRoofColor() {
        /**
         * Try override first
         */
        FunctionOverride functionOverride = this.getItem(MapLink.FUNCTION_OVERRIDES, this.getID());
        if (functionOverride != null && functionOverride.getRoofColor() != null) {
            return functionOverride.getRoofColor();
        }
        return getOrginalFunction().getRoofColor();
    }

    @Override
    public double getRoofInset() {
        return getOrginalFunction().getRoofInset();
    }

    @Override
    public String getRoofTexture() {
        return getOrginalFunction().getRoofTexture();
    }

    @Override
    public String getTopTexture() {
        return getOrginalFunction().getTopTexture();
    }

    @Override
    public Map<TrafficType, Double> getTrafficValues() {
        return getOrginalFunction().getTrafficValues();
    }

    @Override
    public TColor getWallColor() {
        /**
         * Try override first
         */
        FunctionOverride functionOverride = this.getItem(MapLink.FUNCTION_OVERRIDES, this.getID());
        if (functionOverride != null && functionOverride.getWallColor() != null) {
            return functionOverride.getWallColor();
        }
        return getOrginalFunction().getWallColor();
    }

    @Override
    public boolean isDefaultFunction(Stakeholder stakeholder) {
        return getOrginalFunction().isDefaultFunction(stakeholder);
    }

    @Override
    public boolean isDeprecated() {
        return getOrginalFunction().isDeprecated();
    }

    @Override
    public boolean isInRegion(Region region) {
        return getOrginalFunction().isInRegion(region);
    }

    @Override
    protected void putOrginalFunctionValue(FunctionValue key, Double value) {
        getOrginalFunction().putOrginalFunctionValue(key, value);
    }

    @Override
    public String validated(boolean startNewGame) {

        if (this.getOrginalFunction() == null) {
            return "\nMissing orginal base function with ID: " + this.orginalFunctionID + " for duplicate function with ID: "
                    + this.getID();
        }
        return super.validated(startNewGame);
    }
}
