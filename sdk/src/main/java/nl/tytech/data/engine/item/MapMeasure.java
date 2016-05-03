/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.Network;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.net.serializable.PolygonItem;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.data.engine.item.Building.GroundLayerType;
import nl.tytech.data.engine.item.Function.PlacementType;
import nl.tytech.data.engine.item.Global.ReadOnly;
import nl.tytech.data.engine.serializable.MapType;
import nl.tytech.data.engine.serializable.MeasureEditType;
import nl.tytech.data.engine.serializable.MeasureSpatial;
import nl.tytech.data.engine.serializable.UpgradeSpatial;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.StringUtils;
import nl.tytech.util.logger.TLogger;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;

/**
 * A Measure with map interaction; buildings, water, etc.
 *
 * @author Maxim Knepfle
 *
 */
public class MapMeasure extends Measure implements PolygonItem {

    public enum WaterClassification {
        /**
         * Water is alternative and not connected.
         */
        ALTERNATIVE,
        /**
         * Waterway is connected to main water system.
         */
        CONNECTED,
        /**
         * Water is not part of the water assignment.
         */
        NOT_USED
    }

    /**
     *
     */
    private static final long serialVersionUID = -1432031812486067993L;

    @XMLValue
    private Point centerOverride = null;

    @XMLValue
    private double constructionCostsM3 = 0;

    // TODO: (Frank) Fix something for this. What metric is this cost based on? m2 or m3? And will it ever be used?
    @XMLValue
    private double demolishCostsM2 = 0;

    // TODO: (Frank) Fix something for this. What metric is this cost based on? m2 or m3? And will it ever be used?
    @XMLValue
    private double maintenanceCostsM2Year = 0;

    @XMLValue
    @ItemIDField("BUILDINGS")
    private ArrayList<Integer> buildingIDs = new ArrayList<>();

    @XMLValue
    @ListOfClass(UpgradeSpatial.class)
    private ArrayList<UpgradeSpatial> upgradeSpatials = new ArrayList<>(0);

    @XMLValue
    @ListOfClass(MeasureSpatial.class)
    private ArrayList<MeasureSpatial> measureSpatials = new ArrayList<>();

    @XMLValue
    private Double actualHeightChangeM3 = null;

    /**
     * Used to speed up the polygon process when running a normal sim.
     */
    private transient EnumMap<Building.GroundLayerType, MultiPolygon> optimizedPolygonsMap = null;
    private transient EnumMap<Building.GroundLayerType, MultiPolygon> optimizedDemolishPolygonsMap = null;
    private transient MultiPolygon optimizedLandPolygon = null;

    public MapMeasure() {
        super();
    }

    public final void addBuilding(Building building) {
        buildingIDs.add(building.getID());
    }

    public void addMeasureEditTypePolygonsToList(List<MultiPolygon> mps, MeasureEditType measureEditType,
            Building.GroundLayerType groundLayerType) {

        if (measureEditType != MeasureEditType.BUILDING && groundLayerType != Building.GroundLayerType.SURFACE) {
            // Other MeasureEditType polygons are only added for GroundLayerType Surface
            return;
        }

        switch (measureEditType) {
            case BUILDING:
                List<Building> buildings = getBuildings();
                for (Building building : buildings) {
                    MultiPolygon buildingMP = building.getMultiPolygon(null);
                    if (building.getGroundLayerType() == groundLayerType) {
                        mps.add(buildingMP);
                    }
                }
                break;
            case UPGRADE:
                for (UpgradeSpatial UpgradeSpatial : upgradeSpatials) {
                    mps.add(UpgradeSpatial.getMultiPolygon());
                }
                break;
            default:
                for (MeasureSpatial measureSpatial : this.getSpatialsForEditType(measureEditType)) {
                    mps.add(measureSpatial.getOuterMultiPolygon());
                }
                break;

        }
    }

