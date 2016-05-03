/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.net.serializable;

import java.io.Serializable;

/**
 * Token combination, used to check if your are connected to a session.
 * @author Maxim Knepfle
 *
 */
public class TokenPair implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -7633929810500085152L;

    public String clientToken;
    public String serverToken;

    public TokenPair() {

    }

    public TokenPair(String serverToken, String clientToken) {
        this.serverToken = serverToken;
        this.clientToken = clientToken;
    }
}
