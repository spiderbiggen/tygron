/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.editor.event;

import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import com.vividsolutions.jts.geom.Point;

/**
 *
 * @author Frank Baars
 *
 */
public enum EditorPipeJunctionEventType implements EventTypeEnum {

    @EventIDField(links = { "PIPE_JUNCTIONS" }, params = { 0 })
    EDIT_LOCATION(Integer.class, Point.class, Double.class),

    @EventIDField(links = { "PIPE_JUNCTIONS" }, params = { 0 })
    SET_HEAD(Integer.class),

    @EventIDField(links = { "PIPE_JUNCTIONS" }, params = { 0 })
    REMOVE(Integer[].class),

    ;

    private List<Class<?>> classes;

    private EditorPipeJunctionEventType(Class<?>... classes) {
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
