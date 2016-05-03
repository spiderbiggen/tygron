/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import nl.tytech.core.item.annotations.DoNotSaveToInit;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.item.UniqueNamedItem;
import nl.tytech.util.StringUtils;
import com.vividsolutions.jts.geom.Point;

/**
 * Global variable
 *
 * @author Maxim Knepfle
 */
public class Global extends UniqueNamedItem {

    public enum ReadOnly {

        MEASURE_ADDITIONAL_INNOVATIVE_WATER_STORAGE(0),

        MEASURE_CURRENT_ADDITIONAL_INNOVATIVE_WATER_STORAGE(0),

        MEASURE_MAQUETTE_ADDITIONAL_INNOVATIVE_WATER_STORAGE(0),

        MEASURE_ADDITIONAL_TRADITIONAL_WATER_STORAGE(0),

        MEASURE_CURRENT_ADDITIONAL_TRADITIONAL_WATER_STORAGE(0),

        MEASURE_MAQUETTE_ADDITIONAL_TRADITIONAL_WATER_STORAGE(0),

        SUBSIDENCE_A(0.023537),

        SUBSIDENCE_B(0.01263),

        SUBSIDENCE_C(0.00668),

        SUBSIDENCE_YEARS(1.0),

        WATER_STORAGE_ALLOWED_WATER_LEVEL_INCREASE(0.30),

        WATER_STORAGE_MAX_FRACTION_INNOVATIVE(0.30),

        ;

        public final static ReadOnly[] VALUES = ReadOnly.values();

        public static boolean isValue(String name) {
            for (ReadOnly value : VALUES) {
                if (value.name().equals(name)) {
                    return true;
                }
            }
            return false;
        }

        private double defaultValue;

        private ReadOnly(double defaultValue) {
            this.defaultValue = defaultValue;
        }

        public double getDefaultValue() {
            return defaultValue;
        }
    }

    private static final long serialVersionUID = 1362611699406604469L;

    /**
     * Default amount of global decimals
     */
    public static final int DECIMALS = 10;

    @XMLValue
    @DoNotSaveToInit
    private double actualValue = 0;

    @XMLValue
    private double startValue = 0;

    @XMLValue
    private Point point = null;

    @XMLValue
    private String visualisationName = StringUtils.EMPTY;

    @XMLValue
    @ItemIDField("STAKEHOLDERS")
    private Integer bookValueStakeholderID = Item.NONE;

    public double getActualValue() {
        return actualValue;
    }

    public Stakeholder getBookValueStakeholder() {
        return this.getItem(MapLink.STAKEHOLDERS, getBookValueStakeholderID());
    }

    public Integer getBookValueStakeholderID() {
        return bookValueStakeholderID;
    }

    @Override
    public String getDescription() {
        return getName();
    }

    public Point getPoint() {
        return point;
    }

    public double getStartValue() {
        return startValue;
    }

    public String getVisualisationName() {
        if (StringUtils.containsData(this.visualisationName)) {
            return visualisationName;
        }
        return getName();
    }

    public boolean isBookValue() {
        return !Item.NONE.equals(bookValueStakeholderID);
    }

    public boolean isReadOnly() {
        return ReadOnly.isValue(this.getName());
    }

    public void setActualValue(double actualValue) {
        this.actualValue = actualValue;
    }

    public void setBookValueStakeholderID(Integer stakeholderID) {
        this.bookValueStakeholderID = stakeholderID;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public void setStartValue(double startValue) {
        this.startValue = startValue;
    }

    public void setVisualisationName(String visualisationName) {
        this.visualisationName = visualisationName;
    }
}
