/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import nl.tytech.core.item.annotations.AssetDirectory;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.other.Action;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;
import nl.tytech.util.logger.TLogger;

/**
 * ActionMenu
 * <p>
 * ActionMenu contains a set of Actions that the stakeholder can build. Each Action menu has a unique icon in the menu bar.
 * </p>
 *
 * @author Maxim Knepfle & Alexander Hofstede
 */
public class ActionMenu extends Item {

    /**
     *
     */
    public static final String GUI_IMAGES_GUICATEGORY_ICONS = "Gui/Images/Panels/LeftMenuPanel/Icons/";

    /** Generated serialVersionUID */
    private static final long serialVersionUID = -2467220889999050209L;

    @XMLValue
    private HashMap<Integer, List<Integer>> buildable = new HashMap<>();

    @XMLValue
    private TColor color;

    @XMLValue
    private String description = StringUtils.EMPTY;

    @ItemIDField("FUNCTIONS")
    @XMLValue
    private ArrayList<Integer> functionTypes = new ArrayList<Integer>();

    @ItemIDField("DIKES")
    @XMLValue
    private ArrayList<Integer> dikes = new ArrayList<Integer>();

    @ItemIDField("MEASURES")
    @XMLValue
    private ArrayList<Integer> measures = new ArrayList<Integer>();

    @ItemIDField("EVENT_BUNDLES")
    @XMLValue
    private ArrayList<Integer> eventBundles = new ArrayList<Integer>();

    @ItemIDField("UPGRADE_TYPES")
    @XMLValue
    private ArrayList<Integer> upgradeTypes = new ArrayList<Integer>();

    @XMLValue
    @ListOfClass(SpecialOption.Type.class)
    private ArrayList<SpecialOption.Type> specialOptions = new ArrayList<>();

    @XMLValue
    private String name = "New action menu";

    @AssetDirectory(GUI_IMAGES_GUICATEGORY_ICONS)
    @XMLValue
    private String iconFileName = "category_house.png";

    @XMLValue
    private int iconSortIndex = 0;

    @ItemIDField("VIDEOS")
    @XMLValue
    private final Integer videoID = Item.NONE;

    /**
     * XXX:TODO: FRANK: Make sure this is backwards compatible with e.g. Climategame and then remove it again!
     */
    @XMLValue
    @Deprecated
    private HashMap<Integer, Boolean> activeForStakeholder = new HashMap<>();

    public ActionMenu() {

    }

    public void addOption(Action option) {
        if (option instanceof Function) {
            functionTypes.add(option.getID());
        } else if (option instanceof Measure) {
            measures.add(option.getID());
        } else if (option instanceof UpgradeType) {
            upgradeTypes.add(option.getID());
        } else if (option instanceof SpecialOption) {
            specialOptions.add(((SpecialOption) option).getType());
        } else if (option instanceof EventBundle) {
            eventBundles.add(option.getID());
        } else if (option instanceof Dike) {
            dikes.add(option.getID());
        } else {
            TLogger.severe("Unknown option!");
        }
    }

    public final boolean contains(final Action option) {

        List<Integer> ids;
        if (option instanceof Function) {
            ids = functionTypes;
        } else if (option instanceof Measure) {
            ids = measures;
        } else if (option instanceof UpgradeType) {
            ids = upgradeTypes;
        } else if (option instanceof SpecialOption) {
            return specialOptions.contains(((SpecialOption) option).getType());
        } else if (option instanceof EventBundle) {
            ids = eventBundles;
        } else if (option instanceof Dike) {
            ids = dikes;
        } else {
            TLogger.severe("Unknown option!");
            return false;
        }
        for (Integer ID : ids) {
            if (option.getID().equals(ID)) {
                return true;
            }
        }
        return false;
    }

    private final List<Integer> getBuildables(Integer levelID) {
        // fallback to previous level value when not available.
        if (!buildable.containsKey(levelID)) {
            Collection<Level> previousLevels = getPreviousSortedItems(MapLink.LEVELS, levelID);
            for (Level previousLevel : previousLevels) {
                List<Integer> previousBuildable = buildable.get(previousLevel.getID());
                if (previousBuildable != null) {
                    buildable.put(levelID, new ArrayList<Integer>(previousBuildable));
                    break;
                }
            }
        }
        if (!buildable.containsKey(levelID)) {
            buildable.put(levelID, new ArrayList<Integer>());
        }
        return buildable.get(levelID);
    }

    public TColor getColor() {
        return color;
    }

    @Override
    public final String getDescription() {
        return description;
    }

    public List<Integer> getFunctionTypeOptionIDs() {
        return functionTypes;
    }

    /**
     * @return The function types defined for this Category
     */
    public List<Function> getFunctionTypeOptions() {
        return this.getItems(MapLink.FUNCTIONS, functionTypes);
    }

