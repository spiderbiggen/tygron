/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.net.serializable.PolygonItem;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.item.UniqueNamedItem;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.StringUtils;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Piece of Land thart can be owned by a stakeholder.
 * @author Maxim Knepfle
 *
 */
public class Land extends UniqueNamedItem implements PolygonItem {

    /**
     *
     */
    private static final long serialVersionUID = -1347763356998229249L;

    @XMLValue
    @ItemIDField("STAKEHOLDERS")
    private Integer ownerID = Item.NONE;

    @XMLValue
    private MultiPolygon polygons = JTSUtils.EMPTY;

    public MultiPolygon getMultiPolygon() {
        return polygons;
    }

    public Stakeholder getOwner() {
        return this.getItem(MapLink.STAKEHOLDERS, this.getOwnerID());
    }

    public Integer getOwnerID() {
        return ownerID;
    }

    @Override
    public MultiPolygon[] getQTMultiPolygons() {
        return new MultiPolygon[] { getMultiPolygon() };
    }

    @Override
    public void reset() {
        super.reset();
        this.polygons.setUserData(null);
        for (Polygon polygon : JTSUtils.getPolygons(polygons)) {
            polygon.setUserData(null);
        }
    }

    public void setOwnerID(Integer ownerID) {
        this.ownerID = ownerID;
    }

    public void setPolygons(MultiPolygon polygons) {
        this.polygons = polygons;
    }

    @Override
    public String toString() {
        return Land.class.getSimpleName() + StringUtils.WHITESPACE + this.getID();
    }

}
