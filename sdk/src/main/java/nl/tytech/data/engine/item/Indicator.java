/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import nl.tytech.core.item.annotations.DoNotSaveToInit;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.item.Moment.SimTime;
import nl.tytech.data.core.item.SimTimeSetting;
import nl.tytech.data.core.item.UniqueNamedItem;
import nl.tytech.data.engine.item.ClientWord.ClientTerms;
import nl.tytech.data.engine.item.GlobalIndicator.GlobalIndicatorType;
import nl.tytech.data.engine.item.PersonalIndicator.PersonalIndicatorType;
import nl.tytech.data.engine.serializable.MapType;
import nl.tytech.data.engine.serializable.TargetDescription;
import nl.tytech.util.ObjectUtils;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;
import nl.tytech.util.logger.TLogger;

/**
 * Indicator that shows a Stakeholder how he/she is doing.
 * @author Maxim Knepfle
 *
 */
public abstract class Indicator extends UniqueNamedItem {

    public interface TypeInterface {

        public TColor getDefaultColor();

        public double[] getDefaultTargetsCopy();

        public String getHumanString();

        public TargetDescription[] getTargetDescriptions();

        public boolean isGlobal();

        public boolean isSingleInstance();

    }

    /**
     *
     */
    private static final long serialVersionUID = -8776464666577232479L;

    public static final String IMAGE_LOCATION = "Gui/Images/Panels/TopMenu/Icons/";

    @DoNotSaveToInit
    @XMLValue
    private HashMap<Long, Double> indicatorScores = new HashMap<>();

    @DoNotSaveToInit
    @XMLValue
    private HashMap<MapType, Double> mapTypeValues = new HashMap<>();

    @DoNotSaveToInit
    @XMLValue
    private HashMap<MapType, Double> exactNumberValues = new HashMap<>();

    @DoNotSaveToInit
    @XMLValue
    private HashMap<MapType, String> exactTextValues = new HashMap<>();

    @XMLValue
    private HashMap<Integer, Boolean> active = new HashMap<>();

    @XMLValue
    private TColor color = TColor.BLACK;

    @XMLValue
    private String description = StringUtils.EMPTY;

    @DoNotSaveToInit
    @XMLValue
    private String explanation = StringUtils.EMPTY;

    @XMLValue
    private int iconSortIndex = 50;

    @XMLValue
    private boolean weightless = false;

    @XMLValue
    private boolean absolute = false;

    @XMLValue
    private String shortName = StringUtils.EMPTY;

    private long calcTime = 0;

    @XMLValue
    private String imageName = StringUtils.EMPTY;

    @XMLValue
    private HashMap<Integer, Double> startOfLevelValues = new HashMap<>();

    @XMLValue
    private HashMap<Integer, double[]> targets = new HashMap<>();

    @ItemIDField("VIDEOS")
    @XMLValue
    private final Integer videoID = Item.NONE;

    // local value used to check if asset was updated in editor
    private int imageVersion;

    /**
     * @param stakeholder the stakeholder for which the indicator value must be set (null if indicator is global)
     * @param moment value of the moment it has that value
     * @param score double value of the indicator
     */

    public final void addScoreHistory(long moment, final double score) {
        indicatorScores.put(moment, score);
    }

    public double getAbsoluteValue(final MapType mapType) {

        Double value = mapTypeValues.get(mapType);
        if (value == null) {
            return 0;
        }
        return value;
    }

    private Integer getActiveLevelID() {
        Setting setting = this.getItem(MapLink.SETTINGS, Setting.Type.ACTIVE_LEVEL);
        return setting.getIntegerValue();
    }

    public long getCalcTime() {
        return calcTime;
    }

    /**
     * @return the color
     */
    public final TColor getColor() {
        return this.color;
    }

    @Override
    public final String getDescription() {
        return description;
    }

    public Double getExactNumberValue(MapType mapType) {
        if (exactNumberValues.containsKey(mapType)) {
            return exactNumberValues.get(mapType);
        }
        return null;
    }

    public String getExactTextValue(MapType mapType) {
        if (exactTextValues.containsKey(mapType)) {
            return exactTextValues.get(mapType);
        }
        return null;
    }

    public String getExplanation() {

        String text = explanation;
        text += "<p></p><br/><p>";

        SimTimeSetting setting = this.getItem(MapLink.SIMTIME_SETTINGS, SimTimeSetting.Type.TYPE);
        SimTime simTime = setting.getEnumValue(SimTime.class);

        if (simTime == SimTime.TIMELINE) {
            text += this.getWord(MapLink.CLIENT_WORDS, ClientTerms.TIMELINE_CURRENT) + ": ";
            text += (Math.round(this.getValue(MapType.CURRENT) * 100f)) + "% ";
            text += this.getWord(MapLink.CLIENT_WORDS, ClientTerms.TIMELINE_MAQUETTE) + ": ";
            text += (Math.round(this.getValue(MapType.MAQUETTE) * 100f)) + "%";
        } else {
            text += this.getWord(MapLink.CLIENT_WORDS, ClientTerms.STAKEHOLDER_INDICATOR_SCORE) + ": ";
            text += (Math.round(this.getValue(MapType.MAQUETTE) * 100f)) + "%";
        }

        text += "</p>";
        return text;
    }

