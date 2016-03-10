/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.Item;
import nl.tytech.util.StringUtils;

/**
 * Particle emitter
 * <p>
 * This item encapsulates the available models, this are NOT the individual models on the map.
 * </p>
 * 
 * 
 * @author Christian Paping
 */
public class ParticleEmitterModel extends Item {

    /** Generated serialVersionUID */
    private static final long serialVersionUID = 5059475154747750949L;

    @XMLValue
    private String name = "0_new Particle emitter";

    @XMLValue
    private String fileName = StringUtils.EMPTY;

    @XMLValue
    private boolean wind = false;

    @XMLValue
    private boolean zBuffer = false;

    /**
     * Empty constructor.
     */
    public ParticleEmitterModel() {

    }

    /**
     * Returns the file name of the particle emitter.
     * 
     * @return
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * The name of the particle emitter in the game.
     * 
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @return the zBuffer for this particle emitter.
     */
    public final boolean getZBuffer() {
        return this.zBuffer;
    }

    public boolean isWindDirected() {
        return wind;
    }

    @Override
    public String toString() {
        return name;
    }

}
