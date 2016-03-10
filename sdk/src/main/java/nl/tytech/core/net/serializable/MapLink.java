/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.net.serializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.net.Network;
import nl.tytech.core.net.Network.SessionType;
import nl.tytech.core.structure.DataLord;
import nl.tytech.util.StringUtils;

/**
 * MapLink
 * <p>
 * Connects the items with each other.
 * <p>
 *
 *
 * @author Maxim Knepfle
 */
public enum MapLink implements EventTypeEnum {

    /**
     * Subscriptions: LAUNCHER, FACILITATOR, BEAM, CLIENT, EDITOR, SERVER, TOOLS
     */
    ACHIEVEMENTS(false, false, false),

    ACTION_MENUS(false, false, false),

    BEHAVIOR_TERRAINS(false, false),

    BUILDINGS(false, false),

    CHAT_MESSAGES(false, false, false),

    CINEMATIC_DATAS(false, false, false),

    CLIENT_EVENTS(false, false),

    CLIENT_WORDS(false),

    CONTRIBUTORS(false, false),

    GLOBALS(false, false),

    COSTS(false, false, false),

    DEFAULT_WORDS(false, false, false, false, false),

    DIKES(false, false),

    DYNAMIC_WATERSYSTEM(false, false, false),

    ECONOMIES(false),

    EVENT_BUNDLES(false),

    FUNCTION_OVERRIDES(false, false),

    FUNCTIONS(false, false),

    LEVELS(false),

    GEO_LINKS(false, false, false, false, false),

    HEIGHTS(false, false),

    INCOMES(false, false, false),

    INDICATORS(false),

    LANDS(false, false),

    LOANS(false),

    LOGS(false, false, false, false, false),

    MEASURES(false),

    MESSAGES(false),

    MODEL_DATAS(false, false),

    MODEL_SETS(false, false),

    MONEY_TRANSFERS(false),

    OVERLAYS(false),

    PANELS(false, false),

    PARTICLE_EMITTERS(false, false),

    PIPES(false, false),

    PIPE_CLUSTERS(false, false),

    PIPE_DEFINITIONS(false, false),

    PIPE_JUNCTIONS(false, false),

    PIPE_LOADS(false, false),

    PIPE_LOAD_SNAP_SHOTS(false, false),

    PIPE_SETTINGS(false),

    POPUPS(false, false, false),

    PREDICTIONS(false, false, false),

    PROGRESS(),

    QUALITATIVE_FUNCTION_SCORES(false, false),

    SEASONS(false, false, false),

    SERVER_WORDS(false, false, false, false, false),

    SETTINGS(),

    SIMTIME_SETTINGS(false),

    SOUNDS(false, false, false),

    SPECIAL_EFFECTS(false, false),

    SPECIAL_OPTIONS(false, false, false),

    STAKEHOLDERS(false),

    STRATEGIES(false, false, false, false, false),

    AREAS(false, false),

    TIMES(false),

    UNIT_DATA_OVERRIDES(false, false, false),

    UNIT_DATAS(false, false, false),

    UPGRADE_TYPES(false, false),

    VIDEOS(false, false),

    WEATHERS(false, false, false),

    ZONES(false, false),

    ZOOMLEVELS(false, false, false),

    ZIP_CODES(false, false),

    CHAIN_ELEMENTS("productiononly"),

    PRODUCT_STORAGES("productiononly"),

    PRODUCTS("productiononly"),

    TAX_PLANS("productiononly"),

    UNITS("productiononly"),

    ;

    private static List<Class<?>> classes = new ArrayList<Class<?>>();
    /**
     * This position of the event contains the ENTIRE collection of the server control.
     */
    public final static int COMPLETE_COLLECTION = 0;
    /**
     * This position of the event contains only the UPDATED collection of the server control.
     */
    public final static int UPDATED_COLLECTION = 1;

    public final static MapLink[] VALUES = MapLink.values();

    static {
        classes.add(Collection.class);
        classes.add(Collection.class);
    }

    public static MapLink valueOfString(final SessionType sessionType, final String string) {

        if (sessionType == null) {
            return null;
        }

        /**
         * Only use mapLinks valid for this session
         */
        MapLink[] validmapLinks = DataLord.getSessionTypes(sessionType);

        /**
         * First test normal String name compare
         */
        for (MapLink mapLink : validmapLinks) {
            if (mapLink.name().equalsIgnoreCase(string)) {
                return mapLink;
            }
        }

        /**
         * Second test human readable string
         */
        for (MapLink mapLink : validmapLinks) {
            if (StringUtils.capitalizeWithUnderScores(mapLink.name()).equalsIgnoreCase(string)) {
                return mapLink;
            }
        }

        // default
        return null;
    }

    private String limitToProjectName = null;

    private final boolean[] subscriptions = new boolean[Network.AppType.values().length];

    private MapLink(boolean... argSubscriptions) {
        this(null, argSubscriptions);
    }

    private MapLink(String limitToProjectName, boolean... argSubscriptions) {

        this.limitToProjectName = limitToProjectName;

        for (int i = 0; i < subscriptions.length; i++) {
            if (i < argSubscriptions.length) {
                subscriptions[i] = argSubscriptions[i];
            } else {
                subscriptions[i] = true;
            }
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

    public boolean isValidForAppType(Network.AppType type) {
        return subscriptions[type.ordinal()];
    }

    public boolean mapLinkActiveForName(String gameName) {
        return this.limitToProjectName == null || limitToProjectName.equals(gameName);
    }
}
