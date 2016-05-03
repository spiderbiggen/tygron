/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.editor.event;

import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.data.core.item.Moment.SimTime;

/**
 * Events related to setting the Simulation Time.
 * @author Maxim Knepfe
 *
 */
public enum EditorTimeEventType implements EventTypeEnum {

    SET_START_DATE(Long.class),

    SET_SIMULATION_DURATION_YEARS(Integer.class),

    SET_SESSION_DURATION_HOURS(Double.class),

    SET_NUMBER_OF_PAUSE_MOMENTS(Integer.class),

    SET_SIM_TIME_TYPE(SimTime.class);

    private List<Class<?>> classes;

    private EditorTimeEventType(Class<?>... classes) {
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
        return null;
    }

    @Override
    public boolean isServerSide() {
        return true;
    }

}
