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
     * Operating System tag
     */
    private final static String OS_NAME = System.getProperty("os.name").toLowerCase();

    /**
     * TyTech Engine dir.
     */
    public final static String STORAGE_DIRECTORY;

    static {
        STORAGE_DIRECTORY = getLocalAppDirectory() + File.separator + getLocalFileName() + File.separator;
    }

    public final static String getLocalAppDirectory() {

        String userHome = System.getProperty("user.home");

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
        } else if (isMac()) {
            // macs store it in app support dir
            return userHome + File.separator + "Library" + File.separator + "Application Support";
        }

        /**
         * Fall back to default user dir.
         */
        return userHome;
    }

    private final static String getLocalFileName() {

        String fileName = "TyTech";
        if (isLinux()) {
            // linux stores data is hidden directory
            return "." + fileName;
        }
        return fileName;
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
        return (OS_NAME.indexOf("android") >= 0);
    }

    public static boolean isLinux() {
        return (OS_NAME.indexOf("linux") >= 0);
    }

    public static boolean isMac() {
        return (OS_NAME.indexOf("mac") >= 0);
    }

    public static boolean isSolaris() {
        return (OS_NAME.indexOf("sunos") >= 0);
    }

    public static boolean isWindows() {
        return (OS_NAME.indexOf("win") >= 0);
    }
}
