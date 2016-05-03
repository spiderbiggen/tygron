/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.item.Function.Region;
import nl.tytech.data.engine.other.ModelObject;
import nl.tytech.data.engine.serializable.ParticleEmitterCoordinatePair;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;

/**
 * ModelData
 * <p>
 * This item encapsulates the available models, this are NOT the individual models on the map.
 * </p>
 *
 *
 * @author Maxim Knepfle
 */
public class UnitData extends Item implements ModelObject {

    public enum TrafficType {

        // NOx No2 g/km 2015 niet snelwegen stad normaal licht motorvoertuig bron: infomill
        CAR(true, new double[] { 0.36, 0.09 }, new double[] { 0.57, 0.14 }),

        // ?
        ECAR(true, new double[] { 0, 0 }, new double[] { 0, 0 }),

        // NOx No2 g/km 2015 niet snelwegen stad normaal middelzwaar motorvoertuig bron: infomill
        VAN(true, new double[] { 6.9, 0.41 }, new double[] { 11.32, 0.68 }),

        // NOx no2 g/km 2015 niet snelwegen stad normaal zwaar motorvoertuig bron: infomill
        TRUCK(true, new double[] { 8.99, 0.46 }, new double[] { 14.74, 0.76 }),

        // NOx no2 g/km 2015 niet snelwegen stad stagnered bussen bron: infomill
        BUS(true, new double[] { 5.88, 0.61 }, new double[] { 9.40, 0.98 }),

        PEDESTRIAN(false, new double[] { 0, 0 }, new double[] { 0, 0 }),

        SHIP(false, new double[] { 0, 0 }, new double[] { 0, 0 }),

        RAIL(false, new double[] { 0, 0 }, new double[] { 0, 0 }),

        AIR(false, new double[] { 0, 0 }, new double[] { 0, 0 }),

        ;

        /**
         * Static reference to prevent creating new value arrays each time called.
         */
        public final static TrafficType[] VALUES = TrafficType.values();

        private double[] normalEmission, congestedEmission;

        private boolean carBased;

        private TrafficType(boolean carBased, double[] normalEmission, double[] congestedEmission) {
            this.normalEmission = normalEmission;
            this.congestedEmission = congestedEmission;
            this.carBased = carBased;
        }

        public double[] getDefaultEmission(boolean congested) {
            return congested ? congestedEmission : normalEmission;
        }

        public boolean isCarBased() {
            return carBased;
        }
    }

    public static final String UNIT_DIR = "Models/Units/";

    private static final long serialVersionUID = 8050467134935662284L;

    @XMLValue
    @ListOfClass(Region.class)
    private ArrayList<Region> regions = new ArrayList<>();

    @XMLValue
    @ListOfClass(TColor.class)
    private ArrayList<TColor> colors = new ArrayList<TColor>(Arrays.asList(new TColor[] {
            // typical car colors
            TColor.GRAY, TColor.LIGHT_GRAY, TColor.BLACK, TColor.WHITE, TColor.GRAY, new TColor(125, 29, 10), new TColor(7, 86, 24),
            new TColor(25, 10, 118) }));

    @XMLValue
    private String name = "0_new model";

    @XMLValue
    private TrafficType type = TrafficType.CAR;

    @XMLValue
    private String fileName = StringUtils.EMPTY;

    @XMLValue
    private boolean isAlpha = false;

    @XMLValue
    @ListOfClass(ParticleEmitterCoordinatePair.class)
    private ArrayList<ParticleEmitterCoordinatePair> particleEmitters = new ArrayList<>();

    @XMLValue
    private boolean active = false;

    public UnitData() {

    }

    public ArrayList<TColor> getColors() {
        /**
         * Try override first
         */
        UnitDataOverride unitDataOverride = this.getItem(MapLink.UNIT_DATA_OVERRIDES, this.getID());
        if (unitDataOverride != null && unitDataOverride.hasColors()) {
            return unitDataOverride.getColors();
        } else {
            return colors;
        }
    }

    @Override
    public String getFileName() {
        if (!StringUtils.containsData(fileName)) {
            return StringUtils.EMPTY;
        }
        return UNIT_DIR + fileName;
    }

    @Override
    public String getName() {

        /**
         * Try override first
         */
        UnitDataOverride unitDataOverride = getUnitDataOverride();
        if (unitDataOverride != null && StringUtils.containsData(unitDataOverride.getName())) {
            return unitDataOverride.getName();
        } else {
            return name;
        }
    }

    @Override
    public List<ParticleEmitterCoordinatePair> getParticleEmitters() {
        // XXX: maxim: disabled particle emitters for now, units are instanced thus can not have particles flying around
        return new ArrayList<>();// particleEmitters;
    }

    public TrafficType getTrafficType() {
        return type;
    }

    public UnitDataOverride getUnitDataOverride() {
        return this.getItem(MapLink.UNIT_DATA_OVERRIDES, this.getID());
    }

    public boolean hasUnitDataOverride() {
        return this.getItem(MapLink.UNIT_DATA_OVERRIDES, this.getID()) != null;
    }

    public boolean isActive() {
        /**
         * Try override first
         */
        UnitDataOverride unitDataOverride = getUnitDataOverride();
        if (unitDataOverride != null && unitDataOverride.isActive() != null) {
            return unitDataOverride.isActive();
        } else {
            return active;
        }

    }

    @Override
    public boolean isAlpha() {
        return isAlpha;
    }

    /**
     * When true this unit is either region or part of given region.
     * @param region
     * @return
     */
    public boolean isInRegion(Region region) {
        return regions.size() == 0 || region == null || regions.contains(region);
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean skipLOD() {
        return false;
    }

    @Override
    public String toString() {
        return getName();
    }
}
