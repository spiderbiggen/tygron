/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.serializable;

import java.io.Serializable;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.Item;

/**
 * IndicatorWeight
 * <p>
 * Gives weight to indicators for each stakeholder.
 * <p>
 *
 * @author Maxim Knepfle
 */
public class IndicatorWeight implements Serializable {

    private static final long serialVersionUID = 4605099981435018509L;

    @XMLValue
    @ItemIDField("INDICATORS")
    private Integer indicatorID = Item.NONE;

    @XMLValue
    private double weight = 0f;

    public IndicatorWeight() {

    }

    public IndicatorWeight(Integer id, double newWeight) {
        this.indicatorID = id;
        this.weight = newWeight;
    }

    public Integer getIndicatorID() {
        return indicatorID;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {

        return "IndicatorID: " + indicatorID + " weight: " + weight;

    }

}
