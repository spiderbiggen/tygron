/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.editor.event;

import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.data.engine.item.Function.Region;
import nl.tytech.data.engine.item.Setting;
import nl.tytech.data.engine.serializable.FacilitatorTabEnum;
import nl.tytech.data.engine.serializable.PanelEnum;
import nl.tytech.data.engine.serializable.ViewEnum;
import nl.tytech.locale.TCurrency;
import nl.tytech.locale.unit.UnitSystemType;

/**
 * @author Jeroen Warmerdam
 *
 */
public enum EditorSettingsEventType implements EventTypeEnum {

    SET_COMMERCIAL_NAME(String.class),

    /**
     * Set region, e.g. Asia.
     */
    SET_REGION(Region.class),

    /**
     * Set the introduction description of this project
     * @param String description
     */
    SET_DESCRIPTION(String.class),

    // wizard
    SET_PROJECT_NAME(String.class),

    SET_INTRO_IMAGE(String.class),

    SET_PANEL_AVAILABILITY(PanelEnum.class, Boolean.class),

    @EventIDField(links = { "WEATHERS" }, params = { 0 })
    SET_RAINMAN_WEATHER_ID(Integer.class),

    SET_SETTING(Setting.Type.class, String.class),

    /**
     * Triggered at the end of the wizard.
     */
    WIZARD_FINISHED(),

    SET_FACILITATOR_TAB_ACTIVE(FacilitatorTabEnum.class, Boolean.class),

    SET_SATELLITE_NAME(String.class),

    SET_VIEWFLOW_ACTIVE(ViewEnum.class, Boolean.class),

    SET_CURRENCY(TCurrency.class),

    SET_MEASUREMENT_SYSTEM_TYPE(UnitSystemType.class),

    SET_SHOW_PERMISSION_POPUPS(Boolean.class),

    SET_FIXED_SUN_YEAR_ANGLE(Integer.class),

    SET_FIXED_SUN_DAY_ANGLE(Double.class),

    SET_GRID_CELL_SIZE(Integer.class),

    SET_WIND_SPEED(Integer.class);

    private List<Class<?>> classes;

    private EditorSettingsEventType(Class<?>... classes) {

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
        return null;
    }

    @Override
    public boolean isServerSide() {
        return true;
    }

}
