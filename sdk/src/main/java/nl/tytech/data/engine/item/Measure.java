/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nl.tytech.core.event.EventValidationUtils;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.AssetDirectory;
import nl.tytech.core.item.annotations.DoNotSaveToInit;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.data.core.item.CodedEvent;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.item.UniqueNamedItem;
import nl.tytech.data.engine.item.ClientWord.ClientTerms;
import nl.tytech.data.engine.item.CustomIndicator.CustomIndicatorType;
import nl.tytech.data.engine.other.Action;
import nl.tytech.data.engine.other.TimeStateItem;
import nl.tytech.data.engine.serializable.IndicatorScore;
import nl.tytech.data.engine.serializable.MapType;
import nl.tytech.data.engine.serializable.TimeState;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;
import nl.tytech.util.logger.TLogger;

/**
 * Measure
 * <p>
 * This class keeps track of the base Measure.
 * </p>
 *
 * @author Maxim Knepfle
 */
public class Measure extends UniqueNamedItem implements Action, TimeStateItem {

    public enum ActionType {

        CONSTRUCTION_PLAN("On construction planned", false),

        CONSTRUCTION_PLAN_CANCEL("On construction canceled", false),

        CONSTRUCTION_START("On construction started", true),

        CONSTRUCTION_FINISHED("On construction finished", true);

        private String description;
        private boolean isTimelineOnly;

        private ActionType(String description, boolean isTimelineOnly) {
            this.description = description;
            this.isTimelineOnly = isTimelineOnly;
        }

        public String getDescription() {
            return this.description;
        }

        public boolean isTimelineOnly() {
            return isTimelineOnly;
        }
    }

    public enum CostType {
        CONSTRUCTION, DEMOLITION, MAINTENANCE
    }

    /**
     * Write of period in years of a building/measure.
     */
    public static final int MAINTENAINCE_PERIOD_IN_YEARS = 30;

    /** Generated serialVersionUID */
    private static final long serialVersionUID = -2467220889999050209L;

    @XMLValue
    private boolean innovative = false;

    @XMLValue
    private double fixedStorage = 0;

    @XMLValue
    private HashMap<ActionType, List<CodedEvent>> clientActionEvents = new HashMap<>();

    @XMLValue
    private HashMap<ActionType, List<CodedEvent>> serverActionEvents = new HashMap<>();

    @XMLValue
    protected double constructionCostsFixed = Item.NONE;

    @DoNotSaveToInit
    @XMLValue
    private Long constructionFinishDate;

    @XMLValue
    private Long constructionStartDate = null;

    @XMLValue
    private Long activationDate = null;

    @XMLValue
    private double constructionTimeInMonths = 3;

    @DoNotSaveToInit
    @XMLValue
    private boolean custom = false;

    @XMLValue
    protected double demolishCostsFixed = 0;

    @XMLValue
    private double incomeFixed = 0;

    @DoNotSaveToInit
    @XMLValue
    private Long demolishFinishDate;

    @DoNotSaveToInit
    @XMLValue
    private Long demolishStartDate;

    @XMLValue
    private double demolishTimeInMonths = 1;

    @ItemIDField("MEASURES")
    @XMLValue
    private ArrayList<Integer> dependencies = new ArrayList<>();

    @XMLValue
    private String description = StringUtils.EMPTY;

    @XMLValue
    protected double fixedMaintenanceCostsYear = Item.NONE;

    @AssetDirectory(GUI_IMAGES_ACTIONS)
    @XMLValue
    private String imageName = DEFAULT_IMAGE;

    @XMLValue
    @ListOfClass(IndicatorScore.class)
    private ArrayList<IndicatorScore> indicatorScores = new ArrayList<>();

    @ItemIDField("STAKEHOLDERS")
    @XMLValue
    private Integer maintenanceStakeholderID = Item.NONE;

    @ItemIDField("STAKEHOLDERS")
    @XMLValue
    private Integer ownerID = Item.NONE;

    @XMLValue
    private HashMap<Integer, Boolean> landOwnerPermissions = new HashMap<>();

    @DoNotSaveToInit
    @XMLValue
    private TimeState state = TimeState.NOTHING;

    @XMLValue
    private boolean confirmationsRequired = true;

    public Measure() {

    }

