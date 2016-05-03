/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.data.core.item.EnumOrderedItem;
import nl.tytech.data.engine.serializable.Vector3d;

/**
 * @author Jeroen Warmerdam
 */
public class ZoomLevel extends EnumOrderedItem<ZoomLevel.Type> {

    public enum Type {

        REGION(new Vector3d(0, 6000, -240), new Vector3d(0, 0, 0), new Vector3d(0, 0, -1), false, 20000f, 0.0f, 0.86f, false, 20),

        PLAN(new Vector3d(0, 1750, 1), new Vector3d(0, 0, 0), new Vector3d(0, 0, -1), false, 600f, 0, 0.86f, false, 10),

        LOCAL(new Vector3d(0, 240, 200), new Vector3d(0, -30, 0), new Vector3d(0, 1, 0), true, 540f, 250.0f, 0.86f, true, 10),

        STREET(new Vector3d(0, 240, 230), new Vector3d(0, -30, 0), new Vector3d(0, 1, 0), true, 130f, 150.0f, 0.7f, true, 10),

        MEDIUMSTREET(new Vector3d(0, 130, 110), new Vector3d(0, -30, 0), new Vector3d(0, 1, 0), true, 30f, 150.0f, 0.5f, true, 5),

        CLOSESTREET(new Vector3d(0, 55, 110), new Vector3d(0, -10, 0), new Vector3d(0, 1, 0), true, 18f, 150.0f, 0.45f, true, 1),

        WALK(new Vector3d(0, 2, 0), new Vector3d(0, 2, -5), new Vector3d(0, 1, 0), true, 0f, 1f, 0.45f, true, 1);

        public static final Type[] VALUES = Type.values();

        private Vector3d defaultRelativeLocation;
        private Vector3d defaultRelativeLookAt;
        private Vector3d defaultUp;
        private boolean boundToCityMap;
        private Double defaultCameraMovementMultiplier = 0d;

        private Double defaultInPlaneDeceleration = 0d;

        private boolean zoomToMouse;
        @Deprecated
        private double viewBoxHeight;
        private double nearplane;

        private Type(Vector3d relativeLocation, Vector3d relativeLookAt, Vector3d up, boolean defaultBounds, double viewBoxHeight,
                double cameraMovementMultiplier, double inPlaneDeceleration, boolean zoomToMouse, double nearplane) {
            this.defaultRelativeLocation = relativeLocation;
            this.defaultRelativeLookAt = relativeLookAt;
            this.defaultUp = up;
            this.defaultCameraMovementMultiplier = cameraMovementMultiplier;
            this.defaultInPlaneDeceleration = inPlaneDeceleration;

            this.boundToCityMap = defaultBounds;
            this.viewBoxHeight = viewBoxHeight;
            this.zoomToMouse = zoomToMouse;
            this.nearplane = nearplane;

        }

        public Double getDefaultCameraMovementMultiplier() {
            return this.defaultCameraMovementMultiplier;
        }

        public Double getDefaultInPlaneDeceleration() {
            return this.defaultInPlaneDeceleration;
        }

        public Vector3d getDefaultRelativeLocation() {
            return defaultRelativeLocation;
        }

        public Vector3d getDefaultRelativeLookAt() {
            return defaultRelativeLookAt;
        }

        public Vector3d getDefaultUp() {
            return defaultUp;
        }

        public double getNearPlane() {
            return nearplane;
        }

        @Deprecated
        public double getViewBoxHeight() {
            return viewBoxHeight;
        }

        public boolean isBoundToCityMap() {
            return boundToCityMap;
        }

        public boolean isSmallerThan(Type target) {
            return this.ordinal() > target.ordinal();
        }

        public boolean isZoomToMouse() {
            return zoomToMouse;
        }

        public void setBoundToCityMap(boolean boundToCityMap) {
            this.boundToCityMap = boundToCityMap;
        }
    }

    private final static Type DEFAULT_START_LEVEL = Type.STREET;

