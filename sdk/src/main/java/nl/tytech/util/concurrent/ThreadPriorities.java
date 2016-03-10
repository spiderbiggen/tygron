/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.util.concurrent;

/**
 * Thread prio (e.g. for PriorityLock)
 * @author Maxim
 *
 */
public class ThreadPriorities {

    /**
     * Low prio threads like daemons or power sharing
     */
    public static final int LOW = 1;

    /**
     * Avg, user feedback
     */
    public static final int MEDIUM = 5;

    /**
     * High, user actions
     */
    public static final int HIGH = 9;

    /**
     * Top
     */
    public static final int IMMEDIATE_EXECUTION = 10;

}
