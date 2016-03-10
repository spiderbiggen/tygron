/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.event;

import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event.SessionEventTypeEnum;
import nl.tytech.core.event.Event.StartWithMyStakeholderEvent;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.core.item.annotations.TimelineOnly;
import nl.tytech.data.engine.item.Building;
import nl.tytech.data.engine.item.MapMeasure.WaterClassification;
import nl.tytech.data.engine.item.MoneyTransfer;
import nl.tytech.data.engine.serializable.Category;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;

/**
 * ParticipantEventType
 * <p>
 * These server events can be called by participants playing the session.
 * </p>
 * <p>
 * THEY MUST ALWAYS START WITH THE ID OF THE STAKEHOLDER TAKING THE ACTION!
 * <p>
 * @author Maxim Knepfle
 */
public enum ParticipantEventType implements SessionEventTypeEnum, StartWithMyStakeholderEvent {

    @EventParamData(desc = "Apply a bundle of events on the serverside for a particular stakeholder", params = { "Stakeholder ID",
            "Eventbundle ID" })
    @EventIDField(links = { "STAKEHOLDERS", "EVENT_BUNDLES" }, params = { 0, 1 })
    EVENT_BUNDLE_APPLY_SERVER_EVENTS(Integer.class, Integer.class),

    @EventParamData(desc = "Set the location of a Stakeholder in the 3D world.", params = { "Stakeholder ID",
            "Location of the Stakeholder on the map as Point", "Ping others (show them my location)" })
    @EventIDField(links = { "STAKEHOLDERS" }, params = { 0 })
    STAKEHOLDER_SET_LOCATION(Integer.class, Point.class, Boolean.class),

    @EventParamData(desc = "Select a Stakeholder to play.", params = { "Stakeholder ID", "Session Token", })
    @EventIDField(links = { "STAKEHOLDERS" }, params = { 0 })
    STAKEHOLDER_SELECT(Integer.class, String.class),

    @EventParamData(desc = "Plan a new building in the MAQUETTE map", params = { "Stakeholder ID", "Function ID", "Amount of floors",
            "MultiPolygon describing the build contour" })
    @EventIDField(links = { "STAKEHOLDERS", "FUNCTIONS" }, params = { 0, 1 })
    BUILDING_PLAN_CONSTRUCTION(Integer.class, Integer.class, Integer.class, MultiPolygon.class),

    @EventParamData(desc = "Plan the demolition of a building in the MAQUETTE map", params = { "Stakeholder ID (also owner)",
            "Building ID", })
    @EventIDField(links = { "STAKEHOLDERS", "BUILDINGS" }, params = { 0, 1 })
    BUILDING_PLAN_DEMOLISH(Integer.class, Integer.class),

    @EventParamData(desc = "Plan the demolition of a buildings in the given polygon in the MAQUETTE map", params = { "Stakeholder ID",
            "Multipolygon describing the demolition area", "Ground type: SURFACE or UNDERGROUND" })
    @EventIDField(links = { "STAKEHOLDERS" }, params = { 0 })
    BUILDING_PLAN_DEMOLISH_POLYGON(Integer.class, MultiPolygon.class, Building.GroundLayerType.class),

    @EventParamData(desc = "Plan the upgrade of buildings in the given polygon in the MAQUETTE map", params = { "Stakeholder ID",
            "Upgrade Type ID", "Multipolygon describing the upgrade area" })
    @EventIDField(links = { "STAKEHOLDERS", "UPGRADE_TYPES" }, params = { 0, 1 })
    BUILDING_PLAN_UPGRADE(Integer.class, Integer.class, MultiPolygon.class),

    @EventParamData(desc = "Revert polygon to orginal CURRENT map situation.", params = { "Stakeholder ID",
            "Multipolygon describing the to be reverted area" })
    @EventIDField(links = { "STAKEHOLDERS" }, params = { 0 })
    BUILDING_REVERT_POLYGON(Integer.class, MultiPolygon.class),

