/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.core.item;

import nl.tytech.core.item.annotations.DoNotSaveToInit;
import nl.tytech.core.item.annotations.EnumHtml;
import nl.tytech.core.net.serializable.SettingType;
import nl.tytech.data.core.item.Moment.SimTime;

/**
 * SimTimeSetting
 * <p>
 * SimTimeSetting keeps a settings related to the simulation time.
 * <p>
 *
 *
 * @author Maxim Knepfle
 */

public class SimTimeSetting extends AbstractSetting<SimTimeSetting.Type> {

    // (Frank) This enumerator is used by an EnumOrderedItem. Please add new enumerator-values at the end of this enumerator.
    @EnumHtml(ordinal = { 6 })
    public enum Type implements SettingType {

        /** Stores the index of the current round **/
        @DoNotSaveToInit
        INDEX_CURRENT_ROUND(Integer.class, Moment.FIRST_STOP_POSTION + ""),

        /** Max multiplier for 100% speed. */
        MAX_TIME_MULTIPLIER(Integer.class, "1000000"),

        /** If true the simtime is paused. */
        @DoNotSaveToInit
        PAUSED(Boolean.class, "true"),

        /**
         * Percentage speed from 1-100
         */
        SPEED_PERCENTAGE(Double.class, "41"),

        /**
         * The game time is multiplied by this value in comparison to real-life time. e.g. value: 2 means simtime runs twice as fast as real
         * time.
         */
        TIME_MULTIPLIER(Integer.class, "131400"),

        /**
         * The type of simulation time.
         */
        TYPE(SimTime.class, "PLANNING"),

        /**
         * Duration of the session
         */
        SESSION_DURATION_HOURS(Double.class, "4.0"),

        /**
         * When true simtime is running in a editor test run
         */
        @DoNotSaveToInit
        TESTRUN_ACTIVE(Boolean.class, "false");

        private String defaultValue;
        private Class<?> valueType;

        private Type(Class<?> valueType, String defaultValue) {
            this.valueType = valueType;
            this.defaultValue = defaultValue;
        }

        @Override
        public String getDefaultValue() {
            return this.defaultValue;
        }

        @Override
        public Class<?> getValueType() {
            return this.valueType;
        }
    }

    private static final long serialVersionUID = 3730370813978282986L;

    @Override
    public Type[] getEnumValues() {
        return Type.values();
    }
}
