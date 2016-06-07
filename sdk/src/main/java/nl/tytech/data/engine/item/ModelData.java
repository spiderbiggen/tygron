/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.other.ModelObject;
import nl.tytech.data.engine.serializable.ParticleEmitterCoordinatePair;
import nl.tytech.util.StringUtils;

/**
 * ModelData
 * <p>
 * This item encapsulates the available models, this are NOT the individual models on the map.
 * </p>
 *
 *
 * @author Maxim Knepfle
 */
public class ModelData extends Item implements ModelObject {

    /**
     * Placement defines the behavior of the model in the placement algorithm.
     */
    public enum Placement {

        // square models
        SQUARE("square", true), //

        // landmarks
        LANDMARK("landmark", false), //

        // edges
        EDGE("edge", true), //
        EDGE_FILLER("edge_filler", false), //
        EDGE_CORNER("corner", true), //

        ; //

        public final static List<Placement> EMPTY = new ArrayList<Placement>();

        private String assetName;
        /**
         * Model takes a fixed dimension e.g. 1m, 3m, 10m wide
         */
        private boolean hasDimension;

        /**
         * @param enumDescription The game name of the group.
         * @param frequency The frequency of how often the model appears in a plot.
         */
        private Placement(String enumDescription, boolean hasDimension) {
            this.assetName = enumDescription.toLowerCase();
            this.hasDimension = hasDimension;

        }

        public String getAssetName() {
            return this.assetName;
        }

        public boolean hasDimension() {
            return hasDimension;
        }
    }

    public enum Rotation {
        /**
         * Rotation is fixed.
         */
        FIXED,
        /**
         * Rotation is randomly choosen from 0, 90, 180 or 360 degrees (quaters)
         */
        QUARTER,

        /**
         * Free rotation is given the model in-game between 0-360 degrees.
         */
        FREE
    }

    public enum Stack {

        /**
         * Model is complete only for landmarks.
         */
        COMPLETE(""),

        /**
         * Model is used as basement, e.g. poles for water houses.
         */
        BASEMENT("base"),

        /**
         * Model is used on ground level only
         */
        GROUND_LEVEL("gr"),

        /**
         * Models is used in between ground level and roof.
         */
        EXTRA_LEVEL("ex"),

        /**
         * Model is placed on top of all others.
         */
        TOP_LEVEL("tl"),

        /**
         * Models on top of a flat roof or a road. E.g. airco units, chimney's, trees, post signs.
         */
        FURNITURE("furniture");

        private String assetName;

        private Stack(String assetName) {
            this.assetName = assetName.toLowerCase();
        }

        public String getAssetName() {
            return assetName;
        }
    }

    public final static double SMALL_MODEL_HEIGHT = 3;

    public static final String MODEL_DIR = "Models/";

    private static final long serialVersionUID = 8050467134935662284L;

    @XMLValue
    private String name = "0_new model";

    @XMLValue
    private Placement placement = Placement.SQUARE;

    @XMLValue
    private Stack stack = Stack.COMPLETE;

    @XMLValue
    private int frequency = 1;

    @XMLValue
    private int variation = 1;

    @XMLValue
    private int dimension = 10;

    @XMLValue
    private Integer buffer = null;

    @XMLValue
    private boolean imported = false;

    @XMLValue
    private double modelHeight = -1;

    @XMLValue
    private boolean overrideModelHeight = false;

    @XMLValue
    @ListOfClass(ParticleEmitterCoordinatePair.class)
    private ArrayList<ParticleEmitterCoordinatePair> particleEmitters = new ArrayList<>();

    @XMLValue
    private boolean isAlpha = false;

    @XMLValue
    private boolean isShowCloseby = false;

    @XMLValue
    private double randomScale = 0;

    @XMLValue
    private boolean isInstanceable = false;

    @XMLValue
    private Rotation rotation = Rotation.FIXED;

    /**
     * Empty constructor.
     */
    public ModelData() {

    }

    public int getBuffer() {
        if (buffer == null) {
            return dimension;
        }
        return buffer.intValue();
    }

    /**
     * Force standard model asset naming conventions.
     * @return
     */
    private String getCorrectFileName() {

        String result = StringUtils.lowerCaseWithUnderScores(name);
        String extension = "_" + placement.getAssetName();

        if (placement.hasDimension()) {
            extension += "_" + dimension;
        }

        extension += "_" + stack.getAssetName() + "_" + this.variation;

        if (result.contains(StringUtils.LANG_SPLIT)) {
            result = result.replaceFirst(StringUtils.LANG_SPLIT, extension + StringUtils.LANG_SPLIT);
        } else {
            result += extension;
        }
        return result;
    }

    @Override
    public String getDescription() {
        return "Model name:\n " + getName() + "\n";
    }

    /**
     * Get dimension of the model
     */
    public int getDimension() {
        return dimension;
    }

    /**
     * Returns the file name of the model.
     *
     * @return
     */
    @Override
    public String getFileName() {

        String dir = MODEL_DIR + (this.getPlacement() == Placement.LANDMARK ? "Landmarks/" : "Details/");

        if (this.stack != Stack.COMPLETE) {
            return dir + getCorrectFileName();
        } else {
            return dir + StringUtils.lowerCaseWithUnderScores(name);
        }
    }

    public int getFrequency() {
        return this.frequency;
    }

    public double getModelHeightM() {
        return modelHeight;
    }

    /**
     * The name of the model in the game.
     *
     * @return
     */
    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<ParticleEmitterCoordinatePair> getParticleEmitters() {
        return particleEmitters;
    }

    /**
     * @return the group the model belongs to.
     */

    public final Placement getPlacement() {
        return placement;
    }

    public double getRandomScale() {
        return randomScale;
    }

    public Rotation getRotation() {
        return rotation;
    }

    public Stack getStack() {
        return stack;
    }

    @Override
    public boolean isAlpha() {
        return isAlpha;
    }

    public boolean isImported() {
        return imported;
    }

    public boolean isInstanceable() {
        return isInstanceable;
    }

    public boolean isOverrideModelHeight() {
        return overrideModelHeight;
    }

    public boolean isShowCloseby() {
        return isShowCloseby;
    }

    public void setModelHeightM(double modelHeightM) {
        this.modelHeight = modelHeightM;
    }

    @Override
    public boolean skipLOD() {
        return false;
    }

    @Override
    public String toString() {

        if (this.isShowCloseby) {
            String result = name;
            if (result.contains(StringUtils.LANG_SPLIT)) {
                result = result.replaceFirst(StringUtils.LANG_SPLIT, " (closeby)" + StringUtils.LANG_SPLIT);
            } else {
                result += " (closeby)";
            }
            return result;
        }
        if (stack != Stack.COMPLETE) {
            return this.getCorrectFileName();
        }
        return getName();
    }

    @Override
    public String validated(boolean startNewGame) {

        String result = StringUtils.EMPTY;

        // only odd values allowed for dimension
        if (dimension <= 0) {
            result += "\nModel: " + this.getName() + " " + this.getID() + " has an invalid dimension!";
        }

        for (ParticleEmitterCoordinatePair pair : this.getParticleEmitters()) {
            Integer particleID = pair.getParticleEmitterID();
            Object emitter = this.getItem(MapLink.PARTICLE_EMITTERS, particleID);
            if (emitter == null) {
                result += "\nMissing particle emitter item for id: " + particleID + " in model: " + this.getName() + " (" + this.getID()
                        + ").";
            }
        }
        return result;
    }
}
