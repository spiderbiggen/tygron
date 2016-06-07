/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.locale;

import nl.tytech.locale.unit.UnitSystem;
import nl.tytech.util.MathUtils;
import nl.tytech.util.StringUtils;

/**
 * @author Frank Baars
 *
 */
public enum CurrencyOrder {

    CENTS(1d, 2, 0, StringUtils.EMPTY), //
    WHOLE_NUMBERS(1000d, 2, 0, "-"), //
    THOUSANDS(1000000d, 1, 3, "K"), //
    MILLIONS(Double.MAX_VALUE, 1, 6, "M"); //

    public static final CurrencyOrder[] VALUES = CurrencyOrder.values();

    public static CurrencyOrder getSignificantOrder(double amount) {
        for (CurrencyOrder significantOrder : VALUES) {
            if (amount < significantOrder.getMaxValue()) {
                return significantOrder;
            }
        }

        return MILLIONS;
    }

    private double maxValue;
    private int roundingDecimalPosition;
    private String postFix;

    private int power;

    CurrencyOrder(double maxValue, int roundingDecimalPosition, int power, String postFix) {
        this.maxValue = maxValue;
        this.roundingDecimalPosition = roundingDecimalPosition;
        this.power = power;
        this.postFix = postFix;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public double getNumberWithAdjustedNotation(double amount) {
        if (amount == 0d) {
            return 0d;
        }
        amount = MathUtils.round(amount, -power + roundingDecimalPosition);
        amount = amount / (Math.pow(10, getPower()));

        return amount;

    }

    public String getNumberWithPostFix(double amount, UnitSystem unitSystem) {
        String formattedValue = unitSystem.formatLocalValue(amount, getRoundingDecimalPosition(), getRoundingDecimalPosition());

        switch (this) {
            case CENTS:
            case THOUSANDS:
            case MILLIONS:
                return formattedValue + this.postFix;

            case WHOLE_NUMBERS:
            default:
                if (formattedValue.length() > 3) {
                    return unitSystem.formatLocalValue(amount, 0, 0) + formattedValue.charAt(formattedValue.length() - 3) + this.postFix;
                }
                return formattedValue;
        }
    }

    public int getPower() {
        return power;
    }

    public int getRoundingDecimalPosition() {
        return roundingDecimalPosition;
    }
}
