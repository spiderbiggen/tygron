/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.item.annotations.DoNotSaveToInit;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.other.ModelObject;
import nl.tytech.data.engine.serializable.ParticleEmitterCoordinatePair;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.StringUtils;
import com.vividsolutions.jts.geom.Point;

/**
 * SpecialEffect
 * <p>
 * Special effect of particles.
 * </p>
 * @author Maxim Knepfle
 */
public class SpecialEffect extends Item implements ModelObject {

    /**
     *
     */
    private static final long serialVersionUID = 9020181395434283909L;

    @XMLValue
    @ListOfClass(ParticleEmitterCoordinatePair.class)
    private ArrayList<ParticleEmitterCoordinatePair> particleEmitters = new ArrayList<>();

    @DoNotSaveToInit
    @XMLValue
    private boolean active = false;

    @XMLValue
    private String name = "No name";

    @XMLValue
    protected Point point = JTSUtils.createPoint(0, 0, 0);

    public Point getCenter() {
        return point;
    }

    @Override
    public String getFileName() {
        return StringUtils.EMPTY;
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public List<ParticleEmitterCoordinatePair> getParticleEmitters() {

        return particleEmitters;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public boolean isAlpha() {
        return false;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setCenter(Point center) {
        this.point = center;
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
        return this.name;
    }
}
