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
import nl.tytech.data.engine.item.Measure.ActionType;
import nl.tytech.data.engine.item.Measure.CostType;
import nl.tytech.data.engine.serializable.MeasureEditType;
import com.vividsolutions.jts.geom.MultiPolygon;

/**
 * @author Jeroen Warmerdam
 * 
 */
public enum EditorMeasureEventType implements EventTypeEnum {

    @EventIDField(links = { "STAKEHOLDERS" }, params = { 0 })
    ADD(Integer.class),

    @EventIDField(links = { "MEASURES" }, params = { 0 })
    DUPLICATE(Integer[].class),

    @EventIDField(links = { "BUILDINGS" }, params = { 0 })
    DUPLICATE_BUILDINGS(Integer[].class),

    @EventIDField(links = { "MEASURES" }, params = { 0 })
    REMOVE(Integer.class),

    @EventIDField(links = { "MEASURES" }, params = { 0 })
    ADD_BUILDING(Integer.class),

    @EventIDField(links = { "MEASURES" }, params = { 0 })
    ADD_LANDMARK(Integer.class),

    @EventIDField(links = { "MEASURES" }, params = { 0 })
    ADD_UPGRADE(Integer.class),

    @EventIDField(links = { "MEASURES" }, params = { 0 })
    ADD_SPATIAL(Integer.class),

    @EventIDField(links = { "MEASURES", "INDICATORS" }, params = { 0, 1 })
    ADD_INDICATOR_SCORE(Integer.class, Integer.class),

    @EventIDField(links = { "MEASURES" }, params = { 0 })
    SPATIAL_ADD_POLYGONS(Integer.class, Integer.class, Boolean.class, MultiPolygon.class),

    @EventIDField(links = { "MEASURES" }, params = { 0 })
    SPATIAL_REMOVE_POLYGONS(Integer.class, Integer.class, Boolean.class, MultiPolygon.class),

    @EventIDField(links = { "MEASURES" }, params = { 0 })
    UPGRADE_ADD_POLYGONS(Integer.class, Integer.class, MultiPolygon.class),

    @EventIDField(links = { "MEASURES" }, params = { 0 })
    UPGRADE_REMOVE_POLYGONS(Integer.class, Integer.class, MultiPolygon.class),

    @EventIDField(links = { "MEASURES" }, params = { 0 })
    REMOVE_SPATIAL(Integer.class, Integer.class),

    @EventIDField(links = { "MEASURES" }, params = { 0 })
    REMOVE_UPGRADE(Integer.class, Integer.class),

    @EventIDField(links = { "MEASURES", "BUILDINGS" }, params = { 0, 1 })
    REMOVE_BUILDING(Integer.class, Integer.class),

    @EventIDField(links = { "MEASURES", "INDICATORS" }, params = { 0, 1 })
    REMOVE_INDICATOR_SCORE(Integer.class, Integer.class),

    @EventIDField(links = { "MEASURES" }, params = { 0 })
    SPATIAL_SET_HEIGHT(Integer.class, Integer.class, Double.class),

    @EventIDField(links = { "MEASURES" }, params = { 0 })
    SPATIAL_SET_TYPE(Integer.class, Integer.class, MeasureEditType.class),

    @EventIDField(links = { "MEASURES" }, params = { 0 })
    EVENT_ADD(Integer.class, Boolean.class, ActionType.class),

    @EventIDField(links = { "MEASURES" }, params = { 0 })
    EVENT_REMOVE(Integer.class, Boolean.class, ActionType.class, Integer.class),

    @EventIDField(links = { "MEASURES" }, params = { 0 })
    EVENT_UPDATE(Integer.class, Boolean.class, ActionType.class, CodedEvent.class),

    @EventIDField(links = { "MEASURES" }, params = { 0 })
    SET_CONSTRUCTION_TIME(Integer.class, Double.class),

    @EventIDField(links = { "MEASURES" }, params = { 0 })
    SET_COST(Integer.class, CostType.class, Double.class, Boolean.class),

    @EventIDField(links = { "MEASURES" }, params = { 0 })
    SET_DEMOLITION_TIME(Integer.class, Double.class),

    @EventIDField(links = { "MEASURES" }, params = { 0 })
    SET_DESCRIPTION(Integer.class, String.class),

    @EventIDField(links = { "MEASURES" }, params = { 0 })
    SET_IMAGE(Integer.class, String.class),

    @EventIDField(links = { "MEASURES", "INDICATORS" }, params = { 0, 1 })
    SET_INDICATOR_SCORE(Integer.class, Integer.class, Double.class),

    @EventIDField(links = { "MEASURES", "STAKEHOLDERS" }, params = { 0, 1 })
    SET_MAINTAINER(Integer.class, Integer.class),

    @EventIDField(links = { "MEASURES" }, params = { 0 })
    SET_NAME(Integer.class, String.class),

    @EventIDField(links = { "MEASURES", "STAKEHOLDERS" }, params = { 0, 1 })
    SET_OWNER(Integer.class, Integer.class),

    @EventIDField(links = { "MEASURES" }, params = { 0 })
    SET_REQUIRES_CONFIRMATION(Integer.class, Boolean.class),

    @EventIDField(links = { "MEASURES" }, params = { 0 })
    SET_WATER_INNOVATIVE(Integer.class, Boolean.class),

    @EventIDField(links = { "MEASURES" }, params = { 0 })
    SET_FIXED_WATER_STORAGE(Integer.class, Double.class),

    @EventIDField(links = { "MEASURES", "UPGRADE_TYPES" }, params = { 0, 2 })
    SET_UPGRADE_TYPE(Integer.class, Integer.class, Integer.class),

    @EventIDField(links = { "MEASURES" }, params = { 0 })
    SET_CONSTRUCTION_START_DATE(Integer.class, Long.class);

    private List<Class<?>> classes;

    private EditorMeasureEventType(Class<?>... classes) {
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
