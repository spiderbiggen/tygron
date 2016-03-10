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
 *
 * @author Frank Baars
 *
 */
public enum EditorDikeEventType implements EventTypeEnum {

    @EventIDField(links = { "DIKES", "FUNCTIONS" }, params = { 0, 1 })
    SET_TOP_FUNCTION_ID(Integer.class, Integer.class),

    @EventIDField(links = { "DIKES", "FUNCTIONS" }, params = { 0, 1 })
    SET_SIDE_FUNCTION_ID(Integer.class, Integer.class),

    @EventIDField(links = { "DIKES" }, params = { 0 })
    RESET_SIDE_FUNCTION_ID(Integer.class),

    @EventIDField(links = { "DIKES" }, params = { 0 })
    RESET_TOP_FUNCTION_ID(Integer.class),

    @EventIDField(links = { "DIKES" }, params = { 0 })
    SET_NAME(Integer.class, String.class),

    @EventIDField(links = { "DIKES" }, params = { 0 })
    SET_DEFAULT_HEIGHT(Integer.class, Double.class),

    @EventIDField(links = { "DIKES" }, params = { 0 })
    SET_DEFAULT_WIDTH(Integer.class, Double.class),

    @EventIDField(links = { "DIKES" }, params = { 0 })
    SET_DESCRIPTION(Integer.class, String.class),

    ADD(),

    @EventIDField(links = { "DIKES" }, params = { 0 })
    REMOVE(Integer[].class),

    @EventIDField(links = { "DIKES" }, params = { 0 })
    DUPLICATE(Integer[].class),

    @EventIDField(links = { "DIKES" }, params = { 0 })
    SET_IMAGE(Integer.class, String.class),

    @EventIDField(links = { "DIKES" }, params = { 0 })
    SET_IS_FIXED_SIZE(Integer.class, Boolean.class), ;

    private List<Class<?>> classes;

    private EditorDikeEventType(Class<?>... classes) {
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
