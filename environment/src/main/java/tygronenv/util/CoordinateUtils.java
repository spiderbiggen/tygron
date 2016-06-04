package tygronenv.util;

import com.vividsolutions.jts.geom.Coordinate;

public class CoordinateUtils {
	/**
	 * Add two Coordinates together.
	 * @param c1 First coordinate.
	 * @param c2 Second coordinate
	 * @return A new Coordinate.
	 */
	public static Coordinate plus(Coordinate c1, Coordinate c2) {
		return new Coordinate(c1.x + c2.x, c1.y + c2.y, c1.z + c2.z);
	}

	/**
	 * Subtract one Coordinate from another.
	 * @param c1 First coordinate.
	 * @param c2 Second coordinate
	 * @return A new Coordinate.
	 */
	public static Coordinate minus(Coordinate c1, Coordinate c2) {
		return new Coordinate(c1.x - c2.x, c1.y - c2.y, c1.z - c2.z);
	}

	/**
	 * Multiply a Coordinate with a number.
	 * @param c1 The coordinate.
	 * @param num the number.
	 * @return A new Coordinate.
	 */
	public static Coordinate times(Coordinate c, double num) {
		return new Coordinate(c.x * num, c.y * num, c.z * num);
	}

	/**
	 * Divide a Coordinate with a number.
	 * @param c1 The coordinate.
	 * @param num the number.
	 * @return A new Coordinate.
	 */
	public static Coordinate divide(Coordinate c, double num) {
		return new Coordinate(c.x / num, c.y / num, c.z / num);
	}
}
