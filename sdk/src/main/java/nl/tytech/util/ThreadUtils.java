/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.util;

/**
 * Thread related utils.
 * @author Maxim Knepfle
 *
 */
public class ThreadUtils {

    public static void sleepInterruptible(long timeMillis) {
        try {
            Thread.sleep(timeMillis);
        } catch (InterruptedException e) {
        }
    }

    private ThreadUtils() {

    }

}