    public MeasureSpatial addMeasureSpatial(MeasureEditType type) {
        int heighestID = 0;
        for (MeasureSpatial measureSpatial : measureSpatials) {
            heighestID = Math.max(heighestID, measureSpatial.getID());
        }
        heighestID++;
        MeasureSpatial spatial = new MeasureSpatial(type, heighestID);
        this.measureSpatials.add(spatial);

        return spatial;
    }

    public UpgradeSpatial addUpgradeSpatial() {

        int heighestID = 0;
        for (UpgradeSpatial spatial : upgradeSpatials) {
            heighestID = Math.max(heighestID, spatial.getID());
        }
        heighestID++;

        ItemMap<UpgradeType> upgrades = this.getMap(MapLink.UPGRADE_TYPES);
        for (UpgradeType upgrade : upgrades) {
            UpgradeSpatial upgradeSpatial = new UpgradeSpatial(heighestID, upgrade.getID());
            this.upgradeSpatials.add(upgradeSpatial);
            return upgradeSpatial;
        }
        TLogger.severe("No Upgrades found!");
        return null;
    }

    public void clearSurfacePolygons() {
        upgradeSpatials.clear();
        measureSpatials.clear();
        this.optimizedPolygonsMap = null;
        this.optimizedDemolishPolygonsMap = null;
    }

    public List<Integer> getBuildingIDs() {
        return buildingIDs;

    }

    /**
     * @return the buildings
     */
    public final List<Building> getBuildings() {
        return getItems(MapLink.BUILDINGS, buildingIDs);
    }

    public Point getCenterPoint() {
        if (centerOverride != null) {
            return centerOverride;
        }
        return JTSUtils.getCenterPoint(this.getPolygons(Building.GroundLayerType.VALUES));
    }

    public Point getCenterPointOverride() {
        return centerOverride;
    }

    public MultiPolygon getCombinedMultiPolygon(MeasureEditType type) {
        List<Geometry> geoms = new ArrayList<>();
        switch (type) {
            case BUILDING:
                for (Building building : getBuildings()) {
                    geoms.add(building.getMultiPolygon(null));
                }
                break;
            case UPGRADE:
                for (UpgradeSpatial UpgradeSpatial : upgradeSpatials) {
                    geoms.add(UpgradeSpatial.getMultiPolygon());
                }
                break;
            default:
                for (MeasureSpatial spatial : getSpatialsForEditType(type)) {
                    geoms.add(spatial.getOuterMultiPolygon());
                }
                break;
        }
        return JTSUtils.createMP(geoms);
    }

    @Override
    public final double getConstructionCosts() {

        if (this.constructionCostsFixed > 0) {
            return this.constructionCostsFixed;
        } else if (this.constructionCostsM3 > 0) {
            return getM3Multiplied(this.constructionCostsM3);
        } else {
            return 0;
        }
    }

    @Override
    public double getConstructionCostsFixed() {
        return constructionCostsFixed;
    }

    public double getConstructionCostsM3() {
        return constructionCostsM3;
    }

    @Override
    public final double getDemolishCosts() {

        if (this.demolishCostsFixed > 0) {
            return this.demolishCostsFixed;
        } else if (this.demolishCostsM2 > 0) {
            return getM3Multiplied(this.demolishCostsM2);
        } else {
            return 0;
        }
    }

    public double getDemolishCostsFixed() {
        return this.demolishCostsFixed;
    }

    public double getDemolishCostsM2() {
        return this.demolishCostsM2;
    }

    public final MultiPolygon getDemolitionPolygons(Building.GroundLayerType groundLayerType) {
        if (optimizedDemolishPolygonsMap == null) {
            optimizedDemolishPolygonsMap = new EnumMap<>(Building.GroundLayerType.class);
        }

        /**
         * In editor mode always create a new map.
         */
        if (!optimizedDemolishPolygonsMap.containsKey(groundLayerType) || isServerSide()) {
            List<MultiPolygon> mps = new ArrayList<>();
            for (MeasureEditType measureEditType : MeasureEditType.VALUES) {
                if (measureEditType == MeasureEditType.UPGRADE) {
                    continue;
                }
                addMeasureEditTypePolygonsToList(mps, measureEditType, groundLayerType);
            }
            optimizedDemolishPolygonsMap.put(groundLayerType, JTSUtils.createMP(mps));
        }
        return optimizedDemolishPolygonsMap.get(groundLayerType);
    }

