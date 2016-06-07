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
import nl.tytech.data.engine.serializable.MapType;
import nl.tytech.util.color.TColor;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.MultiPolygon;

/**
 *
 * @author Frank Baars
 *
 */
public enum EditorAreaEventType implements EventTypeEnum {

    ADD(),

    ADD_WITH_ATTRIBUTE(String.class, Double.class),

    @EventIDField(links = { "AREAS" }, params = { 0 })
    REMOVE(Integer[].class),

    @EventIDField(links = { "AREAS" }, params = { 0 })
    DUPLICATE(Integer[].class),

    @EventIDField(links = { "AREAS" }, params = { 0 })
    SET_NAME(Integer.class, String.class),

    @EventIDField(links = { "AREAS" }, params = { 0 })
    SET_ACTIVE(Integer.class, Boolean.class),

    @EventIDField(links = { "AREAS" }, params = { 0 })
    SET_COLOR(Integer.class, MapType.class, TColor.class),

    @EventIDField(links = { "AREAS" }, params = { 0 })
    ADD_AREA_POLYGONS(Integer.class, MultiPolygon.class),

    @EventIDField(links = { "AREAS" }, params = { 0 })
    DELETE_AREA_POLYGONS(Integer.class, MultiPolygon.class),

    ADD_GEO_SERVICE_AREA(GeoFormat.class, String.class, String.class, String.class, String.class, String.class),

    ADD_AREA_COLLECTION(GeometryCollection.class, String[].class, String[].class, Double[].class),

    @EventIDField(links = { "AREAS" }, params = { 0 })
    SET_AREA_ATTRIBUTE(Integer.class, String.class, Double.class),

    @EventIDField(links = { "AREAS" }, params = { 0 })
    SET_AREA_ATTRIBUTES(Integer[].class, String.class, Double[].class),

    @EventIDField(links = { "AREAS" }, params = { 0 })
    SET_NAMES(Integer[].class, String[].class),

    @EventIDField(links = { "AREAS" }, params = { 0 })
    RESET_ATTRIBUTES(Integer.class, String[].class),

    ;

    private List<Class<?>> classes;

    private EditorAreaEventType(Class<?>... classes) {
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
