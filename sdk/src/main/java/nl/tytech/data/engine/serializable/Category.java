/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import nl.tytech.data.engine.item.Building;
import nl.tytech.data.engine.item.ClientWord.ClientTerms;
import nl.tytech.data.engine.item.Function;
import nl.tytech.data.engine.item.Function.Value;
import nl.tytech.util.color.TColor;

/**
 * Main categories to group building types.
 * @author Maxim Knepfle
 *
 */
public enum Category {

    /**
     * Social housing.
     */
    SOCIAL(ClientTerms.FUNCTION_CATEGORY_SOCIAL,
    // type is housing and is part of AllocationPlan
            true, true,
            // heat, Quality of Life
            5, -5,
            // color
            new TColor(219, 59, 108), new TColor(150, 76, 76),
            // sell build demolish (costs per M2)
            3000, 2000, 250,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            80, -0.03f, 0, 2.5f, "category_house.png", false,
            // heat flow start year, start value, change per year, power multiplier
            1978, -0.322532, 0.004685, 4.86),

    /**
     * Mid-range homes and apartments.
     */
    NORMAL(ClientTerms.FUNCTION_CATEGORY_NORMAL,
    // type is housing and is part of AllocationPlan
            true, true,
            // heat, Quality of Life
            5, 0,
            // color
            new TColor(219, 111, 0), new TColor(150, 76, 76),
            // sell build demolish (costs per M2)
            4000, 3000, 250,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            150, -0.017f, 0, 3, "category_house.png", false,
            // heat flow start year, start value, change per year, power multiplier
            1978, -0.322532, 0.004685, 4.86),
    /**
     * Luxurious villa's and penthouses.
     */
    LUXE(ClientTerms.FUNCTION_CATEGORY_LUXE,
    // type is housing and is part of AllocationPlan
            true, true,
            // heat, Quality of Life
            4, 7,
            // color
            new TColor(219, 16, 23), new TColor(150, 76, 76),
            // sell build demolish (costs per M2)
            5000, 4000, 250,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            250, -0.009f, 0, 3, "category_house.png", false,
            // heat flow start year, start value, change per year, power multiplier
            1978, -0.322532, 0.004685, 4.86),
    /**
     * Roads (both small and large)
     */
    ROAD(ClientTerms.FUNCTION_CATEGORY_ROAD,
    // type is housing and is part of AllocationPlan
            false, false,
            // heat, Quality of Life
            6, -2,
            // color
            new TColor(100, 100, 100), new TColor(0, 0, 0),
            // sell build demolish (costs per M2)
            0, 100, 20,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            1, 0, 0, 2, "category_road.png", true,
            // heat flow start year, start value, change per year, power multiplier
            0, 0, 0, 0),
    /**
     * Paved areas like squares, parking lots
     */
    PAVED_AREA(ClientTerms.FUNCTION_CATEGORY_PAVED_AREA,
    // type is housing and is part of AllocationPlan
            false, false,
            // heat, Quality of Life
            8, -2,
            // color
            new TColor(161, 157, 165), new TColor(76, 76, 76),
            // sell build demolish (costs per M2)
            0, 100, 20,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            1, 0, 0, 2, "category_watersquare.png", true,
            // heat flow start year, start value, change per year, power multiplier
            0, 0, 0, 0),
    /**
     * Educational building.
     */
    EDUCATION(ClientTerms.FUNCTION_CATEGORY_EDUCATION,
    // type is housing and is part of AllocationPlan
            false, true,
            // heat, Quality of Life
            6, -2,
            // color
            new TColor(208, 233, 250), new TColor(76, 76, 76),
            // sell build demolish (costs per M2)
            0, 2000, 250,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            1, -0.06f, 0, 4, "category_apartments.png", false,
            // heat flow start year, start value, change per year, power multiplier
            1900, -0.39783, 0.00174, 3.78),
    /**
     * Healthcare building.
     */
    HEALTHCARE(ClientTerms.FUNCTION_CATEGORY_HEALTHCARE,
    // type is housing and is part of AllocationPlan
            false, true,
            // heat, Quality of Life
            6, 2,
            // color
            new TColor(168, 0, 255), new TColor(76, 76, 76),
            // sell build demolish (costs per M2)
            0, 2000, 250,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            1, -0.08f, 0, 4, "category_office.png", false,
            // heat flow start year, start value, change per year, power multiplier
            1900, -0.4296, 0.001, 6.3),
    /**
     * Public cultivated Park
     */
    PARK(ClientTerms.FUNCTION_CATEGORY_PARK,
    // type is housing and is part of AllocationPlan
            false, false,
            // heat, Quality of Life
            -8, 4,
            // color
            new TColor(137, 176, 41), new TColor(76, 250, 76),
            // sell build demolish (costs per M2)
            0, 500, 10,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            1, 0f, 1f, 1, "category_park.png", true,
            // heat flow start year, start value, change per year, power multiplier
            0, 0, 0, 0),
    /**
     * Raw nature (mostly trees)
     */
    NATURE(ClientTerms.FUNCTION_CATEGORY_NATURE,
    // type is housing and is part of AllocationPlan
            false, false,
            // heat, Quality of Life
            -10, 3,
            // color
            new TColor(94, 170, 79), new TColor(76, 250, 76),
            // sell build demolish (costs per M2)
            0, 0, 10,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            1, 0f, 1f, 1, "category_park.png", true,
            // heat flow start year, start value, change per year, power multiplier
            0, 0, 0, 0),
    /**
     * Industry (both heavy and normal)
     */
    INDUSTRY(ClientTerms.FUNCTION_CATEGORY_INDUSTRY,
    // type is housing and is part of AllocationPlan
            false, true,
            // heat, Quality of Life
            6, -7,
            // color
            new TColor(209, 208, 205), new TColor(76, 76, 76),
            // sell build demolish (costs per M2)
            5000, 4000, 250,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            1, -0.02f, 0, 4, "category_industry.png", false,
            // heat flow start year, start value, change per year, power multiplier
            1900, -0.14738564, 0, 4.5),
    /**
     * Offices
     */
    OFFICES(ClientTerms.FUNCTION_CATEGORY_OFFICES,
    // type is housing and is part of AllocationPlan
            false, true,
            // heat, Quality of Life
            7, 0,
            // color
            new TColor(243, 176, 46), new TColor(76, 76, 76),
            // sell build demolish (costs per M2)
            5000, 4000, 250,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            1, -0.02f, 0, 4, "category_office.png", false,
            // heat flow start year, start value, change per year, power multiplier
            1900, -0.32340, 0.00066, 4.5),
    /**
     * The rest
     */
    OTHER(ClientTerms.FUNCTION_CATEGORY_OTHER,
    // type is housing and is part of AllocationPlan
            false, true,
            // heat, Quality of Life
            6, 0,
            // color
            new TColor(83, 117, 71), new TColor(76, 76, 76),
            // sell build demolish (costs per M2)
            5000, 4000, 250,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            1, 0f, 0, 3, "category_actor.png", true,
            // heat flow start year, start value, change per year, power multiplier
            0, 0, 0, 0),
    /**
     * Senior/Elderly people housing.
     */
    SENIOR(ClientTerms.FUNCTION_CATEGORY_SENIOR,
    // type is housing and is part of AllocationPlan
            true, true,
            // heat, Quality of Life
            5, 1,
            // color
            new TColor(194, 94, 49), new TColor(150, 76, 76),
            // sell build demolish (costs per M2)
            3000, 2000, 250,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            100, -0.015f, 0, 2, "category_house.png", false,
            // heat flow start year, start value, change per year, power multiplier
            1978, -0.322532, 0.004685, 4.86),

