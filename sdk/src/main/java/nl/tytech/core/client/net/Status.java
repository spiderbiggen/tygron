/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.client.net;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import nl.tytech.core.client.concurrent.ParallelUpdatable;
import nl.tytech.core.client.concurrent.SliceManager;
import nl.tytech.core.client.concurrent.UpdateManager;
import nl.tytech.core.client.event.EventManager;
import nl.tytech.core.client.event.EventManager.ItemManipulationEventType;
import nl.tytech.core.client.net.SlotConnection.Updater;
import nl.tytech.core.event.Event;
import nl.tytech.core.net.Lord;
import nl.tytech.core.net.Network;
import nl.tytech.core.net.Network.ConnectionEvent;
import nl.tytech.core.net.serializable.DeletedItem;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.net.serializable.UpdateResult;
import nl.tytech.core.structure.ClientItemMap;
import nl.tytech.core.structure.ClientItemMaps;
import nl.tytech.core.structure.DataLord;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.item.Moment;
import nl.tytech.data.core.item.SimTimeSetting;
import nl.tytech.util.logger.TLogger;

/**
 * Status
 * <p>
 * Status is a lord containing a lists on the client side. Status fires events when new updates of lists are received. Status is Thread safe
 * using the Priority lock.
 * <p>
 *
 *
 * @author Maxim Knepfle
 */
public class Status implements Lord, ParallelUpdatable {

    private final static float SIMTIME_UPDATE_PERIOD = 1;

    private Integer connectionID = Item.NONE;

    /**
     * ClientMap containing the local lists. These are identified with their Control class type.
     */
    protected ClientItemMaps maps;

    /**
     * Type of subscription (client, beamer, control).
     */
    protected Network.AppType appType;

    /**
     * Only do things when there is an update available.
     */
    private boolean updated = true;

    /**
     * First time fire always.
     */
    private boolean firstTime = true;

    /**
     * Status is allowed to fire events?
     */
    private volatile boolean fireEvents = true;

    /**
     * Local versions to be sent to the server for comparison. This is cached.
     */
    private HashMap<MapLink, Integer> versionRequest = null;

    /**
     * Status allows this gameType. After new connection is made status is reset.
     */
    private Network.SessionType sessionType = null;

    /**
     * Current Simulation time variables.
     */
    private volatile Long simTimeMillis = 0l;

    private volatile float updateTime = 0;

    private volatile long simTimeMultiplier = 0;

    private volatile long lastTimeUpdate = System.currentTimeMillis();

    private String projectName = null;

    /**
     * Construct a Status object.
     *
     * @param argSubscription Subscription type.
     * @param argLocal Local or networked (RMI) game.
     */
    protected <I extends Item> Status(final Network.AppType argSubscription) {

        appType = argSubscription;
        maps = new ClientItemMaps();
        UpdateManager.addParallel(this);
    }

    protected void deactivate() {
        fireEvents = false;
    }

    /**
     * Fire a list with the deleted ID's
     *
     * @param mapLink Type of list.
     * @param deleted items.
     */
    private <I extends Item> void fireListDeleteEvent(final MapLink mapLink, final I[] argDeletedList) {

        if (mapLink == null) {
            TLogger.severe("Cannot fire event for controller, no event enum constant is defined, maybe a server only control?");
            return;
        }

        // Retrieve the ID's and fire event, skip map reset item
        final List<Integer> deletedIDs = new ArrayList<Integer>();
        for (Item item : argDeletedList) {
            if (item.getID().equals(DeletedItem.MAP_RESET)) {
                TLogger.info("Received a map reset for: " + mapLink);
            } else {
                deletedIDs.add(item.getID());
            }
        }
        if (deletedIDs.size() == 0) {
            return;
        }
        // fire using manager
        if (fireEvents) {
            EventManager.fire(connectionID, ItemManipulationEventType.DELETE_ITEMS, this, mapLink, deletedIDs);
        }
    }

