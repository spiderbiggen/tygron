/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.Item;
import nl.tytech.util.StringUtils;

/**
 *
 * @author Maxim Knepfle
 *
 */
public class ProjectAsset extends Item {

    /**
     *
     */
    private static final long serialVersionUID = -6754893695766076519L;

    @XMLValue
    private String location = StringUtils.EMPTY;

    @XMLValue
    private String checksum = StringUtils.EMPTY;

    public ProjectAsset() {

    }

    public ProjectAsset(String location, String checksum) {
        this.location = location;
        this.checksum = checksum;
    }

    public String getChecksum() {
        return checksum;
    }

    public String getLocation() {
        return location;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return location;
    }
}
