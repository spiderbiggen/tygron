package tygronenv.util;

import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

import nl.tytech.util.JTSUtils;

/**
 * Utilty class for coordinates.
 * @author Max Groenenboom
 *
 */
public class CoordinateUtils {

	//TODO: Change to private
	/**
	 * Private constructor.
	 */
	public CoordinateUtils() { }

	/**
	 * Add two <code>Coordinate</code>s together.
	 * @param c1 First Coordinate.
	 * @param c2 Second Coordinate.
	 * @return A new Coordinate.
	 */
	public static Coordinate plus(final Coordinate c1, final Coordinate c2) {
		return new Coordinate(c1.x + c2.x, c1.y + c2.y, c1.z + c2.z);
	}

	/**
	 * Subtract one <code>Coordinate</code> from another.
	 * @param c1 First coordinate.
	 * @param c2 Second coordinate.
	 * @return A new Coordinate.
	 */
	public static Coordinate minus(final Coordinate c1, final Coordinate c2) {
		return new Coordinate(c1.x - c2.x, c1.y - c2.y, c1.z - c2.z);
	}

	/**
	 * Multiply a <code>Coordinate</code> with a number.
	 * @param c The coordinate.
	 * @param num The number.
	 * @return A new Coordinate.
	 */
	public static Coordinate times(final Coordinate c, final double num) {
		return new Coordinate(c.x * num, c.y * num, c.z * num);
	}

	/**
	 * Divide a <code>Coordinate</code> with a number.
	 * @param c The coordinate.
	 * @param num The number.
	 * @return A new Coordinate.
	 */
	public static Coordinate divide(final Coordinate c, final double num) {
		return new Coordinate(c.x / num, c.y / num, c.z / num);
	}

	/**
	 * Converts a {@code List<Coordinate>} to a Geometry.
	 * @param coords A List of Coordinates.
	 * @return A Geometry.
	 */
	public static Geometry coordinatesToGeometry(final List<Coordinate> coords) {
		return JTSUtils.createPolygon(coords);
	}
}