    private <I extends Item> void fireListUpdateEvent(final MapLink type, final I[] argUpdatedList) {

        if (type == null) {
            TLogger.severe("Cannot fire event for controller no event enum constant is defined, maybe a server only control?");
            return;
        }

        final List<I> updatedList = argUpdatedList == null ? new ArrayList<I>() : Arrays.asList(argUpdatedList);

        // fire using manager
        if (fireEvents) {
            EventManager.fire(connectionID, type, this, getMap(type), updatedList);
        }
    }

    /**
     * @return the Subscription
     */
    public Network.AppType getAppType() {
        return appType;
    }

    @Override
    public <I extends Item> ClientItemMap<I> getMap(MapLink type) {

        // check for null's
        if (type == null) {
            TLogger.severe("Status does not contain a item map named NULL.");
            return null;
        }

        ClientItemMap<I> map = maps.get(type);
        if (map == null) {
            TLogger.warning("Status does not (jet) contain a item map named " + type.name() + ".");
        }
        return map;
    }

    public String getProjectName() {
        return this.projectName;
    }

    @Override
    public Network.SessionType getSessionType() {
        if (sessionType == null) {
            TLogger.warning("Trying to access status SessionType, however no connection was made, please connect first.");
            return null;
        }
        return sessionType;
    }

    @Override
    public long getSimTimeMillis() {
        return simTimeMillis;
    }

    /**
     * ONLY the Updater thread may call this method! Get the version request with the total version and per list version.
     *
     * @return Version request.
     */
    protected final HashMap<MapLink, Integer> getVersionRequest() {

        if (!(Thread.currentThread() instanceof Updater)) {
            TLogger.severe("Only the updater thread may call this method!");
            return null;
        }

        if (updated && DataLord.getAppTypes(sessionType, appType).length > 0) {
            updated = false;
            versionRequest = new HashMap<>();
            MapLink[] mapLinks = DataLord.getAppTypes(sessionType, appType);
            for (int i = 0; i < mapLinks.length; i++) {
                versionRequest.put(mapLinks[i], maps.getVersion(mapLinks[i]));
            }
        }
        return versionRequest;
    }

    private void interpolateSimTime() {

        synchronized (Status.this) {
            // interpolate the old simtime.
            long timeDifference = System.currentTimeMillis() - lastTimeUpdate;
            this.simTimeMillis += simTimeMultiplier * timeDifference;
            this.lastTimeUpdate = System.currentTimeMillis();
        }
    }

    public boolean isFirstUpdateFinished() {
        return !firstTime;
    }

