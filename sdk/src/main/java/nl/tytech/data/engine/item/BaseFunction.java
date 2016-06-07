/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import nl.tytech.core.item.annotations.AssetDirectory;
import nl.tytech.core.item.annotations.Html;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.item.UnitData.TrafficType;
import nl.tytech.data.engine.serializable.Category;
import nl.tytech.data.engine.serializable.CategoryValue;
import nl.tytech.data.engine.serializable.ConstructionPeriod;
import nl.tytech.data.engine.serializable.FunctionValue;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;

/**
 * BaseFunction
 * <p>
 * Generic base function can be duplicated by a DuplicateFunction
 * </p>
 * @author Maxim Knepfle
 */
public class BaseFunction extends Function {

    private static final long serialVersionUID = 295091661000494566L;

    @XMLValue
    private TColor color = null;

    @AssetDirectory(GUI_IMAGES_ACTIONS)
    @XMLValue
    private String imageName = DEFAULT_IMAGE;

    // @XMLValue
    // @ListOfClass(TrafficType.class)
    // public ArrayList<TrafficType> trafficTypes = new ArrayList<>();

    @XMLValue
    public HashMap<TrafficType, Double> trafficValues = new HashMap<>();

    @XMLValue
    @ListOfClass(Region.class)
    private ArrayList<Region> regions = new ArrayList<>();

    @XMLValue
    private HashMap<FunctionValue, Double> functionValues = new HashMap<>();

    @Html
    @XMLValue
    private String description = StringUtils.EMPTY;

    @XMLValue
    private int dimension = Item.NONE;

    @XMLValue
    private String name = "0_new function";

    @XMLValue
    private String grTexture = "";

    @XMLValue
    private String exTexture = "";

    @XMLValue
    private String tlTexture = "";

    @XMLValue
    private String roofTexture = "";

    @XMLValue
    private TColor roofColor = null;

    @XMLValue
    private TColor wallColor = TColor.DARK_GRAY;

    @XMLValue
    @ItemIDField("MODEL_SETS")
    private Integer modelSetID = Item.NONE;

    @XMLValue
    @ListOfClass(Stakeholder.Type.class)
    private ArrayList<Stakeholder.Type> defaults = new ArrayList<Stakeholder.Type>();

    @XMLValue
    private int distanceRoad = Item.NONE;

    @XMLValue
    private boolean deprecated = false;

    @XMLValue
    private PlacementType placementType = PlacementType.LAND;

    @XMLValue
    @ListOfClass(ConstructionPeriod.class)
    private ArrayList<ConstructionPeriod> constructionPeriods = new ArrayList<ConstructionPeriod>();

    @XMLValue
    private HashMap<Category, Map<CategoryValue, Double>> categoryValues = new HashMap<>();

    /**
     * Color of this type on the map.
     *
     * @return
     */
    @Override
    public final TColor getColor() {

        if (this.color == null) {
            for (Category cat : getCategories()) {
                return cat.getColor();
            }
        }
        return this.color != null ? this.color : TColor.BLACK;
    }

    @Override
    public List<ConstructionPeriod> getConstructionPeriods() {
        return constructionPeriods;
    }

    @Override
    public String getDescription() {
        String result = StringUtils.EMPTY;
        /**
         * Try game override first
         */
        FunctionOverride functionOverride = this.getItem(MapLink.FUNCTION_OVERRIDES, this.getID());
        if (functionOverride != null && StringUtils.containsData(functionOverride.getDescription())) {
            result = functionOverride.getDescription();
        } else {
            result = description;
        }

        if (result == null || result.equals(StringUtils.EMPTY)) {
            return "<p>" + getName() + "</p>";
        } else {
            return result;
        }
    }

    /**
     * Fixed sized dimensions of buildings like 1x1 or 3x3. The building is always this size and must have models that exactly match the
     * size.
     *
     * @return
     */
    @Override
    public final int getDimension() {
        return dimension;
    }

    /**
     * The number of Blocks (distance) the function can be located from the road.
     *
     * @return
     */
    @Override
    public int getDistanceRoad() {
        return distanceRoad;
    }

    @Override
    public String getExtraTexture() {
        return this.exTexture;
    }

    @Override
    public String getGroundTexture() {
        return this.grTexture;
    }

    @Override
    public String getImageLocation() {
        /**
         * Try game override first
         */
        FunctionOverride functionOverride = this.getItem(MapLink.FUNCTION_OVERRIDES, this.getID());
        if (functionOverride != null && StringUtils.containsData(functionOverride.getImageName())) {
            return functionOverride.getImageLocation();
        } else if (StringUtils.containsData(imageName)) {
            return GUI_IMAGES_ACTIONS + imageName;
        } else {
            return StringUtils.EMPTY;
        }
    }

    @Override
    public String getImageName() {
        /**
         * Try game override first
         */
        FunctionOverride functionOverride = this.getItem(MapLink.FUNCTION_OVERRIDES, this.getID());
        if (functionOverride != null && StringUtils.containsData(functionOverride.getImageName())) {
            return functionOverride.getImageName();
        } else if (StringUtils.containsData(imageName)) {
            return imageName;
        } else {
            return StringUtils.EMPTY;
        }
    }

