/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.serializable;

/**
 * PanelEnum
 * <p>
 * PanelEnum defines the available panels for the user in the beamer viewer.
 * </p>
 *
 * @author Jeroen Warmerdam
 */
public enum BeamerPanelEnum {
    //
    EMPTY_SCORES(),
    //
    PLAYER_SCORES(),
    //
    MAP_CURRENT(),
    //
    MAP_FUTURE();

    public final static BeamerPanelEnum[] VALUES = values();
}
