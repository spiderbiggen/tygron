/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.core.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import nl.tytech.core.event.EventValidationUtils;
import nl.tytech.core.item.annotations.EventList;
import nl.tytech.core.item.annotations.Html;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.util.ObjectUtils;
import nl.tytech.util.StringUtils;

/**
 * Message
 * <p>
 * Message is a letter sent from one stakeholder (sender) to another stakeholder (receiver).
 * <p>
 *
 *
 * @author Maxim Knepfle
 */
public class Message extends Item {

    public enum Type {
        POPUP, STANDARD, LAND, UPGRADE, SUBSIDY, POPUP_REPLACEMENT;
    }

    public final static String REPLY_HEADER = "RE: ";

    /** Generated serialVersionUID */
    private static final long serialVersionUID = 1690352539056351764L;

    public static final Integer REMOVED_NO_REPLY = -2;

    @XMLValue
    private boolean active = false;

    @XMLValue
    private boolean toAllParticipants = false;

    @XMLValue
    @ItemIDField("STAKEHOLDERS")
    private Integer receiverID = NONE;

    @XMLValue
    @ItemIDField("STAKEHOLDERS")
    private Integer senderID = NONE;

    @XMLValue
    private String answer = StringUtils.EMPTY;

    @XMLValue
    private Integer answerID = NONE;

    @XMLValue
    @ListOfClass(Answer.class)
    private ArrayList<Answer> answers = new ArrayList<>();

    @XMLValue
    private String iconLocation = StringUtils.EMPTY;

    @Html
    @XMLValue
    protected String contents = "No Contents";

    @XMLValue
    private String subject = "0_No Subject";

    @XMLValue
    private Long triggerDate;

    @XMLValue
    private boolean clientSent = false;

    @XMLValue
    @EventList(serverSide = true)
    private ArrayList<CodedEvent> events = new ArrayList<>();

    @XMLValue
    @EventList(serverSide = true)
    private ArrayList<CodedEvent> cancelEvents = new ArrayList<>();

    @XMLValue
    @EventList(serverSide = false)
    private ArrayList<CodedEvent> clientEvents = new ArrayList<>();

    @XMLValue
    protected Type type = Type.STANDARD;

    @XMLValue
    private boolean adminSendable = false;

    @XMLValue
    private Integer overrideDefaultAnswerID = Item.NONE;

    @XMLValue
    private Long sentDate;

    @XMLValue
    private int value = Item.NONE;

    @XMLValue
    private boolean sendCopy = false;

    @XMLValue
    private MapLink linkType = null;

    @XMLValue
    private Integer linkID = Item.NONE;

    public Message() {

    }

    public void addAnswer(Answer newAnswer) {

        // give ID the highest value +1
        int highestID = Item.NONE;
        for (Answer answer : this.answers) {
            if (answer.getID().intValue() >= highestID) {
                highestID = answer.getID();
            }
        }
        newAnswer.setID(new Integer(highestID + 1));
        this.answers.add(newAnswer);
    }

    public void deactivate() {
        if (active) {
            resetMessage();
            setActive(false);
        }
    }

    public Answer getAnswer() {

        for (Answer answer : this.answers) {
            if (answer.getID().equals(this.answerID)) {
                return answer;
            }
        }
        return null;
    }

    /**
     * @return the answerID
     */
    public final Integer getAnswerID() {

        return this.answerID;
    }

    /**
     * @return the answers
     */
    public final Collection<Answer> getAnswers() {
        return this.answers;
    }

    /**
     * @return the answerID
     */
    public final String getAnswerString() {

        return this.answer;
    }

    public final Answer getAnswerWithID(Integer answerID) {
        for (Answer answer : this.answers) {
            if (answer.getID().equals(answerID)) {
                return answer;
            }
        }
        return null;
    }

    public final Answer getAnswerWithListIndex(int index) {
        if (index >= 0 && index < this.answers.size()) {
            return this.answers.get(index);
        }
        return null;
    }

    public List<CodedEvent> getCancelEventList() {
        return cancelEvents;
    }

    public List<CodedEvent> getClientEvents() {
        return clientEvents;
    }

    public Integer getContentLinkID() {
        return linkID;
    }

    public MapLink getContentMapLink() {
        return linkType;
    }

    public final String getContents() {
        return this.contents;
    }

    public Answer getDefaultAnswer() {

        Answer answer = this.getAnswerWithID(this.overrideDefaultAnswerID);
        if (answer != null) {
            return answer;
        }
        return answers.size() > 0 ? answers.get(0) : null;
    }

    public Integer getDefaultAnswerID() {
        Answer answer = this.getDefaultAnswer();
        return answer == null ? Item.NONE : answer.getID();
    }

    @Override
    public String getDescription() {
        return getContents();
    }

    public List<CodedEvent> getEventList() {
        return events;
    }

    public String getIconLocation() {
        return iconLocation;
    }

