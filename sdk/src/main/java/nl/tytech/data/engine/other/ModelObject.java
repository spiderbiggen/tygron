/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.other;

import java.util.List;
import nl.tytech.data.engine.serializable.ParticleEmitterCoordinatePair;

/**
 *
 * Objects in 3D world that can be visualized as a model.
 * @author Maxim Knepfle
 *
 */
public interface ModelObject {

    public String getFileName();

    public String getName();

    public List<ParticleEmitterCoordinatePair> getParticleEmitters();

    public boolean isAlpha();

    public boolean skipLOD();

}
