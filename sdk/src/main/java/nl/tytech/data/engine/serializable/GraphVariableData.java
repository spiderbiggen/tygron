/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.serializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nl.tytech.core.item.annotations.DoNotSaveToInit;
import nl.tytech.core.item.annotations.XMLValue;

/**
 *
 * @author Frank Baars
 *
 */
public class GraphVariableData implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 9169313804513380215L;

    private static final VariableValuesPair[] EMPTY_LIST = new VariableValuesPair[0];

    @DoNotSaveToInit
    @XMLValue
    private HashMap<Long, Map<String, ScoreSet>> scoringFactors = new HashMap<>();

    @DoNotSaveToInit
    @XMLValue
    private HashMap<String, List<VariablePackage>> subVariablesMap = new HashMap<>();

    @DoNotSaveToInit
    @XMLValue
    private HashMap<String, VariablePackage> mainVariableMap = new HashMap<>();

    public GraphVariableData() {

    }

    private ScoreSet constructNewDoubleList(VariableValuesPair mainVariable, VariableValuesPair[] values) {
        List<Double> listValues = new ArrayList<Double>();
        for (VariableValuesPair pair : values) {
            listValues.add(pair.getValue());
        }
        ScoreSet result = new ScoreSet(mainVariable.getValue(), listValues);
        return result;
    }

    private HashMap<String, ScoreSet> constructNewScoreSetEntry(VariableValuesPair mainVariable, VariableValuesPair[] values) {

        HashMap<String, ScoreSet> data = new HashMap<>();
        data.put(mainVariable.getName(), constructNewDoubleList(mainVariable, values));
        return data;
    }

    private VariablePackage[] getColorNamePairs(VariableValuesPair[] values) {
        VariablePackage[] result = new VariablePackage[values.length];
        for (int i = 0; i < values.length; ++i) {
            result[i] = values[i].getVariablePackage();
        }
        return result;
    }

    public Long getLatestTimeMoment() {

        Long best = null;
        for (Long timeMillis : this.scoringFactors.keySet()) {
            if (best == null) {
                best = timeMillis;
            } else {
                if (timeMillis > best) {
                    best = timeMillis;
                }
            }
        }
        return best;
    }

    public Map<String, VariablePackage> getParentVariables() {
        return this.mainVariableMap;
    }

    public double getScoreOfVariable(ParentVariable parentVariable, Long calendar) {
        return this.getScoreOfVariableWithName(parentVariable.getName(), calendar);
    }

    public double getScoreOfVariableWithName(String name, Long calendar) {
        if (scoringFactors.containsKey(calendar)) {
            Map<String, ScoreSet> scoresetMap = scoringFactors.get(calendar);
            if (scoresetMap.containsKey(name)) {
                return scoresetMap.get(name).getMainScore();
            }
        }
        return 0f;
    }

    public Map<Long, Map<String, ScoreSet>> getScoringFactors() {
        return this.scoringFactors;
    }

    public List<VariablePackage> getSortedListOfParentVariables() {
        ArrayList<VariablePackage> result = new ArrayList<VariablePackage>(this.mainVariableMap.values());
        Collections.sort(result);
        return result;
    }

    public VariableValuesPair getVariableValuePairWithName(String name, Long timeMillis) {
        double score = getScoreOfVariableWithName(name, timeMillis);
        return new VariableValuesPair(mainVariableMap.get(name), score);
    }

    private void setMainVariableData(VariableValuesPair mainVariable) {
        mainVariableMap.put(mainVariable.getName(), mainVariable.getVariablePackage());
    }

    public final void setScoringValue(VariableValuesPair mainVariable, Long moment) {
        setScoringValue(mainVariable, moment, EMPTY_LIST);
    }

    public final void setScoringValue(VariableValuesPair mainVariable, Long moment, VariableValuesPair[] values) {
        Map<String, ScoreSet> entry = scoringFactors.get(moment);
        if (entry != null) {
            entry.put(mainVariable.getName(), constructNewDoubleList(mainVariable, values));
        } else {
            Map<String, ScoreSet> l = constructNewScoreSetEntry(mainVariable, values);
            scoringFactors.put(moment, l);
        }
        setMainVariableData(mainVariable);
        setVariableData(mainVariable.getVariablePackage(), getColorNamePairs(values));
    }

    private void setVariableData(VariablePackage mainVariable, VariablePackage[] subVariables) {
        if (subVariablesMap.containsKey(mainVariable.getName())) {

            List<VariablePackage> oldVariables = subVariablesMap.get(mainVariable.getName());
            if (subVariables.length != oldVariables.size()) {
                List<VariablePackage> newVariableList = new ArrayList<VariablePackage>(subVariables.length);
                for (int i = 0; i < subVariables.length; ++i) {
                    newVariableList.set(i, new VariablePackage(subVariables[i]));
                }
                subVariablesMap.put(mainVariable.getName(), newVariableList);
            } else {
                for (int i = 0; i < subVariables.length; ++i) {
                    if (!subVariables[i].equals(oldVariables.get(i))) {
                        oldVariables.get(i).copy(subVariables[i]);
                    }
                }
            }
        } else {
            ArrayList<VariablePackage> newPairs = new ArrayList<VariablePackage>(subVariables.length);
            for (int i = 0; i < subVariables.length; ++i) {
                newPairs.add(subVariables[i]);
            }
            subVariablesMap.put(mainVariable.getName(), newPairs);

        }
    }
}