    @Override
    public MapLink getMapLink() {
        return MapLink.FUNCTIONS;
    }

    @Override
    public ModelSet getModelSet() {
        return this.getItem(MapLink.MODEL_SETS, this.getModelSetID());
    }

    public Integer getModelSetID() {
        return this.modelSetID;
    }

    /**
     * The name of the function
     *
     * @return
     */
    @Override
    public final String getName() {

        String result = name;

        /**
         * Try game override first
         */
        if (this.getLord() != null) {
            FunctionOverride functionOverride = this.getItem(MapLink.FUNCTION_OVERRIDES, this.getID());
            if (functionOverride != null && StringUtils.containsData(functionOverride.getName())) {
                result = functionOverride.getName();
            }
        }

        for (Category cat : this.getCategories()) {
            if (cat.isRoad() || cat == Category.NATURE || cat == Category.PARK || cat == Category.OTHER) {
                return result;
            }
        }

        String floors = StringUtils.EMPTY;

        if (result.contains(StringUtils.LANG_SPLIT)) {

            if (this.getMinFloorsFunction() == this.getMaxFloorsFunction()) {
                floors = " (" + this.getMinFloorsFunction() + "h)";
            } else {
                floors = " (" + this.getMinFloorsFunction() + "-" + this.getMaxFloorsFunction() + "h)";
            }

            floors += this.isDeprecated() ? " (DEPRECATED) " : "";
            result = result.replaceFirst(StringUtils.LANG_SPLIT, floors + StringUtils.LANG_SPLIT);
        } else {
            result += floors;
        }
        return result;
    }

    @Override
    public Double getOrginalCategoryValue(Category cat, CategoryValue key) {

        if (!categoryValues.containsKey(cat)) {
            return cat.getCategoryValue(key);
        }

        /**
         * Try generic override next
         */
        Double functionvalue = this.categoryValues.get(cat).get(key);
        if (functionvalue != null) {
            return functionvalue;
        }
        return cat.getCategoryValue(key);
    }

    /**
     * Orginal function value (not overriden by game override.
     * @param key
     * @return
     */
    @Override
    public Double getOrginalFunctionValue(FunctionValue key) {

        /**
         * Try generic override next
         */
        Double functionvalue = this.functionValues.get(key);
        if (functionvalue != null) {
            return functionvalue;
        }

        /**
         * Fallback to first category (always has a value).
         */
        for (Category cat : this.getCategories()) {
            return cat.getFunctionValue(key);
        }
        return null;
    }

    public String getOrginalName() {
        return this.name;
    }

    @Override
    public final Set<Category> getOriginalCategories() {
        return this.categoryValues.keySet();
    }

    @Override
    public PlacementType getPlacementType() {
        return placementType;
    }

    @Override
    public List<Region> getRegions() {
        return regions;
    }

    @Override
    public TColor getRoofColor() {
        /**
         * Try game override first
         */
        FunctionOverride functionOverride = this.getItem(MapLink.FUNCTION_OVERRIDES, this.getID());
        if (functionOverride != null && functionOverride.getRoofColor() != null) {
            return functionOverride.getRoofColor();
        }
        if (roofColor == null) {
            for (Category cat : getCategories()) {
                return cat.getRoofColor();
            }
            return TColor.BLACK;
        }
        return roofColor;
    }

    @Override
    public double getRoofInset() {
        return this.getModelSet().getRoofInset();
    }

    @Override
    public String getRoofTexture() {
        return roofTexture;
    }

    @Override
    public String getTopTexture() {

        return this.tlTexture;
    }

    @Override
    public Map<TrafficType, Double> getTrafficValues() {
        return this.trafficValues;
    }

    @Override
    public TColor getWallColor() {
        /**
         * Try game override first
         */
        FunctionOverride functionOverride = this.getItem(MapLink.FUNCTION_OVERRIDES, this.getID());
        if (functionOverride != null && functionOverride.getWallColor() != null) {
            return functionOverride.getWallColor();
        }
        return this.wallColor;
    }

    @Override
    public boolean isDefaultFunction(Stakeholder stakeholder) {

        if (defaults.contains(stakeholder.getType())) {
            Setting regionSetting = this.getItem(MapLink.SETTINGS, Setting.Type.REGION);
            Region region = regionSetting.getEnumValue(Region.class);
            return this.isInRegion(region);
        }
        return false;
    }

    /**
     * When true this is an old style model, not to be used anymore!
     * @return
     */
    @Override
    public boolean isDeprecated() {
        return deprecated;
    }

    /**
     * When true this function is either region or part of given region.
     * @param region
     * @return
     */
    @Override
    public boolean isInRegion(Region region) {
        return regions.size() == 0 || region == null || regions.contains(region);
    }

    /**
     * Store function values, internal use only.
     */
    @Override
    protected void putOrginalFunctionValue(FunctionValue key, Double value) {
        this.functionValues.put(key, value);
    }
}
