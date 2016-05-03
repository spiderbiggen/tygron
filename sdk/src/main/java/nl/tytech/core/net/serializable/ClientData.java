/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.net.serializable;

import java.io.Serializable;
import nl.tytech.core.net.Network;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;
import nl.tytech.util.logger.TLogger;

/**
 * Client data
 * <p>
 * THREAD SAFE: This class keeps track of a client computer connected to one of the games.
 * <p>
 *
 *
 * @author Maxim Knepfle
 */
public class ClientData implements Serializable {

    /**
     * The state of connection of the client belonging to this stakeholder.
     */
    public enum ConnectionState {

        /** normal heartbeat received */
        CONNECTED(TColor.GREEN, "Connected", 0),
        /** if the heartbeat is longer than failing but less then lost */
        FAILING(TColor.YELLOW, "Failing...", (Network.UPDATEFREQ * 40)),
        /** client heartbeat took longer than timeout */
        LOST(TColor.RED, "Lost Connection!", (Network.UPDATEFREQ * 60)),
        /** client is released from game */
        RELEASED(TColor.BLACK, "No Client", 0);

        private TColor color;

        private String label;

        private int timeout;

        private int maxWait;

        private ConnectionState(TColor color, String label, int timeout) {

            this.color = color;
            this.label = label;
            this.timeout = timeout;
            // set to 80% of failing
            this.maxWait = (int) (timeout * 0.8d);
        }

        public TColor getColor() {

            return color;
        }

        public String getLabel() {

            return label;
        }

        public int getMaxWaitingTime() {

            return maxWait;
        }

        public int getTimeout() {

            return timeout;
        }
    }

    /**
     *
     */
    private static final long serialVersionUID = -1861382127096953800L;

    private String address;

    private String computerName;

    private String fullName;

    private String userName;

    private Network.AppType subscription;

    private String clientToken;

    /**
     * THREAD SAFE: Last time in ms since 1970 the client belonging to this stakeholder connected to the server.
     */
    private volatile long lastSeen = 0;

    /**
     * THREAD SAFE:
     */
    private volatile boolean updated = false;

    /**
     * THREAD SAFE:The state of connection of the client belonging to this stakeholder.
     */
    private volatile ConnectionState connectionState = ConnectionState.CONNECTED;

    /**
     * THREAD SAFE:
     */
    private volatile int serverEventCounter = 0;

    public ClientData() {

    }

    /**
     * @param clientType
     * @param clientAddress
     * @param clientName
     * @param id
     * @param clientToken
     */
    public ClientData(Network.AppType clientType, String userName, String fullName, final String clientAddress, final String clientName) {

        this.subscription = clientType;
        this.address = clientAddress;
        this.computerName = clientName;
        this.userName = userName;
        this.lastSeen = System.currentTimeMillis();
        this.clientToken = StringUtils.randomToken();
        this.fullName = fullName;

        // log
        TLogger.info("Client started: " + this.toString());
    }

    public void connect() {
        synchronized (this) {
            this.connectionState = ConnectionState.CONNECTED;
        }
    }

    /**
     * Get the IP address of the client.
     *
     * @return
     */
    public final String getAddress() {
        return address;
    }

    /**
     * @return the clientType
     */
    public final Network.AppType getAppType() {
        return subscription;
    }

    /**
     * @return the token
     */
    public final String getClientToken() {
        return clientToken;
    }

    /**
     * @return the commands
     */
    public final int getCommands() {
        return this.serverEventCounter;
    }

    /**
     * Get the name of the client computer.
     *
     * @return
     */
    public final String getComputerName() {
        return computerName;
    }

    /**
     * @return the connectionState
     */
    public final ConnectionState getConnectionState() {
        return this.connectionState;
    }

    public String getFullName() {
        return fullName;
    }

    /**
     * THREAD SAFE: Get last seen time
     * @return
     */
    public long getLastSeen() {
        return lastSeen;
    }

    public String getUserName() {
        return userName;
    }

    /**
     * @return the updated
     */
    public final boolean isUpdated() {
        return this.updated;
    }

    /**
     * Ping is called each time the client connects. This shows the facilitator if the client is still alive.
     *
     * @param clientID
     * @param command
     */
    public final void ping(final boolean command) {
        synchronized (this) {
            // update last seen, do not always update!
            lastSeen = System.currentTimeMillis();
            // when a command was executed log it.
            if (command) {
                serverEventCounter++;
            }
        }
    }

    public void release() {
        synchronized (this) {
            this.connectionState = ConnectionState.RELEASED;
        }
    }

    public boolean releaseCheck() {
        synchronized (this) {
            if (this.connectionState == ConnectionState.RELEASED) {
                return true;
            }
            return false;
        }
    }

    /**
     * @param updated the updated to set
     */
    public final void setUpdated(boolean updated) {
        synchronized (this) {
            this.updated = updated;
        }
    }

    @Override
    public final String toString() {
        return getAppType().toString() + "-" + " (token: " + getClientToken() + ", name: " + getComputerName() + ", address: "
                + getAddress() + ")";
    }

    public final void update(final String receivedName, final String receivedAddress) {
        synchronized (this) {
            if (!receivedAddress.equals(address)) {
                TLogger.warning("IP address change from " + this.address + " to " + receivedAddress + " for client " + getClientToken()
                        + ".");
                this.address = receivedAddress;
            }
            if (!receivedName.equals(computerName)) {
                TLogger.warning("Computer name change from " + this.computerName + " to " + receivedName + " for client "
                        + getClientToken() + ".");
                this.computerName = receivedName;
            }
            this.connectionState = ConnectionState.CONNECTED;
        }
    }

    public final void updateState() {
        synchronized (this) {
            // check last update time.
            long now = System.currentTimeMillis();
            switch (connectionState) {
                case RELEASED:
                    break;
                case CONNECTED:
                    if ((now - lastSeen) > ConnectionState.FAILING.getTimeout()) {
                        // not active?
                        connectionState = ConnectionState.FAILING;
                        updated = true;
                        TLogger.info("Client connection failing: " + this.toString());
                    }
                    break;
                case FAILING:
                    if ((now - lastSeen) < ConnectionState.FAILING.getTimeout()) {
                        // active?
                        connectionState = ConnectionState.CONNECTED;
                        updated = true;
                        TLogger.info("Client connection restored: " + this.toString());
                    } else if ((now - lastSeen) > ConnectionState.LOST.getTimeout()) {
                        // lost?
                        connectionState = ConnectionState.LOST;
                        updated = true;
                        TLogger.info("Client connection lost: " + this.toString());
                    }
                    break;
                case LOST:
                    if ((now - lastSeen) < ConnectionState.FAILING.getTimeout()) {
                        // active?
                        connectionState = ConnectionState.CONNECTED;
                        updated = true;
                        // log
                        TLogger.info("Client connection restored: " + this.toString());
                    }
                    break;
            }
        }
    }
}
