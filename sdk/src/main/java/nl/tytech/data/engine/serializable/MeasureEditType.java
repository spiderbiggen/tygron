/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.serializable;

import nl.tytech.data.engine.item.Terrain.TerrainType;

/**
 *
 * @author Frank Baars
 *
 */
public enum MeasureEditType {

    BUILDING(false, null),

    DIKE(true, TerrainType.GRASSLAND),

    FLATTEN(false, TerrainType.GRASSLAND),

    WATER(true, TerrainType.POLDER_WATER),

    UPGRADE(false, null);

    public final static MeasureEditType[] VALUES = MeasureEditType.values();

    private boolean isHeightMapEditType;
    private TerrainType terrainType;

    private MeasureEditType(boolean isHeightMapEditType, TerrainType terrainType) {
        this.isHeightMapEditType = isHeightMapEditType;
        this.terrainType = terrainType;

    }

    public TerrainType getTerrainType() {
        return terrainType;
    }

    public boolean hasTerrainType() {
        return terrainType != null;
    }

    public boolean isHeightMapEditType() {
        return this.isHeightMapEditType;
    }
}
