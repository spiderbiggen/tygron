/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.net.serializable.PolygonItem;
import nl.tytech.data.core.item.UniqueNamedItem;
import nl.tytech.data.engine.serializable.MapType;
import nl.tytech.data.engine.serializable.MeasureSpatial;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.MathUtils;
import nl.tytech.util.ObjectUtils;
import nl.tytech.util.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * HeightSector
 * @author Maxim Knepfle
 */
public class HeightSector extends UniqueNamedItem implements PolygonItem {

    private class HeightSquare {

        private double x;
        private double y;
        private double xMin;
        private double yMin;
        private double xMax;
        private double yMax;

        public HeightSquare(double x, double y, int xMin, int yMin, int xMax, int yMax) {
            this.x = x;
            this.y = y;
            this.xMin = xMin;
            this.yMin = yMin;
            this.xMax = xMax;
            this.yMax = yMax;
        }

        public double getQ11() {
            return (xMax - x) * (yMax - y);
        }

        public double getQ12() {
            return (xMax - x) * (y - yMin);
        }

        public double getQ21() {
            return (x - xMin) * (yMax - y);
        }

        public double getQ22() {
            return (x - xMin) * (y - yMin);
        }

        public double getX1() {
            return (xMax - x);
        }

        public double getX2() {
            return (x - xMin);
        }

        public double getY1() {
            return (yMax - y);
        }

        public double getY2() {
            return (y - yMin);
        }
    }

    public enum HeightValue {

        AVG,

        MIN,

        MAX,

        STDEV;

    }

    public final static int DEFAULT_SECTOR_SIZE_M = 500;

    public final static double DEFAULT_POINT_DISTANCE_M = 2.5;

    private static final long serialVersionUID = -4253782210241563578L;

    @XMLValue
    private float[] current = new float[0];

    @XMLValue
    private float[] maquette = new float[0];

    @JsonIgnore
    private transient float[] buildingHeight = new float[0];

    @XMLValue
    private MultiPolygon square;

    private transient Envelope envelope = null;

    /**
     * Var for client side check if map has changed
     */
    private transient Boolean changed = null;

    public HeightSector() {

    }

    private double calculateAndChangeHeights(MapType mapType, MeasureSpatial measureSpatial, boolean calculateOnly) {
        MultiPolygon multiPolygon = measureSpatial.getOuterMultiPolygon();
        double heightChangeM3 = 0;
        int[] boundaries = getBoundaries(multiPolygon);

        if (boundaries == null) {
            return heightChangeM3;
        }

        for (int x = boundaries[0]; x <= boundaries[2]; ++x) {
            for (int y = boundaries[1]; y <= boundaries[3]; ++y) {

                Double originalHeight = getHeightForIndexes(mapType, x, y);
                if (originalHeight == null) {
                    continue;
                }

                Point point = getPointForXY(x, y);
                double newHeight = measureSpatial.getHeight(point, originalHeight);

                if (Math.abs(newHeight - originalHeight) > 0.01) {
                    if (!calculateOnly) {
                        setHeight(mapType, x, y, newHeight);
                    }
                    heightChangeM3 += HeightSector.DEFAULT_POINT_DISTANCE_M * HeightSector.DEFAULT_POINT_DISTANCE_M
                            * Math.abs(newHeight - originalHeight);
                }
            }
        }
        return heightChangeM3;
    }

    /**
     *
     * @return Changed height in volume M3
     */
    public double changeHeights(MapType mapType, MeasureSpatial measureSpatial) {
        return calculateAndChangeHeights(mapType, measureSpatial, false);
    }

    public int[] getBoundaries(MultiPolygon multiPolygon) {
        MultiPolygon intersection = JTSUtils.intersection(multiPolygon, square);
        if (!JTSUtils.containsData(intersection)) {
            return null;
        }

        Envelope env = intersection.getEnvelopeInternal();
        HeightSquare xyupperLeft = getXYArray(env.getMinX(), env.getMinY());
        HeightSquare xylowerright = getXYArray(env.getMaxX(), env.getMaxY());
        return new int[] { (int) Math.round(xyupperLeft.x), (int) Math.round(xyupperLeft.y), (int) Math.round(xylowerright.x),
                (int) Math.round(xylowerright.y) };

    }

    public Point getCenterPoint() {
        double x = this.getStartX() + this.getWidthM() / 2d;
        double y = this.getStartY() + this.getWidthM() / 2d;
        return JTSUtils.createPoint(x, y);
    }

