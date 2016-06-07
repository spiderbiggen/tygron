/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.client.net;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import nl.tytech.core.client.concurrent.SliceManager;
import nl.tytech.core.client.event.EventManager;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.net.Api;
import nl.tytech.core.net.Network;
import nl.tytech.core.net.Network.ConnectionEvent;
import nl.tytech.core.net.event.RemoteServicesEvent.ServiceEventType;
import nl.tytech.core.net.event.UserServiceEventType;
import nl.tytech.core.net.serializable.Domain;
import nl.tytech.core.net.serializable.User;
import nl.tytech.core.util.SettingsManager;
import nl.tytech.util.Base64;
import nl.tytech.util.Engine;
import nl.tytech.util.OSUtils;
import nl.tytech.util.RestManager;
import nl.tytech.util.RestManager.Format;
import nl.tytech.util.RestManager.ResponseException;
import nl.tytech.util.RestUtils.BadRequestType;
import nl.tytech.util.StringUtils;
import nl.tytech.util.logger.TLogger;

/**
 * </p>
 * @author Maxim Knepfle
 */
public class ServicesManager {

    public enum ServicesEventType implements EventTypeEnum {

        ESRI_ACCOUNT_UPDATED,

        REQUEST_DEBUG_INFO,

        ;

        private final List<Class<?>> classes = new ArrayList<Class<?>>();

