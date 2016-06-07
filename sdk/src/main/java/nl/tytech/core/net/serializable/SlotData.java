/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.net.serializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.net.Network;
import nl.tytech.core.net.serializable.ClientData.ConnectionState;
import nl.tytech.util.ObjectUtils;

/**
 * Stores general data about the running game at a server slot. Can be used in e.g. the launch menu.
 * @author Maxim
 *
 */
public class SlotData implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -3326616812363816703L;

    private ArrayList<ClientData> clients;

    private Integer slotID;

    private String name;

    private Network.SessionType sessionType;

    public SlotData() {

    }

    public SlotData(Integer slotID, Network.SessionType sessionType, String name, List<ClientData> clients) {
        this.slotID = slotID;
        this.sessionType = sessionType;
        this.name = name;
        this.clients = ObjectUtils.toArrayList(clients);
    }

    public boolean areClientsActive() {
        return getNumberOfActiveClients() > 0;
    }

    public List<ClientData> getClients() {
        return clients;
    }

    public String getName() {
        return name;
    }

    public int getNumberOfActiveClients() {
        int i = 0;
        for (ClientData client : clients) {
            if (client.getConnectionState() == ConnectionState.CONNECTED) {
                i++;
            }
        }
        return i;
    }

    public Network.SessionType getSessionType() {
        return sessionType;
    }

    public Integer getSlotID() {
        return slotID;
    }

    @Override
    public String toString() {
        return name;
    }
}
