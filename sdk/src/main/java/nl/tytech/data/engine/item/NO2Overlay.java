/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import java.util.HashMap;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.engine.item.UnitData.TrafficType;
import nl.tytech.util.ObjectUtils;

/**
 * NO2 based on a grid
 *
 * @author Maxim Knepfle
 */
public class NO2Overlay extends GridOverlay {

    public enum GasType {
        NOX, NO2
    }

    /**
     *
     */
    private static final long serialVersionUID = -2572949484529750426L;

    @XMLValue
    private HashMap<TrafficType, double[]> normalEmissions = new HashMap<>();

    @XMLValue
    private HashMap<TrafficType, double[]> congestedEmissions = new HashMap<>();

    @XMLValue
    private double baseO3 = 42;// default value for The Hague

    public double getBaseO3() {
        return baseO3;
    }

    public double getEmissionValue(TrafficType type, GasType gasType, boolean congested) {

        double[] values = this.getEmissionValues(type, congested);
        return values[gasType.ordinal()];
    }

    public double[] getEmissionValues(TrafficType type, boolean congested) {

        if (congested && congestedEmissions.containsKey(type)) {
            return congestedEmissions.get(type);
        } else if (!congested && normalEmissions.containsKey(type)) {
            return normalEmissions.get(type);
        }
        return type.getDefaultEmission(congested);
    }

    public void setBaseO3(double baseO3) {
        this.baseO3 = baseO3;
    }

    public void setEmissionValue(TrafficType type, GasType gasType, boolean congested, double value) {

        if (congested) {
            if (!congestedEmissions.containsKey(type)) {
                congestedEmissions.put(type, ObjectUtils.deepCopy(type.getDefaultEmission(congested)));
            }
            congestedEmissions.get(type)[gasType.ordinal()] = value;
        } else {
            if (!normalEmissions.containsKey(type)) {
                normalEmissions.put(type, ObjectUtils.deepCopy(type.getDefaultEmission(congested)));
            }
            normalEmissions.get(type)[gasType.ordinal()] = value;
        }
    }

    @Override
    public String validated(boolean startNewGame) {

        String validated = super.validated(startNewGame);

        for (double[] values : normalEmissions.values()) {
            if (values.length != GasType.values().length) {
                validated += "\nNormal Emissions do not contain correct values";
            }
        }
        for (double[] values : congestedEmissions.values()) {
            if (values.length != GasType.values().length) {
                validated += "\nCongested Emissions do not contain correct values";
            }
        }
        return validated;
    }
}