    /**
     * Underground without a building on top
     */
    UNDERGROUND(ClientTerms.FUNCTION_CATEGORY_UNDERGROUND,
    // type is housing and is part of AllocationPlan
            false, true,
            // heat, Quality of Life
            0, 0,
            // color
            new TColor(135, 60, 0), new TColor(76, 76, 76),
            // sell build demolish (costs per M2)
            0, 1, 20,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            1, 0, 0, 2, "category_sewers.png", true,
            // heat flow start year, start value, change per year, power multiplier
            0, 0, 0, 0),

    /**
     * Underground with a building on top
     */
    @Deprecated
    UNDERGROUND_WITH_TOP_BUILDING(ClientTerms.FUNCTION_CATEGORY_UNDERGROUND_WITH_TOP_BUILDING,
    // type is housing and is part of AllocationPlan
            false, true,
            // heat, Quality of Life
            0, 0,
            // color
            new TColor(131, 135, 114), new TColor(76, 76, 76),
            // sell build demolish (costs per M2)
            0, 100, 20,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            1, 0, 0, 2, "category_sewers.png", true,
            // heat flow start year, start value, change per year, power multiplier
            0, 0, 0, 0),

    @Deprecated
    DIKE(ClientTerms.FUNCTION_CATEGORY_DIKE,
    // type is housing and is part of AllocationPlan
            false, false,
            // heat, Quality of Life
            -5, 3,
            // color
            new TColor(94, 170, 75), new TColor(76, 76, 76),
            // sell build demolish (costs per M2)
            0, 0, 500,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            1, 0f, 0.5f, 3, "category_wateroverflow.png", false,
            // heat flow start year, start value, change per year, power multiplier
            0, 0, 0, 0),
    /**
     * Shops including restaurants and bars
     */
    SHOPPING(ClientTerms.FUNCTION_CATEGORY_SHOPPING,
    // type is housing and is part of AllocationPlan
            false, true,
            // heat, Quality of Life
            7, -2,
            // color
            new TColor(243, 200, 46), new TColor(76, 76, 76),
            // sell build demolish (costs per M2)
            5000, 4000, 250,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            1, -0.033f, 0, 4, "category_office.png", false,
            // heat flow start year, start value, change per year, power multiplier
            1900, -0.318158, 0.00106, 4.32),

