/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.event;

import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;

/**
 *
 * @author Jeroen Warmerdam
 *
 */
public enum SoundEventType implements EventTypeEnum {
    //
    @EventParamData(editor = true, desc = "Start a particular sound for a particular Stakeholder", params = { "Start for Stakeholder",
            "Sound" })
    @EventIDField(links = { "STAKEHOLDERS", "SOUNDS" }, params = { 0, 1 })
    START(Integer.class, Integer.class),

    //
    @EventParamData(editor = true, desc = "Stop a particular sound for a particular Stakeholder", params = { "Stop for Stakeholder",
            "Sound" })
    @EventIDField(links = { "STAKEHOLDERS", "SOUNDS" }, params = { 0, 1 })
    STOP(Integer.class, Integer.class),

    @EventIDField(links = { "SOUNDS" }, params = { 0 })
    STOPPED(Integer.class),

    @EventParamData(editor = true, desc = "Stop all sounds for a particular Stakeholder", params = { "Stop for Stakeholder",
            "Stop background music" })
    @EventIDField(links = { "STAKEHOLDERS" }, params = { 0 })
    STOP_ALL(Integer.class, Boolean.class),

    @EventIDField(links = { "SOUNDS" }, params = { 0 })
    STARTED(Integer.class, Double.class),

    @EventParamData(editor = true, desc = "Pause a particular sound", params = { "Sound" })
    @EventIDField(links = { "SOUNDS" }, params = { 0 })
    PAUSE(Integer.class),

    @EventParamData(editor = true, desc = "Unpause a particular sound", params = { "Sound" })
    @EventIDField(links = { "SOUNDS" }, params = { 0 })
    UNPAUSE(Integer.class);

    private final List<Class<?>> classes = new ArrayList<Class<?>>();

    private SoundEventType(Class<?>... c) {

        for (Class<?> classz : c) {
            classes.add(classz);
        }
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
        return false;
    }
}