    public CodedEvent addEvent(ActionType actionType, EventTypeEnum type, Object... objects) {
        boolean isServerSide = type.isServerSide();
        CodedEvent event = CodedEvent.createUniqueIDEvent(getActionEventList(isServerSide, actionType), type, objects);
        getActionEventList(isServerSide, actionType).add(event);
        return event;
    }

    public boolean areConfirmationsRequired() {
        return confirmationsRequired;
    }

    public CodedEvent getActionEvent(Integer codedEventID, boolean serverSide, ActionType actionType) {
        List<CodedEvent> codedEvents = getActionEventList(serverSide, actionType);
        if (codedEvents != null) {
            for (CodedEvent codedEvent : codedEvents) {
                if (codedEvent.getID().equals(codedEventID)) {
                    return codedEvent;
                }
            }
        }
        return null;
    }

    /**
     * This method is a intermediate step to creating a timestate based measure event system
     *
     */
    public List<CodedEvent> getActionEventList(boolean serverSide, ActionType type) {

        Map<ActionType, List<CodedEvent>> actionEvents = serverSide ? this.serverActionEvents : this.clientActionEvents;
        if (!actionEvents.containsKey(type)) {
            actionEvents.put(type, new ArrayList<CodedEvent>());
        }
        return actionEvents.get(type);
    }

    public Long getActivationDate() {
        return activationDate;
    }

    public final double getAdditionalFixedWaterStorage() {
        return fixedStorage;
    }

    public double getConstructionCosts() {

        if (this.constructionCostsFixed >= 0) {
            return this.constructionCostsFixed;
        }
        return 0;
    }

    public double getConstructionCostsFixed() {
        return this.constructionCostsFixed;
    }

    /**
     * @return the finishDate
     */
    @Override
    public final Long getConstructionFinishDate() {
        return this.constructionFinishDate;
    }

    /**
     * @return the startDate
     */
    @Override
    public final Long getConstructionStartDate() {
        return this.constructionStartDate;
    }

    @Override
    public final double getConstructionTimeInMonths() {
        return this.constructionTimeInMonths;
    }

    public double getCosts(CostType costType) {
        switch (costType) {
            case CONSTRUCTION:
                return getConstructionCosts();
            case DEMOLITION:
                return getDemolishCosts();
            case MAINTENANCE:
                return getMaintenanceCostsYear();
            default:
                TLogger.severe("Attemption to find cost of unsupported type: " + costType);
                return 0;
        }
    }

    public double getDemolishCosts() {

        if (this.demolishCostsFixed >= 0) {
            return this.demolishCostsFixed;
        }
        return 0;
    }

    /**
     * @return the finishDemolishDate
     */
    @Override
    public final Long getDemolishFinishDate() {
        return this.demolishFinishDate;
    }

    /**
     * @return the startDemolishDate
     */
    @Override
    public final Long getDemolishStartDate() {
        return this.demolishStartDate;
    }

    @Override
    public final double getDemolishTimeInMonths() {
        return this.demolishTimeInMonths;
    }

    public double getDemolitionCostsFixed() {
        return this.demolishCostsFixed;
    }

    public final List<Integer> getDependencies() {
        return dependencies;
    }

    @Override
    public final String getDescription() {
        return description;
    }

    public double getFixedMaintenanceCostsYear() {
        return fixedMaintenanceCostsYear;
    }

    public final String getHTMLStateDescription() {

        Long date = null;
        String stateText = null;

        // wait for construction
        if (this.getTimeState().after(TimeState.WAITING_FOR_DATE) && this.getTimeState().before(TimeState.CONSTRUCTING)) {
            date = this.getConstructionStartDate();
            stateText = this.getWord(MapLink.CLIENT_WORDS, ClientTerms.CONSTRUCTION_STARTS_ON);

        } else if (this.getTimeState().after(TimeState.WAITING_FOR_DEMOLISH_DATE) && this.getTimeState().before(TimeState.DEMOLISHING)) {
            date = this.getDemolishStartDate();
            stateText = this.getWord(MapLink.CLIENT_WORDS, ClientTerms.CONSTRUCTION_STARTS_ON);

        } else if (this.getTimeState().equals(TimeState.CONSTRUCTING)) {
            date = this.getConstructionFinishDate();
            stateText = this.getWord(MapLink.CLIENT_WORDS, ClientTerms.CONSTRUCTION_FINISHES_ON);

        } else if (this.getTimeState().equals(TimeState.DEMOLISHING)) {
            date = this.getDemolishFinishDate();
            stateText = this.getWord(MapLink.CLIENT_WORDS, ClientTerms.CONSTRUCTION_FINISHES_ON);
        }
        if (stateText != null) {
            return "<br/><p><strong>" + stateText + "</strong></p><p>" + StringUtils.getHTMLColorFontOpeningTag(TColor.RED)
                    + StringUtils.dateToHumanString(date, true) + "</font></p>";
        }
        return StringUtils.EMPTY;
    }

