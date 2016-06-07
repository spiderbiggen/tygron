/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.serializable;

/**
 * MapType
 * <p>
 * MapType defines the map you are playing in. e.g. current or marquette.
 * </p>
 * 
 * @author Maxim Knepfle
 */
public enum MapType {

    /**
     * This map show the actual situation at this moment in the simulation.
     */
    CURRENT,

    /**
     * This map shows also shows the planned building etc that are not jet in the actual/current map.
     */
    MAQUETTE;

    /**
     * Static reference to prevent creating new value arrays each time called. Best Practice
     */
    public final static MapType[] VALUES = MapType.values();

}
