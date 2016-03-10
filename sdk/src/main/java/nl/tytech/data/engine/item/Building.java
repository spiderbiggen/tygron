/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.net.serializable.PolygonItem;
import nl.tytech.core.util.DetailUtils;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.item.UniqueNamedItem;
import nl.tytech.data.engine.item.ClientWord.ClientTerms;
import nl.tytech.data.engine.item.Function.Value;
import nl.tytech.data.engine.item.ModelData.Stack;
import nl.tytech.data.engine.item.Overlay.OverlayType;
import nl.tytech.data.engine.item.UnitData.TrafficType;
import nl.tytech.data.engine.other.TimeStateItem;
import nl.tytech.data.engine.other.ValueItem;
import nl.tytech.data.engine.serializable.Address;
import nl.tytech.data.engine.serializable.Category;
import nl.tytech.data.engine.serializable.CategoryValue;
import nl.tytech.data.engine.serializable.FunctionValue;
import nl.tytech.data.engine.serializable.MapType;
import nl.tytech.data.engine.serializable.TimeState;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;
import nl.tytech.util.logger.TLogger;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.prep.PreparedGeometry;
import com.vividsolutions.jts.geom.prep.PreparedGeometryFactory;
import straightskeleton.Skeleton;

/**
 * Building
 * <p>
 * This class keeps track of the basic Building functionality and is abstract!
 * </p>
 * @author Maxim Knepfle
 */
public class Building extends UniqueNamedItem implements TimeStateItem, PolygonItem, ValueItem {

    public enum Detail {
        /**
         * Total number of parking spaces in this building.
         */
        PARKING_LOTS,
        /**
         * Total number of required parking space
         */
        PARKING_LOTS_DEMAND,
        /**
         * Total cost for constructing this building.
         */
        CONSTRUCTION_COST,
        /**
         * Total cost for demolishing (+buyout) this building.
         */
        DEMOLISH_COST,
        /**
         * Cost for buyout of inhabitants
         */
        BUYOUT_COST,
        /**
         * Total sell price of building.
         */
        SELL_PRICE,
        /**
         * The amount of m2 sellable floor space (e.g. a 3 floor building has 3 x surface size of floorspace).
         */
        SELLABLE_FLOORSPACE_M2,

        /**
         * Total amount of m3 innovative water storage in this building.
         */
        WATER_STORAGE_INNOVATIVE_M3,

        /**
         * Total amount of m2 green space in this building.
         */
        GREEN_M2,
        /**
         * The amount of m3 traditional water storage in this building
         */
        WATER_STORAGE_TRADITIONAL_M3,

        /**
         * Total require HEAT flow in GJ per year
         */
        HEAT_FLOW_GJ_YEAR,

        /**
         * Number of houses inside this building.
         */
        NUMBER_OF_HOUSES,

        /**
         * AVG livability
         */
        AVG_LIVABILITY,

        /**
         * AVG heat
         */
        AVG_HEAT;

        public final static Detail[] VALUES = Detail.values();

        public OverlayType getOverlayType() {

            if (this == AVG_HEAT) {
                return Overlay.OverlayType.HEAT;
            } else if (this == AVG_LIVABILITY) {
                return Overlay.OverlayType.LIVABILITY;
            }
            return null;
        }

        public boolean isMoneyDetail() {
            return (this == CONSTRUCTION_COST || this == DEMOLISH_COST || this == BUYOUT_COST || this == SELL_PRICE);
        }
    }

    public enum GroundLayerType {
        SURFACE, UNDERGROUND;

        public final static GroundLayerType[] VALUES = GroundLayerType.values();
    }

    public enum ModelStyle {

        PLAIN,

        COLORED,

        TEXTURED,

        DISCO,

    }

    public final static double MINIMAL_BUILDING_SIZE_M2 = 0.8;

    public final static double LARGE_BUILDING_MIN_SIZE = 100 * 100;

    private static final long serialVersionUID = 1763521856771901193L;

    public final static int HEIGHT_ACCURACY = 3;