    @Override
    public String getImageLocation() {
        if (!StringUtils.containsData(imageName)) {
            return StringUtils.EMPTY;
        }
        return GUI_IMAGES_ACTIONS + imageName;
    }

    public double getIncome() {

        if (this.incomeFixed >= 0) {
            return this.incomeFixed;
        }
        return 0;
    }

    public Double getIndicatorScore(Indicator indicator) {

        for (IndicatorScore indicatorScore : indicatorScores) {
            if (indicatorScore.getIndicatorID().equals(indicator.getID())) {
                return indicatorScore.getScore();
            }
        }
        return null;
    }

    public Double getIndicatorScore(Indicator.TypeInterface indicatorType) {

        ItemMap<Indicator> indicators = this.getMap(MapLink.INDICATORS);
        for (Indicator indicator : indicators) {
            if (indicator.getType() == indicatorType) {
                return getIndicatorScore(indicator);
            }
        }
        return null;
    }

    public List<IndicatorScore> getIndicatorScores() {
        return indicatorScores;
    }

    public double getMaintenanceCostsYear() {
        if (this.fixedMaintenanceCostsYear >= 0) {
            return this.fixedMaintenanceCostsYear;
        }
        return 0;
    }

    public final Stakeholder getMaintenanceStakeholder() {
        return this.getItem(MapLink.STAKEHOLDERS, getMaintenanceStakeholderID());
    }

    public final Integer getMaintenanceStakeholderID() {
        return this.maintenanceStakeholderID;
    }

    @Override
    public MapLink getMapLink() {
        return MapLink.MEASURES;
    }

    public final Stakeholder getOwner() {
        return this.getItem(MapLink.STAKEHOLDERS, getOwnerID());
    }

    public final Integer getOwnerID() {
        return this.ownerID;
    }

    /**
     * Percentage the measure is ready 0 nothing 1 is ready
     *
     * @return
     */
    public double getPercentageReady() {

        if (state.ordinal() < TimeState.CONSTRUCTING.ordinal()) {
            return 0f;
        } else if (state.ordinal() > TimeState.CONSTRUCTING.ordinal()) {
            return 1f;
        } else {
            // must be in building state
            long buildTimeInMillis = this.constructionFinishDate - this.constructionStartDate;
            if (buildTimeInMillis == 0) {
                return 0;
            }
            long current = this.getLord().getSimTimeMillis();
            long doneInMillis = current - this.constructionStartDate;
            return (double) doneInMillis / (double) buildTimeInMillis;
        }
    }

    public HashMap<Integer, Boolean> getPermits() {
        return landOwnerPermissions;
    }

    @Override
    public final TimeState getTimeState() {
        return state;
    }

    @Override
    public boolean isBuildable() {
        return true;
    }

    public boolean isCustom() {
        return custom;
    }

    @Override
    public boolean isFixedLocation() {
        return true;
    }

    public boolean isInMap(MapType mapType) {
        return this.getTimeState().isInMap(mapType);
    }

    public boolean isInnovative() {
        return innovative;
    }

    public boolean isMeasureScoring() {
        return this.indicatorScores.size() > 0;
    }

    public boolean isPhysical() {
        return false;
    }

    public boolean isVisible(MapType mapType) {
        // stakeholderID is irelevant in this case, measure is always visible when build.

        if (mapType == MapType.MAQUETTE && state.after(TimeState.NOTHING)) {
            return true;
        }
        if (mapType == MapType.CURRENT && state.ordinal() >= TimeState.READY.ordinal()) {
            return true;
        }
        return false;
    }

