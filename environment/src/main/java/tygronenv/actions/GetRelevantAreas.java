package tygronenv.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.prep.PreparedGeometry;
import com.vividsolutions.jts.geom.prep.PreparedGeometryFactory;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Translator;
import eis.iilang.Function;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import eis.iilang.ParameterList;
import eis.iilang.Percept;
import nl.tytech.core.client.event.EventManager;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.net.serializable.PolygonItem;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.data.engine.item.Building;
import nl.tytech.data.engine.item.Land;
import nl.tytech.data.engine.item.Setting;
import nl.tytech.data.engine.item.Stakeholder;
import nl.tytech.data.engine.item.Terrain;
import nl.tytech.data.engine.serializable.MapType;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.logger.TLogger;
import tygronenv.TygronEntity;

/**
 * Creates a list of areas that can be used with the corresponding actionType.
 * Possible actionTypes are "build", "demolish" and "sell".
 * It is also possible to specify a filter, with zone([id1],[id2]) or stakeholder([id1],[id2]) functions, filtering
 * only on pieces of land that are owned by the specified stakeholder, or are in the specified zone.
 * @author Max Groenenboom
 */
public class GetRelevantAreas implements CustomAction {

	private static final Translator TRANSLATOR = Translator.getInstance();

	private static final MapType DEFAULT_MAPTYPE = MapType.MAQUETTE;

	@Override
	public String getName() {
		return "get_relevant_areas";
	}

	@Override
	public Percept call(final TygronEntity caller, final LinkedList<Parameter> parameters) {
		try {
			// Get and translate parameters.
			Iterator<Parameter> params = parameters.iterator();
			Number callID = ((Numeral) params.next()).getValue();
			String actionType = ((Identifier) params.next()).getValue();
			ParameterList filters = null;
			if (params.hasNext()) {
				Parameter filterParam = params.next();
				// If the filter parameter is not a ParameterList, it is invalid.
				if (filterParam instanceof ParameterList) {
					filters = (ParameterList) filterParam;
				} else {
					filters = new ParameterList();
				}
			} else {
				filters = new ParameterList();
			}

			return createPercept(caller, actionType, callID, filters);
		} catch (Exception e) {
			TLogger.exception(e);
			throw e;
		}
	}

	/**
	 * Create the actual Percept, after the parameters have been parsed.
	 * @param caller		The TygronEntity that called the action.
	 * @param actionType	The type of the action.
	 * @param callID		The ID of the call.
	 * @param filters		The ParameterList of filters.
	 * @return The constructed Percept.
	 */
	private Percept createPercept(final TygronEntity caller, final String actionType, final Number callID, final ParameterList filters) {
		Percept result = new Percept("relevant_areas");
		debug("action called");

		// Get multiPolygons and filter them.
		List<PolygonItem> items = getUsableArea(caller, actionType);
		for (PolygonItem item : items) {
			debug(items.size() + " items before: " + item.toString());
		}
		filterPolygons(items, filters);
		for (PolygonItem item : items) {
			debug(items.size() + " items after: " + item.toString());
		}

		// Create result parameters.
		result.addParameter(new Numeral(callID));
		ParameterList areas = new ParameterList();
		try {
			for (PolygonItem item : items) {
				MultiPolygon polygon = item.getQTMultiPolygons()[0];
				areas.add(new ParameterList(
						TRANSLATOR.translate2Parameter(polygon)[0],
						new Numeral(polygon.getArea())
				));
			}
		} catch (TranslationException e) {
			TLogger.exception(e);
			e.printStackTrace();
		}
		result.addParameter(areas);

		return result;
	}

	/**
	 * Creates a list of MultiPolygons that can be used for the specified actionType, by the specified
	 * stakeholder.
	 * @param caller The tygronEntity that called the action.
	 * @param actionType The type of the action. Can be "build", "demolish" or "sell".
	 * @return A list of MultiPolygons the stakeholder can use.
	 */
	private List<PolygonItem> getUsableArea(final TygronEntity caller, final String actionType) {
		Stakeholder stakeholder = caller.getStakeholder();
		switch (actionType) {
		case "build":
			return getBuildableArea(stakeholder);
		case "demolish":
			return getDemolishableArea(stakeholder);
		case "sell":
			return getBuildableArea(stakeholder);
		default:
			return new ArrayList<PolygonItem>();
		}
	}

