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
 * @author Jeroen Warmerdam
 *
 */
public enum EditorContributorEventType implements EventTypeEnum {

    ADD,

    @EventIDField(links = { "CONTRIBUTORS" }, params = { 0 })
    REMOVE_CONTRIBUTOR(Integer.class),

    @EventIDField(links = { "CONTRIBUTORS" }, params = { 0 })
    ADD_PERSON(Integer.class),

    @EventIDField(links = { "CONTRIBUTORS" }, params = { 0 })
    SET_NAME(Integer.class, String.class),

    @EventIDField(links = { "CONTRIBUTORS" }, params = { 0 })
    REMOVE_PERSON_BY_INDEX(Integer.class, Integer.class),

    @EventIDField(links = { "CONTRIBUTORS" }, params = { 0 })
    SET_IMAGE(Integer.class, String.class),

    @EventIDField(links = { "CONTRIBUTORS" }, params = { 0 })
    SET_PERSON_NAME(Integer.class, Integer.class, String.class);

    private List<Class<?>> classes;

    private EditorContributorEventType(Class<?>... classes) {
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
