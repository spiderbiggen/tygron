/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.net.serializable;

import java.io.Serializable;
import nl.tytech.data.core.item.Item;
import nl.tytech.util.StringUtils;

/**
 * Invite
 * <p>
 * THREAD SAFE: This class keeps track of a Invite
 * <p>
 *
 *
 * @author Maxim Knepfle
 */
public class Invite implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 4875327270173307781L;

    private String token = StringUtils.randomToken();

    private long sendDate = System.currentTimeMillis();

    private String inviterName = StringUtils.EMPTY;

    private String inviteeEmail = StringUtils.EMPTY;

    private String inviterEmail = StringUtils.EMPTY;

    private Integer stakeholderID = Item.NONE;

    private Integer slotID = Item.NONE;

    public Invite() {

    }

    public Invite(String inviterName, Integer slotID, Integer stakeholderID, String inviterEmail, String inviteeEmail) {

        this.inviterName = inviterName;
        this.slotID = slotID;
        this.stakeholderID = stakeholderID;
        this.inviterEmail = inviterEmail;
        this.inviteeEmail = inviteeEmail;
    }

    public String getInviteeEmail() {
        return inviteeEmail;
    }

    public String getInviterEmail() {
        return inviterEmail;
    }

    public String getInviterName() {
        return inviterName;
    }

    public long getSendDate() {
        return sendDate;
    }

    public Integer getSlotID() {
        return slotID;
    }

    public Integer getStakeholderID() {
        return stakeholderID;
    }

    public String getToken() {
        return token;
    }
}
