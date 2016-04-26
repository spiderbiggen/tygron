/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import javax.vecmath.Point3d;
import nl.tytech.util.jts.EmptyMultiPolygon;
import nl.tytech.util.logger.TLogger;
import straightskeleton.Corner;
import straightskeleton.Edge;
import straightskeleton.Machine;
import straightskeleton.Output.Face;
import straightskeleton.Output.SharedEdge;
import straightskeleton.Skeleton;
import utils.Loop;
import utils.LoopL;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.geom.TopologyException;
import com.vividsolutions.jts.geom.prep.PreparedGeometry;
import com.vividsolutions.jts.geom.prep.PreparedGeometryFactory;
import com.vividsolutions.jts.geom.util.PolygonExtracter;
import com.vividsolutions.jts.operation.buffer.BufferOp;
import com.vividsolutions.jts.operation.buffer.BufferParameters;
import com.vividsolutions.jts.operation.linemerge.LineMerger;
import com.vividsolutions.jts.operation.overlay.OverlayOp;
import com.vividsolutions.jts.operation.overlay.snap.SnapIfNeededOverlayOp;
import com.vividsolutions.jts.precision.GeometryPrecisionReducer;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;
import com.vividsolutions.jts.triangulate.ConformingDelaunayTriangulationBuilder;
import com.vividsolutions.jts.triangulate.ConstraintEnforcementException;

/**
 * Helper methods for JTS
 * @author Maxim Knepfle
 *
 */
public class JTSUtils {

    /**
     * Multi polygon size sorter
     */
    public final static Comparator<MultiPolygon> MP_SIZE_ORDER = (o1, o2) -> {
        if (o1.getArea() < o2.getArea()) {
            return -1;
        } else {
            return 1;
        }
    };

    private final static BufferParameters flatParams = new BufferParameters();

    static {
        flatParams.setEndCapStyle(BufferParameters.CAP_FLAT);
        flatParams.setJoinStyle(BufferParameters.JOIN_MITRE);
        flatParams.setQuadrantSegments(1);
    }

    private final static BufferParameters roundParams = new BufferParameters();

    static {
        roundParams.setEndCapStyle(BufferParameters.CAP_ROUND);
        roundParams.setJoinStyle(BufferParameters.JOIN_MITRE);
        roundParams.setQuadrantSegments(1);
    }

    public final static GeometryFactory sourceFactory = new GeometryFactory(new PrecisionModel(1000d));

    public final static GeometryFactory overlayOperationFactory = new GeometryFactory(new PrecisionModel(1000d * 1000d));

    public final static double TOLERANCE = 0.1d;

    public final static double INTERSECTION_BORDER_MARGIN = 0.001d;

    public final static MultiPolygon EMPTY = new EmptyMultiPolygon(sourceFactory);

    public static Geometry bufferSimple(Geometry geometry, double distance) {

        if (geometry instanceof Point) {
            return BufferOp.bufferOp(geometry, distance, roundParams);
        } else {
            return BufferOp.bufferOp(geometry, distance, flatParams);
        }
    }

    private static Geometry bufferZero(Geometry geometry) {
        return bufferSimple(geometry, 0);
    }

    /**
     * When true geom is not NULL and contains area
     * @param geometry
     * @return
     */
    public static boolean containsData(Geometry geometry) {
        return !isEmpty(geometry) && geometry.getArea() > 0;
    }

    public static boolean covers(Geometry geometry1, Geometry geometry2) {
        PreparedGeometry preparedGeometry1 = PreparedGeometryFactory.prepare(geometry1);
        return covers(preparedGeometry1, geometry2);
    }