    @EventParamData(editor = true, desc = "Set a category active for a stakeholder", params = { "Stakeholder ID", "ActionMenu ID", "Active" })
    @EventIDField(links = { "STAKEHOLDERS", "ACTION_MENUS" }, params = { 0, 1 })
    ACTION_MENU_SET_ACTIVE(Integer.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Activate a loan for a particular Stakeholder", params = { "Stakeholder ID", "Loan ID" })
    @TimelineOnly
    @EventIDField(links = { "STAKEHOLDERS", "LOANS" }, params = { 0, 1 })
    LOAN_ACTIVATE(Integer.class, Integer.class),

    @TimelineOnly
    @EventParamData(editor = true, desc = "Create a new loan for a particular Stakeholder.", params = { "Stakeholder ID", "Amount",
            "Years to pay back" })
    @EventIDField(links = { "STAKEHOLDERS" }, params = { 0 })
    LOAN_NEW(Integer.class, Double.class, Integer.class),

    @EventParamData(desc = "Buy the land definied by the polygon for given price", params = { "Stakeholder ID",
            "Multipolygon describing the area to be bought", "Price per square meter" })
    @EventIDField(links = { "STAKEHOLDERS" }, params = { 0 })
    MAP_BUY_LAND(Integer.class, MultiPolygon.class, Double.class),

    @EventParamData(desc = "Lower land to create open water", params = { "Stakeholder ID", "Multipolygon describing the lowered area" })
    @EventIDField(links = { "STAKEHOLDERS" }, params = { 0 })
    MAP_LOWER_LAND(Integer.class, MultiPolygon.class),

    @EventParamData(desc = "Raise land by one length unit", params = { "Stakeholder ID", "Multipolygon describing the raised area" })
    @EventIDField(links = { "STAKEHOLDERS" }, params = { 0 })
    MAP_RAISE_LAND(Integer.class, MultiPolygon.class),

    @EventParamData(desc = "Create a dike with a given height and area", params = { "Stakeholder ID", "Dike ID",
            "Multipolygon describing the surface area", "Height in meters" })
    @EventIDField(links = { "STAKEHOLDERS", "DIKES" }, params = { 0, 1 })
    MAP_DIKE(Integer.class, Integer.class, MultiPolygon.class, Double.class),

    @EventParamData(desc = "Buy the land definied by the polygon for given price", params = { "Land owner", "Proposed buyer of the land",
            "Multipolygon describing the area to be sold", "Price per square meter" })
    @EventIDField(links = { "STAKEHOLDERS", "STAKEHOLDERS" }, params = { 0, 1 })
    MAP_SELL_LAND(Integer.class, Integer.class, MultiPolygon.class, Double.class),

    @EventParamData(editor = true, desc = "Cancel a measure planned by an Stakeholder while in pre-construction phase", params = {
            "Stakeholder ID", "Measure ID" })
    @EventIDField(links = { "STAKEHOLDERS", "MEASURES" }, params = { 0, 1 })
    MEASURE_CANCEL_CONSTRUCTION(Integer.class, Integer.class),

    @EventParamData(desc = "Create a custom measure during the session that costs money", params = { "Measure owner",
            "Name of the measure", "Description of the measure", "Price" })
    @EventIDField(links = { "STAKEHOLDERS" }, params = { 0 })
    MEASURE_CREATE_CUSTOM(Integer.class, String.class, String.class, Double.class),

    @EventParamData(editor = true, desc = "Plan construction of a measure by an Stakeholder that is not yet planned", params = {
            "Stakeholder ID", "Measure ID" })
    @EventIDField(links = { "STAKEHOLDERS", "MEASURES" }, params = { 0, 1 })
    MEASURE_PLAN_CONSTRUCTION(Integer.class, Integer.class),

    @TimelineOnly
    @EventParamData(editor = true, desc = "Plan demolishion of a measure by an Stakeholder. (Works only for Timeline simulations)", params = {
            "Stakeholder ID", "Measure ID" })
    @EventIDField(links = { "STAKEHOLDERS", "MEASURES" }, params = { 0, 1 })
    MEASURE_PLAN_DEMOLISH(Integer.class, Integer.class),

    // TODO: (Frank) Water clasification is currently not used. Disabled in editor for now!
    // @EventParamData(desc = "Classify the water measure as a Waterboard Stakeholder", params = { "Waterboard Stakeholder", "Measure",
    // "Water classification" })
    @EventIDField(links = { "STAKEHOLDERS", "MEASURES" }, params = { 0, 1 })
    MEASURE_SET_WATER_CLASSIFICATION(Integer.class, Integer.class, WaterClassification.class),

    @EventParamData(desc = "Stakeholder answers a message", params = { "Stakeholder ID", "Message ID", "Answer ID" })
    @EventIDField(links = { "STAKEHOLDERS", "MESSAGES" }, params = { 0, 1 })
    MESSAGE_ANSWER(Integer.class, Integer.class, Integer.class),

    @EventParamData(desc = "Stakeholder answers a message and provides a motivation", params = { "Stakeholder ID", "Message ID",
            "Motivation text" })
    @EventIDField(links = { "STAKEHOLDERS", "MESSAGES" }, params = { 0, 1 })
    MESSAGE_ANSWER_WITH_MOTIVATION(Integer.class, Integer.class, String.class),

    @EventParamData(editor = true, desc = "Send a message between Stakeholders (email)", params = { "Sending Stakeholder ID",
            "Receiving Stakeholder ID", "Subject", "Content", "Motivation needed" })
    @EventIDField(links = { "STAKEHOLDERS", "STAKEHOLDERS" }, params = { 0, 1 })
    MESSAGE_NEW(Integer.class, Integer.class, String.class, String.class, Boolean.class),

    @EventParamData(editor = true, desc = "Ask for money from one stakeholder to another", params = { "Initiating Stakeholder ID",
            "Type of transfer", "Money giving Stakeholder ID", "Money asking Stakeholder ID", "Message text", "Amount" })
    @EventIDField(links = { "STAKEHOLDERS", "STAKEHOLDERS", "STAKEHOLDERS" }, params = { 0, 2, 3 })
    MONEY_TRANSFER_ASK(Integer.class, MoneyTransfer.Type.class, Integer.class, Integer.class, String.class, Double.class),

    @EventParamData(editor = true, desc = "Give money from one stakeholder to another", params = { "Initiating Stakeholder ID",
            "Type of transfer", "Money giving Stakeholder ID", "Money receiving Stakeholder ID", "Message text", "Amount" })
    @EventIDField(links = { "STAKEHOLDERS", "STAKEHOLDERS", "STAKEHOLDERS" }, params = { 0, 2, 3 })
    MONEY_TRANSFER_GIVE(Integer.class, MoneyTransfer.Type.class, Integer.class, Integer.class, String.class, Double.class),

    @EventParamData(desc = "Stakeholder has selected an answer in a panel", params = { "Stakeholder ID", "Panel ID", "Answer ID" })
    @EventIDField(links = { "STAKEHOLDERS", "PANELS" }, params = { 0, 1 })
    PANEL_ANSWER(Integer.class, Integer.class, Integer.class),

    @EventParamData(desc = "Stakeholder deletes a pipe load snapshot", params = { "Stakeholder ID", "PipeLoadSnapShot ID" })
    @EventIDField(links = { "STAKEHOLDERS", "PIPE_LOAD_SNAP_SHOTS" }, params = { 0, 1 })
    PIPE_LOAD_SNAP_SHOT_DELETE(Integer.class, Integer.class),

    @EventParamData(desc = "Stakeholder activates a pipe load snapshot", params = { "Stakeholder ID", "PipeLoadSnapShot ID" })
    @EventIDField(links = { "STAKEHOLDERS", "PIPE_LOAD_SNAP_SHOTS" }, params = { 0, 1 })
    PIPE_LOAD_SNAP_SHOT_LOAD(Integer.class, Integer.class),

    @EventParamData(desc = "Stakeholder saves a pipe load snapshot", params = { "Stakeholder ID", "New SnapShot Name",
            "Zoned Date Time Representation" })
    @EventIDField(links = { "STAKEHOLDERS" }, params = { 0 })
    PIPE_LOAD_SNAP_SHOT_SAVE(Integer.class, String.class, String.class),

    @EventParamData(desc = "Stakeholder answers a popup", params = { "Stakeholder ID", "Popup ID", "Answer ID" })
    @EventIDField(links = { "STAKEHOLDERS", "POPUPS" }, params = { 0, 1 })
    POPUP_ANSWER(Integer.class, Integer.class, Integer.class),

    @EventParamData(desc = "Stakeholder answers a popup with an additional date", params = { "Stakeholder ID", "Message ID", "Answer ID",
            "Date in milliseconds long from epoch" })
    @EventIDField(links = { "STAKEHOLDERS", "POPUPS" }, params = { 0, 1 })
    POPUP_ANSWER_WITH_DATE(Integer.class, Integer.class, Integer.class, Long.class),

    @EventParamData(desc = "Stakeholder restores land back to orginal state", params = { "Stakeholder ID", "Area to be restored" })
    @EventIDField(links = { "STAKEHOLDERS" }, params = { 0 })
    RESTORE_LAND(Integer.class, MultiPolygon.class),

    @EventParamData(desc = "Set the team name for this session.", params = { "Stakeholder ID", "Proposed team name" })
    @EventIDField(links = { "STAKEHOLDERS" }, params = { 0 })
    SETTINGS_TEAM_NAME(Integer.class, String.class),

    @EventParamData(editor = true, desc = "Set the maximum allowed water rise amount in meters in polder water", params = {
            "Initiating Stakeholder ID", "Amount in meters" })
    @EventIDField(links = { "STAKEHOLDERS" }, params = { 0 })
    SETTINGS_SET_ALLOWED_WATER_LEVEL_INCREASE(Integer.class, Double.class),

    // TODO: (Frank) Strategies are currently not used
    // @EventParamData(desc = "Activate a particular startegy initiated by an Stakeholder", params = { "Initiating Stakeholder", "Strategy",
    // "Motivation" })
    @EventIDField(links = { "STAKEHOLDERS", "STRATEGIES" }, params = { 0, 1 })
    STRATEGY_SET_ACTIVE(Integer.class, Integer.class, String.class),

    @EventParamData(desc = "Ask for activation/deactivation of a transportation of a particular product in a product storage", params = {
            "Stakeholder ID", "Product storage ID", "Transport entry ID", "Activate" })
    @EventIDField(links = { "STAKEHOLDERS", "PRODUCT_STORAGES" }, params = { 0, 1 })
    TRANSPORT_ASK_ACTIVATE(Integer.class, Integer.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Activate the transportation of a particular product in a product storage.", params = { "Stakeholder ID",
            "Product storage ID", "Transport entry ID", "Activate" })
    @EventIDField(links = { "STAKEHOLDERS", "PRODUCT_STORAGES" }, params = { 0, 1 })
    TRANSPORT_SET_ACTIVE(Integer.class, Integer.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Decline the activation of the transportation of a particular product in a product storage.", params = {
            "Stakeholder ID", "Product storage ID", "Transport entry ID", "Activate" })
    @EventIDField(links = { "STAKEHOLDERS", "PRODUCT_STORAGES" }, params = { 0, 1 })
    TRANSPORT_SET_ACTIVE_DECLINED(Integer.class, Integer.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Deactivate a ChainElement entry and activate an other instead for a given product storage", params = {
            "Stakeholder ID", "Product Storage ID", "Deactivated ChainElementEntry ID", "Activated ChainElementEntry ID" })
    @EventIDField(links = { "STAKEHOLDERS", "PRODUCT_STORAGES" }, params = { 0, 1 })
    CHAIN_ELEMENT_ENTRY_ACTIVATE_ALTERNATIVE(Integer.class, Integer.class, Integer.class, Integer.class),

    @EventParamData(desc = "Activate/Deactivate a ChainElement entry for a given product storage", params = { "Stakeholder ID",
            "Product Storage ID", "Deactivated ChainElementEntry ID", "Activated ChainElementEntry ID" })
    @EventIDField(links = { "STAKEHOLDERS", "PRODUCT_STORAGES" }, params = { 0, 1 })
    CHAIN_ELEMENT_ENTRY_ACTIVATE(Integer.class, Integer.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Add a FunctionCategory that is allowed to be built within a zone", params = { "Stakeholder ID", "Zone ID",
            "FunctionCategory" })
    @EventIDField(links = { "STAKEHOLDERS", "ZONES", }, params = { 0, 1 })
    ZONE_ADD_FUNCTION_CATEGORY(Integer.class, Integer.class, Category.class),

    @EventParamData(desc = "Remove a FunctionCategory so it cannot be build within the zone", params = { "Stakeholder ID", "Zone ID",
            "FunctionCategory" })
    @EventIDField(links = { "STAKEHOLDERS", "ZONES", }, params = { 0, 1 })
    ZONE_REMOVE_FUNCTION_CATEGORY(Integer.class, Integer.class, Category.class),

    @EventParamData(desc = "Set the maximum allowed number of floors for new constructions built within a zone", params = {
            "Stakeholder ID", "Zone ID", "Maximum number of floors" })
    @EventIDField(links = { "STAKEHOLDERS", "ZONES", }, params = { 0, 1 })
    ZONE_SET_MAX_FLOORS(Integer.class, Integer.class, Integer.class);

    private List<Class<?>> classes;

    private ParticipantEventType(Class<?>... classes) {
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

        if (this == STAKEHOLDER_SELECT) {
            return Boolean.class;
        } else if (this == BUILDING_PLAN_CONSTRUCTION || this == BUILDING_PLAN_DEMOLISH || this == BUILDING_PLAN_DEMOLISH_POLYGON
                || this == BUILDING_PLAN_UPGRADE || this == MEASURE_PLAN_CONSTRUCTION || this == MAP_RAISE_LAND || this == MAP_LOWER_LAND
                || this == EVENT_BUNDLE_APPLY_SERVER_EVENTS) {
            return Integer.class;
        }
        return null;
    }

    @Override
    public boolean isServerSide() {
        return true;
    }

    @Override
    public boolean triggerTestRun() {

        if (this == STAKEHOLDER_SELECT || this == SETTINGS_TEAM_NAME) {
            return false;
        }
        return true;
    }
}
