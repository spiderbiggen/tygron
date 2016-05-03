/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/

/**
 * @author Jurrian Hartveldt
 */
package nl.tytech.locale.unit;

import nl.tytech.locale.unit.imperial.ImperialUnitSystem;
import nl.tytech.locale.unit.si.SIUnitSystem;
import nl.tytech.locale.unit.uscustomary.USCustomaryUnitSystem;
import nl.tytech.util.SkipObfuscation;

/**
 * @author Frank Baars
 */
public enum UnitSystemType implements SkipObfuscation {

    /**
     * British Imperial Measurement system
     */
    BRITISH_IMPERIAL("British Imperial", new ImperialUnitSystem()),

    /**
     * SI: International System of Units
     */
    SI("International", new SIUnitSystem()),

    /**
     * US Customary
     */
    US_CUSTOMARY("US Customary", new USCustomaryUnitSystem());

    public static final UnitSystemType[] VALUES = values();
    private String name;

    private UnitSystem impl;

    private UnitSystemType(String name, UnitSystem impl) {
        this.name = name;
        this.impl = impl;
    }

    public UnitSystem getImpl() {
        return impl;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
