/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.editor.event;

import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.data.editor.serializable.AutoClusterType;
import com.vividsolutions.jts.geom.MultiPolygon;

/**
 *
 * @author Frank Baars
 *
 */
public enum EditorPipeClusterEventType implements EventTypeEnum {

    ADD,

    @EventIDField(links = { "PIPE_CLUSTERS" }, params = { 0 })
    SET_NAME(Integer.class, String.class),

    @EventIDField(links = { "PIPE_CLUSTERS", "STAKEHOLDERS" }, params = { 0, 1 })
    SET_OWNER_ID(Integer.class, Integer.class),

    @EventIDField(links = { "PIPE_CLUSTERS" }, params = { 0 })
    SET_FRACTION_CONNECTED(Integer.class, Double.class),

    @EventIDField(links = { "PIPE_CLUSTERS" }, params = { 0 })
    ADD_LOADS_IN_AREA_TO_PIPECLUSTER(Integer.class, MultiPolygon.class),

    @EventIDField(links = { "PIPE_CLUSTERS" }, params = { 0 })
    REMOVE_LOADS_IN_AREA_FROM_PIPECLUSTER(Integer.class, MultiPolygon.class),

    @EventIDField(links = { "PIPE_CLUSTERS", "LEVELS" }, params = { 0, 1 })
    SET_LEVEL_ID(Integer.class, Integer.class),

    @EventIDField(links = { "PIPE_CLUSTERS" }, params = { 0 })
    REMOVE(Integer[].class),

    AUTO_CLUSTER(AutoClusterType.class, MultiPolygon.class, Integer[].class, Double.class),

    IMPORT_NIF_FILE(String.class, byte[].class, Double.class, Double.class),

    IMPORT_EXCEL_FILE(byte[].class, Double.class, Double.class),

    EXPORT_NIF_FILE,

    EXPORT_EXCEL_FILE,

    CLEAR_NETWORK(Boolean.class),

    REMOVE_ALL_CLUSTERS,

    ;

    private List<Class<?>> classes;

    private EditorPipeClusterEventType(Class<?>... classes) {
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
        if (this == IMPORT_NIF_FILE || this == IMPORT_EXCEL_FILE) {
            return String.class;
        } else if (this == EXPORT_NIF_FILE) {
            return String[].class;
        } else if (this == EXPORT_EXCEL_FILE) {
            return byte[][].class;
        }
        return null;
    }

    @Override
    public boolean isServerSide() {
        return true;
    }
}