        private ServicesEventType(Class<?>... c) {
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

    private static class SingletonHolder {
        private static final ServicesManager INSTANCE = new ServicesManager();
    }

    public static <T> T fireServiceEvent(ServiceEventType type, Object... args) {
        if (Thread.currentThread().getId() == SliceManager.OPENGL_THREAD_ID) {
            TLogger.severe("Do not fire events from OpenGL thread!");
            Thread.dumpStack();
        }
        return SingletonHolder.INSTANCE._fireRestServicesEvent(type, args);
    }

    public static String getApiTarget() {
        return SingletonHolder.INSTANCE._getApiTarget();
    }

    public static String getMyHostName() {
        return SingletonHolder.INSTANCE._getMyHostName();
    }

    public static User getMyUserAccount() {
        return SingletonHolder.INSTANCE._getMyUserAccount(false);
    }

    public static String getServer() {
        return SingletonHolder.INSTANCE._getServer();
    }

    public static boolean hasLoginCredentials() {
        return SingletonHolder.INSTANCE._hasLoginCredentials();
    }

    public static boolean isPublicAccount() {
        return SingletonHolder.INSTANCE._isPublicAccount();
    }

    public static User reloadMyUserAccount() {
        return SingletonHolder.INSTANCE._getMyUserAccount(true);
    }

    public static void removeLoginCredentials() {
        SingletonHolder.INSTANCE._removeLoginCredentials();
    }

    public static void setPublicAccount(boolean publicAccount) {
        SingletonHolder.INSTANCE._setPublicAccount(publicAccount);
    }

    public static void setSessionLoginCredentials(String username, String passwd) {
        SingletonHolder.INSTANCE._setLoginCredentials(username, passwd, false);
    }

    public static void setSessionLoginCredentials(String username, String passwd, boolean hashKey) {
        SingletonHolder.INSTANCE._setLoginCredentials(username, passwd, hashKey);
    }

    /**
     * Test the online connection to the server as API/SDK client
     * @return NULL means OK, otherwise a String is returned with the failure cause.
     */
    public static String testServerAPIConnection() {
        return SingletonHolder.INSTANCE._testServerConnection(true);
    }

    /**
     * Test the online connection to the server as Tygron Cient Application
     * @return NULL means OK, otherwise a String is returned with the failure cause.
     */
    public static String testServerConnection() {
        return SingletonHolder.INSTANCE._testServerConnection(false);
    }

    /**
     * Client host name
     */
    private String hostName = "Unknown";

    /**
     * Login credential of this session only.
     */
    private String userName = StringUtils.EMPTY;

    private String passwd = StringUtils.EMPTY;

    private boolean hashKey = true;

    /**
     * My user account.
     */
    private User myUser = null;

    private boolean publicAccount = false;

    private Long serverBootupTime = null;

    private final String apiTarget;

    private final String server;

    private ServicesManager() {

        /**
         * Get my computer name
         */
        try {
            hostName = InetAddress.getLocalHost().getHostName();
            hostName += " (" + InetAddress.getLocalHost().getHostAddress() + ")";
        } catch (Exception exp) {
            TLogger.exception(exp);
        }

        /**
         * Create target URL
         */
        String ip = SettingsManager.getServerIP();
        server = "https://" + ip + (Network.PortSetting.SSL_PORT == 443 ? "" : ":" + Network.PortSetting.SSL_PORT) + "/";
        apiTarget = server + Api.ROOT;

        /**
         * Localhost connection override ssl *.tygron.com host safety check
         */
        if ("localhost".equals(ip) || "127.0.0.1".equals(ip)) {
            HttpsURLConnection.setDefaultHostnameVerifier((arg0, arg1) -> true);
        }
        this.updateHeaders();

    }

    @SuppressWarnings("unchecked")
    private <T> T _fireRestServicesEvent(final ServiceEventType type, final Object... args) {

        try {
            return (T) RestManager.post(apiTarget, "services/event/" + type.getClass().getSimpleName() + "/" + type.toString(), null, args,
                    type.getResponseClass(), Format.TJSON);

        } catch (Exception exp) {

            /**
             * Default answer, can be overriden
             */
            ConnectionEvent eventType = ConnectionEvent.SERVER_REFUSED;
            String details = eventType.getDetails();

            if (exp instanceof ResponseException) {
                BadRequestType expType = ((ResponseException) exp).getExceptionType();
                int statusCode = ((ResponseException) exp).getStatusCode();

                if (statusCode == Response.Status.UNAUTHORIZED.getStatusCode()) {
                    eventType = ConnectionEvent.AUTHENTICATION_FAIL;
                    details = exp.getMessage();

                } else if (expType == BadRequestType.SERVER_REBOOT) {
                    eventType = ConnectionEvent.SERVER_REBOOT;
                    details = eventType.getDetails();

                } else if (expType == BadRequestType.NO_SESSION_IN_SLOT) {
                    eventType = ConnectionEvent.SERVER_NO_SESSION;
                    details = eventType.getDetails();

                } else if (statusCode == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
                    eventType = ConnectionEvent.SERVER_EXECUTION;
                    details = eventType.getDetails();
                } else {
                    TLogger.exception(exp);
                }
            }
            if (eventType != null) {
                EventManager.fire(eventType, null, details, exp);
            }
            return null;
        }
    }

    private String _getApiTarget() {
        return apiTarget;
    }

    private String _getMyHostName() {
        return hostName;
    }

    private synchronized User _getMyUserAccount(boolean reload) {

        if (this.myUser == null || reload) {
            // cannot continue, this freezes untill resolved
            this.myUser = _fireRestServicesEvent(UserServiceEventType.GET_MY_USER);
            if (this.myUser != null && !reload) {
                SettingsManager.setOnlineAccountDomain(this.myUser.getDomain());
            }
        }
        return this.myUser;
    }

    private String _getServer() {
        return server;
    }

    private boolean _hasLoginCredentials() {
        return StringUtils.containsData(this.getUserName()) && StringUtils.containsData(this.getUserPasswd());
    }

    private boolean _isPublicAccount() {
        return publicAccount;
    }

    private void _removeLoginCredentials() {
        this.myUser = null;
        this.userName = StringUtils.EMPTY;
        this.passwd = StringUtils.EMPTY;
        SettingsManager.setOnlineUserName(StringUtils.EMPTY);
        SettingsManager.setOnlinePassword(StringUtils.EMPTY);
        updateHeaders();
    }

    private void _setLoginCredentials(String username, String passwd, boolean hashKey) {
        this.userName = username;
        this.passwd = passwd;
        this.hashKey = hashKey;
        updateHeaders();
    }

    private void _setPublicAccount(boolean publicAccount) {
        this.publicAccount = publicAccount;
        updateHeaders();
    }

    /**
     * Test online connection when valid return NULL otherwise the fail cause.
     * @return
     */
    private String _testServerConnection(boolean api) {

        try {
            TLogger.info("Using HTTP SSL to contact the server for " + (api ? "API/SDK compatibility test." : "App version test."));

            // do online test
            String[] result = RestManager.post(server, "test", null, null, String[].class);
            if (result == null || result.length != 2 || !StringUtils.containsData(result[0]) || !StringUtils.containsData(result[1])) {
                return "Got negative answer from Server on test connection.";
            }
            serverBootupTime = Long.valueOf(result[0]);
            String serverVersion = result[1];

            if (api) {
                if (!serverVersion.startsWith(Engine.VERSION_API_COMPATIBLE)) {
                    return "Please update your API/SDK to version: " + serverVersion;
                }
            } else {
                if (!serverVersion.equals(Engine.VERSION)) {
                    return "Please update your Client Application to version: " + serverVersion;
                }
            }
            return null;

        } catch (Exception exp) {
            TLogger.exception(exp);
        }
        return "Cannot connect to Server at location: " + SettingsManager.getServerIP();
    }

    /**
     * Returns a unique public username that can be used to login without an account.
     * @return
     */
    private String getMyPublicUsername() {

        List<String> macs = OSUtils.getMacAddresses();
        if (macs.size() == 0) {
            TLogger.severe("Cannot generate a public username without a MAC address.");
            return null;
        }
        String mac = macs.get(0);
        return mac.toLowerCase() + User.PUBLIC_ACCOUNT;
    }

    private String getUserName() {
        if (_isPublicAccount()) {
            return getMyPublicUsername();
        }
        return userName;
    }

    private String getUserPasswd() {
        if (_isPublicAccount()) {
            return Domain.PUBLIC;
        }
        return passwd;
    }

    private void updateHeaders() {

        String encoded = Base64.encode((this.getUserName() + ":" + this.getUserPasswd()).getBytes());
        boolean basic = _isPublicAccount() || !this.hashKey;
        String passHeader = (basic ? "Basic " : "Hashkey ") + encoded;

        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        headers.putSingle(HttpHeaders.AUTHORIZATION, passHeader);
        headers.putSingle(Network.CLIENT_NAME_HEADER, hostName);

        if (this.serverBootupTime != null) {
            headers.putSingle(Network.SERVER_BOOT_HEADER, this.serverBootupTime.toString());
        }
        RestManager.setHeaders(apiTarget, headers);
    }
}
