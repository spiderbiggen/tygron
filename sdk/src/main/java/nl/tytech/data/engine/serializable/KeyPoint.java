/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.serializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.item.annotations.AssetDirectory;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.Item;
import nl.tytech.util.StringUtils;

/**
 * KeyPoint
 * <p>
 * A point in tthe Cinematic.
 * </p>
 * @author Maxim Knepfle
 */
public class KeyPoint implements Serializable {

    private static final long serialVersionUID = -96399057258718886L;

    @XMLValue
    private boolean pause = false;

    @XMLValue
    private String description = StringUtils.EMPTY;

    @XMLValue
    private Vector3d location = new Vector3d();

    @XMLValue
    private Vector3d direction = new Vector3d();

    @XMLValue
    private Vector3d up = new Vector3d(0d, 1d, 0d);

    @XMLValue
    @ItemIDField("EVENT_BUNDLES")
    private ArrayList<Integer> eventBundlesIDs = new ArrayList<>();

    @XMLValue
    @ItemIDField("SOUNDS")
    private Integer soundID = Item.NONE;

    @XMLValue
    private double time = 4;

    @XMLValue
    private int waitingTimeInSeconds = Item.NONE;

    @XMLValue
    private Integer id = Item.NONE;

    @XMLValue
    @AssetDirectory(value = "Gui/Images/Hotspots/", allowEmpty = true)
    private String imageName = StringUtils.EMPTY;

    @XMLValue
    private double detailScale = Item.NONE;

    public KeyPoint() {

    }

    public String getDescription() {

        return description;
    }

    public double getDetailScale() {
        return detailScale;
    }

    public final Vector3d getDirection() {

        return this.direction;
    }

    public List<Integer> getEventBundleIDs() {

        return eventBundlesIDs;
    }

    public Integer getID() {
        return id;
    }

    public String getImageName() {
        return imageName;
    }

    public final Vector3d getLocation() {

        return this.location;
    }

    public Integer getSoundID() {

        return soundID;
    }

    public double getTime() {
        return time;
    }

    public final Vector3d getUp() {

        return this.up;
    }

    public int getWaitingTimeInSeconds() {

        return waitingTimeInSeconds;
    }

    public final boolean isPause() {

        return this.pause;
    }

    public void setDescription(String newText) {

        this.description = newText;
    }

    public final void setDirection(Float[] n_direction) {

        direction.x = n_direction[Item.X];
        direction.y = n_direction[Item.Y];
        direction.z = n_direction[Item.Z];
    }

    public final void setDirection(Vector3d n_direction) {
        direction.x = n_direction.x;
        direction.y = n_direction.y;
        direction.z = n_direction.z;
    }

    public void setID(Integer id) {
        this.id = id;
    }

    public final void setLocation(Float[] n_location) {

        location.x = n_location[Item.X];
        location.y = n_location[Item.Y];
        location.z = n_location[Item.Z];
    }

    public final void setLocation(Vector3d n_location) {

        location.x = n_location.x;
        location.y = n_location.y;
        location.z = n_location.z;
    }

    public final void setPause(boolean pause) {

        this.pause = pause;
    }

    public void setSoundID(Integer id) {
        this.soundID = id;

    }

    public void setTime(double time) {

        this.time = time;

    }

    public final void setUp(Float[] n_up) {

        up.x = n_up[Item.X];
        up.y = n_up[Item.Y];
        up.z = n_up[Item.Z];
    }

    public final void setUp(Vector3d n_up) {

        up.x = n_up.x;
        up.y = n_up.y;
        up.z = n_up.z;
    }

    @Override
    public String toString() {

        return "[" + (int) (getLocation().x) + ", " + (int) (getLocation().y) + ", " + (int) (getLocation().z) + "]";
    }

}
