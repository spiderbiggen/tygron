/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.client.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import nl.tytech.util.StringUtils;

/**
 * OnThread
 * <p>
 * Event is handled on this thread
 * <p>
 *
 *
 * @author Maxim Knepfle
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface OnEventThread {

    public enum EventThread {

        /**
         * Execute on orginal calling thread
         */
        CALLER,

        /**
         * JavaFX Thread
         */
        JAVAFX,

        /**
         * Parallel thread
         */
        PARALLEL,

        /**
         * Long running thread tasks for background processes.
         */
        LONG,
        /**
         * Open GL thread handled by JME3
         */
        OPENGL;
    }

    String value() default StringUtils.EMPTY;
}
