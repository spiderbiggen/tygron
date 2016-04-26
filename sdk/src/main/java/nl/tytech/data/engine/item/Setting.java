/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import nl.tytech.core.item.annotations.DoNotSaveToInit;
import nl.tytech.core.item.annotations.EnumHtml;
import nl.tytech.core.net.serializable.EditOptions;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.net.serializable.SettingType;
import nl.tytech.data.core.item.AbstractSetting;
import nl.tytech.data.core.item.Moment;
import nl.tytech.data.engine.item.Building.ModelStyle;
import nl.tytech.data.engine.item.Function.Region;
import nl.tytech.data.engine.item.Overlay.OverlayType;
import nl.tytech.data.engine.serializable.BeamerPanelEnum;
import nl.tytech.data.engine.serializable.CameraSpeedEnum;
import nl.tytech.data.engine.serializable.FacilitatorTabEnum;
import nl.tytech.data.engine.serializable.PanelEnum;
import nl.tytech.data.engine.serializable.ViewEnum;
import nl.tytech.locale.TCurrency;
import nl.tytech.locale.unit.UnitSystemType;
import nl.tytech.util.StringUtils;

/**
 * Setting
 * <p>
 * Setting keeps a set of general purpose game settings.
 * <p>
 *
 *
 * @author Maxim Knepfle
 */

public class Setting extends AbstractSetting<Setting.Type> {

    // (Frank) This enumerator is used by an EnumOrderedItem. Please add new
    // enumerator-values at the end of this enumerator.
    @EnumHtml(ordinal = { 6 })
    public enum Type implements SettingType {
        /**
         * Currently active game level.
         */
        @DoNotSaveToInit
        ACTIVE_LEVEL(Integer.class, "0"),

        /**
         * When true players can interact with the game..
         */
        @DoNotSaveToInit
        ALLOW_USER_INTERACTION(Boolean.class, "true"),

        /**
         * Allowed raise of the
         */
        @Deprecated
        ALLOWED_WATER_LEVEL_INCREASE(Double.class, "0.3"),

        /** Basic panels that are not triggered by special functionality. */
        BASE_PANELS(PanelEnum.class, "MESSAGE_PANEL HOVER_PANEL MAP_PANEL TOPBAR_PANEL LEFT_MENU_PANEL FEEDBACK_PANEL"),

        /**
         * Beamer Layer (Which layer should be displayed by the Beamer (HEAT, QUALITY_OF_LIFE)
         */
        @DoNotSaveToInit
        @Deprecated
        BEAMER_LAYER(OverlayType.class, "OWNERSHIP"),

        /**
         * Beamer Overlay ID
         */
        @DoNotSaveToInit
        BEAMER_ITEM_ID(Integer.class, "-1"),

        /**
         * Ordinal of active beamer score panel
         */
        @DoNotSaveToInit
        BEAMER_SCORE_PANEL(BeamerPanelEnum.class, "MAP_CURRENT"),

        /**
         * Ordinal of active beamer panel
         */
        @DoNotSaveToInit
        BEAMER_VIEW(ViewEnum.class, ViewEnum.BEAMER_WELCOME.name()),

        /**
         * Camera Speed (SLOW, NORMAL)
         */
        CAMERA_SPEED(CameraSpeedEnum.class, "NORMAL"),

        /**
         * Commercial name
         */
        COMMERCIAL_NAME(String.class, "Tygron Next Generation Planner"),

        /** Default price in EUR to lower one m3 ground */
        DEFAULT_GROUND_LOWER_PRICE_M3(Double.class, "50"),

        /** Default price in EUR to raise one m3 ground */
        DEFAULT_GROUND_RAISE_PRICE_M3(Double.class, "50"),

        /** Default price of one m2 ground in euro */
        DEFAULT_GROUNDPRICE_M2(Double.class, "400"),

        /**
         * When true replace stakeholders with AI that are not played by humamn players.
         */
        ENABLE_AI(Boolean.class, "false"),

        /**
         * When true enable the digital game assistant
         */
        ENABLE_ASSISTANT(Boolean.class, "false"),

        /**
         * Project description in intro panels
         */
        PROJECT_DESCRIPTION(String.class, "<html><body>Project description</body></html>"),

        /**
         * Project name
         */
        PROJECT_NAME(String.class, "Project Name"),

        /**
         * When using the hotspot search panel start at this hotspot ID.
         */
        HOTSPOT_SEARCH_ROOT(Integer.class, "0"),

        /**
         * Idle time settings [ Max. idle time in seconds, Event bundle ID ]
         */
        IDLE_TIME_SETTING(Integer[].class, "300 -1"),

        /**
         * Name of the intro image file,
         */
        INTRO_FILE_NAME(String.class, "default.png"),

        /** Game type **/
        EDIT_ZONE(String.class, EditOptions.GREEN.name()),

        /**
         * When true messages send to the gameleader or non-human players are auto-responded.
         */
        MESSAGES_AUTO_RESPOND(Boolean.class, "true"),

        PAUSE_PANEL_USE_TEXT(Boolean.class, "true"),

        /** Active weather item for rain manager */
        RAINMAN_ACTIVE_WEATHER_ID(Integer.class, "0"),

        /** When a building is placed an interaction popup needs to be shown. */
        SHOW_BUILDING_AND_MEASURE_POPUP(Boolean.class, "true"),

