/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.net;

import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.util.StringUtils;
import nl.tytech.util.logger.TLogger;

/**
 * Collection of shared small networking classes
 * @author Maxim Knepfle
 *
 */
public interface Network {

    /**
     * Defines the type of communication. Each client (client, beam and control) have unique subscriptions to events and priorities.
     */
    public enum AppType {

        /**
         * The app game launcher.
         */
        LAUNCHER,
        /**
         * Application for the facilitator. Has more rights in network mode.
         */
        FACILITATOR,
        /**
         * Beam application, simple small apps.
         */
        BEAMER,
        /**
         * Application for the session participants.
         */
        PARTICIPANT,
        /**
         * The editor application.
         */
        EDITOR,
        /**
         * Server app running logic
         */
        SERVER,
        /**
         * Tools client application
         */
        TOOLS;

        public static AppType get(int value) {
            for (AppType subscription : AppType.values()) {
                if (subscription.ordinal() == value) {
                    return subscription;
                }
            }
            TLogger.severe("Cannot get subscription for ordinal value: " + value + ".");
            return AppType.FACILITATOR;
        }

        /**
         * Name used for the final end user in documentation etc.
         */
        public String getEndUserNaming() {
            return StringUtils.capitalizeWithSpacedUnderScores(this.name().toLowerCase());
        }
    }

    public enum ClientConnectionState {
        OFFLINE(false), CONNECTING(true), DISCONNECTING(true), CONNECTED(false);

        private final boolean busy;

        private ClientConnectionState(boolean busy) {
            this.busy = busy;
        }

        public boolean isBusy() {
            return busy;
        }
    }

    public enum ConnectionEvent implements EventTypeEnum {

        //
        JAVA_FILE_VERSION("Local java files of the game are not compatible with server.", String.class, Exception.class),
        //
        MARSHAL("Connection failed. (Check version compatibility or IP change.)", String.class, Exception.class),
        //
        NO_ROUTE("Connection failed (No route to Host)...", String.class, Exception.class),
        //
        NO_INTERNET("Connection failed (Unable to determine host adress)...", String.class, Exception.class),
        //
        NULL_POINTER("Null Pointer", String.class, Exception.class),
        //
        SERVER_DIFFERENT_SESSION_TYPE("Server running different Session Type!"),
        //
        SERVER_EXECUTION("Error during execution server side.", String.class, Exception.class),
        //
        SERVER_NO_SESSION("No project loaded on the server.", String.class, Exception.class),
        //
        SERVER_REFUSED("Server computer refused. No TyTech Server active?", String.class, Exception.class),
        //
        SERVER_TOKEN("Server running with different server/game token! Please restart.", String.class, Exception.class),
        //
        THREAD_INTERRUPT("Cannot run client command, thread was interrupted.", String.class, Exception.class),
        //

        FIRST_UPDATE_STARTED("Game is update is starting."),

        FIRST_UPDATE_FINISHED("Game is updated for the first time.", Boolean.class),
        //
        FIRST_UPDATE_EVENT_FINISHED("First update event is handled."),
        //
        TIMED_OUT("Connection timed out...", String.class, Exception.class),
        //
        UNKNOWN("Connection failed (Unknown)", String.class, Exception.class),
        //
        CONNECTION_ATTEMPT_RETRY("Former connection attempt failed, forcing retry.", Integer.class),

        CONNECTION_LATENCY("Connection latency.", Long.class, Double.class),

        THREAD_EXCECUTION("Failure during execution of thread.", String.class, Exception.class),

        RELEASED_FROM_SESSION("Stakeholder was released from the session.", String.class, Exception.class),

        CONNECTION_STATE_CHANGE("Connection changed from state.", Network.ClientConnectionState.class, Boolean.class),

        AUTHENTICATION_FAIL("Authentication at server failed.", String.class, Exception.class),

        SERVER_REBOOT("Server rebooted, restart app.", String.class, Exception.class);

        private final String details;

        private List<Class<?>> classes = null;

        private ConnectionEvent(final String details, Class<?>... classes) {
            this.details = details;
            this.classes = Arrays.asList(classes);
        }

        @Override
        public boolean canBePredefined() {
            return false;
        }

        @Override
        public List<Class<?>> getClasses() {
            return classes;
        }

        public String getDetails() {
            return this.details;
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

    public class PortSetting {
        public static int HTTP_PORT = 80;
        public static int SSL_PORT = 443;
    }

    /**
     * Defines the type of session. This can be local, WAN, LAN
     */
    public static enum SessionType {

        /**
         * A single user
         */
        SINGLE,
        /**
         * A Multi user session on a network (LAN or WAN)
         */
        MULTI,
        /**
         * A project launched in edit mode.
         */
        EDITOR;

        /**
         * When true a client is allowed to save a session.
         * @return
         */
        public boolean isClientSavable() {
            return this == SINGLE;
        }
    }

    /**
     * When a message starts with this String an exception was thrown server side.
     */
    public final static String JSON_EXCEPTION_HEADER = "EXCEPTION::";

    /**
     * Update frequency when connected to the server.
     */
    public final static int UPDATEFREQ = 500;

    /**
     * The port that runs the Tygron Engine Server broadcast channel.
     */
    public static final int BROADCAST_PORT = 4446;

    /**
     * Broadcast frequency of the server
     */
    public final static int BROADCASTFREQ = 1000;

    public static final String SERVER_BOOT_HEADER = "BootTime";

    public static final String CLIENT_NAME_HEADER = "ClientName";

    public static final String SERVER_TOKEN_HEADER = "ServerToken";

    public static final String CLIENT_TOKEN_HEADER = "ClientToken";

}
