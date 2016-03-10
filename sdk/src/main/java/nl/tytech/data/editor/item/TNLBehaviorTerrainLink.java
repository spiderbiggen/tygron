/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.editor.item;

import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.engine.item.BehaviorTerrain;
import nl.tytech.data.engine.item.BehaviorTerrain.Behavior;
import nl.tytech.data.engine.item.Stakeholder;
import nl.tytech.data.engine.item.Stakeholder.Type;
import nl.tytech.util.color.TColor;

/**
 * @author Frank Baars
 */
public class TNLBehaviorTerrainLink extends TNLTerrainLink {

    /**
     *
     */
    private static final long serialVersionUID = -3030480178840758455L;

    @XMLValue
    private BehaviorTerrain.Behavior behavior = BehaviorTerrain.Behavior.GRASSLAND;

    public Behavior getBehavior() {
        return behavior;
    }

    @Override
    public TColor getColor() {
        return BehaviorGeoLink.getColor(behavior);
    }

    @Override
    public Type getDefaultStakeholderType() {
        return Stakeholder.Type.MUNICIPALITY;
    }

    @Override
    public String getName() {
        return behavior.name();
    }

    @Override
    public boolean isRoad() {
        return false;
    }

    @Override
    public boolean isWater() {
        return behavior.isWater();
    }

    @Override
    public boolean mustContainHouses() {
        return false;
    }

}
