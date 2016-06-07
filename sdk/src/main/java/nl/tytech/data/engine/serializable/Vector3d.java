/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.serializable;

import java.io.Serializable;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.util.ObjectUtils;
import nl.tytech.util.logger.TLogger;

/**
 * Vector3fData
 * <p>
 * Stores the JMonkey Vector3f data in a special non-jmonkey related object.
 * </p>
 *
 * @author Maxim Knepfle
 */
public class Vector3d implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -1205148417639450780L;

    /**
     * the x value of the vector.
     */
    @XMLValue
    public double x = 0;

    /**
     * the y value of the vector.
     */
    @XMLValue
    public double y = 0;

    /**
     * the z value of the vector.
     */
    @XMLValue
    public double z = 0;

    public Vector3d() {

    }

    public Vector3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3d(Vector3d base) {
        this(base.x, base.y, base.z);
    }

    public double distance(Vector3d end) {
        double dx = x - end.x;
        double dy = y - end.y;
        double dz = z - end.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public Vector3d multLocal(double scalar) {
        x *= scalar;
        y *= scalar;
        z *= scalar;
        return this;
    }

    public Vector3d normalize() {

        double length = x * x + y * y + z * z;
        if (length != 1d && length != 0d) {
            length = (1.0d / Math.sqrt(length));
            return new Vector3d(x * length, y * length, z * length);
        }
        return ObjectUtils.deepCopy(this);
    }

    public void set(Vector3d other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public Double[] toArray(Double[] array) {
        if (array == null) {
            array = new Double[3];
        }

        if (array.length != 3) {
            TLogger.severe("Cannot convert " + this.getClass().getSimpleName() + " to an array of size " + array.length
                    + ". Only 3 is allowed.");
            return array;
        }

        array[0] = x;
        array[1] = y;
        array[2] = z;

        return array;
    }

    public Float[] toArray(Float[] array) {
        if (array == null) {
            array = new Float[3];
        }

        if (array.length != 3) {
            TLogger.severe("Cannot convert " + this.getClass().getSimpleName() + " to an array of size " + array.length
                    + ". Only 3 is allowed.");
            return array;
        }

        array[0] = (float) x;
        array[1] = (float) y;
        array[2] = (float) z;

        return array;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
