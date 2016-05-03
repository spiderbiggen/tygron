/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.item.ModelData.Placement;
import nl.tytech.data.engine.serializable.MapType;
import nl.tytech.data.engine.serializable.Vector3d;
import nl.tytech.util.JTSUtils;
import com.vividsolutions.jts.geom.Polygon;

/**
 * TilemapLandmarkBuilding
 * <p>
 * Extension of the TilemapBuilding for landmarks. Landmarks can have an offset/rotation.
 * </p>
 * @author Maxim Knepfle
 */
public class LandmarkBuilding extends Building {

    /**
     *
     */
    private static final long serialVersionUID = 8687165638585869472L;

    @XMLValue
    private Vector3d offset = new Vector3d();

    @XMLValue
    private int rotation = 0;

    public LandmarkBuilding() {
        super();
    }

    public LandmarkBuilding(final Integer functionTypeID, final String name) {
        super(functionTypeID, name);
    }

    /**
     * Get the average height of the function's ground floor.
     * @return
     */
    @Override
    public double getHeightM(boolean includeRoofAndFurniture) {

        Function function = this.getFunction();
        for (ModelData model : function.getModels()) {
            if (model.getPlacement() == Placement.LANDMARK) {
                return model.getModelHeightM();
            }
        }
        return super.getHeightM(includeRoofAndFurniture);
    }

    /**
     * @return the landmarkID
     */
    public final Integer getLandmarkID() {

        Function function = this.getFunction();
        for (ModelData model : function.getModels()) {
            if (model.getPlacement() == Placement.LANDMARK) {
                return model.getID();
            }
        }
        return Item.NONE;
    }

    public final Polygon getLargestPolygon(final MapType mapType) {

        Polygon largest = null;
        for (Polygon p : JTSUtils.getPolygons(getMultiPolygon(mapType))) {
            if (largest == null || largest.getArea() < p.getArea()) {
                largest = p;
            }
        }
        return largest;
    }

    public Vector3d getOffset() {
        return offset;
    }

    public int getRotationDegrees() {
        return rotation;
    }

    @Override
    public boolean isLandmark() {
        return true;
    }

    public void setRotationDegrees(int rotation) {
        this.rotation = rotation;
    }

    @Override
    public String toString() {
        return "(Landmark) " + this.getName();
    }
}
