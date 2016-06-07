/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.serializable;

import java.util.HashMap;
import java.util.Map;

/**
 * PanelEnum
 * <p>
 * PanelEnum defines the available panels for the user in the client viewer.
 * </p>
 *
 * @author Maxim Knepfle
 */
public enum PanelEnum {

    //
    ACHIEVEMENT_PANEL("AchievementPanelController"),

    //
    ASSIGNMENT_PROGRESS_PANEL("ProgressPanelController"),

    //
    BUDGET_PERSONAL_PANEL("BudgetPanelController"),

    //
    CATEGORY_PANEL("CategoryPanelController"),

    //
    CREDITS_PANEL("CreditsPanelController"),

    //
    WEB_PANEL("WebPanelController"),

    //
    FEEDBACK_PANEL("FeedbackPanelController"),

    //
    FLYTHROUGH_PANEL("CinematicPanelController"),

    //
    GLOBAL_PANEL("GlobalPanelController"),

    //
    GRID_OPTION_PANEL("GridOptionPanelController"),

    //
    HOVER_PANEL("HoverPanelController"),

    //
    INDICATOR_PANEL("IndicatorPanelController"),

    //
    LAND_BUY_PANEL("LandBuyPanelController"),

    //
    LAND_SELL_PANEL("LandSellPanelController"),

    //
    LEFT_MENU_PANEL("LeftMenuPanelController"),

    //
    LOAN_PANEL("LoanPanelController"),

    //
    OVERLAY_LEGEND_PANEL("OverlayLegendPanelController"),

    //
    SELECTION_LEGEND_PANEL("SelectionLegendPanelController"),

    //
    MAP_PANEL("MapPanelController"),

    //
    MENU_PANEL("MenuPanelController"),

    //
    MESSAGE_MESSAGE_PANEL("MessagePanelController"),

    //
    MESSAGE_PANEL("InboxPanelController"),

    //
    MESSAGE_REACTION_PANEL("ReplyPanelController"),

    //
    MESSAGE_SEND_PANEL("NewMessagePanelController"),

    //
    MONEY_TRANSFER_PANEL("MoneyTransferPanelController"),

    //
    PAUSE_PANEL("PausePanelController"),

    //
    LOGO_PANEL("LogoPanelController"),

    //
    POPUP_INFO_PANEL("PopupPanelController"),

    //
    RAIN_MAN_PANEL("RainmanPanelController"),

    //
    SETTINGS_PANEL("SettingsPanelController"),

    //
    STAKEHOLDER_PANEL("StakeholderPanelController"),

    //
    SWITCH_PANEL("SwitchPanelController"),

    //
    SUBSIDY_PANEL("SubsidyPanelController"),

    //
    TOPBAR_PANEL("TopPanelController"),

    VIDEO_PANEL("VideoPanelController"),

    FULLSCREEN_PANEL("FullscreenPanelController"),

    ACTION_LOG_PANEL("ActionLogPanelController"),

    HEAT_PANEL("HeatPanelController"),

    PIPE_LOAD_SNAP_SHOT_PANEL("PipeLoadSnapShotController"),

    ZONING_PERMIT_PANEL("ZoningPermitPanelController");

    private final static Map<String, PanelEnum> idMap = new HashMap<>();

    public final static PanelEnum[] VALUES = PanelEnum.values();

    static {
        for (PanelEnum pve : VALUES) {
            idMap.put(pve.getPanelName().toLowerCase(), pve);
        }
    }

    private String panelName;

    private PanelEnum(String name) {
        this.panelName = name;
    }

    public String getPanelName() {
        return panelName;
    }

    /**
     * Base panels are always visible and not related to special functionality like e.e. a message triggers a message panel.
     * @return
     */
    public boolean isBasePanel() {
        return this == MAP_PANEL || this == TOPBAR_PANEL || this == LEFT_MENU_PANEL || this == HOVER_PANEL || this == FEEDBACK_PANEL;
    }
}
