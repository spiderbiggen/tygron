/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import java.util.ArrayList;
import java.util.Collection;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.util.StringUtils;

/**
 * @author Frank Baars
 */
public class PipeLoadSnapShot extends Item {

    /**
     *
     */
    private static final long serialVersionUID = 7249680157134102707L;

    @XMLValue
    @ItemIDField("PIPE_LOADS")
    private ArrayList<Integer> activePipeLoadIDs = new ArrayList<>();

    @XMLValue
    @ItemIDField("STAKEHOLDERS")
    private Integer ownerID;

    @XMLValue
    private String date = StringUtils.EMPTY;

    @XMLValue
    private String name = StringUtils.EMPTY;

    public void addPipeLoadID(Integer pipeLoadID) {
        if (!activePipeLoadIDs.contains(pipeLoadID)) {
            activePipeLoadIDs.add(pipeLoadID);
        }
    }

    public Collection<Integer> getActivePipeLoadIDs() {
        return this.activePipeLoadIDs;
    }

    public Collection<PipeLoad> getActivePipeLoads() {
        return this.getItems(MapLink.PIPE_LOADS, activePipeLoadIDs);
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public Stakeholder getOwner() {
        return getItem(MapLink.STAKEHOLDERS, ownerID);
    }

    public Integer getOwnerID() {
        return ownerID;
    }

    public void removePipeLoadID(Integer pipeLoadID) {
        activePipeLoadIDs.remove(pipeLoadID);
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOwnerID(Integer ownerID) {
        this.ownerID = ownerID;
    }

    @Override
    public String toString() {
        return name;
    }

}
