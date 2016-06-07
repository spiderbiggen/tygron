/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.util.color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import nl.tytech.util.MathUtils;
import nl.tytech.util.logger.TLogger;

/**
 * @author Jeroen Warmerdam, Frank Baars
 * 
 */
public class ColorUtil {

    public final static int COLOR_SIMPLIFY_FACTOR = 10;

    // Interface colors
    public static final TColor COLOR_INTERFACE_RED = new TColor(225, 135, 135);

    public static final TColor COLOR_INTERFACE_SHADOW_GREEN = new TColor(125, 235, 125);

    public static final TColor COLOR_INTERFACE_GREEN = new TColor(99, 255, 99);

    public static final TColor COLOR_INTERFACE_BLUE = new TColor(192, 192, 255);

    public static final TColor COLOR_INTERFACE_YELLOW = new TColor(230, 230, 120);

    public static final TColor COLOR_INTERFACE_ORANGE = new TColor(255, 170, 70);

    public static final TColor COLOR_INTERFACE_ACTIVE = new TColor(84, 255, 255);

    public static final TColor COLOR_INTERFACE = new TColor(69, 175, 197);

    // Selection colors
    public static final TColor SELECTION_ACCEPT_COLOR = COLOR_INTERFACE_GREEN;
    public static final TColor SELECTION_NO_OWNERSHIP = COLOR_INTERFACE_ORANGE;
    public static final TColor SELECTION_DISCARD = COLOR_INTERFACE_RED;
    public static final TColor SELECTION_CURSOR_COLOR = new TColor(85, 255, 255);
    public static final TColor DEFAULT_MAQUETTE_COLOR = new TColor(146, 181, 190);
    public static final TColor DEFAULT_ORGINAL_COLOR = new TColor(146, 181, 190, 80);
    public static final TColor UI_BACKGROUND_BLACK = new TColor(0, 0, 0);
    public static final TColor UI_BACKGROUND_WHITE = new TColor(255, 255, 255);

    // Random Color Generation Parameters
    private static final int MINIMAL_RESERVED_COLOR_DISTANCE = 10;
    private static final int MAX_RANDOM_TRIES = 500;
    private static final Random random = new Random();

    private final static List<TColor> RESERVED_COLORS = new ArrayList<TColor>();

    static {
        RESERVED_COLORS.add(SELECTION_ACCEPT_COLOR);
        RESERVED_COLORS.add(SELECTION_NO_OWNERSHIP);
        RESERVED_COLORS.add(SELECTION_DISCARD);
        RESERVED_COLORS.add(SELECTION_CURSOR_COLOR);
        RESERVED_COLORS.add(DEFAULT_MAQUETTE_COLOR);
        RESERVED_COLORS.add(UI_BACKGROUND_BLACK);
        RESERVED_COLORS.add(UI_BACKGROUND_WHITE);
    }

    private final static List<TColor> DEFAULT_GRAPH_COLORS = new ArrayList<TColor>();

    static {
        DEFAULT_GRAPH_COLORS.add(new TColor(255, 10, 10));
        DEFAULT_GRAPH_COLORS.add(new TColor(0, 255, 100));
        DEFAULT_GRAPH_COLORS.add(new TColor(0, 180, 255));
        DEFAULT_GRAPH_COLORS.add(new TColor(237, 132, 0));
        DEFAULT_GRAPH_COLORS.add(new TColor(255, 255, 255));
        DEFAULT_GRAPH_COLORS.add(new TColor(150, 200, 255));
        DEFAULT_GRAPH_COLORS.add(new TColor(0, 255, 100));
        DEFAULT_GRAPH_COLORS.add(new TColor(0, 180, 255));
        DEFAULT_GRAPH_COLORS.add(new TColor(237, 132, 0));
        DEFAULT_GRAPH_COLORS.add(new TColor(255, 255, 255));
    }

