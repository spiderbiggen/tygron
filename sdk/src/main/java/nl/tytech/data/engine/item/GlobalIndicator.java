/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nl.tytech.core.item.annotations.DoNotSaveToInit;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.EditOptions;
import nl.tytech.data.engine.serializable.FunctionValue;
import nl.tytech.data.engine.serializable.MapType;
import nl.tytech.data.engine.serializable.TargetDescription;
import nl.tytech.locale.unit.UnitType;
import nl.tytech.util.ObjectUtils;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;

/**
 * Indicators that have the same value for every Stakeholder
 * @author Maxim Knepfle
 *
 */
public class GlobalIndicator extends Indicator {

    public enum GlobalIndicatorType implements TypeInterface {

        LIVABILITY(EditOptions.GREEN, TColor.PINK, 1, new TargetDescription[] { new TargetDescription("Point average required per zone.",
                1, FunctionValue.LIVABILITY_EFFECT.getMinValue(), FunctionValue.LIVABILITY_EFFECT.getMaxValue(), UnitType.NONE) }, true),

        WATER_STORAGE(EditOptions.GREEN, TColor.BLUE, 2, new TargetDescription[] {
                new TargetDescription("Max allowed innovative (greenroofs, etc) percentage of the storage. ", 30, 0, 100,
                        UnitType.PERCENTAGE),
                new TargetDescription("Total amount of water storage (polder water or innovative measures) that has to be achieved. ",
                        20000, 0, Double.MAX_VALUE, UnitType.VOLUME) }, true),

        GREEN(EditOptions.GREEN, TColor.GREEN, 2, new TargetDescription[] { new TargetDescription(
                "Amount of green area in %s per house. Higher is better.", 75, 0, Double.MAX_VALUE, UnitType.SURFACE) }, true),

        HEAT(EditOptions.GREEN, TColor.ORANGE, 1, new TargetDescription[] { new TargetDescription(
                "On a summer day the zone should not be X degrees %s hotter then the average.", 0, FunctionValue.HEAT_EFFECT.getMinValue(),
                FunctionValue.HEAT_EFFECT.getMaxValue(), UnitType.TEMPERATURE_RELATIVE) }, true),

        PARKING(EditOptions.GREEN, TColor.YELLOW, 2, new TargetDescription[] { new TargetDescription(
                "Parking compliance rate. Higher means more parking space is required.", 1, 0, Double.MAX_VALUE, UnitType.NONE) }, true),

        @Deprecated
        DEPRECATED(EditOptions.RED, null, 0, null, true),

        ;

        public static GlobalIndicatorType[] getActiveValues() {

            List<GlobalIndicatorType> types = new ArrayList<GlobalIndicator.GlobalIndicatorType>();
            for (GlobalIndicatorType type : GlobalIndicatorType.values()) {
                Deprecated depAnno = ObjectUtils.getEnumAnnotation(type, Deprecated.class);
                if (depAnno == null) {
                    types.add(type);
                }
            }
            return types.toArray(new GlobalIndicatorType[0]);
        }

        public static GlobalIndicatorType[] getActiveValues(EditOptions userZone) {

            List<GlobalIndicatorType> types = new ArrayList<GlobalIndicator.GlobalIndicatorType>();
            for (GlobalIndicatorType type : GlobalIndicatorType.values()) {
                if (type.userZone.ordinal() <= userZone.ordinal()) {
                    Deprecated depAnno = ObjectUtils.getEnumAnnotation(type, Deprecated.class);
                    if (depAnno == null) {
                        types.add(type);
                    }
                }
            }
            return types.toArray(new GlobalIndicatorType[0]);
        }

        private int scoreVariables;

        private TargetDescription[] targetDescriptions;

        private TColor color;

        private EditOptions userZone;

        private boolean isSingleInstance;

        private GlobalIndicatorType(EditOptions userZone, TColor color, int scoreVariables, TargetDescription[] targets,
                boolean isSingleInstance) {
            this.scoreVariables = scoreVariables;
            this.targetDescriptions = targets;
            this.color = color;
            this.userZone = userZone;
            this.isSingleInstance = isSingleInstance;
        }

        @Override
        public TColor getDefaultColor() {
            return color;
        }

        @Override
        public double[] getDefaultTargetsCopy() {

            double[] targets = new double[this.targetDescriptions.length];
            for (int i = 0; i < targets.length; i++) {
                targets[i] = targetDescriptions[i].getDefaultValue();
            }
            return targets;
        }

        @Override
        public String getHumanString() {
            return StringUtils.capitalizeWithSpacedUnderScores(this.name());
        }

        public int getScoreVariables() {
            return scoreVariables;
        }

        @Override
        public TargetDescription[] getTargetDescriptions() {
            return targetDescriptions;
        }

        @Override
        public boolean isGlobal() {
            return true;
        }

        @Override
        public boolean isSingleInstance() {
            return isSingleInstance;
        }

    }

    /**
     *
     */
    private static final long serialVersionUID = -8549122571363354770L;

    @DoNotSaveToInit
    @XMLValue
    private HashMap<Integer, Double> maquetteZoneScores = new HashMap<>();

    @DoNotSaveToInit
    @XMLValue
    private HashMap<Integer, Double> currentZoneScores = new HashMap<>();

    @XMLValue
    private GlobalIndicatorType type = null;

    @Override
    public TypeInterface getType() {
        return type;
    }

    public double getZoneAmount() {
        return maquetteZoneScores.size();
    }

    public double getZoneScore(MapType mapType, Integer neighbourhoodID) {
        Map<Integer, Double> scores = getZoneScores(mapType);

        if (!scores.containsKey(neighbourhoodID)) {
            return 0f;
        }
        return scores.get(neighbourhoodID);
    }

    private Map<Integer, Double> getZoneScores(MapType mapType) {
        switch (mapType) {
            case CURRENT:
                return currentZoneScores;
            case MAQUETTE:
            default:
                return maquetteZoneScores;
        }
    }

    @Override
    public void resetStartOfLevelValues() {
        super.resetStartOfLevelValues();
        this.maquetteZoneScores.clear();
        this.currentZoneScores.clear();
    }

    public void setType(GlobalIndicatorType type) {
        this.type = type;
    }

    public void setZoneTotalScore(MapType mapType, Integer neighbourhoodID, double score) {
        getZoneScores(mapType).put(neighbourhoodID, score);

    }
}