    /**
     * Agriculture
     */
    AGRICULTURE(ClientTerms.FUNCTION_CATEGORY_AGRICULTURE,
    // type is housing and is part of AllocationPlan
            false, true,
            // heat, Quality of Life
            4, 0,
            // color
            TColor.WHITE, new TColor(76, 150, 76),
            // sell build demolish (costs per M2)
            5000, 4000, 250,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            1, 0f, 0, 3, "category_roof.png", true,
            // heat flow start year, start value, change per year, power multiplier
            0, 0, 0, 0),
    /**
     * Recreation, sports, culture
     */
    LEISURE(ClientTerms.FUNCTION_CATEGORY_LEISURE,
    // type is housing and is part of AllocationPlan
            false, true,
            // heat, Quality of Life
            6, 0,
            // color
            new TColor(255, 255, 71), new TColor(76, 76, 76),
            // sell build demolish (costs per M2)
            5000, 4000, 500,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            1, -0.013f, 0, 3, "category_heart.png", true,
            // heat flow start year, start value, change per year, power multiplier
            1900, -0.41016, 0.0022, 5.04),
    /**
     * Student housing. (form of social housing)
     */
    STUDENT(ClientTerms.FUNCTION_CATEGORY_STUDENT,
    // type is housing and is part of AllocationPlan
            true, true,
            // heat, Quality of Life
            5, -5,
            // color
            new TColor(219, 200, 0), new TColor(76, 76, 76),
            // sell build demolish (costs per M2)
            3000, 2000, 250,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            20, -0.008f, 0, 2.5f, "category_apartments.png", false,
            // heat flow start year, start value, change per year, power multiplier
            1978, -0.322532, 0.004685, 4.86),
    /**
     * Gardens around houses
     */
    GARDEN(ClientTerms.FUNCTION_CATEGORY_GARDEN,
    // type is housing and is part of AllocationPlan
            false, false,
            // heat, Quality of Life
            0, 0,
            // color
            new TColor(191, 210, 155), new TColor(76, 250, 76),
            // sell build demolish (costs per M2)
            0, 500, 10,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            1, 0f, 1f, 1, "category_park.png", true,
            // heat flow start year, start value, change per year, power multiplier
            0, 0, 0, 0),

