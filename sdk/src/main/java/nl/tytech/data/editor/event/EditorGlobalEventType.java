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
 * @author Maxim Knepfle
 *
 */
public enum EditorGlobalEventType implements EventTypeEnum {

    ADD(),

    @EventIDField(links = { "GLOBALS" }, params = { 0 })
    DUPLICATE(Integer[].class),

    @EventIDField(links = { "GLOBALS" }, params = { 0 })
    REMOVE(Integer[].class),

    @EventIDField(links = { "GLOBALS" }, params = { 0 })
    REMOVE_POINT(Integer.class),

    @EventIDField(links = { "GLOBALS" }, params = { 0 })
    SET_NAME(Integer.class, String.class),

    @EventIDField(links = { "GLOBALS" }, params = { 0 })
    SET_VISUALISATION_NAME(Integer.class, String.class),

    @EventIDField(links = { "GLOBALS" }, params = { 0 })
    SET_POINT(Integer.class, Point.class),

    @EventIDField(links = { "GLOBALS" }, params = { 0 })
    SET_START_VALUE(Integer.class, Double.class),

    @EventIDField(links = { "GLOBALS", "STAKEHOLDERS" }, params = { 0, 1 })
    SET_BOOK_VALUE(Integer.class, Integer.class),

    @EventIDField(links = { "GLOBALS" }, params = { 0 })
    REMOVE_BOOK_VALUE(Integer.class),

    ;

    private List<Class<?>> classes;

    private EditorGlobalEventType(Class<?>... classes) {
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
