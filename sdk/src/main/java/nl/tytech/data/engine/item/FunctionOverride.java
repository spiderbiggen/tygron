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
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.item.ClientWord.ClientTerms;
import nl.tytech.data.engine.other.Action;
import nl.tytech.data.engine.serializable.Category;
import nl.tytech.data.engine.serializable.CategoryValue;
import nl.tytech.data.engine.serializable.FunctionValue;
import nl.tytech.data.engine.serializable.IndicatorScore;
import nl.tytech.util.ObjectUtils;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;
import nl.tytech.util.logger.TLogger;

/**
 * FunctionOverride
 * <p>
 * Score of custom indicator and override default values of the function. function ID = override ID
 * </p>
 * @author Maxim Knepfle
 */
public class FunctionOverride extends Item {

    public enum AssetValue {
        NAME(ClientTerms.NAME), DESCRIPTION(ClientTerms.DESCRIPTION), IMAGELOCATION(ClientTerms.IMAGE), ROOFCOLOR(ClientTerms.ROOFCOLOR), WALLCOLOR(
                ClientTerms.WALLCOLOR);

        private ClientTerms term;

        private AssetValue(ClientTerms term) {
            this.term = term;
        }

        public ClientTerms getTerm() {
            return term;
        }
    }

    /**
     *
     */
    private static final long serialVersionUID = -640351328367911902L;

    @XMLValue
    @ListOfClass(IndicatorScore.class)
    protected ArrayList<IndicatorScore> indicatorScores = new ArrayList<>();

    @XMLValue
    private HashMap<FunctionValue, Double> functionValues = new HashMap<>();

    @XMLValue
    private HashMap<Category, Map<CategoryValue, Double>> categoryValues = new HashMap<>();

    @XMLValue
    private String name = StringUtils.EMPTY;

    @XMLValue
    private String description = StringUtils.EMPTY;

    @XMLValue
    private String imageName = StringUtils.EMPTY;

    @XMLValue
    private TColor roofColor = null;

    @XMLValue
    private TColor wallColor = null;

    public boolean addNewCategory(Category cat) {

        readDefaults();

        for (Category oldCat : categoryValues.keySet()) {
            if (oldCat.isRoad()) {
                return false;
            }
        }

        if (!categoryValues.containsKey(cat) && !cat.isRoad()) {
            categoryValues.put(cat, new HashMap<CategoryValue, Double>());
            return true;
        }
        return false;
    }

    public void clearFunctionValues() {
        this.functionValues.clear();
        this.categoryValues.clear();
    }

    public boolean deleteCategory(Category cat) {
        readDefaults();
        return categoryValues.remove(cat) != null;
    }

    public Set<Category> getCategories() {
        return categoryValues.keySet();
    }

    public double getCategoryPercentage(Category cat) {
        double sum = 0;
        for (Category someCat : getCategories()) {
            sum += getCategoryValue(someCat, CategoryValue.CATEGORY_WEIGHT);
        }
        if (sum == 0) {
            return 0;
        }
        return getCategoryValue(cat, CategoryValue.CATEGORY_WEIGHT) / sum;
    }

    public Double getCategoryValue(Category cat, CategoryValue key) {

        if (!categoryValues.containsKey(cat)) {
            return null;
        }
        return this.categoryValues.get(cat).get(key);
    }

    @Override
    public String getDescription() {
        return description;
    }

    public Function getFunction() {
        return this.getItem(MapLink.FUNCTIONS, this.getFunctionID());
    }

    public Integer getFunctionID() {
        return this.getID();
    }

    public Double getFunctionValue(FunctionValue value) {

        return functionValues.get(value);
    }

    public String getImageLocation() {
        return Action.GUI_IMAGES_ACTIONS + imageName;
    }

    public String getImageName() {
        return imageName;
    }

    private IndicatorScore getIndicatorScoreOrGenerate(Integer indicatorID) {

        for (IndicatorScore indicatorScore : indicatorScores) {
            if (indicatorScore.getIndicatorID().equals(indicatorID)) {
                return indicatorScore;
            }
        }

        IndicatorScore newScore = new IndicatorScore(indicatorID, 0);
        indicatorScores.add(newScore);
        return newScore;
    }

    public Double getIndicatorScorePer100M2(Indicator indicator) {
        for (IndicatorScore indicatorScore : indicatorScores) {
            if (indicatorScore.getIndicatorID().equals(indicator.getID())) {
                return indicatorScore.getScore();
            }
        }
        return null;
    }

    public List<IndicatorScore> getIndicatorScores() {
        return indicatorScores;
    }

    public Double getMaxPoints(Indicator indicator) {
        for (IndicatorScore indicatorScore : indicatorScores) {
            if (indicatorScore.getIndicatorID().equals(indicator.getID())) {
                return indicatorScore.getMaxPoints();
            }
        }
        return 0d;
    }

    public String getName() {
        return name;
    }

    public TColor getRoofColor() {
        return roofColor;
    }

    public TColor getWallColor() {
        return wallColor;
    }

    private void readDefaults() {
        // if zero fill with defaults.
        if (categoryValues.size() == 0) {
            Function function = this.getFunction();
            for (Category defaultCat : function.getCategories()) {
                categoryValues.put(defaultCat, new HashMap<CategoryValue, Double>());
            }
        }
    }

    /**
     * Removes score on indicator and return if the override has a score on this indicator.
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

    public void setCategoryValue(Category cat, CategoryValue type, Double value) {

        readDefaults();
        categoryValues.get(cat).put(type, value);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFunctionValue(FunctionValue type, Double value) {
        this.functionValues.put(type, value);
    }

    public void setImageName(String image) {
        this.imageName = image;
    }

    public void setIndicatorScorePer100M2(Integer indicatorID, double scoreValue) {

        IndicatorScore score = getIndicatorScoreOrGenerate(indicatorID);
        score.setScore(scoreValue);
    }

    public void setIndicatorScores(List<IndicatorScore> indicatorScores) {
        this.indicatorScores = ObjectUtils.toArrayList(indicatorScores);
    }

    public void setMaxPoints(Integer indicatorID, double maxPoints) {
        IndicatorScore score = getIndicatorScoreOrGenerate(indicatorID);
        score.setMaxPoints(maxPoints);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRoofColor(TColor roofColor) {
        this.roofColor = roofColor;
    }

    public void setWallColor(TColor wallColor) {
        this.wallColor = wallColor;
    }

    @Override
    public String toString() {
        Function function = this.getFunction();
        if (function != null) {
            return function.getName();
        }
        return FunctionOverride.class.getSimpleName() + StringUtils.WHITESPACE + getID();
    }

    @Override
    public String validated(boolean start) {

        for (Category cat : this.getCategories()) {
            Map<CategoryValue, Double> map = categoryValues.get(cat);
            Double value = map.get(CategoryValue.PARKING_LOTS_PER_M2);
            if (value != null && value.doubleValue() < 0) {
                map.put(CategoryValue.PARKING_LOTS_DEMAND_PER_M2, Math.abs(value));
                map.remove(CategoryValue.PARKING_LOTS_PER_M2);
                TLogger.info("Moved parking demand to seperate value (" + value + ") for function override: " + this.getName());
            }
        }
        return super.validated(start);
    }
}
