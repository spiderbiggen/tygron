/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.editor.event;

import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.data.core.item.CodedEvent;

/**
 * @author Jeroen Warmerdam
 *
 */
public enum EditorLevelEventType implements EventTypeEnum {

    ADD,

    @EventIDField(links = { "LEVELS", "MEASURES" }, params = { 0, 1 })
    ADD_ACTIVATED_MEASURE(Integer.class, Integer.class),

    @EventIDField(links = { "LEVELS", "INDICATORS" }, params = { 0, 1 })
    ADD_ACTIVE_INDICATOR(Integer.class, Integer.class),

    @EventIDField(links = { "LEVELS", "MESSAGES" }, params = { 0, 1 })
    ADD_MESSAGE(Integer.class, Integer.class),

    @EventIDField(links = { "LEVELS", "MEASURES" }, params = { 0, 1 })
    REMOVE_ACTIVATED_MEASURE(Integer.class, Integer.class),

    @EventIDField(links = { "LEVELS", "INDICATORS" }, params = { 0, 1 })
    REMOVE_ACTIVE_INDICATOR(Integer.class, Integer.class),

    @EventIDField(links = { "LEVELS" }, params = { 0 })
    REMOVE(Integer[].class),

    @EventIDField(links = { "LEVELS", "MESSAGES" }, params = { 0, 1 })
    REMOVE_MESSAGE(Integer.class, Integer.class),

    @EventIDField(links = { "LEVELS" }, params = { 0 })
    UPDATE_DESCRIPTION(Integer.class, String.class),

    @EventIDField(links = { "LEVELS" }, params = { 0 })
    UPDATE_NAME(Integer.class, String.class),

    @EventIDField(links = { "LEVELS", "STAKEHOLDERS", "CINEMATIC_DATAS" }, params = { 0, 1, 2 })
    UPDATE_STARTING_CINEMATIC(Integer.class, Integer.class, Integer.class),

    @EventIDField(links = { "LEVELS", "STAKEHOLDERS" }, params = { 0, 1 })
    REMOVE_STARTING_CINEMATIC(Integer.class, Integer.class),

    @EventIDField(links = { "LEVELS" }, params = { 0 })
    ADD_EVENT(Integer.class),

    @EventIDField(links = { "LEVELS" }, params = { 0 })
    REMOVE_EVENT(Integer.class, Integer.class),

    @EventIDField(links = { "LEVELS" }, params = { 0 })
    UPDATE_EVENT(Integer.class, CodedEvent.class),

    @EventIDField(links = { "LEVELS" }, params = { 0 })
    MOVE_BACKWARD(Integer.class),

    @EventIDField(links = { "LEVELS" }, params = { 0 })
    MOVE_FORWARD(Integer.class);

    private List<Class<?>> classes;

    private EditorLevelEventType(Class<?>... classes) {

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
