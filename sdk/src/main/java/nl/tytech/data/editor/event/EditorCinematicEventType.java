/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.editor.event;

import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;

/**
 * @author Frank Baars, Jeroen Warmerdam
 */
public enum EditorCinematicEventType implements EventTypeEnum {

    @EventIDField(links = { "STAKEHOLDERS" }, params = { 0 })
    ADD(Integer.class),

    @EventIDField(links = { "CINEMATIC_DATAS" }, params = { 0 })
    REMOVE(Integer.class),

    @EventIDField(links = { "CINEMATIC_DATAS" }, params = { 0 })
    DUPLICATE(Integer.class),

    @EventIDField(links = { "CINEMATIC_DATAS" }, params = { 0 })
    UPDATE_KEYPOINT_ORIENTATION(Integer.class, Integer.class, Float[].class, Float[].class, Float[].class),

    @EventIDField(links = { "CINEMATIC_DATAS" }, params = { 0 })
    ADD_KEYPOINT_WITH_PARAMETERS(Integer.class, Integer.class, Float[].class, Float[].class, Float[].class),

    @EventIDField(links = { "CINEMATIC_DATAS" }, params = { 0 })
    MOVE_KEYPOINT(Integer.class, Integer.class, Integer.class),

    @EventIDField(links = { "CINEMATIC_DATAS" }, params = { 0 })
    ADD_KEYPOINT(Integer.class),

    @EventIDField(links = { "CINEMATIC_DATAS" }, params = { 0 })
    ADD_KEYPOINT_ABOVE_KEYPOINT(Integer.class, Integer.class),

    @EventIDField(links = { "CINEMATIC_DATAS" }, params = { 0 })
    REMOVE_KEYPOINT(Integer.class, Integer.class),

    @EventIDField(links = { "CINEMATIC_DATAS" }, params = { 0 })
    DUPLICATE_KEYPOINT(Integer.class, Integer.class),

    @EventIDField(links = { "CINEMATIC_DATAS" }, params = { 0 })
    SET_NAME(Integer.class, String.class),

    @EventIDField(links = { "CINEMATIC_DATAS" }, params = { 0 })
    SET_KEYPOINT_DESCRIPTION(Integer.class, Integer.class, String.class),

    @EventIDField(links = { "CINEMATIC_DATAS" }, params = { 0 })
    SET_KEYPOINT_PAUSED(Integer.class, Integer.class, Boolean.class),

    @EventIDField(links = { "CINEMATIC_DATAS" }, params = { 0 })
    SET_KEYPOINT_TIME(Integer.class, Integer.class, Double.class),

    @EventIDField(links = { "CINEMATIC_DATAS", "EVENT_BUNDLES" }, params = { 0, 2 })
    ADD_KEYPOINT_EVENT_BUNDLE(Integer.class, Integer.class, Integer.class),

    @EventIDField(links = { "CINEMATIC_DATAS" }, params = { 0 })
    REMOVE_KEYPOINT_EVENT_BUNDLE(Integer.class, Integer.class, Integer.class);

    private List<Class<?>> classes;

    private EditorCinematicEventType(Class<?>... classes) {
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
