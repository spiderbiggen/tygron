/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.editor.event;

import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.util.color.TColor;

/**
 *
 * @author Frank Baars
 *
 */
public enum EditorUnitDataOverrideEventType implements EventTypeEnum {

    @EventIDField(links = { "UNIT_DATAS" }, params = { 0 })
    ADD(Integer.class),

    @EventIDField(links = { "UNIT_DATA_OVERRIDES" }, params = { 0 })
    DUPLICATE(Integer[].class),

    @EventIDField(links = { "UNIT_DATA_OVERRIDES" }, params = { 0 })
    SET_NAME(Integer.class, String.class),

    @EventIDField(links = { "UNIT_DATA_OVERRIDES" }, params = { 0 })
    SET_ACTIVE(Integer.class, Boolean.class),

    @EventIDField(links = { "UNIT_DATA_OVERRIDES" }, params = { 0 })
    ADD_COLOR(Integer.class),

    @EventIDField(links = { "UNIT_DATA_OVERRIDES" }, params = { 0 })
    DUPLICATE_COLOR(Integer.class, TColor.class),

    @EventIDField(links = { "UNIT_DATA_OVERRIDES" }, params = { 0 })
    CHANGE_COLOR(Integer.class, Integer.class, TColor.class),

    @EventIDField(links = { "UNIT_DATA_OVERRIDES" }, params = { 0 })
    REMOVE_COLOR(Integer.class, Integer.class, TColor.class);

    private List<Class<?>> classes;

    private EditorUnitDataOverrideEventType(Class<?>... classes) {
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
