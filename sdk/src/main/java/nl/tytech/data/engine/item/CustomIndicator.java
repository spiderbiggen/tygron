/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.EditOptions;
import nl.tytech.data.engine.serializable.CalculationSpaceType;
import nl.tytech.data.engine.serializable.TargetDescription;
import nl.tytech.locale.unit.UnitType;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;

/**
 * Custom Indicators
 *
 * @author Maxim Knepfle
 */
public class CustomIndicator extends Indicator {

    public enum CustomIndicatorType implements TypeInterface {

        QUALITATIVE(EditOptions.GREEN, TColor.CYAN, 0, new TargetDescription[] { new TargetDescription(
                "Target amount of points. Using zero means that the max achievable amount of points is used.", 0, -Double.MAX_VALUE,
                Double.MAX_VALUE, UnitType.NONE) }, false, false),

        EXCEL(EditOptions.GREEN, TColor.CYAN, 0, new TargetDescription[] { new TargetDescription(
                "This value is ignored, handled by Excelsheet.", 0, -Double.MAX_VALUE, Double.MAX_VALUE, UnitType.NONE) }, false, false),

        HEAT_EXCEL(EditOptions.HEAT, TColor.CYAN, 0, new TargetDescription[] { new TargetDescription(
                "This value is ignored, handled by Excelsheet.", 0, -Double.MAX_VALUE, Double.MAX_VALUE, UnitType.NONE) }, false, false),

        API_POST(EditOptions.GREEN, TColor.WHITE, 0, new TargetDescription[] { new TargetDescription(
                "This value is ignored, handled by API call.", 0, -Double.MAX_VALUE, Double.MAX_VALUE, UnitType.NONE) }, false, false);

        public static CustomIndicatorType[] getActiveValues(EditOptions userZone) {

            List<CustomIndicatorType> types = new ArrayList<>();
            for (CustomIndicatorType type : CustomIndicatorType.values()) {
                if (type.editZone.ordinal() <= userZone.ordinal()) {
                    types.add(type);
                }
            }
            return types.toArray(new CustomIndicatorType[0]);
        }

        public static boolean isExcelType(TypeInterface type) {
            return type == HEAT_EXCEL || type == EXCEL;
        }

        private EditOptions editZone;

        private int scoreVariables;

        private TargetDescription[] targetDescriptions;

        private TColor color;

        private boolean isSingleInstance;

        private CustomIndicatorType(EditOptions editZone, TColor color, int scoreVariables, TargetDescription[] targets, boolean threaded,
                boolean isSingleInstance) {

            this.editZone = editZone;
            this.scoreVariables = scoreVariables;
            this.targetDescriptions = targets;
            this.color = color;
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

    public final static double QUALITATIVE_SCORE_TILE_M2 = 100d;

    /**
     *
     */
    private static final long serialVersionUID = -3312131928668156362L;

    @XMLValue
    private CalculationSpaceType calculationType = CalculationSpaceType.SURFACE_SPACE;

    @XMLValue
    private String customUnitDimension = "points";

    public CustomIndicator() {

    }

    public CalculationSpaceType getCalculationType() {
        return calculationType;
    }

    public String getCustomUnitDimension() {
        return customUnitDimension;
    }

    @Override
    public TypeInterface getType() {
        return CustomIndicatorType.QUALITATIVE;
    }

    public void setCalculationType(CalculationSpaceType calculationType) {
        this.calculationType = calculationType;
    }

    public void setCustomUnitDimension(String customUnitDimension) {
        this.customUnitDimension = customUnitDimension;
    }
}