    /**
     * Simply polygons with this factor, when sketonizing, to make it easier/faster
     */
    public final static double SKELETON_SIMPLIFY_FACTOR = 100d;

    @ItemIDField("FUNCTIONS")
    @XMLValue
    private Integer functionID = Item.NONE;

    @XMLValue
    private HashMap<FunctionValue, Double> functionValues = new HashMap<>();

    @XMLValue
    private HashMap<Category, Map<CategoryValue, Double>> categoryValues = new HashMap<>();

    @XMLValue
    private Long finishBuildingDate = null;

    @XMLValue
    private Long finishDemolishDate = null;

    @XMLValue
    @ItemIDField("MEASURES")
    private Integer measureID = Item.NONE;

    @XMLValue
    @ItemIDField("UPGRADE_TYPES")
    private Integer upgradeID = Item.NONE;

    @XMLValue
    @ItemIDField("STAKEHOLDERS")
    private Integer upgradeOwnerID = Item.NONE;

    @ItemIDField("STAKEHOLDERS")
    @XMLValue
    private Integer ownerID = Item.NONE;

    @ItemIDField("STAKEHOLDERS")
    @XMLValue
    private ArrayList<Integer> renterIDs = new ArrayList<>();

    @XMLValue
    private Long startBuildingDate = null;

    @XMLValue
    private Long startDemolishDate = null;

    @XMLValue
    private TimeState state = TimeState.NOTHING;

    @XMLValue
    private int constructionYear = Item.NONE;

    @XMLValue
    private String gisInfo = StringUtils.EMPTY;

    @XMLValue
    private Integer predecessorID = Item.NONE;

    private int modelVersion = 0;

    @XMLValue
    private boolean vacant = false;

    @XMLValue
    private MultiPolygon polygons = JTSUtils.EMPTY;

    @XMLValue
    private MultiLineString skeletonLines = null;

    @XMLValue
    private MultiPolygon roofPolygons = null;

    @XMLValue
    private TColor overrideRoofColor = null;

    @XMLValue
    private TColor overrideWallColor = null;

    @XMLValue
    @ListOfClass(Address.class)
    private ArrayList<Address> addresses = new ArrayList<>();

    public Building() {
    }

    public Building(final Integer functionID, final String name) {
        this();
        this.functionID = functionID;
        this.setName(name);
    }

    public void addAddress(Address address) {
        if (!addresses.contains(address)) {
            addresses.add(address);
        }
    }

    /**
     * @param renterIDs the renterIDs to set
     */
    public final boolean addRenter(Stakeholder renter) {

        for (Integer renterID : renterIDs) {
            if (renterID.equals(renter.getID())) {
                TLogger.severe("Stakeholder " + renter + " is already renting in building " + getName());
                return false;
            }
        }
        this.renterIDs.add(renter.getID());
        return true;
    }

    public void clearMultiPolygon() {
        this.polygons = JTSUtils.EMPTY;
        this.updateSkeletons();
    }

    public final boolean containsCarBasedTraffic() {
        for (TrafficType type : this.getTrafficValues().keySet()) {
            if (type.isCarBased()) {
                return true;
            }
        }
        return false;
    }