    public float[] getData(MapType mapType) {
        return getData(mapType, false);
    }

    public float[] getData(MapType mapType, boolean building) {

        if (building) {
            return this.buildingHeight;
        }

        // current return current
        if (mapType == null || mapType == MapType.CURRENT) {
            return current;
        }
        // maquette only when available
        return maquette.length == current.length ? maquette : current;
    }

    /**
     * Get the approximate interpolated value at world coordinates X,Y
     * @param wx
     * @param wy
     * @return aprox value
     */
    public Double getHeight(MapType mapType, double wx, double wy) {

        HeightSquare xy = getXYArray(wx, wy);
        return getHeightForCoordinate(mapType, xy);
    }

    private Double getHeightBilinear(MapType mapType, HeightSquare heightSquare) {
        double count = 0;
        double height = 0;
        Double h11 = getHeightForIndexes(mapType, (int) heightSquare.xMin, (int) heightSquare.yMin);
        Double h21 = getHeightForIndexes(mapType, (int) heightSquare.xMax, (int) heightSquare.yMin);
        Double h12 = getHeightForIndexes(mapType, (int) heightSquare.xMin, (int) heightSquare.yMax);
        Double h22 = getHeightForIndexes(mapType, (int) heightSquare.xMax, (int) heightSquare.yMax);

        if (h11 != null) {
            height += heightSquare.getQ11() * h11;
            count += heightSquare.getQ11();
        }
        if (h21 != null) {
            height += heightSquare.getQ21() * h21;
            count += heightSquare.getQ21();
        }
        if (h12 != null) {
            height += heightSquare.getQ12() * h12;
            count += heightSquare.getQ12();
        }
        if (h22 != null) {
            height += heightSquare.getQ22() * h22;
            count += heightSquare.getQ22();
        }

        if (count == 0) {
            return null;
        }

        return height / count;
    }

    private Double getHeightForCoordinate(MapType mapType, HeightSquare heightSquare) {

        if (heightSquare.xMin == heightSquare.xMax && heightSquare.yMin == heightSquare.yMax) {
            return getHeightForIndexes(mapType, (int) heightSquare.xMin, (int) heightSquare.yMin);
        }

        if (heightSquare.xMin != heightSquare.xMax && heightSquare.yMin == heightSquare.yMax) {
            return getHeightForLinearX(mapType, heightSquare);
        }

        if (heightSquare.xMin == heightSquare.xMax && heightSquare.yMin != heightSquare.yMax) {
            return getHeightForLinearY(mapType, heightSquare);
        }

        return getHeightBilinear(mapType, heightSquare);

    }

    public Double getHeightForIndexes(MapType mapType, int x, int y) {
        int loc = y * getWidthPoints() + x;
        float[] data = this.getData(mapType);
        // guard against round offs outside my square
        if (loc < 0 || loc >= data.length) {
            return null;
        }

        if (data[loc] <= Short.MIN_VALUE) {
            return null;
        }
        return (double) data[loc];

    }

    private Double getHeightForLinearX(MapType mapType, HeightSquare heightCoordinate) {
        Double h11 = getHeightForIndexes(mapType, (int) heightCoordinate.xMin, (int) heightCoordinate.yMin);
        Double h21 = getHeightForIndexes(mapType, (int) heightCoordinate.xMax, (int) heightCoordinate.yMin);
        double count = 0;
        double height = 0;

        if (h11 != null) {
            height += heightCoordinate.getX1() * h11;
            count += heightCoordinate.getX1();
        }
        if (h21 != null) {
            height += heightCoordinate.getX2() * h21;
            count += heightCoordinate.getX2();
        }
        if (count == 0) {
            return null;
        }

        return height / count;
    }

    private Double getHeightForLinearY(MapType mapType, HeightSquare heightCoordinate) {
        Double h11 = getHeightForIndexes(mapType, (int) heightCoordinate.xMin, (int) heightCoordinate.yMin);
        Double h12 = getHeightForIndexes(mapType, (int) heightCoordinate.xMin, (int) heightCoordinate.yMax);

        double count = 0;
        double height = 0;

        if (h11 != null) {
            height += heightCoordinate.getY1() * h11;
            count += heightCoordinate.getY1();
        }
        if (h12 != null) {
            height += heightCoordinate.getY2() * h12;
            count += heightCoordinate.getY2();
        }
        if (count == 0) {
            return null;
        }

        return height / count;
    }

