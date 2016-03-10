/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.other;

import java.util.Collection;
import nl.tytech.data.core.item.Answer;
import com.vividsolutions.jts.geom.MultiPolygon;

/**
 * Question
 * <p>
 * Question is an interface for popups and messages that contain a question that can be answered.
 * </p>
 * @author Maxim Knepfle
 */
public interface Question {

    public void addAnswer(Answer answer);

    public Collection<Answer> getAnswers();

    public Integer getID();

    public void setMultiPolygon(MultiPolygon mp);

    public void setSubject(String title);

    public void setText(String title);

}
