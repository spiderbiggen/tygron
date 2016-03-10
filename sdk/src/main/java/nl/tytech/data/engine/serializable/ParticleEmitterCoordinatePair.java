/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.serializable;

import java.io.Serializable;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.Item;

/**
 * ParticleEmitterCoordinatePair
 * <p>
 * Defines a connection between an object (or center of world when no object is related) and emitter in 3D world Coordinates.
 * <p>
 *
 * @author Maxim Knepfle
 */
public class ParticleEmitterCoordinatePair implements Serializable {

    private static final long serialVersionUID = 4505099981435018509L;

    @XMLValue
    @ItemIDField("PARTICLE_EMITTERS")
    private Integer particleEmitter = Item.NONE;

    @XMLValue
    private double[] offsetCoordinate = new double[3];

    public Integer getParticleEmitterID() {
        return particleEmitter;
    }

    public double[] getWorldOffset() {
        return this.offsetCoordinate;
    }

    public void setParticleEmitter(Integer particleEmitter) {
        this.particleEmitter = particleEmitter;
    }

    public void setWorldOffset(double[] offset) {
        this.offsetCoordinate[0] = offset[0];
        this.offsetCoordinate[1] = offset[1];
        this.offsetCoordinate[2] = offset[2];
    }
}
