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
import java.util.Arrays;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class GeometrySerializer<G extends Geometry> extends JsonSerializer<G> {

    private Class<G> type;

    public GeometrySerializer(Class<G> type) {
        this.type = type;
    }

    @Override
    public Class<G> handledType() {
        return type;
    }

    @Override
    public void serialize(Geometry value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        writeGeometry(jgen, value);
    }

    @Override
    public void serializeWithType(G geom, JsonGenerator jgen, SerializerProvider provider, TypeSerializer paramTypeSerializer)
            throws IOException, JsonProcessingException {

        paramTypeSerializer.writeTypePrefixForScalar(this, jgen, handledType());
        serialize(geom, jgen, provider);
        paramTypeSerializer.writeTypeSuffixForScalar(this, jgen);
    }

    public void writeGeometry(JsonGenerator jgen, Geometry value) throws IOException {
        if (value instanceof Polygon) {
            writePolygon(jgen, (Polygon) value);

        } else if (value instanceof Point) {
            writePoint(jgen, (Point) value);

        } else if (value instanceof MultiPoint) {
            writeMultiPoint(jgen, (MultiPoint) value);

        } else if (value instanceof MultiPolygon) {
            writeMultiPolygon(jgen, (MultiPolygon) value);

        } else if (value instanceof LineString) {
            writeLineString(jgen, (LineString) value);

        } else if (value instanceof MultiLineString) {
            writeMultiLineString(jgen, (MultiLineString) value);

        } else if (value instanceof GeometryCollection) {
            writeGeometryCollection(jgen, (GeometryCollection) value);

        } else {
            throw new JsonMappingException("Geometry type "
                    + value.getClass().getName()
                    + " cannot be serialized as GeoJSON."
                    + "Supported types are: "
                    + Arrays.asList(Point.class.getName(), LineString.class.getName(), Polygon.class.getName(), MultiPoint.class.getName(),
                            MultiLineString.class.getName(), MultiPolygon.class.getName(), GeometryCollection.class.getName()));
        }
    }

    private void writeGeometryCollection(JsonGenerator jgen, GeometryCollection value) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField(TYPE, GEOMETRY_COLLECTION);
        jgen.writeArrayFieldStart(GEOMETRIES);

        for (int i = 0; i != value.getNumGeometries(); ++i) {
            writeGeometry(jgen, value.getGeometryN(i));
        }

        jgen.writeEndArray();
        jgen.writeEndObject();
    }

    private void writeLineString(JsonGenerator jgen, LineString lineString) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField(TYPE, LINE_STRING);
        jgen.writeFieldName(COORDINATES);
        writeLineStringCoords(jgen, lineString);
        jgen.writeEndObject();
    }

    private void writeLineStringCoords(JsonGenerator jgen, LineString ring) throws IOException {
        jgen.writeStartArray();
        for (int i = 0; i != ring.getNumPoints(); ++i) {
            Point p = ring.getPointN(i);
            writePointCoords(jgen, p);
        }
        jgen.writeEndArray();
    }

    private void writeMultiLineString(JsonGenerator jgen, MultiLineString value) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField(TYPE, MULTI_LINE_STRING);
        jgen.writeArrayFieldStart(COORDINATES);

        for (int i = 0; i != value.getNumGeometries(); ++i) {
            writeLineStringCoords(jgen, (LineString) value.getGeometryN(i));
        }

        jgen.writeEndArray();
        jgen.writeEndObject();
    }

    private void writeMultiPoint(JsonGenerator jgen, MultiPoint value) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField(TYPE, MULTI_POINT);
        jgen.writeArrayFieldStart(COORDINATES);

        for (int i = 0; i != value.getNumGeometries(); ++i) {
            writePointCoords(jgen, (Point) value.getGeometryN(i));
        }

        jgen.writeEndArray();
        jgen.writeEndObject();
    }

    private void writeMultiPolygon(JsonGenerator jgen, MultiPolygon value) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField(TYPE, MULTI_POLYGON);
        jgen.writeArrayFieldStart(COORDINATES);

        for (int i = 0; i != value.getNumGeometries(); ++i) {
            writePolygonCoordinates(jgen, (Polygon) value.getGeometryN(i));
        }

        jgen.writeEndArray();
        jgen.writeEndObject();
    }

    private void writePoint(JsonGenerator jgen, Point p) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField(TYPE, POINT);
        jgen.writeFieldName(COORDINATES);
        writePointCoords(jgen, p);
        jgen.writeEndObject();
    }

    private void writePointCoords(JsonGenerator jgen, Point p) throws IOException {
        jgen.writeStartArray();
        jgen.writeNumber(p.getX());
        jgen.writeNumber(p.getY());
        jgen.writeEndArray();
    }

    private void writePolygon(JsonGenerator jgen, Polygon value) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField(TYPE, POLYGON);
        jgen.writeFieldName(COORDINATES);
        writePolygonCoordinates(jgen, value);

        jgen.writeEndObject();
    }

    private void writePolygonCoordinates(JsonGenerator jgen, Polygon value) throws IOException {
        jgen.writeStartArray();
        writeLineStringCoords(jgen, value.getExteriorRing());

        for (int i = 0; i < value.getNumInteriorRing(); ++i) {
            writeLineStringCoords(jgen, value.getInteriorRingN(i));
        }
        jgen.writeEndArray();
    }

}