    public final List<Action> getGUIOptions() {

        List<Action> options = new ArrayList<Action>();
        options.addAll(this.<Measure> getItems(MapLink.MEASURES, measures));
        options.addAll(this.<EventBundle> getItems(MapLink.EVENT_BUNDLES, eventBundles));
        options.addAll(this.<Function> getItems(MapLink.FUNCTIONS, functionTypes));
        options.addAll(this.<Dike> getItems(MapLink.DIKES, dikes));
        options.addAll(this.<UpgradeType> getItems(MapLink.UPGRADE_TYPES, upgradeTypes));
        options.addAll(this.<SpecialOption, SpecialOption.Type> getEnumItems(MapLink.SPECIAL_OPTIONS, specialOptions));
        return options;
    }

    public int getIconSortIndex() {
        return iconSortIndex;
    }

    public String getImageLocation() {
        if (!StringUtils.containsData(iconFileName)) {
            return StringUtils.EMPTY;
        }
        return GUI_IMAGES_GUICATEGORY_ICONS + iconFileName;
    }

    public String getImageName() {
        return iconFileName;
    }

    public List<Integer> getMeasureIDList() {
        return measures;
    }

    public final List<Measure> getMeasureOptions() {
        return this.getItems(MapLink.MEASURES, measures);
    }

    /**
     * @return the name
     */
    public final String getName() {
        return this.name;
    }

    public List<SpecialOption> getSpecialOptions() {
        return this.getEnumItems(MapLink.SPECIAL_OPTIONS, specialOptions);
    }

    /**
     * @return The upgrade types defined for this Category
     */
    public List<UpgradeType> getUpgradeTypeOptions() {
        return this.getItems(MapLink.UPGRADE_TYPES, upgradeTypes);
    }

    public List<Integer> getUpgradeTypeOptionsIDs() {
        return upgradeTypes;
    }

    public Video getVideo() {
        return this.getItem(MapLink.VIDEOS, this.getVideoID());
    }

    public Integer getVideoID() {
        return videoID;
    }

    public final boolean isBuildable(Integer stakeholderID) {
        Setting setting = this.getItem(MapLink.SETTINGS, Setting.Type.ACTIVE_LEVEL);
        return this.isBuildable(setting.getIntegerValue(), stakeholderID);
    }

    public final boolean isBuildable(Integer levelID, Integer stakeholderID) {

        List<Integer> buildables = getBuildables(levelID);
        for (Integer buildablestakeholderID : buildables) {
            if (buildablestakeholderID.equals(stakeholderID)) {
                return true;
            }
        }
        return false;
    }

    public boolean isInbox() {
        return specialOptions.contains(SpecialOption.Type.INBOX);
    }

    public void removeAction(Action option) {
        if (option instanceof Function) {
            functionTypes.remove(option.getID());
        } else if (option instanceof Measure) {
            measures.remove(option.getID());
        } else if (option instanceof UpgradeType) {
            upgradeTypes.remove(option.getID());
        } else if (option instanceof SpecialOption) {
            specialOptions.remove(((SpecialOption) option).getType());
        } else if (option instanceof EventBundle) {
            eventBundles.remove(option.getID());
        } else if (option instanceof Dike) {
            dikes.remove(option.getID());
        } else {
            TLogger.severe("Unknown option!");
        }
    }

    public void removeBuildableLevel(Integer levelID) {
        this.buildable.remove(levelID);
    }

    public void setBuildableForAllLevels(Integer stakeholderID, boolean setBuildable) {
        ItemMap<Level> gameLevels = getMap(MapLink.LEVELS);
        for (Level gameLevel : gameLevels) {
            this.setBuildableForLevel(gameLevel.getID(), stakeholderID, setBuildable);
        }
    }

    public void setBuildableForLevel(Integer levelID, Integer stakeholderID, boolean setBuildable) {

        List<Integer> buildables = this.getBuildables(levelID);
        if (setBuildable && !buildables.contains(stakeholderID)) {
            buildables.add(stakeholderID);
        }
        if (!setBuildable && buildables.contains(stakeholderID)) {
            buildables.remove(stakeholderID);
        }
    }

    public void setFunctions(Collection<Function> functions) {
        for (Function function : functions) {
            functionTypes.add(function.getID());
        }
    }

    public void setIconFileName(String iconName) {
        this.iconFileName = iconName;
    }

    public void setIconSortIndex(int index) {
        this.iconSortIndex = index;
    }

    public void setMeasureIDList(ArrayList<Integer> measureList) {
        this.measures = measureList;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public void setSpecialOptions(List<SpecialOption.Type> specialEditorOptions) {
        specialOptions.addAll(specialEditorOptions);
    }

    @Override
    public final String toString() {
        return getName();
    }
}
