/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.util;

import java.io.File;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import nl.tytech.util.logger.TLogger;

/**
 * Util class to detect OS related settings
 * @author Maxim Knepfle
 *
 */
public class OSUtils {

    /**
     * TyTech Engine dir.
     */
    public final static String STORAGE_DIRECTORY;

    static {
        STORAGE_DIRECTORY = getAppDataDirectory() + File.separator + "TyTech" + File.separator;
    }

    public static String getAppDataDirectory() {

        if (isWindows()) {
            /**
             * Try local appdata first
             */
            String localAppDataLocation = System.getenv("LOCALAPPDATA");
            if (localAppDataLocation != null) {
                File file = new File(localAppDataLocation);
                if (file.exists() && file.canWrite()) {
                    return localAppDataLocation;
                }
            }

            /**
             * Secondly try normal (roaming) appdata
             */
            localAppDataLocation = System.getenv("APPDATA");
            if (localAppDataLocation != null) {
                File file = new File(localAppDataLocation);
                if (file.exists() && file.canWrite()) {
                    return localAppDataLocation;
                }
            }
        }

        /**
         * Fall back to default user dir.
         */
        return System.getProperty("user.home");
    }

    /**
     * Returns a list of MAC addresses for this system.
     * @return
     */
    public static List<String> getMacAddresses() {

        List<String> macAddresses = new ArrayList<String>();
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            if (nis != null) {
                while (nis.hasMoreElements()) {
                    NetworkInterface ni = nis.nextElement();

                    // interface should not be null and not be a tunnel or something else.
                    if (ni != null && !ni.isPointToPoint()) {
                        byte[] mac = ni.getHardwareAddress();
                        if (mac != null) {
                            StringBuffer macAddress = new StringBuffer();
                            for (int i = 0; i < mac.length; i++) {
                                macAddress.append(String.format("%02X%s", mac[i], i < mac.length - 1 ? "-" : ""));
                            }
                            // should be more then at least 2 chars.
                            if (macAddress.length() > 2) {
                                macAddresses.add(macAddress.toString());
                            }
                        }
                    }
                }
            }
        } catch (Exception exp) {
            TLogger.exception(exp);
        }
        return macAddresses;
    }

    public static boolean isAndroid() {
        String os = System.getProperty("java.vendor").toLowerCase();
        // linux or unix
        return (os.indexOf("android") >= 0);
    }

    public static boolean isLinux() {
        String os = System.getProperty("os.name").toLowerCase();
        // linux
        return (os.indexOf("linux") >= 0);
    }

    public static boolean isMac() {
        String os = System.getProperty("os.name").toLowerCase();
        // Mac
        return (os.indexOf("mac") >= 0);
    }

    public static boolean isSolaris() {
        String os = System.getProperty("os.name").toLowerCase();
        // Solaris
        return (os.indexOf("sunos") >= 0);
    }

    public static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        // windows
        return (os.indexOf("win") >= 0);
    }
}
