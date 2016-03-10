/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import nl.tytech.core.item.annotations.AssetDirectory;
import nl.tytech.core.item.annotations.DoNotSaveToInit;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.data.core.item.CoreStakeholder;
import nl.tytech.data.core.item.Moment.SimTime;
import nl.tytech.data.core.item.SimTimeSetting;
import nl.tytech.data.engine.item.GlobalIndicator.GlobalIndicatorType;
import nl.tytech.data.engine.item.Indicator.TypeInterface;
import nl.tytech.data.engine.item.PersonalIndicator.PersonalIndicatorType;
import nl.tytech.data.engine.serializable.IndicatorWeight;
import nl.tytech.data.engine.serializable.MapType;
import nl.tytech.util.ObjectUtils;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;
import nl.tytech.util.logger.TLogger;
import com.vividsolutions.jts.geom.Point;

/**
 * Stakeholder
 * <p>
 * This class keeps track of the role of a Stakeholder
 * </p>
 * @author Maxim Knepfle, Frank Baars
 */
public class Stakeholder extends CoreStakeholder {

    public enum CreditRating {
        A(0.06), AAA(0.05), B(0.08), C(0.12), D(0.50);

        private double percentage = 0;

        CreditRating(double percentage) {
            this.percentage = percentage;
        }

        public double getPercentage() {
            return percentage;
        }
    }

    public enum Type {

        CIVILIAN(new TColor(250, 128, 114), 0, true, PersonalIndicatorType.FINANCE, GlobalIndicatorType.GREEN, GlobalIndicatorType.PARKING),

        COMPANY(TColor.GRAY, 500000, true, PersonalIndicatorType.FINANCE, GlobalIndicatorType.PARKING),

        EDUCATION(TColor.CYAN, 500000, false, PersonalIndicatorType.FINANCE, PersonalIndicatorType.HOUSING, GlobalIndicatorType.PARKING),

        EXPERT(TColor.BROWN, 0, false, PersonalIndicatorType.FINANCE),

        FARMER(new TColor(154, 205, 50), 500000, false, PersonalIndicatorType.FINANCE, PersonalIndicatorType.HOUSING),

        HEALTHCARE(TColor.MAGENTA, 1000000, false, PersonalIndicatorType.FINANCE, PersonalIndicatorType.HOUSING),

        HOUSING_CORPORATION(new TColor(238, 221, 130), 1000000, true, PersonalIndicatorType.FINANCE, PersonalIndicatorType.HOUSING),

        MUNICIPALITY(TColor.LIGHT_GRAY, 1000000, true, PersonalIndicatorType.FINANCE, PersonalIndicatorType.HOUSING,
                GlobalIndicatorType.GREEN, GlobalIndicatorType.WATER_STORAGE, GlobalIndicatorType.PARKING),

        MEDIA(TColor.PINK, 0, false),

        PROJECT_DEVELOPER(new TColor(178, 34, 34), 500000, true, PersonalIndicatorType.FINANCE, PersonalIndicatorType.HOUSING),

        UTILITY_CORPORATION(TColor.YELLOW, 1000000, false, PersonalIndicatorType.PIPE_NETWORK_CONNECTED,
                PersonalIndicatorType.PIPE_NETWORK_FINANCE, PersonalIndicatorType.PIPE_NETWORK_POLLUTION),

        WATER_BOARD(TColor.BLUE, 1000000, false, PersonalIndicatorType.FINANCE, GlobalIndicatorType.WATER_STORAGE),

        OTHER_AUTHORITY(TColor.DARK_GRAY, 1000000, false, PersonalIndicatorType.FINANCE),

        OTHER(TColor.DARK_GRAY, 0, false);

        public static final Type[] VALUES = values();

        public static Type[] getCommonStakeholders() {

            List<Type> types = new ArrayList<Type>();
            for (Type type : Type.values()) {
                if (type.common) {
                    types.add(type);
                }
            }
            return types.toArray(new Type[types.size()]);
        }

        private TColor defaultColor;
        private double defaultStartBudget;
        private boolean common;

        private ArrayList<TypeInterface> defaultIndicators;

