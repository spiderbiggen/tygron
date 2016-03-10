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
public class ParentVariable implements Serializable, Comparable<ParentVariable> {

    private static final long serialVersionUID = -5459143243384524918L;

    @XMLValue
    private VariablePackage parentVariable;

    @XMLValue
    @ListOfClass(VariableValuesPair.class)
    private ArrayList<VariableValuesPair> subVariables = new ArrayList<VariableValuesPair>();

    public ParentVariable() {
        parentVariable = null;
    }

    public ParentVariable(VariablePackage parentVariable, List<VariableValuesPair> subVariablePairs) {
        this.parentVariable = parentVariable;
        this.subVariables = new ArrayList<>(subVariablePairs);
    }

    @Override
    public int compareTo(ParentVariable arg0) {
        return this.getSortIndex() - arg0.getSortIndex();
    }

    public TColor getColor() {
        return this.parentVariable.getColor();
    }

    public String getName() {
        return this.parentVariable.getName();
    }

    public int getSortIndex() {
        return this.parentVariable.getSortIndex();
    }

    public List<VariableValuesPair> getSubVariables() {
        return this.subVariables;
    }

    public void setName(String name) {
        parentVariable.setName(name);
    }

    public void setParentVariable(VariablePackage parentVariable) {
        this.parentVariable = parentVariable;
    }

    public void setSubVariables(List<VariableValuesPair> newSubVariables) {
        this.subVariables = new ArrayList<>(newSubVariables);
    }
}
