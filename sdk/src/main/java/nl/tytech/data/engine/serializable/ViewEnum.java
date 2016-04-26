/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.serializable;

import nl.tytech.core.net.Network;
import nl.tytech.core.net.Network.AppType;

/**
 * ViewEnum
 * <p>
 * Defines the available view in the client viewer for this specific game.
 * </p>
 *
 * @author Jeroen Warmerdam
 */
public enum ViewEnum {

    // Loading screen
    LOADING(AppType.PARTICIPANT, "", true, "LoadingScreenController"),

    // Contributors screen
    CONTRIBUTORS(AppType.PARTICIPANT, "viewflow_contributor.png", true, "ContributorScreenController"),

    // Intro screen
    INTRODUCTION(AppType.PARTICIPANT, "viewflow_intro.png", true, "IntroductionScreenController"),

    // team name
    TEAM_NAME(AppType.PARTICIPANT, "viewflow_team.png", true, "TeamNameScreenController"),

    // and stakeholder
    STAKEHOLDER_SELECTION(AppType.PARTICIPANT, "viewflow_actor.png", true, "SelectionScreenController"),

    ASSIGNMENT(AppType.PARTICIPANT, "viewflow_assignment.png", true, "AssignmentScreenController"),

    // Main view
    MAIN(AppType.PARTICIPANT, "viewflow_main.png", false, "MainScreenController"),

    //
    BEAMER_WELCOME(AppType.BEAMER, false, "BeamerContributorScreenController"),
    //
    BEAMER_INDICATOR(AppType.BEAMER, false, "BeamerIndicatorScreenController"),
    //
    BEAMER_HEAT(AppType.BEAMER, false, "BeamerHeatScreenController"),
    //
    BEAMER_WEBVIEW(AppType.BEAMER, false, "BeamerWebviewScreenController"),
    //
    BEAMER_SCORE(AppType.BEAMER, false, "BeamerMapScreenController");

    private static final String VIEWFLOW_PLACEHOLDER_IMAGE_JPG = "viewflow_ph.jpg";

    private static final String VIEWFLOW_IMAGE_DIR = "Gui/Images/Editor/Viewflow/";

    public final static ViewEnum[] VALUES = ViewEnum.values();

    private final Network.AppType applicationType;
    private final String imageFileName;
    private final boolean dynamicallyAssignable;
    private final String controller;

    private ViewEnum(Network.AppType applicationType, boolean dynamic, String fxScreenController) {
        this(applicationType, VIEWFLOW_PLACEHOLDER_IMAGE_JPG, dynamic, fxScreenController);
    }

    private ViewEnum(Network.AppType applicationType, String image, boolean dynamic, String fxScreenController) {
        this.applicationType = applicationType;
        this.imageFileName = image;
        this.dynamicallyAssignable = dynamic;
        this.controller = fxScreenController;
    }

    public Network.AppType getApplicationType() {
        return this.applicationType;
    }

    public String getController() {
        return controller;
    }

    public String getImageFileName() {
        return VIEWFLOW_IMAGE_DIR + imageFileName;
    }

    public boolean isDynamicallyAssignable() {
        return dynamicallyAssignable;
    }

}
