/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.locale.unit;

import java.text.NumberFormat;
import java.text.ParseException;
import nl.tytech.locale.TCurrency;
import nl.tytech.util.MathUtils;
import nl.tytech.util.StringUtils;

/**
 *
 * @author Frank Baars
 *
 */
public abstract class UnitSystem {

    public final static String SEPERATOR = " / ";

    protected abstract UnitSystem create();

    public String formatLocalValue(double localValue) {
        return getLocalNumberFormatter().format(localValue);
    }

    public String formatLocalValue(double localValue, int maxDecimals) {
        return getLocalNumberFormatter(maxDecimals).format(localValue);
    }

    public String formatLocalValue(double localValue, int minDecimals, int maxDecimals) {
        return getLocalNumberFormatter(minDecimals, maxDecimals).format(localValue);
    }

    public String formatLocalValue(Number number) {
        return getLocalNumberFormatter().format(number);
    }

    protected abstract NumberFormat getLocalNumberFormatter();

    private NumberFormat getLocalNumberFormatter(int decimals) {
        NumberFormat nf = getLocalNumberFormatter();
        nf.setMaximumFractionDigits(decimals);
        return nf;
    }

    private NumberFormat getLocalNumberFormatter(int minDecimals, int maxDecimals) {
        NumberFormat nf = getLocalNumberFormatter(maxDecimals);
        nf.setMinimumFractionDigits(minDecimals);
        return nf;
    }

    protected abstract LocalUnit getLocalUnit(UnitType unit);

    private double getLocalValueWithAdjustedNotation(double siValue, LocalUnit localUnit) {
        return localUnit.toLocalValue(siValue) / localUnit.getRelativeSingleUnitValue();
    }

    private LocalUnit getSignificantLocalUnit(double siValue, UnitType units) {

        LocalUnit localUnit = getLocalUnit(units);
        siValue = Math.abs(siValue);
        for (LocalUnit significantOrder : localUnit.getValues()) {
            if (Math.abs(siValue) < significantOrder.getMaxValue()) {
                return significantOrder;
            }
        }
        return localUnit.getDefault();
    }

    public String getUnitAbbreviation(TCurrency currency, UnitType... units) {
        return currency.getCurrencyCharacter() + getUnitAbbreviation(units);
    }

    public String getUnitAbbreviation(UnitType... units) {

        if (units == null) {
            throw new IllegalArgumentException("Missing units array!");
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < units.length; i++) {
            if (i == 0) {
                result.append(getLocalUnit(units[i]).getPostFix());
            } else {
                result.append(SEPERATOR + getLocalUnit(units[i]).getPostFix());
            }
        }
        return result.toString();
    }

    protected abstract UnitSystemType getUnitSystem();

    public double parseDouble(String text) throws ParseException {
        return getLocalNumberFormatter().parse(text).doubleValue();
    }

    public double parseDouble(String text, int decimals) throws ParseException {
        return getLocalNumberFormatter(decimals).parse(text).doubleValue();
    }

    public float parseFloat(String text) throws ParseException {
        return getLocalNumberFormatter().parse(text).floatValue();
    }

    public float parseFloat(String text, int decimals) throws ParseException {
        return getLocalNumberFormatter(decimals).parse(text).floatValue();
    }

    public int parseInt(String text) throws ParseException {
        return getLocalNumberFormatter().parse(text).intValue();
    }

    public long parseLong(String text) throws ParseException {
        return getLocalNumberFormatter().parse(text).longValue();
    }

    public boolean significantlyDifferent(double value1, double value2, UnitType... units) {

        double significance = units.length > 0 ? Double.MAX_VALUE : UnitType.NONE.getSignificance();
        for (UnitType unit : units) {
            significance = Math.min(unit.getSignificance(), significance);
        }
        return Math.abs(value1 - value2) > significance;
    }

    public double toLocalValue(double siValue, boolean round, boolean useSignificantLocalUnit, UnitType unitDimensionType) {
        double value = toLocalValue(siValue, useSignificantLocalUnit, unitDimensionType);
        return (round ? MathUtils.round(value, unitDimensionType.getRoundingDecimalPosition()) : value);
    }

    private double toLocalValue(double siValue, boolean useSignificantLocalUnit, UnitType unitDimensionType) {

        LocalUnit unitDimension = useSignificantLocalUnit ? getSignificantLocalUnit(siValue, unitDimensionType)
                : getLocalUnit(unitDimensionType);
        return getLocalValueWithAdjustedNotation(siValue, unitDimension);
    }