    public static boolean covers(PreparedGeometry preparedGeometry1, Geometry geometry2) {

        try {
            // cheap method to test covers first
            return preparedGeometry1.covers(geometry2);

        } catch (TopologyException e) {
            TLogger.warning("Covers fail on: " + preparedGeometry1.getGeometry().toString() + " and " + geometry2.toString());
        }

        /**
         * Fall back scenario adding border margin
         */

        // point cannot to negative margin test, thus must be contained in geom 1
        if (geometry2 instanceof Point) {
            // both are points, skip overlap test (unlikely)
            if (preparedGeometry1.getGeometry() instanceof Point) {
                return preparedGeometry1.contains(geometry2);
            }
            // add margin to geom 1
            Geometry geometry1 = bufferSimple(preparedGeometry1.getGeometry(), -INTERSECTION_BORDER_MARGIN);
            preparedGeometry1 = PreparedGeometryFactory.prepare(geometry1);
            return preparedGeometry1.covers(geometry2);
        }

        // try expensive more robust method
        return preparedGeometry1.covers(bufferSimple(geometry2, -INTERSECTION_BORDER_MARGIN));
    }

    public static GeometryCollection createCollection(Collection<? extends Geometry> geoms) {

        Geometry[] array = new Geometry[geoms.size()];
        return new GeometryCollection(geoms.toArray(array), sourceFactory);
    }

    public static List<LineString> createGrid(double width, double height, double step) {

        List<LineString> lines = new ArrayList<>();
        for (double x = 0; x < width; x += step) {
            Coordinate[] coordinates = new Coordinate[2];
            coordinates[0] = new Coordinate(x, 0, 0);
            coordinates[1] = new Coordinate(x, height, 0);
            lines.add(sourceFactory.createLineString(coordinates));
        }

        for (double y = 0; y < height; y += step) {
            Coordinate[] coordinates = new Coordinate[2];
            coordinates[0] = new Coordinate(0, y, 0);
            coordinates[1] = new Coordinate(width, y, 0);
            lines.add(sourceFactory.createLineString(coordinates));
        }
        return lines;
    }

    public static Geometry createGridCell(Point point, double cellSizeM) {

        if (point == null) {
            return null;
        }
        return createSquarePolygon(point.getX() - cellSizeM / 2d, point.getY() + cellSizeM / 2d, cellSizeM, cellSizeM);
    }

    public static LineString createLine(Coordinate... coordinates) {

        for (Coordinate c : coordinates) {
            c.x = sourceFactory.getPrecisionModel().makePrecise(c.x);
            c.y = sourceFactory.getPrecisionModel().makePrecise(c.y);
            c.z = sourceFactory.getPrecisionModel().makePrecise(c.z);
        }
        return sourceFactory.createLineString(coordinates);
    }

    public static LinearRing createLinearRing(Coordinate... coordinates) {

        for (Coordinate c : coordinates) {
            c.x = sourceFactory.getPrecisionModel().makePrecise(c.x);
            c.y = sourceFactory.getPrecisionModel().makePrecise(c.y);
            c.z = sourceFactory.getPrecisionModel().makePrecise(c.z);
        }
        return sourceFactory.createLinearRing(coordinates);
    }

    public static MultiPolygon createMP(Geometry... geometries) {

        if (geometries == null || geometries.length == 0) {
            return EMPTY;
        }
        return createMP(Arrays.asList(geometries));
    }

    /**
     * Creates a MultiPolygon of the polygons and discards all others (points, lines, etc)
     * @param geometry
     * @return
     */
    public static MultiPolygon createMP(List<? extends Geometry> geometries) {

        // no polys is empty
        if (geometries == null || geometries.isEmpty()) {
            return EMPTY;
        }
        // also skip where first poly is emtpy
        if (geometries.size() == 1 && geometries.get(0).isEmpty()) {
            return EMPTY;
        }

        List<Geometry> validPolyons = new ArrayList<>();
        for (Geometry geometry : geometries) {
            // try fixing it with buffer
            geometry = validate(geometry);
            // extract polygons to list validPolyons
            PolygonExtracter.getPolygons(geometry, validPolyons);
        }

        // parallel union still seems slower!
        // Geometry geom = ParallelCascadedPolygonUnion.union(validPolyons);

        // tricky bit, geom precision must be applied here to prevent Topology Exceptions
        Polygon[] pa = new Polygon[validPolyons.size()];
        GeometryCollection gc = new GeometryCollection(validPolyons.toArray(pa), JTSUtils.overlayOperationFactory);
        Geometry reduced = reduceToStandardPrecision(gc);
        Geometry result = bufferZero(reduced);

        if (!containsData(result)) {
            return JTSUtils.EMPTY;
        }

        // try fixing it with buffer (again)
        result = validate(result);

        if (result instanceof Polygon) {
            Polygon[] polygons = new Polygon[1];
            polygons[0] = (Polygon) result;
            return sourceFactory.createMultiPolygon(polygons);

        } else if (result instanceof MultiPolygon) {
            return (MultiPolygon) result;

        } else {
            TLogger.severe("Unknown geometry created: " + result.toString());
            return null;
        }
    }

