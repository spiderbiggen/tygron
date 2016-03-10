/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.client.net;

import java.io.InvalidClassException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import nl.tytech.core.client.concurrent.SliceManager;
import nl.tytech.core.client.event.EventManager;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.event.EventValidationUtils;
import nl.tytech.core.net.Network;
import nl.tytech.core.net.Network.AppType;
import nl.tytech.core.net.Network.ClientConnectionState;
import nl.tytech.core.net.Network.ConnectionEvent;
import nl.tytech.core.net.event.IOServiceEventType;
import nl.tytech.core.net.serializable.ClientData;
import nl.tytech.core.net.serializable.ClientData.ConnectionState;
import nl.tytech.core.net.serializable.JoinReply;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.net.serializable.UpdateResult;
import nl.tytech.core.structure.DataLord;
import nl.tytech.core.util.SettingsManager;
import nl.tytech.core.util.SettingsManager.RunMode;
import nl.tytech.data.core.item.CoreStakeholder;
import nl.tytech.data.core.item.Item;
import nl.tytech.locale.TLanguage;
import nl.tytech.util.ObjectUtils;
import nl.tytech.util.RestManager;
import nl.tytech.util.RestManager.Format;
import nl.tytech.util.RestManager.ResponseException;
import nl.tytech.util.RestUtils.BadRequestType;
import nl.tytech.util.StringUtils;
import nl.tytech.util.ThreadUtils;
import nl.tytech.util.concurrent.ThreadPriorities;
import nl.tytech.util.logger.TLogger;

/**
 * Connection
 * <p>
 * Base Client-Server connection class. Is is used by CommunicationManager.
 * <p>
 *
 *
 * @author Maxim Knepfle
 */
public class SlotConnection {

    public enum ComEvent implements EventTypeEnum {

        /**
         * Please connect to this server using the settings from settingsmanager
         */
        DIRECT_CONNECT(),

        COMMUNICATION_STAKEHOLDER_SET(CoreStakeholder.class),

        MAPLINKS_INITIALIZED(Network.SessionType.class, String.class, AppType.class);

        private List<Class<?>> classes = new ArrayList<Class<?>>();