        private Type(TColor defaultColor, int defaultStartBudget, boolean common, TypeInterface... defaultIndicators) {
            this.defaultColor = defaultColor;
            this.defaultStartBudget = defaultStartBudget;
            this.common = common;
            this.defaultIndicators = ObjectUtils.toArrayList(defaultIndicators);
        }

        public TColor getDefaultColor() {
            return defaultColor;
        }

        public double getDefaultStartBudget() {
            return defaultStartBudget;
        }

        public boolean isDefaultIndicator(TypeInterface type) {
            return defaultIndicators.contains(type);
        }

        /**
         * When the stakeholder is unique, a game can only contain 1 instance of of this stakeholder.
         */
        public boolean isUnique() {
            return this == MUNICIPALITY || this == WATER_BOARD;
        }
    }

    public static final String GUI_IMAGES_PORTRAITS = "Gui/Images/Portraits/";

    private static final long serialVersionUID = 178838230577313380L;

    @XMLValue
    private HashMap<Integer, String> assignments = new HashMap<>();

    @XMLValue
    private CreditRating creditRating = CreditRating.AAA;

    private ArrayList<IndicatorWeight> currentIndicatorWeights = new ArrayList<>();

    @XMLValue
    private HashMap<Integer, List<IndicatorWeight>> baseIndicatorWeights = new HashMap<>();

    @XMLValue
    @AssetDirectory(GUI_IMAGES_PORTRAITS)
    private String portrait = StringUtils.EMPTY;

    @DoNotSaveToInit
    @XMLValue
    @ListOfClass(Double.class)
    private ArrayList<Double> scores = new ArrayList<>();

    @XMLValue
    private double startBudget = 0d;

    @XMLValue
    private HashMap<Integer, Double> startBudgets = new HashMap<>();

    @XMLValue
    private HashMap<Integer, Integer> startCinematics = new HashMap<>();

    @XMLValue
    private Type type = Type.CIVILIAN;

    @XMLValue
    private double yearlyIncome = 0;

    @XMLValue
    @ItemIDField("LANDS")
    private ArrayList<Integer> ownedLands = new ArrayList<Integer>();

    @XMLValue
    private Point startPoint = null;

    @DoNotSaveToInit
    @XMLValue
    @ListOfClass(Double.class)
    private ArrayList<Double> yearlyIncomeMultipliers = new ArrayList<>();

    @XMLValue
    private String shortName = StringUtils.EMPTY;

    public void addOwnedLandID(Integer id) {
        ownedLands.add(id);
    }

    /**
     * @param scores the scores to set
     */
    public final void addScore(final double score) {

        this.scores.add(score);
    }

    public void addYearlyIncomeMultiplier(double yearlyIncomeMultiplier) {
        this.yearlyIncomeMultipliers.add(yearlyIncomeMultiplier);
    }

    /**
     * Actual score in the current map
     * @return
     */
    public double getActualScore(MapType mapType) {

        SimTimeSetting setting = this.getItem(MapLink.SIMTIME_SETTINGS, SimTimeSetting.Type.TYPE);
        SimTime simTime = setting.getEnumValue(SimTime.class);

        double score = 0;
        for (Indicator indicator : getMyIndicators()) {
            // when stakeholder is broke, you score zero!
            if (simTime == SimTime.TIMELINE && indicator.getType() == PersonalIndicatorType.FINANCE
                    && indicator.getExactNumberValue(mapType) != null && indicator.getExactNumberValue(mapType) < 0) {
                return 0;
            }
            double value = indicator.getValue(mapType);
            score += value * getCurrentIndicatorWeight(indicator);
        }
        return score;
    }

    public final String getAssignment() {
        Setting setting = this.getItem(MapLink.SETTINGS, Setting.Type.ACTIVE_LEVEL);
        return getAssignment(setting.getIntegerValue());
    }

