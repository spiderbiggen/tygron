/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.net.serializable.PolygonItem;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.item.UniqueNamedItem;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.StringUtils;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;

/**
 * Pipe connection that can transport e.g. water
 *
 * @author Maxim Knepfle
 *
 */
public class Pipe extends UniqueNamedItem implements PolygonItem {

    /**
     *
     */
    private static final long serialVersionUID = 5434684457934689387L;

    public final static double DEFAULT_PIPE_SQUARE_SIZE = 10;

    @XMLValue
    @ItemIDField("PIPE_JUNCTIONS")
    private Integer startJunctionID = Item.NONE;

    @XMLValue
    @ItemIDField("PIPE_JUNCTIONS")
    private Integer endJunctionID = Item.NONE;

    @XMLValue
    private boolean alwaysActive = false;

    @XMLValue
    private double flow = 0;

    @XMLValue
    private double diameterM = 0;

    private transient MultiPolygon[] multiPolygon;

    private transient LineSegment representation;

    @ItemIDField("PIPE_DEFINITIONS")
    private Integer pipeDefinitionID = Item.NONE;

    public double distance(Point point) {

        LineSegment representation = getRepresentation();
        if (representation == null) {
            return Double.MAX_VALUE;
        }
        return representation.distance(point.getCoordinate());
    }

    public Point getCenterPoint() {

        Point start = this.getStartPoint();
        Point end = this.getEndPoint();

        if (start == null || end == null) {
            return null;
        }

        double dx = start.getX() - end.getX();
        double dy = start.getY() - end.getY();

        return JTSUtils.createPoint(start.getX() - dx / 2d, start.getY() - dy / 2d, 0d);
    }

    public Point getClosestPoint(Point point) {

        LineSegment lineSegment = getRepresentation();
        if (lineSegment == null) {
            return null;
        }
        return JTSUtils.createPoint(lineSegment.closestPoint(point.getCoordinate()));
    }

    public double getCosts() {

        double lenghtM = this.getLenghtM();
        PipeDefinition pipeInterface = this.getPipeInterface();
        return lenghtM * pipeInterface.getPriceM();
    }

    public double getDiameterM() {
        return diameterM;
    }

    public PipeJunction getEndJunction() {
        return this.getItem(MapLink.PIPE_JUNCTIONS, getEndJunctionID());
    }

    public Integer getEndJunctionID() {
        return endJunctionID;
    }

    public Point getEndPoint() {
        if (getEndJunction() == null) {
            return null;
        }

        return this.getEndJunction().getPoint();
    }

    public double getFlow() {
        return flow;
    }

    public double getLenghtM() {
        Point start = this.getStartPoint();
        Point end = this.getEndPoint();
        if (start == null || end == null) {
            return 0;
        }
        return start.distance(end);
    }

    /**
     * Return the pipe type required for this pipe.
     * @return
     */
    private PipeDefinition getPipeInterface() {
        PipeDefinition definition = getItem(MapLink.PIPE_DEFINITIONS, this.pipeDefinitionID);
        if (definition == null) {
            updatePipeDefinition();
            definition = getItem(MapLink.PIPE_DEFINITIONS, this.pipeDefinitionID);
        }
        return definition;
    }

    public String getPipeTypeName() {
        PipeDefinition pipeInterface = this.getPipeInterface();
        return pipeInterface.getName();
    }

    @Override
    public MultiPolygon[] getQTMultiPolygons() {
        if (multiPolygon == null) {

            LineSegment geom = getRepresentation();
            if (geom instanceof LineSegment) {
                multiPolygon = new MultiPolygon[] { JTSUtils.createMP(JTSUtils.bufferSimple(JTSUtils.createLine(geom.p0, geom.p1),
                        DEFAULT_PIPE_SQUARE_SIZE / 2d)) };

            } else {
                multiPolygon = new MultiPolygon[] { JTSUtils.EMPTY };
            }
        }
        return multiPolygon;
    }

    public LineSegment getRepresentation() {
        if (representation == null) {
            Point start = getStartPoint();
            Point end = getEndPoint();
            if (start == null || end == null) {
                return null;
            }
            representation = new LineSegment(start.getCoordinate(), end.getCoordinate());
        }
        return representation;
    }

    public PipeJunction getStartJunction() {
        return this.getItem(MapLink.PIPE_JUNCTIONS, getStartJunctionID());
    }

    public Integer getStartJunctionID() {
        return this.startJunctionID;
    }

    public Point getStartPoint() {
        if (getStartJunction() == null) {
            return null;
        }

        return getStartJunction().getPoint();
    }

    public boolean hasMissingJunctions() {
        return getStartJunction() == null || getEndJunction() == null;
    }

    /**
     *
     * @return
     */
    public boolean isActive() {
        if (this.alwaysActive) {
            return true;
        }
        // prevent roundoff
        return diameterM > 0;
    }

    @Override
    public void reset() {
        super.reset();
        resetTransients();
    }

    private void resetTransients() {
        representation = null;
        multiPolygon = null;
    }

    public void setAlwaysActive(boolean alwaysActive) {
        this.alwaysActive = alwaysActive;
    }

    public void setDiameterM(double diameterM) {

        this.diameterM = diameterM;
        updatePipeDefinition();
    }

    public void setEndJunctionID(Integer endJunctionID) {
        this.endJunctionID = endJunctionID;
        resetTransients();
    }

    public void setFlow(double flow) {
        this.flow = flow;
    }

    public void setStartJunctionID(Integer startJunctionID) {
        this.startJunctionID = startJunctionID;
        resetTransients();
    }

    @Override
    public String toString() {
        return getName();
    }

    private void updatePipeDefinition() {
        /**
         * Search for best definition match based on closets diameter
         */
        double bestDistanceM = Double.MAX_VALUE;
        this.pipeDefinitionID = Item.NONE;
        ItemMap<PipeDefinition> defs = this.getMap(MapLink.PIPE_DEFINITIONS);
        for (PipeDefinition pipeDef : defs.values()) {
            double testDistance = Math.abs(pipeDef.getDiameterM() - diameterM);
            if (testDistance < bestDistanceM) {
                bestDistanceM = testDistance;
                this.pipeDefinitionID = pipeDef.getID();
            }
        }
    }

    @Override
    public String validated(boolean startNewGame) {

        if (Item.NONE.equals(this.getStartJunctionID()) || Item.NONE.equals(this.getEndJunctionID())) {
            return "\nPipe: " + this + " must have a valid start and end junction!";
        }
        return StringUtils.EMPTY;
    }
}
