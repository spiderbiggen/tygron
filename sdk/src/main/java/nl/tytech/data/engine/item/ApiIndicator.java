/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.engine.item.CustomIndicator.CustomIndicatorType;

/**
 * API Indicator
 *
 * @author Maxim Knepfle
 */
public class ApiIndicator extends Indicator {

    /**
     *
     */
    private static final long serialVersionUID = -3312131928668156363L;

    public static final String DEFAULT_ADDRESS = "http://server2.tygron.com:4000/run";

    @XMLValue
    private String remoteAddress = DEFAULT_ADDRESS;

    public String getRemoteAddress() {
        return remoteAddress;
    }

    @Override
    public TypeInterface getType() {
        return CustomIndicatorType.API_POST;
    }

    public void setRemoteAddress(String address) {
        this.remoteAddress = address;
    }
}
