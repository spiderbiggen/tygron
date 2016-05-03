/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.Message;
import nl.tytech.data.engine.other.Question;
import nl.tytech.util.JTSUtils;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;

/**
 *
 * @author Maxim Knepfle
 *
 *
 *
 */
public class FlyToMessage extends Message implements Question {

    private static final long serialVersionUID = -2627016540046015845L;

    @XMLValue
    private Point point = null;

    @XMLValue
    private MultiPolygon polygons = JTSUtils.EMPTY;

    @XMLValue
    private String map = "MAQUETTE";

    public MultiPolygon getMuliPolygon() {
        return this.polygons;
    }

    public Point getPoint() {
        return this.point;
    }

    /**
     * @return the map
     */
    public final <E extends Enum<E>> E getWorldMap(Class<E> type) {

        if (map == null || type.getEnumConstants() == null) {
            return null;
        }
        for (E constant : type.getEnumConstants()) {
            if (constant.toString().equals(map)) {
                return constant;
            }
        }
        return null;
    }

    public void initFlyTo(final Stakeholder sender, final Stakeholder receiver, final Long triggerDate, final String subject,
            final String message, final Point coordinate, final Enum<?> mapType) {

        init(sender, receiver, triggerDate, subject, message);
        this.point = coordinate;
        this.map = mapType.toString();
        this.type = Message.Type.STANDARD;
    }

    public void initPopup(final Stakeholder sender, final Stakeholder receiver, final Long triggerDate, final String subject,
            final String message, final Point coordinate, final Enum<?> mapType) {

        init(sender, receiver, triggerDate, subject, message);
        this.point = coordinate;
        this.map = mapType.toString();
        this.type = Message.Type.POPUP;
    }

    public void setMapType(final Enum<?> mapType) {
        this.map = mapType.toString();
    }

    @Override
    public void setMultiPolygon(MultiPolygon polygons) {
        this.polygons = polygons;
    }

    @Override
    public void setText(String text) {
        contents = text;

    }

}
