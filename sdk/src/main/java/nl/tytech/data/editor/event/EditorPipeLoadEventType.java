/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.editor.event;

import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.data.engine.item.PipeLoad.LoadType;
import nl.tytech.data.engine.serializable.Category;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;

/**
 *
 * @author Frank Baars
 *
 */
public enum EditorPipeLoadEventType implements EventTypeEnum {

    @EventIDField(links = { "PIPE_LOADS" }, params = { 0 })
    SET_CONNECT_COST(Integer.class, Double.class),

    @EventIDField(links = { "PIPE_LOADS" }, params = { 0 })
    SET_CONNECTION_COUNT(Integer.class, Integer.class),

    @EventIDField(links = { "PIPE_LOADS" }, params = { 0 })
    SET_JUNCTION(Integer.class, Point.class),

    @EventIDField(links = { "PIPE_LOADS" }, params = { 0 })
    SET_FLOW(Integer.class, Double.class),

    @EventIDField(links = { "PIPE_LOADS" }, params = { 0 })
    SET_POWER(Integer.class, Double.class),

    @EventIDField(links = { "PIPE_LOADS" }, params = { 0 })
    SET_LOAD_TYPE(Integer.class, LoadType.class),

    @EventIDField(links = { "PIPE_LOADS" }, params = { 0 })
    REMOVE(Integer[].class),

    @EventIDField(links = { "PIPE_LOADS" }, params = { 0 })
    ADD_ADDRESS(Integer.class, String.class),

    @EventIDField(links = { "PIPE_LOADS" }, params = { 0 })
    REMOVE_ADDRESS(Integer.class, String.class),

    CLEAR_OVERRIDE_VALUES,

    AUTO_GENERATE_LOADS(MultiPolygon.class, Category[].class, Integer.class, Integer.class, Integer[].class, Boolean.class, Boolean.class,
            Double.class, Double.class);

    private List<Class<?>> classes;

    private EditorPipeLoadEventType(Class<?>... classes) {
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
