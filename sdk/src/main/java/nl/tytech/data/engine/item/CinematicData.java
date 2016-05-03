/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import nl.tytech.core.event.Event.SessionEventTypeEnum;
import nl.tytech.core.item.annotations.DoNotSaveToInit;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.CodedEvent;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.serializable.KeyPoint;
import nl.tytech.util.StringUtils;
import nl.tytech.util.logger.TLogger;

/**
 * @author Alexander Hofstede
 */
public class CinematicData extends Item {

    public enum EndType {
        SIMPLE,

        LOOP,

        WAIT_AT_END;
    }

    public final static int MAX_SPEED_M_SEC = 80;
    public final static double MAX_ROTATION_SPEED = 0.1f;

    /** Generated serialVersionUID */
    private static final long serialVersionUID = -8198427741756526965L;

    @XMLValue
    private boolean animateToStartPoint = false;

    @XMLValue
    @ListOfClass(KeyPoint.class)
    private ArrayList<KeyPoint> keyPoints = new ArrayList<KeyPoint>();

    @XMLValue
    private String name = "No Name";

    @XMLValue
    private EndType endType = EndType.SIMPLE;

    @XMLValue
    private Integer stakeholderID = Item.NONE;

    @DoNotSaveToInit
    @XMLValue
    private boolean animateToStart = false;

    @DoNotSaveToInit
    @XMLValue
    private Integer keyIndex = 0;

    @XMLValue
    @DoNotSaveToInit
    private boolean active = false;

    @XMLValue
    private boolean finishAtStakeholderStartLocation = false;

    private transient boolean localClientOnly = false;

    public CinematicData() {

    }

    public void add(int index, KeyPoint keyPoint) {
        int maxID = Item.NONE;
        for (KeyPoint other : keyPoints) {
            maxID = Math.max(maxID, other.getID());
        }

        maxID++;
        keyPoint.setID(maxID);

        if (index < 0 || index >= keyPoints.size()) {
            keyPoints.add(keyPoint);
        } else {
            keyPoints.add(index, keyPoint);
        }
    }

    public void add(KeyPoint keyPoint) {
        add(keyPoints.size(), keyPoint);
    }

    public KeyPoint get(int index) {
        return keyPoints.get(index);
    }

    @Override
    public String getDescription() {
        return "Cinematic " + name + " (" + getID() + ") " + keyPoints;
    }

    public EndType getEndType() {
        return endType;
    }

    public boolean getFinishAtStakeholderStartLocation() {
        return finishAtStakeholderStartLocation;
    }

    public int getIndexOf(Integer keyPointID) {
        for (int i = 0; i < keyPoints.size(); ++i) {
            KeyPoint other = keyPoints.get(i);
            if (other.getID().equals(keyPointID)) {
                return i;
            }
        }
        return -1;
    }

    public KeyPoint getKeyPoint(Integer keyPointID) {
        for (KeyPoint keyPoint : keyPoints) {
            if (keyPoint.getID().equals(keyPointID)) {
                return keyPoint;
            }
        }
        return null;

    }

    private Iterable<Integer> getKeyPointBundleIDs(int pointIndex) {
        KeyPoint point = keyPoints.get(pointIndex);
        if (point == null) {
            TLogger.severe(pointIndex + " is an invalid index for flightpoints in flighpath: " + this.toString());
            return null;
        }
        return point.getEventBundleIDs();
    }

    private List<EventBundle> getKeyPointBundles(int pointIndex) {

        return this.getItems(MapLink.EVENT_BUNDLES, getKeyPointBundleIDs(pointIndex));
    }

    public final List<KeyPoint> getKeyPoints() {
        return keyPoints;
    }

    public Integer getKeyPointSoundID(int pointIndex) {
        KeyPoint point = keyPoints.get(pointIndex);
        return point.getSoundID();
    }

    public String getName() {
        return name;
    }

    public Integer getPointIndex() {
        return keyIndex;
    }

    public Integer getStakeholderID() {
        return stakeholderID;
    }