        private ComEvent(Class<?>... c) {
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

    public enum Processing implements EventTypeEnum {

        START(EventTypeEnum.class), DONE;

        private final List<Class<?>> classes = new ArrayList<Class<?>>();

        private Processing(Class<?>... c) {

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

    public class Updater extends Thread {

        private Updater(int counter) {
            this.setName(SlotConnection.THREAD_NAME + "-" + counter);
            this.setPriority(ThreadPriorities.MEDIUM);
            this.setDaemon(true);
            updateStart = 0;
            updateEnd = 0;
        }

        @Override
        public final void run() {

            while (updater == this) {
                if (firstConnection) {
                    // set time diff between server and client.
                    updateServerTimeDiff();
                    // wait for first update
                    TLogger.info("Processing first item update...");
                    // feedback
                    EventManager.fire(LoadingEventType.TEXT, this, "Downloading Online Data", 15);
                    waitForUpdate();
                    // feedback
                    EventManager.fire(LoadingEventType.TEXT, this, "Processing Online Data", 25);
                    TLogger.info("Finished first item update!");

                    EventManager.fire(connectionID, ConnectionEvent.FIRST_UPDATE_FINISHED, this, true);

                    /**
                     * Both Long runner and parallel thread must be ready when this event is fired! NICE! ;-)
                     */
                    SliceManager.exec(() -> SliceManager.execLongRunner(new Runnable() {
                        @Override
                        public void run() {
                            EventManager.fire(connectionID, ConnectionEvent.FIRST_UPDATE_EVENT_FINISHED, this);
                        }
                    }));

                    firstConnection = false;
                } else {
                    // wait for update
                    waitForUpdate();
                    // yield thread to allow others, (not really required).
                    Thread.yield();
                }
            }
            TLogger.info("Killed " + this.getName() + " thread.");
        }

        private final boolean waitForUpdate() {

            if (status == null) {
                TLogger.severe("Cannot perform operation, initconnection is not started!");
                return false;
            }
            // default false
            boolean succes = false;
            UpdateResult serverVersion = null;
            // while command was not successful retry
            while (!succes && updater == this) {
                try {
                    HashMap<MapLink, Integer> request = status.getVersionRequest();

                    updateStart = System.currentTimeMillis();
                    serverVersion = RestManager.post(connectionTarget, "update/", null, request, UpdateResult.class, Format.ZIPTJSON);
                    updateEnd = System.currentTimeMillis();

                    // if (StringUtils.containsData(serverVersionString)) {
                    // totalMbReceived += (serverVersionString.length() / 2d) / (1024d * 1024d);
                    // }
                    // successful server update
                    succes = true;

                } catch (Exception exp) {
                    succes = updater == this ? handle(exp) : true;
                    try {
                        Thread.sleep(Network.UPDATEFREQ);
                    } catch (InterruptedException e) {
                    }
                }
            }
            if (updater == this && serverVersion != null && state == Network.ClientConnectionState.CONNECTED) {

                // set state connected, all is OK
                setState(Network.ClientConnectionState.CONNECTED, true);

                // calculate latency
                long serverTime = System.currentTimeMillis() - timeDiff;
                latency = serverTime - serverVersion.getTimeStamp();
                EventManager.fire(ConnectionEvent.CONNECTION_LATENCY, this, latency, getConnectionReceivedMB());

                // update local versions
                status.updateVersions(SlotConnection.this, serverVersion);
                return true;
            }
            return false;
        }
    }

    /**
     * Special thread that checks connection for freezing and server IP changes.
     * @author Maxim
     *
     */
    private class UpdaterChecker extends Thread {

        private UpdaterChecker(int connectionID) {
            this.setName("Client-" + UpdaterChecker.class.getSimpleName() + "-" + connectionID);
            this.setPriority(ThreadPriorities.MEDIUM);
            this.setDaemon(true);
        }

        @Override
        public final void run() {
            while (true) {
                if (state == Network.ClientConnectionState.CONNECTED) {
                    long updateDif = System.currentTimeMillis() - updateStart;
                    if (updateEnd > 0 && updateDif > ConnectionState.LOST.getMaxWaitingTime() && !state.isBusy()) {
                        setState(Network.ClientConnectionState.OFFLINE, false);
                    }
                }

                if (state == Network.ClientConnectionState.OFFLINE && !firstConnection) {
                    connect();
                }

                try {
                    Thread.sleep(Network.UPDATEFREQ);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    private final static String THREAD_NAME = "Client-" + Updater.class.getSimpleName();

    private CoreStakeholder myStakeholder = null;

    /**
     * Last time in MS the server was succesfully connected.
     */
    private volatile long updateStart = 0;

    private volatile long updateEnd = 0;

    /**
     * Time difference between Server and Client at connection. (used as reference).
     */
    private long timeDiff = 0;

    /**
     * the token used by the client to reconnect to an old session
     */
    private String clientToken = null;

    /**
     * A single worker thread executes the runnable commands.
     */
    private final ExecutorService commandQueue = Executors.newSingleThreadExecutor();

    /**
     * The state of this connection object.
     */
    private volatile Network.ClientConnectionState state = Network.ClientConnectionState.OFFLINE;

    /**
     * The ID of the connection.
     */
    private Integer connectionID;

    /**
     * Only true when the client has successfully not connected before to server.
     */
    private volatile boolean firstConnection = true;

    /**
     * The unique game slot ID. A server can run multiple game slots each identified with an ID.
     */
    private Integer serverSlotID;

    /**
     * The server's address, usualy an IP.
     */
    private String serverAddress;

    /**
     * the token used by the client to reconnect to an old session
     */
    private String serverToken = null;

    /**
     * Total data received via update version.
     */
    private double totalMbReceived = 0;

    /**
     * Clients receive a unique Session when connected to a server.
     */
    private ClientData client = null;

    /**
     * The status object keeps track of the local version of the items.
     */
    private Status status;

    private String connectionTarget = null;

    private long latency = 0;

    private volatile Updater updater = null;

    private int updaterThreadCounter = 0;

    public SlotConnection() {
        this(0);// default connection ID
    }

    protected SlotConnection(Integer connectionID) {

        this.connectionID = connectionID;

        // name the executing thread.
        Runnable exec = () -> Thread.currentThread().setName("Client-FireServerEvent");
        commandQueue.submit(exec);

        // start connection check
        UpdaterChecker checker = new UpdaterChecker(connectionID);
        checker.start();
    }

    /**
     * Init connection with the brain. Can be by rmi or local. Loop true is only used when reconnecting and thus private.
     *
     * @return succes of connection.
     */
    public final synchronized boolean connect() {

        // already connected?
        if (state != ClientConnectionState.OFFLINE) {
            TLogger.warning("Cannot run connect when connection is in state: " + state);
            return false;
        }
        killUpdater();
        setState(Network.ClientConnectionState.CONNECTING, true);

        // try it a few times
        int counter = 1;

        // keep on trying
        while (!innerConnect(counter)) {
            // start checking for other servers (IP change?)
            EventManager.fire(ConnectionEvent.CONNECTION_ATTEMPT_RETRY, this, counter);

            // try a few times
            if (counter >= 10) {
                TLogger.warning("Unable to connect.");
                setState(Network.ClientConnectionState.OFFLINE, false);
                return false;
            }
            counter++;
            // sleep for a sec.
            ThreadUtils.sleepInterruptible(1000);
        }
        // Notify controls we're connected
        setState(Network.ClientConnectionState.CONNECTED, true);
        startUpdater();
        return true;
    }

    public void disconnect(boolean keepServerAlive) {

        TLogger.info("Disconnecting from Server...");

        // disconnect updater thread
        killUpdater();
        setState(Network.ClientConnectionState.DISCONNECTING, true);

        Integer slotID = SettingsManager.getServerSlotID(connectionID);
        String clientToken = SettingsManager.getClientToken(connectionID);

        Boolean succes = ServicesManager.fireServiceEvent(IOServiceEventType.CLOSE_SESSION, slotID, clientToken, keepServerAlive);

        if (keepServerAlive) {
            TLogger.info("Fired close client session and keep server slot alive request.");
        } else if (Boolean.TRUE.equals(succes)) {
            TLogger.info("Closed client session on Server.");
        } else {
            TLogger.info("Tried to close my client session on Server however slot is already shutdown.");
        }

        this.firstConnection = true;
        this.serverToken = null;
        this.clientToken = null;
        SettingsManager.setServerToken(connectionID, StringUtils.randomToken());
        SettingsManager.setClientToken(connectionID, StringUtils.randomToken());

        // create new status
        Network.AppType oldSubscription = status.getAppType();
        status = new Status(oldSubscription);
        // give new status to event manager
        EventManager.setStatus(this.connectionID, status);

        setState(Network.ClientConnectionState.OFFLINE, true);
        TLogger.info("Disconnected!");
    }

    @SuppressWarnings("unchecked")
    public final <T> T fireServerEvent(final boolean wait, final EventTypeEnum type, final Object... arguments) {

        if (status == null) {
            TLogger.severe("Cannot perform operation, initconnection is not started!");
            return null;
        }
        Object[] params = ObjectUtils.deepCopy(arguments);

        if (!EventValidationUtils.validateEvent(type, params)) {
            if (SettingsManager.getRunMode() != RunMode.RELEASE) {
                TLogger.showstopper("Event failure, see error messages above!");
            }
            return null;
        }

        final Callable<Object> command = new Callable<Object>() {

            @Override
            public Object call() throws Exception {

                EventManager.fire(Processing.START, this, type);

                // create the event

                Object result = null;
                // default false
                boolean succes = false;
                // while command was not successful retry
                while (!succes) {
                    try {
                        result = RestManager.post(connectionTarget, "event/" + type.getClass().getSimpleName() + "/" + type.toString(),
                                null, params, type.getResponseClass(), Format.TJSON);

                        // successful server connection
                        setState(Network.ClientConnectionState.CONNECTED, true);
                        succes = true;
                    } catch (Exception exp) {
                        succes = handle(exp);
                    }
                }
                EventManager.fire(Processing.DONE, this);
                return result;
            }
        };

        try {
            if (wait) {
                /**
                 * This is temp construction but seems to fix the interrupt problem. When the executor thread is interrupted while it is
                 * waiting for another executor thread it should not return. By adding this thread it does not go to sleep mode but to
                 * blocked mode.
                 */
                // TODO: Maxim: Make the method so that it cannot fire events more then 1 time.
                boolean succes = false;
                Object result = null;
                while (!succes) {
                    try {
                        Thread.yield();
                        result = commandQueue.submit(command).get();
                        succes = true;
                    } catch (InterruptedException exp) {
                        // do nothing
                    } catch (Exception exp) {
                        TLogger.exception(exp);
                        succes = true;
                    }
                }
                return (T) result;
            }
            // execute the command and continue.
            commandQueue.submit(command);
        } catch (Exception exp) {
            TLogger.exception(exp);
            return null;
        }
        return null;
    }

    protected final ClientData getClientData() {
        return client;
    }

    /**
     * @return the clientToken
     */
    protected String getClientToken() {
        return clientToken;
    }

    protected long getConnectionLatency() {
        return latency;
    }

    protected double getConnectionReceivedMB() {
        return this.totalMbReceived;
    }

    protected Integer getID() {
        return connectionID;
    }

    protected CoreStakeholder getMyStakeholder() {
        return myStakeholder;
    }

    /**
     * @return the serverToken
     */
    protected final String getServerToken() {
        return this.serverToken;
    }

    /**
     * Handles server exceptions and fires them as events.
     *
     * @param exp
     */
    private boolean handle(Exception exp) {

        ConnectionEvent type = ConnectionEvent.UNKNOWN;
        // exception is fatal when there is no way to recover from this.
        boolean fatalException = false;
        boolean offline = false;
        // only server exception keep the connection alive.
        if (exp instanceof ResponseException) {
            ResponseException rexp = (ResponseException) exp;
            if (rexp.getStatusCode() == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
                type = ConnectionEvent.SERVER_EXECUTION;
                // server error also log!
                TLogger.severe(rexp.getMessage());

            } else if (rexp.getExceptionType() == BadRequestType.INVALID_SERVER_TOKEN) {
                type = ConnectionEvent.SERVER_TOKEN;
                fatalException = true;
            } else if (rexp.getExceptionType() == BadRequestType.CLIENT_RELEASED) {
                type = ConnectionEvent.RELEASED_FROM_SESSION;
                fatalException = true;
            } else if (rexp.getExceptionType() == BadRequestType.NO_SESSION_IN_SLOT) {
                type = ConnectionEvent.SERVER_NO_SESSION;
                fatalException = true;
            }
        } else {

            // display a message accroding to the error type.
            if (exp instanceof InvalidClassException) {
                type = ConnectionEvent.JAVA_FILE_VERSION;
            } else if (exp instanceof InterruptedException) {
                type = ConnectionEvent.THREAD_INTERRUPT;
            } else if (exp instanceof ExecutionException) {
                type = ConnectionEvent.THREAD_EXCECUTION;
            } else if (exp instanceof NullPointerException) {
                type = ConnectionEvent.NULL_POINTER;
                TLogger.exception(exp);
            } else if (exp instanceof ProcessingException) {
                type = ConnectionEvent.NO_ROUTE;
                offline = true;
            }
        }
        if ((fatalException || offline) && !state.isBusy()) {
            setState(Network.ClientConnectionState.OFFLINE, false);
        }
        // fire event and small log
        String details = type != null ? type.getDetails() : "Unknown";
        TLogger.warning(details + " -> " + exp);
        EventManager.fire(type, this, details, exp);

        return fatalException;
    }

    public final void initSettings(final Network.AppType appType, final String argServerAddress, final Integer slotID,
            final String serverToken, final String clientToken) {

        // create status
        this.status = new Status(appType);

        // give status to event manager
        EventManager.setStatus(this.connectionID, status);

        // set the address and gameid
        this.serverAddress = argServerAddress;
        this.serverSlotID = slotID;
        this.clientToken = clientToken;
        this.serverToken = serverToken;

        /**
         * Create target URL
         */
        this.connectionTarget = "https://" + serverAddress
                + (Network.PortSetting.SSL_PORT == 443 ? "" : ":" + Network.PortSetting.SSL_PORT) + "/api/slots/" + serverSlotID + "/";

    }

    /**
     * Init connection with the brain. Can be by rmi or local
     *
     * @return succes of connection.
     */
    private boolean innerConnect(int counter) {

        try {

            // feedback
            EventManager.fire(LoadingEventType.TEXT, this, "Connecting to Server Simulation", 14);
            TLogger.info("Connecting to Server Slot: " + this.serverSlotID + " (Attempt " + counter + ")");

            String clientAddress = "Unknown";
            String clientName = "Unknown";
            try {
                // get the address of this host.
                clientAddress = InetAddress.getLocalHost().getHostAddress();
                clientName = InetAddress.getLocalHost().getHostName();
            } catch (Exception exp) {
                TLogger.exception(exp);
            }

            JoinReply reply = ServicesManager.fireServiceEvent(IOServiceEventType.JOIN_SESSION, serverSlotID, status.getAppType(),
                    clientAddress, clientName, clientToken);

            if (reply == null) {
                return false;
            }

            client = reply.client;
            clientToken = reply.client.getClientToken();
            serverToken = reply.serverToken;

            /**
             * Set headers for this connection target
             */
            MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
            headers.putSingle(Network.CLIENT_TOKEN_HEADER, clientToken);
            headers.putSingle(Network.SERVER_TOKEN_HEADER, serverToken);

            RestManager.setHeaders(this.connectionTarget, headers);

            /**
             * Setup client
             */
            if (firstConnection) {
                Map<Network.SessionType, MapLink[]> mapList = new HashMap<>();
                mapList.put(reply.sessionType, reply.lists);
                DataLord.setup(mapList);

                // add settings after successful first connect
                SettingsManager.setProjectName(reply.project);
                SettingsManager.setLanguage(reply.languague);
                SettingsManager.setClientToken(connectionID, clientToken);
                SettingsManager.setServerToken(connectionID, serverToken);
                SettingsManager.setServerSlotID(connectionID, serverSlotID);

                // set gameType for this game/connection
                status.setSessionType(reply.sessionType, reply.project);
                TLogger.info("Client MapLinks for: " + reply.sessionType + ": " + status.getAppType() + ": " + reply.project + ": "
                        + DataLord.getAppTypes(reply.sessionType, status.getAppType()).length);
                EventManager.fire(connectionID, ComEvent.MAPLINKS_INITIALIZED, this, reply.sessionType, reply.project, status.getAppType());
            }

        } catch (Exception exp) {
            handle(exp);
            return false;
        }
        TLogger.info("Connected with client token: " + clientToken + " and server token: " + serverToken + ".");
        return true;
    }

    public boolean isConnected() {
        return state == Network.ClientConnectionState.CONNECTED;
    }

    /**
     * @return the connected
     */
    public final boolean isConnectedOrConnecting() {
        return state == Network.ClientConnectionState.CONNECTED || state == Network.ClientConnectionState.CONNECTING;
    }

    private void killUpdater() {
        // zombiefy old thread
        if (updater != null) {
            Updater zombie = this.updater;
            zombie.setName(zombie.getName() + "-Zombie");
            updater = null;
            zombie.interrupt();
            updateStart = 0;
            updateEnd = 0;
        }
    }

    protected void setMyStakeholder(final Item[] items) {

        if (!StringUtils.containsData(clientToken)) {
            return;
        }

        for (Item item : items) {
            CoreStakeholder stakeholder = (CoreStakeholder) item;
            if (clientToken.equals(stakeholder.getClientToken())) {
                if (myStakeholder == null || !myStakeholder.getID().equals(stakeholder.getID())) {
                    this.myStakeholder = stakeholder;
                    EventManager.fire(ComEvent.COMMUNICATION_STAKEHOLDER_SET, this, myStakeholder);
                } else {
                    this.myStakeholder = stakeholder;
                }
            }
        }
    }

    /**
     * Set connection to a certain state.
     */
    private final void setState(Network.ClientConnectionState argState, boolean onPurpose) {

        if (argState != state) {
            // first set to connected flag
            state = argState;
            EventManager.fire(connectionID, ConnectionEvent.CONNECTION_STATE_CHANGE, this, state, onPurpose);
        }
    }

    protected Integer startOrLoadSessionOnServer(final Network.SessionType sessionType, final String projectName, final String sessionName,
            String groupToken, final String saveName, final TLanguage language, final Integer triggerBundleID) {

        // only setup when game is not already connected.
        if (state != ClientConnectionState.OFFLINE) {
            TLogger.severe("Cannot run setup when connection is in state: " + state);
            return Item.NONE;
        }

        Integer slotID;
        if (StringUtils.containsData(sessionName) && StringUtils.containsData(saveName)) {
            slotID = (Integer) ServicesManager.fireServiceEvent(IOServiceEventType.LOAD_SAVED_SESSION, sessionType, projectName,
                    sessionName, saveName, groupToken);
        } else {
            slotID = (Integer) ServicesManager.fireServiceEvent(IOServiceEventType.START_NEW_SESSION, sessionType, projectName, language,
                    triggerBundleID, groupToken);
        }
        SettingsManager.setServerSlotID(this.connectionID, slotID);
        return slotID;
    }

    private void startUpdater() {
        // start updating the client
        updater = new Updater(updaterThreadCounter);
        updaterThreadCounter++;
        updater.start();
    }

    private void updateServerTimeDiff() {
        try {
            // calc value
            long serverTime = ServicesManager.fireServiceEvent(IOServiceEventType.GET_SERVER_TIME);
            this.timeDiff = (System.currentTimeMillis() - serverTime);
        } catch (Exception exp) {
            TLogger.exception(exp);
        }
    }
}