    @Override
    public boolean isServerSide() {
        return false;
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTestRun() {
        // TODO: handle this properly, false for now
        return false;
    }

    private <I extends Item> void removeFromMap(final MapLink type, final I[] update) {

        if (update != null) {
            // create a new map to prevent concurrent modifications.
            ClientItemMap<Item> map = new ClientItemMap<>(maps.get(type));
            int version = 0;

            for (I item : update) {
                map.remove(item.getID());
                // version update
                if (version < item.getVersion()) {
                    version = item.getVersion();
                }
            }
            maps.put(type, map, version);
        }
    }

    /**
     * Session type is set from connection for this specific session.
     * @param sesionType
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void setSessionType(Network.SessionType sessionType, String projectName) {
        this.sessionType = sessionType;
        this.projectName = projectName;

        // read in maps.
        for (MapLink mapLink : DataLord.getAppTypes(sessionType, appType)) {
            this.maps.put(mapLink, new ClientItemMap());
        }
    }

    /**
     * Sync client simtime with servers simtime.
     * @param updateMoments
     */
    private void syncServerClientTime() {

        Moment moment = this.<Moment> getMap(MapLink.TIMES).get(Moment.CURRENT_POSTION);
        SimTimeSetting multSetting = this.<SimTimeSetting> getMap(MapLink.SIMTIME_SETTINGS).get(SimTimeSetting.Type.TIME_MULTIPLIER);
        SimTimeSetting pausedSetting = this.<SimTimeSetting> getMap(MapLink.SIMTIME_SETTINGS).get(SimTimeSetting.Type.PAUSED);

        synchronized (Status.this) {
            this.simTimeMillis = moment.getMillis();
            this.simTimeMultiplier = pausedSetting.getBooleanValue() ? 0 : multSetting.getLongValue();
            this.lastTimeUpdate = System.currentTimeMillis();
            this.updateTime = 0;
        }
    }

    /**
     * Update a specific list.
     *
     * @param type The list type to update.
     * @param update The updated items.
     */

    private void updateMap(final MapLink type, final Item[] update) {

        if (update != null) {
            // create a new map to prevent concurrent modifications.
            ClientItemMap<Item> map = new ClientItemMap<>(maps.get(type));
            int version = 0;
            for (Item item : update) {
                // add status
                item.setLord(this);
                map.put(item.getID(), item);

                // version update
                if (version < item.getVersion()) {
                    version = item.getVersion();
                }
            }
            maps.put(type, map, version);
        }
    }

    @Override
    public void updateParallel(float tpf) {

        updateTime += tpf;
        if (updateTime > SIMTIME_UPDATE_PERIOD) {
            interpolateSimTime();
            updateTime = 0;
        }
    }

    /**
     * ONLY the UpdaterThread may call this method!
     *
     * @param <C>
     * @param <I>
     * @param serverVersion
     */
    @SuppressWarnings("unchecked")
    protected synchronized <I extends Item> void updateVersions(final SlotConnection connection, final UpdateResult serverVersion) {

        this.connectionID = connection.getID();

        try {
            if (serverVersion != null) {
                updated = true;

                // update the lists
                MapLink[] mapLinks = DataLord.getAppTypes(sessionType, appType);

                for (Entry<String, Item[]> entry : serverVersion.getItems().entrySet()) {
                    MapLink mapLink = MapLink.valueOf(entry.getKey());
                    updateMap(mapLink, entry.getValue());
                }

                for (Entry<String, Item[]> entry : serverVersion.getDeletes().entrySet()) {
                    MapLink mapLink = MapLink.valueOf(entry.getKey());
                    removeFromMap(mapLink, entry.getValue());
                }

                // set my stakeholder and time first!
                for (Entry<String, Item[]> entry : serverVersion.getItems().entrySet()) {
                    MapLink mapLink = MapLink.valueOf(entry.getKey());
                    if (mapLink.equals(MapLink.STAKEHOLDERS)) {
                        connection.setMyStakeholder(entry.getValue());
                    } else if (mapLink.equals(MapLink.TIMES) || mapLink.equals(MapLink.SIMTIME_SETTINGS)) {
                        syncServerClientTime();
                    }
                }

                SliceManager.exec(() -> {
                    if (firstTime && fireEvents) {
                        EventManager.fire(connectionID, ConnectionEvent.FIRST_UPDATE_STARTED, this);
                    }

                    // long start = System.currentTimeMillis();
                    // fire events
                    for (int i = 0; i < mapLinks.length; i++) {
                        MapLink type = mapLinks[i];
                        Item[] update = serverVersion.getItems().get(type.name());
                        Item[] deleted = serverVersion.getDeletes().get(type.name());

                        // fire event when updated and when items are deleted
                        if (firstTime || update != null || deleted != null) {
                            fireListUpdateEvent(type, (I[]) update);
                        }
                        // for deletes also fire deleted item event
                        if (deleted != null) {
                            fireListDeleteEvent(type, (I[]) deleted);
                        }
                    }
                    firstTime = false;
                });
                // TLogger.info("Fired updates in " + (System.currentTimeMillis() - start) + " ms.");
            }
        } catch (RuntimeException e) {
            // this is run from a future, so there is no notification of runtime exceptions
            // this is added just to notify the developer something is wrong
            TLogger.exception(e);
            throw e;
        }
    }

    @Override
    public String validateEventItems(Event event) {
        // XXX: Maxim : must always be true, cannot do full validation client-side due to missing maps.
        return null;
    }
}
