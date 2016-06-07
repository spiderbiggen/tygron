/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.editor.event;

import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.data.engine.serializable.Address.AddressParameter;
import nl.tytech.data.engine.serializable.Category;
import nl.tytech.data.engine.serializable.CategoryValue;
import nl.tytech.data.engine.serializable.FunctionValue;
import nl.tytech.util.color.TColor;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.MultiPolygon;

/**
 * Events related to editing bauildings.
 * @author Maxim Knepfle
 *
 */
public enum EditorBuildingEventType implements EventTypeEnum {

    @EventIDField(links = { "STAKEHOLDERS" }, params = { 0 })
    ADD_LANDMARK(Integer.class),

    @EventIDField(links = { "STAKEHOLDERS" }, params = { 0 })
    ADD_ROAD(Integer.class),

    @EventIDField(links = { "STAKEHOLDERS" }, params = { 0 })
    ADD_STANDARD(Integer.class),

    @EventIDField(links = { "STAKEHOLDERS" }, params = { 0 })
    ADD_UNDERGROUND(Integer.class),

    @EventIDField(links = { "BUILDINGS", "FUNCTIONS" }, params = { 0, 1 })
    SET_FUNCTION(Integer.class, Integer.class),

    //
    @EventIDField(links = { "BUILDINGS", "STAKEHOLDERS" }, params = { 0, 1 })
    SET_OWNER(Integer.class, Integer.class),

    @EventIDField(links = { "BUILDINGS" }, params = { 0 })
    REMOVE(Integer[].class),

    @EventIDField(links = { "BUILDINGS" }, params = { 0 })
    SET_NAME(Integer.class, String.class),

    @EventIDField(links = { "BUILDINGS" }, params = { 0 })
    SET_LANDMARK_ROTATION(Integer.class, Integer.class),

    @EventIDField(links = { "BUILDINGS" }, params = { 0 })
    SET_LANDMARK_OFFSET_X(Integer.class, Double.class),

    @EventIDField(links = { "BUILDINGS" }, params = { 0 })
    SET_LANDMARK_OFFSET_Y(Integer.class, Double.class),

    @EventIDField(links = { "BUILDINGS" }, params = { 0 })
    SET_LANDMARK_OFFSET_Z(Integer.class, Double.class),

    @EventParamData(desc = "Change amount of floors", params = { "Building ID", "Number of floors", })
    @EventIDField(links = { "BUILDINGS" }, params = { 0 })
    SET_FLOORS(Integer.class, Integer.class),

    @EventIDField(links = { "BUILDINGS" }, params = { 0 })
    SET_VACANT(Integer.class, Boolean.class),

    @EventIDField(links = { "BUILDINGS" }, params = { 0 })
    SET_ROOF_COLOR(Integer.class, TColor.class),

    @EventIDField(links = { "BUILDINGS" }, params = { 0 })
    SET_WALL_COLOR(Integer.class, TColor.class),

    @EventIDField(links = { "BUILDINGS" }, params = { 0 })
    BUILDING_ADD_POLYGONS(Integer.class, MultiPolygon.class),

    @EventIDField(links = { "BUILDINGS" }, params = { 0 })
    BUILDING_DELETE_POLYGONS(Integer.class, MultiPolygon.class),

    @EventIDField(links = { "FUNCTIONS" }, params = { 0 })
    MULTI_SELECT(Integer.class, Integer.class, Boolean.class, Integer[].class),

    SELECT_QUERY(String.class),

    UPDATE_QUERY(String.class, Double.class),

    @EventIDField(links = { "BUILDINGS" }, params = { 0 })
    SET_ADDRESS_PARAMETER(Integer.class, String.class, AddressParameter.class, String.class),

    @EventIDField(links = { "BUILDINGS" }, params = { 0 })
    SET_ADDRESS_SURFACE_SIZE_M2(Integer.class, String.class, Double.class),

    @EventIDField(links = { "BUILDINGS" }, params = { 0 })
    ADD_ADDRESS(Integer.class),

    @EventIDField(links = { "BUILDINGS" }, params = { 0 })
    DUPLICATE_ADDRESS(Integer.class, String.class),

    @EventIDField(links = { "BUILDINGS" }, params = { 0 })
    REMOVE_ADDRESS(Integer.class, String.class),

    @EventIDField(links = { "BUILDINGS" }, params = { 0 })
    IMPORT_ADDRESSES(Integer.class),

    @EventIDField(links = { "BUILDINGS" }, params = { 0 })
    SET_CONSTRUCTION_YEAR(Integer.class, Integer.class),

    @EventIDField(links = { "FUNCTIONS" }, params = { 0 })
    ADD_BUILDING_COLLECTION(Integer.class, GeometryCollection.class),

    @EventIDField(links = { "BUILDINGS" }, params = { 0 })
    SET_FUNCTION_VALUE(Integer.class, FunctionValue.class, Double.class),

    @EventIDField(links = { "BUILDINGS" }, params = { 0 })
    SET_FUNCTION_VALUES(Integer[].class, FunctionValue.class, Double[].class),

    @EventIDField(links = { "BUILDINGS" }, params = { 0 })
    SET_CATEGORY_VALUE(Integer.class, Category.class, CategoryValue.class, Double.class),

    @EventIDField(links = { "BUILDINGS" }, params = { 0 })
    RESET_FUNCTION_VALUES(Integer.class, FunctionValue[].class),

    @EventIDField(links = { "BUILDINGS" }, params = { 0 })
    RESET_CATEGORY_VALUES(Integer.class, Category.class, CategoryValue[].class),

    @EventIDField(links = { "BUILDINGS" }, params = { 0 })
    SET_NAMES(Integer[].class, String[].class),

    ;

    private List<Class<?>> classes;

    private EditorBuildingEventType(Class<?>... classes) {
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

        if (this == ADD_LANDMARK || this == ADD_ROAD || this == ADD_STANDARD) {
            return Integer.class;
        } else if (this == SELECT_QUERY) {
            return Object.class;
        } else if (this == UPDATE_QUERY) {
            return Boolean.class;
        }
        return null;
    }

    @Override
    public boolean isServerSide() {
        return true;
    }

}
