/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.editor.event;

import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.data.engine.item.Weather.WeatherTypeEffect;

/**
 * Edit weather in Simulation
 * @author Maxim Knepfle
 *
 */
public enum EditorWeatherEventType implements EventTypeEnum {

    ADD(WeatherTypeEffect.class),

    @EventIDField(links = { "WEATHERS" }, params = { 0 })
    REMOVE(Integer[].class),

    @EventIDField(links = { "WEATHERS" }, params = { 0 })
    DUPLICATE(Integer[].class),

    @EventIDField(links = { "WEATHERS" }, params = { 0 })
    SET_WEATHER_TYPE_EFFECT(Integer.class, WeatherTypeEffect.class),

    @EventIDField(links = { "WEATHERS" }, params = { 0 })
    SET_NAME(Integer.class, String.class),

    @EventIDField(links = { "WEATHERS" }, params = { 0 })
    SET_RAIN_M(Integer.class, Double.class),

    @EventIDField(links = { "WEATHERS" }, params = { 0 })
    SET_RAIN_MINUTE(Integer.class, Double.class),

    @EventIDField(links = { "WEATHERS" }, params = { 0 })
    SET_RAIN_ANNUAL_INCREMENT(Integer.class, Double.class),

    @EventIDField(links = { "WEATHERS" }, params = { 0 })
    SET_AUTO_TRIGGER(Integer.class, Boolean.class),

    @EventIDField(links = { "WEATHERS" }, params = { 0 })
    SET_FLOODING_M(Integer.class, Double.class),

    @EventIDField(links = { "WEATHERS" }, params = { 0 })
    SET_DURATION(Integer.class, Double.class), ;

    private List<Class<?>> classes;

    private EditorWeatherEventType(Class<?>... classes) {
        this.classes = Arrays.asList(classes);
    }

    @Override
    public boolean canBePredefined() {
        return false;
    }

    @Override
    public List<Class<?>> getClasses() {
        return classes;
    }

    @Override
    public Class<?> getResponseClass() {
        return this == ADD ? Integer.class : null;
    }

    @Override
    public boolean isServerSide() {
        return true;
    }

}
