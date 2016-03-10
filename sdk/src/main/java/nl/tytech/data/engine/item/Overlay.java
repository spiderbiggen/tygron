/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import nl.tytech.core.item.annotations.AssetDirectory;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.EditOptions;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.data.core.item.UniqueNamedItem;
import nl.tytech.data.engine.item.GlobalIndicator.GlobalIndicatorType;
import nl.tytech.data.engine.item.Indicator.TypeInterface;
import nl.tytech.data.engine.item.PersonalIndicator.PersonalIndicatorType;
import nl.tytech.data.engine.serializable.FunctionValue;
import nl.tytech.data.engine.serializable.LegendEntry;
import nl.tytech.util.MathUtils;
import nl.tytech.util.ObjectUtils;
import nl.tytech.util.StringUtils;

/**
 * Overlay
 * <p>
 * Overlays are put over the map and give extra details.
 * </p>
 * @author Maxim Knepfle
 */
public class Overlay extends UniqueNamedItem {

    public enum OverlayType {

        OWNERSHIP(Overlay.class, EditOptions.GREEN, false, null),

        TRAFFIC_FLOW(Overlay.class, EditOptions.GREEN, false, null),

        //
        UNDERGROUND(Overlay.class, EditOptions.GREEN, false, null),

        //
        ZONING(Overlay.class, EditOptions.GREEN, false, null),

        //
        DISTANCE_ZONE(GridOverlay.class, EditOptions.GREEN, null, FunctionValue.DISTANCE_ZONE_M, 1, -100),

        //
        DISTANCE_SIGHT(GridOverlay.class, EditOptions.GREEN, null, FunctionValue.DISTANCE_ZONE_M, 1, -100),

        //
        GROUND_WATER_HI(GroundWaterOverlay.class, EditOptions.GREEN, null, null, 100, -100),

        //
        GROUND_WATER_LOW(GroundWaterOverlay.class, EditOptions.GREEN, null, null, 100, -100),

        //
        SUBSIDENCE(SubsidenceOverlay.class, EditOptions.GREEN, null, null, 1000, -100),

        //
        HEAT(GridOverlay.class, EditOptions.GREEN, GlobalIndicatorType.HEAT, FunctionValue.HEAT_EFFECT, 100, 0),

        //
        LIVABILITY(GridOverlay.class, EditOptions.GREEN, GlobalIndicatorType.LIVABILITY, FunctionValue.LIVABILITY_EFFECT, 100, 0),

        //
        TRAFFIC_NOISE(GridOverlay.class, EditOptions.GREEN, null, null, 4, -300),

        //
        TRAFFIC_NO2(NO2Overlay.class, EditOptions.GREEN, null, null, -50, 100),

        //
        @Deprecated
        WATER_STRESS(Overlay.class, EditOptions.RED, false, null),

        FLOODING_AREA(Overlay.class, EditOptions.GREEN, false, null),

        AREAS(AreaOverlay.class, EditOptions.GREEN, true, null),

        //
        PIPE_NETWORK_FINANCIAL(Overlay.class, EditOptions.HEAT, false, null),

        //
        PIPE_NETWORK_TECHNICAL(Overlay.class, EditOptions.HEAT, false, PersonalIndicatorType.PIPE_NETWORK_CONNECTED),

        //
        FUNCTION_HIGHLIGHT(FunctionsOverlay.class, EditOptions.GREEN, true, null),

        //
        CUSTOM_COLOR_AREAS(CustomColorOverlay.class, EditOptions.GREEN, true, null),

        //
        VACANCY(Overlay.class, EditOptions.GREEN, false, null),

        //
        IMAGE(ImageOverlay.class, EditOptions.GREEN, true, null),

        //
        PIPE_CLUSTERS(PipeClusterOverlay.class, EditOptions.HEAT, true, null),

        //
        ZIP_CODES(Overlay.class, EditOptions.GREEN, false, null);

        public final static OverlayType[] getActiveValues(EditOptions editOptions) {

            List<OverlayType> types = new ArrayList<>();
            for (OverlayType type : OverlayType.values()) {
                Deprecated depAnno = ObjectUtils.getEnumAnnotation(type, Deprecated.class);
                if (depAnno != null) {
                    continue;
                }

                if (type.editOptions.ordinal() <= editOptions.ordinal()) {
                    types.add(type);
                }
            }
            Collections.sort(types, ObjectUtils.CASE_INSENSITIVE_ORDER);
            return types.toArray(new OverlayType[types.size()]);
        }

