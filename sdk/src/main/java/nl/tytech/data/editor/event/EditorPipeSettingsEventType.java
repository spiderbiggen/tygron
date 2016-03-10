/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.editor.event;

import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.data.engine.item.PipeSetting;

/**
 *
 * @author Frank Baars
 *
 */
public enum EditorPipeSettingsEventType implements EventTypeEnum {

    SET_NETWORK_FINANCING_INTREST(Double.class),

    SET_NETWORK_CONNECTION_FEE_PERCENTAGE(Double.class),

    SET_NETWORK_WRITE_OFF_PERIOD_YEARS(Integer.class),

    SET_CONCURRENCY_FACTOR(Double.class),

    SET_CLUSTER_FRACTION_CONNECTED(Double.class),

    SET_HEAT_EXCEL_MODEL(String.class),

    SET_HEAT_DEFAULT_NIF(String.class),

    SET_HEAT_EXCEL_PARAMETER_ADJUSTABLE(PipeSetting.Type.class, Boolean.class),

    SET_APPROVAL_UTILITY_CORPORATION_REQUIRED(Boolean.class),

    ;

    private List<Class<?>> classes;

    private EditorPipeSettingsEventType(Class<?>... classes) {
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
