package tygronenv.actions;

import java.util.LinkedList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.prep.PreparedGeometry;
import com.vividsolutions.jts.geom.prep.PreparedGeometryFactory;

import eis.eis2java.exception.TranslationException;
import eis.iilang.Function;
import eis.iilang.Identifier;
import eis.iilang.Parameter;
import eis.iilang.ParameterList;
import eis.iilang.Percept;
import nl.tytech.core.client.event.EventManager;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.data.engine.item.Building;
import nl.tytech.data.engine.item.Land;
import nl.tytech.data.engine.item.Setting;
import nl.tytech.data.engine.item.Terrain;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.logger.TLogger;
import tygronenv.TygronEntity;

public class GetRelevantAreasBuild implements RelevantAreasAction {

	@Override
	public Percept call(TygronEntity caller, LinkedList<Parameter> parameters) {
		// TODO Not yet implemented.
		return null;
	}

	@Override
	public String getName() {
		return "get_buildable_areas";
	}

	@Override
	public String getInternalName() {
		return "build";
	}

	@Override
	public void internalCall(Percept createdPercept, TygronEntity caller, ParameterList parameters) {
		GetRelevantAreas.debug("function called");
		GetRelevantAreas.debug("fetching data");
		// Fetch all necessary data from the SDK.
		final Integer stakeholderID = caller.getStakeholder().getID();
		final ItemMap<Land> lands = EventManager.getItemMap(MapLink.LANDS);
		final ItemMap<Terrain> terrains = EventManager.getItemMap(MapLink.TERRAINS);
		final Setting reservedLandSetting = EventManager.getItem(MapLink.SETTINGS, Setting.Type.RESERVED_LAND);
		final ItemMap<Building> buildings = EventManager.getItemMap(MapLink.BUILDINGS);

		GetRelevantAreas.debug("combining land");
		// Get a MultiPolygon of all lands combined.
		MultiPolygon constructableLand = JTSUtils.EMPTY;
		for (Land land : lands) {
			if (land.getOwnerID() == stakeholderID) {
				constructableLand = JTSUtils.union(constructableLand, land.getMultiPolygon());
			}
		}

		GetRelevantAreas.debug("removing water");
		// Remove all pieces of land that cannot be build on (water).
		for (Terrain terrain : terrains) {
			if (terrain.getType().isWater()) {
				constructableLand = JTSUtils.difference(constructableLand, terrain.getMultiPolygon(GetRelevantAreas.DEFAULT_MAPTYPE));
			}
		}

		GetRelevantAreas.debug("removing reserved");
		// Remove all pieces of reserved land.
		MultiPolygon reservedLand = reservedLandSetting.getMultiPolygon();
		if (JTSUtils.containsData(reservedLand)) {
			constructableLand = JTSUtils.difference(constructableLand, reservedLand);
		}

		GetRelevantAreas.debug("removing buildings");
		// Remove all pieces of occupied land.
		PreparedGeometry prepped = PreparedGeometryFactory.prepare(constructableLand);
		for (Building building : buildings) {
			final MultiPolygon buildingMP = building.getMultiPolygon(GetRelevantAreas.DEFAULT_MAPTYPE);
			if (prepped.intersects(buildingMP)) {
				constructableLand = JTSUtils.difference(constructableLand, buildingMP);
			}
		}

		GetRelevantAreas.debug("finalizing selection");
		final int minArea = 200;
		final int maxArea = 2000;
		final int maxPolys = 15;
		int numPolys = 0;
		final ParameterList params = new ParameterList();
		for (Polygon poly: JTSUtils.getPolygons(constructableLand)) {
			List<Polygon> listPolygon = JTSUtils.getTriangles(poly, minArea);
			for (Geometry geom : listPolygon) {
				if (numPolys > maxPolys) {
					break;
				}
				GetRelevantAreas.debug("Before: " + geom.getArea());
				geom = createNewPolygon(geom);
				GetRelevantAreas.debug("After: " + geom.getArea());
				geom = geom.intersection(constructableLand);
				geom = geom.buffer(-10).buffer(5);
				if (geom.getArea() < minArea / 2 || geom.getArea() > maxArea) {
					continue;
				}
				MultiPolygon mp = JTSUtils.createMP(geom);
				constructableLand = JTSUtils.createMP(constructableLand.difference(mp));
				try {
					params.add(GetRelevantAreas.convertMPtoPL(mp));
				} catch (TranslationException e) {
					TLogger.exception(e);
					continue;
				}
				numPolys++;
				GetRelevantAreas.debug("Added " + mp.toText());
			}
		}
		GetRelevantAreas.debug("created result");
	}

	/**
	 * Create a new polygon from a triangle, by creating two new corner points.
	 * @param triangle The triangle to derive the square from.
	 * @return The new polygon.
	 */
	private Geometry createNewPolygon(Geometry triangle) {
		if (triangle.getNumPoints() != 4) {
			return triangle;
		} else {
			Coordinate[] coords = triangle.getCoordinates();

			// Create first new point.
			Coordinate newPoint1 = plus(coords[0], coords[2]);
			newPoint1 = divide(newPoint1, 2);
			newPoint1 = minus(newPoint1, coords[1]);
			newPoint1 = plus(coords[1], times(newPoint1, 1.25));

			// Create second new point.
			Coordinate newPoint2 = plus(coords[0], coords[1]);
			newPoint2 = divide(newPoint2, 2);
			newPoint2 = minus(newPoint2, coords[2]);
			newPoint2 = plus(coords[2], times(newPoint2, 1.25));

			// Insert two new points 
			Coordinate[] newCoords = {
					coords[0],
					newPoint2,
					coords[1],
					coords[2],
					newPoint1,
					coords[0]
			};
			
			// Using stringbuilder and translator because no other way has been found
			// to create multipolygons yet.
			StringBuilder sb = new StringBuilder("MULTIPOLYGON (((");
			for (Coordinate coord : newCoords) {
				sb.append(coord.x + " " + coord.y + ", ");
			}
			sb.delete(sb.length() - 2, sb.length());
			sb.append(")))");

			MultiPolygon result = null;
			try {
				result = GetRelevantAreas.TRANSLATOR.translate2Java(new Function("multipolygon",
						new Identifier(sb.toString())), MultiPolygon.class);
			} catch (TranslationException e) {
				TLogger.exception(e);
				return triangle;
			}

			return result;
		}
	}

	/**
	 * Add two Coordinates together.
	 * @param c1 First coordinate.
	 * @param c2 Second coordinate
	 * @return A new Coordinate.
	 */
	private Coordinate plus(Coordinate c1, Coordinate c2) {
		return new Coordinate(c1.x + c2.x, c1.y + c2.y, c1.z + c2.z);
	}

	/**
	 * Subtract one Coordinate from another.
	 * @param c1 First coordinate.
	 * @param c2 Second coordinate
	 * @return A new Coordinate.
	 */
	private Coordinate minus(Coordinate c1, Coordinate c2) {
		return new Coordinate(c1.x - c2.x, c1.y - c2.y, c1.z - c2.z);
	}

	/**
	 * Multiply a Coordinate with a number.
	 * @param c1 The coordinate.
	 * @param num the number.
	 * @return A new Coordinate.
	 */
	private Coordinate times(Coordinate c, double num) {
		return new Coordinate(c.x * num, c.y * num, c.z * num);
	}

	/**
	 * Divide a Coordinate with a number.
	 * @param c1 The coordinate.
	 * @param num the number.
	 * @return A new Coordinate.
	 */
	private Coordinate divide(Coordinate c, double num) {
		return new Coordinate(c.x / num, c.y / num, c.z / num);
	}
}
