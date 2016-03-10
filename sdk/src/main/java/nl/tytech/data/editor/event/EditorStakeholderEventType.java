/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.editor.event;

import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.data.engine.item.Stakeholder;
import nl.tytech.util.color.TColor;
import com.vividsolutions.jts.geom.MultiPolygon;

/**
 * Editing Stakeholders
 * @author Maxim Knepfle
 *
 */
public enum EditorStakeholderEventType implements EventTypeEnum {

    ADD(),

    ADD_WITH_TYPE_AND_PLAYABLE(Stakeholder.Type.class, Boolean.class),

    @EventIDField(links = { "STAKEHOLDERS" }, params = { 0 })
    REMOVE(Integer[].class),

    @EventIDField(links = { "STAKEHOLDERS" }, params = { 0 })
    DUPLICATE(Integer[].class),

    @EventIDField(links = { "STAKEHOLDERS" }, params = { 0 })
    SET_COLOR(Integer.class, TColor.class),

    @EventIDField(links = { "STAKEHOLDERS" }, params = { 0 })
    SET_DESCRIPTION(Integer.class, String.class),

    @EventIDField(links = { "STAKEHOLDERS", "INDICATORS", "LEVELS" }, params = { 0, 1, 2 })
    SET_INDICATOR_WEIGHT(Integer.class, Integer.class, Integer.class, Double.class),

    @EventIDField(links = { "STAKEHOLDERS", "INDICATORS", "LEVELS" }, params = { 0, 1, 2 })
    ADD_INDICATOR_WEIGHT(Integer.class, Integer.class, Integer.class),

    @EventIDField(links = { "STAKEHOLDERS", "INDICATORS", "LEVELS" }, params = { 0, 1, 2 })
    REMOVE_INDICATOR_WEIGHT(Integer.class, Integer.class, Integer.class),

    @EventIDField(links = { "STAKEHOLDERS", "LEVELS" }, params = { 0, 1 })
    SET_LEVEL_ASSIGNMENT(Integer.class, Integer.class, String.class),

    @EventIDField(links = { "STAKEHOLDERS", "LEVELS" }, params = { 0, 1 })
    SET_LEVEL_BUDGET_INCREMENT(Integer.class, Integer.class, Double.class),

    @EventIDField(links = { "STAKEHOLDERS" }, params = { 0 })
    SET_NAME(Integer.class, String.class),

    @EventIDField(links = { "STAKEHOLDERS" }, params = { 0 })
    SET_SHORT_NAME(Integer.class, String.class),

    @EventIDField(links = { "STAKEHOLDERS" }, params = { 0 })
    SET_START_BUDGET(Integer.class, Double.class),

    @EventIDField(links = { "STAKEHOLDERS" }, params = { 0 })
    SET_TYPE(Integer.class, Stakeholder.Type.class),

    @EventIDField(links = { "STAKEHOLDERS" }, params = { 0 })
    SET_PLAYABLE(Integer.class, Boolean.class),

    @EventIDField(links = { "STAKEHOLDERS" }, params = { 0 })
    SET_PORTRAIT(Integer.class, String.class),

    @EventIDField(links = { "STAKEHOLDERS" }, params = { 0 })
    SET_STAKEHOLDER_YEARLY_INCOME(Integer.class, Integer.class),

    @EventIDField(links = { "STAKEHOLDERS" }, params = { 0 })
    ADD_LAND_OWNERSHIP(Integer.class, MultiPolygon.class),

    /**
     * Select first available stakeholder, param sessionID
     */
    SELECT_PREF_OR_FIRST_PLAYABLE_STAKEHOLDER(Integer.class, String.class),

    LOAD_OWNERSHIP_FROM_FEATURE_SERVICE(String.class, String.class, String.class);

    private List<Class<?>> classes;

    private EditorStakeholderEventType(Class<?>... classes) {
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

        if (this == ADD_WITH_TYPE_AND_PLAYABLE) {
            return Integer.class;
        } else if (this == SELECT_PREF_OR_FIRST_PLAYABLE_STAKEHOLDER) {
            return Boolean.class;
        }
        return null;
    }

    @Override
    public boolean isServerSide() {
        return true;
    }

}
