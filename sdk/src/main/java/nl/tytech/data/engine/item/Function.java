/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.item.ModelData.Placement;
import nl.tytech.data.engine.item.ModelData.Stack;
import nl.tytech.data.engine.item.UnitData.TrafficType;
import nl.tytech.data.engine.other.Action;
import nl.tytech.data.engine.other.ValueItem;
import nl.tytech.data.engine.serializable.Category;
import nl.tytech.data.engine.serializable.CategoryValue;
import nl.tytech.data.engine.serializable.ConstructionPeriod;
import nl.tytech.data.engine.serializable.FunctionValue;
import nl.tytech.locale.TCurrency;
import nl.tytech.locale.unit.UnitSystemType;
import nl.tytech.locale.unit.UnitType;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;
import nl.tytech.util.logger.TLogger;

/**
 * Function
 * <p>
 * This class keeps track of the building types.
 * </p>
 * @author Maxim Knepfle
 */
public abstract class Function extends Item implements Action, ValueItem {

    public enum FunctionValueGroup {

        CONSTRUCTION,

        FINANCIAL,

        PIPES,

        ENVIRONMENT,

        TRAFFIC,

        ASSETS,

        INDICATORS,

    }

    public enum PlacementType {
        WATER, LAND, HYBRID
    }

    public enum Region {
        NORTH_AMERICA(),

        NORTHWESTERN_EUROPE(),

        ASIA(),

        AFRICA(),

        /**
         * Rest of the world.
         */
        OTHER();

        public static final Region[] VALUES = values();

        private Region() {

        }

        public TColor getRoadLineColor() {
            if (this == NORTH_AMERICA) {
                return TColor.YELLOW;
            }
            return TColor.WHITE;
        }
    }

    public interface Value {

        public ClientWord.ClientTerms getDescriptionAsROTerms();

        public String getEditorName();

        public FunctionValueGroup getGroup();

        public double getMaxValue();

        public double getMinValue();

        public double getServerUnitValue(double value, UnitSystemType unitSystem);

        public String getUnit(TCurrency currency, UnitSystemType unitSystem);

        public UnitType[] getUnitDimensions();

        public double getUnitValue(double value, int decimals, UnitSystemType unitSystem);

        public double getUnitValue(double value, UnitSystemType unitSystem);

        public String getUnitValueFormatted(double value, int decimals, UnitSystemType unitSystem);

        public String getUnitValueFormatted(double value, UnitSystemType unitSystem);

        public String name();

    }

    /**
     * Function values are stored with 6 digit accuracy
     */
    public final static double VALUE_ACCURACY = 1e-6d;

    public static final double MIN_FLOOR_HEIGHT_M = 2;
    public final static int MIN_ALLOWED_FLOORS = 1;
    public final static int MAX_ALLOWED_FLOORS = 250;

    /**
     * When a building is vacant add this impact to QOL score!
     */
    public final static double VACANT_ENVIRONMENT_EFFECT_IMPACT = -2;

    private final static long serialVersionUID = 295091661000494567L;

    public final static double STANDARD_REQUIRED_DISTANCE_TO_BOEZEM_WATER_M = 20;
    private final static double STANDARD_REQUIRED_DISTANCE_TO_POLDER_WATER_M = 10;
    public final static double DEFAULT_DEMOLISH_TIME_IN_MONTHS = 1;

