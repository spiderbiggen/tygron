/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.editor.event;

import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;

/**
 * EditorEventType
 * <p>
 * EditorEventType defines all events related to the Editor
 * <p>
 * @author Maxim Knepfle
 */
public enum EditorEventType implements EventTypeEnum {

    /**
     * Set the initial (empty) map size before adding stuff to it
     */
    SET_INITIAL_MAP_SIZE(Integer.class),

    /**
     *
     * @param <code>X, Y center in web mercator for the center of the to be generated map</code>
     */
    START_WORLD_CREATION(Double.class, Double.class),

    /**
     * When true activate the testrun and when false restore to previous point.
     */
    ACTIVATE_TESTRUN(Boolean.class),

    /**
     * Delete entire map
     */
    CLEAR_MAP,

    /**
     * Send chat message in my domain
     */
    SEND_SESSION_CHAT_MESSAGE(String.class);

    private List<Class<?>> classes;

    private EditorEventType(Class<?>... classes) {
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
