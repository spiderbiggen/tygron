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
public enum LengthUSCustomary implements LocalUnit {
    INCHES("in", (1d / 12d), 0.999d), //
    FEET("ft", 1, 52800d), //
    YARDS("yd", 3, 52800d), //
    MILES("mi", 5280, Double.MAX_VALUE); //

    public static final double METRES_TO_FEET_MULTIPLIER_ACCURATE = 3.280839895013d;

    public static final LengthUSCustomary[] VALUES = LengthUSCustomary.values();

    private String postFix = StringUtils.EMPTY;
    private double relativeSingleUnitValue;
    private double maxValue;

    private LengthUSCustomary(String postFix, double singleUnitValue, double maxValue) {
        this.postFix = postFix;
        this.relativeSingleUnitValue = singleUnitValue;
        this.maxValue = maxValue;
    }

    @Override
    public LocalUnit getDefault() {
        return FEET;
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
        return amount * METRES_TO_FEET_MULTIPLIER_ACCURATE;
    }

    @Override
    public double toSIValue(double amount) {
        return amount / METRES_TO_FEET_MULTIPLIER_ACCURATE;
    }
}
