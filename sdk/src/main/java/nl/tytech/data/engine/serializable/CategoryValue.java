/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.serializable;

import nl.tytech.data.engine.item.ClientWord.ClientTerms;
import nl.tytech.data.engine.item.Function.FunctionValueGroup;
import nl.tytech.data.engine.item.Function.Value;
import nl.tytech.locale.TCurrency;
import nl.tytech.locale.unit.UnitSystemType;
import nl.tytech.locale.unit.UnitType;
import nl.tytech.util.StringUtils;

/**
 *
 * Category value enum
 *
 * @author Maxim Knepfle
 *
 */
public enum CategoryValue implements Value {

    /**
     * Size of a house in m2, all other use 1m
     */
    CATEGORY_WEIGHT(FunctionValueGroup.CONSTRUCTION, ClientTerms.CATEGORY_WEIGHT, Double.MIN_VALUE, Double.MAX_VALUE, false, UnitType.NONE),

    /**
     * Size of a house in m2, all other use 1m
     */
    UNIT_SIZE_M2(FunctionValueGroup.CONSTRUCTION, "Unit Size", ClientTerms.UNIT_FLOORSPACE, 0, Double.MAX_VALUE, false, UnitType.SURFACE),

    /**
     * Demand for heat flow per m2 floorspace
     */
    HEAT_FLOW_M2_YEAR(FunctionValueGroup.PIPES, "Heat Flow", ClientTerms.HEAT_FLOW_M2_YEAR, -100, 100, false, UnitType.HEAT_FLOW),

    /**
     * Park lots per m2 floorspace
     */
    PARKING_LOTS_PER_M2(FunctionValueGroup.TRAFFIC, "Parking Lots", ClientTerms.DETAIL_PARKING_SPACES, 0, 1, false, UnitType.NONE,
            UnitType.SURFACE),

    /**
     * Park lots demand per m2 floorspace
     */
    PARKING_LOTS_DEMAND_PER_M2(FunctionValueGroup.TRAFFIC, "Parking Lots Demand", ClientTerms.DETAIL_PARKING_SPACES, 0, 2, false,
            UnitType.NONE, UnitType.SURFACE),

    /**
     * Building cost in euro per M2 floorspace.
     */
    CONSTRUCTION_COST_M2(FunctionValueGroup.FINANCIAL, "Construction Cost", ClientTerms.CONSTRUCTION_COST, 0, Double.MAX_VALUE, true,
            UnitType.NONE, UnitType.SURFACE),

    /**
     * Demolish cost in euro per M2 floorspace.
     */
    DEMOLISH_COST_M2(FunctionValueGroup.FINANCIAL, "Demolition Cost", ClientTerms.DETAIL_DEMOLISH_COST, 0, Double.MAX_VALUE, true,
            UnitType.NONE, UnitType.SURFACE),

    /**
     * Owner buyout cost in euro per M2 floorspace.
     */
    BUYOUT_COST_M2(FunctionValueGroup.FINANCIAL, "Buyout Cost", ClientTerms.DETAIL_BUYOUT_COST, 0, Double.MAX_VALUE, true, UnitType.NONE,
            UnitType.SURFACE),

    /**
     * Sell price in euro per M2 floorspace.
     */
    SELL_PRICE_M2(FunctionValueGroup.FINANCIAL, "Sell Price", ClientTerms.DETAIL_SELL_PRICE, 0, Double.MAX_VALUE, true, UnitType.NONE,
            UnitType.SURFACE);

    public static final CategoryValue[] VALUES = values();
    private double minValue;
    private double maxValue;
    private ClientTerms description;
    private UnitType[] unitDimensions;

    private boolean monetary = false;
    private String editorName;

    private FunctionValueGroup group;

    private CategoryValue(FunctionValueGroup group, ClientTerms description, double minValue, double maxValue, boolean monetary,
            UnitType... unitDimensions) {
        this(group, null, description, minValue, maxValue, monetary, unitDimensions);
    }

    private CategoryValue(FunctionValueGroup group, String editorName, ClientTerms description, double minValue, double maxValue,
            boolean monetary, UnitType... unitDimensions) {
        this.description = description;
        this.maxValue = maxValue;
        this.minValue = minValue;
        this.unitDimensions = unitDimensions;
        this.monetary = monetary;
        this.editorName = editorName;
        this.group = group;
    }

    @Override
    public ClientTerms getDescriptionAsROTerms() {
        return description;
    }

    @Override
    public String getEditorName() {
        if (!StringUtils.containsData(editorName)) {
            editorName = StringUtils.capitalizeWithSpacedUnderScores(name());
        }
        return editorName;
    }

    @Override
    public FunctionValueGroup getGroup() {
        return group;
    }

    @Override
    public double getMaxValue() {
        return maxValue;
    }

    @Override
    public double getMinValue() {
        return minValue;
    }

    @Override
    public double getServerUnitValue(double value, UnitSystemType unitSystem) {
        return unitSystem.getImpl().toSIValue(value, getUnitDimensions());
    }

    @Override
    public String getUnit(TCurrency currency, UnitSystemType unitSystem) {
        if (!isMonetary()) {
            return unitSystem.getImpl().getUnitAbbreviation(getUnitDimensions());
        }

        return unitSystem.getImpl().getUnitAbbreviation(currency, getUnitDimensions());
    }

    @Override
    public UnitType[] getUnitDimensions() {
        return unitDimensions;
    }

    @Override
    public double getUnitValue(double value, int decimals, UnitSystemType unitSystem) {
        return unitSystem.getImpl().toLocalValue(value, decimals, getUnitDimensions());
    }

    @Override
    public double getUnitValue(double value, UnitSystemType unitSystem) {
        return unitSystem.getImpl().toLocalValue(value, getUnitDimensions());
    }

    @Override
    public String getUnitValueFormatted(double value, int decimals, UnitSystemType unitSystem) {
        return unitSystem.getImpl().toLocalValueWithFormatting(value, decimals, getUnitDimensions());
    }

    @Override
    public String getUnitValueFormatted(double value, UnitSystemType unitSystem) {
        return unitSystem.getImpl().toLocalValueWithFormatting(value, getUnitDimensions());
    }

    public boolean isMonetary() {
        return monetary;
    }
}
