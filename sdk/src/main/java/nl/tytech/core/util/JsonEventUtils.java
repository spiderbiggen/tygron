/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MultivaluedMap;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.structure.DataLord;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.JsonMapper;
import nl.tytech.util.RestManager.Format;
import nl.tytech.util.RestUtils;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;
import nl.tytech.util.logger.TLogger;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;

/**
 * Utils to handle JSON events
 * @author Maxim Knepfle
 *
 */
public class JsonEventUtils {

    private static Map<String, Class<? extends EventTypeEnum>> events;

    public static Object exampleEvent(Format format, List<Class<?>> classes) {

        try {
            List<Object> exampleParams = new ArrayList<>();

            for (int i = 0; i < classes.size(); i++) {
                Class<?> classz = classes.get(i);

                if (classz.getSuperclass() != null && classz.getSuperclass().equals(Number.class)) {
                    Integer random = (int) (Math.random() * 100d);
                    exampleParams.add(random);

                } else if (classz.equals(Boolean.class)) {
                    Boolean random = Math.random() > 0.5d;
                    exampleParams.add(random);

                } else if (classz.isEnum()) {
                    Object object = classz.getEnumConstants()[0];
                    exampleParams.add(object);

                } else if (classz.equals(MultiPolygon.class)) {
                    MultiPolygon mp = JTSUtils.createSquare(0, 0, Math.random() * 100d, Math.random() * 100d);
                    exampleParams.add(mp);

                } else if (classz.equals(Point.class)) {
                    Point point = JTSUtils.createPoint(Math.random() * 100d, Math.random() * 100d);
                    exampleParams.add(point);

                } else if (classz.equals(TColor.class)) {
                    TColor color = new TColor(Math.random(), Math.random(), Math.random(), Math.random());
                    exampleParams.add(color);

                } else if (classz.equals(String.class)) {
                    exampleParams.add("String value text");

                } else {
                    exampleParams.add("TODO Add Example");
                }
            }

            // parse to format and return
            return RestUtils.writeObject(exampleParams, format);
        } catch (Exception e) {
            TLogger.exception(e);
            return StringUtils.EMPTY;
        }
    }

    public static EventTypeEnum getRSEvent(String eventClass, String eventName) {

        Class<? extends EventTypeEnum> classz = events.get(eventClass);
        if (classz == null) {
            return null;
        }
        for (EventTypeEnum eventType : classz.getEnumConstants()) {
            if (eventType.toString().equals(eventName)) {
                return eventType;
            }
        }
        return null;
    }

    public static void init() {

        events = new HashMap<>();
        // special event, only one in core that can be triggered.
        events.put(MapLink.class.getSimpleName(), MapLink.class);
        for (Class<? extends EventTypeEnum> eventClass : DataLord.getEventClasses()) {
            events.put(eventClass.getSimpleName(), eventClass);
        }
        JsonMapper.setDefaultTyping(new ItemTypeResolverBuilder());
    }

    public final static Object[] parseFormParams(Format format, EventTypeEnum event, MultivaluedMap<String, String> formParams) {

        try {
            Object[] params = new Object[formParams.size()];
            for (int i = 0; i < event.getClasses().size(); i++) {
                Class<?> type = event.getClasses().get(i);
                List<String> entry = formParams.get("" + i);

                String value = "";
                for (String part : entry) {
                    value += part;
                }
                value = value.trim();

                // contain no sub units
                if (!value.startsWith("{")) {
                    value = "\"" + value + "\"";
                }

                if (type.isEnum() && "\"\"".equals(value)) {
                    params[i] = null;
                } else {
                    params[i] = JsonMapper.getMapper(format).readValue(value, type);
                }
            }
            return params;
        } catch (Exception e) {
            TLogger.exception(e);
            return null;
        }
    }

    public static Object[] parseJsonParams(Format format, EventTypeEnum type, String json) {

        try {

            Object[] params = new Object[type.getClasses().size()];

            /**
             * Parse args form json array
             */
            if (params.length > 0) {
                JsonParser jp = RestUtils.FACTORY.createParser(json);
                // advance to opening START
                jp.nextToken();
                int i = 0;
                while (jp.nextToken() != JsonToken.END_ARRAY && i < type.getClasses().size()) {
                    Object value = JsonMapper.getMapper(format).readValue(jp, type.getClasses().get(i));
                    params[i] = value;
                    i++;
                }
            }
            return params;

        } catch (Exception e) {
            TLogger.exception(e);
            return null;
        }
    }
}
