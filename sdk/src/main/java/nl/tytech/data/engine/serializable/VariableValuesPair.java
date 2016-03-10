/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.serializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.util.color.TColor;

/**
 *
 * @author Frank Baars
 *
 */
public class VariableValuesPair implements Serializable, Comparable<VariableValuesPair> {

    private static final long serialVersionUID = 8016985974645865345L;

    @XMLValue
    private VariablePackage variableData = new VariablePackage();

    @XMLValue
    @ListOfClass(Double.class)
    private ArrayList<Double> values = new ArrayList<>();

    public VariableValuesPair() {
        // default add 1 value.
        values.add(0d);
    }

    public VariableValuesPair(String name, TColor color, double value, int sortIndex) {
        setup(name, color, value, sortIndex);
    }

    public VariableValuesPair(VariablePackage variable, double value) {
        setup(variable, value);
    }

    private void addValue(double value) {
        this.values.add(value);
    }

    private void addValues(List<Double> values) {
        this.values.addAll(values);
    }

    @Override
    public int compareTo(VariableValuesPair o) {
        return variableData.getSortIndex() - variableData.getSortIndex();
    }

    public TColor getColor() {
        return this.variableData.getColor();
    }

    public String getName() {
        return this.variableData.getName();
    }

    public int getSortIndex() {
        return this.variableData.getSortIndex();
    }

    public double getValue() {
        return this.values.get(0);
    }

    public List<Double> getValues() {
        return this.values;
    }

    public VariablePackage getVariablePackage() {
        return new VariablePackage(this.variableData);
    }

    public double getWeight() {
        return this.variableData.getWeight();
    }

    private void initClassVariables(String name, TColor color, int sortIndex) {
        variableData = new VariablePackage(name, color, sortIndex);
        this.values = new ArrayList<>();
    }

    private void setup(String name, TColor color, double value, int sortIndex) {
        initClassVariables(name, color, sortIndex);
        addValue(value);
    }

    private void setup(String name, TColor color, List<Double> values, int sortIndex) {
        initClassVariables(name, color, sortIndex);
        addValues(values);

    }

    private void setup(VariablePackage variable, double value) {
        variableData = new VariablePackage(variable);
        this.values = new ArrayList<>();
        values.add(value);
    }

    private void setup(VariablePackage variable, List<Double> values) {
        variableData = new VariablePackage(variable);
        this.values = new ArrayList<>();
        this.values.addAll(values);
    }

    @Override
    public String toString() {
        return this.getName() + " " + this.getValues();
    }

}
