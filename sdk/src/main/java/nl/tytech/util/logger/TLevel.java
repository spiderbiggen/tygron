/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.util.logger;

import java.util.logging.Level;

/**
 * Extra log severity settings used by Tygron Engine
 * @author Maxim Knepfle
 *
 */
public class TLevel extends Level {

    /**
     *
     */
    private static final long serialVersionUID = 7727487931637590482L;

    /**
     * SHOWSTOPPER is a message level indicating a realy serious failure. Inmidiate stop of program.
     * <p>
     * In general SHOWSTOPPER messages should describe events that are of considerable importance and which will prevent normal program
     * execution. They should be reasonably intelligible to end users and to system administrators. This level is initialized to
     * <CODE>1100</CODE>.
     */
    public final static TLevel SHOWSTOPPER = new TLevel("SHOWSTOPPER", 1100);

    /**
     * NOTIFICATION means that it will be emailed/notified to RD, but is not a bug, e.g. security breach attempts
     *
     * <CODE>1050</CODE>.
     */
    public final static TLevel NOTIFICATION = new TLevel("NOTIFICATION", 1050);

    /**
     * @param name
     * @param value
     */
    private TLevel(String name, int value) {

        super(name, value);

    }
}
