/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.serializable;

import java.io.Serializable;
import nl.tytech.core.item.annotations.DoNotSaveToInit;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.ColorUtil;
import nl.tytech.util.color.TColor;

/**
 *
 * @author Frank Baars
 *
 */
public class VariablePackage implements Serializable, Comparable<VariablePackage> {

    private static final long serialVersionUID = 4864939684979562008L;

    private static final int BASE_WEIGHT = 1;

    private static final int BASE_SORT_INDEX = 0;

    @DoNotSaveToInit
    @XMLValue
    private TColor color = ColorUtil.getDefaultGraphColor(0);

    @DoNotSaveToInit
    @XMLValue
    private String name = StringUtils.EMPTY;

    @DoNotSaveToInit
    @XMLValue
    private int sortIndex = BASE_SORT_INDEX;
    @DoNotSaveToInit
    @XMLValue
    private double weight = BASE_WEIGHT;

    public VariablePackage() {

    }

    public VariablePackage(String name, TColor color) {
        setup(name, color, BASE_SORT_INDEX, BASE_WEIGHT);
    }

    public VariablePackage(String name, TColor color, int sortIndex) {
        setup(name, color, sortIndex, BASE_WEIGHT);
    }

    public VariablePackage(VariablePackage other) {
        setup(other.name, other.color, other.sortIndex, other.weight);
    }

    @Override
    public int compareTo(VariablePackage other) {
        return this.sortIndex - other.sortIndex;
    }

    public void copy(VariablePackage other) {
        setup(other.name, other.color, other.sortIndex, other.weight);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof VariablePackage) {
            VariablePackage otherVariable = (VariablePackage) other;
            return this.sortIndex == otherVariable.sortIndex && this.name.equals(otherVariable.name)
                    && this.color.equals(otherVariable.color) && this.weight == otherVariable.weight
                    && this.sortIndex == otherVariable.sortIndex;
        }
        return false;

    }

    public TColor getColor() {
        return this.color;
    }

    public String getName() {
        return this.name;
    }

    public int getSortIndex() {
        return this.sortIndex;
    }

    public double getWeight() {
        return this.weight;
    }

    public void setColor(TColor color) {
        this.color = color;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSortIndex(int newSortIndex) {
        this.sortIndex = newSortIndex;
    }

    private void setup(String name, TColor color, int sortIndex, double weight) {
        this.color = color;
        this.name = name;
        this.sortIndex = sortIndex;
        this.weight = weight;
    }

    public void setWeight(double newWeight) {
        this.weight = newWeight;
    }
}
