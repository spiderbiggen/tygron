/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.structure;

import java.util.Collection;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;
import nl.tytech.data.core.item.EnumOrderedItem;
import nl.tytech.data.core.item.Item;
import nl.tytech.util.logger.TLogger;

/**
 * ClientItemMap
 * <p>
 * Map containing the items and their versions on the client side.
 * <p>
 *
 *
 * @author Maxim Knepfle
 */
public class ClientItemMap<I extends Item> extends AbstractItemMap<I> implements Collection<I> {

    private SortedMap<Integer, I> map = new TreeMap<Integer, I>();

    /**
     * When this is an enum map it is based on this enum class type.
     */
    private Class<?> enumType = null;

    /**
     * When true this map uses enums ordinals to order the items
     */
    private Boolean enumOrdered = null;

    public ClientItemMap() {

    }

    public ClientItemMap(ClientItemMap<I> oldMap) {
        map = new TreeMap<>(oldMap.map);
    }

    @Override
    public boolean add(I item) {

        return map.put(this.size(), item) != null;
    }

    @Override
    public boolean addAll(Collection<? extends I> arg) {

        for (I item : arg) {
            this.add(item);
        }
        return true;
    }

    @Override
    public void clear() {
        TLogger.severe("Cannot clear an ItemMap");

    }

    @Override
    public boolean contains(Object value) {

        return map.containsValue(value);
    }

    @Override
    public boolean containsAll(Collection<?> arg) {

        return false;
    }

    @Override
    public boolean containsKey(Integer id) {

        return map.containsKey(id);
    }

    /**
     * It is also possible to retrieve an item from the map by using a enum value. The ordinal is the key ord ID.
     *
     * @param enumerator
     * @return Item.
     */
    @Override
    public <TE extends EnumOrderedItem<G>, G extends Enum<G>> I get(G enumerator) {

        // get first item and check if this is an enum ordered item.
        if (enumOrdered == null) {
            for (I item : map.values()) {
                if (item != null) {
                    if (item instanceof EnumOrderedItem) {
                        enumType = ((EnumOrderedItem<?>) item).getType().getClass();
                        enumOrdered = true;
                    } else {
                        enumOrdered = false;
                    }
                    break;
                }
            }
        }

        // map must be empty! or key is empty
        if (enumOrdered == null || enumerator == null) {
            return null;
        }

        // when not enum based, return
        if (!enumOrdered) {
            TLogger.severe("Trying to get an item based on the enum ordinal value, however map is not enum ordered.");
            return null;
        }

        // check if this is the correct enum class type.
        if (enumerator.getClass() != enumType) {
            TLogger.severe("Trying to get an item based on enum: " + enumerator.getClass().getSimpleName()
                    + ", however this does not match map enum type: " + enumType.getSimpleName() + ".");
            return null;
        }
        // okay get item
        return this.get(enumerator.ordinal());
    }

    @Override
    public I get(Integer id) {

        return map.get(id);
    }

    @Override
    public int getVersion() {
        // TODO: Maxim: not always calc this but store when adding items?
        int maxVersion = 0;
        for (Item item : this.values()) {
            if (item.getVersion() > maxVersion) {
                maxVersion = item.getVersion();
            }
        }
        return maxVersion;
    }

    @Override
    public boolean isEmpty() {

        return this.size() == 0;
    }

    @Override
    public Iterator<I> iterator() {

        return map.values().iterator();
    }

    @Override
    public I put(Integer id, I item) {

        return map.put(id, item);
    }

    @Override
    public I remove(Integer id) {

        return map.remove(id);
    }

    @Override
    public boolean remove(Object item) {

        if (!(item instanceof Item)) {
            return false;
        }
        Item itemObject = (Item) item;
        return map.remove(itemObject.getID()) != null;
    }

    @Override
    public boolean removeAll(Collection<?> arg) {

        for (Object item : arg) {
            this.remove(item);
        }
        return true;
    }

    @Override
    public boolean retainAll(Collection<?> arg0) {
        TLogger.severe("Cannot retain all on an ItemMap");
        return false;
    }

    @Override
    public final int size() {

        // is 0
        if (map.size() == 0) {
            return 0;
        }
        // map can contain null's, lastkey is the size
        return map.lastKey() + 1;
    }

    @Override
    public Object[] toArray() {

        return map.values().toArray();
    }

    @Override
    public <T> T[] toArray(T[] arg) {

        return map.values().toArray(arg);
    }

    @Override
    public Collection<I> values() {

        return map.values();
    }
}