    /**
     * Get the receiving stakeholder.
     *
     * @param <A>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <A extends CoreStakeholder> A getReceiver() {
        return (A) this.getItem(MapLink.STAKEHOLDERS, getReceiverID());
    }

    public Integer getReceiverID() {
        return receiverID;

    }

    /**
     * Get the sending stakeholder.
     *
     * @param <A>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <A extends CoreStakeholder> A getSender() {
        return (A) this.getItem(MapLink.STAKEHOLDERS, getSenderID());
    }

    public Integer getSenderID() {
        return senderID;
    }

    public Long getSentDate() {
        return sentDate;
    }

    public String getSubject() {
        return subject;
    }

    /**
     * @return the triggerDate
     */
    public final Long getTriggerDate() {
        return this.triggerDate;
    }

    public Type getType() {

        /*
         * if (popup != null) { return Type.POPUP; }
         */
        return type;
    }

    public int getValue() {
        return value;
    }

    public void init(final CoreStakeholder sender, final CoreStakeholder receiver, final Long triggerDate, final String subject,
            final String message) {

        Integer senderID = Item.NONE;
        Integer receiverID = Item.NONE;
        if (sender != null) {
            senderID = sender.getID();
        }
        if (receiver != null) {
            receiverID = receiver.getID();
        }
        init(senderID, receiverID, triggerDate, subject, message);
    }

    public void init(final Integer senderID, final Integer receiverID, final Long triggerDate, final String subject, final String message) {

        this.senderID = senderID;
        this.receiverID = receiverID;
        this.triggerDate = triggerDate;
        this.contents = message;
        this.subject = subject;

    }

    /**
     * @return the active
     */
    public final boolean isActive() {

        return this.active;
    }

    /**
     * @return the answered
     */
    public final boolean isAnswered() {
        return StringUtils.containsData(this.answer) || !NONE.equals(answerID);
    }

    public boolean isClientSent() {
        return clientSent;
    }

    public boolean isFacilitatorSendable() {
        return adminSendable;
    }

    /**
     * True when message reply needs a motivation.
     *
     * @return the motivation
     */
    public final boolean isMotivationNeeded() {
        return (this.answers == null || this.answers.size() == 0);
    }

    public boolean isSendCopy() {
        return sendCopy;
    }

    public boolean isToAllParticipants() {
        return toAllParticipants;
    }

    public final boolean removeAnswerWithID(Integer answerID) {
        for (Answer answer : this.answers) {
            if (answer.getID().equals(answerID)) {
                return this.answers.remove(answer);
            }
        }
        return false;
    }

    private void resetMessage() {
        if (isAnswered()) {
            setAnswered(Item.NONE);
        }
        setSentDate(null);
        setTriggerDate(null);
    }

    /**
     * @param active the active to set
     */
    public final void setActive(boolean active) {

        this.active = active;
    }

    /**
     * @param answered the answered to set
     */
    public final void setAnswered(final Integer answerID) {
        this.answerID = answerID;
    }

    public final void setAnswered(final String answer) {
        this.answer = answer;

    }

    public void setClientEvents(List<CodedEvent> clientEvents) {
        this.clientEvents = ObjectUtils.toArrayList(clientEvents);
    }

    public void setClientSent(boolean clientSent) {
        this.clientSent = clientSent;
    }

    public void setContentItem(MapLink mapLink, Integer itemID) {
        this.linkType = mapLink;
        this.linkID = itemID;
    }

    public void setContents(String content) {
        this.contents = content;
    }

    public void setDefaultAnswerID(Integer answerID) {
        this.overrideDefaultAnswerID = answerID;
    }

    public void setFacilitatorSendable(boolean facilibatorSendable) {
        this.adminSendable = facilibatorSendable;
    }

    public void setReceiverID(Integer id) {
        this.receiverID = id;
    }

    public void setSendCopy(boolean sendCopy) {
        this.sendCopy = sendCopy;
    }

    public void setSenderID(Integer senderID) {
        this.senderID = senderID;

    }

    public void setSentDate(Long sentDate) {
        this.sentDate = sentDate;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setToAllParticipants(boolean toAllParticipants) {
        this.toAllParticipants = toAllParticipants;
    }

    public void setTriggerDate(Long trigger) {
        this.triggerDate = trigger;
    }

    /**
     * @param type the type to set
     */
    public void setType(Type type) {
        this.type = type;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {

        if (StringUtils.containsData(subject)) {
            return subject;
        }
        return "Message " + this.getID();
    }

    @Override
    public String validated(boolean startNewSession) {

        String result = EventValidationUtils.validateCodedEvents(this, events, true)
                + EventValidationUtils.validateCodedEvents(this, cancelEvents, true)
                + EventValidationUtils.validateCodedEvents(this, clientEvents, false);

        for (Answer answer : this.getAnswers()) {
            result += EventValidationUtils.validateCodedEvents(this, answer.getEvents(), true);
            result += EventValidationUtils.validateCodedEvents(this, answer.getClientEvents(), false);
        }
        return result;
    }
}
