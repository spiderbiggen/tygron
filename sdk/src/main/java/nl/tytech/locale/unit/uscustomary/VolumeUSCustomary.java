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
public enum VolumeUSCustomary implements LocalUnit {
    CUBIC_FEET("cubic ft", 1, Double.MAX_VALUE);

    private static final double CUBIC_METRES_TO_CUBIC_FEET_MULTIPLIER_ACCURATE = 35.31466672149d;

    public static final VolumeUSCustomary[] VALUES = VolumeUSCustomary.values();

    private String postFix = StringUtils.EMPTY;
    private double relativeSingleUnitValue;
    private double maxValue;

    private VolumeUSCustomary(String postFix, double singleUnitValue, double maxValue) {
        this.postFix = postFix;
        this.relativeSingleUnitValue = singleUnitValue;
        this.maxValue = maxValue;
    }

    @Override
    public LocalUnit getDefault() {
        return CUBIC_FEET;
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
        return amount * CUBIC_METRES_TO_CUBIC_FEET_MULTIPLIER_ACCURATE;
    }

    @Override
    public double toSIValue(double amount) {
        return amount / CUBIC_METRES_TO_CUBIC_FEET_MULTIPLIER_ACCURATE;
    }
}
