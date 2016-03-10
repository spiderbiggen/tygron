/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.net.serializable;

import nl.tytech.core.net.Network.SessionType;
import nl.tytech.locale.TLanguage;

/**
 * Wrapper class that handles all parameters as a answer from the server to the session request.
 * @author Maxim
 *
 */
public class JoinReply {

    public ClientData client;
    public String serverToken;
    public SessionType sessionType;
    public String project;
    public TLanguage languague;
    public MapLink[] lists;
}
