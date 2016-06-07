/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.editor.event;

import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.data.engine.serializable.Category;
import nl.tytech.util.color.TColor;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.MultiPolygon;

/**
 * Edit Zones
 * @author Maxim
 *
 */
public enum EditorZoningEventType implements EventTypeEnum {

    ADD(),

    @EventIDField(links = { "ZONES" }, params = { 0 })
    REMOVE(Integer[].class),

    @EventIDField(links = { "ZONES" }, params = { 0 })
    SET_NAME(Integer.class, String.class),

    @EventIDField(links = { "ZONES" }, params = { 0 })
    SET_COLOR(Integer.class, TColor.class),

    @EventIDField(links = { "LEVELS", "ZONES" }, params = { 0, 1 })
    SET_PLAYABLE(Integer.class, Integer.class, Boolean.class),

    @EventIDField(links = { "ZONES" }, params = { 0 })
    SET_DESCRIPTION(Integer.class, String.class),

    @EventIDField(links = { "ZONES" }, params = { 0 })
    ADD_FUNCTION_CATEGORY(Integer.class, Category.class),

    @EventIDField(links = { "ZONES" }, params = { 0 })
    REMOVE_FUNCTION_CATEGORY(Integer.class, Category.class),

    @EventIDField(links = { "ZONES" }, params = { 0 })
    SET_MAX_ALLOWED_FLOORS(Integer.class, Integer.class),

    @EventIDField(links = { "ZONES" }, params = { 0 })
    ZONE_UPDATE_POLYGONS(Integer.class, MultiPolygon.class),

    @EventIDField(links = { "ZONES" }, params = { 0 })
    ZONE_DELETE_POLYGONS(Integer.class, MultiPolygon.class),

    ADD_ZONE_COLLECTION(GeometryCollection.class);

    private List<Class<?>> classes;

    private EditorZoningEventType(Class<?>... classes) {
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
