/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.util.color;

import java.awt.Color;
import java.io.Serializable;
import nl.tytech.util.MathUtils;
import nl.tytech.util.SkipObfuscation;
import nl.tytech.util.StringUtils;

/**
 * Generic Tygron Color (TColor) optimized for our usage and the default to store data.
 *
 * Client apps can have there own color, e.g. ColorRGBA AWT Color Android Color or FX Color, etc...
 *
 * @author Maxim Knepfle
 */
public class TColor implements Serializable, SkipObfuscation {

    /**
     *
     */
    private static final long serialVersionUID = -4420931296679670236L;

    public static final TColor BLACK = new TColor(0, 0, 0, 255);

    public static final TColor WHITE = new TColor(255, 255, 255, 255);

    public static final TColor DARK_GRAY = new TColor(0.2, 0.2, 0.2, 1.0);

    public static final TColor GRAY = new TColor(0.5, 0.5, 0.5, 1.0);

    public static final TColor LIGHT_GRAY = new TColor(0.8, 0.8, 0.8, 1.0);

    public static final TColor RED = new TColor(255, 0, 0, 255);

    public static final TColor GREEN = new TColor(0, 255, 0, 255);

    public static final TColor BLUE = new TColor(0, 0, 255, 255);

    public static final TColor YELLOW = new TColor(255, 255, 0, 255);

    public static final TColor MAGENTA = new TColor(255, 0, 255, 255);

    public static final TColor CYAN = new TColor(0, 255, 255, 255);

    public static final TColor ORANGE = new TColor(251, 130, 0, 255);

    public static final TColor BROWN = new TColor(65, 40, 25, 255);

    public static final TColor PINK = new TColor(1.0, 0.68, 0.68, 1.0);

    /**
     * The Red, Green Blue and alpha values.
     */
    private final int rgba;
    /**
     * Cached value of other representations
     */
    private transient String html = null;
    private transient Color awt = null;
    private transient javafx.scene.paint.Color fx = null;
    private transient String fxString = null;
    private transient String string = null;

    public TColor() {
        this(0);
    }

    public TColor(double r, double g, double b, double a) {
        this((int) (r * 255d), (int) (g * 255d), (int) (b * 255d), (int) (a * 255d));
    }

    public TColor(int rgba) {
        this.rgba = rgba;
    }

    public TColor(int r, int g, int b) {
        this(r, g, b, 255);
    }

    public TColor(int r, int g, int b, int a) {
        this(((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF) << 0));
    }

    public TColor(TColor color) {
        this(color.rgba);
    }

    public TColor(TColor color, int a) {
        this(color.getRed(), color.getGreen(), color.getBlue(), a);
    }

    public int distance(TColor other) {

        int distance = Math.abs(this.getRed() - other.getRed());
        distance += Math.abs(this.getBlue() - other.getBlue());
        distance += Math.abs(this.getGreen() - other.getGreen());
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TColor)) {
            return false;
        }
        if (this == o) {
            return true;
        }
        TColor comp = (TColor) o;
        if (rgba != comp.rgba) {
            return false;
        }
        return true;
    }

    public int getAlpha() {
        return (getRGBA() >> 24) & 0xff;
    }

    public int getBlue() {
        return (getRGBA() >> 0) & 0xff;
    }

    public int getGreen() {
        return (getRGBA() >> 8) & 0xff;
    }

    public int getRed() {
        return (getRGBA() >> 16) & 0xff;
    }

    public int getRGBA() {
        return rgba;
    }

    @Override
    public int hashCode() {
        return rgba;
    }

    public TColor mult(double factor) {
        int r = MathUtils.clamp((int) (getRed() * factor), 0, 255);
        int g = MathUtils.clamp((int) (getGreen() * factor), 0, 255);
        int b = MathUtils.clamp((int) (getBlue() * factor), 0, 255);
        return new TColor(r, g, b, getAlpha());
    }

    public Color toAWTColor() {
        if (awt == null) {
            awt = new Color(this.getRed(), this.getGreen(), this.getBlue(), this.getAlpha());
        }
        return awt;
    }

    public javafx.scene.paint.Color toFXColor() {
        if (fx == null) {
            fx = new javafx.scene.paint.Color(this.getRed() / 255d, this.getGreen() / 255d, this.getBlue() / 255d, this.getAlpha() / 255d);
        }
        return fx;
    }

    public String toFXString() {
        if (fxString == null) {
            fxString = "rgba(" + this.getRed() + ", " + this.getGreen() + ", " + this.getBlue() + ", " + this.getAlpha() + ")";
        }
        return fxString;
    }

    public String toHTML() {
        if (html == null) {
            html = "#" + Integer.toHexString(this.getRGBA()).substring(2).toUpperCase();
        }
        return html;
    }

    @Override
    public String toString() {
        if (string == null) {
            string = this.getRed() + StringUtils.WHITESPACE + this.getGreen() + StringUtils.WHITESPACE + this.getBlue()
                    + StringUtils.WHITESPACE + this.getAlpha();
        }
        return string;
    }
}
