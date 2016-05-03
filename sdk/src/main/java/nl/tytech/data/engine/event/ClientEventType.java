/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.event;

import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.data.engine.serializable.PanelEnum;
import com.vividsolutions.jts.geom.Point;

/**
 * This class has been placed here so that it can be easily located for the xml serialization.
 *
 * @author Jeroen Warmerdam, Frank Baars
 */
public enum ClientEventType implements EventTypeEnum {

    @EventParamData(editor = true, desc = "Weather to show", params = { "Weather ID" })
    @EventIDField(links = { "WEATHERS" }, params = { 0 })
    ACTIVATE_WEATHER(Integer.class),

    @EventParamData(editor = true, desc = "Video to activate", params = { "Video ID" })
    @EventIDField(links = { "VIDEOS" }, params = { 0 })
    ACTIVATE_VIDEO(Integer.class),

    @EventParamData(editor = true, desc = "Blink Indicator icon in top menu", params = { "Indicator ID" })
    @EventIDField(links = { "INDICATORS" }, params = { 0 })
    BLINK_INDICATOR(Integer.class),

    @EventParamData(editor = true, desc = "Blink Overlay icon below map panel", params = { "Overlay ID" })
    @EventIDField(links = { "OVERLAYS" }, params = { 0 })
    BLINK_OVERLAY(Integer.class),

    @EventParamData(editor = true, desc = "Blink ActionMenu in left menu panel", params = { "ActionMenu", "Blinking on" })
    @EventIDField(links = { "ACTION_MENUS" }, params = { 0 })
    BLINK_CATEGORY(Integer.class, Boolean.class),

    @EventParamData(editor = true, desc = "Stop all blinking Indicators and ActionMenus", params = {})
    BLINK_STOP_ALL(),

    @EventParamData(editor = true, desc = "Change a zoning permit for a building, where the building is defined by a function and amount of floors", params = {
            "Zone ID", "Function ID", "Amount of floors" })
    @EventIDField(links = { "ZONES", "FUNCTIONS" }, params = { 0, 1 })
    CHANGE_ZONING_PERMIT(Integer.class, Integer.class, Integer.class),

    @EventParamData(editor = true, desc = "Text to show", params = { "Text" })
    FEEDBACK_PANEL_SHOW_TEXT(String.class),

    @EventParamData(editor = true, desc = "Hide the feedback text", params = {})
    FEEDBACK_PANEL_HIDE_TEXT(),

    @EventParamData(editor = true, desc = "Force the activation of a video", params = { "Video ID" })
    @EventIDField(links = { "VIDEOS" }, params = { 0 })
    FORCE_ACTIVATE_VIDEO(Integer.class),

    @EventParamData(editor = false, desc = "Goto start location", params = {})
    GOTO_START_LOCATION(),

    @EventParamData(editor = true, desc = "Respond to a Message with chosen answer", params = { "Message ID", "Answer ID" })
    @EventIDField(links = { "MESSAGES" }, params = { 0 })
    MESSAGE_ANSWERED(Integer.class, Integer.class),

    @EventParamData(editor = true, desc = "Respond to a Message with a written motivation", params = { "Message ID", "Motivation text" })
    @EventIDField(links = { "MESSAGES" }, params = { 0 })
    MESSAGE_MOTIVATION_ANSWER(Integer.class, String.class),

    @EventParamData(editor = true, desc = "Respond to a popup with a given change with the first answer of that popup", params = { "Chance to respond between 0 and 1" })
    RANDOM_ACTIVATE_POPUP_PERCENTAGE(Double.class),

    @EventParamData(editor = true, desc = "Respond to a Panel with chosen answer", params = { "Panel ID", "Answer ID" })
    @EventIDField(links = { "PANELS" }, params = { 0 })
    PANEL_ANSWER(Integer.class, Integer.class),

    @EventParamData(editor = true, desc = "Change the visualisation speed", params = { "Speed factor" })
    SET_VISUALISATION_SPEED(Double.class),

    @EventParamData(editor = true, desc = "Show the browser for ESRI Layers applicable to the city area", params = {})
    SHOW_BROWSER_MAP,

    @EventParamData(editor = true, desc = "Show a specific panel", params = { "Panel ID", "Visible" })
    @EventIDField(links = { "PANELS" }, params = { 0 })
    SHOW_CUSTOM_PANEL(Integer.class, Boolean.class),

    @EventParamData(editor = true, desc = "Show a specific overlay", params = { "Overlay ID" })
    @EventIDField(links = { "OVERLAYS" }, params = { 0 })
    SHOW_OVERLAY(Integer.class),

    @EventParamData(editor = true, desc = "Set a particular panel visible/invisible", params = { "Particular panel", "Visible" })
    SHOW_PANEL(PanelEnum.class, Boolean.class),

    @EventParamData(editor = true, desc = "Stop any weather that is visually active on a client", params = {})
    STOP_WEATHER(),

    @EventParamData(editor = true, desc = "Gives visual attention for a given point and amount of seconds", params = { "Point in the city",
            "Amount of seconds" })
    TILE_ATTENTION(Point.class, Double.class),

    ;

    private final List<Class<?>> classes = new ArrayList<Class<?>>();

    private ClientEventType(Class<?>... c) {
        for (Class<?> classz : c) {
            classes.add(classz);
        }
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
        return false;
    }

}