    /**
     * Roads (both small and large)
     */
    INTERSECTION(ClientTerms.FUNCTION_CATEGORY_INTERSECTION,
    // type is housing and is part of AllocationPlan
            false, false,
            // heat, Quality of Life
            6, -2,
            // color
            new TColor(101, 100, 100), new TColor(0, 0, 0),
            // sell build demolish (costs per M2)
            0, 100, 20,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            1, 0, 0, 2, "category_road.png", true,
            // heat flow start year, start value, change per year, power multiplier
            0, 0, 0, 0),
    /**
     * Roads (both small and large)
     */
    BRIDGE(ClientTerms.FUNCTION_CATEGORY_BRIDGE,
    // type is housing and is part of AllocationPlan
            false, false,
            // heat, Quality of Life
            6, -2,
            // color
            new TColor(100, 100, 101), new TColor(0, 0, 0),
            // sell build demolish (costs per M2)
            0, 100, 20,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            1, 0, 0, 2, "category_road.png", true,
            // heat flow start year, start value, change per year, power multiplier
            0, 0, 0, 0);

    public final static Category[] VALUES;
    static {
        List<Category> list = new ArrayList<>(Arrays.asList(Category.values()));
        list.remove(Category.DIKE);
        list.remove(Category.UNDERGROUND_WITH_TOP_BUILDING);
        VALUES = list.toArray(new Category[list.size()]);
    }

    private boolean housing;
    private ClientTerms term;
    private TColor color = TColor.BLACK;
    private TColor roofColor = TColor.BLACK;
    private String iconName;
    private boolean single;
    private boolean road;

    private HashMap<Value, Double> categoryValues = new HashMap<>();