    public int getIconSortIndex() {

        // finance always first!
        if (this.getType() == PersonalIndicatorType.FINANCE) {
            return -1;
        }
        return iconSortIndex;
    }

    public String getImageLocation() {
        return IMAGE_LOCATION + getImageName();
    }

    public String getImageName() {
        return imageName;
    }

    public int getImageVersion() {
        return imageVersion;
    }

    /**
     * Score mapped from a starting value to 100%.
     * @param value
     * @return
     */
    private double getMappedScore(Integer levelID, double value) {

        double zeroScore = getStartOfLevelValue(levelID);
        double distance = 1f - zeroScore;
        if (distance == 0) {
            // when no distance, score 100%
            return 1d;
        }
        return (value - zeroScore) / distance;
    }

    public Map<Long, Double> getScoreHistory() {
        return this.indicatorScores;
    }

    /**
     * @return The indicator s short name
     */
    public String getShortName() {
        return shortName == null || StringUtils.EMPTY.equals(shortName) ? getName() : shortName;
    }

    protected double getStartOfLevelValue(Integer levelID) {

        // fallback to previous level value when not available.
        if (!startOfLevelValues.containsKey(levelID)) {
            Collection<Level> previousLevels = getPreviousSortedItems(MapLink.LEVELS, levelID);
            for (Level previousLevel : previousLevels) {
                Double previousStartValue = startOfLevelValues.get(previousLevel.getID());
                if (previousStartValue != null) {
                    startOfLevelValues.put(levelID, previousStartValue);
                    break;
                }
            }
        }
        Double result = this.startOfLevelValues.get(levelID);
        if (result == null) {
            // fallback to Zero
            return 0;
        }
        return result;
    }

    public double getTarget() {

        double[] targets = this.getTargets();
        if (targets == null || targets.length == 0) {
            return 0;
        }
        return targets[0];
    }

    public double[] getTargets() {
        Integer levelID = this.getActiveLevelID();
        return this.getTargets(levelID);
    }

    public double[] getTargets(Integer levelID) {

        // fallback to previous level value when not available.
        if (!targets.containsKey(levelID)) {
            Collection<Level> previousLevels = getPreviousSortedItems(MapLink.LEVELS, levelID);
            for (Level previousLevel : previousLevels) {
                double[] previousTargets = targets.get(previousLevel.getID());
                if (previousTargets != null) {
                    targets.put(levelID, ObjectUtils.deepCopy(previousTargets));
                    break;
                }
            }
        }
        // there is always one level with targets otherwise validate would fail.
        return targets.get(levelID);
    }

    public abstract TypeInterface getType();

    /**
     * Get mapped score value of this indicator.
     * @param mapType
     * @return
     */
    public double getValue(final MapType mapType) {
        double orginalValue = getAbsoluteValue(mapType);
        if (absolute) {
            return orginalValue;
        }
        return this.getMappedScore(getActiveLevelID(), orginalValue);
    }

    public Video getVideo() {
        return this.getItem(MapLink.VIDEOS, this.getVideoID());
    }

    public Integer getVideoID() {
        return videoID;
    }

    public boolean isAbsolute() {
        return absolute;
    }

    public boolean isActive() {
        return this.isActive(getActiveLevelID());
    }

    public boolean isActive(Integer levelID) {

        // fallback to previous level value when not available.
        if (!active.containsKey(levelID)) {
            Collection<Level> previousLevels = getPreviousSortedItems(MapLink.LEVELS, levelID);
            for (Level previousLevel : previousLevels) {
                Boolean previousActive = active.get(previousLevel.getID());
                if (previousActive != null) {
                    active.put(levelID, previousActive);
                    break;
                }
            }
        }
        return this.active.containsKey(levelID) && this.active.get(levelID);
    }

    public boolean isStartOfLevelSet() {
        return !startOfLevelValues.isEmpty();
    }

    /**
     * When true this indicator has no "weight" for all stakeholders. It is however shown in the top bar. E.g. the amount of people living
     * in the town.
     * @return
     */
    public boolean isWeightless() {
        return this.weightless;
    }

    public void removeExactNumberValues() {
        exactNumberValues.clear();
    }

    public void removeExactTextValues() {
        exactTextValues.clear();
    }

    public void removeLevelTarget(Integer levelID) {
        targets.remove(levelID);
    }

    public void resetStartOfLevelValues() {
        startOfLevelValues.clear();
    }

    public void setAbsolute(boolean absolute) {
        this.absolute = absolute;
    }

    private final void setAbsoluteValue(double[] values) {

        if (values.length != MapType.VALUES.length) {
            TLogger.severe("Double array values of function setValue for class " + GlobalIndicator.class.getSimpleName() + " with type "
                    + this.getType().toString() + "is not of the same size as size of enum " + MapType.class.getSimpleName() + "!"
                    + " Therefore, values are not set!");
        }
        for (MapType mapType : MapType.VALUES) {
            mapTypeValues.put(mapType, values[mapType.ordinal()]);
        }
    }

