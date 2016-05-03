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
 * @author Jeroen Warmerdam
 * 
 */
public enum EditorSpecialEffectEventType implements EventTypeEnum {

    ADD,

    @EventIDField(links = { "SPECIAL_EFFECTS" }, params = { 0 })
    ADD_PARTICLE_PAIR(Integer.class),

    @EventIDField(links = { "SPECIAL_EFFECTS" }, params = { 0 })
    REMOVE(Integer.class),

    @EventIDField(links = { "SPECIAL_EFFECTS" }, params = { 0 })
    DUPLICATE(Integer.class),

    @EventIDField(links = { "SPECIAL_EFFECTS" }, params = { 0 })
    DUPLICATE_PAIR(Integer.class, Integer.class),

    @EventIDField(links = { "SPECIAL_EFFECTS" }, params = { 0 })
    SET_ACTIVE(Integer.class, Boolean.class),

    @EventIDField(links = { "SPECIAL_EFFECTS" }, params = { 0 })
    SET_NAME(Integer.class, String.class),

    @EventIDField(links = { "SPECIAL_EFFECTS" }, params = { 0 })
    UPDATE_LOCATION(Integer.class, Point.class),

    @EventIDField(links = { "SPECIAL_EFFECTS" }, params = { 0 })
    REMOVE_PARTICLE_PAIR(Integer.class, Integer.class),

    @EventIDField(links = { "SPECIAL_EFFECTS" }, params = { 0 })
    UPDATE_PARTICLE_PAIR_LOCATION(Integer.class, Integer.class, double[].class),

    @EventIDField(links = { "SPECIAL_EFFECTS", "PARTICLE_EMITTERS" }, params = { 0, 2 })
    UPDATE_PARTICLE_PAIR_TYPE(Integer.class, Integer.class, Integer.class);

    private List<Class<?>> classes;

    private EditorSpecialEffectEventType(Class<?>... classes) {

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
