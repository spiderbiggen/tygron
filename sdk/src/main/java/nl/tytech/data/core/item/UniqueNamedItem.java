/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.core.item;

import nl.tytech.core.item.annotations.XMLValue;

/**
 * Items with a unique name must extend this item class
 *
 * @author Maxim Knepfle
 *
 */
public abstract class UniqueNamedItem extends Item {

    /**
     *
     */
    private static final long serialVersionUID = -5605037089134219736L;

    @XMLValue
    private String name = "A New Item";

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