    private static double colorDistance(int r1, int g1, int b1, int r2, int g2, int b2) {
        double rmean = (r1 + r2) / 2;
        int r = r1 - r2;
        int g = g1 - g2;
        int b = b1 - b2;
        double weightR = 2 + rmean / 256;
        double weightG = 4.0;
        double weightB = 2 + (255 - rmean) / 256;
        return Math.sqrt(weightR * r * r + weightG * g * g + weightB * b * b);
    }

    /**
     * Creates a random RGB value. To avoid black and white, 0 and 255 have been prohibited.
     *
     * @return random RGB value
     */
    public static TColor createRandomRGB() {
        return ColorUtil.createRandomRGB(255, null);
    }

    /**
     * Creates a random RGB value. To avoid black and white, 0 and 255 have been prohibited.
     * @param alpha The alpha the generated colour should have
     * @return random RGB value
     */
    public static TColor createRandomRGB(int alpha, Iterable<TColor> forbiddenColors) {

        double distance = 0;
        int r, g, b = 0;
        int tries = 0;
        while (tries < MAX_RANDOM_TRIES) {
            distance = Double.MAX_VALUE;
            r = random.nextInt(254) + 1;
            g = random.nextInt(254) + 1;
            b = random.nextInt(254) + 1;

            for (TColor rc : RESERVED_COLORS) {
                distance = Math.min(distance, colorDistance(r, g, b, rc.getRed(), rc.getGreen(), rc.getBlue()));
            }

            if (forbiddenColors != null) {
                for (TColor rc : forbiddenColors) {
                    distance = Math.min(distance, colorDistance(r, g, b, rc.getRed(), rc.getGreen(), rc.getBlue()));
                }
            }

            if (distance > MINIMAL_RESERVED_COLOR_DISTANCE) {
                return new TColor(r, g, b);
            }
        }
        TLogger.warning("Create non-unique color, because we've already tried " + MAX_RANDOM_TRIES + " random generations.");
        return new TColor(random.nextInt(254) + 1, random.nextInt(254) + 1, random.nextInt(254) + 1, alpha);
    }

    public static TColor createRandomRGB(Iterable<TColor> forbiddenColors) {
        return ColorUtil.createRandomRGB(255, forbiddenColors);
    }

    // (Frank) See http://stackoverflow.com/questions/18022364/how-to-convert-rgb-color-to-int-in-java
    // (Frank) TODO: Improve reference
    public static int getColorInt(int r, int g, int b) {
        r = (r << 16) & 0x00FF0000; // Shift red 16-bits and mask out other stuff
        g = (g << 8) & 0x0000FF00; // Shift Green 8-bits and mask out other stuff
        b = b & 0x000000FF; // Mask out anything not blue.
        return 0xFF000000 | r | g | b; // 0xFF000000 for 100% Alpha. Bitwise OR everything together.
    }

    public static TColor getDefaultGraphColor(int index) {
        if (index < 0 || index >= DEFAULT_GRAPH_COLORS.size()) {
            return null;
        }
        return DEFAULT_GRAPH_COLORS.get(index);
    }

    public static int getRed(int rbg) {
        return (rbg >> 16) & 0xFF;
    }

    public static TColor simplify(TColor color) {

        if (color == null) {
            return null;
        }
        int red = MathUtils.clamp(Math.round(color.getRed() / (float) COLOR_SIMPLIFY_FACTOR) * COLOR_SIMPLIFY_FACTOR, 0, 255);
        int green = MathUtils.clamp(Math.round(color.getGreen() / (float) COLOR_SIMPLIFY_FACTOR) * COLOR_SIMPLIFY_FACTOR, 0, 255);
        int blue = MathUtils.clamp(Math.round(color.getBlue() / (float) COLOR_SIMPLIFY_FACTOR) * COLOR_SIMPLIFY_FACTOR, 0, 255);
        int alpha = MathUtils.clamp(Math.round(color.getAlpha() / (float) COLOR_SIMPLIFY_FACTOR) * COLOR_SIMPLIFY_FACTOR, 0, 255);
        return new TColor(red, green, blue, alpha);
    }
}
