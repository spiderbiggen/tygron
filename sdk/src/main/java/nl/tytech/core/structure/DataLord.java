/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.structure;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.net.Network;
import nl.tytech.core.net.Network.AppType;
import nl.tytech.core.net.Network.SessionType;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.util.JsonEventUtils;
import nl.tytech.util.PackageUtils;
import nl.tytech.util.logger.TLogger;

/**
 * OverLord
 * <p>
 * OverLord defines the controllers, events, file locations etc of the game.
 * <p>
 *
 *
 * @author Maxim Knepfle
 * @param <L>
 */

public class DataLord {

    private static class SingletonHolder {
        private final static DataLord INSTANCE = new DataLord();
    }

    public enum Space {

        CORE,

        ENGINE,

        EDITOR
    }

    /**
     * Default platform specific location of the game item classes.
     */
    private final static String PLATFORM_ITEM_LOCATION = "nl.tytech.data.SPACE.item";

    /**
     * Default platform specific location of the game Serializable classes.
     */
    private final static String PLATFORM_SERIALIZABLE_LOCATION = "nl.tytech.data.SPACE.serializable";

    /**
     * Default platform specific location of the game server-side events
     */

    private final static String PLATFORM_EVENT_LOCATION = "nl.tytech.data.SPACE.event";

    /**
     * Used for internal network synchronization only. Do not call unless you know what you are doing.
     * @return
     */
    public final static List<MapLink> getAllTypes() {
        return SingletonHolder.INSTANCE.allPossibleTypes;
    }

    public final static MapLink[] getAppTypes(SessionType sessionType, AppType appType) {
        return SingletonHolder.INSTANCE._getAppTypes(sessionType, appType);
    }

    @SuppressWarnings("unchecked")
    public final static List<Class<? extends EventTypeEnum>> getEventClasses() {

        List<Class<? extends EventTypeEnum>> classes = new ArrayList<>();
        for (Space space : Space.values()) {
            List<String> classNames = PackageUtils.getPackageClassNames(PLATFORM_EVENT_LOCATION.replaceAll("SPACE", space.name()
                    .toLowerCase()));
            for (String className : classNames) {
                try {
                    classes.add((Class<? extends EventTypeEnum>) Class.forName(className));
                } catch (Exception e) {
                    TLogger.exception(e);
                }
            }
        }
        return classes;
    }

    public final static MapLink[] getSessionTypes(SessionType sessionType) {
        return SingletonHolder.INSTANCE._getSessionTypes(sessionType);
    }

    public final static MapLink getType(EventIDField eventIDField, int parameterIndex) {

        if (eventIDField == null) {
            return null;
        }

        for (int i = 0; i < eventIDField.params().length; i++) {
            int value = eventIDField.params()[i];
            if (value == parameterIndex) {
                String linkName = eventIDField.links()[i];
                return MapLink.valueOf(linkName);
            }
        }
        return null;
    }

    /**
     * Returns true when maplink is active for this given game and type.
     * @param gameType
     * @param gameName
     * @param type
     * @return
     */
    public final static boolean isMapLinkActive(Network.SessionType sessionType, AppType appType, MapLink type) {

        MapLink[] links = getAppTypes(sessionType, appType);
        for (MapLink link : links) {
            if (link == type) {
                return true;
            }
        }
        return false;
    }

    /**
     * Server only means that the controller does not fire events to client computers (beamer, facilitator, client). Thus no need to define
     * the update event constant for this class.
     *
     * @param <C>
     * @param <I>
     * @param type
     * @return
     */
    public final static boolean serverOnly(MapLink type) {

        for (Network.AppType appType : Network.AppType.values()) {
            if (type.isValidForAppType(appType)) {
                return false;
            }
        }
        return true;
    }

    public static void setup(Map<Network.SessionType, MapLink[]> mapLinks) {
        SingletonHolder.INSTANCE._setup(mapLinks);
    }

