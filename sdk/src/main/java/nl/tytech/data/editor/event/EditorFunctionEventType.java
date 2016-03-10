/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.editor.event;

import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;

/**
 * @author Maxim Knepfle
 *
 */
public enum EditorFunctionEventType implements EventTypeEnum {

    @EventIDField(links = { "FUNCTIONS", "MODEL_DATAS" }, params = { 0, 1 })
    ADD_MODEL(Integer.class, Integer.class),

    @EventIDField(links = { "FUNCTIONS", "MODEL_DATAS" }, params = { 0, 1 })
    REMOVE_MODEL(Integer.class, Integer.class);

    private List<Class<?>> classes;

    private EditorFunctionEventType(Class<?>... classes) {
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
