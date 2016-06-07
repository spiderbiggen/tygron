/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.util;

/**
 * Simple helper class to read user and server from the SDK README.txt file.
 * @author Maxim Knepfle
 *
 */
public class SDKReadmeConfig {

    public final static String SDK_README = "README.txt";

    public final static String TAG_NOTSET = "XXX-";

    public final static String USER_KEY_TAG = "USERNAME:";
    public final static String SERVER_KEY_TAG = "SERVER:";
    public final static String VERSION_KEY_TAG = "VERSION:";

    public final static String USER_VALUE_TAG = TAG_NOTSET + "USERNAME";
    public final static String SERVER_VALUE_TAG = TAG_NOTSET + "SERVER";
    public final static String VERSION_VALUE_TAG = TAG_NOTSET + "VERSION";

    public final static String DEFAULT_USER = "maxim@tygron.com";
    public final static String DEFAULT_SERVER = "localhost";

    public static final String FULL_VALUE_TAG = "XXX-FULLUSER";

    /**
     * Load Server from README.txt
     */
    public static String loadServer() {

        String readme = PackageUtils.getStringFromResource(SDK_README);
        String serverName = readme.substring(readme.indexOf(SERVER_KEY_TAG) + SERVER_KEY_TAG.length(), readme.indexOf(VERSION_KEY_TAG))
                .trim();
        if (!serverName.startsWith(TAG_NOTSET)) {
            return serverName;
        }
        return DEFAULT_SERVER;
    }

    /**
     * Load User from README.txt
     */
    public static String loadUser() {

        String readme = PackageUtils.getStringFromResource(SDK_README);
        String userName = readme.substring(readme.indexOf(USER_KEY_TAG) + USER_KEY_TAG.length(), readme.indexOf(SERVER_KEY_TAG)).trim();
        if (!userName.startsWith(TAG_NOTSET)) {
            return userName;
        }
        return DEFAULT_USER;
    }
}
