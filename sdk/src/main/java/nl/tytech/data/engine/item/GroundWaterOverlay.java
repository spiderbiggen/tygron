/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import nl.tytech.core.item.annotations.XMLValue;

/**
 * GroundWater based on a grid
 *
 * @author Maxim Knepfle
 */
public class GroundWaterOverlay extends GridOverlay {

    /**
     *
     */
    private static final long serialVersionUID = -2572949484529750426L;

    @XMLValue
    private String waterLevelKey = "WATER_LEVEL";

    @XMLValue
    private String waterLevelIncreaseKey = "WATER_LEVEL_INCREASE";

    @XMLValue
    private String groundWaterLevelKey = "GROUND_WATER_LEVEL";

    public String getGroundWaterLevelKey() {
        return groundWaterLevelKey;
    }

    public String getWaterLevelIncreaseKey() {
        return waterLevelIncreaseKey;
    }

    public String getWaterLevelKey() {
        return waterLevelKey;
    }

    public void setGroundWaterLevelKey(String groundWaterLevelKey) {
        this.groundWaterLevelKey = groundWaterLevelKey;
    }

    public void setWaterLevelIncreaseKey(String waterLevelIncreaseKey) {
        this.waterLevelIncreaseKey = waterLevelIncreaseKey;
    }

    public void setWaterLevelKey(String waterLevelKey) {
        this.waterLevelKey = waterLevelKey;
    }
}
