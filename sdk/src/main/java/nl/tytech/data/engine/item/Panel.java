/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import java.util.ArrayList;
import java.util.Collection;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.Answer;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.serializable.PopupModelType;
import nl.tytech.util.StringUtils;
import com.vividsolutions.jts.geom.Point;

/**
 * @author Frank Baars
 */
public class Panel extends Item {

    public enum PanelType {
        GLOBAL_PANEL,

        EXCEL_PANEL,

        WEB_PANEL,

        ;
        public static final PanelType[] VALUES = PanelType.values();
    }

    private static final long serialVersionUID = 7679659744033436741L;

    public static final double MAX_WIDTH = 1000;
    public static final double MAX_HEIGHT = 600;

    @XMLValue
    private double width = 600;// default width

    @XMLValue
    private double height = 300;// default height

    @XMLValue
    private String name = StringUtils.EMPTY;

    @XMLValue
    private Point point = null;

    @XMLValue
    @ItemIDField("STAKEHOLDERS")
    private Integer stakeholderID = Item.NONE;

    @XMLValue
    @ListOfClass(Answer.class)
    private ArrayList<Answer> answers = new ArrayList<>();

    @XMLValue
    private boolean active = true;

    @XMLValue
    private Integer overrideDefaultAnswerID = Item.NONE;

    @XMLValue
    private PopupModelType modelType = PopupModelType.QUESTION_MARK;

    public final void addAnswer(Answer newAnswer) {

        int highestID = Item.NONE;
        for (Answer answer : this.answers) {
            if (answer.getID().intValue() >= highestID) {
                highestID = answer.getID();
            }
        }
        newAnswer.setID(new Integer(highestID + 1));
        this.answers.add(newAnswer);
    }

    public final Collection<Answer> getAnswers() {
        return this.answers;
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

    public Answer getDefaultAnswer() {

        Answer answer = this.getAnswerWithID(this.overrideDefaultAnswerID);
        if (answer != null) {
            return answer;
        }
        return answers.size() > 0 ? answers.get(0) : null;
    }

    public double getHeight() {
        return height;
    }

    public String getName() {
        return name;
    }

    public Point getPoint() {
        return point;
    }

    public PopupModelType getPopupModelType() {
        return modelType;
    }

    public Integer getStakeholderID() {
        return stakeholderID;
    }

    public double getWidth() {
        return width;
    }

    public boolean isActive() {
        return active;
    }

    public final boolean removeAnswerWithID(Integer answerID) {
        for (Answer answer : this.answers) {
            if (answer.getID().equals(answerID)) {
                return this.answers.remove(answer);
            }
        }
        return false;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setModelType(PopupModelType modelType) {
        this.modelType = modelType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public void setStakeholderID(Integer stakeholderID) {
        this.stakeholderID = stakeholderID;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    @Override
    public String toString() {
        return name;
    }

}
