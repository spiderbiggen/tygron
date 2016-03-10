/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.editor.event;

import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.net.serializable.MapLink;

/**
 * EditorGUICategoryEventType
 *
 * @author Maxim Knepfle
 */
public enum EditorActionMenuEventType implements EventTypeEnum {

    @EventIDField(links = { "STAKEHOLDERS" }, params = { 0 })
    ADD(Integer.class),

    @EventIDField(links = { "ACTION_MENUS" }, params = { 0 })
    REMOVE(Integer[].class),

    @EventIDField(links = { "ACTION_MENUS" }, params = { 0 })
    DUPLICATE(Integer[].class),

    @EventIDField(links = { "ACTION_MENUS", "ACTION_MENUS" }, params = { 0, 1 })
    SWAP_ORDER(Integer.class, Integer.class),

    @EventIDField(links = { "ACTION_MENUS" }, params = { 0 })
    SET_ICON(Integer.class, String.class),

    @EventIDField(links = { "ACTION_MENUS" }, params = { 0 })
    SET_NAME(Integer.class, String.class),

    @EventIDField(links = { "ACTION_MENUS" }, params = { 0 })
    ADD_ACTION(Integer.class, MapLink.class, Integer.class),

    @EventIDField(links = { "ACTION_MENUS" }, params = { 0 })
    REMOVE_ACTION(Integer.class, MapLink.class, Integer.class),

    @EventIDField(links = { "ACTION_MENUS", "STAKEHOLDERS", "LEVELS" }, params = { 0, 1, 2 })
    SET_ACTIVE_FOR_STAKEHOLDER_AND_LEVEL(Integer.class, Integer.class, Integer.class, Boolean.class);

    private List<Class<?>> classes;

    private EditorActionMenuEventType(Class<?>... classes) {
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
