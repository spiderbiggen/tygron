/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.serializable;

import java.io.Serializable;
import java.util.ArrayList;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.StringUtils;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.prep.PreparedGeometry;
import com.vividsolutions.jts.geom.prep.PreparedGeometryFactory;

/**
 *
 * @author Frank Baars
 *
 */
public class MeasureSpatial implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -272099094225816319L;

    @XMLValue
    private MultiPolygon innerPolygon = JTSUtils.EMPTY;

    @XMLValue
    private MultiPolygon outerPolygon = JTSUtils.EMPTY;

    @XMLValue
    private double innerHeight;

    @XMLValue
    private Integer id = Item.NONE;

    @XMLValue
    private MeasureEditType measureEditType = MeasureEditType.DIKE;

    @XMLValue
    private Integer mapLinkID = Item.NONE;

    @XMLValue
    private MapLink mapLink = null;

    private transient PreparedGeometry innerPrep = null;
    private transient PreparedGeometry outerPrep = null;
    private transient ArrayList<LineString> outerRings = null;

    public MeasureSpatial() {

    }

    public MeasureSpatial(MeasureEditType measureEditType, Integer id) {
        this.measureEditType = measureEditType;
        this.id = id;
        switch (measureEditType) {
            case DIKE:
                this.innerHeight = 20;
                break;
            case WATER:
                this.innerHeight = -20;
                break;
            case FLATTEN:
                this.innerHeight = 10;
                break;
            default:
                break;
        }
    }

    public void addInnerPolygon(MultiPolygon add) {
        this.innerPolygon = JTSUtils.union(innerPolygon, add);
        this.outerPolygon = JTSUtils.union(outerPolygon, innerPolygon);
        resetPreps();
    }

    public void addOuterPolygon(MultiPolygon outer) {
        this.outerPolygon = JTSUtils.union(this.outerPolygon, outer);
        resetPreps();
    }

    public double getHeight(Point point, double originalHeight) {
        if (innerPrep == null) {
            innerPrep = PreparedGeometryFactory.prepare(innerPolygon);
        }

        if (JTSUtils.covers(innerPrep, point)) {
            return innerHeight;
        }

        if (!JTSUtils.containsData(outerPolygon)) {
            return originalHeight;
        }

        if (outerPrep == null) {
            outerPrep = PreparedGeometryFactory.prepare(outerPolygon);
            outerRings = new ArrayList<>();
            for (Polygon polygon : JTSUtils.getPolygons(outerPolygon)) {
                outerRings.add(polygon.getExteriorRing());
            }
        }

        if (JTSUtils.covers(outerPrep, point)) {
            if (!JTSUtils.containsData(innerPolygon) || outerRings.isEmpty()) {
                return originalHeight;
            }

            double distInner = point.distance(innerPolygon);
            double distOut = Double.MAX_VALUE;
            for (LineString lineString : outerRings) {
                distOut = Math.min(distOut, point.distance(lineString));
            }

            return (distOut * innerHeight + distInner * originalHeight) / (distInner + distOut);

        }

        return originalHeight;
    }

    public Integer getID() {
        return id;
    }

    public double getInnerHeight() {
        return innerHeight;
    }

    public MultiPolygon getInnerMultiPolygon() {
        return innerPolygon;
    }

    public MeasureEditType getMeasureEditType() {
        return measureEditType;
    }

    public String getName() {
        if (measureEditType == null) {
            return MeasureSpatial.class.getSimpleName() + StringUtils.WHITESPACE + getID();
        }
        return measureEditType.name() + StringUtils.WHITESPACE + getID();
    }

    public MultiPolygon getOuterMultiPolygon() {
        return outerPolygon;
    }

    public Integer getReferenceItemID() {
        return mapLinkID;
    }

    public MapLink getReferenceMapLink() {
        return mapLink;
    }

    public MultiPolygon getSideMultiPolygon() {
        return JTSUtils.difference(getOuterMultiPolygon(), getInnerMultiPolygon(), true);
    }

    public void removeInnerPolygon(MultiPolygon remove) {
        this.innerPolygon = JTSUtils.difference(innerPolygon, remove);
        resetPreps();
    }

    public void removeOuterPolygon(MultiPolygon remove) {
        this.outerPolygon = JTSUtils.difference(outerPolygon, remove);
        this.innerPolygon = JTSUtils.intersection(outerPolygon, innerPolygon);
        resetPreps();
    }

    private void resetPreps() {
        innerPrep = null;
        outerPrep = null;
        outerRings = null;
    }

    public void setInnerHeight(double height) {
        this.innerHeight = height;
    }

    public void setMeasureEditType(MeasureEditType type) {
        this.measureEditType = type;
    }

    public void setReferenceItem(MapLink mapLink, Integer itemID) {
        this.mapLink = mapLink;
        this.mapLinkID = itemID;
    }

}
