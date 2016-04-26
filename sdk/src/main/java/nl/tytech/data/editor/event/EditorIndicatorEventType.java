/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.editor.event;

import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.data.engine.item.CustomIndicator.CustomIndicatorType;
import nl.tytech.data.engine.item.GlobalIndicator.GlobalIndicatorType;
import nl.tytech.data.engine.item.PersonalIndicator.PersonalIndicatorType;
import nl.tytech.data.engine.serializable.CalculationSpaceType;
import nl.tytech.util.color.TColor;

/**
 * @author Jeroen Warmerdam, Frank Baars
 */
public enum EditorIndicatorEventType implements EventTypeEnum {

    @EventIDField(links = { "STAKEHOLDERS" }, params = { 0 })
    ADD_PERSONAL_INDICATOR(Integer.class, PersonalIndicatorType.class),

    @EventIDField(links = { "STAKEHOLDERS" }, params = { 0 })
    ADD_GLOBAL_INDICATOR(Integer.class, GlobalIndicatorType.class),

    @EventIDField(links = { "STAKEHOLDERS" }, params = { 0 })
    ADD_CUSTOM_INDICATOR(Integer.class, CustomIndicatorType.class),

    @EventIDField(links = { "INDICATORS" }, params = { 0 })
    REMOVE(Integer.class),

    @EventIDField(links = { "INDICATORS" }, params = { 0 })
    DUPLICATE_INDICATOR(Integer.class, Integer.class),

    @EventIDField(links = { "INDICATORS", "STAKEHOLDERS" }, params = { 0, 1 })
    DUPLICATE_PERSONAL_INDICATOR(Integer.class, Integer.class),

    @EventIDField(links = { "INDICATORS", "STAKEHOLDERS" }, params = { 0, 1 })
    SET_STAKEHOLDER(Integer.class, Integer.class),

    @EventIDField(links = { "LEVELS", "INDICATORS" }, params = { 0, 1 })
    SET_TARGETS(Integer.class, Integer.class, double[].class),

    @EventIDField(links = { "LEVELS", "INDICATORS" }, params = { 0, 1 })
    SET_TARGET(Integer.class, Integer.class, Integer.class, Double.class),

    RESET_INDICATORS(Boolean.class),

    @EventIDField(links = { "INDICATORS" }, params = { 0 })
    SET_DESCRIPTION(Integer.class, String.class),

    @EventIDField(links = { "INDICATORS" }, params = { 0 })
    SET_NAME(Integer.class, String.class),

    @EventIDField(links = { "INDICATORS" }, params = { 0 })
    SET_SHORT_NAME(Integer.class, String.class),

    @EventIDField(links = { "INDICATORS" }, params = { 0 })
    SET_ACTIVE(Integer.class, Boolean.class),

    @EventIDField(links = { "INDICATORS" }, params = { 0 })
    SET_ABSOLUTE(Integer.class, Boolean.class),

    @EventIDField(links = { "INDICATORS" }, params = { 0 })
    SET_COLOR(Integer.class, TColor.class),

    @EventIDField(links = { "INDICATORS" }, params = { 0 })
    SET_IMAGE_NAME(Integer.class, String.class),

    @EventIDField(links = { "INDICATORS" }, params = { 0 })
    SET_API_REMOTE_ADDRESS(Integer.class, String.class),

    @EventIDField(links = { "INDICATORS" }, params = { 0 })
    UPDATE_CUSTOM_UNIT_DIMENSION(Integer.class, String.class),

    @EventIDField(links = { "INDICATORS" }, params = { 0 })
    SET_CALCULATION_TYPE(Integer.class, CalculationSpaceType.class),

    @EventIDField(links = { "INDICATORS" }, params = { 0 })
    SET_EXCEL_NAME(Integer.class, String.class),

    @EventIDField(links = { "INDICATORS" }, params = { 0 })
    EXPORT_EXCEL_FILE(Integer.class, Boolean.class);

    ;

    private List<Class<?>> classes;

    private EditorIndicatorEventType(Class<?>... classes) {
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

        if (RESET_INDICATORS == this) {
            return Boolean.class;
        }
        if (SET_EXCEL_NAME == this) {
            return String.class;
        }
        if (this == EXPORT_EXCEL_FILE) {
            return byte[].class;
        }

        return (this == ADD_PERSONAL_INDICATOR || this == ADD_GLOBAL_INDICATOR || this == ADD_CUSTOM_INDICATOR) ? Integer.class : null;
    }

    @Override
    public boolean isServerSide() {
        return true;
    }

}
