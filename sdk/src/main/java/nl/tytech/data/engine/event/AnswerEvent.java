/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.event;

import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.event.Event.SessionEventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.data.engine.item.MapMeasure.WaterClassification;
import com.vividsolutions.jts.geom.MultiPolygon;

/**
 * PopupAnswerEvent
 * <p>
 * These events should ONLY used by the PopupControl. It is used to trigger events that are given within the popup's answer.
 * </p>
 *
 * @author Maxim Knepfle
 */
public enum AnswerEvent implements SessionEventTypeEnum {

    @EventParamData(desc = "Stakeholder has planned the construction of a particular building for a particular date stored in a popup", params = {
            "Stakeholder ID", "Building ID", "Popup ID" })
    @EventIDField(links = { "STAKEHOLDERS", "BUILDINGS", "POPUPS" }, params = { 0, 1, 2 })
    BUILDING_ASK_CONSTRUCTION_DATE(Integer.class, Integer.class, Integer.class),

    @EventParamData(desc = "Stakeholder has planned the upgrade-construction of a particular building for a particular date stored in a popup", params = {
            "Stakeholder ID", "Building ID", "Popup ID" })
    @EventIDField(links = { "STAKEHOLDERS", "BUILDINGS", "POPUPS" }, params = { 0, 1, 2 })
    UPGRADE_ASK_DATE(Integer.class, Integer.class, Integer.class),

    @EventParamData(desc = "Stakeholder has planned the construction of a particular measure for a particular date stored in a popup", params = {
            "Stakeholder ID", "Measure ID", "Popup ID" })
    @EventIDField(links = { "STAKEHOLDERS", "MEASURES", "POPUPS" }, params = { 0, 1, 2 })
    MEASURE_ASK_CONSTRUCTION_DATE(Integer.class, Integer.class, Integer.class),

    // TODO: (Frank) Weird event, not fully functional
    // @EventParamData(desc = "Ask aproval for water", params = { "Stakeholder ID", "Measure ID " })
    @EventIDField(links = { "STAKEHOLDERS", "MEASURES", }, params = { 0, 1 })
    WATER_ASK_APPROVAL(Integer.class, Integer.class),

