/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.core.item;

import nl.tytech.core.item.annotations.Html;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.ClientData;
import nl.tytech.core.net.serializable.ClientData.ConnectionState;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;

/**
 * Stakeholder
 * <p>
 * This class keeps track of the stakeholder. The stakeholder is played by a participant, identified over the network with a client ID.
 * <p>
 *
 *
 * @author Maxim Knepfle
 */
public class CoreStakeholder extends UniqueNamedItem {

    /**
     * Serial
     */
    private static final long serialVersionUID = 178858230577513380L;

    @Html
    @XMLValue
    private String description = "<html><body>No Description</body></html>";

    @XMLValue
    private boolean human = false;

    @XMLValue
    private TColor color = new TColor(255, 255, 255);

    /**
     * The network session belonging to this stakeholder.
     */
    private ClientData client;

    /**
     * Empty constructor.
     */
    public CoreStakeholder() {

    }

    /**
     * @return the session
     */
    public final ClientData getClient() {
        return this.client;
    }

    /**
     * @return the clientAddress
     */
    public final String getClientAddress() {

        if (client == null) {
            return StringUtils.EMPTY;
        }
        return client.getAddress();
    }

    public final String getClientComputerName() {

        if (client == null) {
            return "-";
        }
        return client.getComputerName();
    }

    /**
     * Returns the ID of the client belonging to this stakeholder.
     *
     * @return
     */
    public final String getClientToken() {

        if (client == null) {
            return null;
        }
        return client.getClientToken();
    }

    /**
     * @return the color
     */
    public final TColor getColor() {

        return this.color;
    }

    /**
     * Return the amount of command executed by this stakeholder. E.g. build building, pay money etc.
     *
     * @return the amount of commands
     */
    public final int getCommandsExecuted() {

        if (client == null) {
            return 0;
        }
        return client.getCommands();
    }

    /**
     * @return the state of the connection with the client playing this stakeholder.
     */
    public final ConnectionState getConnectionState() {

        if (client == null) {
            return ConnectionState.RELEASED;
        }

        return client.getConnectionState();
    }

    @Override
    public final String getDescription() {
        return description;
    }

    public boolean isPlayable() {
        return human;
    }

    /**
     * Release the client currently belonging to this stakeholder.
     */
    public final void releaseSession() {

        if (this.client != null) {
            this.client.release();
        }
        this.client = null;
    }

    /**
     * @param color
     */
    public void setColor(TColor color) {
        this.color = color;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }

    public void setPlayable(boolean playable) {
        this.human = playable;
    }

    public final void setSession(ClientData session) {
        this.client = session;
    }

    @Override
    public final String toString() {

        if (!StringUtils.containsData(getName())) {
            return "Stakeholder " + this.getID();
        }
        return getName();
    }
}
