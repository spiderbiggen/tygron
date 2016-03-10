/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.serializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;

/**
 *
 * @author Frank Baars
 *
 */
public class ScoreSet implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -2820681842836158664L;

    @XMLValue
    @ListOfClass(Double.class)
    private ArrayList<Double> subScores;

    @XMLValue
    private Double mainScore;

    public ScoreSet() {

    }

    public ScoreSet(Double mainScore, List<Double> subScores) {
        this.mainScore = mainScore;
        this.subScores = new ArrayList<>(subScores);
    }

    public Double getMainScore() {
        return this.mainScore;
    }

    public List<Double> getSubScores() {
        return this.subScores;
    }

}
