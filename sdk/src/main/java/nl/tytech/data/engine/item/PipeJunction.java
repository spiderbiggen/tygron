/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.net.serializable.PolygonItem;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.item.UniqueNamedItem;
import nl.tytech.util.JTSUtils;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;

/**
 * Connecting point for two pipes.
 * @author Maxim Knepfle
 *
 */
public class PipeJunction extends UniqueNamedItem implements PolygonItem {

    public static final double MINIMUM_JUNCTION_DISTANCE = 0.01;
    /**
     *
     */
    private static final long serialVersionUID = 2739744881310956349L;

    @XMLValue
    private Point point = null;

    @XMLValue
    @ItemIDField("PIPE_LOADS")
    private Integer pipeLoadID = Item.NONE;

    /**
     * Maxim: Values are calculated on the fly (at start) and thus not stored in XML.
     */
    private ArrayList<Integer> connectedPipeIDs = new ArrayList<>();

    private transient MultiPolygon[] multiPolygon;

    public void addPipeLookup(Integer id) {
        if (!connectedPipeIDs.contains(id)) {
            connectedPipeIDs.add(id);
        }
    }

    public List<Integer> getConnectedPipeIDs() {
        return connectedPipeIDs;
    }

    public List<Pipe> getConnectedPipes() {
        return this.getItems(MapLink.PIPES, getConnectedPipeIDs());
    }

    public PipeLoad getPipeLoad() {
        return getItem(MapLink.PIPE_LOADS, pipeLoadID);
    }

    public Integer getPipeLoadID() {
        return pipeLoadID;
    }

    public Point getPoint() {
        return point;
    }

    @Override
    public MultiPolygon[] getQTMultiPolygons() {
        if (multiPolygon == null) {
            if (point != null) {
                double width = Pipe.DEFAULT_PIPE_SQUARE_SIZE;
                multiPolygon = new MultiPolygon[] { JTSUtils.createSquare(point.getX() - width / 2, point.getY() - width / 2, width, width) };
            } else {
                multiPolygon = new MultiPolygon[] { JTSUtils.EMPTY };
            }
        }
        return multiPolygon;
    }

    public boolean hasConnectedPipes() {
        return connectedPipeIDs.size() > 0;
    }

    public boolean hasLoad() {
        return !Item.NONE.equals(pipeLoadID);
    }

    public boolean isActive() {
        for (Pipe pipe : this.getConnectedPipes()) {
            if (pipe.isActive()) {
                return true;
            }
        }
        return false;
    }

    public boolean isConnectedTo(Integer otherJunctionID) {
        for (Pipe pipe : getConnectedPipes()) {
            if (pipe.getStartJunctionID().equals(otherJunctionID) || pipe.getEndJunctionID().equals(otherJunctionID)) {
                return true;
            }
        }
        return false;
    }

    public boolean isEndOfLine() {
        return this.connectedPipeIDs.size() == 1;
    }

    public boolean isHead() {
        PipeSetting setting = this.getItem(MapLink.PIPE_SETTINGS, PipeSetting.Type.HEAD_JUCTION);
        return this.getID().equals(setting.getIntegerValue());
    }

    public boolean removeConnectedPipe(Integer pipeID) {
        return connectedPipeIDs.remove(pipeID);
    }

    @Override
    public void reset() {
        super.reset();
        resetTransients();
    }

    public void resetPipeLookup() {
        this.connectedPipeIDs.clear();
    }

    private void resetTransients() {
        multiPolygon = null;
    }

    public void setPipeLoadID(Integer pipeLoadID) {
        this.pipeLoadID = pipeLoadID;
    }

    public void setPoint(Point point) {
        this.point = point;
        resetTransients();
    }
}
