/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.core.item;

/**
 * EnumOrderedItem
 * <p>
 * This Item is has a enum as key/location in the map/list.
 * <p>
 * 
 * 
 * @author Maxim Knepfle
 */
public abstract class EnumOrderedItem<E extends Enum<E>> extends Item {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1596022551151222427L;

    /**
     * Get the enum keys associated with this item.
     * 
     * @return
     */
    public abstract E[] getEnumValues();

    /**
     * Returns the enum (type) of this item.
     * 
     * @return
     */
    public final E getType() {

        // safety check
        if (this.getID().equals(Item.NONE)) {
            return null;
        }
        return this.getEnumValues()[this.getID()];
    }

    @Override
    public String toString() {

        if (this.getID() > Item.NONE && this.getID() < this.getEnumValues().length) {
            return this.getEnumValues()[this.getID()].toString();
        }
        return "Unknown type [ID: " + getID() + "]";
    }
}