    public double getHeightChangeM3() {

        if (actualHeightChangeM3 != null) {
            return actualHeightChangeM3;
        }
        return 0;
    }

    public MultiPolygon getLandMultiPolygon() {

        /**
         * In game editor mode always create a new map.
         */
        if (optimizedLandPolygon == null || isServerSide()) {
            List<Geometry> geoms = new ArrayList<>();
            for (MeasureSpatial measureSpatial : measureSpatials) {
                geoms.add(measureSpatial.getOuterMultiPolygon());
            }
            optimizedLandPolygon = JTSUtils.createMP(geoms);
        }
        return optimizedLandPolygon;
    }

    private double getM3Multiplied(double multiplier) {
        return getHeightChangeM3() * multiplier;
    }

    public double getMaintenanceCostsFixedYear() {
        return this.fixedMaintenanceCostsYear;
    }

    public double getMaintenanceCostsM2Year() {
        return this.maintenanceCostsM2Year;
    }

    @Override
    public final double getMaintenanceCostsYear() {

        if (this.fixedMaintenanceCostsYear > 0) {

            return this.fixedMaintenanceCostsYear;
        } else if (this.maintenanceCostsM2Year > 0) {

            return getM3Multiplied(this.maintenanceCostsM2Year);
        } else {

            return 0;
        }
    }

    public MeasureSpatial getMeasureSpatial(Integer measureParamID) {
        for (MeasureSpatial measureSpatial : measureSpatials) {
            if (measureSpatial.getID().equals(measureParamID)) {
                return measureSpatial;
            }
        }
        return null;
    }

    public final double getOpenWaterStorage() {
        double result = 0;
        if (isWaterMeasure()) {
            Global global = this.<Global> getItem(MapLink.GLOBALS, ReadOnly.WATER_STORAGE_ALLOWED_WATER_LEVEL_INCREASE.name());
            double allowedWaterIncrease = global == null ? ReadOnly.WATER_STORAGE_ALLOWED_WATER_LEVEL_INCREASE.getDefaultValue() : global
                    .getActualValue();

            // TODO: (Frank) for now outer ring, until something better has been found
            double totalArea = 0;
            for (MeasureSpatial measureSpatial : getSpatialsForEditType(MeasureEditType.WATER)) {
                totalArea += measureSpatial.getOuterMultiPolygon().getArea();
            }
            result = totalArea * allowedWaterIncrease;
        }
        return result;
    }

    /**
     * Returns all the polygons for this option.
     *
     * @return
     */
    public final MultiPolygon getPolygons(Building.GroundLayerType... groundLayerTypes) {

        if (groundLayerTypes == null || groundLayerTypes.length == 0) {
            TLogger.severe("Trying to get null groundlayer type polygons from measure: " + this.getName());
            return null;
        }

        if (groundLayerTypes.length == 1) {
            return getPolygonsInner(groundLayerTypes[0]);
        } else {
            MultiPolygon allMP = JTSUtils.EMPTY;
            for (Building.GroundLayerType type : groundLayerTypes) {
                allMP = JTSUtils.union(allMP, getPolygonsInner(type));
            }
            return allMP;
        }
    }

    public final MultiPolygon getPolygonsAndCenterPoint(Building.GroundLayerType... groundLayerTypes) {
        MultiPolygon mp = getPolygons(groundLayerTypes);
        if (centerOverride != null) {
            mp = JTSUtils.createMP(mp, centerOverride.buffer(0.01));
        }
        return mp;
    }

