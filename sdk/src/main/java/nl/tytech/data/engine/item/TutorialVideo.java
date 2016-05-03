/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import java.util.ArrayList;
import nl.tytech.core.item.annotations.DoNotSaveToInit;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;

/**
 * Video that is used as a Tutorial
 * @author Maxim Knepfle
 *
 */
public class TutorialVideo extends Video {

    /**
     *
     */
    private static final long serialVersionUID = -6754893695766076519L;

    @DoNotSaveToInit
    @ItemIDField("STAKEHOLDERS")
    @XMLValue
    private ArrayList<Integer> viewedStakeholders = new ArrayList<>();

    @DoNotSaveToInit
    @ItemIDField("STAKEHOLDERS")
    @XMLValue
    private ArrayList<Integer> firedStakeholders = new ArrayList<>();

    public boolean firedAlready(Integer stakeholderID) {
        return firedStakeholders.contains(stakeholderID);
    }

    public void setFired(Integer stakeholderID) {
        if (!viewedAlready(stakeholderID)) {
            firedStakeholders.add(stakeholderID);
        }
    }

    public void setViewed(Integer stakeholderID) {
        if (!viewedAlready(stakeholderID)) {
            viewedStakeholders.add(stakeholderID);
        }
    }

    public boolean viewedAlready(Integer stakeholderID) {
        return viewedStakeholders.contains(stakeholderID);
    }
}
