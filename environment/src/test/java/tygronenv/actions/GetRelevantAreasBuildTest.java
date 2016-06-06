package tygronenv.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

import nl.tytech.util.JTSUtils;

/**
 * Test the GetRelevantAreasBuild class.
 * @author Rico Tubbing
 *
 */
public class GetRelevantAreasBuildTest {


	/**
	 * Test if createNewPolygon returns a bigger geometry.
	 */
	@Test
	public void testCreateNewPolygonGoodWeather() {
		GetRelevantAreasBuild action = new GetRelevantAreasBuild(null);
		Geometry triangle = createTriangle();
		Geometry result = action.createNewPolygon(triangle);
		final double area = 1.5;
		assertEquals(area, result.getArea(), 0);
	}

	/**
	 * Test if createNewPolygon returns the same if we give it no triangle.
	 */
	@Test
	public void testCreateNewPolygonBadWeather() {
		GetRelevantAreasBuild action = new GetRelevantAreasBuild(null);
		Geometry triangle = createSquare();
		Geometry result = action.createNewPolygon(triangle);
		assertEquals(1, result.getArea(), 0);
		assertTrue(result == triangle);
	}

	/**
	 * Create a new triangle.
	 * @return A triangle.
	 */
	private Geometry createTriangle() {
		Coordinate c1 = new Coordinate(1, 1);
		Coordinate c2 = new Coordinate(1, 2);
		final Coordinate c3 = new Coordinate(3, 3);
		List<Coordinate> coordinates = new LinkedList<Coordinate>();
		coordinates.add(c1);
		coordinates.add(c2);
		coordinates.add(c3);
		coordinates.add(c1);
		return JTSUtils.createPolygon(coordinates);
	}

	/**
	 * Create a square.
	 * @return A square.
	 */
	private Geometry createSquare() {
		Coordinate c1 = new Coordinate(0, 0);
		Coordinate c2 = new Coordinate(0, 1);
		Coordinate c3 = new Coordinate(1, 0);
		Coordinate c4 = new Coordinate(1, 1);
		List<Coordinate> coordinates = new LinkedList<Coordinate>();
		coordinates.add(c1);
		coordinates.add(c2);
		coordinates.add(c4);
		coordinates.add(c3);
		coordinates.add(c1);
		return JTSUtils.createPolygon(coordinates);
	}

}
