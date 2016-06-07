/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.net.serializable;

import java.util.HashMap;
import java.util.Map;
import nl.tytech.data.core.item.Item;

/**
 * Returns the result of an update version call
 *
 * @author Maxim Knepfle
 *
 */
public class UpdateResult {

    private HashMap<String, Item[]> items = new HashMap<>();

    private HashMap<String, Item[]> deletes = new HashMap<>();

    private long timeStamp;

    public UpdateResult() {

    }

    public Map<String, Item[]> getDeletes() {
        return deletes;
    }

    public Map<String, Item[]> getItems() {
        return items;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
