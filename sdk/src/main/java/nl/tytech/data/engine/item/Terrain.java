/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.net.serializable.PolygonItem;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.item.UniqueNamedItem;
import nl.tytech.data.engine.item.Building.Detail;
import nl.tytech.data.engine.item.ClientWord.ClientTerms;
import nl.tytech.data.engine.item.Global.ReadOnly;
import nl.tytech.data.engine.other.Action;
import nl.tytech.data.engine.serializable.MapType;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;
import nl.tytech.util.logger.TLogger;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

/**
 * @author Maxim Knepfle
 */
public class Terrain extends UniqueNamedItem implements PolygonItem {

    public enum TerrainType {

        /**
         * Normal grassland
         */
        GRASSLAND(false, -6, 0, 1, 0.7f, ClientTerms.BEHAVIOUR_GRASSLAND, "grass.png", new TColor(94, 106, 37, 255)),
        /**
         * Water connected to the main river system.
         */
        BOEZEM_WATER(true, -10, 0, 5, 0.0f, ClientTerms.BEHAVIOUR_BOEZEM_WATER, "water.png", new TColor(78, 109, 137, 255)),
        /**
         * Waterways part of the polder's water system.
         */
        POLDER_WATER(true, -10, 0, 5, 0.0f, ClientTerms.BEHAVIOUR_POLDER_WATER, "water.png", new TColor(78, 109, 137, 255)),
        /**
         * Beach
         */
        BEACH(false, -6, 0, 5, 0.0f, ClientTerms.BEHAVIOUR_BEACH, "strand.png", TColor.YELLOW),
        /**
         * Breakwater
         */
        BREAKWATER(false, -6, 0, 0, 0.0f, ClientTerms.BEHAVIOUR_BREAKWATER, "strand.png", TColor.GRAY),
        /**
         * Dikes
         */
        DIKE(false, -6, 0, 1, 0.7f, ClientTerms.BEHAVIOUR_DIKE, "grass.png", new TColor(55, 200, 55, 255)),
        /**
         * Dunes
         */
        DUNES(false, -6, 0, 5, 0.3f, ClientTerms.BEHAVIOUR_DUNES, "strand.png", TColor.YELLOW),
        /**
         * Concrete
         */
        CONCRETE(false, 4, 0, -1, 0.0f, ClientTerms.BEHAVIOUR_CONCRETE, "buiten.png", TColor.GRAY);

        /**
         * Static reference to prevent creating new value arrays each time called.
         */
        public final static TerrainType[] VALUES = TerrainType.values();
        public final static double WATER_HEIGHT_OFFSET = -3.0;

        private int heat;
        private int safety;
        private int livability;
        private boolean water;
        private double greenFactor;
        private String imageName;
        private ClientTerms term;
        private TColor color;

        private TerrainType(boolean water, int heat, int safety, int livability, double greenfactor, ClientTerms term, String imageName,
                TColor color) {

            this.water = water;
            this.heat = heat;
            this.safety = safety;
            this.livability = livability;
            this.greenFactor = greenfactor;
            this.term = term;
            this.imageName = imageName;
            this.color = color;
        }

        public TColor getColor() {
            return color;
        }

        public double getGreenFactor() {
            return this.greenFactor;
        }

        public int getHeatEffect() {
            return heat;
        }

        public String getImageName() {
            if (!StringUtils.containsData(imageName)) {
                return StringUtils.EMPTY;
            }
            return Action.GUI_IMAGES_ACTIONS + imageName;
        }

        public int getLivabilityEffect() {
            return livability;
        }

        public int getSafeZoneDistanceM() {
            return safety;
        }

        public ClientTerms getTerm() {
            return this.term;
        }

        public double getTerrainValue(TerrainValue value) {
            switch (value) {
                case GREEN_FACTOR:
                    return this.greenFactor;
                case HEAT_EFFECT:
                    return this.heat;
                case LIVABILITY_EFFECT:
                    return this.livability;
                default:
                    return 0d;
            }
        }

