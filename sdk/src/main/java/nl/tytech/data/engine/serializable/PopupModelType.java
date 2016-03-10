/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.serializable;

/**
 * PopupModelType
 * <p>
 * PopupModelType defines the visualization model and image used for a popup.
 * </p>
 *
 * @author Frank Baars
 */
public enum PopupModelType {
    //
    APPROVED("approved"),
    //
    ARROW("arrow"),
    //
    BUY("buy"),
    //
    CONNECT_ON("connect_on"),
    //
    CONNECT_OFF("connect_off"),
    //
    CONSTRUCTING("constructing"),
    //
    DECLINED("declined"),
    //
    DEMOLISHING("demolishing"),
    //
    WAITING("waiting"),
    //
    QUESTION_MARK("questionmark");

    public static final PopupModelType[] VALUES = values();

    private static final String MODEL_LOCATION = "Models/Popups/";
    private static final String POPUP_IMAGE_LOCATION = "Gui/Images/Popups/";

    private String assetName;

    private PopupModelType(String assetName) {
        this.assetName = assetName;
    }

    public String getPopupImageAssetLocation() {
        return POPUP_IMAGE_LOCATION + assetName + ".png";
    }

    public String getPopupModelAssetLocation() {
        return MODEL_LOCATION + assetName + ".j3o";
    }
}
