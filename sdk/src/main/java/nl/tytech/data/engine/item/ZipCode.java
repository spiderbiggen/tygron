/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.PolygonItem;
import nl.tytech.data.core.item.UniqueNamedItem;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.color.ColorUtil;
import nl.tytech.util.color.TColor;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

/**
 * @author Maxim Knepfle
 */
public class ZipCode extends UniqueNamedItem implements PolygonItem {

    private static final long serialVersionUID = 1362611699406604464L;

    @XMLValue
    private TColor color = ColorUtil.createRandomRGB();

    @XMLValue
    private MultiPolygon polygons = JTSUtils.EMPTY;

    public ZipCode() {
    }

    public TColor getColor() {
        return this.color;
    }

    @Override
    public String getDescription() {
        return getName();
    }

    public MultiPolygon getMultiPolygon() {
        return this.polygons;
    }

    @Override
    public MultiPolygon[] getQTMultiPolygons() {
        return new MultiPolygon[] { polygons, };
    }

    @Override
    public void reset() {
        super.reset();
        this.polygons.setUserData(null);
        for (Polygon polygon : JTSUtils.getPolygons(polygons)) {
            polygon.setUserData(null);
        }
    }

    public void setMultiPolygon(MultiPolygon mp) {
        this.polygons = mp;
    }
}