    public final String getAssignment(Integer levelID) {

        // fallback to previous level value when not available.
        if (!assignments.containsKey(levelID)) {
            Collection<Level> previousLevels = getPreviousSortedItems(MapLink.LEVELS, levelID);
            for (Level previousLevel : previousLevels) {
                String previousAssign = assignments.get(previousLevel.getID());
                if (previousAssign != null) {
                    assignments.put(levelID, previousAssign);
                    break;
                }
            }
        }

        if (!assignments.containsKey(levelID)) {
            return StringUtils.EMPTY;
        }
        return this.assignments.get(levelID);
    }

    public final double getBaseIndicatorWeight(Indicator indicator) {
        Setting setting = this.getItem(MapLink.SETTINGS, Setting.Type.ACTIVE_LEVEL);
        return this.getBaseIndicatorWeight(setting.getIntegerValue(), indicator.getID());
    }

    public final double getBaseIndicatorWeight(Integer levelID, Integer indicatorID) {
        List<IndicatorWeight> weights = this.getBaseIndicatorWeights(levelID);
        for (IndicatorWeight indicatorWeight : weights) {
            if (indicatorID.equals(indicatorWeight.getIndicatorID())) {
                return indicatorWeight.getWeight();
            }
        }
        return 0f;
    }

    public List<IndicatorWeight> getBaseIndicatorWeights() {

        Setting setting = this.getItem(MapLink.SETTINGS, Setting.Type.ACTIVE_LEVEL);
        Integer levelID = setting.getIntegerValue();
        return this.getBaseIndicatorWeights(levelID);

    }

    public List<IndicatorWeight> getBaseIndicatorWeights(Integer levelID) {

        // fallback to previous level value when not available.
        if (!baseIndicatorWeights.containsKey(levelID)) {
            Collection<Level> previousLevels = getPreviousSortedItems(MapLink.LEVELS, levelID);
            for (Level plevel : previousLevels) {
                List<IndicatorWeight> previousWeights = baseIndicatorWeights.get(plevel.getID());
                if (previousWeights != null) {
                    baseIndicatorWeights.put(levelID, ObjectUtils.deepCopy(previousWeights));
                    break;
                }
            }
        }
        // if no fallback create empty list
        if (!baseIndicatorWeights.containsKey(levelID)) {
            baseIndicatorWeights.put(levelID, new ArrayList<IndicatorWeight>());
        }
        return baseIndicatorWeights.get(levelID);
    }

    /**
     * Returns players budget based on the finance indicator or NULL when there is no budget in the session (things are free)
     *
     * @param mapType
     * @return
     */
    public Double getBudget() {

        for (Indicator indicator : getMyIndicators()) {
            if (indicator.getType() == PersonalIndicatorType.FINANCE) {
                // Note: maptype is not relevant for finance indicator values are the same.
                Double budget = indicator.getExactNumberValue(MapType.CURRENT);
                if (budget != null) {
                    return budget.doubleValue();
                }
            }
        }
        return null;
    }

    public CreditRating getCreditRating() {
        return creditRating;
    }

    public final double getCurrentIndicatorWeight(Indicator indicator) {
        for (IndicatorWeight indicatorWeight : this.currentIndicatorWeights) {
            if (indicator.getID().equals(indicatorWeight.getIndicatorID())) {
                return indicator.isWeightless() ? 0 : indicatorWeight.getWeight();
            }
        }
        return 0f;
    }

    public List<IndicatorWeight> getCurrentIndicatorWeights() {
        return currentIndicatorWeights;
    }

    /**
     * @return the start
     */
    public final double getLevelStartBudget() {
        Setting setting = this.getItem(MapLink.SETTINGS, Setting.Type.ACTIVE_LEVEL);
        return getLevelStartBudget(setting.getIntegerValue());
    }

    public final double getLevelStartBudget(Integer levelID) {
        if (!startBudgets.containsKey(levelID)) {
            return 0;
        }
        return this.startBudgets.get(levelID);
    }

    public List<Indicator> getMyIndicators() {

        List<Indicator> result = new ArrayList<Indicator>();
        ItemMap<Indicator> indicators = this.getMap(MapLink.INDICATORS);
        for (Indicator indicator : indicators) {
            if (this.getCurrentIndicatorWeight(indicator) > 0
                    || (indicator instanceof PersonalIndicator && ((PersonalIndicator) indicator).getStakeholderID().equals(this.getID()) && indicator
                            .getType() == PersonalIndicatorType.FINANCE)) {
                result.add(indicator);
            }
        }
        return result;
    }

