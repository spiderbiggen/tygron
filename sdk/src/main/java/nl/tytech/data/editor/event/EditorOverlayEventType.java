/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.editor.event;

import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.data.editor.serializable.GeoFormat;
import nl.tytech.data.engine.item.NO2Overlay.GasType;
import nl.tytech.data.engine.item.Overlay.OverlayType;
import nl.tytech.data.engine.item.PipeLoad.LoadParameterType;
import nl.tytech.data.engine.item.UnitData.TrafficType;
import nl.tytech.util.color.TColor;
import com.vividsolutions.jts.geom.MultiPolygon;

/**
 *
 * @author Frank Baars
 *
 */
public enum EditorOverlayEventType implements EventTypeEnum {

    ADD(OverlayType.class),

    @EventIDField(links = { "OVERLAYS" }, params = { 0 })
    DUPLICATE(Integer[].class),

    @EventIDField(links = { "OVERLAYS" }, params = { 0 })
    CHANGE_SORT_ORDER(Integer.class, Integer.class),

    @EventIDField(links = { "OVERLAYS" }, params = { 0 })
    REMOVE(Integer[].class),

    @EventIDField(links = { "OVERLAYS" }, params = { 0 })
    SET_NAME(Integer.class, String.class),

    @EventIDField(links = { "OVERLAYS", "FUNCTIONS" }, params = { 0, 1 })
    REMOVE_OVERLAY_FUNCTION(Integer.class, Integer.class),

    @EventIDField(links = { "OVERLAYS", "FUNCTIONS" }, params = { 0, 1 })
    ADD_OVERLAY_FUNCTION(Integer.class, Integer.class),

    @EventIDField(links = { "OVERLAYS", "AREAS" }, params = { 0, 1 })
    CHANGE_OVERLAY_AREA(Integer.class, Integer[].class, Boolean.class),

    @EventIDField(links = { "OVERLAYS" }, params = { 0 })
    SET_OVERLAY_FUNCTION_COLOR(Integer.class, TColor.class),

    @EventIDField(links = { "OVERLAYS" }, params = { 0 })
    SET_OVERLAY_REST_COLOR(Integer.class, TColor.class),

    @EventIDField(links = { "OVERLAYS" }, params = { 0 })
    ADD_CUSTOM_OVERLAY_COLOR_AREA(Integer.class),

    @EventIDField(links = { "OVERLAYS" }, params = { 0 })
    REMOVE_CUSTOM_OVERLAY_COLOR_AREA(Integer.class, Integer.class),

    @EventIDField(links = { "OVERLAYS" }, params = { 0 })
    SET_CUSTOM_OVERLAY_COLOR_AREA_COLOR(Integer.class, Integer.class, TColor.class),

    @EventIDField(links = { "OVERLAYS" }, params = { 0 })
    SET_CUSTOM_OVERLAY_COLOR_AREA_NAME(Integer.class, Integer.class, String.class),

    @EventIDField(links = { "OVERLAYS" }, params = { 0 })
    ADD_CUSTOM_OVERLAY_COLOR_AREA_COORDINATES(Integer.class, Integer.class, MultiPolygon.class),

    @EventIDField(links = { "OVERLAYS" }, params = { 0 })
    REMOVE_CUSTOM_OVERLAY_COLOR_AREA_COORDINATES(Integer.class, Integer.class, MultiPolygon.class),

    ADD_SERVICE_IMAGE_OVERLAY(GeoFormat.class, String.class, String.class, String.class, String.class, Integer.class),

    @EventIDField(links = { "OVERLAYS" }, params = { 0 })
    SET_IMAGE(Integer.class, String.class),

    @EventIDField(links = { "OVERLAYS" }, params = { 0 })
    SET_PIPE_CLUSTER_OVERLAY_PARAMETER(Integer.class, LoadParameterType.class),

    @EventIDField(links = { "OVERLAYS" }, params = { 0 })
    SET_PIPE_CLUSTER_OVERLAY_SCALING_SEGMENT(Integer.class, Integer.class, Double.class, Double.class, Double.class, Double.class),

    @EventIDField(links = { "OVERLAYS" }, params = { 0 })
    ADD_PIPE_CLUSTER_OVERLAY_SCALING_SEGMENT(Integer.class),

    @EventIDField(links = { "OVERLAYS" }, params = { 0 })
    REMOVE_PIPE_CLUSTER_OVERLAY_SCALING_SEGMENT(Integer.class, Integer.class),

    @EventIDField(links = { "OVERLAYS" }, params = { 0 })
    SET_TRAFFIC_EMISSION_VALUE(Integer.class, TrafficType.class, GasType.class, Boolean.class, Double.class),

    @EventIDField(links = { "OVERLAYS" }, params = { 0 })
    SET_SUBSIDENCE_VALUES(Integer.class, String.class, Double.class, Double.class, Double.class, Double.class),

    @EventIDField(links = { "OVERLAYS" }, params = { 0 })
    SET_GROUND_WATER_VALUES(Integer.class, String.class, String.class, String.class),

    @EventIDField(links = { "OVERLAYS" }, params = { 0 })
    SET_BASE_O3(Integer.class, Double.class),

    REFRESH_GRID()

    ;

    private List<Class<?>> classes;

    private EditorOverlayEventType(Class<?>... classes) {
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
