/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.event;

import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event.SessionEventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.engine.item.Economy;
import nl.tytech.data.engine.item.MoneyTransfer;
import nl.tytech.data.engine.item.Overlay.OverlayType;
import nl.tytech.data.engine.serializable.BeamerPanelEnum;
import nl.tytech.data.engine.serializable.Category;
import nl.tytech.data.engine.serializable.MapType;
import nl.tytech.data.engine.serializable.ViewEnum;
import com.vividsolutions.jts.geom.MultiPolygon;

/**
 * LogicEventType
 * <p>
 * These server events trigger new functionality.
 * <p>
 * @author Maxim Knepfle
 */
public enum LogicEventType implements SessionEventTypeEnum {

    @EventParamData(editor = true, desc = "Add or remove a function from a particular Action Menu", params = { "Action Menu ID",
            "Function ID", "Available" })
    @EventIDField(links = { "ACTION_MENUS", "FUNCTIONS" }, params = { 0, 1 })
    ACTION_MENU_SET_FUNCTION_AVAILABLE(Integer.class, Integer.class, Boolean.class),

    @EventParamData(editor = true, desc = "Add or remove a measure from a particular Action Menu", params = { "Action Menu ID",
            "Measure ID", "Available" })
    @EventIDField(links = { "ACTION_MENUS", "MEASURES" }, params = { 0, 1 })
    ACTION_MENU_SET_MEASURE_AVAILABLE(Integer.class, Integer.class, Boolean.class),

