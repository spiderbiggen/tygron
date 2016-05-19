/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import nl.tytech.core.item.annotations.Html;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.net.serializable.PolygonItem;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.item.UniqueNamedItem;
import nl.tytech.data.engine.serializable.Category;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.ObjectUtils;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * ROTerrain
 * <p>
 * Maintains the functions allocated to the zone.
 * </p>
 * @author Maxim Knepfle
 */
public class Zone extends UniqueNamedItem implements PolygonItem {

    private static final long serialVersionUID = 6586583895720453036L;

    public static final int DEFAULT_MAX_FLOORS = 100; 

    @XMLValue
    private TColor color = TColor.WHITE;

    @Html
    @XMLValue
    private String description = StringUtils.EMPTY;

    @XMLValue
    @ListOfClass(Category.class)
    private ArrayList<Category> allowedFunctions = new ArrayList<>();

    @XMLValue
    private int allowedFloors = DEFAULT_MAX_FLOORS;

    @XMLValue
    private boolean showLabel = true;

    @XMLValue
    private Integer sortIndex = Item.NONE;

    @XMLValue
    private HashMap<Integer, Boolean> playable = new HashMap<>();

    @XMLValue
    private MultiPolygon polygons = JTSUtils.EMPTY;

    public Zone() {
    }

    public boolean addAllowedCategory(Category category) {

        if (this.allowedFunctions.contains(category)) {
            return false;
        }
        this.allowedFunctions.add(category);
        return true;
    }

    public List<Category> getAllowedCategories() {
        return this.allowedFunctions;
    }

    public Point getCenterPoint() {
        return JTSUtils.getCenterPoint(this.polygons);
    }

    public final TColor getColor() {
        return this.color;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public int getMaxAllowedFloors() {
        return this.allowedFloors;
    }

    public MultiPolygon getMultiPolygon() {
        return this.polygons;
    }

    @Override
    public final MultiPolygon[] getQTMultiPolygons() {
        return new MultiPolygon[] { getMultiPolygon() };
    }

    public int getSortIndex() {
        return this.sortIndex;
    }

    public boolean isCategoryConformZoningPlan(Building building) {
        if (!building.isZoningPermitRequired()) {
            return true;
        }
        return allowedFunctions.containsAll(building.getCategories());
    }

    /**
     * When true the build is allowed by the zoning plane
     * @param building
     * @return
     */
    public boolean isConformZoningPlan(Building building) {
        if (!building.isZoningPermitRequired()) {
            return true;
        }
        return isFloorsConformZoningPlan(building) && allowedFunctions.containsAll(building.getCategories());
    }

    public boolean isFloorsConformZoningPlan(Building building) {
        return building.getFloors() <= this.allowedFloors;
    }

    public boolean isPlayable() {
        Setting setting = this.getItem(MapLink.SETTINGS, Setting.Type.ACTIVE_LEVEL);
        return this.isPlayable(setting.getIntegerValue());
    }

    public boolean isPlayable(Integer levelID) {

        // when not set, default to true!
        if (playable.size() == 0) {
            return true;
        }

        // fallback to previous level value when not available.
        if (!playable.containsKey(levelID)) {
            Collection<Level> previousLevels = getPreviousSortedItems(MapLink.LEVELS, levelID);
            for (Level previousLevel : previousLevels) {
                Boolean previousTargets = playable.get(previousLevel.getID());
                if (previousTargets != null) {
                    playable.put(levelID, ObjectUtils.deepCopy(previousTargets));
                    break;
                }
            }
        }
        return this.playable.containsKey(levelID) && playable.get(levelID);
    }

    /**
     * @return Whether to show a label for this zone on the flipmap
     */
    public boolean isShowLabel() {
        return showLabel;
    }

    /**
     * Check if the category contains functions that require a permit to be build.
     * @param category
     * @return
     */
    public boolean isZoningPermitRequired(Category category) {

        for (Function function : this.<Function> getMap(MapLink.FUNCTIONS).values()) {
            if (function.getCategories().contains(category) && function.isZoningPermitRequired()) {
                return true;
            }
        }
        return false;
    }

    public boolean removeCategory(Category category) {
        return this.allowedFunctions.remove(category);
    }

    @Override
    public void reset() {
        super.reset();
        this.polygons.setUserData(null);
        for (Polygon polygon : JTSUtils.getPolygons(polygons)) {
            polygon.setUserData(null);
        }
    }

    public void setAllowedCategories(List<Category> categories) {
        allowedFunctions.clear();
        for (Category category : categories) {
            addAllowedCategory(category);
        }
    }
    
    /**
     * @return list of functions id's allowed for this zone.
     */
    public ArrayList<Function> getfunctions() {
    	ArrayList<Function> functions = new ArrayList<>();
    	for (Catergory category : this.allowedfunctions){
    		for (Function function : this.<Function> getMap(MapLink.FUNCTIONS).values()) {
    			if (function.getCategories().contains(category) && !functions.contains(function)) {
    				functions.add(function);
    			}
    		}
    	}
    	return functions;
    }

    /**
     * @param color the color to set
     */
    public final void setColor(TColor color) {

        this.color = color;
    }

    public void setDescription(String desc) {
        description = desc;
    }

    public void setMaxAllowedFloors(int allowedFloors) {
        this.allowedFloors = allowedFloors;
    }

    public void setMultiPolygon(MultiPolygon coordinates) {
        this.polygons = coordinates;
    }

    public void setPlayable(Integer levelID, boolean playable) {
        this.playable.put(levelID, playable);
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
