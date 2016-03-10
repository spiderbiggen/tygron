package com.bedatadriven.jackson.datatype.jts;

import static com.bedatadriven.jackson.datatype.jts.GeoJson.COORDINATES;
import static com.bedatadriven.jackson.datatype.jts.GeoJson.GEOMETRIES;
import static com.bedatadriven.jackson.datatype.jts.GeoJson.GEOMETRY_COLLECTION;
import static com.bedatadriven.jackson.datatype.jts.GeoJson.LINE_STRING;
import static com.bedatadriven.jackson.datatype.jts.GeoJson.MULTI_LINE_STRING;
import static com.bedatadriven.jackson.datatype.jts.GeoJson.MULTI_POINT;
import static com.bedatadriven.jackson.datatype.jts.GeoJson.MULTI_POLYGON;
import static com.bedatadriven.jackson.datatype.jts.GeoJson.POINT;
import static com.bedatadriven.jackson.datatype.jts.GeoJson.POLYGON;
import static com.bedatadriven.jackson.datatype.jts.GeoJson.TYPE;
import java.io.IOException;
import nl.tytech.util.JTSUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class GeometryDeserializer<T extends Geometry> extends JsonDeserializer<T> {

    /**
     * Use simple empty own mapper to prevent tying issues from default mapper.
     */
    private final static ObjectMapper MAPPER = new ObjectMapper();

    private GeometryFactory gf = JTSUtils.sourceFactory;

    private Class<T> type;

    public GeometryDeserializer(Class<T> type) {
        this.type = type;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode root = MAPPER.readTree(jp);
        return (T) parseGeometry(root);
    }

    @Override
    public Class<T> handledType() {
        return type;
    }

    private Coordinate parseCoordinate(JsonNode array) {
        assert array.isArray() && array.size() == 2 : "expecting coordinate array with single point [ x, y ]";
        return new Coordinate(array.get(0).asDouble(), array.get(1).asDouble());
    }

    private Coordinate[] parseCoordinates(JsonNode array) {
        Coordinate[] points = new Coordinate[array.size()];
        for (int i = 0; i != array.size(); ++i) {
            points[i] = parseCoordinate(array.get(i));
        }
        return points;
    }

    private Geometry[] parseGeometries(JsonNode arrayOfGeoms) throws JsonMappingException {
        Geometry[] items = new Geometry[arrayOfGeoms.size()];
        for (int i = 0; i != arrayOfGeoms.size(); ++i) {
            items[i] = parseGeometry(arrayOfGeoms.get(i));
        }
        return items;
    }

    private Geometry parseGeometry(JsonNode root) throws JsonMappingException {
        String typeName = root.get(TYPE).asText();
        if (POINT.equals(typeName)) {
            return parsePoint(root);

        } else if (MULTI_POINT.equals(typeName)) {
            return parseMultiPoint(root);

        } else if (LINE_STRING.equals(typeName)) {
            return parseLineString(root);

        } else if (MULTI_LINE_STRING.equals(typeName)) {
            return parseMultiLineStrings(root);

        } else if (POLYGON.equals(typeName)) {
            return parsePolygon(root);

        } else if (MULTI_POLYGON.equals(typeName)) {
            return parseMultiPolygon(root);

        } else if (GEOMETRY_COLLECTION.equals(typeName)) {
            return parseGeometryCollection(root);

        } else {
            throw new JsonMappingException("Invalid geometry type: " + typeName);
        }
    }

    private GeometryCollection parseGeometryCollection(JsonNode root) throws JsonMappingException {
        return gf.createGeometryCollection(parseGeometries(root.get(GEOMETRIES)));
    }

    private LinearRing[] parseInteriorRings(JsonNode arrayOfRings) {
        LinearRing[] rings = new LinearRing[arrayOfRings.size() - 1];
        for (int i = 1; i < arrayOfRings.size(); ++i) {
            rings[i - 1] = parseLinearRing(arrayOfRings.get(i));
        }
        return rings;
    }

    private LinearRing parseLinearRing(JsonNode coordinates) {
        assert coordinates.isArray() : "expected coordinates array";
        return gf.createLinearRing(parseCoordinates(coordinates));
    }

    private LineString parseLineString(JsonNode root) {
        return gf.createLineString(parseCoordinates(root.get(COORDINATES)));
    }

    private LineString[] parseLineStrings(JsonNode array) {
        LineString[] strings = new LineString[array.size()];
        for (int i = 0; i != array.size(); ++i) {
            strings[i] = gf.createLineString(parseCoordinates(array.get(i)));
        }
        return strings;
    }

    private MultiLineString parseMultiLineStrings(JsonNode root) {
        return gf.createMultiLineString(parseLineStrings(root.get(COORDINATES)));
    }

    private MultiPoint parseMultiPoint(JsonNode root) {
        return gf.createMultiPoint(parseCoordinates(root.get(COORDINATES)));
    }

    private MultiPolygon parseMultiPolygon(JsonNode root) {
        JsonNode arrayOfPolygons = root.get(COORDINATES);
        return gf.createMultiPolygon(parsePolygons(arrayOfPolygons));
    }

    private Point parsePoint(JsonNode root) {
        return gf.createPoint(parseCoordinate(root.get(COORDINATES)));
    }

    private Polygon parsePolygon(JsonNode root) {
        JsonNode arrayOfRings = root.get(COORDINATES);
        return parsePolygonCoordinates(arrayOfRings);
    }

    private Polygon parsePolygonCoordinates(JsonNode arrayOfRings) {
        return gf.createPolygon(parseLinearRing(arrayOfRings.get(0)), parseInteriorRings(arrayOfRings));
    }

    private Polygon[] parsePolygons(JsonNode arrayOfPolygons) {
        Polygon[] polygons = new Polygon[arrayOfPolygons.size()];
        for (int i = 0; i != arrayOfPolygons.size(); ++i) {
            polygons[i] = parsePolygonCoordinates(arrayOfPolygons.get(i));
        }
        return polygons;
    }
}
