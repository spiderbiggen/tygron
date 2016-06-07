/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.structure;

import java.util.ArrayList;
import java.util.List;
import nl.tytech.data.core.item.EnumOrderedItem;
import nl.tytech.data.core.item.Item;

/**
 * base map to store item objects
 * @author Maxim Knepfle
 *
 * @param <I>
 */
public abstract class AbstractItemMap<I extends Item> implements ItemMap<I> {

    @Override
    public <IE extends EnumOrderedItem<T>, T extends Enum<T>> List<I> getEnumItems(Iterable<T> requestEnums) {

        // return null when requesting null
        if (requestEnums == null) {
            return null;
        }

        // list with items
        List<I> requestItems = new ArrayList<>();

        for (T requestEnum : requestEnums) {
            // get the requested Item for the given id.
            I requestItem = get(requestEnum);
            // only add not null
            if (requestItem != null) {
                requestItems.add(requestItem);
            }
        }
        // return list
        return requestItems;
    }

    @Override
    public List<I> getItems(Iterable<Integer> requestIDs) {

        // return null when requesting null
        if (requestIDs == null) {
            return null;
        }

        // list with items
        List<I> requestItems = new ArrayList<>();

        for (Integer requestID : requestIDs) {
            // get the requested Item for the given id.
            I requestItem = get(requestID);
            // only add not null
            if (requestItem != null) {
                requestItems.add(requestItem);
            }
        }
        // return list
        return requestItems;
    }

    @Override
    public List<I> toList(int fromIndex) {

        List<I> list = new ArrayList<>();
        for (I item : values()) {
            if (item.getID().intValue() >= fromIndex) {
                list.add(item);
            }
        }
        return list;
    }
}
