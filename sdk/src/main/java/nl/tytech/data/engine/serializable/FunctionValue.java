/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.serializable;

import java.util.ArrayList;
import java.util.List;
import nl.tytech.data.engine.item.ClientWord.ClientTerms;
import nl.tytech.data.engine.item.Function;
import nl.tytech.data.engine.item.Function.FunctionValueGroup;
import nl.tytech.data.engine.item.Function.Value;
import nl.tytech.data.engine.item.GridOverlay;
import nl.tytech.locale.TCurrency;
import nl.tytech.locale.unit.UnitSystemType;
import nl.tytech.locale.unit.UnitType;
import nl.tytech.util.ObjectUtils;
import nl.tytech.util.StringUtils;

/**
 *
 * Values specific to functions
 *
 * @author Maxim Knepfle
 *
 */
public enum FunctionValue implements Value {

    /**
     * Effect on the QOL Environment.
     */
    LIVABILITY_EFFECT(FunctionValueGroup.ENVIRONMENT, ClientTerms.ENVIRONMENT_EFFECT, -10, 10),

    /**
     * Amount of traffic
     */
    TRAFFIC_FLOW(FunctionValueGroup.TRAFFIC, "Units per hour", ClientTerms.TRAFFIC_FLOW, 0, Double.MAX_VALUE, false),

    /**
     * Traffic speed in kmph
     */
    TRAFFIC_SPEED(FunctionValueGroup.TRAFFIC, "Speedlimit kmph", ClientTerms.TRAFFIC_SPEED, 0, Double.MAX_VALUE, false),

    /**
     * Traffic Lanes
     */
    TRAFFIC_LANES(FunctionValueGroup.TRAFFIC, "Lanes", ClientTerms.TRAFFIC_LANES, -Double.MAX_VALUE, Double.MAX_VALUE, false),

    /**
     * Road sigma: Constante per weg type: verschil in dB(A) bij de referentiesnelheid V0
     */
    TRAFFIC_NOISE_SIGMA(FunctionValueGroup.TRAFFIC, "Noise Sigma", ClientTerms.TRAFFIC_NOISE_SIGMA, -Double.MAX_VALUE, Double.MAX_VALUE,
            false),

    /**
     * Road tau: Constante per weg type: snelheidsindex in dB(A) per decade snelheidstoename
     */
    TRAFFIC_NOISE_TAU(FunctionValueGroup.TRAFFIC, "Noise Tau", ClientTerms.TRAFFIC_NOISE_TAU, -Double.MAX_VALUE, Double.MAX_VALUE, false),

    /**
     * Bridge height M
     */
    HEIGHT_OFFSET_M(FunctionValueGroup.TRAFFIC, "(Bridge) Height Offset", ClientTerms.TRAFFIC_SPEED, 0, Double.MAX_VALUE, false,
            UnitType.LENGTH),

    /**
     * Effect on the heat in degrees per Tile.
     */
    HEAT_EFFECT(FunctionValueGroup.ENVIRONMENT, ClientTerms.TILE_HEAT_EFFECT_UNIT, -10, 10, UnitType.TEMPERATURE_RELATIVE),

    /**
     * Amount of meters around this function that is part of safe zone.
     */
    DISTANCE_ZONE_M(FunctionValueGroup.ENVIRONMENT, "Distance Zone", ClientTerms.DISTANCE_ZONE, 0, GridOverlay.MAX_DISTANCE_ZONE_M, false,
            UnitType.LENGTH),

    /**
     * Water storage in M3 on the roof per M2 kavel.
     */
    WATER_STORAGE_M2(FunctionValueGroup.ENVIRONMENT, "Water Storage", ClientTerms.FUNCTION_WATER_STORAGE, 0, Double.MAX_VALUE, false,
            UnitType.VOLUME, UnitType.SURFACE),

    /**
     * Amount green M2 per M2 kavel.
     */
    GREEN_M2(FunctionValueGroup.ENVIRONMENT, "Green Space", ClientTerms.GREEN, 0, 1, false, UnitType.SURFACE, UnitType.SURFACE),

