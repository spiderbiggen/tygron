/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import nl.tytech.util.color.TColor;

/**
 * General helper class to use for various helper methods
 *
 * @author Alexander Hofstede
 */
public class MathUtils {

    public static double avg(double... values) {
        if (values.length == 0) {
            return 0;
        }
        double sum = MathUtils.sum(values);
        return sum / values.length;
    }

    public static float avg(float... values) {
        if (values.length == 0) {
            return 0;
        }
        float sum = MathUtils.sum(values);
        return sum / values.length;
    }

    public static double clamp(double value, double min, double max) {
        return (value < min) ? min : ((value > max) ? max : value);
    }

    public static float clamp(float value, float min, float max) {
        return (value < min) ? min : ((value > max) ? max : value);
    }

    public static int clamp(int value, float min, float max) {
        return (int) clamp((float) value, min, max);
    }

    /**
     * Clamps between 0 and 1, use full for percentages.
     * @param value
     * @return
     */
    public static float clampPercentage(float value) {
        return clamp(value, 0f, 1f);
    }

    public static boolean hasDecimals(double value) {
        return (Math.ceil(value) - value) > 0d;
    }

    /**
     * Returns the index of the largest value in the array, or the first when all are the same or array is empty
     * @param array
     * @return
     */
    public static int indexOfMax(float[] array) {

        float max = Float.NEGATIVE_INFINITY;
        int index = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
                index = i;
            }
        }
        return index;
    }

    public static <T extends Comparable<T>> int indexOfMax(List<T> list) {
        if (list.size() <= 0) {
            return -1;
        } else {
            int index = 0;
            T max = list.get(index);
            for (int i = 0; i < list.size(); ++i) {
                if (list.get(i).compareTo(max) > 0) {
                    max = list.get(i);
                    index = i;
                }
            }
            return index;
        }
    }

    public static <T> int indexOfMax(List<T> list, Comparator<T> comparator) {
        if (list.size() <= 0) {
            return -1;
        } else {
            int index = 0;
            T max = list.get(index);
            for (int i = 0; i < list.size(); ++i) {
                if (comparator.compare(list.get(i), max) > 0) {
                    max = list.get(i);
                    index = i;
                }
            }
            return index;
        }
    }

    public static <T extends Comparable<T>> int indexOfMax(T[] tArray) {
        if (tArray.length <= 0) {
            return -1;
        } else {
            int index = 0;
            T max = tArray[index];
            for (int i = 0; i < tArray.length; ++i) {
                if (tArray[i].compareTo(max) > 0) {
                    max = tArray[i];
                    index = i;
                }
            }
            return index;
        }
    }

    public static <T> int indexOfMax(T[] tArray, Comparator<T> comparator) {
        if (tArray.length <= 0) {
            return -1;
        } else {
            int index = 0;
            T max = tArray[index];
            for (int i = 0; i < tArray.length; ++i) {
                if (comparator.compare(tArray[i], max) > 0) {
                    max = tArray[i];
                    index = i;
                }
            }
            return index;
        }
    }

    public static <T extends Comparable<T>> int indexOfMin(List<T> list) {
        if (list.size() <= 0) {
            return -1;
        } else {
            int index = 0;
            T min = list.get(index);
            for (int i = 0; i < list.size(); ++i) {
                if (list.get(i).compareTo(min) < 0) {
                    min = list.get(i);
                    index = i;
                }
            }
            return index;
        }
    }

    public static <T> int indexOfMin(List<T> list, Comparator<T> comparator) {
        if (list.size() <= 0) {
            return -1;
        } else {
            int index = 0;
            T min = list.get(index);
            for (int i = 0; i < list.size(); ++i) {
                if (comparator.compare(list.get(i), min) < 0) {
                    min = list.get(i);
                    index = i;
                }
            }
            return index;
        }
    }

    public static <T extends Comparable<T>> int indexOfMin(T[] tArray) {
        if (tArray.length <= 0) {
            return -1;
        } else {
            int index = 0;
            T min = tArray[index];
            for (int i = 0; i < tArray.length; ++i) {
                if (tArray[i].compareTo(min) < 0) {
                    min = tArray[i];
                    index = i;
                }
            }
            return index;
        }
    }

    public static <T> int indexOfMin(T[] tArray, Comparator<T> comparator) {
        if (tArray.length <= 0) {
            return -1;
        } else {
            int index = 0;
            T min = tArray[index];
            for (int i = 0; i < tArray.length; ++i) {
                if (comparator.compare(tArray[i], min) < 0) {
                    min = tArray[i];
                    index = i;
                }
            }
            return index;
        }
    }

    public static boolean inRange(double value, double min, double max) {
        return (value >= min && value <= max);
    }

    /**
     * checks if a value is in the range min..max, min+max are included
     *
     * @param value
     * @param min
     * @param max
     * @return
     */
    public static boolean inRange(int value, int min, int max) {
        return (value >= min && value <= max);
    }

    public static double interpolate(double x1, double x2, double inbetween) {
        return x1 + (x2 - x1) * inbetween;
    }

    public static float interpolate(float x1, float x2, float inbetween) {
        return x1 + (x2 - x1) * inbetween;
    }

    public static int interpolate(int x1, int x2, double inbetween) {
        return (int) (x1 + (x2 - x1) * inbetween);
    }

    public static TColor interpolateRGB(TColor s, TColor d, double inbetween) {
        return new TColor((int) (s.getRed() * inbetween + d.getRed() * (1 - inbetween)),//
                (int) (s.getGreen() * inbetween + d.getGreen() * (1 - inbetween)),//
                (int) (s.getBlue() * inbetween + d.getBlue() * (1 - inbetween)));//
    }

    // RGB because we can also interpolate in hsv or any other color space
    public static TColor interpolateRGB(TColor s, TColor d, float inbetween) {
        return interpolateRGB(s, d, (double) inbetween);
    }

    /**
     * returns true if Double.valueOf does not throw a NumberFormatException
     *
     * @param d
     * @return
     */
    public static boolean isDouble(String d) {
        try {
            Double.valueOf(d);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    /**
     * returns true if Integer.valueOf does not throw a NumberFormatException
     *
     * @param d
     * @return
     */
    public static boolean isInteger(String d) {
        try {
            Integer.valueOf(d);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static double max(double[] array) {

        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }

    public static float max(float[] array) {

        float max = Float.NEGATIVE_INFINITY;
        for (int i = 0; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }

    /**
     * Returns true when the max distance between values in the array is NOT larger then the given value
     * @param array
     * @param maxDistance
     * @return
     */
    public static boolean maxDistance(byte[] array, int maxDistance) {

        if (array == null || array.length == 0) {
            return true;
        }

        int min = array[0];
        int max = array[0];
        for (byte value : array) {
            min = Math.min(value, min);
            max = Math.max(value, max);
            if (Math.abs(min - max) > maxDistance) {
                return false;
            }
        }
        return true;
    }

    /**
     * Multiply all values in the given array with the multiplier.
     * @param array
     * @param multiplier
     * @return Cloned version of the array. Not same object!
     */
    public static byte[] multiplyValues(byte[] array, int multiplier) {

        byte[] newArray = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            newArray[i] = (byte) (array[i] * multiplier);
        }
        return newArray;
    }

    /**
     * Normalizes numbers in a collection.
     *
     * @param c the collection you want the sum from
     */
    public static void normalize(ArrayList<Double> c) {

        double sum = sum(c);
        for (int index = 0; index < c.size(); index++) {
            c.set(index, c.get(index) / sum);
        }
    }

    /**
     * Round a double to a certain number of decimal places
     *
     * @param value The double to round
     * @param decimalPlaces The number of decimal places to round off to
     * @return The rounded double
     */
    public static double round(double value, int decimalPlaces) {

        if (!Double.isFinite(value)) {
            return value;
        }

        // see the Javadoc about why we use a String in the constructor
        // http://java.sun.com/j2se/1.5.0/docs/api/java/math/BigDecimal.html#BigDecimal(double)
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(decimalPlaces, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * Round a float to a certain number of decimal places
     *
     * @param value The float to round
     * @param decimalPlaces The number of decimal places to round off to
     * @return The rounded float
     */
    public static float round(float value, int decimalPlaces) {

        if (Float.isNaN(value)) {
            return value;
        }

        // see the Javadoc about why we use a String in the constructor
        // http://java.sun.com/j2se/1.5.0/docs/api/java/math/BigDecimal.html#BigDecimal(float)
        BigDecimal bd = new BigDecimal(Float.toString(value));
        bd = bd.setScale(decimalPlaces, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    /**
     * Sort a map by its values
     *
     * @param <S> Key type
     * @param <T> Value type
     * @param map The map to sort
     * @return The map, sorted by its values
     */
    public static <S, T extends Comparable<T>> Map<S, T> sortByValue(Map<S, T> map) {

        List<Entry<S, T>> list = new LinkedList<Entry<S, T>>(map.entrySet());

        Collections.sort(list, (o1, o2) -> o1.getValue().compareTo(o2.getValue()));

        Map<S, T> result = new LinkedHashMap<S, T>();
        for (Entry<S, T> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    /**
     * Add all numbers in a collection.
     *
     * @param <N>
     * @param collection the collection you want the sum from
     * @return the sum of all the entries in the collection (double)
     */
    public static <N extends Number> double sum(Collection<N> collection) {

        double sum = 0;
        for (N number : collection) {
            sum += number.doubleValue();
        }
        return sum;
    }

    public static double sum(double[] c) {
        double r = 0;
        for (double x : c) {
            r += x;
        }
        return r;
    }

    /**
     * Add all numbers in an array.
     *
     * @param <N>
     * @param c the collection you want the sum from
     * @return the sum of all the entries in the collection (float)
     */
    public static float sum(float[] c) {
        float r = 0;
        for (float x : c) {
            r += x;
        }
        return r;
    }

    /**
     * Add all numbers in an array.
     *
     * @param <N>
     * @param c the collection you want the sum from
     * @return the sum of all the entries in the collection (int)
     */
    public static int sum(int[] c) {
        int r = 0;
        for (int x : c) {
            r += x;
        }
        return r;
    }
}
