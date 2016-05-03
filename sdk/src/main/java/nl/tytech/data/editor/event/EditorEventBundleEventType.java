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
 * @author Frank Baars
 */
public enum EditorEventBundleEventType implements EventTypeEnum {

    ADD,

    @EventIDField(links = { "EVENT_BUNDLES" }, params = { 0 })
    REMOVE(Integer[].class),

    @EventIDField(links = { "EVENT_BUNDLES" }, params = { 0 })
    DUPLICATE(Integer[].class),

    @EventIDField(links = { "EVENT_BUNDLES" }, params = { 0 })
    SET_NAME(Integer.class, String.class),

    @EventIDField(links = { "EVENT_BUNDLES" }, params = { 0 })
    SET_DESCRIPTION(Integer.class, String.class),

    @EventIDField(links = { "EVENT_BUNDLES" }, params = { 0 })
    SET_IMAGE(Integer.class, String.class),

    @EventIDField(links = { "EVENT_BUNDLES", "EVENT_BUNDLES" }, params = { 0, 1 })
    ADD_COMPOUND_EVENT_BUNDLE(Integer.class, Integer.class),

    @EventIDField(links = { "EVENT_BUNDLES", "EVENT_BUNDLES" }, params = { 0, 1 })
    REMOVE_COMPOUND_EVENT_BUNDLE(Integer.class, Integer.class),

    @EventIDField(links = { "EVENT_BUNDLES" }, params = { 0 })
    ADD_EVENT(Integer.class, Boolean.class),

    @EventIDField(links = { "EVENT_BUNDLES" }, params = { 0 })
    EDIT_EVENT(Integer.class, Boolean.class, CodedEvent.class),

    @EventIDField(links = { "EVENT_BUNDLES" }, params = { 0 })
    REMOVE_EVENT(Integer.class, Boolean.class, Integer.class),

    ;

    private List<Class<?>> classes;

    private EditorEventBundleEventType(Class<?>... classes) {
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
