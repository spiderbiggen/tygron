/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.item.PipeLoad.LoadParameterType;
import nl.tytech.data.engine.serializable.MapType;
import nl.tytech.data.engine.serializable.ScalingSegment;

/**
 * Special overlay for showing potential of PipeClusters
 *
 * @author Frank Baars
 */
public class PipeClusterOverlay extends Overlay {

    /**
     *
     */
    private static final long serialVersionUID = 5274209347318961443L;

    @XMLValue
    private LoadParameterType parameterType = LoadParameterType.FLOW;

    @XMLValue
    @ListOfClass(ScalingSegment.class)
    private ArrayList<ScalingSegment> scalingSegments = new ArrayList<>();

    public void addSegment() {
        int maxID = Item.NONE;
        for (ScalingSegment segment : scalingSegments) {
            if (maxID < segment.getID()) {
                maxID = segment.getID();
            }
        }
        maxID += 1;
        scalingSegments.add(new ScalingSegment(maxID, 0, 1, ScalingSegment.SCALE_THRESHOLD, 1));
    }

    public boolean containsSegment(Integer segmentID) {
        return getSegment(segmentID) != null;
    }

    public LoadParameterType getParameterType() {
        return parameterType;
    }

    public double getScale(MapType mapType, PipeCluster pipeCluster) {
        if (scalingSegments.size() == 0) {
            return 1;
        }

        double value = pipeCluster.getParameterValue(mapType, this.parameterType);
        for (ScalingSegment segment : scalingSegments) {
            if (segment.withinSegment(value)) {
                return segment.getScale(value);
            }
        }

        double closestValue = value;
        double closestDistance = Double.MAX_VALUE;
        for (ScalingSegment segment : scalingSegments) {
            if (Math.abs(value - segment.getMinValue()) < closestDistance) {
                closestDistance = Math.abs(value - segment.getMinValue());
                closestValue = segment.getMinValue();
            }
            if (Math.abs(value - segment.getMaxValue()) < closestDistance) {
                closestDistance = Math.abs(value - segment.getMaxValue());
                closestValue = segment.getMaxValue();
            }
        }

        for (ScalingSegment segment : scalingSegments) {
            if (segment.withinSegment(closestValue)) {
                return segment.getScale(closestValue);
            }
        }

        return 1;
    }

    public List<ScalingSegment> getScalingSegments() {
        return scalingSegments;
    }

    public ScalingSegment getSegment(Integer segmentID) {
        for (ScalingSegment segment : scalingSegments) {
            if (segment.getID().equals(segmentID)) {
                return segment;
            }
        }
        return null;
    }

    public List<ScalingSegment> getSegments() {
        return scalingSegments;
    }

    public int indexOf(Integer segmentID) {
        int index = 0;
        for (ScalingSegment segment : scalingSegments) {
            if (segment.getID().equals(segmentID)) {
                return index;
            }
            index++;
        }
        return -1;

    }

    public void removeSegment(Integer segmentID) {
        ScalingSegment removedSegment = null;
        for (ScalingSegment segment : scalingSegments) {
            if (segment.getID().equals(segmentID)) {
                removedSegment = segment;
                break;
            }
        }
        if (removedSegment != null) {
            scalingSegments.remove(removedSegment);
        }
    }

    public void setLoadParameterType(LoadParameterType parameterType) {
        this.parameterType = parameterType;
    }

}
