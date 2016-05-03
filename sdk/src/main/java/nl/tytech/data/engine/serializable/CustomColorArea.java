/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.serializable;

import java.io.Serializable;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.Item;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;
import com.vividsolutions.jts.geom.MultiPolygon;

/**
 *
 * @author Frank Baars
 *
 */
public class CustomColorArea implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -6859295119584860624L;

    @XMLValue
    private Integer zoneID = Item.NONE;

    @XMLValue
    private String name = StringUtils.EMPTY;

    @XMLValue
    private TColor color = TColor.BLUE;

    @XMLValue
    protected MultiPolygon polygons = JTSUtils.EMPTY;

    public CustomColorArea() {

    }

    public CustomColorArea(Integer zoneID, String name, TColor color) {
        this.zoneID = zoneID;
        this.name = name;
        this.color = color;
    }

    public TColor getColor() {
        return color;
    }

    public Integer getID() {
        return zoneID;
    }

    public MultiPolygon getMultiPolygon() {
        return polygons;
    }

    public String getName() {
        return name;
    }

    public void setColor(TColor color) {
        this.color = color;
    }

    public void setMultiPolygon(MultiPolygon multiPolygon) {
        this.polygons = multiPolygon;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name + " (id=" + zoneID + ") " + color;
    }

}