    public static Point createPoint(Coordinate coordinate) {
        return sourceFactory.createPoint(coordinate);
    }

    public static Point createPoint(double x, double y) {
        return createPoint(new Coordinate(x, y));
    }

    public static Point createPoint(double x, double y, double z) {
        return createPoint(new Coordinate(x, y, z));
    }

    public static Geometry createPolygon(LinearRing outer, LinearRing[] inner) {

        Polygon polygon = sourceFactory.createPolygon(outer, inner);
        return JTSUtils.bufferZero(polygon);
    }

    public static Geometry createPolygon(List<Coordinate> coordinates) {

        if (coordinates.size() < 3) {
            return null;
        }

        Coordinate[] array;
        Coordinate first = coordinates.get(0);
        Coordinate last = coordinates.get(coordinates.size() - 1);

        if (first.equals(last)) {
            if (coordinates.size() < 4) {
                return null;
            }
            array = new Coordinate[coordinates.size()];
        } else {
            last = first;
            array = new Coordinate[coordinates.size() + 1];
        }

        for (int i = 0; i < coordinates.size(); ++i) {
            array[i] = coordinates.get(i);
        }
        array[array.length - 1] = last;

        LinearRing linear = createLinearRing(array);
        return createPolygon(linear, null);
    }

    public static MultiPolygon createSquare(double x, double y, double width, double height) {

        if (width == 0 || height == 0) {
            return EMPTY;
        }
        return createMP(createSquarePolygon(x, y, width, height));
    }

    public static MultiPolygon createSquare(Envelope envelope) {
        return createSquare(envelope.getMinX(), envelope.getMinY(), envelope.getWidth(), envelope.getHeight());
    }

    public static Geometry createSquarePolygon(double x, double y, double width, double height) {

        List<Coordinate> coordinates = new ArrayList<>();
        coordinates.add(new Coordinate(x, y));
        coordinates.add(new Coordinate(x + width, y));
        coordinates.add(new Coordinate(x + width, y + height));
        coordinates.add(new Coordinate(x, y + height));
        coordinates.add(new Coordinate(x, y));
        return createPolygon(coordinates);
    }

    public static MultiPolygon difference(Geometry base, Geometry remove) {
        return difference(base, remove, false);
    }

    public static MultiPolygon difference(Geometry base, Geometry remove, boolean erodeAndDilate) {

        if (!containsData(remove)) {
            return JTSUtils.createMP(base);
        }

        if (covers(remove, base)) {
            return EMPTY;
        }
        Geometry result = executeOverlayOperation(base, remove, OverlayOp.DIFFERENCE);

        // maybe also do an erode dilate
        if (erodeAndDilate) {
            double factor = INTERSECTION_BORDER_MARGIN * 10d;
            result = erodeAndDilate(result, factor);
        }
        return createMP(result);
    }

    public static boolean equals(MultiPolygon mp, MultiPolygon other) {
        if (mp.getNumGeometries() != other.getNumGeometries()) {
            return false;
        }
        Geometry geom, otherGeom;
        for (int i = 0; i < mp.getNumGeometries(); ++i) {
            geom = mp.getGeometryN(i);
            otherGeom = other.getGeometryN(i);
            if (geom instanceof Polygon && otherGeom instanceof Polygon) {

                if (!equals((Polygon) geom, (Polygon) otherGeom)) {
                    return false;
                }
            } else if (!geom.equals(otherGeom)) {
                return false;
            }

        }
        return true;
    }

