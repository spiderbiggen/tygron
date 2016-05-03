/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import java.util.HashMap;
import java.util.Set;
import nl.tytech.core.item.annotations.DoNotSaveToInit;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.PolygonItem;
import nl.tytech.data.core.item.UniqueNamedItem;
import nl.tytech.data.engine.serializable.MapType;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.color.ColorUtil;
import nl.tytech.util.color.TColor;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

/**
 * @author Maxim Knepfle
 */
public class Area extends UniqueNamedItem implements PolygonItem {

    private static final long serialVersionUID = 1362611699406604463L;

    public final static double FLOOD_THRESHOLD_IN_M = 0.03;

    /**
     * Attribute name: SAFE_ZONE
     */
    public final static String SAFE_ZONE = "SAFE_ZONE";

    /**
     * Attribute name: FLOODING_HEIGHT
     */
    public static final String FLOODING_HEIGHT = "FLOODING_HEIGHT";

    @XMLValue
    private boolean active = true;

    @XMLValue
    private TColor color = ColorUtil.createRandomRGB();

    @XMLValue
    private MultiPolygon polygons = JTSUtils.EMPTY;

    @XMLValue
    @DoNotSaveToInit
    private MultiPolygon maquettePolygons = null;

    @XMLValue
    private TColor maquetteColor = null;

    @XMLValue
    private HashMap<String, Double> attributes = new HashMap<>();

    public Area() {
    }

    public double getAttribute(String key) {
        Double value = this.attributes.get(key);
        return value == null ? 0 : value;
    }

    public Set<String> getAttributes() {
        return this.attributes.keySet();
    }

    public TColor getColor(MapType mapType) {
        if (mapType == MapType.MAQUETTE && maquetteColor != null) {
            return this.maquetteColor;
        } else {
            return this.color;
        }
    }

    @Override
    public String getDescription() {
        return getName();
    }

    public MultiPolygon getMultiPolygon(MapType mapType) {
        if (mapType == MapType.MAQUETTE && maquettePolygons != null) {
            return maquettePolygons;
        }
        return this.polygons;
    }

    @Override
    public MultiPolygon[] getQTMultiPolygons() {
        return new MultiPolygon[] { polygons, };
    }

    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }

    public boolean hasMaquettePolygons() {
        return this.maquettePolygons != null;
    }

    public final boolean isActive() {
        return this.active;
    }

    public boolean removeAttribute(String key) {
        return this.attributes.remove(key) != null;
    }

    @Override
    public void reset() {

        super.reset();

        this.polygons.setUserData(null);
        for (Polygon polygon : JTSUtils.getPolygons(polygons)) {
            polygon.setUserData(null);
        }
        if (this.maquettePolygons != null) {
            this.maquettePolygons.setUserData(null);
            for (Polygon polygon : JTSUtils.getPolygons(maquettePolygons)) {
                polygon.setUserData(null);
            }
        }
    }

    public final void setActive(boolean active) {
        this.active = active;
    }

    public void setAttribute(String key, double value) {
        this.attributes.put(key, value);
    }

    public void setColor(MapType mapType, TColor color) {

        if (mapType == MapType.MAQUETTE) {
            this.maquetteColor = color;
        } else {
            this.color = color;
        }
    }

    public void setMultiPolygon(MapType mapType, MultiPolygon mp) {

        if (mapType == MapType.MAQUETTE) {
            this.maquettePolygons = mp;
        } else {
            this.polygons = mp;
        }
    }
}
