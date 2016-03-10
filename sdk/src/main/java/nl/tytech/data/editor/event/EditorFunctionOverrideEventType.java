/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.editor.event;

import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.data.engine.serializable.Category;
import nl.tytech.data.engine.serializable.CategoryValue;
import nl.tytech.data.engine.serializable.FunctionValue;
import nl.tytech.util.color.TColor;

/**
 *
 * @author Frank Baars
 *
 */
public enum EditorFunctionOverrideEventType implements EventTypeEnum {

    @EventIDField(links = { "FUNCTIONS" }, params = { 0 })
    ADD(Integer.class),

    @EventIDField(links = { "FUNCTION_OVERRIDES" }, params = { 0 })
    REMOVE_INDICATOR_SCORES(Integer.class),

    @EventIDField(links = { "FUNCTION_OVERRIDES" }, params = { 0 })
    REMOVE_FUNCTION_VALUES(Integer.class),

    @EventIDField(links = { "FUNCTION_OVERRIDES" }, params = { 0 })
    REMOVE_FUNCTION_ASSETS(Integer.class),

    @EventIDField(links = { "FUNCTION_OVERRIDES" }, params = { 0 })
    RESET_FUNCTION_OVERRIDE(Integer.class),

    @EventIDField(links = { "FUNCTION_OVERRIDES", "INDICATORS" }, params = { 0, 1 })
    SET_INDICATOR_VALUE(Integer.class, Integer.class, Double.class),

    @EventIDField(links = { "FUNCTION_OVERRIDES" }, params = { 0 })
    SET_FUNCTION_VALUE(Integer.class, FunctionValue.class, Double.class),

    @EventIDField(links = { "FUNCTION_OVERRIDES" }, params = { 0 })
    SET_CATEGORY_VALUE(Integer.class, Category.class, CategoryValue.class, Double.class),

    @EventIDField(links = { "FUNCTION_OVERRIDES", "INDICATORS" }, params = { 0, 1 })
    SET_MAX_VALUE(Integer.class, Integer.class, Double.class),

    @EventIDField(links = { "FUNCTION_OVERRIDES" }, params = { 0 })
    SET_FUNCTION_NAME(Integer.class, String.class),

    @EventIDField(links = { "FUNCTION_OVERRIDES" }, params = { 0 })
    SET_FUNCTION_DESCRIPTION(Integer.class, String.class),

    @EventIDField(links = { "FUNCTION_OVERRIDES" }, params = { 0 })
    SET_FUNCTION_IMAGE(Integer.class, String.class),

    @EventIDField(links = { "FUNCTION_OVERRIDES" }, params = { 0 })
    SET_FUNCTION_ROOF_COLOR(Integer.class, TColor.class),

    @EventIDField(links = { "FUNCTION_OVERRIDES" }, params = { 0 })
    SET_FUNCTION_WALL_COLOR(Integer.class, TColor.class),

    @EventIDField(links = { "FUNCTIONS" }, params = { 0 })
    DUPLICATE(Integer.class),

    @EventIDField(links = { "FUNCTION_OVERRIDES" }, params = { 0 })
    DELETE_CATEGORY(Integer.class, Category.class),

    @EventIDField(links = { "FUNCTION_OVERRIDES" }, params = { 0 })
    ADD_NEW_CATEGORY(Integer.class, Category.class),

    ;

    private List<Class<?>> classes;

    private EditorFunctionOverrideEventType(Class<?>... classes) {
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
        if (this == ADD) {
            return Integer.class;
        }
        return null;
    }

    @Override
    public boolean isServerSide() {
        return true;
    }

}
