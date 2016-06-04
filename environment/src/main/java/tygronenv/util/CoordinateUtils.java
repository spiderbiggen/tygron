package tygronenv.util;

import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

import nl.tytech.util.JTSUtils;

public class CoordinateUtils {
	/**
	 * Add two <code>Coordinate</code>s together.
	 * @param c1 First Coordinate.
	 * @param c2 Second Coordinate.
	 * @return A new Coordinate.
	 */
	public static Coordinate plus(Coordinate c1, Coordinate c2) {
		return new Coordinate(c1.x + c2.x, c1.y + c2.y, c1.z + c2.z);
	}

	/**
	 * Subtract one <code>Coordinate</code> from another.
	 * @param c1 First coordinate.
	 * @param c2 Second coordinate.
	 * @return A new Coordinate.
	 */
	public static Coordinate minus(Coordinate c1, Coordinate c2) {
		return new Coordinate(c1.x - c2.x, c1.y - c2.y, c1.z - c2.z);
	}

	/**
	 * Multiply a <code>Coordinate</code> with a number.
	 * @param c1 The coordinate.
	 * @param num The number.
	 * @return A new Coordinate.
	 */
	public static Coordinate times(Coordinate c, double num) {
		return new Coordinate(c.x * num, c.y * num, c.z * num);
	}

	/**
	 * Divide a <code>Coordinate</code> with a number.
	 * @param c1 The coordinate.
	 * @param num The number.
	 * @return A new Coordinate.
	 */
	public static Coordinate divide(Coordinate c, double num) {
		return new Coordinate(c.x / num, c.y / num, c.z / num);
	}

	/**
	 * Converts a {@code List<Coordinate>} to a Geometry.
	 * @param A List of Coordinates.
	 * @return A Geometry.
	 */
	public static Geometry coordinatesToGeometry(List<Coordinate> coords) {
		return JTSUtils.createPolygon(coords);
	}
}
