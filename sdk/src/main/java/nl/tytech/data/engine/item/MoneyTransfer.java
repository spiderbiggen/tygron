/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.UniqueNamedItem;
import nl.tytech.data.engine.item.ClientWord.ClientTerms;
import nl.tytech.util.StringUtils;

/**
 * @author Maxim Knepfle
 */
public class MoneyTransfer extends UniqueNamedItem {

    public enum Type {
        SUBSIDY, LAND, TRANSFER
    }

    private static final long serialVersionUID = -7075877109212624952L;

    @XMLValue
    private double amount = 0;

    @XMLValue
    private Type type = Type.TRANSFER;

    @ItemIDField("STAKEHOLDERS")
    @XMLValue
    private Integer senderID = NONE;

    @ItemIDField("STAKEHOLDERS")
    @XMLValue
    private Integer receiverID = NONE;

    @XMLValue
    private String moneyMessageDescription = StringUtils.EMPTY;

    @XMLValue
    private boolean active = false;

    public MoneyTransfer() {
    }

    public MoneyTransfer(Type type, Integer senderID, Integer receiverID, String name, double amount) {
        this.type = type;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.amount = amount;
        this.setName(name);
    }

    /**
     * @return the amount
     */
    public final double getAmount() {
        return this.amount;
    }

    public String getMoneyTransferDescription() {
        return moneyMessageDescription;
    }

    public Stakeholder getReceiver() {
        return this.getItem(MapLink.STAKEHOLDERS, receiverID);
    }

    public Integer getReceiverID() {
        return receiverID;
    }

    public Stakeholder getSender() {
        return this.getItem(MapLink.STAKEHOLDERS, senderID);
    }

    public Integer getSenderID() {
        return senderID;
    }

    public Type getType() {
        return type;
    }

    /**
     * @return the active
     */
    public final boolean isActive() {
        return this.active;
    }

    /**
     * @return the active
     */
    public final String isActiveString() {

        if (this.isActive()) {
            return this.getWord(MapLink.CLIENT_WORDS, ClientTerms.SUBSIDY_GRANTED);
        }
        return this.getWord(MapLink.CLIENT_WORDS, ClientTerms.SUBSIDY_NOT_GRANTED);
    }

    /**
     * @param active the active to set
     */
    public final void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return getName();
    }
}