    /**
     * Build time in months
     */
    CONSTRUCTION_TIME_IN_MONTHS(FunctionValueGroup.CONSTRUCTION, ClientTerms.CONSTRUCTION_PERIOD_UNIT, 0, Double.MAX_VALUE),

    FLOOR_HEIGHT_M(FunctionValueGroup.CONSTRUCTION, ClientTerms.DEFAULT_FLOOR_HEIGHT, 0, Double.MAX_VALUE),

    /**
     * Minimal amount of floors
     */
    MIN_FLOORS(FunctionValueGroup.CONSTRUCTION, ClientTerms.MIN_FLOORS, Function.MIN_ALLOWED_FLOORS, Function.MAX_ALLOWED_FLOORS),

    /**
     * Default amount of floors
     */
    FLOORS(FunctionValueGroup.CONSTRUCTION, ClientTerms.DEFAULT_FLOORS, Function.MIN_ALLOWED_FLOORS, Function.MAX_ALLOWED_FLOORS),

    /**
     * Default amount of floors
     */
    FLOATING(FunctionValueGroup.CONSTRUCTION, ClientTerms.FLOATING, 0, 1),

    /**
     * Maximum amount of floors
     */
    MAX_FLOORS(FunctionValueGroup.CONSTRUCTION, ClientTerms.MAX_FLOORS, Function.MIN_ALLOWED_FLOORS, Function.MAX_ALLOWED_FLOORS),

    /**
     * Demolish time in months;
     */
    DEMOLISH_TIME_IN_MONTHS(FunctionValueGroup.FINANCIAL, ClientTerms.DEMOLISH_PERIOD_UNIT, 0, Double.MAX_VALUE),

    /**
     * Drainage influence;
     */
    DRAINAGE(FunctionValueGroup.ENVIRONMENT, ClientTerms.DRAINAGE, -10, 10, UnitType.LENGTH),

    /**
     * When true function requires a zoning permit to be build
     */
    ZONING_PERMIT_REQUIRED(FunctionValueGroup.CONSTRUCTION, ClientTerms.ZONING_PERMIT_REQUIRED, 0, 1),

    /**
     * How high is the slanting roof, 0 = flat roof 1m gives a small slanting roof.
     */
    SLANTING_ROOF_HEIGHT(FunctionValueGroup.CONSTRUCTION, ClientTerms.SLANTING_ROOF_HEIGHT, 0, 10, UnitType.LENGTH),

    /**
     * When true function allows pipe items to be constructed under it.
     */
    PIPES_PERMITTED(FunctionValueGroup.PIPES, ClientTerms.PIPES_PERMITTED, 0, 1);

    public final static FunctionValue[] ACTIVE_VALUES;
    static {
        List<FunctionValue> types = new ArrayList<>();
        for (FunctionValue type : FunctionValue.values()) {
            Deprecated depAnno = ObjectUtils.getEnumAnnotation(type, Deprecated.class);
            if (depAnno == null) {
                types.add(type);
            }
        }
        ACTIVE_VALUES = types.toArray(new FunctionValue[0]);
    }

    private double minValue;
    private double maxValue;
    private ClientTerms description;
    private UnitType[] unitDimensions;
    private String editorName;
    private boolean monetary = false;
    private FunctionValueGroup group;

    private FunctionValue(FunctionValueGroup group, ClientTerms description, double minValue, double maxValue, boolean monetary,
            UnitType... unitDimensions) {
        this(group, null, description, minValue, maxValue, monetary, unitDimensions);
    }

    private FunctionValue(FunctionValueGroup group, ClientTerms description, double minValue, double maxValue, UnitType... unitDimensions) {
        this(group, description, minValue, maxValue, false, unitDimensions);
    }

    private FunctionValue(FunctionValueGroup group, String editorName, ClientTerms description, double minValue, double maxValue,
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
        if (this.equals(CONSTRUCTION_TIME_IN_MONTHS) || this.equals(DEMOLISH_TIME_IN_MONTHS)) {
            return "months";
        }

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
    public String getUnitValueFormatted(double siValue, UnitSystemType unitSystem) {
        return unitSystem.getImpl().toLocalValueWithFormatting(siValue, getUnitDimensions());
    }

    public boolean isMonetary() {
        return monetary;
    }
}