	/**
	 * Returns all of the MultiPolygons the specified stakeholder can build on.
	 * @param stakeholder The stakeholder to compile a list for.
	 * @return The list of MultiPolygons.
	 */
	private List<PolygonItem> getBuildableArea(final Stakeholder stakeholder) {
		debug("function called");
		debug("fetching data");
		// Fetch all necessary data from the SDK.
		final Integer stakeholderID = stakeholder.getID();
		final ItemMap<Land> lands = EventManager.getItemMap(MapLink.LANDS);
		final ItemMap<Terrain> terrains = EventManager.getItemMap(MapLink.TERRAINS);
		final Setting reservedLandSetting = EventManager.getItem(MapLink.SETTINGS, Setting.Type.RESERVED_LAND);
		final ItemMap<Building> buildings = EventManager.getItemMap(MapLink.BUILDINGS);

		debug("combining land");
		// Get a MultiPolygon of all lands combined.
		MultiPolygon constructableLand = JTSUtils.EMPTY;
		for (Land land : lands) {
			if (land.getOwnerID() == stakeholderID) {
				constructableLand = JTSUtils.union(constructableLand, land.getMultiPolygon());
			}
		}

		debug("removing water");
		// Remove all pieces of land that cannot be build on (water).
		for (Terrain terrain : terrains) {
			if (terrain.getType().isWater()) {
				constructableLand = JTSUtils.difference(constructableLand, terrain.getMultiPolygon(DEFAULT_MAPTYPE));
			}
		}

		debug("removing reserved");
		// Remove all pieces of reserved land.
		MultiPolygon reservedLand = reservedLandSetting.getMultiPolygon();
		if (JTSUtils.containsData(reservedLand)) {
			constructableLand = JTSUtils.difference(constructableLand, reservedLand);
		}

		debug("removing buildings");
		// Remove all pieces of occupied land.
		PreparedGeometry prepped = PreparedGeometryFactory.prepare(constructableLand);
		for (Building building : buildings) {
			final MultiPolygon buildingMP = building.getMultiPolygon(DEFAULT_MAPTYPE);
			if (prepped.intersects(buildingMP)) {
				constructableLand = JTSUtils.difference(constructableLand, buildingMP);
			}
		}

		debug("finalizing selection");
		final int minArea = 200;
		final int maxArea = 2000;
		final int maxPolys = 15;
		int numPolys = 0;
		List<PolygonItem> polygons = new LinkedList<PolygonItem>();
		for (Polygon poly: JTSUtils.getPolygons(constructableLand)) {
			List<Polygon> listPolygon = JTSUtils.getTriangles(poly, minArea);
			for (Geometry geom : listPolygon) {
				if (numPolys > maxPolys) {
					break;
				}
				debug("Before: " + geom.getArea());
				geom = createNewPolygon(geom);
				geom = geom.intersection(constructableLand);
				debug("After: " + geom.getArea());
				geom = geom.buffer(-15);
				geom = geom.buffer(10);
				if (geom.getArea() < minArea / 2 || geom.getArea() > maxArea) {
					continue;
				}
				MultiPolygon mp = JTSUtils.createMP(geom);
				polygons.add(new PolygonWrapper(mp));
				numPolys++;
				debug("Added " + mp.toText());
			}
		}

		debug("created result");
		return polygons;
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
					newPoint1,
					coords[1],
					coords[2],
					newPoint2,
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
				result = TRANSLATOR.translate2Java(new Function("multipolygon",
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

	/**
	 * Print a debug message.
	 * Is a separate function to make it easier to remove debug messages.
	 * @param message The message.
	 */
	private void debug(String message) {
		// Debug temporarliy disabled;
		// System.out.println("Debug: " + message);
	}

	/**
	 * Returns all of the MultiPolygons the specified stakeholder can demolish.
	 * @param stakeholder The stakeholder to compile a list for.
	 * @return The list of MultiPolygons.
	 */
	private List<PolygonItem> getDemolishableArea(final Stakeholder stakeholder) {
		List<PolygonItem> polygons = new LinkedList<PolygonItem>();
		final ItemMap<Building> buildings = EventManager.<Building> getItemMap(MapLink.BUILDINGS);

		for (Building building : buildings) {
			if (building.getOwner().getID() == stakeholder.getID()) {
				polygons.add(building);
			}
		}
		return polygons;
	}

	/**
	 * Filters all polygons according the the specified filters.
	 * @param polygons The list of polygons to filter on.
	 * @param filters The filters.
	 */
	private void filterPolygons(final List<PolygonItem> items, final ParameterList filters) {
		debug("" + filters.size());
		for (int i = 0; i < filters.size(); i++) {
			Function param = (Function) filters.get(i);
			debug(param.getName());
			String filterName = param.getName();
			switch (filterName) {
			case "function":
				Iterator<Parameter> functionIDs = param.getParameters().iterator();
				while (functionIDs.hasNext()) {
					removeItemsWithFunction(items, functionIDs.next());
				}
			case "size":
				filterSize(items, param);
			default:
				break;
			}
		}
	}

	private void filterSize(final List<PolygonItem> items,  Function param) {
		// TODO Auto-generated method stub
		
	}

	private void removeItemsWithFunction(final List<PolygonItem> items, final Parameter _functionID) {
		Number functionID = ((Numeral)_functionID ).getValue();
		int size = items.size();
		for (int i = size - 1; i >= 0; i--) {
			PolygonItem item = items.get(i);
			if (item instanceof Building && ((Building) item).getFunctionID().intValue() != functionID.intValue()) {
				items.remove(i);
			}
		}
	}
}
