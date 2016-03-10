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
 * IndicatorScore
 * <p>
 * Gives the score of a measure on the indicator.
 * </p>
 *
 * @author Maxim Knepfle
 */
public class IndicatorScore implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -1288614736955798328L;

    @XMLValue
    @ItemIDField("INDICATORS")
    private Integer indicatorID = Item.NONE;

    @XMLValue
    private double maxPoints = Integer.MAX_VALUE;

    @XMLValue
    private double score = 0;

    public IndicatorScore() {

    }

    public IndicatorScore(Integer indicatorID, double score) {
        this.indicatorID = indicatorID;
        this.score = score;
    }

    public Integer getIndicatorID() {
        return indicatorID;
    }

    public double getMaxPoints() {
        return this.maxPoints;
    }

    public double getScore() {
        return score;
    }

    public void setMaxPoints(double maxPoints) {
        this.maxPoints = maxPoints;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "IndicatorID: " + indicatorID;
    }
}
