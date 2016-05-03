/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import nl.tytech.core.event.EventValidationUtils;
import nl.tytech.core.item.annotations.DoNotSaveToInit;
import nl.tytech.core.item.annotations.EventList;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.CodedEvent;
import nl.tytech.data.core.item.Item;
import nl.tytech.util.StringUtils;

/**
 * Achievement
 * @author Maxim Knepfle
 */
public class Achievement extends Item {

    public enum Type {
        DESTROY_FIRST_TREE, PLAN_FIRST_BUILDING, FIRST_POPUP_RECEIVED, FIRST_LAND_TRANSACTION_POPUP_RECEIVED, FIRST_ZONING_POPUP_RECEIVED,
    }

    private static final long serialVersionUID = -4253782210241563578L;

    @DoNotSaveToInit
    @XMLValue
    private HashMap<Integer, Boolean> awardedForStakeholder = new HashMap<>();

    @XMLValue
    @EventList(serverSide = false)
    @ListOfClass(CodedEvent.class)
    private ArrayList<CodedEvent> clientEvents = new ArrayList<>();

    @XMLValue
    private String message = StringUtils.EMPTY;

    @XMLValue
    @EventList(serverSide = true)
    @ListOfClass(CodedEvent.class)
    private ArrayList<CodedEvent> serverEvents = new ArrayList<>();

    @XMLValue
    private String title = StringUtils.EMPTY;

    @XMLValue
    private Type type = null;

    public Achievement() {

    }

    public List<Integer> getAwardedForStakeholders() {
        List<Integer> result = new ArrayList<Integer>();
        for (Entry<Integer, Boolean> award : awardedForStakeholder.entrySet()) {
            if (award.getValue()) {
                result.add(award.getKey());
            }
        }
        return result;
    }

    public List<CodedEvent> getClientEvents() {
        return clientEvents;
    }

    public String getMessage() {
        return message;
    }

    public List<CodedEvent> getServerEvents() {
        return serverEvents;
    }

    public String getTitle() {
        return title;
    }

    public Type getType() {
        return type;
    }

    public boolean isAwarded(Integer stakeholderID) {
        if (!awardedForStakeholder.containsKey(stakeholderID)) {
            return false;
        }
        return awardedForStakeholder.get(stakeholderID);

    }

    public void setAwarded(Integer stakeholderID, boolean awarded) {
        this.awardedForStakeholder.put(stakeholderID, awarded);
    }

    @Override
    public String toString() {

        return StringUtils.EMPTY + this.getType();
    }

    @Override
    public String validated(boolean startNewSession) {

        return EventValidationUtils.validateCodedEvents(this, clientEvents, false)
                + EventValidationUtils.validateCodedEvents(this, serverEvents, true);
    }
}
