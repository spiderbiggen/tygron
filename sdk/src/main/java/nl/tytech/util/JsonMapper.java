/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.util;

import nl.tytech.util.RestManager.Format;
import com.bedatadriven.jackson.datatype.jts.GeometryDeserializer;
import com.bedatadriven.jackson.datatype.jts.GeometrySerializer;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTypeResolverBuilder;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Maps object to JSON
 * @author Maxim Knepfle
 *
 */

public class JsonMapper {

    /**
     * Normal mapper using Getters and Setters
     */
    private static final ObjectMapper[] mappers = new ObjectMapper[Format.values().length];

    static {

        for (Format format : Format.values()) {

            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS);
            mapper.enable(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN);

            mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
            mapper.setVisibility(PropertyAccessor.GETTER, Visibility.NONE);
            mapper.setVisibility(PropertyAccessor.IS_GETTER, Visibility.NONE);
            mapper.setVisibility(PropertyAccessor.SETTER, Visibility.NONE);

            if (format.isPrettified()) {
                mapper.enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
                mapper.enable(SerializationFeature.INDENT_OUTPUT);
            }

            SimpleModule polygonModule = new SimpleModule();

            polygonModule.addSerializer(Geometry.class, new GeometrySerializer<Geometry>(Geometry.class));
            polygonModule.addDeserializer(Geometry.class, new GeometryDeserializer<Geometry>(Geometry.class));

            polygonModule.addSerializer(Point.class, new GeometrySerializer<Point>(Point.class));
            polygonModule.addDeserializer(Point.class, new GeometryDeserializer<Point>(Point.class));

            polygonModule.addSerializer(Polygon.class, new GeometrySerializer<Polygon>(Polygon.class));
            polygonModule.addDeserializer(Polygon.class, new GeometryDeserializer<Polygon>(Polygon.class));

            polygonModule.addSerializer(MultiPolygon.class, new GeometrySerializer<MultiPolygon>(MultiPolygon.class));
            polygonModule.addDeserializer(MultiPolygon.class, new GeometryDeserializer<MultiPolygon>(MultiPolygon.class));

            polygonModule.addSerializer(MultiLineString.class, new GeometrySerializer<MultiLineString>(MultiLineString.class));
            polygonModule.addDeserializer(MultiLineString.class, new GeometryDeserializer<MultiLineString>(MultiLineString.class));

            polygonModule.addSerializer(GeometryCollection.class, new GeometrySerializer<GeometryCollection>(GeometryCollection.class));
            polygonModule.addDeserializer(GeometryCollection.class, new GeometryDeserializer<GeometryCollection>(GeometryCollection.class));

            mapper.registerModule(polygonModule);

            mappers[format.ordinal()] = mapper;
        }

    }

    public static ObjectMapper getMapper(Format format) {
        return mappers[format.ordinal()];
    }

    public static void setDefaultTyping(DefaultTypeResolverBuilder resolver) {

        for (Format format : Format.values()) {
            if (format.isTyped()) {
                mappers[format.ordinal()].setDefaultTyping(resolver);
            }
        }
    }
}
