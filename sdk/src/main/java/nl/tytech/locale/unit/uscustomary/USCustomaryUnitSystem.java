/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.locale.unit.uscustomary;

import java.text.NumberFormat;
import java.util.Locale;
import nl.tytech.locale.unit.LocalUnit;
import nl.tytech.locale.unit.UnitSystem;
import nl.tytech.locale.unit.UnitSystemType;
import nl.tytech.locale.unit.UnitType;
import nl.tytech.locale.unit.generic.AirPollution;
import nl.tytech.locale.unit.generic.Energy;
import nl.tytech.locale.unit.generic.HeatFlow;
import nl.tytech.locale.unit.generic.NoUnitDimension;
import nl.tytech.locale.unit.generic.Noise;
import nl.tytech.locale.unit.generic.Percentage;
import nl.tytech.locale.unit.generic.Power;

/**
 *
 * @author Frank Baars
 *
 */
public class USCustomaryUnitSystem extends UnitSystem {

    @Override
    protected UnitSystem create() {
        return new USCustomaryUnitSystem();
    }

    @Override
    protected NumberFormat getLocalNumberFormatter() {
        return NumberFormat.getInstance(Locale.US);
    }

    @Override
    protected LocalUnit getLocalUnit(UnitType unitDimensionType) {

        switch (unitDimensionType) {
            case LENGTH:
                return LengthUSCustomary.FEET;
            case VOLUME:
                return VolumeUSCustomary.CUBIC_FEET;
            case SURFACE:
                return SurfaceUSCustomary.SQUARE_SURVEY_FEET;
            case TEMPERATURE:
                return TemperatureUSCustomary.DEGREES_FAHRENHEIT;
            case TEMPERATURE_RELATIVE:
                return RelativeTemperatureUSCustomary.DEGREES_FAHRENHEIT;
            case PERCENTAGE:
                return Percentage.PERCENTAGE;
            case HEAT_FLOW:
                return HeatFlow.HEAT_FLOW;
            case ENERGY:
                return Energy.ENERGY;
            case POWER:
                return Power.KILO_WATT;
            case NOISE:
                return Noise.DECIBEL;
            case AIR_POLLUTION:
                return AirPollution.AIR_POLLUTION;
            case DIAMETER:
                return LengthUSCustomary.INCHES;
            case WATER_MM:
                return LengthUSCustomary.INCHES;
            case MAP_SIZE:
                return LengthUSCustomary.YARDS;
            case NONE:
            default:
                break;
        }
        return NoUnitDimension.NONE;
    }

    @Override
    protected UnitSystemType getUnitSystem() {
        return UnitSystemType.US_CUSTOMARY;
    }
}
