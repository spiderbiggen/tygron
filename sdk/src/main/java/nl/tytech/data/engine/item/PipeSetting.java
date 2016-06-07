/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import nl.tytech.core.item.annotations.DoNotSaveToInit;
import nl.tytech.core.net.serializable.SettingType;
import nl.tytech.data.core.item.AbstractSetting;

/**
 * Heat Setting
 * <p>
 * heat Setting keep only setting related to heat model.
 * <p>
 *
 *
 * @author Maxim Knepfle
 */

public class PipeSetting extends AbstractSetting<PipeSetting.Type> {

    public static enum CalculationModel {

        NONE, HEAT
    }

    public enum Type implements SettingType {

        CALCULATION_MODEL(CalculationModel.class, CalculationModel.NONE.name()),

        EXCEL_MODEL(String.class, ""),

        DEFAULT_NIF(String.class, "default.nif"),

        HEAD_JUCTION(Integer.class, "-1"),

        NETWORK_OWNER_ID(Integer.class, "-1"),

        NETWORK_FINANCING_INTREST(Double.class, "0.05"),

        NETWORK_CONNECTION_FEE_PERCENTAGE(Double.class, "0.20"),

        NETWORK_WRITE_OFF_PERIOD_YEARS(Integer.class, "30"),

        POLLUTION_UNIT_NAME(String.class, "Ton CO2/jaar"),

        CONCURRENCY_FACTOR(Double.class, "0.6"),

        FLOW_UNIT(String.class, "KG/sec"),

        CLUSTER_FRACTION_CONNECTED(Double.class, "1.0"),

        ADJUSTABLE_TYPES(
                Type.class,
                "BAK_VALUE TRANSPORT_FIXED TRANSPORT_VAR HEAT_RETURN_ON_INVESTMENT GENERATED_POWER HEAT_ENERGY_BUY_PRICE HEAT_GAS_BUY_PRICE HEAT_PRICE HEAT_VAR_GAS_PRICE HEAT_FIXED_GAS_PRICE AVAILABILITY"),

        REQUIRE_UTILITY_CORPORATION_APPROVAL(Boolean.class, "true"),

        /**
         * DYNAMIC HEAT STUFF FROM HERE
         */

        @DoNotSaveToInit
        TOTAL_CONNECTED_DEMAND(Double.class, "0.0"),

        @DoNotSaveToInit
        LOST_CONTENT_PERCENTAGE(Double.class, "0.0"),

        @DoNotSaveToInit
        POLLUTION_REDUCTION(Double.class, "0.0"),

        @DoNotSaveToInit
        PRESSURE_DROP(Double.class, "0.0"),

        @DoNotSaveToInit
        HEAD_FLOW(Double.class, "0.0"),

        @DoNotSaveToInit
        FLOW_COST(Double.class, "1.0"),

        @DoNotSaveToInit
        INCOME_NETCONTROLLER(Double.class, "0.0"),

        @DoNotSaveToInit
        INCOME_PRODUCERS(Double.class, "0.0"),

        @DoNotSaveToInit
        INCOME_SUPPLIER(Double.class, "0.0"),

        @DoNotSaveToInit
        INCOME_CONSUMERS(Double.class, "0.0"),

        @DoNotSaveToInit
        TOTAL_INVESTMENT(Double.class, "0.0"),

        @DoNotSaveToInit
        TOTAL_INVESTMENT_YEAR(Double.class, "0.0"),

        @DoNotSaveToInit
        OLD_CONTENT_REDUCTION(Double.class, "0.0"),

        @DoNotSaveToInit
        BAK_VALUE(Double.class, "1700"),

        @DoNotSaveToInit
        BAK_VALUE_ADVISED(Double.class, "1700"),

        @DoNotSaveToInit
        TRANSPORT_VAR(Double.class, "37"),

        @DoNotSaveToInit
        TRANSPORT_FIXED(Double.class, "230"),

        @DoNotSaveToInit
        HEAT_SESSION(String.class, ""),

        @DoNotSaveToInit
        HEAT_PRICE(Double.class, "6.5"),

        @DoNotSaveToInit
        HEAT_VAR_GAS_PRICE(Double.class, "0.4767"),

        @DoNotSaveToInit
        HEAT_FIXED_GAS_PRICE(Double.class, "230"),

        @DoNotSaveToInit
        HEAT_GAS_USAGE_HOUSE(Double.class, "1220"),

        @DoNotSaveToInit
        AVAILABILITY(Double.class, "0.98"),

        @DoNotSaveToInit
        GENERATED_POWER(Double.class, "20"),

        @DoNotSaveToInit
        HEAT_ENERGY_BUY_PRICE(Double.class, "5.0"),

        @DoNotSaveToInit
        HEAT_GAS_BUY_PRICE(Double.class, "26.47"),

        @DoNotSaveToInit
        HEAT_RETURN_ON_INVESTMENT(Double.class, "0.08358"), ;

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

    private static final long serialVersionUID = 3730370813278282986L;
    public static final String HEAT_FILES = "Heat/";
    public static final String DEFAULT_HEAT_FILES = HEAT_FILES + "Default/";
    public static final String HEAT_EXCEL_DIR = HEAT_FILES + "Excel/";

    public static final PipeSetting.Type[] ADJUSTABLES = { Type.BAK_VALUE, Type.TRANSPORT_FIXED, Type.TRANSPORT_VAR,
        Type.HEAT_RETURN_ON_INVESTMENT, Type.GENERATED_POWER, Type.HEAT_ENERGY_BUY_PRICE, Type.HEAT_GAS_BUY_PRICE, Type.HEAT_PRICE,
        Type.HEAT_VAR_GAS_PRICE, Type.HEAT_FIXED_GAS_PRICE, Type.AVAILABILITY };

    @Override
    public Type[] getEnumValues() {
        return Type.values();
    }
}