    private Category(ClientTerms term, boolean housing, boolean zoningPermitRequired, double heat, double qol, TColor color,
            TColor roofColor, double sellPriceM2, double buildCostM2, double demolishCostM2, double unitSizeM2,
            double parkingLotsDemandPerM2, double greenM2, double buildTimeMonths, String iconName, boolean single,
            double heatFlowStartYear, double heatFlowGJm2StartValue, double heatFlowGJm2ChangePerYear, double heatFlowPowerMultiplier) {

        this.term = term;
        this.housing = housing;
        this.color = color;
        this.roofColor = roofColor;
        this.iconName = iconName;
        this.single = single;
        this.road = term == ClientTerms.FUNCTION_CATEGORY_ROAD || term == ClientTerms.FUNCTION_CATEGORY_BRIDGE
                || term == ClientTerms.FUNCTION_CATEGORY_INTERSECTION;

        categoryValues.put(FunctionValue.MIN_FLOORS, 1d);
        categoryValues.put(FunctionValue.FLOORS, 1d);
        categoryValues.put(FunctionValue.MAX_FLOORS, zoningPermitRequired ? 5d : 1d);

        categoryValues.put(FunctionValue.ZONING_PERMIT_REQUIRED, zoningPermitRequired ? 1d : 0d);
        categoryValues.put(FunctionValue.SLANTING_ROOF_HEIGHT, 0d);
        categoryValues.put(FunctionValue.HEAT_EFFECT, heat);
        categoryValues.put(FunctionValue.DISTANCE_ZONE_M, 0d);
        categoryValues.put(FunctionValue.LIVABILITY_EFFECT, qol);
        categoryValues.put(FunctionValue.GREEN_M2, greenM2);
        categoryValues.put(FunctionValue.FLOATING, 0d);

        categoryValues.put(CategoryValue.CATEGORY_WEIGHT, 1d);
        categoryValues.put(CategoryValue.UNIT_SIZE_M2, unitSizeM2);
        categoryValues.put(CategoryValue.CONSTRUCTION_COST_M2, buildCostM2);
        categoryValues.put(CategoryValue.DEMOLISH_COST_M2, demolishCostM2);
        categoryValues.put(CategoryValue.SELL_PRICE_M2, sellPriceM2);

        categoryValues.put(CategoryValue.PARKING_LOTS_PER_M2, 0d);
        categoryValues.put(CategoryValue.PARKING_LOTS_DEMAND_PER_M2, Math.abs(parkingLotsDemandPerM2));
        categoryValues.put(FunctionValue.HEIGHT_OFFSET_M, term == ClientTerms.FUNCTION_CATEGORY_BRIDGE ? 3.0 : 0.0);

        categoryValues.put(FunctionValue.TRAFFIC_NOISE_SIGMA, 0.0);
        categoryValues.put(FunctionValue.TRAFFIC_NOISE_TAU, 0.0);
        categoryValues.put(FunctionValue.TRAFFIC_FLOW, road ? 6.0 : 0.0);
        categoryValues.put(FunctionValue.TRAFFIC_SPEED, road ? 50.0 : 0.0);
        categoryValues.put(FunctionValue.TRAFFIC_LANES, road ? 1.0 : 0.0);
        categoryValues.put(FunctionValue.PIPES_PERMITTED, road ? 1.0 : 0.0);

        /**
         * Buyout costs are 80% of original price. -> Price you need to pay to kick out the inhabitants
         */
        categoryValues.put(CategoryValue.BUYOUT_COST_M2, zoningPermitRequired ? sellPriceM2 * 0.8d : 0d);

        categoryValues.put(FunctionValue.CONSTRUCTION_TIME_IN_MONTHS, buildTimeMonths);
        categoryValues.put(FunctionValue.DRAINAGE, 0d);
        categoryValues.put(FunctionValue.DEMOLISH_TIME_IN_MONTHS, Function.DEFAULT_DEMOLISH_TIME_IN_MONTHS);
        categoryValues.put(FunctionValue.WATER_STORAGE_M2, 0d);
        categoryValues.put(FunctionValue.FLOOR_HEIGHT_M, zoningPermitRequired ? 3.5d : 0d);

        /**
         * TODO: Maxim differentiate per function type.
         */
        double demand = zoningPermitRequired ? -0.3d : 0d;
        categoryValues.put(CategoryValue.HEAT_FLOW_M2_YEAR, demand);

        categoryValues.put(CategoryValue.HEAT_FLOW_M2_START_YEAR, heatFlowStartYear);
        categoryValues.put(CategoryValue.HEAT_FLOW_M2_START_VALUE, heatFlowGJm2StartValue);
        categoryValues.put(CategoryValue.HEAT_FLOW_M2_CHANGE_PER_YEAR, heatFlowGJm2ChangePerYear);
        categoryValues.put(CategoryValue.HEAT_POWER_TO_FLOW_MULTIPLIER, heatFlowPowerMultiplier);

    }

    public Double getCategoryValue(CategoryValue key) {
        return categoryValues.get(key);
    }

    public TColor getColor() {
        return color;
    }

    public Double getFunctionValue(FunctionValue key) {
        return categoryValues.get(key);
    }

    public Building.GroundLayerType getGroundLayerType() {
        switch (this) {
            case UNDERGROUND:
            case UNDERGROUND_WITH_TOP_BUILDING:
                return Building.GroundLayerType.UNDERGROUND;
            default:
                return Building.GroundLayerType.SURFACE;
        }
    }

    public String getIconName() {
        return iconName;
    }

    public ClientTerms getLocalisedTerm() {
        return term;
    }

    public TColor getRoofColor() {
        return roofColor;
    }

    public boolean isCategoryPermitRequired() {
        return categoryValues.get(FunctionValue.ZONING_PERMIT_REQUIRED) > 0;
    }

    public boolean isHousing() {
        return housing;
    }

    public boolean isRoad() {
        return road;
    }

    public boolean isSingle() {
        return single;
    }
}
