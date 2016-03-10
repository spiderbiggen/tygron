/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.core.item;

import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;
import nl.tytech.util.logger.TLogger;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.io.WKTReader;

/**
 * Setting
 * <p>
 * This Item can be used to save settings. These setting can differ in type and thus are all stored as Strings.
 * <p>
 *
 *
 * @author Maxim Knepfle
 */
public abstract class AbstractSetting<E extends Enum<E>> extends EnumOrderedItem<E> {

    public static final long serialVersionUID = 5772008764384172107L;

    @XMLValue
    private String value = StringUtils.EMPTY;

    public AbstractSetting() {

    }

    public final boolean getBooleanValue() {

        try {
            return Boolean.parseBoolean(getValue());
        } catch (Exception exp) {
            TLogger.exception(exp);
            return false;
        }
    }

    public final TColor getColorValue() {

        try {
            int[] data = this.getIntArrayValue();
            return new TColor(data[0], data[1], data[2], data[3]);
        } catch (Exception exp) {
            TLogger.exception(exp, "Failed on Color value: " + getValue());
            return TColor.BLACK;
        }
    }

    @Override
    public final String getDescription() {
        return this.getValue();
    }

    public final double[] getDoubleArrayValue() {

        try {
            if (StringUtils.EMPTY.equals(getValue())) {
                return new double[0];
            }
            final String[] split = StringUtils.SEPARATOR.split(getValue());
            double[] array = new double[split.length];
            for (int i = 0; i < split.length; i++) {
                if (!split[i].equals(StringUtils.EMPTY)) {
                    array[i] = StringUtils.parse(split[i]).doubleValue();
                }
            }
            return array;
        } catch (Exception exp) {
            TLogger.exception(exp);
            return null;
        }
    }

    public final double getDoubleValue() {

        try {
            return StringUtils.parse(getValue()).doubleValue();
        } catch (Exception exp) {
            TLogger.exception(exp);
            return -1;
        }
    }

    public final <F extends Enum<F>> List<F> getEnumListValue(Class<F> type) {

        try {
            final String[] split = StringUtils.SEPARATOR.split(getValue());
            List<F> list = new ArrayList<>();
            for (int i = 0; i < split.length; i++) {
                if (!split[i].equals(StringUtils.EMPTY) && !split[i].equals(StringUtils.WHITESPACE)) {
                    try {
                        F item = Enum.valueOf(type, split[i]);

                        if (item != null) {
                            list.add(item);
                        }
                    } catch (IllegalArgumentException e) {
                        TLogger.warning("XML contains an enum op type [" + type.getSimpleName() + "] with value [" + split[i]
                                + "], but this value is not valid.");
                    }
                }
            }
            return list;
        } catch (Exception exp) {
            TLogger.exception(exp);
            return null;
        }
    }

    public final <F extends Enum<F>> F getEnumValue(Class<F> type) {
        try {
            return Enum.valueOf(type, getValue());
        } catch (Exception exp) {
            TLogger.exception(exp);
            return null;
        }
    }

    public final int[] getIntArrayValue() {

        try {
            if (StringUtils.EMPTY.equals(getValue())) {
                return new int[0];
            }
            final String[] split = StringUtils.SEPARATOR.split(getValue());
            int[] array = new int[split.length];
            for (int i = 0; i < split.length; i++) {
                Number n = StringUtils.parse(split[i]);
                array[i] = n.intValue();
            }
            return array;
        } catch (Exception exp) {
            TLogger.exception(exp);
            return null;
        }
    }

    public final Integer getIntegerValue() {
        return this.getIntValue();
    }

    public final int getIntValue() {

        try {
            Number n = StringUtils.parse(getValue());
            return n.intValue();
        } catch (Exception exp) {
            TLogger.exception(exp);
            return NONE;
        }
    }

    public final long getLongValue() {

        try {
            return StringUtils.parse(getValue()).longValue();
        } catch (Exception exp) {
            TLogger.exception(exp);
            return NONE;
        }
    }

    public final MultiPolygon getMultiPolygon() {

        WKTReader wtkReader = new WKTReader();
        if (!StringUtils.containsData(getValue())) {
            return JTSUtils.EMPTY;
        }
        try {
            return (MultiPolygon) wtkReader.read(getValue());
        } catch (Exception e) {
            TLogger.exception(e);
            return JTSUtils.EMPTY;
        }
    }

    public final List<String> getStringListValue() {
        try {
            final String[] split = StringUtils.SEPARATOR.split(getValue());
            List<String> list = new ArrayList<String>();
            for (int i = 0; i < split.length; i++) {
                list.add(split[i]);
            }
            return list;
        } catch (Exception exp) {
            TLogger.exception(exp);
            return null;
        }
    }

    public final String getValue() {
        if (value == null) {
            return null;
        }
        return value.trim();
    }

    public final void setValue(final Boolean value) {
        this.value = String.valueOf(value);
    }

    public final void setValue(final double[] value) {

        StringBuffer data = new StringBuffer();
        for (double valueItem : value) {
            data.append(valueItem + StringUtils.WHITESPACE);
        }
        this.value = data.toString();
    }

    public final void setValue(final Enum<?> value) {
        this.value = value.name();
    }

    public final void setValue(final int[] value) {

        StringBuffer data = new StringBuffer();
        for (int valueItem : value) {
            data.append(valueItem + StringUtils.WHITESPACE);
        }
        this.value = data.toString();
    }

    public <T extends Enum<?>> void setValue(List<T> list) {

        StringBuffer data = new StringBuffer();
        for (T e : list) {
            data.append(e.name() + StringUtils.WHITESPACE);
        }
        this.value = data.toString();
    }

    public final void setValue(final Number value) {
        this.value = String.valueOf(value);
    }

    public final void setValue(final Number[] value) {
        for (int i = 0; i < value.length; i++) {
            this.value += value[i] + StringUtils.WHITESPACE;
        }
    }

    public final void setValue(final String value) {
        this.value = value;
    }
}
