/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.net.serializable;

import nl.tytech.data.core.item.Item;

/**
 * DeletedItem
 * <p>
 * When an item is deleted this object is sent to the clients to let them know something is deleted. It also has a version just like the
 * normal items.
 * <p>
 * 
 * 
 * @author Maxim Knepfle
 */
public class DeletedItem<T extends Item> extends Item {

    /**
     * Map reset ID
     */
    public final static Integer MAP_RESET = -1;

    /**
     * 
     */
    private static final long serialVersionUID = 1669501789391939169L;

    /**
     * Used for total map reset
     */
    public DeletedItem() {
        this.setId(MAP_RESET);
    }

    public DeletedItem(final T item) {
        this.setId(item.getID());
    }

    @Override
    public String toString() {
        return this.getID().toString();
    }
}
