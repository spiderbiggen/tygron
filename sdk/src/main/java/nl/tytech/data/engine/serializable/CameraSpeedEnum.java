/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.serializable;

import nl.tytech.util.logger.TLogger;

/**
 * CameraSpeedEnum
 * <p>
 * This class defines speed of the camera.
 * </p>
 * @author Marijn van Zanten
 */
public enum CameraSpeedEnum {

    CUSTOM(20f, 0.15f, 1.15f), NORMAL(1.2f, 1, 1), INSTANT(Float.MAX_VALUE, 0f, 0f);

    private float interpolationTime;
    private float multiplier;
    private float deceleration;

    private CameraSpeedEnum(float interpolation, float multiplier, float deceleration) {
        this.interpolationTime = interpolation;
        this.multiplier = multiplier;
        this.deceleration = deceleration;
    }

    public float getDeceleration() {
        return this.deceleration;
    }

    public float getInterpolationTime() {
        return this.interpolationTime;
    }

    public float getMultiplier() {
        return this.multiplier;
    }

    public void setInterpolationTime(float interpolationTime) {
        if (this == CUSTOM) {
            this.interpolationTime = interpolationTime;
        } else {
            TLogger.warning("You can only override the custom cam speed enum!");
        }
    }
}