    public double getHeightM() {
        return square.getEnvelopeInternal().getHeight();
    }

    public Point getPointForXY(double x, double y) {

        if (envelope == null) {
            envelope = square.getEnvelopeInternal();
        }
        double width = getWidthPoints();
        double px = envelope.getMinX() + (x / width) * (envelope.getMaxX() - envelope.getMinX());
        double py = envelope.getMinY() + (y / width) * (envelope.getMaxY() - envelope.getMinY());

        return JTSUtils.createPoint(px, py);
    }

    public double getPredictionChangedHeights(MapType mapType, MeasureSpatial measureSpatial) {
        return calculateAndChangeHeights(mapType, measureSpatial, true);
    }

    @Override
    public MultiPolygon[] getQTMultiPolygons() {
        return new MultiPolygon[] { square };
    }

    public String getSatImageLocation() {
        Setting setting = this.getItem(MapLink.SETTINGS, Setting.Type.SATELLITE_FILE_NAME);
        return Setting.SATELLITE_IMAGE_LOCATION + setting.getValue() + this.getID() + ".jpg";
    }

    public MultiPolygon getSquare() {
        return this.square;
    }

    public double getStartX() {
        return square.getEnvelopeInternal().getMinX();
    }

    public double getStartY() {
        return square.getEnvelopeInternal().getMinY();
    }

    public double getWidthM() {
        return square.getEnvelopeInternal().getWidth();
    }

    public int getWidthPoints() {
        return getWidthPoints(false);
    }

    public int getWidthPoints(boolean building) {
        if (building) {
            return (int) Math.round((buildingHeight.length == 0 ? 0 : Math.sqrt(buildingHeight.length)));
        } else {
            return (int) Math.round((current.length == 0 ? 0 : Math.sqrt(current.length)));
        }
    }

    private HeightSquare getXYArray(double wx, double wy) {
        wx -= getStartX();
        wy -= getStartY();
        double width = this.getWidthM();
        double percentageX = MathUtils.clamp(wx / width, 0d, 1d);
        double percentageY = MathUtils.clamp(wy / width, 0d, 1d);

        double x = (getWidthPoints() - 1) * percentageX;
        double y = (getWidthPoints() - 1) * percentageY;

        return new HeightSquare(x, y, (int) Math.floor(x), (int) Math.floor(y), (int) Math.ceil(x), (int) Math.ceil(y));
    }

    /**
     * Check client side only if there is a difference between the CURRENT and MAQUETTE maps
     * @return
     */
    public boolean isClientSideChanged() {

        if (changed == null) {
            changed = false;

            // check for changes
            if (this.maquette != null && this.maquette.length == this.current.length) {
                for (int i = 0; i < current.length; i++) {
                    if (current[i] != maquette[i]) {
                        changed = true;
                        break;
                    }
                }
            }
        }
        return changed;
    }

    @Override
    public void reset() {
        super.reset();
        this.square.setUserData(null);
        for (Polygon polygon : JTSUtils.getPolygons(square)) {
            polygon.setUserData(null);
        }
    }

    /**
     * Only use for setting data once at start
     * @param mapType
     * @param data
     */
    public void setBaseHeightData(float[] data) {
        this.setBaseHeightData(data, false);
    }

    public void setBaseHeightData(float[] data, boolean includeBuilding) {

        if (includeBuilding) {
            this.buildingHeight = data;
        } else {
            this.current = data;
            this.maquette = new float[0];
        }
    }

    /**
     * Set value and round it off to 1 decimal place
     * @param mapType
     * @param x
     * @param y
     * @param value
     */
    public void setHeight(MapType mapType, int x, int y, double value) {

        if (mapType == MapType.CURRENT) {
            current[y * getWidthPoints() + x] = (float) MathUtils.round(value, 1);
            return;
        }

        // lazy create maquette data
        if (maquette == null | maquette.length != current.length) {
            maquette = ObjectUtils.deepCopy(current);
        }
        maquette[y * getWidthPoints() + x] = (float) MathUtils.round(value, 1);
    }

    public void setSquare(MultiPolygon square) {
        this.square = square;
        this.envelope = square.getEnvelopeInternal();
    }

    @Override
    public String toString() {
        return StringUtils.EMPTY + this.getID();
    }
}
