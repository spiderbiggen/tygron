/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.serializable;

import java.io.Serializable;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.Item;
import nl.tytech.util.MathUtils;

/**
 *
 * @author Frank Baars
 *
 */
public class ScalingSegment implements Serializable {

    public static final double SCALE_THRESHOLD = 0.001;

    /**
     *
     */
    private static final long serialVersionUID = -4961860878838426684L;

    @XMLValue
    private double minValue = 0;

    @XMLValue
    private double maxValue = 1;

    @XMLValue
    private double minScale = SCALE_THRESHOLD;

    @XMLValue
    private double maxScale = 1;

    @XMLValue
    private Integer segmentID = Item.NONE;

    public ScalingSegment() {

    }

    public ScalingSegment(Integer segmentID, double minValue, double maxValue, double minScale, double maxScale) {
        this.segmentID = segmentID;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.minScale = Math.max(minScale, SCALE_THRESHOLD);
        this.maxScale = maxScale;
        validateMinMax();
    }

    public Integer getID() {
        return segmentID;
    }

    public double getMaxScale() {
        return maxScale;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public double getMinScale() {
        return minScale;
    }

    public double getMinValue() {
        return minValue;
    }

    public double getScale(double value) {
        if (maxValue - minValue == 0) {
            value = 1;
        } else {
            value = (value - minValue) / (maxValue - minValue);
        }

        return Math.abs(MathUtils.clamp(value * maxScale, minScale, maxScale));
    }

    public void setMaxScale(double maxScale) {
        this.maxScale = maxScale;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
        validateMinMax();
    }

    public void setMinAndMaxValue(double minValue, double maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        validateMinMax();
    }

    public void setMinScale(double minScale) {
        this.minScale = Math.max(minScale, SCALE_THRESHOLD);
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
        validateMinMax();
    }

    private void validateMinMax() {
        if (minValue > maxValue) {
            double swap = minValue;
            minValue = maxValue;
            maxValue = swap;
        }
    }

    public boolean withinSegment(double value) {
        return value <= maxValue && value >= minValue;
    }

}
