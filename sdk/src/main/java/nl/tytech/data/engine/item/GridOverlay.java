/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.engine.serializable.MapType;

/**
 * Overlays based on calculated grid data
 *
 * @author Maxim Knepfle
 */
public class GridOverlay extends Overlay {

    public final static double MAX_DISTANCE_ZONE_M = 200;

    public final static int MIN_GRID_CELLS = 10;

    public final static int DEFAULT_GRID_CELLS = 150;

    public final static int MAX_GRID_CELLS = 250;

    /**
     *
     */
    private static final long serialVersionUID = 3137271350161025072L;

    private final static byte[] copy(byte[] input) {
        byte[] output = new byte[input.length];
        System.arraycopy(input, 0, output, 0, input.length);
        return output;
    }

    /**
     * No use storing them to XML, these get calculated on the fly
     */
    private byte[] currentData = new byte[0];
    private byte[] maquetteData = new byte[0];

    public GridOverlay() {

    }

    public double getDiagramMultiplier() {
        return 1d;
    }

    public byte[] getGridData(MapType mapType) {
        return getGridData(mapType, false);
    }

    public byte[] getGridData(MapType mapType, boolean copy) {

        Setting mapSetting = this.getItem(MapLink.SETTINGS, Setting.Type.MAP_WIDTH_METERS);
        Setting cellSetting = this.getItem(MapLink.SETTINGS, Setting.Type.GRID_CELL_SIZE_M);
        int gridSize = (int) (mapSetting.getDoubleValue() / cellSetting.getDoubleValue());
        byte[] data = (mapType == MapType.CURRENT || maquetteData.length == 0) ? currentData : maquetteData;

        // check if size is correct
        if (data.length != gridSize * gridSize) {
            data = new byte[gridSize * gridSize];
            // do not update maquette
            if (mapType == MapType.CURRENT || maquetteData.length > 0) {
                this.setGridData(mapType, data);
            }
        }
        return copy ? copy(data) : data;
    }

    public byte getGridDataValue(MapType mapType, int gridCellID) {

        byte[] data = this.getGridData(mapType);
        if (gridCellID < 0 || gridCellID >= data.length) {
            return 0;
        }
        return data[gridCellID];
    }

    public void setGridData(MapType mapType, byte[] newdata) {

        if (mapType == MapType.CURRENT) {
            currentData = copy(newdata);
        } else {
            maquetteData = copy(newdata);
        }
    }
}
