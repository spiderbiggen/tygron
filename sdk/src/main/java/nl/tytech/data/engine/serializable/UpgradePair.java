/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.serializable;

import java.io.Serializable;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.Item;

/**
 * UpgradePair
 * <p>
 * Defines a pair of functions types can be upgraded. FROM (source) -> TO (target)
 * <p>
 *
 * @author Maxim Knepfle, Frank Baars
 */
public class UpgradePair implements Serializable {

    private static final long serialVersionUID = 4505099981435018509L;

    @XMLValue
    @ItemIDField("FUNCTIONS")
    private Integer sourceFunctionID = Item.NONE;

    @XMLValue
    @ItemIDField("FUNCTIONS")
    private Integer targetFunctionID = Item.NONE;

    public Integer getSourceFunctionID() {
        return sourceFunctionID;
    }

    public Integer getTargetFunctionID() {
        return targetFunctionID;
    }

    public void setSourceFunctionID(Integer id) {
        sourceFunctionID = id;
    }

    public void setTargetFunctionID(Integer id) {
        targetFunctionID = id;
    }

}