        /**
         * Is special option measure proposal active
         */
        SHOW_MEASURE_PROPOSAL(Boolean.class, "false"),

        /**
         * When a waterway is placed an interaction popup (for the waterboard) needs to be shown.
         */
        SHOW_WATER_POPUP(Boolean.class, "false"),

        /**
         * Name of the sat file
         */
        SATELLITE_FILE_NAME(String.class, "default"),

        /**
         * Team name
         */
        @DoNotSaveToInit
        TEAM_NAME(String.class, ""),

        /**
         * Replaced by map width in meters
         */
        @Deprecated
        TILEMAP_WIDTH(Integer.class, "0"),

        /**
         * Grid cell size in meters, default is 10
         */
        GRID_CELL_SIZE_M(Integer.class, "10"),

        /**
         * Map size in X and Y in meters, default 0 (no map)
         */
        MAP_WIDTH_METERS(Integer.class, "0"),

        /**
         * Gives progress feedback for verifier TODO: maybe move the editor settings?
         */
        @DoNotSaveToInit
        VERIFIER_FEEDBACK(String.class, "-"),

        /**
         * Enums with the names of the intro panels
         */
        VIEW_FLOW(ViewEnum.class, "CONTRIBUTORS INTRODUCTION TEAM_NAME STAKEHOLDER_SELECTION ASSIGNMENT MAIN"),

        /**
         * Super user message to all clients
         */
        @DoNotSaveToInit
        SUPER_USER_MESSAGE(String.class, StringUtils.EMPTY),

        VISIBLE_FACILITATOR_TABS(FacilitatorTabEnum.class,
                "SERVER SETTINGS LEVEL ECONOMY MEASURE TEAM_STATUS TEAM_STAKEHOLDER TEAM_BEAMER TEAM_MESSAGE TEAM_SUBSIDY"),

        /** Enable/disable water using this setting */
        WATER_ENABLED(Boolean.class, "true"),

        /**
         * True when the wizard was succesfully completed (for this point the game is loadable).
         */
        WIZARD_FINISHED(Boolean.class, "false"),

        /**
         * Region game is played in.
         */
        REGION(Region.class, "NORTHWESTERN_EUROPE"),

        /**
         * TODO: Maxim move WaterType server side? generalise
         */
        WATER_TYPE(String.class, "CANAL"),

        SKY_TYPE(String.class, "DEFAULT"),

        TRAFFIC_MULTIPLIER(Double.class, "1.0"),

        WIND_DIRECTION(Integer.class, "45"),

        WIND_SPEED_M_PER_S(Integer.class, "5"), // default 5 m/s for west NL

        TRANSPARENT_WATER(Boolean.class, "false"),

        CURRENCY(TCurrency.class, TCurrency.EURO.name()),

        MEASUREMENT_SYSTEM_TYPE(UnitSystemType.class, UnitSystemType.SI.name()),

        FIXED_SUN_DAY_ANGLE(Double.class, "0.9"),

        FIXED_SUN_YEAR_ANGLE(Integer.class, "40"),

        /**
         * Give players by default 2 minutes to cancel their proposed constructions.
         */
        DEFAULT_PLAYER_CANCEL_DELAY_TIME(Long.class, "" + (Moment.MINUTE * 2l)),

        DEFAULT_MEASURE_CONSTRUCTION_TIME_MONTHS(Double.class, "3.0"),

        RESERVED_LAND(String.class, "MULTIPOLYGON EMPTY"),

        SURROUNDING_MAP_EXTEND_M(Integer[].class, DEFAULT_SURROUNDING_EXTEND + " " + DEFAULT_SURROUNDING_EXTEND),

        /*
         * Extend of the loaded map
         */
        WORLD_REFERENCE_POINT(Double[].class, "0 0"),

        @DoNotSaveToInit
        @Deprecated
        PROJECT_ASSETS_VERSION(Integer.class, "1"),

        @Deprecated
        WORLD_REFERENCE_POINT_MERC(Double[].class, "DEPRECATED"),

        WATER_HEIGHT(Double.class, "" + NO_WATER_HEIGHT_SET),

        MODEL_STYLE(ModelStyle.class, "TEXTURED"),

        SATELLITE_BRIGHTNESS(Double.class, "-1"),

        /**
         * Beamer active item maplink
         */
        @DoNotSaveToInit
        BEAMER_MAPLINK(MapLink.class, MapLink.INDICATORS.name()),

        ;

        // NOTE: Use NOT_USED values first before adding new values!!!

        private String defaultValue;
        private Class<?> valueType;

        private Type(Class<?> valueType, String defaultValue) {
            this.valueType = valueType;
            this.defaultValue = defaultValue;
        }

        @Override
        public String getDefaultValue() {
            return defaultValue;
        }

        @Override
        public Class<?> getValueType() {
            return valueType;
        }
    }

    public final static double NO_WATER_HEIGHT_SET = -20000;

    public static final String INTRO_IMAGE_LOCATION = "Gui/Images/Intro/";

    public static final String SATELLITE_IMAGE_LOCATION = "Satellite/";

    public static final int DEFAULT_SURROUNDING_EXTEND = 500;

    public static final int MAX_MAP_SIZE = 20000;

    public static final String EXCEL_DIR = "Excel/";

    private static final long serialVersionUID = 3730370813978282986L;

    @Override
    public Type[] getEnumValues() {
        return Type.values();
    }
}
