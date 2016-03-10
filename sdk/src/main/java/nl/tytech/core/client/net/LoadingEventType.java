/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.client.net;

import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;

/**
 * LoadingEventType
 * <p>
 * Event related to the loading proces.
 * </p>
 * @author Maxim Knepfle
 */
public enum LoadingEventType implements EventTypeEnum {

    TEXT(String.class, Integer.class),

    ASSET(String.class),

    FAIL();

    private final List<Class<?>> classes = new ArrayList<Class<?>>();

    private LoadingEventType(Class<?>... c) {
        for (Class<?> classz : c) {
            classes.add(classz);
        }
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
        return false;
    }
}