    @EventParamData(desc = "Stakeholder approves construction of building", params = { "Stakeholder ID", "Building ID", "Approves" })
    @EventIDField(links = { "STAKEHOLDERS", "BUILDINGS", }, params = { 0, 1 })
    BUILDING_CONSTRUCTION_APPROVAL(Integer.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Owner of building confirms the continuation of the planned construction", params = { "Stakeholder ID",
            "Building ID", "Confirms" })
    @EventIDField(links = { "STAKEHOLDERS", "BUILDINGS", }, params = { 0, 1 })
    BUILDING_CONSTRUCTION_APPROVAL_CONFIRMED(Integer.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Owner of buildings confirms the continuation of the planned construction", params = { "Stakeholder ID",
            "Building IDs", "Confirms" })
    @EventIDField(links = { "STAKEHOLDERS", "BUILDINGS", }, params = { 0, 1 })
    BUILDINGS_CONSTRUCTION_APPROVAL_CONFIRMED(Integer.class, Integer[].class, Boolean.class),

    @EventParamData(desc = "Owner of building confirms that the planned construction is not approved", params = { "Stakeholder ID",
            "Building ID" })
    @EventIDField(links = { "STAKEHOLDERS", "BUILDINGS", }, params = { 0, 1 })
    BUILDING_CONSTRUCTION_DENIED_CONFIRMED(Integer.class, Integer.class),

    @EventParamData(desc = "Owner of measure confirms that the planned measure is not approved", params = { "Stakeholder ID", "Measure ID" })
    @EventIDField(links = { "STAKEHOLDERS", "MEASURES", }, params = { 0, 1 })
    MEASURE_CONSTRUCTION_DENIED_CONFIRMED(Integer.class, Integer.class),

    @EventParamData(desc = "Stakeholder approves construction of measure", params = { "Stakeholder ID", "Measure ID",
            "Approving Stakeholder ID", "Approves" })
    @EventIDField(links = { "STAKEHOLDERS", "MEASURES", "STAKEHOLDERS" }, params = { 0, 1, 2 })
    MEASURE_CONSTRUCTION_APPROVAL(Integer.class, Integer.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Owner of measure confirms the continuation of the planned construction", params = { "Stakeholder ID",
            "Measure ID", "Confirms" })
    @EventIDField(links = { "STAKEHOLDERS", "MEASURES" }, params = { 0, 1 })
    MEASURE_CONSTRUCTION_APPROVAL_CONFIRM(Integer.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Stakeholder approves demolition of building", params = { "Stakeholder ID", "Building ID", "Approves" })
    @EventIDField(links = { "STAKEHOLDERS", "BUILDINGS" }, params = { 0, 1 })
    BUILDING_DEMOLISH_APPROVAL(Integer.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Owner of building confirms that the planned demolition is not approved", params = { "Stakeholder ID",
            "Building ID" })
    @EventIDField(links = { "STAKEHOLDERS", "BUILDINGS" }, params = { 0, 1 })
    BUILDING_DEMOLISH_DENIED_CONFIRMED(Integer.class, Integer.class),

    @EventParamData(desc = "Owner of building confirms the continuation of the planned demolition", params = { "Stakeholder ID",
            "Building ID", "Confirms" })
    @EventIDField(links = { "STAKEHOLDERS", "BUILDINGS" }, params = { 0, 1 })
    BUILDING_DEMOLISH_APPROVAL_CONFIRMED(Integer.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Owner of buildings confirms the continuation of the planned demolition", params = { "Stakeholder ID",
            "Building IDs", "Confirms" })
    @EventIDField(links = { "STAKEHOLDERS", "BUILDINGS" }, params = { 0, 1 })
    BUILDINGS_DEMOLISH_APPROVAL_CONFIRMED(Integer.class, Integer[].class, Boolean.class),

    @EventParamData(desc = "Stakeholder approves demolition of measure", params = { "Stakeholder ID", "Measure ID", "Approves" })
    @EventIDField(links = { "STAKEHOLDERS", "MEASURES" }, params = { 0, 1 })
    MEASURE_DEMOLISH_APPROVAL(Integer.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Stakeholder has planned the demolition of a particular building for a particular date stored in a popup", params = {
            "Stakeholder ID", "Building ID", "Popup ID" })
    @EventIDField(links = { "STAKEHOLDERS", "BUILDINGS", "POPUPS" }, params = { 0, 1, 2 })
    BUILDING_ASK_DEMOLISH_DATE(Integer.class, Integer.class, Integer.class),

    @EventParamData(desc = "Stakeholder has planned the demolition of a particular measure for a particular date stored in a popup", params = {
            "Stakeholder ID", "Measure ID", "Popup ID" })
    @EventIDField(links = { "STAKEHOLDERS", "MEASURES", "POPUPS" }, params = { 0, 1, 2 })
    MEASURE_ASK_DEMOLISH_DATE(Integer.class, Integer.class, Integer.class),

    // TODO: (Frank) Weird event, not fully functional
    // @EventParamData(desc = "Confirm water", params = { "Measure ID", "WaterClassification" })
    @EventIDField(links = { "MEASURES" }, params = { 0, })
    WATER_CONFIRM(Integer.class, WaterClassification.class),

    @EventParamData(desc = "Stakeholder approves upgrade-construction of building", params = { "Stakeholder ID", "Building ID", "Approves" })
    @EventIDField(links = { "STAKEHOLDERS", "BUILDINGS", }, params = { 0, 1 })
    UPGRADE_APPROVAL(Integer.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Upgrading Stakeholder confirms the continuation of the planned upgrade", params = { "Stakeholder ID",
            "Building ID", "Confirms" })
    @EventIDField(links = { "STAKEHOLDERS", "BUILDINGS" }, params = { 0, 1 })
    UPGRADE_APPROVAL_CONFIRMED(Integer.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Upgrading Stakeholder confirms the continuation of the planned upgrades", params = { "Stakeholder ID",
            "Building IDs", "Confirms" })
    @EventIDField(links = { "STAKEHOLDERS", "BUILDINGS" }, params = { 0, 1 })
    UPGRADES_APPROVAL_CONFIRMED(Integer.class, Integer[].class, Boolean.class),

    @EventParamData(desc = "Stakeholder's upgrade is denied based on zoning", params = { "Stakeholder ID", "Building ID" })
    @EventIDField(links = { "STAKEHOLDERS", "BUILDINGS" }, params = { 0, 1 })
    UPGRADE_ZONING_PERMIT_DENIED(Integer.class, Integer.class),

    @EventParamData(desc = "Stakeholder's upgrade has received a zoning permit", params = { "Stakeholder ID", "Building ID" })
    @EventIDField(links = { "STAKEHOLDERS", "BUILDINGS" }, params = { 0, 1 })
    UPGRADE_ZONING_PERMIT_CONFIRMED(Integer.class, Integer.class),

    @EventParamData(desc = "Selling Stakeholder has refused to sell the specified land for a given price per square meter to buying Stakeholder", params = {
            "Buying Stakeholder ID", "Selling Stakeholder ID", "MultiPolygon describing the land contour", "Price per square meter" })
    @EventIDField(links = { "STAKEHOLDERS", "STAKEHOLDERS" }, params = { 0, 1 })
    LAND_BUY_REFUSED(Integer.class, Integer.class, MultiPolygon.class, Double.class),

    @EventParamData(desc = "Selling Stakeholder has accepted to sell the specified land for a given price per square meter to buying Stakeholder", params = {
            "Buying Stakeholder ID", "Selling Stakeholder ID", "MultiPolygon describing the land contour", "Price per square meter" })
    @EventIDField(links = { "STAKEHOLDERS", "STAKEHOLDERS" }, params = { 0, 1 })
    LAND_BUY_APROVED(Integer.class, Integer.class, MultiPolygon.class, Double.class),

    @EventParamData(desc = "Buying Stakeholder has accepted to buy the specified land for a given price per square meter from selling Stakeholder", params = {
            "Buying Stakeholder ID", "Selling Stakeholder ID", "MultiPolygon describing the land contour", "Price per square meter" })
    @EventIDField(links = { "STAKEHOLDERS", "STAKEHOLDERS" }, params = { 0, 1 })
    LAND_SELL_APROVED(Integer.class, Integer.class, MultiPolygon.class, Double.class),

    @EventParamData(desc = "Buying Stakeholder has refused to buy the specified land for a given price per square meter from selling Stakeholder", params = {
            "Buying Stakeholder ID", "Selling Stakeholder ID", "MultiPolygon describing the land contour" })
    @EventIDField(links = { "STAKEHOLDERS", "STAKEHOLDERS" }, params = { 0, 1 })
    LAND_SELL_REFUSED(Integer.class, Integer.class, MultiPolygon.class),

    @EventParamData(desc = "Delete a popup", params = { "Popup ID" })
    @EventIDField(links = { "POPUPS" }, params = { 0 })
    DELETE_POPUP(Integer.class),

    @EventParamData(desc = "Delete a message", params = { "Message ID" })
    @EventIDField(links = { "MESSAGES" }, params = { 0 })
    DELETE_MESSAGE(Integer.class),

    @EventParamData(desc = "Connect a pipe cluster as a consumer", params = { "PipeCluster ID" })
    @EventIDField(links = { "PIPE_CLUSTERS" }, params = { 0 })
    PIPE_CONSUMER_CONNECT(Integer.class),

    @EventParamData(desc = "Connect a pipe cluster as a producer", params = { "PipeCluster ID" })
    @EventIDField(links = { "PIPE_CLUSTERS" }, params = { 0 })
    PIPE_PRODUCER_CONNECT(Integer.class),

    @EventParamData(desc = "Accept the connection of a pipeCluster", params = { "PipeCluster ID" })
    @EventIDField(links = { "PIPE_CLUSTERS" }, params = { 0 })
    PIPE_ACCEPT_CONNECT(Integer.class),

    @EventParamData(desc = "Reject the connection of a pipeCluster", params = { "PipeCluster ID" })
    @EventIDField(links = { "PIPE_CLUSTERS" }, params = { 0 })
    PIPE_REJECT_CONNECT(Integer.class),

    @EventParamData(desc = "Cancel connecting a pipeCluster", params = { "PipeCluster ID" })
    @EventIDField(links = { "PIPE_CLUSTERS" }, params = { 0 })
    PIPE_CANCEL_CONNECT(Integer.class);

    private final List<Class<?>> classes = new ArrayList<Class<?>>();

    private AnswerEvent(Class<?>... c) {
        for (Class<?> classz : c) {
            classes.add(classz);
        }
    }

    @Override
    public boolean canBePredefined() {
        return false;
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
        return true;
    }
}
