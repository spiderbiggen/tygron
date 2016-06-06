package tygronenv.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;

public class CoordinateUtilsTest {

	Coordinate coordinate1 = new Coordinate(5, 10, 0);
	Coordinate coordinate2 = new Coordinate(8, 2, 5);

	@Test
	public void testPlus() {
		Coordinate coord = CoordinateUtils.plus(coordinate1, coordinate2);
		assertEquals(13, coord.x, 0);
		assertEquals(12, coord.y, 0);
		assertEquals(5, coord.z, 0);
	}

	@Test
	public void testMinus() {
		Coordinate coord = CoordinateUtils.minus(coordinate1, coordinate2);
		assertEquals(-3, coord.x, 0);
		assertEquals(8, coord.y, 0);
		assertEquals(-5, coord.z, 0);
	}

	@Test
	public void testTimes() {
		Coordinate coord = CoordinateUtils.times(coordinate1, 4);
		assertEquals(20, coord.x, 0);
		assertEquals(40, coord.y, 0);
		assertEquals(0, coord.z, 0);
	}

	@Test
	public void testDivide() {
		Coordinate coord = CoordinateUtils.divide(coordinate1, 4);
		assertEquals(1.25, coord.x, 0);
		assertEquals(2.5, coord.y, 0);
		assertEquals(0, coord.z, 0);
	}	

}
