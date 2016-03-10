/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.locale.unit;

/**
 *
 * @author Frank Baars
 *
 */
public enum UnitType {
    LENGTH(1), //
    SURFACE(0), //
    VOLUME(1), //
    TEMPERATURE(1), //
    TEMPERATURE_RELATIVE(2), //
    PERCENTAGE(1), //
    NONE(1), //
    HEAT_FLOW(1), //
    ENERGY(1), //
    AIR_POLLUTION(1), //
    WATER_MM(2), //
    DIAMETER(3), //
    POWER(3), //
    MAP_SIZE(0), //
    NOISE(1), //
    ;

    private int roundingDecimalPosition;
    private double significance;

    private UnitType(int roundingDecimalPosition) {
        this.roundingDecimalPosition = roundingDecimalPosition;
        this.significance = Math.pow(10, -1 * roundingDecimalPosition);
    }

    public int getRoundingDecimalPosition() {
        return roundingDecimalPosition;
    }

    public double getSignificance() {
        return significance;
    }
}
