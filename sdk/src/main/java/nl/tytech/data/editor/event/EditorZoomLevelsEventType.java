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
public enum EditorZoomLevelsEventType implements EventTypeEnum {

    @EventIDField(links = { "ZOOMLEVELS" }, params = { 0 })
    SET_ACTIVE(Integer.class, Boolean.class),

    @EventIDField(links = { "ZOOMLEVELS" }, params = { 0 })
    SET_START(Integer.class);

    private List<Class<?>> classes;

    private EditorZoomLevelsEventType(Class<?>... classes) {
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
