/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.locale.unit.imperial;

import java.text.NumberFormat;
import java.util.Locale;
import nl.tytech.locale.unit.UnitSystem;
import nl.tytech.locale.unit.UnitSystemType;
import nl.tytech.locale.unit.uscustomary.USCustomaryUnitSystem;

/**
 *
 * @author Frank Baars
 *
 */
public class ImperialUnitSystem extends USCustomaryUnitSystem {

    @Override
    protected UnitSystem create() {
        return new ImperialUnitSystem();
    }

    @Override
    protected NumberFormat getLocalNumberFormatter() {
        return NumberFormat.getInstance(Locale.ENGLISH);
    }

    @Override
    protected UnitSystemType getUnitSystem() {
        return UnitSystemType.BRITISH_IMPERIAL;
    }
}
