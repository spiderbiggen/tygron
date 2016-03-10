/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.event;

import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event.SessionEventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;

/**
 * Events related to messages sent between stakeholders.
 * @author Frank Baars
 *
 */
public enum FacilitatorSessionEventType implements SessionEventTypeEnum {

    @EventParamData(editor = true, desc = "Create a new message", params = { "Stakeholder receiver ID (-1 is to all)",
            "Stakeholder sender ID", "Message title", "Message body", "Motivation required for response" })
    @EventIDField(links = { "STAKEHOLDERS" }, params = { 1 })
    NEW_MESSAGE(Integer.class, Integer.class, String.class, String.class, Boolean.class),

    @EventParamData(editor = true, desc = "Send a message", params = { "Message ID" })
    @EventIDField(links = { "MESSAGES" }, params = { 0 })
    SEND_MESSAGE(Integer.class),

    @EventParamData(editor = true, desc = "Revoke a message", params = { "Message ID" })
    @EventIDField(links = { "MESSAGES" }, params = { 0 })
    REVOKE_MESSAGE(Integer.class);

    private List<Class<?>> classes;

    private FacilitatorSessionEventType(Class<?>... classes) {
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

    @Override
    public boolean triggerTestRun() {
        return true;
    }
}