    /**
     * Removes score on indicator and return if the measure has a score on this indicator.
     * @param indicatorID
     * @return
     */
    public boolean removeIndicatorScore(Integer indicatorID) {

        boolean succes = false;
        List<IndicatorScore> scores = new ArrayList<IndicatorScore>(indicatorScores);
        for (IndicatorScore score : scores) {
            if (score.getIndicatorID().equals(indicatorID)) {
                indicatorScores.remove(score);
                succes = true;
            }
        }
        return succes;
    }

    public final void resetDates() {

        /**
         * Do not reset when measure start date is not handled by player.
         */
        if (this.areConfirmationsRequired()) {
            constructionStartDate = null;
        }
        constructionFinishDate = null;
        demolishStartDate = null;
        demolishFinishDate = null;
    }

    public void setActivationDate(Long activationDate) {
        this.activationDate = activationDate;
    }

    public void setConformationsRequired(boolean confirmation) {
        this.confirmationsRequired = confirmation;
    }

    public final void setConstructionCostsFixed(double price) {
        this.constructionCostsFixed = price;
    }

    public void setConstructionFinishDate(Long finishDate2) {
        this.constructionFinishDate = finishDate2;

    }

    public void setConstructionStartDate(Long startDate2) {
        this.constructionStartDate = startDate2;

    }

    public void setConstructionTimeInMonths(double time) {
        this.constructionTimeInMonths = time;
    }

    public void setCostFixed(CostType costType, double value) {
        switch (costType) {
            case CONSTRUCTION:
                this.constructionCostsFixed = value;
                break;
            case DEMOLITION:
                this.demolishCostsFixed = value;
                break;
            case MAINTENANCE:
                this.fixedMaintenanceCostsYear = value;
                break;
        }

    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }

    public void setDemolishFinishDate(Long finishDate2) {
        this.demolishFinishDate = finishDate2;

    }

    public void setDemolishStartDate(Long startDate2) {
        this.demolishStartDate = startDate2;

    }

    public void setDemolitionCostFixed(double value) {
        this.demolishCostsFixed = value;

    }

    public void setDemolitionTimeInMonths(double time) {
        this.demolishTimeInMonths = time;

    }

    public void setDescription(String description) {
        this.description = description;
    }

    public final void setFixedMaintenanceCostsYear(Double cost) {
        this.fixedMaintenanceCostsYear = cost;
    }

    public void setFixedStorage(double storage) {
        this.fixedStorage = storage;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;

    }

    public void setIndicatorScore(Indicator indicator, double value) {

        for (IndicatorScore indicatorScore : this.indicatorScores) {
            if (indicator.getID().equals(indicatorScore.getIndicatorID())) {
                indicatorScore.setScore(value);
                return;
            }
        }
        indicatorScores.add(new IndicatorScore(indicator.getID(), value));
    }

    public void setInnovative(Boolean innovative) {
        this.innovative = innovative;
    }

    public void setMaintenanceStakeholderID(Integer stakeholderID) {
        this.maintenanceStakeholderID = stakeholderID;
    }

    public final void setOwnerID(Integer stakeholderID) {
        this.ownerID = stakeholderID;
    }

    public final void setTimeState(final TimeState status) {
        this.state = status;
    }

    @Override
    public final String toString() {

        return getName();
    }

    @Override
    public String validated(boolean startNewGame) {

        String result = StringUtils.EMPTY;
        for (CostType costType : CostType.values()) {
            if (this.getCosts(costType) < 0) {
                result += "\nCost: " + costType + " is too low: " + this.getCosts(costType) + " for " + this.getName();
            }
        }

        for (List<CodedEvent> eventList : this.clientActionEvents.values()) {
            result += EventValidationUtils.validateCodedEvents(this, eventList, false);
        }
        for (List<CodedEvent> eventList : this.serverActionEvents.values()) {
            result += EventValidationUtils.validateCodedEvents(this, eventList, true);
        }

        /**
         * Check for correct scoring on MEASURE SUM indicators.
         */
        if (this.getLord() != null) {
            ItemMap<Indicator> indicators = this.getMap(MapLink.INDICATORS);
            for (IndicatorScore indicatorScore : indicatorScores) {
                Indicator indicator = indicators.get(indicatorScore.getIndicatorID());
                if (indicator == null || !(indicator.getType() instanceof CustomIndicatorType)) {
                    result += "\nMeasure " + this.getName() + " scores on indicator " + indicator
                            + ", however it must be an custom indicator type.";
                }
            }
        }
        return result;
    }
}
