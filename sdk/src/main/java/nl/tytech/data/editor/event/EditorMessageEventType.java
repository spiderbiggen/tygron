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

/**
 * @author Jeroen Warmerdam
 * 
 */
public enum EditorMessageEventType implements EventTypeEnum {

    @EventIDField(links = { "STAKEHOLDERS", "STAKEHOLDERS" }, params = { 0, 1 })
    ADD(Integer.class, Integer.class),

    @EventIDField(links = { "MESSAGES" }, params = { 0 })
    ADD_MESSAGES_TO_MESSAGE(Integer.class, Integer[].class),

    @EventIDField(links = { "MESSAGES" }, params = { 0 })
    ADD_ANSWER(Integer.class),

    @EventIDField(links = { "MESSAGES" }, params = { 0 })
    ADD_ANSWER_EVENT(Integer.class, Integer.class, Boolean.class),

    @EventIDField(links = { "MESSAGES" }, params = { 0 })
    REMOVE(Integer.class),

    @EventIDField(links = { "MESSAGES" }, params = { 0 })
    REMOVE_ANSWER(Integer.class, Integer.class),

    @EventIDField(links = { "MESSAGES" }, params = { 0 })
    REMOVE_ANSWER_EVENT(Integer.class, Integer.class, Boolean.class, Integer.class),

    @EventIDField(links = { "MESSAGES" }, params = { 0 })
    DUPLICATE(Integer.class),

    @EventIDField(links = { "MESSAGES" }, params = { 0 })
    DUPLICATE_ANSWER(Integer.class, Integer.class),

    @EventIDField(links = { "MESSAGES" }, params = { 0 })
    EDIT_ANSWER_EVENT(Integer.class, Integer.class, Boolean.class, CodedEvent.class),

    @EventIDField(links = { "MESSAGES", "MESSAGES" }, params = { 0, 1 })
    REMOVE_MESSAGE_FROM_MESSAGE(Integer.class, Integer.class),

    @EventIDField(links = { "MESSAGES" }, params = { 0 })
    SET_FACILITATOR_MESSAGE(Integer.class, Boolean.class),

    @EventIDField(links = { "MESSAGES" }, params = { 0 })
    SET_ANSWER_CONTENTS(Integer.class, Integer.class, String.class),

    @EventIDField(links = { "MESSAGES", "STAKEHOLDERS" }, params = { 0, 1 })
    SET_FROM(Integer.class, Integer.class),

    @EventIDField(links = { "MESSAGES", "STAKEHOLDERS" }, params = { 0, 1 })
    SET_TO(Integer.class, Integer.class),

    @EventIDField(links = { "MESSAGES" }, params = { 0 })
    SET_TO_EVERYONE(Integer.class, Boolean.class),

    @EventIDField(links = { "MESSAGES" }, params = { 0 })
    SET_CONTENT(Integer.class, String.class),

    @EventIDField(links = { "MESSAGES" }, params = { 0 })
    SET_SUBJECT(Integer.class, String.class),

    @EventIDField(links = { "MESSAGES" }, params = { 0 })
    SET_TRIGGER_DATE(Integer.class, Long.class),

    @EventIDField(links = { "MESSAGES" }, params = { 0 })
    SET_DEFAUlT_AUTORESPOND_ANSWER_ID(Integer.class, Integer.class, Boolean.class);

    private List<Class<?>> classes;

    private EditorMessageEventType(Class<?>... classes) {
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
