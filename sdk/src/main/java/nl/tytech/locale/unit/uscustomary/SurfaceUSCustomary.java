/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.locale.unit.uscustomary;

import nl.tytech.locale.unit.LocalUnit;
import nl.tytech.util.StringUtils;

/**
 *
 * @author Frank Baars
 *
 */
public enum SurfaceUSCustomary implements LocalUnit {
    SQUARE_SURVEY_FEET("sq ft", 1, 435600), //
    // HECTARE("ha"),
    ACRES("ac", 43560, Double.MAX_VALUE); //

    private static final double SQUARE_METRES_TO_SQUARE_FEET_MULTIPLIER = 10.7639104167d;

    public static final SurfaceUSCustomary[] VALUES = SurfaceUSCustomary.values();

    private String postFix = StringUtils.EMPTY;
    private double relativeSingleUnitValue;
    private double maxValue;

    private SurfaceUSCustomary(String postFix, double singleUnitValue, double maxValue) {
        this.postFix = postFix;
        this.relativeSingleUnitValue = singleUnitValue;
        this.maxValue = maxValue;
    }

    @Override
    public LocalUnit getDefault() {
        return SQUARE_SURVEY_FEET;
    }

    @Override
    public double getMaxValue() {
        return maxValue;
    }

    @Override
    public String getPostFix() {
        return postFix;
    }

    @Override
    public double getRelativeSingleUnitValue() {
        return relativeSingleUnitValue;
    }

    @Override
    public LocalUnit[] getValues() {
        return VALUES;
    }

    @Override
    public double toLocalValue(double amount) {
        return amount * SQUARE_METRES_TO_SQUARE_FEET_MULTIPLIER;
    }

    @Override
    public double toSIValue(double amount) {
        return amount / SQUARE_METRES_TO_SQUARE_FEET_MULTIPLIER;
    }
}
