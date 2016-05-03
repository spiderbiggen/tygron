/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.locale.unit.generic;

import nl.tytech.locale.unit.LocalUnit;
import nl.tytech.util.StringUtils;

/**
 *
 * @author Frank Baars
 *
 */
public enum NoUnitDimension implements LocalUnit {
    NONE(StringUtils.EMPTY, 1, Double.MAX_VALUE);

    public static NoUnitDimension[] VALUES = NoUnitDimension.values();

    private String postFix = StringUtils.EMPTY;
    private double relativeSingleUnitValue;
    private double maxValue;

    private NoUnitDimension(String postFix, double singleUnitValue, double maxValue) {
        this.postFix = postFix;
        this.relativeSingleUnitValue = singleUnitValue;
        this.maxValue = maxValue;
    }

    @Override
    public LocalUnit getDefault() {
        return NONE;
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
        return amount;
    }

    @Override
    public double toSIValue(double amount) {
        return amount;
    }
}