        private Indicator.TypeInterface indicator = null;

        private boolean allowsMultipleInstances = false;

        private boolean grid = false;

        private FunctionValue effect = null;

        private EditOptions editOptions;

        private double overlayMultiplier = 1;

        private double overlayOffset = 1;

        private Class<? extends Overlay> clasz;

        /**
         * Grid overlay
         */
        private OverlayType(Class<? extends GridOverlay> clasz, EditOptions editOptions, Indicator.TypeInterface indicator,
                FunctionValue effect, double overlayMultiplier, double overlayOffset) {
            this.editOptions = editOptions;
            this.indicator = indicator;
            this.grid = true;
            this.effect = effect;
            this.overlayMultiplier = overlayMultiplier;
            this.overlayOffset = overlayOffset;
            this.clasz = clasz;

        }

        /**
         * Normal overlay
         */
        private OverlayType(Class<? extends Overlay> clasz, EditOptions editOptions, boolean multipleInstancesAllowed,
                Indicator.TypeInterface indicator) {
            this.editOptions = editOptions;
            this.allowsMultipleInstances = multipleInstancesAllowed;
            this.indicator = indicator;
            this.clasz = clasz;

        }

        public final boolean allowsMultipleInstances() {
            return allowsMultipleInstances;
        }

        public final EditOptions getEditOptions() {
            return editOptions;
        }

        public final FunctionValue getEffect() {
            return effect;
        }

        public final Overlay getNewInstance() {
            return ObjectUtils.newInstanceForArgs(clasz, new Object[0]);
        }

        public final Indicator.TypeInterface getRelatedIndicator() {
            return indicator;
        }

        public final boolean isGrid() {
            return grid;
        }

        public final byte toOverlayValue(double originalValue) {
            // to int based overlay value
            int overlayValue = (int) Math.round((originalValue * overlayMultiplier) + overlayOffset);
            // move int to byte acceptable value
            return (byte) MathUtils.clamp(overlayValue, Byte.MIN_VALUE, Byte.MAX_VALUE);
        }
    }

    private static final String GUI_IMAGES_PANELS_MAP_PANEL_MAP_ICOONS = "Gui/Images/Panels/MapPanel/Icons/";

    /**
     *
     */
    private static final long serialVersionUID = 3663268509035999152L;

    @XMLValue
    private byte[] data = new byte[0];

    @XMLValue
    @AssetDirectory(GUI_IMAGES_PANELS_MAP_PANEL_MAP_ICOONS)
    private String iconFileName = StringUtils.EMPTY;

    @XMLValue
    private int colorOffset = 127;

    @XMLValue
    private double colorMultiplier = 1;

    @XMLValue
    private OverlayType type = null;

    @XMLValue
    private int sortIndex = 50;

    @XMLValue
    @ListOfClass(LegendEntry.class)
    private ArrayList<LegendEntry> legend = new ArrayList<>();

    public double getColorMultiplier() {
        return colorMultiplier;
    }

    public int getColorOffset() {
        return colorOffset;
    }

    public String getIconFileName() {
        return GUI_IMAGES_PANELS_MAP_PANEL_MAP_ICOONS + iconFileName;
    }

    public List<LegendEntry> getLegend() {
        return legend;
    }

    public int getSortIndex() {
        return sortIndex;
    }

    public OverlayType getType() {
        return type;
    }

    public boolean isVisibleForStakeholder(Stakeholder stakeholder) {

        if (stakeholder == null) {
            return false;
        }
        if (type == null) {
            return true;
        }
        TypeInterface type = this.type.getRelatedIndicator();
        if (type == null) {
            return true;
        }
        ItemMap<Indicator> indicators = this.getMap(MapLink.INDICATORS);
        for (Indicator indicator : indicators) {
            if (indicator.getType() == type && stakeholder.getCurrentIndicatorWeight(indicator) > 0) {
                return true;
            }
        }
        return false;
    }

    public void setIconFileName(String iconFileName) {
        this.iconFileName = iconFileName;

    }

    public void setSortIndex(int index) {
        this.sortIndex = index;
    }

    public void setType(OverlayType overlayType) {
        this.type = overlayType;

    }

    public double toOriginalValue(byte overlayValue) {
        OverlayType type = this.getType();
        return (overlayValue - type.overlayOffset) / type.overlayMultiplier;
    }
}
