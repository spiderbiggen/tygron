/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.CoreStakeholder;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.item.ClientWord.ClientTerms;

/**
 * @author Maxim Knepfle
 */
public abstract class BookValue extends Item {

    public enum ChangeType {
        OVERRIDE, ADD, SUBSTRACT;
    }

    public interface Type {
        public ClientTerms getTranslationTerm();
    }

    private static final long serialVersionUID = -8755571740380078801L;

    @XMLValue
    private String name = "No Name";

    @XMLValue
    private double value = 0;

    @XMLValue
    private MapLink mapLink = null;

    @XMLValue
    private Integer linkID = Item.NONE;

    @XMLValue
    @ItemIDField("STAKEHOLDERS")
    private Integer stakeholderID = Item.NONE;

    public BookValue() {
    }

    public BookValue(final CoreStakeholder stakeholder, final MapLink mapLink, Integer linkID, final String name, final double value) {

        this.stakeholderID = stakeholder.getID();
        this.name = name;
        this.value = value;
        this.mapLink = mapLink;
        this.linkID = linkID;
    }

    public <T extends Item> T getContentItem() {
        return this.getItem(mapLink, linkID);
    }

    public Integer getContentLinkID() {
        return linkID;
    }

    public MapLink getContentMapLink() {
        return mapLink;
    }

    /**
     * @return the name
     */
    public final String getName() {
        return this.name;
    }

    public CoreStakeholder getStakeholder() {
        return this.getItem(MapLink.STAKEHOLDERS, this.getStakeholderID());
    }

    public Integer getStakeholderID() {
        return this.stakeholderID;
    }

    public abstract Type getType();

    public String getTypeName() {
        return this.getWord(MapLink.CLIENT_WORDS, this.getType().getTranslationTerm());
    }

    public double getValue() {
        return value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStakeholder(Stakeholder stakeholder) {
        this.stakeholderID = stakeholder.getID();
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return getType() + " " + getName() + " " + getValue();
    }
}