        public boolean isWater() {
            return water;
        }

    }

    public enum TerrainValue {

        GREEN_FACTOR, HEAT_EFFECT, LIVABILITY_EFFECT;

        public static final TerrainValue[] VALUES = values();

    }

    /**
     *
     */
    private static final long serialVersionUID = -5100582384433357166L;

    @XMLValue
    private MultiPolygon current = JTSUtils.EMPTY;

    @XMLValue
    private MultiPolygon maquette = null;

    @XMLValue
    private TerrainType type = null;

    @XMLValue
    private boolean showSatellite = true;

    private int heightmapVersion = Item.NONE;
    private int maqHeightmapVersion = Item.NONE;

    public double getDetail(Detail detail, double terrainM2) {
        switch (detail) {
            case WATER_STORAGE_TRADITIONAL_M3:
                if (getType() == TerrainType.POLDER_WATER) {
                    Global global = this.<Global> getItem(MapLink.GLOBALS, ReadOnly.WATER_STORAGE_ALLOWED_WATER_LEVEL_INCREASE.name());
                    double increase = global == null ? ReadOnly.WATER_STORAGE_ALLOWED_WATER_LEVEL_INCREASE.getDefaultValue() : global.getActualValue();
                    return terrainM2 * increase;
                } else {
                    return 0;
                }

            case GREEN_M2:
                return terrainM2 * getType().greenFactor;

            default:
                return 0;
        }
    }

    public int getHeightmapVersion(MapType mapType) {
        if (mapType == null) {
            return Math.max(maqHeightmapVersion, heightmapVersion);
        }
        if (mapType == MapType.MAQUETTE) {
            return maqHeightmapVersion;
        }
        return heightmapVersion;
    }

    public MultiPolygon getMultiPolygon(MapType mapType) {

        /**
         * Return current when maquette is still null.
         */
        if (mapType == MapType.CURRENT) {
            return current;
        } else if (maquette == null) {
            return current;
        } else {
            return maquette;
        }
    }

    @Override
    public MultiPolygon[] getQTMultiPolygons() {
        return new MultiPolygon[] { current, maquette };
    }

    public TerrainType getType() {
        return type;
    }

    public boolean hasDifferentMaquette() {
        return this.maquette != null;
    }

    public boolean isShowSatellite() {
        return showSatellite;
    }

    @Override
    public void reset() {
        super.reset();
        this.current.setUserData(null);
        for (Polygon polygon : JTSUtils.getPolygons(current)) {
            polygon.setUserData(null);
        }
        if (this.maquette != null) {
            this.maquette.setUserData(null);
            for (Polygon polygon : JTSUtils.getPolygons(maquette)) {
                polygon.setUserData(null);
            }
        }
    }

    public void setMultiPolygon(MapType mapType, MultiPolygon mp) {

        if (mapType == MapType.CURRENT) {
            this.current = mp;
            this.heightmapVersion = this.getVersion();
        } else {
            this.maquette = mp;
            this.maqHeightmapVersion = this.getVersion();
        }
    }

    public void setShowSatellite(boolean showSatellite) {
        this.showSatellite = showSatellite;
    }

    public void setType(TerrainType type) {
        this.type = type;
    }

    @Override
    protected void updateInternalVersion(int version) {
        this.heightmapVersion = version;
        this.maqHeightmapVersion = version;
    }

    @Override
    public String validated(boolean startNewGame) {

        if (this.type == null) {
            if (this.getID() >= TerrainType.VALUES.length) {
                return "Cannot convert Terrain ID " + this.getID() + " to valid Type.";
            }
            type = TerrainType.VALUES[this.getID()];
            this.setName(this.getID().toString());
            TLogger.info("Converting Terrain ID to: " + type);
        }

        return super.validated(startNewGame);
    }
}
