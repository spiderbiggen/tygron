package contextvh.actions;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.TopologyException;
import contextvh.ContextEntity;
import contextvh.util.MapUtils;
import eis.eis2java.exception.TranslationException;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import eis.iilang.ParameterList;
import eis.iilang.Percept;
import nl.tytech.core.client.event.EventManager;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.data.engine.item.Stakeholder;
import nl.tytech.data.engine.item.Zone;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.logger.TLogger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Custom action to retrieve a list of all possible bits of land you can buy,
 * with an area larger than 200 and smaller then 2000.
 *
 * @author Stefan Breetveld
 */
public class GetRelevantAreasBuy implements RelevantAreasAction {

	private static final int DEFAULT_MAX_POLYGONS = 10;
	private static final int DEFAULT_MIN_AREA = 200, DEFAULT_MAX_AREA = 30000;
	private GetRelevantAreas parent;

	/**
	 * Create a new <code>GetRelevantAreasBuild</code> action.
	 * @param par The parent GetRelevantAreas of this action.
	 */
	public GetRelevantAreasBuy(final GetRelevantAreas par) {
		parent = par;
	}

	@Override
	public Percept call(final ContextEntity caller, final LinkedList<Parameter> parameters)
			throws TranslationException {
		// Redirect call to GetRelevantAreas to avoid code duplication.
		parameters.add(1, new Identifier(getInternalName()));
		return parent.call(caller, parameters);
	}

	/**
	 * Create a percept with pieces of land to buy per stakeholder per zone.
	 *
	 * @param createdPercept The Percept that has been created, add parameter to this percept.
	 * @param caller The ContextEntity representing the agent that called the action.
	 * @param parameters The parameters the getRelevantAreas action was called with.
	 */
	@Override
	public void internalCall(final Percept createdPercept, final ContextEntity caller,
							 final Parameters parameters) {
		int maxPolygons = DEFAULT_MAX_POLYGONS;
		double minArea = DEFAULT_MIN_AREA;
		double maxArea = DEFAULT_MAX_AREA;
		boolean emptyland = false;
		final Integer connectionID = caller.getSlotConnection().getConnectionID();
		final ParameterList parameterList = new ParameterList();

		List<Integer> zoneFilter = new ArrayList<>();
		List<Integer> stakeholderFilter = new ArrayList<>();
		if (parameters != null) {
			if (parameters.containsKey("zones")) {
				zoneFilter = createFilterList(parameters, "zones");
			}
			if (parameters.containsKey("amount")) {
				maxPolygons = ((Numeral) parameters.get("amount").getFirst()).getValue().intValue();
			}
			if (parameters.containsKey("area")) {
				minArea = ((Numeral) parameters.get("area").getFirst()).getValue().doubleValue();
				maxArea = ((Numeral) parameters.get("area").getLast()).getValue().doubleValue();
				if (minArea >= maxArea) {
					throw new IllegalArgumentException("min area must be smaller than max area");
				}
			}
			if (parameters.containsKey("stakeholders")) {
				stakeholderFilter = createFilterList(parameters, "stakeholders");
			}
			if (parameters.containsKey("buildings")) {
				emptyland = ((Identifier) parameters.get("buildings").getFirst())
						.getValue().equalsIgnoreCase("false");
			}
		}

		ItemMap<Zone> zones = EventManager.getItemMap(connectionID, MapLink.ZONES);
		ItemMap<Stakeholder> stakeholders = EventManager.getItemMap(connectionID, MapLink.STAKEHOLDERS);
		int numPolys = 0;

		for (Zone zone : zones) {
			if (!zoneFilter.isEmpty() && !zoneFilter.contains(zone.getID())) {
				continue;
			}
			for (Stakeholder stakeholder : stakeholders) {
				if (!stakeholderFilter.isEmpty() && !stakeholderFilter.contains(stakeholder.getID())) {
					continue;
				}
				MultiPolygon stakeholderLands = MapUtils.getStakeholderLands(connectionID, stakeholder.getID());
				MultiPolygon usableArea = getUsableArea(caller, zone.getID(), emptyland);
				try {
					MultiPolygon landPerStakeholder = JTSUtils
							.createMP(stakeholderLands.intersection(usableArea));
					for (Polygon polygon : JTSUtils.getPolygons(landPerStakeholder)) {
						final List<Polygon> polygonList = JTSUtils
								.getTriangles(polygon, minArea);
						for (Geometry geometry : polygonList) {
							if (numPolys > maxPolygons) {
								createdPercept.addParameter(parameterList);
								return;
							}
							if (geometry.getArea() > maxArea) {
								continue;
							}
							MultiPolygon multiPolygon = JTSUtils.createMP(geometry);
							try {
								parameterList.add(GetRelevantAreas
										.convertMPtoPL(multiPolygon));
							} catch (TranslationException exception) {
								TLogger.exception(exception);
							}
							numPolys++;
						}
					}
				} catch (TopologyException e) {
					TLogger.exception(e);
				}
			}
		}
		createdPercept.addParameter(parameterList);
	}

	/**
	 * Create a list of ids to filter on like zone ids or stakeholder ids.
	 *
	 * @param parameters The map with data
	 * @param key the key to retrieve data from the map.
	 * @return list of IDs
	 */
	private List<Integer> createFilterList(final Parameters parameters, final String key) {
		return parameters.get(key).parallelStream()
				.filter(par -> par instanceof Numeral).collect(Collectors.toList())
				.stream().map(parameter -> ((Numeral) parameter).getValue().intValue())
				.collect(Collectors.toList());
	}

	/**
	 * Returns all area that can be bought in the specified zone.
	 * @param caller The caller of the action.
	 * @param zone The zone provided to the action.
	 * @param emptyLand indicates if buildings should not be included
	 * @return The multiPolygon that can be built on.
	 */
	protected MultiPolygon getUsableArea(final ContextEntity caller, final Integer zone, final boolean emptyLand) {
		if (zone == null) {
			throw new IllegalArgumentException("Zone can't be null");
		}
		// Get a MultiPolygon of all lands combined.
		final Integer connectionID = caller.getSlotConnection().getConnectionID();
		final Integer stakeholderID = caller.getStakeholder().getID();
		MultiPolygon land = MapUtils.getZone(connectionID, zone);
		// Remove all pieces of reserved land.
		land = MapUtils.removeReservedLand(connectionID, land);
		land = JTSUtils.difference(land, MapUtils.getStakeholderLands(connectionID, stakeholderID));
		if (emptyLand) {
			ItemMap<Stakeholder> stakeholders = EventManager.getItemMap(connectionID, MapLink.STAKEHOLDERS);
			for (Stakeholder stakeholder : stakeholders) {
				land = MapUtils.removeBuildings(connectionID, stakeholder.getID(), land);
			}
		}
		land = MapUtils.removeWater(connectionID, land);
		return land;
	}

	@Override
	public String getName() {
		return "get_buyable_areas";
	}

	@Override
	public String getInternalName() {
		return "buy";
	}
}
