/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.structure;

import java.util.Collection;
import java.util.HashMap;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;

/**
 * ClientItemMaps
 * <p>
 * Map containing the items lists and their versions on the client side.
 * <p>
 *
 *
 * @author Maxim Knepfle
 */
public final class ClientItemMaps {

    /**
     * Decorated internal Map.
     */
    private final HashMap<MapLink, ClientItemMap<? extends Item>> map = new HashMap<>();

    /**
     * Local version identified by their type.
     */
    private final HashMap<MapLink, Integer> versions = new HashMap<>();

    /**
     * Get the ItemMap instance associated with the given control class.
     *
     * @param <C>
     * @param <I>
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
    public final <I extends Item> ClientItemMap<I> get(MapLink key) {
        return (ClientItemMap<I>) map.get(key);
    }

    /**
     * Get the version of the controller.
     *
     * @param <C> Type of controller
     * @param key Controller class
     * @return version
     */
    public final int getVersion(MapLink key) {
        if (!versions.containsKey(key)) {
            versions.put(key, 0);
        }
        return versions.get(key);
    }

    public final Collection<MapLink> keySet() {
        return map.keySet();
    }

    /**
     * Save the ItemMap using the Control class as key.
     *
     * @param <C>
     * @param <I>
     * @param key
     * @param value
     * @return
     */
    public final <I extends Item> ClientItemMap<I> put(MapLink key, ClientItemMap<I> value) {
        return put(key, value, 0);
    }

    @SuppressWarnings("unchecked")
    public final <I extends Item> ClientItemMap<I> put(MapLink key, ClientItemMap<I> value, int version) {
        // set versions
        versions.put(key, version);
        return (ClientItemMap<I>) map.put(key, value);
    }

    /**
     * Set the version of a controller defined by the type.
     *
     * @param <C>
     * @param key
     * @param value
     */
    public final void setVersion(MapLink key, final int value) {
        versions.put(key, value);
    }

    /**
     * Returns a collections of the ItemMaps contained.
     *
     * @return
     */
    public final Collection<ClientItemMap<? extends Item>> values() {
        return map.values();
    }
}
