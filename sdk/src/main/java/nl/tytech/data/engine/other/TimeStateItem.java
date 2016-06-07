/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.other;

import nl.tytech.data.engine.serializable.TimeState;

/**
 * TimeStateItem
 * <p>
 * TimeStateItem is an item that has a time state (e.g. buildings and measures).
 * <p>
 * 
 * @author Maxim Knepfle
 */
public interface TimeStateItem {

    public Long getConstructionFinishDate();

    public Long getConstructionStartDate();

    public double getConstructionTimeInMonths();

    public Long getDemolishFinishDate();

    public Long getDemolishStartDate();

    public double getDemolishTimeInMonths();

    public Integer getID();

    public String getImageLocation();

    public String getName();

    public TimeState getTimeState();
}
