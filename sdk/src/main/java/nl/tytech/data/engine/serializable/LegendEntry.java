/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.serializable;

import java.io.Serializable;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;

/**
 * Entry on an overlay's legend
 * @author Maxim Knepfle
 *
 */
public class LegendEntry implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -3270236333331543401L;

    @XMLValue
    private TColor color = TColor.BLACK;

    @XMLValue
    private String entryName = StringUtils.EMPTY;

    public LegendEntry() {

    }

    public LegendEntry(String entryName, TColor color) {
        this.entryName = entryName;
        this.color = color;
    }

    // NOTE: If you ever change this equals method, check the methods where these legendentries are compared/ sorted etc.
    @Override
    public boolean equals(Object entry) {
        if (entry == null || !(entry instanceof LegendEntry)) {
            return false;
        }
        return ((LegendEntry) entry).getColor().equals(this.color);
    }

    public TColor getColor() {
        return this.color;
    }

    public String getEntryName() {
        return this.entryName;
    }
}