    private MultiPolygon getPolygonsInner(Building.GroundLayerType type) {

        if (optimizedPolygonsMap == null) {
            optimizedPolygonsMap = new EnumMap<>(Building.GroundLayerType.class);
        }
        /**
         * In game editor mode always create a new map.
         */
        if (!optimizedPolygonsMap.containsKey(type) || isServerSide()) {
            List<MultiPolygon> mps = new ArrayList<>();
            for (MeasureEditType mtype : MeasureEditType.VALUES) {
                addMeasureEditTypePolygonsToList(mps, mtype, type);
            }
            optimizedPolygonsMap.put(type, JTSUtils.createMP(mps));
        }
        return optimizedPolygonsMap.get(type);
    }

    @Override
    public final MultiPolygon[] getQTMultiPolygons() {
        return new MultiPolygon[] { getLandMultiPolygon() };
    }

    public List<MeasureSpatial> getSpatials() {
        return measureSpatials;
    }

    public List<MeasureSpatial> getSpatialsForEditType(MeasureEditType type) {
        List<MeasureSpatial> spatials = new ArrayList<>();
        for (MeasureSpatial measureSpatial : measureSpatials) {
            if (type == measureSpatial.getMeasureEditType()) {
                spatials.add(measureSpatial);
            }
        }
        return spatials;
    }

    /**
     * @return the storage
     */
    public final double getTotalWaterStorage() {
        return getOpenWaterStorage() + getAdditionalFixedWaterStorage();

    }

    public UpgradeSpatial getUpgradeSpatial(Integer measureParamID) {
        for (UpgradeSpatial upgradeSpatial : upgradeSpatials) {
            if (upgradeSpatial.getID().equals(measureParamID)) {
                return upgradeSpatial;
            }
        }
        return null;
    }

    public List<UpgradeSpatial> getUpgradeSpatials() {
        return upgradeSpatials;
    }

    public boolean hasSpatial(Integer measureSpatialID) {
        return getMeasureSpatial(measureSpatialID) == null;
    }

    public boolean hasSpatialForEditType(MeasureEditType type) {
        for (MeasureSpatial measureSpatial : measureSpatials) {
            if (type == measureSpatial.getMeasureEditType()) {
                return true;
            }
        }
        return false;
    }

    public boolean isDikeMeasure() {
        return hasSpatialForEditType(MeasureEditType.DIKE);
    }

    public boolean isFlattenMeasure() {
        return hasSpatialForEditType(MeasureEditType.FLATTEN);
    }

    private boolean isFreeForEditType(MapType mapType, MultiPolygon mp, Building myBuilding, MeasureEditType measureEditType) {
        GroundLayerType groundLayerType = myBuilding.getGroundLayerType();

        if (groundLayerType != Building.GroundLayerType.SURFACE && measureEditType != MeasureEditType.BUILDING) {
            // (Frank) Other MeasureEditType polygons are only added for GroundLayerType Surface
            return false;
        }

        switch (measureEditType) {
            case BUILDING:
                List<Building> buildings = getBuildings();
                for (Building otherBuilding : buildings) {
                    if (otherBuilding.getID().equals(myBuilding.getID())) {
                        continue;
                    }

                    MultiPolygon otherBuildingMP = otherBuilding.getMultiPolygon(mapType);
                    if (otherBuilding.getGroundLayerType() == groundLayerType && JTSUtils.intersectsBorderExcluded(otherBuildingMP, mp)) {
                        return false;
                    }
                }
                return true;
            case UPGRADE:
                for (UpgradeSpatial UpgradeSpatial : upgradeSpatials) {
                    if (JTSUtils.intersectsBorderExcluded(UpgradeSpatial.getMultiPolygon(), mp)) {
                        return false;
                    }
                }
                return true;
            default:
                for (MeasureSpatial measureSpatial : measureSpatials) {
                    if (measureSpatial.getMeasureEditType() == measureEditType) {
                        if (myBuilding.getFunction().getPlacementType() == PlacementType.WATER
                                && measureSpatial.getMeasureEditType() == MeasureEditType.WATER) {
                            continue;
                        } else if (myBuilding.getFunction().getPlacementType() == PlacementType.LAND
                                && (measureSpatial.getMeasureEditType() == MeasureEditType.DIKE || measureSpatial.getMeasureEditType() == MeasureEditType.FLATTEN)) {
                            continue;
                        }
                        if (JTSUtils.intersectsBorderExcluded(measureSpatial.getOuterMultiPolygon(), mp)) {
                            return false;
                        }
                    }
                }
                return true;
        }

    }