    public final void setAbsoluteValue(double[] values, final String explanation) {
        setAbsoluteValue(values);
        this.explanation = explanation;
    }

    public void setActive(boolean visible) {
        Setting setting = this.getItem(MapLink.SETTINGS, Setting.Type.ACTIVE_LEVEL);
        this.active.put(setting.getIntegerValue(), visible);
    }

    public void setActive(Integer levelID, boolean visible) {
        this.active.put(levelID, visible);
    }

    public void setCalcTime(long calcTime) {
        this.calcTime = calcTime;
    }

    public void setColor(TColor color) {
        this.color = color;
    }

    public void setDescription(String text) {
        this.description = text;
    }

    /**
     * @param stakeholder The stakeholder to apply the new value to (optional for global indicators)
     * @param rgba The new exact indicator value (not a percentage)
     */

    public void setExactNumberValue(double[] values) {
        for (MapType mapType : MapType.VALUES) {
            exactNumberValues.put(mapType, values[mapType.ordinal()]);
        }
    }

    public void setExactTextValues(String[] values) {
        for (MapType mapType : MapType.VALUES) {
            exactTextValues.put(mapType, values[mapType.ordinal()]);
        }
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
        this.imageVersion++;
    }

    public void setShortName(String text) {
        this.shortName = text;
    }

    public void setStartOfLevelValue(Integer levelID, double zeroScoreValue) {
        /**
         * When the score starts at 100%, I still need a starting value. Then the default value of 0 is used!
         */
        if (zeroScoreValue >= 1) {
            zeroScoreValue = 0f;
        }
        this.startOfLevelValues.put(levelID, zeroScoreValue);
    }

    public void setTargets(double[] targets) {
        Setting setting = this.getItem(MapLink.SETTINGS, Setting.Type.ACTIVE_LEVEL);
        this.setTargets(setting.getIntegerValue(), targets);
    }

    public void setTargets(Integer levelID, double[] targets) {
        this.targets.put(levelID, targets);
    }

    /**
     * Show exact value instead of progress bar
     * @return
     */
    public boolean showExact() {
        return getType() == PersonalIndicatorType.FINANCE || getType() == PersonalIndicatorType.PIPE_NETWORK_FINANCE;
    }

    @Override
    public String toString() {

        if (this.getName() == null || this.getName().equals(StringUtils.EMPTY) || this.getName().equals("0_new")) {
            if (getType() != null) {
                return StringUtils.capitalizeWithSpacedUnderScores(getType().toString());
            } else {
                if (this instanceof GlobalIndicator) {
                    return "Global indicator";
                } else {
                    return "Personal indicator";
                }
            }
        }
        return this.getName();
    }

    @Override
    public String validated(boolean startNewGame) {

        if (this.getType() == null) {
            return "Missing type for indicator: " + this.getName();
        }

        if (!StringUtils.containsData(imageName)) {
            imageName = getType().toString().toLowerCase() + ".png";
        }

        this.description = StringUtils.removeHTMLTags(this.description);

        String result = StringUtils.EMPTY;
        TargetDescription[] targetDescriptions = this.getType().getTargetDescriptions();
        ItemMap<Level> levels = this.getMap(MapLink.LEVELS);

        for (Level level : levels.values()) {

            double[] targets = this.getTargets(level.getID());
            if (this.getType() == GlobalIndicatorType.DEPRECATED) {
                // ignore
            } else if (targets == null) {
                result += "\nMissing targets for indicator: " + this.getName() + " in level: " + level.getName() + "!";

            } else if (targetDescriptions.length != targets.length) {
                String text = StringUtils.EMPTY;
                int i = 0;
                for (TargetDescription targetDescription : targetDescriptions) {
                    String addition = StringUtils.EMPTY;
                    if (targetDescription.hasROTermAddition()) {
                        ClientWord clientWord = this.getItem(MapLink.CLIENT_WORDS, targetDescription.getROTermAddition());
                        addition = clientWord.getTranslation();
                    }
                    text += "\n" + i + ": " + targetDescription.getDescription() + addition + " Between: "
                            + targetDescription.getMinValue() + " - " + targetDescription.getMaxValue();
                    i++;
                }
                result += "\nIncorrect targets for: " + this.getName() + " in level: " + level.getName() + ", use:" + text;

            } else {
                for (int i = 0; i < targets.length; i++) {
                    TargetDescription targetDescription = targetDescriptions[i];
                    double target = targets[i];
                    if (target < targetDescription.getMinValue() || target > targetDescription.getMaxValue()) {
                        result += "\nTarget " + i + " of indicator " + this.getName() + " in level: " + level.getName()
                                + " should be between: " + targetDescription.getMinValue() + " - " + targetDescription.getMaxValue();
                    }
                }
            }
        }
        return result;
    }
}