    @EventParamData(editor = true, desc = "Add or remove an upgrade from a particular Action Menu", params = { "Action Menu ID",
            "Upgrade ID", "Available" })
    @EventIDField(links = { "ACTION_MENUS", "UPGRADE_TYPES" }, params = { 0, 1 })
    ACTION_MENU_SET_UPGRADE_AVAILABLE(Integer.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Set the score value and explanation text for an API Indicator", params = { "Indicator ID", "Explanation text",
            "Score value between 0 and 1" })
    @EventIDField(links = { "INDICATORS" }, params = { 0 })
    API_INDICATOR_SET_VALUE(Integer.class, String.class, Double.class),

    @EventParamData(desc = "Set what screen the beamer should show", params = { "Beamer View" })
    BEAMER_SET_VIEW(ViewEnum.class),

    @EventParamData(desc = "Set what panel the beamer should show", params = { "BeamerPanel Type" })
    BEAMER_SET_SCORE_PANEL(BeamerPanelEnum.class),

    @EventParamData(desc = "Set what screen and panel the beamer should show", params = { "Beamer View", "BeamerPanel Type",
            "MapLink mapLink", "Item ID" })
    BEAMER_SWITCH(ViewEnum.class, BeamerPanelEnum.class, MapLink.class, Integer.class),

    @EventParamData(editor = true, desc = "Set the beamer layer to a specific overlay type", params = { "Overlay Type" })
    @Deprecated
    BEAMER_SET_LAYER(OverlayType.class),

    @EventParamData(editor = true, desc = "Set the beamer layer to a specific overlay", params = { "Overlay ID" })
    @EventIDField(links = { "OVERLAYS" }, params = { 0 })
    BEAMER_SET_OVERLAY_ID(Integer.class),

    @EventParamData(editor = true, desc = "Start a cinematic for a specific stakeholder", params = { "Stakeholder ID", "Cinematic ID",
            "Animate to cinematic starting point" })
    @EventIDField(links = { "STAKEHOLDERS", "CINEMATIC_DATAS" }, params = { 0, 1 })
    CINEMATIC_STAKEHOLDER_START(Integer.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Flightpoint reached for a specific cinematic", params = { "Stakeholder ID", "Cinematic ID",
            "ID of point in cinematic reached" })
    @EventIDField(links = { "STAKEHOLDERS", "CINEMATIC_DATAS" }, params = { 0, 1 })
    CINEMATIC_REACHED_POINT(Integer.class, Integer.class, Integer.class),

    @EventParamData(editor = true, desc = "Stop any cinematic for a specific Stakeholder", params = { "Stakeholder ID" })
    @EventIDField(links = { "STAKEHOLDERS" }, params = { 0 })
    CINEMATIC_STAKEHOLDER_STOP(Integer.class),

    @EventParamData(desc = "Sets the economy demand for a certain FunctionCategory", params = { "FunctionCategory", "Economic Demand" })
    ECONOMY_SET_FUNCTION_STATE(Category.class, Economy.State.class),

    @EventParamData(editor = true, desc = "Set Global Variable to given number Value", params = { "Global ID", "Value" })
    @EventIDField(links = { "GLOBALS" }, params = { 0 })
    GLOBAL_SET_VALUE(Integer.class, Double.class),

    @EventParamData(editor = true, desc = "Set Area Attribute to given number Value", params = { "Area ID", "Attribute Name", "Value" })
    @EventIDField(links = { "AREAS" }, params = { 0 })
    AREA_SET_ATTRIBUTE(Integer.class, String.class, Double.class),

    @EventParamData(editor = true, desc = "Activate a level", params = { "Level ID" })
    @EventIDField(links = { "LEVELS" }, params = { 0 })
    LEVEL_SET_ACTIVE(Integer.class),

    @EventParamData(editor = true, desc = "Update a custom made measure", params = { "Owner ID", "Measure ID", "Name", "Description",
            "Price", "Maintenance", "Water storage amount", "Innovatie water storage", "Selected" })
    @EventIDField(links = { "STAKEHOLDERS", "MEASURES" }, params = { 0, 1 })
    MEASURE_UPDATE_CUSTOM(Integer.class, Integer.class, String.class, String.class, Double.class, Double.class, Double.class,
            Boolean.class, Boolean.class),

    @EventParamData(editor = true, desc = "Activate a message", params = { "Message ID" })
    @EventIDField(links = { "MESSAGES" }, params = { 0 })
    MESSAGE_ACTIVATE(Integer.class),

    @EventParamData(editor = true, desc = "Revoke a message. Effects are not revoked yet", params = { "Message ID" })
    @EventIDField(links = { "MESSAGES" }, params = { 0 })
    MESSAGE_REVOKE(Integer.class),

    @EventParamData(editor = true, desc = "Reset a message. Sets the message as inactive and clears the send date.", params = { "Message ID" })
    @EventIDField(links = { "MESSAGES" }, params = { 0 })
    MESSAGE_RESET(Integer.class),

    @EventParamData(desc = "Create a new money transfer between two Stakeholders", params = { "Money transfer Type", "Stakeholder From ID",
            "Stakeholder To ID", "Provided motivation", "Transfer amount" })
    @EventIDField(links = { "STAKEHOLDERS", "STAKEHOLDERS" }, params = { 1, 2 })
    MONEY_TRANSFER_ADD(MoneyTransfer.Type.class, Integer.class, Integer.class, String.class, Double.class),

    @EventParamData(desc = "Approve a money transfer", params = { "Money transfer ID", "Approved" })
    @EventIDField(links = { "MONEY_TRANSFERS" }, params = { 0 })
    MONEY_TRANSFERS_SET_APPROVED(Integer.class, Boolean.class),

    @EventParamData(desc = "Direct sale of land, no approval asked", params = { "Selling Stakeholder ID", "Buying Stakeholder ID",
            "Area of land being sold", "Price per square meter" })
    @EventIDField(links = { "STAKEHOLDERS", "STAKEHOLDERS" }, params = { 0, 1 })
    MAP_DIRECT_SELL_LAND(Integer.class, Integer.class, MultiPolygon.class, Double.class),

    @EventParamData(desc = "Set the default fraction of connected loads with a cluster", params = { "Fraction connected, between 0 and 1" })
    PIPE_CLUSTERS_SET_DEFAULT_FRACTION_CONNECTED(Double.class),

    @EventParamData(desc = "Set the fraction of connected loads with a cluster", params = { "PipeCluster ID",
            "Fraction connected, between 0 and 1" })
    @EventIDField(links = { "PIPE_CLUSTERS", }, params = { 0 })
    PIPE_CLUSTER_SET_FRACTION_CONNECTED(Integer.class, Double.class),

    @EventParamData(desc = "Set the price of a product", params = { "Product ID", "Price" })
    @EventIDField(links = { "PRODUCTS", }, params = { 0 })
    PRODUCT_SET_PRICE(Integer.class, Double.class),

    @EventParamData(desc = "Release a client from a session using its client token", params = { "ClientToken" })
    SESSION_RELEASE(String.class),

    @EventParamData(editor = true, desc = "Allow interactor", params = { "Interaction allowed" })
    SETTINGS_ALLOW_INTERACTION(Boolean.class),

    // TODO: (Frank) Enable when assistant is operational again
    // @EventParamData(desc = "Enable or disable the digital assistant", params = { "Available" })
    SETTINGS_ENABLE_ASSISTANT(Boolean.class),

    // TODO: (Frank) Enable when the measure proposal panel is implemented
    // @EventParamData(desc = "Show or hide the measure proposal option", params = { "Showing measure proposals" })
    SETTINGS_SHOW_MEASURE_PROPOSAL(Boolean.class),

    @EventParamData(editor = true, desc = "When true messages to unplayable stakeholders and faciliator are auto responded.", params = { "Automatically respond to messages from players to non-human players and faciliators" })
    SETTINGS_SET_MESSAGE_AUTO_RESPOND(Boolean.class),

    @EventParamData(desc = "Set the HEAT Contibution to connection costs and Variable transport costs", params = {
            "Contibution to connection costs", "Fixed transport costs", "Variable transport costs", "Return on Investment in percentage",
            "Generated power in MW", "Buy price of energy per GJ", "Buy price of gas per MWH", "Sell price of energy per GJ",
            "Sell price of gas per cubic meter", "Standing charge of gas", "Availability percentage" })
    SET_HEAT_NUMBERS(Double.class, Double.class, Double.class, Double.class, Double.class, Double.class, Double.class, Double.class,
            Double.class, Double.class, Double.class),

    @EventParamData(editor = true, desc = "Activate/deactivate a special effect", params = { "Special Effect ID", "Activated" })
    @EventIDField(links = { "SPECIAL_EFFECTS" }, params = { 0 })
    SPECIAL_EFFECT_SET_ACTIVE(Integer.class, Boolean.class),

    @EventParamData(desc = "Release a stakeholder from a session", params = { "Stakeholder ID" })
    @EventIDField(links = { "STAKEHOLDERS" }, params = { 0 })
    STAKEHOLDER_RELEASE(Integer.class),

    @EventParamData(editor = true, desc = "Activate/deactivate a Area", params = { "Area ID", "Activated" })
    @EventIDField(links = { "AREAS" }, params = { 0, })
    AREA_SET_ACTIVE(Integer.class, Boolean.class),

    @EventParamData(editor = true, desc = "Set multipolygon for Area in given map", params = { "Area ID", "MapType", "MultiPolygon" })
    @EventIDField(links = { "AREAS" }, params = { 0, })
    AREA_SET_POLYGON(Integer.class, MapType.class, MultiPolygon.class),

    @EventParamData(editor = true, desc = "Set the time multiplier. A value of 1 means that 1 sec reallife is also 1 sec time. 2 means 1 sec reallife is 2 sec simulated time", params = { "Multiplier (>=1)" })
    TIME_SET_SIM_MULTIPLIER(Long.class),

    @EventParamData(desc = "Undo the last building action of a stakeholder", params = { "Stakeholder ID" })
    @EventIDField(links = { "STAKEHOLDERS", }, params = { 0 })
    UNDO_LAST_BUILDING_ACTION(Integer.class),

    @EventParamData(editor = true, desc = "Activate/deactivate units of a specific type", params = { "UnitDataOverride ID", "Activated" })
    @EventIDField(links = { "UNIT_DATA_OVERRIDES" }, params = { 0 })
    UNIT_TYPE_SET_ACTIVE(Integer.class, Boolean.class),

    @EventParamData(desc = "Show a video for a stakeholder", params = { "Stakeholder ID", "Video ID" })
    @EventIDField(links = { "STAKEHOLDERS", "VIDEOS", }, params = { 0, 1 })
    VIDEOS_SHOW_TUTORIAL(Integer.class, Integer.class),

    @EventParamData(desc = "Fire events linked to a video for a stakeholder", params = { "Stakeholder ID", "Video ID" })
    @EventIDField(links = { "STAKEHOLDERS", "VIDEOS", }, params = { 0, 1 })
    VIDEOS_FIRE_TUTORIAL_EVENTS(Integer.class, Integer.class),

    @EventParamData(editor = true, desc = "Set rain data of the weather", params = { "Weather ID", "Total amount of rain in millimeters",
            "Duration of rainfall in minutes" })
    @EventIDField(links = { "WEATHERS" }, params = { 0 })
    WEATHER_SET_DATA(Integer.class, Double.class, Double.class),

    @EventParamData(editor = true, desc = "Activate or deactivate a zoomlevel", params = { "Zoomlevel ID", "Activated" })
    @EventIDField(links = { "ZOOMLEVELS" }, params = { 0 })
    ZOOMLEVEL_SET_ACTIVE(Integer.class, Boolean.class),

    @EventParamData(editor = true, desc = "Multiply traffic density with this factor", params = { "Factor, default 1.0" })
    SET_TRAFFIC_MULTIPLIER(Double.class),



    ;

    private List<Class<?>> classes;

    private LogicEventType(Class<?>... classes) {
        this.classes = Arrays.asList(classes);
    }

    @Override
    public boolean canBePredefined() {
        return true;
    }

    @Override
    public List<Class<?>> getClasses() {
        return classes;
    }

    @Override
    public Class<?> getResponseClass() {
        return null;
    }

    @Override
    public boolean isServerSide() {
        return true;
    }

    @Override
    public boolean triggerTestRun() {

        if (this == STAKEHOLDER_RELEASE || this == VIDEOS_FIRE_TUTORIAL_EVENTS || this == API_INDICATOR_SET_VALUE
                || this == SET_TRAFFIC_MULTIPLIER) {
            return false;
        }
        return true;
    }
}