    public boolean containsHousing() {
        for (Category cat : this.getCategories()) {
            if (cat.isHousing()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return the range
     */
    @Override
    public Set<Category> getCategories() {

        /**
         * Try game override first
         */
        if (this.getLord() != null) {
            FunctionOverride functionOverride = this.getItem(MapLink.FUNCTION_OVERRIDES, this.getID());
            if (functionOverride != null && functionOverride.getCategories().size() > 0) {
                return functionOverride.getCategories();
            }
        }
        return getOriginalCategories();

    }

    @Override
    public double getCategoryPercentage(Category cat) {
        if (getCategories().size() == 1) {
            return 1;
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

    /**
     * Color of this type on the map.
     *
     * @return
     */
    public abstract TColor getColor();

    public abstract List<ConstructionPeriod> getConstructionPeriods();

    @Override
    public final double getConstructionTimeInMonths() {
        return this.getValue(FunctionValue.CONSTRUCTION_TIME_IN_MONTHS);
    }

    public final int getDefaultFloors() {
        return (int) this.getValue(FunctionValue.FLOORS);
    }

    @Override
    public abstract String getDescription();

    /**
     * Fixed sized dimensions of buildings like 10x10 or 30x30. The building is always this size and must have models that exactly match the
     * size.
     *
     * @return
     */
    public abstract int getDimension();

    /**
     * The number of Blocks (distance) the function can be located from the road.
     *
     * @return
     */
    public abstract int getDistanceRoad();

    public final double getDistanceToBehavior(BehaviorTerrain.Behavior b) {

        double result = 0;
        switch (b) {
            case BOEZEM_WATER:
                if (this.getCategories().contains(Category.UNDERGROUND)) {
                    result = STANDARD_REQUIRED_DISTANCE_TO_BOEZEM_WATER_M;
                }
                break;
            case POLDER_WATER:
                result = STANDARD_REQUIRED_DISTANCE_TO_POLDER_WATER_M;
                break;
            default:
                result = 0;
        }
        return result;
    }

    public abstract String getExtraTexture();

    /**
     * Get the valid allowed floor amount for this function or return -1 when not allowed!
     * @param heightM
     * @param flatroofOnly
     * @return
     */
    public int getFloorsForHeight(double heightM, boolean flatroofOnly) {

        if (heightM == 0) {
            return -1;
        }

        double slantingRoofHeight = flatroofOnly ? 0 : this.getValue(FunctionValue.SLANTING_ROOF_HEIGHT) / 2f;// take average
        double defaultFloorHeight = this.getValue(FunctionValue.FLOOR_HEIGHT_M);

        if (defaultFloorHeight + slantingRoofHeight >= heightM) {
            return 1;
        }
        if (2 * defaultFloorHeight + slantingRoofHeight >= heightM) {
            return 2;
        }
        // minus ground
        heightM -= defaultFloorHeight;
        // minus top level
        heightM -= defaultFloorHeight;
        heightM -= slantingRoofHeight;
        return 2 + (int) Math.round(heightM / defaultFloorHeight);
    }

    public final Building.GroundLayerType getGroundLayerType() {
        for (Category cat : this.getCategories()) {
            return cat.getGroundLayerType();
        }
        return null;
    }

    public abstract String getGroundTexture();

    @Override
    public abstract String getImageLocation();

    public abstract String getImageName();

    public final int getMaxFloorsFunction() {
        return (int) this.getValue(FunctionValue.MAX_FLOORS);
    }

    public final int getMinFloorsFunction() {
        return (int) this.getValue(FunctionValue.MIN_FLOORS);
    }

    public final List<ModelData> getModels() {
        return this.getItems(MapLink.MODEL_DATAS, this.getModelSet().getModelIDs());
    }

    public abstract ModelSet getModelSet();

    /**
     * The name of the function
     *
     * @return
     */
    @Override
    public abstract String getName();

    public abstract Double getOrginalCategoryValue(Category cat, CategoryValue key);

    /**
     * Orginal function value (not overriden by game override.
     * @param key
     * @return
     */
    public abstract Double getOrginalFunctionValue(FunctionValue key);

    public abstract Set<Category> getOriginalCategories();

    public abstract PlacementType getPlacementType();

    public abstract List<Region> getRegions();

    public abstract TColor getRoofColor();

    public abstract double getRoofInset();

    public abstract String getRoofTexture();

    public abstract String getTopTexture();

    public abstract Map<TrafficType, Double> getTrafficValues();

    @Override
    public double getValue(Category cat, CategoryValue key) {

        /**
         * Try game override first
         */
        if (this.getLord() != null) {
            FunctionOverride functionOverride = this.getItem(MapLink.FUNCTION_OVERRIDES, this.getID());
            if (functionOverride != null) {
                Double functionvalue = functionOverride.getCategoryValue(cat, key);
                if (functionvalue != null) {
                    return functionvalue;
                }
            }
        }
        return getOrginalCategoryValue(cat, key);
    }

    @Override
    public double getValue(Value key) {

        if (key instanceof FunctionValue) {

            /**
             * Try game override first
             */
            if (this.getLord() != null) {
                FunctionOverride functionOverride = this.getItem(MapLink.FUNCTION_OVERRIDES, this.getID());
                if (functionOverride != null) {
                    Double functionvalue = functionOverride.getFunctionValue((FunctionValue) key);
                    if (functionvalue != null) {
                        return functionvalue;
                    }
                }
            }
            return getOrginalFunctionValue((FunctionValue) key);

        } else {
            // CategoryValue
            double value = 0;
            for (Category cat : this.getCategories()) {
                value += getValue(cat, (CategoryValue) key) * getCategoryPercentage(cat);
            }
            return value;
        }
    }

    public abstract TColor getWallColor();

    /**
     * Only functions with a roof texture here
     *
     */
    public final boolean hasRoof() {
        return StringUtils.containsData(this.getRoofTexture());
    }

    public final boolean isBridgeFunction() {
        return this.getCategories().contains(Category.BRIDGE);
    }

    /**
     * @return the buildable
     */
    @Override
    public final boolean isBuildable() {
        return true;
    }

    public abstract boolean isDefaultFunction(Stakeholder stakeholder);

    /**
     * When true this is an old style model, not to be used anymore!
     * @return
     */
    public abstract boolean isDeprecated();

    @Override
    public final boolean isFixedLocation() {
        // buildings are selectable on the map
        return false;
    }

    public boolean isGISDetectable() {
        return !this.isDeprecated() && this.getConstructionPeriods().size() > 0;
    }

    /**
     * When true this function is either region or part of given region.
     * @param region
     * @return
     */
    public abstract boolean isInRegion(Region region);

    public boolean isIntersectionFunction() {
        return this.getCategories().contains(Category.INTERSECTION);
    }

    public final boolean isLandmark() {
        for (ModelData model : this.getModels()) {
            if (model.getPlacement() == Placement.LANDMARK) {
                return true;
            }
        }
        return false;
    }

    public boolean isRoadSystemFunction() {
        for (Category cat : this.getCategories()) {
            if (cat == Category.ROAD || cat == Category.INTERSECTION || cat == Category.BRIDGE) {
                return true;
            }
        }
        return false;
    }

    /**
     * When true the actual value is not the orginal/generic one
     * @param key
     * @return
     */
    public boolean isValueOverridden(FunctionValue key) {
        double orginal = getOrginalFunctionValue(key);
        double actual = getValue(key);
        return orginal != actual;
    }

    public final boolean isZoningPermitRequired() {
        return this.getValue(FunctionValue.ZONING_PERMIT_REQUIRED) > 0;
    }

    protected abstract void putOrginalFunctionValue(FunctionValue key, Double value);

    @Override
    public final String toString() {

        // must be in tools mode!
        if (this.getName().contains(StringUtils.LANG_SPLIT)) {
            return (this.getMinFloorsFunction() != this.getMaxFloorsFunction() ? " " : "") + " " + this.getName();
        }
        return this.getName();
    }

    @Override
    public String validated(boolean startNewGame) {

        String result = StringUtils.EMPTY;

        if (this.getCategories().size() == 0) {
            result += "\nFunction (" + getID() + ") " + this.getName() + " has no Categories!";
            return result;
        }

        for (Category cat : this.getCategories()) {
            if (this.getValue(cat, CategoryValue.UNIT_SIZE_M2) < 1) {
                result += "\nFunction (" + getID() + ") " + this.getName()
                        + " has an invalid unit size, unit sizes should be larger then 1 m2!";
            }
            if (cat.isSingle() && this.getCategories().size() > 1) {
                result += "\nFunction (" + getID() + ") " + this.getName() + " has multiple categories but: " + cat.toString()
                        + " should always be alone!";
            }
        }

        if (!this.getTrafficValues().isEmpty()) {
            double total = 0;
            for (double percentage : this.getTrafficValues().values()) {
                total += percentage;
            }
            if (total != 1d) {
                result += "\nFunction (" + getID() + ") " + this.getName() + " should have a total traffic percenatge of 100, not:"
                        + total * 100d + "!";
            }
        }

        /**
         * Validate amount of floors
         *
         */
        if (this.getMinFloorsFunction() < 1) {
            result += "\nFunction " + this.getName() + " needs at least a value of 1 as the minimum amount of floors.";
        } else if (this.getMinFloorsFunction() > this.getMaxFloorsFunction()) {
            result += "\nFunction (" + getID() + ") " + this.getName() + " has minimal amount of floors (" + this.getMinFloorsFunction()
                    + ") should be lower then its maximum: " + this.getMaxFloorsFunction();
        }

        if (this.getMinFloorsFunction() > this.getDefaultFloors() || this.getMaxFloorsFunction() < this.getDefaultFloors()) {
            result += "\nFunction (" + getID() + ") " + this.getName() + " needs to have a default value between: "
                    + this.getMinFloorsFunction() + " - " + this.getMaxFloorsFunction();
        }

        for (FunctionValue type : FunctionValue.ACTIVE_VALUES) {
            double value = this.getValue(type);
            if (value < type.getMinValue()) {
                result += "\n(" + getID() + ") " + type.name() + ": " + value + " is too low for " + this.getName();
            } else if (value > type.getMaxValue()) {
                result += "\n(" + getID() + ") " + type.name() + ": " + value + " is too high for " + this.getName();
            }
        }
        if (this.isLandmark()) {
            int landmarkCounter = 0;
            for (ModelData model : this.getModels()) {
                if (model.getPlacement() != Placement.LANDMARK) {
                    result += "\nCannot have a non-landmark model " + model.getName() + " in landmark only function: (" + getID() + ") "
                            + this.getName();
                } else if (model.getPlacement() == Placement.LANDMARK) {
                    landmarkCounter++;
                }
            }
            if (this.getConstructionPeriods().size() > 0) {
                result += "\nFunction (" + getID() + ") " + this.getName()
                        + " is a specific landmark and can thus not have a GIS construction period linkage!";
            }
            if (landmarkCounter > 1) {
                result += "\nFunction (" + getID() + ") " + this.getName() + " can have only one landmark model!";
            }
        } else if (!this.isDeprecated() && this.getDimension() < 0) {

            List<Placement> required = Placement.EMPTY;
            // int modelDimension = 1;

            List<ModelData> models = this.getModels();

            /**
             * Check each placement for enough stackables.
             */
            List<Stack> requiredStacks = new ArrayList<>();

            Map<Placement, List<Stack>> map = new HashMap<>();
            for (ModelData model : models) {

                Stack stack = model.getStack();
                Placement placement = model.getPlacement();

                if (!map.containsKey(model.getPlacement())) {
                    map.put(model.getPlacement(), new ArrayList<Stack>());
                }

                if (!map.get(model.getPlacement()).contains(stack)) {
                    map.get(model.getPlacement()).add(stack);
                }
                if (!requiredStacks.contains(model.getStack()) && stack != Stack.COMPLETE && stack != Stack.FURNITURE
                        && placement != Placement.LANDMARK && placement != Placement.SQUARE) {
                    requiredStacks.add(model.getStack());
                }
            }

            // not required for roads, use fillers
            if (this.isRoadSystemFunction()) {
                map.remove(Placement.EDGE);
            }

            if (map.size() < required.size()) {
                TLogger.warning("Function :" + this.getName() + " misses placements.");
            }

            for (Entry<Placement, List<Stack>> entry : map.entrySet()) {
                Placement placement = entry.getKey();
                List<Stack> stacks = entry.getValue();

                if (placement != Placement.LANDMARK && placement != Placement.SQUARE) {

                    if (stacks.contains(Stack.COMPLETE) && stacks.size() == 1) {
                        continue;
                    }

                    for (Stack stack : requiredStacks) {
                        if (!stacks.contains(stack)) {
                            TLogger.severe("Function :" + this.getName() + " misses placements.");
                            result += "\nMissing " + stack + " stack for " + this.getName() + " placement: " + placement;
                        }
                    }
                }
            }
        } else if (!this.isDeprecated() && this.getDimension() > 0) {
            List<ModelData> models = this.getModels();
            for (ModelData model : models) {
                if (model.getStack() != Stack.FURNITURE || model.getPlacement() != Placement.SQUARE) {
                    TLogger.severe("Function :" + this.getName() + " has a fixed dimension an can only have Furniture Squares.");
                    result += "\nFunction :" + this.getName() + " has a fixed dimension an can only have Furniture Squares.";
                }
                if (model.getDimension() != this.getDimension()) {
                    TLogger.severe("Function :" + this.getName() + " has a fixed dimension " + this.getDimension()
                            + " the model has dimension " + model.getDimension());
                    result += "\nFunction :" + this.getName() + " has a fixed dimension " + this.getDimension()
                            + " the model has dimension " + model.getDimension();
                }
            }
        }
        return result;
    }
}
