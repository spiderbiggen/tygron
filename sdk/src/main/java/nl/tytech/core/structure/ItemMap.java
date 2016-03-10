/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.structure;

import java.util.Collection;
import java.util.List;
import nl.tytech.data.core.item.EnumOrderedItem;
import nl.tytech.data.core.item.Item;

/**
 * ItemMap
 * <p>
 * Interface uniting the server and client item maps.
 * <p>
 *
 *
 * @author Maxim Knepfle
 */
public interface ItemMap<I extends Item> extends Iterable<I> {

    public boolean containsKey(Integer id);

    /**
     * Get an item from the map using the ID
     *
     * @param id
     * @return
     */
    public I get(Integer id);

    /**
     * Get an item from the map using the enum ordinal ID
     *
     * @param id
     * @return
     */

    public <IE extends EnumOrderedItem<T>, T extends Enum<T>> I get(T e);

    public <IE extends EnumOrderedItem<T>, T extends Enum<T>> List<I> getEnumItems(Iterable<T> requestEnums);

    public List<I> getItems(Iterable<Integer> requestIDs);

    /**
     * Overall version of map.
     * @return
     */
    public int getVersion();

    /**
     * Put an Item in the map using the ID for key.
     *
     * @param id
     * @param item
     * @return
     */
    public I put(Integer id, I item);

    /**
     * Remove an item from the map using the ID as key.
     *
     * @param id
     * @return
     */
    public I remove(Integer id);

    /**
     * The capacity size of the map.
     * <p>
     * WARNING: this is the last item ID +1, the map does not guarantee that all ID's between 0 and size() are used!
     * </p>
     *
     * @return
     */
    public int size();

    public List<I> toList(int fromIndex);

    /**
     * Returns an collection of the items contained in the map.
     *
     * @return
     */
    public Collection<I> values();

}
