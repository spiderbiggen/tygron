/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.serializable;

import nl.tytech.util.StringUtils;

/**
 * FaciliatorTabEnum
 * <p>
 * Enum for storing the possible general panels in the facilitator panel
 * </p>
 * @author Frank Baars
 */
public enum FacilitatorTabEnum {

    //
    SERVER(false),
    //
    SETTINGS(false),
    //
    LEVEL(false),
    //
    ECONOMY(false),
    //
    MEASURE(false),

    //
    TEAM_STATUS(true),
    //
    TEAM_STAKEHOLDER(true),
    //
    TEAM_BEAMER(true),
    //
    TEAM_MESSAGE(true),
    //
    TEAM_SUBSIDY(true);

    private static final String DOT_PNG_EXT = ".png";

    public final static FacilitatorTabEnum[] VALUES = FacilitatorTabEnum.values();

    private boolean isTeamTab;

    private FacilitatorTabEnum(boolean isTeamTab) {
        this.isTeamTab = isTeamTab;
    }

    public String getImageFileName() {
        return StringUtils.capitalizeWithUnderScores(this.name()) + DOT_PNG_EXT;
    }

    public boolean isTeamTab() {
        return isTeamTab;
    }

}
