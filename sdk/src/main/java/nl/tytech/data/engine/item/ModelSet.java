/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.Item;

/**
 *
 * A collection of 3d models that belong together
 *
 * @author Maxim Knepfle
 *
 */
public class ModelSet extends Item {

    /**
     *
     */
    private static final long serialVersionUID = 5713247932428755794L;

    @XMLValue
    @ItemIDField("MODEL_DATAS")
    private ArrayList<Integer> models = new ArrayList<Integer>();

    @XMLValue
    private String name = "0_new modelset";

    @XMLValue
    private double roofInset = 0;

    public List<Integer> getModelIDs() {
        return models;
    }

    public String getName() {
        return name;
    }

    public double getRoofInset() {
        return roofInset;
    }

    @Override
    public String toString() {
        return name;
    }
}
