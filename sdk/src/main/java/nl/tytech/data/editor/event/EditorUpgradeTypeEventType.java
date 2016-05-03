/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.editor.event;

import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.data.engine.serializable.CalculationSpaceType;

/**
 *
 * @author Frank Baars
 *
 */
public enum EditorUpgradeTypeEventType implements EventTypeEnum {

    ADD,

    @EventIDField(links = { "UPGRADE_TYPES" }, params = { 0 })
    REMOVE_UPGRADE_TYPE(Integer[].class),

    @EventIDField(links = { "UPGRADE_TYPES" }, params = { 0 })
    DUPLICATE(Integer[].class),

    @EventIDField(links = { "UPGRADE_TYPES" }, params = { 0 })
    ADD_PAIR(Integer.class),

    @EventIDField(links = { "UPGRADE_TYPES" }, params = { 0 })
    REMOVE_PAIR(Integer.class, Integer.class),

    @EventIDField(links = { "UPGRADE_TYPES", "FUNCTIONS", "FUNCTIONS" }, params = { 0, 1, 2 })
    CHANGE_PAIR(Integer.class, Integer.class, Integer.class, Boolean.class),

    @EventIDField(links = { "UPGRADE_TYPES" }, params = { 0 })
    DUPLICATE_PAIR(Integer.class, Integer.class),

    @EventIDField(links = { "UPGRADE_TYPES" }, params = { 0 })
    SET_MUST_OWN(Integer.class, Boolean.class),

    @EventIDField(links = { "UPGRADE_TYPES" }, params = { 0 })
    SET_ZONE_PERMIT_REQUIRED(Integer.class, Boolean.class),

    @EventIDField(links = { "UPGRADE_TYPES" }, params = { 0 })
    SET_NAME(Integer.class, String.class),

    @EventIDField(links = { "UPGRADE_TYPES" }, params = { 0 })
    SET_DESCRIPTION(Integer.class, String.class),

    @EventIDField(links = { "UPGRADE_TYPES" }, params = { 0 })
    SET_CONSTRUCTION_TIME_MONTHS(Integer.class, Double.class),

    @EventIDField(links = { "UPGRADE_TYPES" }, params = { 0 })
    SET_COST_M2(Integer.class, Double.class),

    @EventIDField(links = { "UPGRADE_TYPES" }, params = { 0 })
    SET_IMAGE(Integer.class, String.class),

    @EventIDField(links = { "UPGRADE_TYPES" }, params = { 0 })
    SET_CALCULATION_TYPE(Integer.class, CalculationSpaceType.class),

    ;

    private List<Class<?>> classes;

    private EditorUpgradeTypeEventType(Class<?>... classes) {
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
