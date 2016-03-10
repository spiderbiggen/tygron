/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.util;

/**
 * Engine version definitions class
 *
 * @author Maxim Knepfle
 *
 */
public class Engine {

    public final static String VERSION = "2016.4.0 DEV 17";

    private final static String APP_NAME = "Tygron Engine";

    public final static String USER_AGENT = APP_NAME + " " + VERSION;

    public final static String NAME_WITH_MAIN_VERSION = APP_NAME + " " + getMainVersion();

    public static int getMainVersion() {
        return Integer.valueOf(VERSION.toLowerCase().split("\\.")[0]);
    }

    public static boolean isDevVersion() {
        return VERSION.toLowerCase().contains("dev");
    }

    public static boolean isRCVersion() {
        return VERSION.toLowerCase().contains("rc");
    }

}
