/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.item.PipeLoad.LoadParameterType;
import nl.tytech.data.engine.item.PipeLoad.LoadType;
import nl.tytech.data.engine.serializable.Address;
import nl.tytech.data.engine.serializable.MapType;
import nl.tytech.data.engine.serializable.TimeState;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;

/**
 * Cluster of pipe loads that can be activated togheter.
 *
 * @author Maxim Knepfle
 *
 */
public class PipeCluster extends Item {

    public final static TColor SUPPLY = TColor.ORANGE;
    public final static TColor DEMAND = TColor.YELLOW;

    public final static TColor NOT_CONNECTED = TColor.WHITE;

    public static final String GUI_IMAGES_FLOWGROUPS = "Gui/Images/Flowgroups/";

    /**
     *
     */
    private static final long serialVersionUID = 7992398125463026642L;

    @ItemIDField("STAKEHOLDERS")
    @XMLValue
    private Integer ownerID = Item.NONE;

    @XMLValue
    private String name = StringUtils.EMPTY;

    @XMLValue
    private boolean firstTimeConnect = true;

    @ItemIDField("LEVELS")
    @XMLValue
    private Integer levelID = Item.NONE;

    @Deprecated
    @XMLValue
    public TimeState connectedState = null;

    @XMLValue
    private Double fractionConnectedOverride = null;

    @XMLValue
    @ItemIDField("PIPE_LOADS")
    private ArrayList<Integer> loadIDs = new ArrayList<>();

    public PipeCluster() {

    }

    public boolean addLoadID(Integer loadID) {
        if (!this.loadIDs.contains(loadID)) {
            this.loadIDs.add(loadID);
            return true;
        }
        return false;

    }

    public List<PipeLoad> getActiveLoads() {
        TimeState timeState = getConnectedState();
        List<PipeLoad> activeLoads = new ArrayList<>();
        for (PipeLoad load : this.getLoads()) {
            if (load.getConnectionState() == timeState) {
                activeLoads.add(load);
            }
        }
        return activeLoads;
    }

    public List<Integer> getBuildingIDs() {

        List<Integer> buildings = new ArrayList<>();
        for (PipeLoad load : this.getLoads()) {
            buildings.add(load.getBuildingID());
        }

        return buildings;
    }

    public List<Building> getBuildings() {

        List<Building> buildings = new ArrayList<>();
        for (PipeLoad load : this.getLoads()) {
            if (load.getBuilding() != null) {
                buildings.add(load.getBuilding());
            }
        }

        return buildings;
    }

    public Point getCenterPoint() {

        if (this.getLoads().size() > 0) {
            List<Geometry> geoms = new ArrayList<>();
            for (PipeLoad load : this.getLoads()) {
                geoms.add(load.getPoint());
            }
            return JTSUtils.getCenterPoint(geoms);
        }
        return null;
    }

    public TimeState getConnectedState() {
        TimeState bestTimeState = TimeState.NOTHING;
        for (PipeLoad load : this.getLoads()) {
            if (load.getConnectionState().after(bestTimeState)) {
                bestTimeState = load.getConnectionState();
            }
        }
        return bestTimeState;
    }

    public double getConnectionCosts() {

        double cost = 0;
        for (PipeLoad load : this.getActiveLoads()) {
            cost += load.getConnectionCosts();
        }
        return cost;
    }

    public int getConnectionCount() {

        int count = 0;
        for (PipeLoad load : this.getActiveLoads()) {
            count += load.getConnectionCount();
        }
        return count;
    }

    public double getFlow() {

        double flow = 0;
        for (PipeLoad load : this.getActiveLoads()) {
            flow += load.getFlow();
        }
        return flow;
    }

    public double getFractionConnected() {
        if (fractionConnectedOverride != null) {
            return fractionConnectedOverride;
        } else {
            PipeSetting pipeSetting = getItem(MapLink.PIPE_SETTINGS, PipeSetting.Type.CLUSTER_FRACTION_CONNECTED);
            return pipeSetting.getDoubleValue();
        }
    }

    public String getImageName() {

        Stakeholder stakeholder = getOwner();
        if (stakeholder == null) {
            return StringUtils.EMPTY;
        }
        return stakeholder.getPortrait();

    }

    public Level getLevel() {
        return this.getItem(MapLink.LEVELS, this.getLevelID());
    }

    public Integer getLevelID() {
        return levelID;
    }

    public List<PipeLoad> getLoads() {
        return this.getItems(MapLink.PIPE_LOADS, this.loadIDs);
    }

    public MultiPolygon getMultiPolygon(MapType mapType) {

        List<MultiPolygon> mps = new ArrayList<>();
        for (Building building : this.getBuildings()) {
            mps.add(building.getMultiPolygon(mapType));
        }
        MultiPolygon mp = JTSUtils.createMP(mps);

        return mp;
    }

    public String getName() {

        // set name
        if (StringUtils.containsData(name)) {
            return name;
        }

        List<PipeLoad> pipeLoads = getLoads();
        if (pipeLoads.size() > 0) {
            PipeLoad pipeLoad = getLoads().get(0);
            Address address = pipeLoad.getMainAddress();
            if (pipeLoads.size() == 1 && address != null && StringUtils.containsData(address.getAddressCode())) {
                return address.getAddressCode();
            }
            // else take first building
            for (Building building : this.getBuildings()) {
                return building.getName();
            }
        }

        // return id name
        return this.getClass().getSimpleName() + StringUtils.WHITESPACE + this.getID();
    }

    public Stakeholder getOwner() {
        return this.getItem(MapLink.STAKEHOLDERS, this.getOwnerID());
    }

    public Integer getOwnerID() {
        return ownerID;
    }

    public double getParameterValue(MapType mapType, LoadParameterType param) {
        double result = 0;
        for (PipeLoad pipeLoad : getActiveLoads()) {
            result += pipeLoad.getParameterValue(mapType, param);
        }
        return result;
    }

    public String getPopupDescription() {
        String result = getName();
        List<Building> buildings = getBuildings();
        if (!buildings.isEmpty()) {
            result += " (" + buildings.get(0).getName().split("\\(")[0].trim() + ")";
        }
        return result;
    }

    public double getPower() {

        double power = 0;
        for (PipeLoad load : this.getActiveLoads()) {
            power += load.getPower();
        }
        return power;
    }

    public LoadType getType() {
        for (PipeLoad load : this.getActiveLoads()) {
            return load.getType();
        }
        return LoadType.UNKNOWN;
    }

    public boolean hasFractionConnectedOverride() {
        return fractionConnectedOverride != null;
    }

    public boolean isActive() {
        return TimeState.READY.equals(getConnectedState());
    }

    public boolean isConsumer() {
        return getFlow() < 0;
    }

    public boolean isFirstTimeConnect() {
        return firstTimeConnect;
    }

    public boolean isLocated() {
        return getCenterPoint() != null;
    }

    public boolean isProducer() {
        return getFlow() > 0;
    }

    public boolean removeLoadID(Integer pipeLoadID) {
        return this.loadIDs.remove(pipeLoadID);
    }

    public void setFirstTimeConnect(boolean firstTimeConnect) {
        this.firstTimeConnect = firstTimeConnect;
    }

    public void setFractionConnected(Double fractionConnected) {
        this.fractionConnectedOverride = fractionConnected;
    }

    public void setLevelID(Integer levelID) {
        this.levelID = levelID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOwnerID(Integer ownerID) {
        this.ownerID = ownerID;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