    public String getPortrait() {

        if (!StringUtils.containsData(portrait)) {
            return StringUtils.EMPTY;
        }
        return GUI_IMAGES_PORTRAITS + portrait;
    }

    public String getPortraitName() {
        return portrait;
    }

    /**
     * @return the scores
     */
    public final List<Double> getScores() {

        return this.scores;
    }

    public String getShortestName() {
        if (StringUtils.EMPTY.equals(shortName)) {
            return this.getName();
        } else {
            return this.shortName;
        }
    }

    public String getShortName() {
        return this.shortName;
    }

    public double getStartBudget() {
        return startBudget;
    }

    public CinematicData getStartCinematic() {
        Setting setting = this.getItem(MapLink.SETTINGS, Setting.Type.ACTIVE_LEVEL);
        Integer levelID = setting.getIntegerValue();
        return getStartCinematic(levelID);
    }

    public CinematicData getStartCinematic(Integer levelID) {
        Integer cinematicID = this.startCinematics.get(levelID);
        return this.getItem(MapLink.CINEMATIC_DATAS, cinematicID);
    }

    public Point getStartLocation() {
        return startPoint;
    }

    /**
     * @return the type
     */
    public final Type getType() {

        return this.type;
    }

    public int getYearlyIncome() {

        double income = yearlyIncome;

        for (double mult : yearlyIncomeMultipliers) {
            income = income * mult;
        }
        return (int) income;
    }

    /**
     * @return the civilian
     */
    public final boolean isCivilian() {

        return (this.getType() == Type.CIVILIAN);
    }

    public void removeAssignment(Integer levelID) {
        this.assignments.remove(levelID);
    }

    public boolean removeBaseWeightsForLevel(Integer levelID) {
        List<?> removed = this.baseIndicatorWeights.remove(levelID);
        return removed != null && removed.size() > 0;
    }

    public boolean removeIndicatorFromBaseWeightsAndLevel(Integer indicatorID, Integer levelID) {
        List<IndicatorWeight> weights = this.baseIndicatorWeights.get(levelID);
        ArrayList<IndicatorWeight> temp = new ArrayList<>();
        temp.addAll(weights);

        boolean removed = false;
        for (IndicatorWeight weight : temp) {
            if (indicatorID.equals(weight.getIndicatorID())) {
                weights.remove(weight);
                removed = true;
            }
        }
        return removed;
    }

    private boolean removeIndicatorFromCurrentWeights(Integer indicatorID) {
        ArrayList<IndicatorWeight> temp = new ArrayList<>();
        temp.addAll(currentIndicatorWeights);

        boolean removed = false;
        for (IndicatorWeight weight : temp) {
            if (indicatorID.equals(weight.getIndicatorID())) {
                currentIndicatorWeights.remove(weight);
                removed = true;
            }
        }
        temp.clear();
        return removed;
    }

    public boolean removeIndicatorWeights(Integer indicatorID) {
        removeIndicatorFromCurrentWeights(indicatorID);
        ArrayList<IndicatorWeight> temp = new ArrayList<>();

        boolean removed = false;
        for (List<IndicatorWeight> weightList : baseIndicatorWeights.values()) {
            temp.clear();
            temp.addAll(weightList);
            for (IndicatorWeight weight : temp) {
                if (indicatorID.equals(weight.getIndicatorID())) {
                    weightList.remove(weight);
                    removed = true;
                }
            }
        }
        return removed;
    }

    public boolean removeIndicatorWeightsForLevel(Integer indicatorID, Integer levelID) {
        Setting setting = this.getItem(MapLink.SETTINGS, Setting.Type.ACTIVE_LEVEL);
        boolean removed = false;
        if (levelID.equals(setting.getIntegerValue())) {
            removed |= removeIndicatorFromCurrentWeights(indicatorID);
        }
        removed |= removeIndicatorFromBaseWeightsAndLevel(indicatorID, levelID);
        return removed;

    }

