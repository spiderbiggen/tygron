/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.net.serializable;

import java.io.Serializable;
import nl.tytech.core.net.Network;
import nl.tytech.locale.TLanguage;
import nl.tytech.util.StringUtils;

/**
 * Contains info about the session running in this lot.
 *
 * @author Maxim Knepfle
 *
 */
public class SlotInfo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -8634240612067574617L;

    public Integer id;
    public String name = StringUtils.EMPTY;
    public TLanguage language;
    public TokenPair tokenPair;
    public String groupToken;
    public Network.AppType appType;
    public Network.SessionType sessionType;

    public SlotInfo() {

    }

    @Override
    public String toString() {
        String type = sessionType != null ? (StringUtils.capitalizeWithSpacedUnderScores(sessionType.name()) + ": ") : "";
        return type + name + StringUtils.WHITESPACE + "(" + language.name() + StringUtils.WHITESPACE + id + ")";
    }

}