    public boolean isFreeForNonHeightMapEditType(MapType mapType, MultiPolygon mp, Building building) {
        GroundLayerType groundLayerType = building.getGroundLayerType();
        if (Building.GroundLayerType.UNDERGROUND == groundLayerType) {
            return isFreeForEditType(mapType, mp, building, MeasureEditType.BUILDING);
        }

        boolean isFree = true;
        for (MeasureEditType type : MeasureEditType.VALUES) {
            isFree &= isFreeForEditType(mapType, mp, building, type);

        }
        return isFree;
    }

    @Override
    public boolean isPhysical() {
        for (GroundLayerType type : GroundLayerType.VALUES) {
            if (JTSUtils.containsData(this.getPolygonsInner(type))) {
                return true;
            }
        }
        return false;
    }

    private boolean isServerSide() {
        return this.getLord().getSessionType() == Network.SessionType.EDITOR && this.getLord().isServerSide();
    }

    public boolean isUpgradeMeasure() {
        // return upgradePolygons != null;
        return !upgradeSpatials.isEmpty();
    }

    public boolean isWaterMeasure() {
        return hasSpatialForEditType(MeasureEditType.WATER);
    }

    public void removeBuildingID(Integer id) {
        buildingIDs.remove(id);
    }

    public boolean removeSpatial(Integer measureSpatialID) {
        for (MeasureSpatial measureSpatial : measureSpatials) {
            if (measureSpatial.getID().equals(measureSpatialID)) {
                return measureSpatials.remove(measureSpatial);
            }
        }
        return false;
    }

    public boolean removeUpgradeSpatial(Integer upgradeSpatialID) {
        for (UpgradeSpatial upgradeSpatial : upgradeSpatials) {
            if (upgradeSpatial.getID().equals(upgradeSpatialID)) {
                return upgradeSpatials.remove(upgradeSpatial);
            }
        }
        return false;
    }

    @Override
    public void reset() {
        super.reset();
        optimizedPolygonsMap = null;
        optimizedDemolishPolygonsMap = null;
        optimizedLandPolygon = null;
    }

    /**
     * @param buildings the buildings to set
     */
    public final void setBuildings(List<Building> buildings) {

        this.buildingIDs.clear();
        for (Building building : buildings) {
            this.buildingIDs.add(building.getID());
        }
    }

    public void setCenterPointOverride(Point centerPointOverride) {
        centerOverride = centerPointOverride;
    }

    public void setConstructionCostsM3(double costs) {
        this.constructionCostsM3 = costs;
    }

    public void setCostM2(CostType costType, double value) {
        switch (costType) {
            case CONSTRUCTION:
                this.constructionCostsM3 = value;
                break;
            case DEMOLITION:
                this.demolishCostsM2 = value;
                break;
            case MAINTENANCE:
                this.maintenanceCostsM2Year = value;
                break;
        }

    }

    public void setDemolitionCostsM2(double costs) {
        this.demolishCostsM2 = costs;
    }

    public void setHeightChange(double heightChangeM3) {
        this.actualHeightChangeM3 = heightChangeM3;
    }

    public void setMaintenanceCostsM2Year(double cost) {
        this.maintenanceCostsM2Year = cost;
    }

    @Override
    public String validated(boolean startNewGame) {

        String result = StringUtils.EMPTY;
        /**
         * Validate building linkage and timestate.
         */
        for (Building building : this.getBuildings()) {
            if (!building.getMeasureID().equals(this.getID())) {
                result += "\nMeasure: " + this.getName() + " links to building: " + building.getName()
                        + " that does not have a valid measure link back.";
            }
        }
        return result + super.validated(startNewGame);
    }
}
