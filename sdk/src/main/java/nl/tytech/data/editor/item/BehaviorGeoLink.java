/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.editor.item;

import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.engine.item.BehaviorTerrain;
import nl.tytech.data.engine.item.Stakeholder;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;

/**
 * @author Jurrian Hartveldt
 */
public class BehaviorGeoLink extends GeoLink {

    private static final long serialVersionUID = 3227809616030984800L;

    private static final TColor TERRAIN_PARAM_COLOR_GRASS = new TColor(0.482, 0.604, 0.0, 1.0);
    private static final TColor TERRAIN_PARAM_COLOR_WATER = new TColor(123, 174, 212);
    private static final TColor CLIENT_ARTIST_COLOR_CONCRETE = TColor.GRAY;
    private static final TColor CLIENT_ARTIST_COLOR_BEACH = TColor.YELLOW;

    public static TColor getColor(BehaviorTerrain.Behavior behavior) {
        switch (behavior) {
            case BOEZEM_WATER:
            case POLDER_WATER:
                return TERRAIN_PARAM_COLOR_WATER;
            case BREAKWATER:
            case CONCRETE:
                return CLIENT_ARTIST_COLOR_CONCRETE;
            case BEACH:
            case DUNES:
                return CLIENT_ARTIST_COLOR_BEACH;
            case GRASSLAND:
                return TERRAIN_PARAM_COLOR_GRASS;
            default:
                return TColor.BLACK;
        }
    }

    @XMLValue
    private BehaviorTerrain.Behavior tileBehavior = null;

    public BehaviorTerrain.Behavior getBehavior() {
        return tileBehavior;
    }

    @Override
    public TColor getColor() {
        return getColor(getBehavior());
    }

    @Override
    public Stakeholder.Type getDefaultStakeholderType() {
        /**
         * Empty space is almost certainly owned by the municipality (both water and land).
         */
        return Stakeholder.Type.MUNICIPALITY;
    }

    @Override
    public String getName() {
        return StringUtils.EMPTY + getBehavior();
    }

    @Override
    public boolean isRoad() {
        return false;
    }

    @Override
    public boolean isWater() {
        return getBehavior().isWater();
    }

    public void setTileBehavior(BehaviorTerrain.Behavior tileUsageType) {
        tileBehavior = tileUsageType;
    }

    @Override
    public String toString() {
        return StringUtils.EMPTY + this.getPriority() + ") " + getName();
    }
}
