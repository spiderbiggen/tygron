/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.net.serializable;

import java.io.Serializable;
import nl.tytech.data.core.item.Item;

/**
 * Wait to get thrown into a session
 * @author Maxim Knepfle
 *
 */
public class BucketWaiter implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 5626866392555987666L;

    public String token;

    public Integer slotID = Item.NONE;

    public String clientComputerName;

    public String clientAddress;

    public long timeStamp = System.currentTimeMillis();

}
