/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.serializable;

import java.io.Serializable;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.Lord;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.item.UpgradeType;
import nl.tytech.util.JTSUtils;
import com.vividsolutions.jts.geom.MultiPolygon;

/**
 *
 * @author Frank Baars
 *
 */
public class UpgradeSpatial implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 3297087351882502145L;

    @XMLValue
    private MultiPolygon multiPolygon = JTSUtils.EMPTY;

    @XMLValue
    private Integer id = Item.NONE;

    @XMLValue
    @ItemIDField("UPGRADE_TYPES")
    private Integer upgradeID = Item.NONE;

    public UpgradeSpatial() {

    }

    public UpgradeSpatial(Integer id, Integer upgradeID) {
        this.id = id;
        this.upgradeID = upgradeID;
    }

    public void addMultiPolygon(MultiPolygon add) {
        this.multiPolygon = JTSUtils.union(this.multiPolygon, add);
    }

    public Integer getID() {
        return id;
    }

    public MultiPolygon getMultiPolygon() {
        return multiPolygon;
    }

    public Integer getUpgradeID() {
        return upgradeID;
    }

    public UpgradeType getUpgradeType(Lord lord) {
        return lord.<UpgradeType> getMap(MapLink.UPGRADE_TYPES).get(upgradeID);
    }

    public void removeMultiPolygon(MultiPolygon remove) {
        this.multiPolygon = JTSUtils.difference(this.multiPolygon, remove);
    }

    public void setID(Integer id) {
        this.id = id;
    }

    public void setMultiPolygon(MultiPolygon multiPolygon) {
        this.multiPolygon = multiPolygon;
    }

    public void setUpgradeID(Integer upgradeID) {
        this.upgradeID = upgradeID;
    }

}
