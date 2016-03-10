/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.serializable;

import nl.tytech.data.engine.item.BehaviorTerrain.Behavior;

/**
 *
 * @author Frank Baars
 *
 */
public enum MeasureEditType {

    BUILDING(false, null),

    DIKE(true, Behavior.GRASSLAND),

    FLATTEN(false, Behavior.GRASSLAND),

    WATER(true, Behavior.POLDER_WATER),

    UPGRADE(false, null);

    public final static MeasureEditType[] VALUES = MeasureEditType.values();

    private boolean isHeightMapEditType;
    private Behavior behavior;

    private MeasureEditType(boolean isHeightMapEditType, Behavior behavior) {
        this.isHeightMapEditType = isHeightMapEditType;
        this.behavior = behavior;

    }

    public Behavior getBehavior() {
        return behavior;
    }

    public boolean hasBehavior() {
        return behavior != null;
    }

    public boolean isHeightMapEditType() {
        return this.isHeightMapEditType;
    }
}