    public double toLocalValue(double siValue, boolean round, UnitType... units) {
        double value = toLocalValue(siValue, units);
        int decimals = 1;
        if (units.length > 0) {
            decimals = units[0].getRoundingDecimalPosition();
        }
        return (round ? MathUtils.round(value, decimals) : value);
    }

    public double toLocalValue(double siValue, int decimals, boolean useSignificantLocalUnit, UnitType unitDimensionType) {
        double value = toLocalValue(siValue, useSignificantLocalUnit, unitDimensionType);
        return MathUtils.round(value, decimals);
    }

    public double toLocalValue(double siValue, int decimals, UnitType... units) {
        return MathUtils.round(toLocalValue(siValue, units), decimals);
    }

    public double toLocalValue(double siValue, UnitType... units) {

        if (units == null) {
            throw new IllegalArgumentException("Missing units array!");
        }

        double value = siValue;

        for (int i = 0; i < units.length; i++) {
            LocalUnit localUnit = getLocalUnit(units[i]);

            if (i == 0) {
                value = getLocalValueWithAdjustedNotation(siValue, localUnit);
            } else {
                value /= getLocalValueWithAdjustedNotation(1d, localUnit);
            }
        }
        return value;
    }

    public String toLocalValueWithFormatting(double siValue, boolean round, boolean useSignificantLocalUnit, UnitType unit) {
        return formatLocalValue(toLocalValue(siValue, round, useSignificantLocalUnit, unit));
    }

    public String toLocalValueWithFormatting(double siValue, int maxDecimals, boolean useSignificantLocalUnit, UnitType unit) {
        return formatLocalValue(toLocalValue(siValue, maxDecimals, useSignificantLocalUnit, unit), maxDecimals);
    }

    public String toLocalValueWithFormatting(double siValue, int maxDecimals, UnitType... units) {
        return formatLocalValue(toLocalValue(siValue, maxDecimals, units), maxDecimals);
    }

    public String toLocalValueWithFormatting(double siValue, UnitType... units) {
        return formatLocalValue(toLocalValue(siValue, true, units));
    }

    public String toLocalValueWithUnit(double siValue, boolean round, boolean useSignificantLocalUnit, UnitType unit) {
        return toLocalValueWithFormatting(siValue, round, useSignificantLocalUnit, unit) + StringUtils.WHITESPACE
                + getSignificantLocalUnit(siValue, unit).getPostFix();
    }

    public String toLocalValueWithUnit(double siValue, int decimals, boolean useSignificantLocalUnit, UnitType unit) {
        return toLocalValueWithFormatting(siValue, decimals, useSignificantLocalUnit, unit) + StringUtils.WHITESPACE
                + getSignificantLocalUnit(siValue, unit).getPostFix();

    }

    public String toLocalValueWithUnit(double siValue, UnitType... units) {
        return toLocalValueWithFormatting(siValue, units) + StringUtils.WHITESPACE + getUnitAbbreviation(units);
    }

    public String toSignedLocalValue(double siValue) {

        String result = toLocalValueWithUnit(siValue, UnitType.NONE);
        if (siValue > 0) {
            return "+" + result;
        }
        return result;
    }

    public String toSignedLocalValue(double siValue, UnitType... units) {

        String result = toLocalValueWithUnit(siValue, units);
        if (siValue > 0) {
            return "+" + result;
        }
        return result;
    }

    public double toSIValue(double localValue, int decimals, UnitType... units) {
        return MathUtils.round(toSIValue(localValue, units), decimals);
    }

    public double toSIValue(double localValue, UnitType... units) {

        if (units == null) {
            throw new IllegalArgumentException("Missing units array!");
        }

        double newValue = localValue;
        for (int i = 0; i < units.length; i++) {
            LocalUnit unitDimension = getLocalUnit(units[i]);
            if (i == 0) {
                newValue = toSIValueWithAdjustedNotation(localValue, unitDimension);
            } else {
                newValue /= toSIValueWithAdjustedNotation(1d, unitDimension);
            }
        }
        return newValue;
    }

    private double toSIValueWithAdjustedNotation(double localValue, LocalUnit unit) {
        return unit.toSIValue(localValue) * unit.getRelativeSingleUnitValue();
    }
}