    /**
     *
     */
    private static final long serialVersionUID = -6333968983835455545L;

    @XMLValue
    private Boolean startZoomLevel;

    @XMLValue
    private Vector3d relativeLocation;

    @XMLValue
    private Vector3d relativeLookAt;

    @XMLValue
    private Boolean boundToCityMap;

    @XMLValue
    private Vector3d up;

    @XMLValue
    private Double cameraMovementMultiplier;

    @XMLValue
    private Double inPlaneDeceleration;

    @Deprecated
    @XMLValue
    private Double viewBoxHeight;

    @XMLValue
    private boolean active = true;

    @XMLValue
    private Boolean zoomToMouse;

    public Double getCameraMovementMultiplier() {
        if (cameraMovementMultiplier == null) {
            cameraMovementMultiplier = Type.VALUES[this.getID()].getDefaultCameraMovementMultiplier();
        }
        return cameraMovementMultiplier;
    }

    @Override
    public String getDescription() {
        return toString() + " Active: " + active;
    }

    @Override
    public Type[] getEnumValues() {
        return Type.VALUES;
    }

    public Double getInPlaneDeceleration() {
        if (inPlaneDeceleration == null) {
            inPlaneDeceleration = Type.VALUES[this.getID()].getDefaultInPlaneDeceleration();
        }
        return inPlaneDeceleration;
    }

    public ZoomLevel getNextHigher() {
        ItemMap<ZoomLevel> levels = this.getMap(MapLink.ZOOMLEVELS);

        for (int i = this.getID().intValue() - 1; i >= 0; i--) {
            ZoomLevel higher = levels.get(i);
            if (higher != null && higher.isActive()) {
                return higher;
            }
        }
        return null;
    }

    public ZoomLevel getNextLower() {
        ItemMap<ZoomLevel> levels = this.getMap(MapLink.ZOOMLEVELS);
        for (int i = this.getID().intValue() + 1; i < Type.VALUES.length; i++) {
            ZoomLevel lower = levels.get(i);
            if (lower != null && lower.isActive()) {
                return lower;
            }
        }
        return null;
    }

    public Vector3d getRelativeLocation() {
        if (relativeLocation == null) {
            relativeLocation = Type.VALUES[this.getID()].getDefaultRelativeLocation();
        }
        return relativeLocation;
    }

    public Vector3d getRelativeLookAt() {
        if (relativeLookAt == null) {
            relativeLookAt = Type.VALUES[this.getID()].getDefaultRelativeLookAt();
        }
        return relativeLookAt;
    }

    public Vector3d getUp() {
        if (up == null) {
            up = Type.VALUES[this.getID()].getDefaultUp();
        }
        return up;
    }

    @Deprecated
    public Double getViewBoxHeight() {
        if (viewBoxHeight == null) {
            viewBoxHeight = Type.VALUES[this.getID()].getViewBoxHeight();
        }
        return viewBoxHeight;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isBoundToCityMap() {
        if (boundToCityMap == null) {
            boundToCityMap = Type.VALUES[this.getID()].isBoundToCityMap();
        }
        return boundToCityMap;
    }

    public boolean isStartZoomLevel() {

        // default zoom is street
        if (startZoomLevel == null) {
            return Type.VALUES[this.getID()] == DEFAULT_START_LEVEL;
        }
        return startZoomLevel;
    }

    public boolean isZoomToMouse() {
        if (zoomToMouse == null) {
            zoomToMouse = Type.VALUES[this.getID()].isZoomToMouse();
        }
        return zoomToMouse;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setCameraMovementMultiplier(double cameraMovementMultiplier) {
        this.cameraMovementMultiplier = cameraMovementMultiplier;
    }

    public void setStartZoomLevel(boolean start) {
        this.startZoomLevel = start;
    }

    public void setZoomToMouse(boolean zoomToMouse) {
        this.zoomToMouse = zoomToMouse;
    }
}
