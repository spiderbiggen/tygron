/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.serializable.MapType;
import nl.tytech.data.engine.serializable.MeasureEditType;
import nl.tytech.util.JTSUtils;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;

/**
 * Prediction (NOT SAVED TO XML)
 * @author Maxim Knepfle
 */
public class Prediction extends Item {

    private static final long serialVersionUID = -4253782210241563578L;

    private HashMap<Integer, Double> scoreBefore = new HashMap<>();

    private HashMap<Integer, Double> scoreAfter = new HashMap<>();

    private ArrayList<Integer> buildingIDs = new ArrayList<>();

    @XMLValue
    private Integer measureID = Item.NONE;

    public Prediction() {
    }

    public boolean containsAfterScore(Indicator indicator) {
        return this.scoreAfter.containsKey(indicator.getID());
    }

    public List<Integer> getBuildingIDs() {
        return buildingIDs;
    }

    public Point getCenterPoint() {
        return JTSUtils.getCenterPoint(this.getMultiPolygon());
    }

    public Double getIncrease(Indicator indicator) {

        Double before = scoreBefore.get(indicator.getID());
        if (before == null) {
            return null;
        }
        Double after = scoreAfter.get(indicator.getID());
        if (after == null) {
            return null;
        }
        return after - before;
    }

    public Integer getMeasureID() {
        return measureID;
    }

    public MultiPolygon getMultiPolygon() {

        List<MultiPolygon> mps = new ArrayList<>();
        List<Building> buildings = this.getItems(MapLink.BUILDINGS, this.getBuildingIDs());
        for (Building building : buildings) {
            mps.add(building.getMultiPolygon(null));
        }

        Measure measure = this.getItem(MapLink.MEASURES, this.measureID);
        if (measure != null && measure instanceof MapMeasure) {
            MapMeasure mapMeasure = ((MapMeasure) measure);
            for (MeasureEditType type : MeasureEditType.VALUES) {
                mps.add(mapMeasure.getCombinedMultiPolygon(type));
            }
        }
        return JTSUtils.createMP(mps);
    }

    public boolean isFinished() {
        return scoreBefore.size() == scoreAfter.size();
    }

    public void setAfterScore(Indicator indicator) {
        scoreAfter.put(indicator.getID(), indicator.getValue(MapType.MAQUETTE));
    }

    public void setBeforeScore(Indicator indicator) {
        scoreBefore.put(indicator.getID(), indicator.getValue(MapType.MAQUETTE));
    }

    public void setMeasureID(Integer measureID) {
        this.measureID = measureID;
    }

    @Override
    public String toString() {
        return "Prediction for: " + this.getMultiPolygon();
    }
}