    public static boolean equals(Polygon polygon, Polygon other) {
        if (!polygon.getExteriorRing().equals(other.getExteriorRing())) {
            return false;
        }

        if (polygon.getNumInteriorRing() != other.getNumInteriorRing()) {
            return false;
        }

        for (int i = 0; i < polygon.getNumInteriorRing(); ++i) {
            if (!polygon.getInteriorRingN(i).equals(other.getInteriorRingN(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Removes minor "frutsels"
     */
    public static Geometry erodeAndDilate(Geometry geom, double valueM) {
        return JTSUtils.bufferSimple(JTSUtils.bufferSimple(geom, -valueM), valueM);
    }

    private static Geometry executeOverlayOperation(Geometry mp1, Geometry mp2, int type) {

        try {
            /**
             * First try executing it the normal way on existing precision
             */
            return SnapIfNeededOverlayOp.overlayOp(mp1, mp2, type);

        } catch (TopologyException e) {

            try {
                /**
                 * Second attempt forced increase precision model
                 */
                Geometry exactMp1 = bufferZero(overlayOperationFactory.createGeometry(mp1));
                Geometry exactMp2 = bufferZero(overlayOperationFactory.createGeometry(mp2));
                Geometry result = SnapIfNeededOverlayOp.overlayOp(exactMp1, exactMp2, type);
                return bufferZero(reduceToStandardPrecision(result));

            } catch (TopologyException e2) {
                /**
                 * Third attempt forced increase precision model and make the objects a bit smaller.
                 */
                Geometry exactMp1 = bufferSimple(overlayOperationFactory.createGeometry(mp1), -0.001);
                Geometry exactMp2 = bufferSimple(overlayOperationFactory.createGeometry(mp2), -0.001);
                Geometry result = SnapIfNeededOverlayOp.overlayOp(exactMp1, exactMp2, type);
                return bufferZero(reduceToStandardPrecision(result));
            }
        }
    }

    public static Point getCenterPoint(Geometry geom) {

        if (isEmpty(geom)) {
            return null;
        }
        return geom.getCentroid();
    }

    public static Point getCenterPoint(List<Geometry> geoms) {

        if (geoms == null || geoms.size() == 0) {
            return null;
        }
        GeometryCollection gc = new GeometryCollection(geoms.toArray(new Geometry[geoms.size()]), JTSUtils.overlayOperationFactory);
        if (isEmpty(gc)) {
            return null;
        }
        return gc.getCentroid();
    }

    /**
     * Count number of coordinates in MultiPolygon (complexity)
     * @param mp
     * @return
     */
    public static int getCoordinateCount(MultiPolygon mp) {

        int vertices = 0;
        for (Polygon polygon : getPolygons(mp)) {
            vertices += polygon.getCoordinates().length;
        }
        return vertices;
    }

    public static Envelope getEnvelope(Geometry geometry) {

        Envelope envelope = new Envelope();
        envelope.expandToInclude(geometry.getEnvelopeInternal());
        return envelope;
    }

    public final static Polygon getLargestPolygon(final Geometry geom) {

        if (geom instanceof Polygon) {
            return (Polygon) geom;
        }

        List<Polygon> polygons = JTSUtils.getPolygons(geom);
        double topSize = 0;
        Polygon largest = null;

        for (Polygon poly : polygons) {
            double mySize = poly.getArea();
            if (mySize > topSize) {
                topSize = mySize;
                largest = poly;
            }
        }
        return largest;
    }

    @SuppressWarnings("unchecked")
    public static List<Polygon> getPolygons(Geometry geometry) {

        List<Polygon> polygons = new ArrayList<>();

        if (geometry instanceof Polygon) {
            if (geometry.getArea() > 0) {
                polygons.add((Polygon) geometry);
            }
        } else {
            List<Polygon> extractedPolygons = PolygonExtracter.getPolygons(geometry);
            for (Polygon extract : extractedPolygons) {
                if (extract.getArea() > 0) {
                    polygons.add(extract);
                }
            }
        }
        return polygons;
    }

    public static Skeleton getSkeleton(LinearRing line) {

        // setup skeleton
        LoopL<Edge> out = new LoopL<>();
        Loop<Edge> loop1 = new Loop<>();
        Machine directionMachine = new Machine();
        Corner[] corners = new Corner[line.getNumPoints()];

        // convert points to corners in reverse order
        int c = 0;
        for (int i = line.getNumPoints() - 1; i > 0; i--) {
            Point p1 = line.getPointN(i);
            corners[c] = new Corner(p1.getX(), p1.getY());
            c++;
        }
        // finish with first
        corners[c] = corners[0];

        // create edges from corners
        for (int i = 0; i < corners.length - 1; i++) {
            Corner c1 = corners[i];
            Corner c2 = corners[i + 1];
            Edge edge = new Edge(c1, c2);
            edge.machine = directionMachine;
            loop1.append(edge);
        }

        // do skeleton stuff
        out.add(loop1);
        Skeleton skeleton = new Skeleton(out, true);
        skeleton.skeleton();
        return skeleton;
    }

    public static List<Polygon> getSkeletonPolygons(Polygon originalPolygon, Skeleton skeleton) {

        List<Polygon> result = new ArrayList<>();
        for (Face face : skeleton.output.faces.values()) {
            LoopL<Point3d> loopl = face.getLoopL();

            for (Loop<Point3d> pl : loopl) {
                List<Coordinate> coordinates = new ArrayList<>();
                for (Point3d p : pl) {
                    coordinates.add(new Coordinate(p.x, p.y));
                }
                Geometry geom = createPolygon(coordinates);
                // Note: Sometimes the algorithm above freaks out, reduce roof polygons orginal Poly
                MultiPolygon intersection = JTSUtils.intersection(geom, originalPolygon);
                result.addAll(getPolygons(intersection));
            }
        }
        return result;
    }

    public static MultiLineString getSkeletonTopLines(Geometry geometry, double accuracy) {

        Geometry g = JTSUtils.simplify(geometry, accuracy);
        List<LineString> result = new ArrayList<>();

        for (Polygon polygon : JTSUtils.getPolygons(g)) {
            if (polygon.getNumInteriorRing() == 0) {
                result.addAll(getSkeletonTopLines(getSkeleton((LinearRing) polygon.getExteriorRing()), true));
            }
        }
        return new MultiLineString(result.toArray(new LineString[result.size()]), sourceFactory);
    }

    @SuppressWarnings("unchecked")
    public static Collection<LineString> getSkeletonTopLines(Skeleton skeleton, boolean merged) {

        List<Point3d> bottomPoints = new ArrayList<>();
        LineMerger merger = merged ? new LineMerger() : null;
        List<LineString> seperateLines = new ArrayList<>();

        for (Face face : skeleton.output.faces.values()) {

            // gather bottom points (on outer contour of polygon)
            for (SharedEdge edge : face.definingSE) {
                bottomPoints.add(edge.start);
                bottomPoints.add(edge.end);
            }

            for (Loop<SharedEdge> edgeLoop : face.edges) {
                for (SharedEdge edge : edgeLoop) {

                    // add only edges that to not intersect with outer polygon contour
                    if (!bottomPoints.contains(edge.start) && !bottomPoints.contains(edge.end)) {

                        Coordinate start = new Coordinate(edge.start.x, edge.start.y);
                        Coordinate end = new Coordinate(edge.end.x, edge.end.y);

                        LineString topLine;
                        if (start.x > end.x) {
                            topLine = createLine(start, end);
                        } else {
                            topLine = createLine(end, start);
                        }

                        if (!seperateLines.contains(topLine)) {
                            if (merged) {
                                merger.add(topLine);
                            }
                            seperateLines.add(topLine);
                        }
                    }
                }
            }
            // reset per face, faster
            bottomPoints.clear();
        }
        return merged ? merger.getMergedLineStrings() : seperateLines;
    }

    public static List<Polygon> getTriangles(Collection<? extends Geometry> roofParts, double minTriangleArea) {

        List<Polygon> triangles = new ArrayList<>();
        for (Geometry roofPart : roofParts) {
            for (Polygon roofPoly : JTSUtils.getPolygons(roofPart)) {
                JTSUtils.getTrianglesInner(triangles, roofPoly, minTriangleArea);
            }
        }
        return triangles;
    }

    public static List<Polygon> getTriangles(Polygon polygon, double minTriangleArea) {

        List<Polygon> triangles = new ArrayList<>();
        JTSUtils.getTrianglesInner(triangles, polygon, minTriangleArea);
        return triangles;
    }

    private static void getTrianglesInner(List<Polygon> triangles, Polygon polygon, double minTriangleArea) {

        if (!containsData(polygon)) {
            return;
        }
        // if triangle? (size is 3 + 1 start double)
        if (polygon.getNumInteriorRing() == 0 && polygon.getExteriorRing().getNumPoints() == 4 && polygon.getArea() > 0) {
            // reverse order for normal mapping (code below also reverses)
            if (polygon.getArea() > minTriangleArea) {
                triangles.add((Polygon) polygon.reverse());
            }
            return;
        }

        long start = System.currentTimeMillis();
        try {
            PreparedGeometry optimizedPolygon = PreparedGeometryFactory.prepare(bufferSimple(polygon, -TOLERANCE));
            ConformingDelaunayTriangulationBuilder triangulator = new ConformingDelaunayTriangulationBuilder();
            triangulator.setTolerance(TOLERANCE);
            triangulator.setConstraints(polygon);
            triangulator.setSites(polygon);

            /**
             * Filter out all triangles outside the original polygon.
             */
            GeometryCollection gc = (GeometryCollection) triangulator.getTriangles(sourceFactory);
            for (int i = 0; i < gc.getNumGeometries(); i++) {
                Geometry triangle = gc.getGeometryN(i);
                if (intersectsBorderIncluded(optimizedPolygon, triangle)) {
                    if (triangle.getArea() > minTriangleArea) {
                        triangles.add((Polygon) triangle);
                    }
                }
            }
        } catch (ConstraintEnforcementException e) {
            TLogger.warning("Triangulation too complex calculated: " + (System.currentTimeMillis() - start) + " ms on polygon: "
                    + polygon.toString());
        } catch (Exception e) {
            TLogger.exception(e);
        }
    }

    public static MultiPolygon intersection(Geometry mp1, Geometry mp2) {

        if (!containsData(mp1) || !containsData(mp2)) {
            return EMPTY;
        }
        Geometry result = executeOverlayOperation(mp1, mp2, OverlayOp.INTERSECTION);
        return createMP(result);
    }

    /**
     * Accurate intersection check, Border is NOT included (by a margin)
     */
    public static boolean intersectsBorderExcluded(Geometry geometry1, Geometry geometry2) {

        PreparedGeometry preparedGeometry1 = PreparedGeometryFactory.prepare(geometry1);
        return intersectsBorderExcluded(preparedGeometry1, geometry2);
    }

    /**
     * Accurate intersection check, Border is NOT included (by a margin)
     */
    public static boolean intersectsBorderExcluded(PreparedGeometry preparedGeometry1, Geometry geometry2) {

        try {
            // cheap method to test intersection first
            if (!preparedGeometry1.intersects(geometry2)) {
                return false; 
            }
            // cheap method to test if it only touches edges
            if (preparedGeometry1.touches(geometry2)) {
                return false;
            }

        } catch (TopologyException e) {
            // TLogger.warning("Intersect fail on: " + preparedGeometry1.getGeometry().toString() + " and " + geometry2.toString());
        }

        // point cannot to negative margin test, thus must be contained in geom 1
        if (geometry2 instanceof Point) {

            // both are points, skip overlap test
            if (preparedGeometry1.getGeometry() instanceof Point) {
                return preparedGeometry1.contains(geometry2);
            }
            // add margin to geom 1
            Geometry geometry1 = bufferSimple(preparedGeometry1.getGeometry(), -INTERSECTION_BORDER_MARGIN);
            preparedGeometry1 = PreparedGeometryFactory.prepare(geometry1);
            return preparedGeometry1.intersects(geometry2);
        }

        // try expensive more robust method
        return preparedGeometry1.intersects(bufferSimple(geometry2, -INTERSECTION_BORDER_MARGIN));
    }

    /**
     * Faster intersection check, Including Border, when it fails fall back to Border Excluded
     */
    public static boolean intersectsBorderIncluded(Geometry geometry1, Geometry geometry2) {

        PreparedGeometry preparedGeometry1 = PreparedGeometryFactory.prepare(geometry1);
        return intersectsBorderIncluded(preparedGeometry1, geometry2);
    }

    /**
     * Faster intersection check, Including Border, when it fails fall back to Border Excluded
     */
    public static boolean intersectsBorderIncluded(PreparedGeometry preparedGeometry1, Geometry geometry2) {

        try {
            // cheap method to test intersection
            return preparedGeometry1.intersects(geometry2);
        } catch (TopologyException e) {
            TLogger.warning("Rough intersect fail on: " + preparedGeometry1.getGeometry().toString() + " and " + geometry2.toString());
            // fall back to exact check:
            return intersectsBorderExcluded(preparedGeometry1, geometry2);
        }
    }

    private static boolean isEmpty(Geometry geometry) {
        return geometry == null || geometry.isEmpty();
    }

    public static boolean isExactWidthAndHeight(final MultiPolygon mp, final double width, final double height, double errorMargin) {

        if (Math.abs(mp.getArea() - width * height) > errorMargin) {
            return false;
        }
        Envelope envelope = JTSUtils.getEnvelope(mp);
        return Math.abs(envelope.getHeight() - height) < errorMargin && Math.abs(envelope.getWidth() - width) < errorMargin;
    }

    public static Geometry reduceToStandardPrecision(Geometry geometry) {

        GeometryPrecisionReducer reducer = new GeometryPrecisionReducer(sourceFactory.getPrecisionModel());
        reducer.setChangePrecisionModel(true);
        try {
            return reducer.reduce(geometry);
        } catch (Exception e) {
            // when it fails, validate and try once more...
            validate(geometry);
            return reducer.reduce(geometry);
        }
    }

    public static MultiPolygon removeHoles(MultiPolygon mp) {
        List<Polygon> holeRemoved = new ArrayList<>();
        for (Polygon polygon : JTSUtils.getPolygons(mp)) {
            if (polygon.getNumInteriorRing() > 0) {
                holeRemoved.add(sourceFactory.createPolygon(polygon.getExteriorRing().getCoordinateSequence()));
            } else {
                holeRemoved.add(polygon);
            }
        }
        return JTSUtils.createMP(holeRemoved);

    }

    public static MultiPolygon simplify(Geometry mp, double tolerance) {

        List<Polygon> polygons = JTSUtils.getPolygons(mp);
        List<Polygon> simples = new ArrayList<>();

        for (Polygon polygon : polygons) {

            Geometry g = DouglasPeuckerSimplifier.simplify(polygon, tolerance);

            if (g instanceof Polygon) {
                simples.add((Polygon) g);

            } else if (g instanceof MultiPolygon) {
                simples.addAll(JTSUtils.getPolygons(g));
            } else {
                TLogger.severe("Unknown geometry result in simplify: " + g.toString());
            }
        }
        return createMP(simples);
    }

    public static Envelope[] subdivideEnvelope(Envelope env) {

        // Divide into 4 quarters
        Envelope[] array = new Envelope[4];
        double width = env.getWidth() / 2d;
        double height = env.getHeight() / 2d;

        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 2; y++) {
                Envelope newEnv = new Envelope(//
                        env.getMinX() + (x * width),//
                        env.getMinX() + (x + 1) * width,//
                        env.getMinY() + (y * height),//
                        env.getMinY() + (y + 1) * height //
                );
                array[x + y * 2] = newEnv;
            }
        }
        return array;
    }

    public static MultiPolygon union(MultiPolygon... mps) {
        return createMP(Arrays.asList(mps));
    }

    public static Geometry validate(Geometry geometry) {

        try {
            if (geometry != null && !geometry.isValid()) {
                geometry = bufferZero(geometry);
                if (!geometry.isValid()) {
                    geometry = bufferZero(geometry);
                    if (!geometry.isValid()) {
                        TLogger.severe("Still Invalid Geometry after double buffer: " + geometry.toString());
                    }
                }
            }
        } catch (Exception e) {
            // validating can crash, that must then be invalid!
            geometry = bufferZero(geometry);
            if (!geometry.isValid()) {
                geometry = bufferZero(geometry);
                if (!geometry.isValid()) {
                    TLogger.severe("Still Invalid Geometry after exception: " + e + " and double buffer: " + geometry.toString());
                }
            }
        }
        return geometry;
    }
}
