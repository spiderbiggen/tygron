/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.UniqueNamedItem;
import nl.tytech.data.engine.serializable.Vector3d;

/**
 * Weather
 * <p>
 * Weather defines the type of shower or river-height.
 * </p>
 * @author Maxim Knepfle
 */
public class Weather extends UniqueNamedItem {

    public enum WeatherTypeEffect {
        NO_EFFECT(false),

        RAIN(true),

        SNOW(true),

        WIND(false),

        /**
         * Low water level
         */
        DROUGHT(false),

        /**
         * High water level, flooding areas are flooded.
         */
        FLOODING(true),

        SAFE_ZONE_FLOODING(true);

        private boolean hasLighting;

        private WeatherTypeEffect(boolean hasLighting) {
            this.hasLighting = hasLighting;
        }

        public boolean hasLighting() {
            return this.hasLighting;
        }
    }

    private static final long serialVersionUID = -578313169572826818L;

    @XMLValue
    private WeatherTypeEffect effect = WeatherTypeEffect.RAIN;

    @XMLValue
    private double rainM = 27;

    @XMLValue
    private double rainDuration = 60;

    @XMLValue
    private double floodingM = 5;

    @XMLValue
    private double rainfallIncrementFactor = 0.15f;

    @XMLValue
    private boolean allowClientAutoTrigger = false;

    @XMLValue
    private double durationInSecs = 25;

    private Vector3d windDirection = new Vector3d();

    public void decideWindDirection() {

        windDirection.x = Math.random() - 0.5d;
        windDirection.y = 0;
        windDirection.z = Math.random() - 0.5d;
    }

    @Override
    public String getDescription() {
        return getName();
    }

    public double getDurationInSecs() {
        return durationInSecs;
    }

    public double getFloodingM() {
        return floodingM;
    }

    public double getRainDuration() {
        return this.rainDuration;
    }

    public double getRainfallIncrement() {
        return this.rainfallIncrementFactor;
    }

    public double getRainM() {
        return this.rainM;
    }

    public WeatherTypeEffect getWeatherTypeEffect() {
        return effect;
    }

    public Vector3d getWindDirection() {
        return windDirection;
    }

    public double getWindSpeed() {
        return Math.random();
    }

    public boolean isAllowClientAutoTrigger() {
        return allowClientAutoTrigger;
    }

    public void setAllowClientAutoTrigger(boolean allowClientAutoTrigger) {
        this.allowClientAutoTrigger = allowClientAutoTrigger;
    }

    public void setDurationInSecs(double durationInSecs) {
        this.durationInSecs = durationInSecs;
    }

    public void setFloodingM(double floodingM) {
        this.floodingM = floodingM;
    }

    public void setRainDuration(double duration) {
        this.rainDuration = duration;
    }

    public void setRainfallIncrementFactor(double annualIncrement) {
        this.rainfallIncrementFactor = annualIncrement;
    }

    public void setRainMM(double rainMM) {
        this.rainM = rainMM;
    }

    public void setWeatherTypeEffect(WeatherTypeEffect effect) {
        this.effect = effect;
    }

    @Override
    public String toString() {
        return this.getName();
    }

}