    public boolean containsHousing() {
        for (Category cat : this.getCategories()) {
            if (cat.isHousing()) {
                return true;
            }
        }
        return false;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public final double getBuildingDetail(final MapType requestMapType, final Detail detail) {
        return getBuildingDetailForM2(this.getMultiPolygon(requestMapType).getArea(), detail);
    }

    public final double getBuildingDetailForM2(double surfaceSizeM2, Detail detail) {
        return DetailUtils.getBuildingDetailForM2(this, surfaceSizeM2, getFloors(), detail, this.isVacant());
    }

    @Override
    public Collection<Category> getCategories() {
        if (categoryValues.isEmpty()) {
            return this.getFunction().getCategories();
        } else {
            return categoryValues.keySet();
        }
    }

    @Override
    public double getCategoryPercentage(Category cat) {

        if (getCategories().size() == 1) {
            return 1d;
        }
        double sum = 0;
        for (Category someCat : getCategories()) {
            sum += getValue(someCat, CategoryValue.CATEGORY_WEIGHT);
        }
        if (sum == 0) {
            return 0;
        }
        return getValue(cat, CategoryValue.CATEGORY_WEIGHT) / sum;
    }

    public final double getCategoryUnits(Category cat, double buildingSizeM2) {
        return DetailUtils.getCategoryUnits(this, cat, buildingSizeM2, this.getFloors());
    }

    public final double getCategoryUnits(final MapType mapType, Category cat) {
        return getCategoryUnits(cat, this.getLotSizeM2(mapType));
    }

    public Point getCenterPoint() {
        return JTSUtils.getCenterPoint(polygons);
    }

    @Override
    public final Long getConstructionFinishDate() {

        Measure measure = this.getMeasure();
        if (measure != null) {
            return measure.getConstructionFinishDate();
        }
        return finishBuildingDate;
    }

    @Override
    public final Long getConstructionStartDate() {

        Measure measure = this.getMeasure();
        if (measure != null) {
            return measure.getConstructionStartDate();
        }
        return startBuildingDate;
    }

    @Override
    public double getConstructionTimeInMonths() {

        Measure measure = this.getMeasure();
        if (measure != null) {
            return measure.getConstructionTimeInMonths();
        }
        if (this.isUpgraded()) {
            return this.getUpgrade().getConstructionTimeInMonths();
        }
        return this.getValue(FunctionValue.CONSTRUCTION_TIME_IN_MONTHS);
    }

    public int getConstructionYear() {
        return constructionYear;
    }

    /**
     * @return the finishDemolishDate
     */
    @Override
    public final Long getDemolishFinishDate() {

        Measure measure = this.getMeasure();
        if (measure != null) {
            return measure.getDemolishFinishDate();
        }
        return this.finishDemolishDate;
    }

    /**
     * @return the startDemolishDate
     */
    @Override
    public final Long getDemolishStartDate() {

        Measure measure = this.getMeasure();
        if (measure != null) {
            return measure.getDemolishStartDate();
        }
        return this.startDemolishDate;
    }

    @Override
    public double getDemolishTimeInMonths() {

        Measure measure = this.getMeasure();
        if (measure != null) {
            return measure.getDemolishTimeInMonths();
        }
        return this.getValue(FunctionValue.DEMOLISH_TIME_IN_MONTHS);
    }

    @Override
    public String getDescription() {

        String info = "\n";

        info += this.getWord(MapLink.CLIENT_WORDS, ClientTerms.BUILDING_NAME, getName());
        if (getOwner() != null) {
            info += this.getWord(MapLink.CLIENT_WORDS, ClientTerms.BUILDING_OWNER, getOwner().getName());
        }

        info += this.getWord(MapLink.CLIENT_WORDS, ClientTerms.BUILDING_RENTER, getRenters());
        info += this.getWord(MapLink.CLIENT_WORDS, ClientTerms.BUILDING_STATE, getTimeState().name());
        if (getTimeState().ordinal() >= TimeState.REQUEST_CONSTRUCTION_APPROVAL.ordinal() && startBuildingDate != null
                && finishBuildingDate != null) {
            info += this.getWord(MapLink.CLIENT_WORDS, ClientTerms.BUILDING_START_DATE, StringUtils.dateToShortString(startBuildingDate));
            info += this.getWord(MapLink.CLIENT_WORDS, ClientTerms.BUILDING_FINISH_DATE, StringUtils.dateToShortString(finishBuildingDate));

            if (getTimeState() == TimeState.CONSTRUCTING) {
                info += this.getWord(MapLink.CLIENT_WORDS, ClientTerms.BUILDING_PERCENTAGE_COMPLETE, (int) (getPercentageReady() * 100));
            }
        }
        return info;
    }

    public int getFloors() {
        return (int) this.getValue(FunctionValue.FLOORS);
    }

    public Function getFunction() {
        return this.getItem(MapLink.FUNCTIONS, functionID);
    }

    public Integer getFunctionID() {
        return functionID;
    }

    public String getGisInfo() {
        return gisInfo;
    }

    /**
     * When true building is beneath the ground
     * @return
     */
    public Building.GroundLayerType getGroundLayerType() {
        for (Category cat : getCategories()) {
            return cat.getGroundLayerType();
        }
        return null;
    }

    public final double getHeightM() {
        return getHeightM(false);
    }

    public double getHeightM(boolean includeRoofAndFurniture) {

        double floorsHeight = this.getValue(FunctionValue.FLOOR_HEIGHT_M) * this.getFloors();
        if (!includeRoofAndFurniture) {
            return floorsHeight;
        }

        // check furniture
        double roofHeight = 0;
        for (ModelData model : this.getFunction().getModels()) {
            if (model.getStack() == Stack.FURNITURE && roofHeight < model.getModelHeightM()) {
                roofHeight = model.getModelHeightM();
            }
        }
        // add slanting roof
        roofHeight += this.getValue(FunctionValue.SLANTING_ROOF_HEIGHT);

        // combine and return
        return floorsHeight + roofHeight;
    }

    @Override
    public String getImageLocation() {
        return this.getFunction().getImageLocation();
    }

    /**
     * Total size in M2 of the "Perceel" area, includes gardens and surrounding land.
     * @param mapType
     * @return
     */
    public final double getLotSizeM2(final MapType mapType) {
        return getMultiPolygon(mapType).getArea();
    }

    public final int getMaxFloors() {
        return (int) this.getValue(FunctionValue.MAX_FLOORS);
    }

    /**
     * @return the measure
     */
    public final MapMeasure getMeasure() {
        return this.getItem(MapLink.MEASURES, measureID);
    }

    public final Integer getMeasureID() {
        return measureID;
    }

    public final int getMinFloors() {
        return (int) this.getValue(FunctionValue.MIN_FLOORS);
    }

    public double getModelBasementHeightM() {
        for (ModelData model : this.getFunction().getModels()) {
            if (model.getStack() == Stack.BASEMENT) {
                return model.getModelHeightM();
            }
        }
        return 0;
    }

    public int getModelVersion() {
        return modelVersion;
    }

    public final MultiPolygon getMultiPolygon(final MapType mapType) {
        return isInMap(mapType) ? polygons : JTSUtils.EMPTY;
    }

    public Set<Entry<FunctionValue, Double>> getOverrideValues() {
        return this.functionValues.entrySet();
    }

    public final Stakeholder getOwner() {
        return this.getItem(MapLink.STAKEHOLDERS, this.getOwnerID());
    }

    public final Integer getOwnerID() {

        Measure measure = this.getMeasure();
        if (measure != null && !this.isUpgraded()) {
            return measure.getOwnerID();
        }
        return ownerID;
    }

    /**
     * Percentage the building is ready 0 nothing 1 is ready
     * @return
     */
    public double getPercentageReady() {

        TimeState state = this.getTimeState();

        if (state == TimeState.READY || state == TimeState.DEMOLISH_FINISHED) {
            return 1f;

        } else if (state == TimeState.CONSTRUCTING) {
            // must be in building state
            long buildTimeInMillis = this.getConstructionFinishDate() - this.getConstructionStartDate();
            if (buildTimeInMillis == 0) {
                return 0f;
            }
            long current = this.getLord().getSimTimeMillis();
            long doneInMillis = current - this.getConstructionStartDate();
            return (double) doneInMillis / (double) buildTimeInMillis;

        } else if (state == TimeState.DEMOLISHING) {

            long demolitionTimeInMillis = this.getDemolishFinishDate() - this.getDemolishStartDate();
            if (demolitionTimeInMillis == 0) {
                return 0f;
            }
            long current = this.getLord().getSimTimeMillis();
            long doneInMillis = current - this.getDemolishStartDate();
            return (double) doneInMillis / (double) demolitionTimeInMillis;
        }
        return 0f;
    }

    public Building getPredecessor() {
        return this.getItem(MapLink.BUILDINGS, getPredecessorID());
    }

    public Integer getPredecessorID() {
        return predecessorID;
    }

    public Point getPreferredCoordinate() {

        if (this.getTimeState().after(TimeState.NOTHING)) {
            // get building center
            return JTSUtils.getCenterPoint(getMultiPolygon(MapType.MAQUETTE));
        }
        return null;
    }

    @Override
    public final MultiPolygon[] getQTMultiPolygons() {
        return new MultiPolygon[] { getMultiPolygon(null) };
    }

    /**
     * @return the renterIDs
     */
    public final List<Stakeholder> getRenters() {
        return this.getItems(MapLink.STAKEHOLDERS, renterIDs);
    }

    public TColor getRoofColor() {

        if (overrideRoofColor == null) {
            return this.getFunction().getRoofColor();
        }
        return overrideRoofColor;
    }

    public Collection<Polygon> getRoofPolygons(Polygon polygon) {

        PreparedGeometry prep = PreparedGeometryFactory.prepare(polygon);
        Collection<Polygon> result = new ArrayList<>();
        if (roofPolygons == null) {
            return result;
        }

        for (int n = 0; n < roofPolygons.getNumGeometries(); n++) {
            Polygon roofPolygon = (Polygon) roofPolygons.getGeometryN(n);
            if (JTSUtils.intersectsBorderExcluded(prep, roofPolygon)) {
                result.add(roofPolygon);
            }
        }
        return result;
    }

    public Collection<LineString> getSkeletonTopLines(Geometry geometry) {

        PreparedGeometry prep = PreparedGeometryFactory.prepare(geometry);
        Collection<LineString> result = new ArrayList<>();
        if (skeletonLines == null) {
            return result;
        }
        for (int n = 0; n < skeletonLines.getNumGeometries(); n++) {
            LineString line = (LineString) skeletonLines.getGeometryN(n);
            if (JTSUtils.covers(prep, line)) {
                result.add(line);
            }
        }
        return result;
    }

    @Override
    public final TimeState getTimeState() {
        Measure measure = this.getMeasure();
        if (measure != null) {
            return measure.getTimeState();
        }
        return this.state;
    }

    public double getTrafficFactor(TrafficType type) {
        Double value = this.getTrafficValues().get(type);
        return value != null ? value.doubleValue() : 0d;
    }

    public double getTrafficFlow() {

        double base = getValue(FunctionValue.TRAFFIC_FLOW);
        if (base == 0) {
            return 0;
        }
        Setting setting = this.getItem(MapLink.SETTINGS, Setting.Type.TRAFFIC_MULTIPLIER);
        return base * setting.getDoubleValue();
    }

    public Map<TrafficType, Double> getTrafficValues() {
        return this.getFunction().getTrafficValues();
    }

    public final String getUniqueFunctionName() {

        return "" + this.getFunction().getName() + " (" + this.getID() + ")";
    }

    public final UpgradeType getUpgrade() {
        return this.getItem(MapLink.UPGRADE_TYPES, this.getUpgradeID());
    }

    public final Integer getUpgradeID() {
        return upgradeID;
    }

    public final Integer getUpgradeOwnerID() {
        return upgradeOwnerID;
    }

    @Override
    public double getValue(Category cat, CategoryValue val) {

        // no overrides?
        if (this.categoryValues.isEmpty()) {
            return this.getFunction().getValue(cat, val);
        }
        // Override mode: check if I got it?
        Map<CategoryValue, Double> catMap = categoryValues.get(cat);
        if (catMap == null) {
            return 0;
        }
        Double value = catMap.get(val);
        if (value != null) {
            return value;
        }
        return this.getFunction().getValue(cat, val);
    }

    @Override
    public double getValue(Value key) {

        if (key instanceof FunctionValue) {
            if (this.functionValues.containsKey(key)) {
                return functionValues.get(key);
            } else {
                return getFunction().getValue(key);
            }
        } else {
            double value = 0;
            for (Category cat : this.getCategories()) {
                value += getValue(cat, (CategoryValue) key) * getCategoryPercentage(cat);
            }
            return value;
        }
    }

    public TColor getWallColor() {

        if (overrideWallColor == null) {
            return this.getFunction().getWallColor();
        }
        return overrideWallColor;
    }

    public boolean hasAddress(Address address) {
        return addresses.contains(address);
    }

    public boolean hasOverride(Category category, CategoryValue value) {
        Map<CategoryValue, Double> overrides = categoryValues.get(category);
        if (overrides == null) {
            return false;
        }
        return overrides.get(value) != null;
    }

    public boolean hasOverride(FunctionValue value) {
        return functionValues.get(value) != null;
    }

    public boolean isBothMapsActive() {
        return (getTimeState() == TimeState.READY || getTimeState() == TimeState.CONSTRUCTING);
    }

    public final boolean isBridge() {
        return this.getCategories().contains(Category.BRIDGE);
    }

    public boolean isGarden() {
        return getCategories().contains(Category.GARDEN);
    }

    public boolean isInMap(MapType mapType) {
        // XXX (Frank) Discuss with Maxim if this is ok, initialization issue for buildings with measures and the createPolygonTree method.
        if (mapType == null) {
            return true;
        }
        return this.getTimeState().isInMap(mapType);
    }

    /**
     * Get total model stack height of this building (same for current as maquette).
     * @return
     */
    public boolean isLandmark() {
        return false;
    }

    public final boolean isPartOfMeasure() {
        return !Item.NONE.equals(this.measureID);
    }

    /**
     * When true building is of category type road/intersection or bridge.
     * @return
     */
    public final boolean isRoadSystem() {
        for (Category cat : this.getCategories()) {
            if (cat == Category.ROAD || cat == Category.INTERSECTION || cat == Category.BRIDGE) {
                return true;
            }
        }
        return false;
    }

    public boolean isRoofColorOverride() {
        return this.overrideRoofColor != null;
    }

    public final boolean isSurface() {
        return !this.getCategories().contains(Category.UNDERGROUND);
    }

    public boolean isUpgraded() {
        return !Item.NONE.equals(this.getUpgradeID());
    }

    public boolean isVacant() {
        return this.vacant;
    }

    public boolean isWallColorOverride() {
        return this.overrideWallColor != null;
    }

    public boolean isZoningPermitRequired() {

        boolean permit = this.getValue(FunctionValue.ZONING_PERMIT_REQUIRED) > 0;
        if (permit && getUpgrade() != null) {
            return getUpgrade().isZoningPermitRequired();
        }
        return permit;
    }

    public boolean removeAddress(Address address) {
        return addresses.remove(address);
    }

    public void removeOverrideCategories() {
        this.categoryValues.clear();
    }

    public boolean removeOverrideValue(Category cat, CategoryValue key) {
        Map<CategoryValue, Double> map = this.categoryValues.get(cat);
        return map != null && map.remove(key) != null;
    }

    public boolean removeOverrideValue(FunctionValue key) {
        return this.functionValues.remove(key) != null;
    }

    /**
     * @param renterIDs the renterIDs to set
     */
    public final boolean removeRenter(Stakeholder renter) {
        return renterIDs.remove(renter.getID());
    }

    @Override
    public void reset() {
        super.reset();
        this.polygons.setUserData(null);
        for (Polygon polygon : JTSUtils.getPolygons(polygons)) {
            polygon.setUserData(null);
        }
    }

    public final void setConstructionFinishDate(final Long finishDate) {
        if (this.isPartOfMeasure()) {
            TLogger.severe("Building is part of measure, cannot set time state, this is handled by the measure.");
            return;
        }
        this.finishBuildingDate = finishDate;

        if (Item.NONE.equals(this.constructionYear) && getConstructionFinishDate() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(getConstructionFinishDate());
            setConstructionYear(calendar.get(Calendar.YEAR));
        }
    }

    public final void setConstructionStartDate(final Long startDate) {
        if (this.isPartOfMeasure()) {
            TLogger.severe("Building is part of measure, cannot set time state, this is handled by the measure.");
            return;
        }
        this.startBuildingDate = startDate;
    }

    public void setConstructionYear(int constructionYear) {
        this.constructionYear = constructionYear;
    }

    /**
     * @param finishDemolishDate the finishDemolishDate to set
     */
    public final void setDemolishFinishDate(Long finishDemolishDate) {
        if (this.isPartOfMeasure()) {
            TLogger.severe("Building is part of measure, cannot set time state, this is handled by the measure.");
            return;
        }
        this.finishDemolishDate = finishDemolishDate;
    }

    /**
     * @param startDemolishDate the startDemolishDate to set
     */
    public final void setDemolishStartDate(Long startDemolishDate) {
        if (this.isPartOfMeasure()) {
            TLogger.severe("Building is part of measure, cannot set time state, this is handled by the measure.");
            return;
        }
        this.startDemolishDate = startDemolishDate;
    }

    public boolean setFloors(int floors) {
        // make sure it fits
        if (!this.validAmountOfFloors(floors)) {
            return false;
        }
        this.setOverrideValue(FunctionValue.FLOORS, floors);
        return true;
    }

    public void setFunction(Function function) {
        this.setFunctionID(function.getID());
    }

    public void setFunctionID(Integer functionID) {
        this.functionID = functionID;
        this.updateSkeletons();
    }

    public void setGisInfo(String gisInfo) {
        this.gisInfo = gisInfo;
    }

    /**
     * @param measure the measure to set
     */
    public final void setMeasure(Integer measureIDother) {
        this.measureID = measureIDother;
    }

    /**
     * Depending on the timestate the coordinates are placed in CURRENT or MAQUETTE
     * @param mp
     */
    public final void setMultiPolygon(final MultiPolygon mp) {
        setMultiPolygon(mp, false);
    }

    /**
     * ONLY call this when you know what you are doing, allows to force override coordinates
     * @param mp
     * @param force
     */
    public final void setMultiPolygon(final MultiPolygon mp, boolean force) {
        setMultiPolygon(mp, force, true);
    }

    /**
     * ONLY call this when you know what you are doing, allows to force override coordinates
     * @param mp
     * @param force
     */
    public final void setMultiPolygon(final MultiPolygon mp, boolean force, boolean updateSkeletons) {

        TimeState state = this.getTimeState();

        if (force || state.before(TimeState.CONSTRUCTING) || state == TimeState.READY || state == TimeState.WAITING_FOR_DEMOLISH_DATE) {
            this.polygons = JTSUtils.createMP(mp);
            if (updateSkeletons) {
                this.updateSkeletons();
            }
        } else {
            /**
             * Why would you want this?
             */
            TLogger.severe("Cannot set coordinates for building " + this.getName() + ", since timestate is " + state);
        }
    }

    public void setOverrideRoofColor(TColor roofColor) {
        this.overrideRoofColor = roofColor;
    }

    public void setOverrideValue(Category cat, CategoryValue key, double value) {

        boolean hasParent = this.getFunction().getCategories().contains(cat);
        if (hasParent && Math.abs(this.getFunction().getValue(cat, key) - value) < Function.VALUE_ACCURACY) {
            this.removeOverrideValue(cat, key);
            return;
        }
        // maybe add new category
        if (!categoryValues.containsKey(cat)) {
            categoryValues.put(cat, new HashMap<>());
        }
        categoryValues.get(cat).put(key, value);
    }

    public void setOverrideValue(FunctionValue key, double value) {

        if (Math.abs(this.getFunction().getValue(key) - value) < Function.VALUE_ACCURACY) {
            this.removeOverrideValue(key);
        } else {
            this.functionValues.put(key, value);
        }

        // update skeletons
        if (key == FunctionValue.SLANTING_ROOF_HEIGHT) {
            this.updateSkeletons();
        }
    }

    public void setOverrideWallColor(TColor wallColor) {
        this.overrideWallColor = wallColor;
    }

    /**
     * @param ownerID the stakeholder to set
     */
    public final void setOwner(Stakeholder stakeholder) {
        setOwnerID(stakeholder.getID());
    }

    public final void setOwnerID(Integer stakeholderID) {

        if (this.isPartOfMeasure()) {
            TLogger.severe("Building is part of measure, cannot set owner, this is handled by the measure.");
            return;
        }
        ownerID = stakeholderID;
    }

    public void setPredecessorID(Integer predecessorID) {
        this.predecessorID = predecessorID;
    }

    public final void setTimeState(TimeState state) {

        if (this.isPartOfMeasure()) {
            TLogger.severe("Building is part of measure, cannot set time state, this is handled by the measure.");
            return;
        }
        this.state = state;
    }

    public void setUpgrade(Integer upgradeOwnerID, Integer upgradeID) {
        this.upgradeOwnerID = upgradeOwnerID;
        this.upgradeID = upgradeID;
    }

    public void setVacant(Boolean vacant) {
        this.vacant = vacant;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    protected void updateInternalVersion(int version) {
        this.modelVersion = version;
    }

    public void updateSkeletons() {

        this.skeletonLines = null;
        this.roofPolygons = null;

        if (!JTSUtils.containsData(polygons)) {
            return;
        }

        if (this.isRoadSystem() || !this.getFunction().getTrafficValues().isEmpty()) {
            // margin of 2 meters is enough for roads
            this.skeletonLines = JTSUtils.getSkeletonTopLines(polygons, 2d);

        } else if (this.getValue(FunctionValue.SLANTING_ROOF_HEIGHT) > 0) {
            List<Polygon> allPoly = new ArrayList<>();
            List<LineString> allLines = new ArrayList<>();
            for (Polygon polygon : JTSUtils.getPolygons(polygons)) {
                if (polygon.getNumInteriorRing() > 0) {
                    continue;
                }
                MultiPolygon mp = JTSUtils.simplify(polygon, polygon.getNumPoints() / SKELETON_SIMPLIFY_FACTOR);
                for (Polygon p : JTSUtils.getPolygons(mp)) {
                    Skeleton skeleton = JTSUtils.getSkeleton((LinearRing) p.getExteriorRing());
                    allPoly.addAll(JTSUtils.getSkeletonPolygons(p, skeleton));
                    allLines.addAll(JTSUtils.getSkeletonTopLines(skeleton, false));
                }
            }

            this.skeletonLines = new MultiLineString(allLines.toArray(new LineString[allLines.size()]), JTSUtils.sourceFactory);
            this.roofPolygons = new MultiPolygon(allPoly.toArray(new Polygon[allPoly.size()]), JTSUtils.sourceFactory);
        }
    }

    /**
     * Checks if given amount of floors is allowed for this function.
     * @param floors
     * @return
     */
    public final boolean validAmountOfFloors(int floors) {

        if (floors < this.getValue(FunctionValue.MIN_FLOORS) || floors > this.getValue(FunctionValue.MAX_FLOORS)) {
            TLogger.severe(floors + " is an invalid amount for function " + this.getName() + ", it needs a value in range: "
                    + this.getValue(FunctionValue.MIN_FLOORS) + "-" + this.getValue(FunctionValue.MAX_FLOORS));
            return false;
        }
        return true;
    }

    @Override
    public String validated(boolean startNewSession) {

        if (this.getFunction() == null) {
            return "\nBuilding " + this + " has no Function!";
        }

        if (polygons != null) {
            polygons = JTSUtils.createMP(polygons);
        }
        if ((this.isRoadSystem() || this.getValue(FunctionValue.SLANTING_ROOF_HEIGHT) > 0) && skeletonLines == null) {
            this.updateSkeletons();
        }

        return super.validated(startNewSession);
    }
}