    public double getTotalPlayingTime() {

        double totalTime = 0;
        for (KeyPoint keyPoint : this.getKeyPoints()) {
            totalTime += keyPoint.getTime();
        }
        return totalTime;
    }

    public boolean hasServerSessionEvents() {

        Set<Integer> eventBundleIDs = new TreeSet<>();
        for (KeyPoint keyPoint : getKeyPoints()) {
            eventBundleIDs.addAll(keyPoint.getEventBundleIDs());
        }

        List<EventBundle> eventBundles = getItems(MapLink.EVENT_BUNDLES, eventBundleIDs);
        for (EventBundle eventBundle : eventBundles) {
            for (CodedEvent event : eventBundle.getServerEvents()) {
                if (event.getType() instanceof SessionEventTypeEnum) {
                    return true;
                }
            }
        }
        return false;
    }

    public int indexOf(KeyPoint keyPoint) {
        return keyPoints.indexOf(keyPoint);
    }

    public boolean isActive() {
        return active;
    }

    public boolean isAnimateToStartPoint() {
        return animateToStartPoint;
    }

    public boolean isContinuous() {
        return endType == EndType.LOOP;
    }

    public boolean isEmpty() {
        return keyPoints.isEmpty();
    }

    /**
     * When true play cinematic localy, no server events or server reporting
     * @return
     */
    public boolean isLocalClientOnly() {
        return localClientOnly;
    }

    public KeyPoint removeKeyPoint(Integer keyPointID) {
        Iterator<KeyPoint> iterator = keyPoints.iterator();
        while (iterator.hasNext()) {
            KeyPoint keyPoint = iterator.next();
            if (keyPoint.getID().equals(keyPointID)) {
                iterator.remove();
                return keyPoint;
            }
        }
        return null;
    }

    /**
     * Reset cinamatic's stakeholder, point, etc.
     */

    public void resetCinematic() {
        this.keyIndex = 0;
        this.active = false;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setAnimateToStartPoint(boolean animate) {
        animateToStartPoint = animate;
    }

    public void setEndType(EndType endType) {
        this.endType = endType;
    }

    public void setFinishAtStakeholderStartLocation(boolean finishAtStart) {
        this.finishAtStakeholderStartLocation = finishAtStart;
    }

    public void setLocalClientOnly(boolean localClientOnly) {
        this.localClientOnly = localClientOnly;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public void setPointIndex(Integer pointIndex) {
        this.keyIndex = pointIndex;
    }

    public void setStakeholderID(Integer stakeholderID) {
        this.stakeholderID = stakeholderID;
    }

    public int size() {
        return keyPoints.size();
    }

    @Override
    public String toString() {
        return name + " (" + getID() + ")";
    }

    @Override
    public String validated(boolean startNewGame) {

        String result = StringUtils.EMPTY;

        // TODO: (Frank) If keypoints do not have a set id, set them once
        if (!this.getKeyPoints().isEmpty() && Item.NONE.equals(this.getKeyPoints().get(0).getID())) {
            int index = 0;
            for (KeyPoint keyPoint : this.getKeyPoints()) {
                keyPoint.setID(index);
                index++;
            }
        }

        for (KeyPoint point : this.getKeyPoints()) {
            point.setDescription(StringUtils.removeHTMLTags(point.getDescription()));
        }

        for (int i = 0; i < keyPoints.size(); i++) {
            for (Integer bundleID : this.getKeyPointBundleIDs(i)) {
                result += this.validIntegerLink(null, MapLink.EVENT_BUNDLES, bundleID);
            }
            // TODO: (Frank) Move this check to EventBundleControl?
            List<EventBundle> bundles = this.getKeyPointBundles(i);
            for (EventBundle bundle : bundles) {
                result += bundle.validated(startNewGame);
            }
        }

        if (startNewGame && active) {
            result += "\nCinematic data should not be active at the start. This should be triggerd through the "
                    + Level.class.getSimpleName() + ". Invalid data in " + CinematicData.class.getSimpleName() + " [" + getID() + "]";
        }
        return result;
    }
}
