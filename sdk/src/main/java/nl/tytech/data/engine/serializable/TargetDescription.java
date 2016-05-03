/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.serializable;

import java.io.Serializable;
import nl.tytech.data.engine.item.ClientWord.ClientTerms;
import nl.tytech.locale.unit.UnitType;
import nl.tytech.util.StringUtils;

/**
 * Target that needs to be filled in for each indicator.
 * @author Maxim
 *
 */
public class TargetDescription implements Serializable {

    private static final long serialVersionUID = 4912927280948356268L;
    private String description = StringUtils.EMPTY;
    private double defaultValue = 0;
    private double minValue = -Double.MAX_VALUE;
    private double maxValue = Double.MAX_VALUE;
    private UnitType[] unitDimensionType = { UnitType.LENGTH };
    private ClientTerms addition = null;

    public TargetDescription() {

    }

    public TargetDescription(String description, double defaultValue, double minValue, double maxValue, UnitType... unitDimensionType) {
        this.description = description;
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.unitDimensionType = unitDimensionType;
    }

    public void addROTermAsDescriptionAddition(ClientTerms roTerm) {
        this.addition = roTerm;
    }

    public double getDefaultValue() {
        return defaultValue;
    }

    public String getDescription() {
        return description;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public double getMinValue() {
        return minValue;
    }

    public ClientTerms getROTermAddition() {
        return addition;
    }

    public UnitType[] getUnitDimensionTypes() {
        return unitDimensionType;
    }

    public boolean hasROTermAddition() {
        return addition != null;
    }
}
