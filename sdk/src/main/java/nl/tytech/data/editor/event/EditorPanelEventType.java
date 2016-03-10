/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.editor.event;

import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.data.core.item.CodedEvent;
import nl.tytech.data.engine.item.Panel.PanelType;
import nl.tytech.data.engine.serializable.PopupModelType;
import com.vividsolutions.jts.geom.Point;

/**
 *
 * @author Frank Baars
 *
 */
public enum EditorPanelEventType implements EventTypeEnum {

    ADD(PanelType.class),

    @EventIDField(links = { "PANELS" }, params = { 0 })
    REMOVE(Integer[].class),

    @EventIDField(links = { "PANELS" }, params = { 0 })
    DUPLICATE(Integer[].class),

    @EventIDField(links = { "PANELS" }, params = { 0 })
    SET_NAME(Integer.class, String.class),

    @EventIDField(links = { "PANELS" }, params = { 0 })
    SET_URL(Integer.class, String.class),

    @EventIDField(links = { "PANELS" }, params = { 0 })
    SET_EXCEL_NAME(Integer.class, String.class),

    @EventIDField(links = { "PANELS" }, params = { 0 })
    SET_WIDTH(Integer.class, Double.class),

    @EventIDField(links = { "PANELS" }, params = { 0 })
    SET_POINT(Integer.class, Point.class),

    @EventIDField(links = { "PANELS" }, params = { 0 })
    SET_HEIGHT(Integer.class, Double.class),

    @EventIDField(links = { "PANELS", "GLOBALS" }, params = { 0, 1 })
    ADD_GLOBAL_IDS(Integer.class, Integer[].class),

    @EventIDField(links = { "PANELS", "GLOBALS" }, params = { 0, 1 })
    REMOVE_GLOBAL_IDS(Integer.class, Integer[].class),

    @EventIDField(links = { "PANELS" }, params = { 0 })
    SET_ACTIVE(Integer.class, Boolean.class),

    @EventIDField(links = { "PANELS", "STAKEHOLDERS" }, params = { 0, 1 })
    SET_FOR_STAKEHOLDER(Integer.class, Integer.class),

    @EventIDField(links = { "PANELS" }, params = { 0 })
    SET_FOR_EVERYONE(Integer.class),

    @EventIDField(links = { "PANELS" }, params = { 0 })
    SET_POPUP_MODEL_TYPE(Integer.class, PopupModelType.class),

    @EventIDField(links = { "PANELS" }, params = { 0 })
    ADD_ANSWER(Integer.class),

    @EventIDField(links = { "PANELS" }, params = { 0 })
    ADD_ANSWER_EVENT(Integer.class, Integer.class, Boolean.class),

    @EventIDField(links = { "PANELS" }, params = { 0 })
    REMOVE_ANSWER(Integer.class, Integer.class),

    @EventIDField(links = { "PANELS" }, params = { 0 })
    REMOVE_ANSWER_EVENT(Integer.class, Integer.class, Boolean.class, Integer.class),

    @EventIDField(links = { "PANELS" }, params = { 0 })
    SET_ANSWER_CONTENTS(Integer.class, Integer.class, String.class),

    @EventIDField(links = { "PANELS" }, params = { 0 })
    DUPLICATE_ANSWER(Integer.class, Integer.class),

    @EventIDField(links = { "PANELS" }, params = { 0 })
    EDIT_ANSWER_EVENT(Integer.class, Integer.class, Boolean.class, CodedEvent.class),

    IMPORT_WIKIPEDIA_POINTS(),

    ;

    private List<Class<?>> classes;

    private EditorPanelEventType(Class<?>... classes) {
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
        if (this == SET_EXCEL_NAME || this == SET_URL || this == REMOVE_GLOBAL_IDS || this == ADD_GLOBAL_IDS) {
            return String.class;
        }
        return null;
    }

    @Override
    public boolean isServerSide() {
        return true;
    }

}
