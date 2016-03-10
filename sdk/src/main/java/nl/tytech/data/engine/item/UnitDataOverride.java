/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import java.util.ArrayList;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.item.UnitData.TrafficType;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;

/**
 * UnitDataOverride
 * <p>
 * Addition production values. unitData ID = override ID
 * </p>
 * @author Frank Baars
 */
public class UnitDataOverride extends Item {

    /**
     *
     */
    private static final long serialVersionUID = 1089214173999556938L;

    @XMLValue
    private String name = StringUtils.EMPTY;

    @XMLValue
    @ListOfClass(TColor.class)
    private ArrayList<TColor> colors = null;

    @XMLValue
    private Boolean active = null;

    public UnitDataOverride() {

    }

    public void addColor(TColor color) {
        if (!hasColors()) {
            initColors();
        }
        colors.add(color);

    }

    public boolean changeColor(Integer index, TColor color) {
        if (!hasColors()) {
            initColors();
        }
        if (index < 0 || index >= colors.size() || colors.get(index).equals(color)) {
            return false;
        }
        colors.set(index, color);
        return true;
    }

    public ArrayList<TColor> getColors() {
        return colors;
    }

    public String getName() {
        return name;
    }

    public TrafficType getTrafficType() {
        UnitData unit = this.getUnitData();
        return unit.getTrafficType();
    }

    public UnitData getUnitData() {
        return this.getItem(MapLink.UNIT_DATAS, this.getUnitDataID());
    }

    public Integer getUnitDataID() {
        return this.getID();
    }

    public boolean hasColors() {
        return colors != null && colors.size() > 0;
    }

    private void initColors() {
        if (!hasColors()) {
            UnitData unitData = getUnitData();
            if (unitData != null) {
                colors = new ArrayList<>(unitData.getColors());
            } else {
                colors = new ArrayList<>();
            }
        }
    }

    public Boolean isActive() {
        return active;
    }

    public boolean removeColor(Integer unitDataID, int index, TColor color) {
        if (!hasColors()) {
            initColors();
        }
        if (index < 0 || index >= colors.size() || !colors.get(index).equals(color)) {
            return false;
        }
        if (colors.get(index).equals(color)) {
            colors.remove(index);
            return true;
        }

        colors.remove(color);
        return true;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        if (StringUtils.containsData(name)) {
            return name;
        }
        UnitData unitData = this.getUnitData();
        if (unitData != null && StringUtils.containsData(unitData.getName())) {
            return unitData.getName();
        }
        return UnitDataOverride.class.getSimpleName() + StringUtils.WHITESPACE + getID();
    }
}
