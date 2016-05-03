/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.locale.unit.si;

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
public class SIUnitSystem extends UnitSystem {

    @Override
    protected UnitSystem create() {
        return new SIUnitSystem();
    }

    @Override
    protected NumberFormat getLocalNumberFormatter() {
        return NumberFormat.getInstance(Locale.GERMAN);
    }

    @Override
    protected LocalUnit getLocalUnit(UnitType unitDimensionType) {
        switch (unitDimensionType) {
            case LENGTH:
                return LengthSI.METRES;
            case VOLUME:
                return VolumeSI.CUBIC_METRES;
            case SURFACE:
                return SurfaceSI.SQUARE_METRES;
            case TEMPERATURE:
            case TEMPERATURE_RELATIVE:
                return TemperatureSI.DEGREES_CELSIUS;
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
                return LengthSI.METRES;
            case WATER_MM:
                return LengthSI.MILLIMETRES;
            case MAP_SIZE:
                return LengthSI.METRES;
            case NONE:
            default:
                break;
        }
        return NoUnitDimension.VALUES[0].getDefault();
    }

    @Override
    protected UnitSystemType getUnitSystem() {
        return UnitSystemType.SI;
    }
}