    public void removeStartBudget(Integer id) {
        this.startBudgets.remove(id);
    }

    public void removeStartingCinematic(Integer levelID) {
        this.startCinematics.remove(levelID);
    }

    public void removeYearlyIncomeMultiplier(double yearlyIncomeMultiplier) {

        if (!this.yearlyIncomeMultipliers.contains(yearlyIncomeMultiplier)) {
            TLogger.severe("No multiplier available for " + this.getName() + " with value: " + yearlyIncomeMultiplier);
            return;
        }

        // delete only first instance
        for (int i = 0; i < yearlyIncomeMultipliers.size(); i++) {
            double item = yearlyIncomeMultipliers.get(i);
            if (item == yearlyIncomeMultiplier) {
                yearlyIncomeMultipliers.remove(i);
                return;
            }
        }
    }

    public void setBaseIndicatorWeight(Integer indicatorID, Double newWeight) {
        Setting setting = this.getItem(MapLink.SETTINGS, Setting.Type.ACTIVE_LEVEL);
        this.setBaseIndicatorWeight(setting.getIntegerValue(), indicatorID, newWeight);
    }

    public void setBaseIndicatorWeight(Integer levelID, Integer indicatorID, Double newWeight) {
        List<IndicatorWeight> weights = this.getBaseIndicatorWeights(levelID);
        for (IndicatorWeight indicatorWeight : weights) {
            if (indicatorID.equals(indicatorWeight.getIndicatorID())) {
                indicatorWeight.setWeight(newWeight);
                return;
            }
        }
        weights.add(new IndicatorWeight(indicatorID, newWeight));
    }

    public void setCreditRating(CreditRating creditRating) {
        this.creditRating = creditRating;
    }

    public void setCurrentIndicatorWeight(Indicator indicator, double newWeight) {
        setCurrentIndicatorWeight(indicator.getID(), newWeight);
    }

    public void setCurrentIndicatorWeight(Integer indicatorID, double newWeight) {
        for (IndicatorWeight indicatorWeight : this.currentIndicatorWeights) {
            if (indicatorID.equals(indicatorWeight.getIndicatorID())) {
                indicatorWeight.setWeight(newWeight);
                return;
            }
        }
        currentIndicatorWeights.add(new IndicatorWeight(indicatorID, newWeight));
    }

    public void setLevelStartBudget(double startBudget) {
        Setting setting = this.getItem(MapLink.SETTINGS, Setting.Type.ACTIVE_LEVEL);
        this.setLevelStartBudget(setting.getIntegerValue(), startBudget);
    }

    public void setLevelStartBudget(Integer levelID, double startBudget) {
        this.startBudgets.put(levelID, startBudget);
    }

    public void setNewAssignment(Integer levelID, String newAssignment) {
        this.assignments.put(levelID, newAssignment);
    }

    public void setNewAssignment(String newAssignment) {
        Setting setting = this.getItem(MapLink.SETTINGS, Setting.Type.ACTIVE_LEVEL);
        this.setNewAssignment(setting.getIntegerValue(), newAssignment);
    }

    public void setPortrait(String p) {
        portrait = p;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public void setStartBudget(double startBudget) {
        this.startBudget = startBudget;
    }

    public void setStartCinematic(Integer cinematicID) {
        Setting setting = this.getItem(MapLink.SETTINGS, Setting.Type.ACTIVE_LEVEL);
        this.setStartCinematic(setting.getIntegerValue(), cinematicID);
    }

    public void setStartCinematic(Integer levelID, Integer cinematicID) {
        this.startCinematics.put(levelID, cinematicID);
    }

    public void setStartLocation(Point startLocation) {
        this.startPoint = startLocation;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setYearlyIncome(int newYearlyIncome) {
        this.yearlyIncome = newYearlyIncome;
    }

    @Override
    public String validated(boolean startNewGame) {

        for (Entry<Integer, ?> entry : new ArrayList<>(startBudgets.entrySet())) {
            if (entry.getValue() instanceof Integer) {
                startBudgets.put(entry.getKey(), Double.valueOf("" + entry.getValue()));
            }
        }
        return super.validated(startNewGame);
    }
}