    /**
     * The gameName defines the game type: e.g. simport or watergame.
     */
    private boolean setupNamespace = false;

    /**
     * Do not try to setup the Lord simultaneous you plebs!
     */
    private final Object setupLock = new Object();

    private final List<MapLink> allPossibleTypes = new ArrayList<MapLink>();

    private final Map<SessionType, MapLink[]> sessionTypeMap = new EnumMap<>(SessionType.class);

    private final Map<SessionType, EnumMap<AppType, MapLink[]>> appTypeMap = new EnumMap<>(SessionType.class);

    /**
     * The constructor.
     */
    private DataLord() {

    }

    private final MapLink[] _getAppTypes(SessionType sessionType, AppType appType) {

        if (sessionType == null || appType == null) {
            TLogger.severe("Missing info for requesting map types!");
            return null;
        }
        return appTypeMap.get(sessionType).get(appType);
    }

    private final MapLink[] _getSessionTypes(SessionType sessionType) {

        if (sessionType == null) {
            TLogger.severe("Missing info for requesting map types!");
            return null;
        }
        return sessionTypeMap.get(sessionType);
    }

    /**
     * Setup the Overlord to use the specified gameName as package location. The location must now be like "nl/tygron/" + gameName +
     * "/server/brain/control" for the control and "nl/tygron/" + gameName + "/general/item" for the item. Note: This method only runs when
     * the gameName is not set. It is no problem to call it multiple times, only not with a different gameName.
     *
     * @param argGameName
     */
    private void _setup(Map<Network.SessionType, MapLink[]> mapLinks) {

        synchronized (setupLock) {

            // get session type mapLinks
            for (Entry<Network.SessionType, MapLink[]> entry : mapLinks.entrySet()) {

                // only add when sessionType when it is unknown to me
                if (!sessionTypeMap.containsKey(entry.getKey())) {

                    // store in sessions
                    sessionTypeMap.put(entry.getKey(), entry.getValue());

                    // store in ALL types
                    for (MapLink mapLink : entry.getValue()) {
                        // also save all possible variants.
                        if (!allPossibleTypes.contains(mapLink)) {
                            this.allPossibleTypes.add(mapLink);
                        }
                    }

                    // store per session/app type
                    EnumMap<Network.AppType, MapLink[]> appTypeMap = new EnumMap<>(Network.AppType.class);
                    for (AppType appType : AppType.values()) {
                        List<MapLink> newList = new ArrayList<>();
                        MapLink[] oldArray = entry.getValue();
                        for (MapLink link : oldArray) {
                            if (link.isValidForAppType(appType)) {
                                newList.add(link);
                            }
                        }
                        // TLogger.info("Loaded: " + entry.getKey() + ": " + appType + ": " + newList.size());
                        appTypeMap.put(appType, newList.toArray(new MapLink[newList.size()]));
                    }
                    this.appTypeMap.put(entry.getKey(), appTypeMap);
                }
            }

            if (!setupNamespace) {
                // first time, setup game name
                setupNamespace = true;

                for (Space space : Space.values()) {
                    if (!ItemNamespace.addPackageClasses(PLATFORM_EVENT_LOCATION.replaceAll("SPACE", space.name().toLowerCase()))) {
                        TLogger.warning("A problem occurred while loading event classes.");
                    }
                    if (!ItemNamespace.addPackageClasses(PLATFORM_SERIALIZABLE_LOCATION.replaceAll("SPACE", space.name().toLowerCase()))) {
                        TLogger.warning("A problem occurred while loading serializable classes.");
                    }
                    if (!ItemNamespace.addPackageClasses(PLATFORM_ITEM_LOCATION.replaceAll("SPACE", space.name().toLowerCase()))) {
                        TLogger.warning("A problem occurred while loading item classes.");
                    }
                }
                // XXX (Frank) Not the ideal solution yet!!
                JsonEventUtils.init();
            }
        }
    }
}
