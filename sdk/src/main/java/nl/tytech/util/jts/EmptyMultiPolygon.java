/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.util.jts;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Not modifiable (?) Empty MP
 * @author Maxim Knepfle
 *
 */

public class EmptyMultiPolygon extends MultiPolygon {

    /**
     *
     */
    private static final long serialVersionUID = 3451947641123390894L;

    private final static Coordinate[] COORDINATES = new Coordinate[0];

    private final static double ZERO = 0d;

    private final MultiLineString BOUNDARY;

    public EmptyMultiPolygon(GeometryFactory factory) {
        super(new Polygon[0], factory);
        BOUNDARY = new MultiLineString(null, factory);
    }

    @Override
    public final double getArea() {
        return ZERO;
    }

    @Override
    public final Geometry getBoundary() {
        return BOUNDARY;
    }

    @Override
    public final Coordinate getCoordinate() {
        return null;
    }

    @Override
    public final Coordinate[] getCoordinates() {
        return COORDINATES;
    }

    @Override
    public final double getLength() {
        return ZERO;
    }

    @Override
    public final boolean isEmpty() {
        return true;
    }
}
