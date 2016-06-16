package contextvh.actions;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
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

	@Override
	public void internalCall(final Percept createdPercept, final ContextEntity caller,
							 final Parameters parameters) {
		int maxPolygons = DEFAULT_MAX_POLYGONS;
		double minArea = DEFAULT_MIN_AREA;
		double maxArea = DEFAULT_MAX_AREA;
		final int bufferUp = 5;
		final int bufferDown = -10;
		final Integer connectionID = caller.getSlotConnection().getConnectionID();
		final ParameterList parameterList = new ParameterList();

		List<Integer> zoneFilter = new ArrayList<>();
		if (parameters != null) {
			if (parameters.containsKey("zones")) {
				zoneFilter = parameters.get("zones").parallelStream()
						.filter(par -> par instanceof Numeral).collect(Collectors.toList())
						.stream().map(parameter -> ((Numeral) parameter).getValue().intValue())
						.collect(Collectors.toList());
			}
			if (parameters.containsKey("amount")) {
				maxPolygons = ((Numeral) parameters.get("amount").getFirst()).getValue().intValue();
			}
			if (parameters.containsKey("area")) {
				minArea = ((Numeral) parameters.get("area").getFirst()).getValue().doubleValue();
				maxArea = ((Numeral) parameters.get("area").getLast()).getValue().doubleValue();
			}
			if (minArea >= maxArea) {
				throw new IllegalArgumentException("min area must be smaller than max area");
			}
		}

		ItemMap<Zone> zones = EventManager.getItemMap(connectionID, MapLink.ZONES);
		int numPolys = 0;
		for (Zone zone : zones) {
			if (zoneFilter.isEmpty() || zoneFilter.contains(zone.getID())) {
				MultiPolygon usableArea = getUsableArea(caller, zone.getID());

				for (Polygon polygon : JTSUtils.getPolygons(usableArea)) {
					final List<Polygon> polygonList = JTSUtils.getTriangles(polygon, minArea);
					for (Geometry geometry : polygonList) {
						if (numPolys > maxPolygons) {
							break;
						}
						if (geometry.getArea() > maxArea) {
							continue;
						}
						MultiPolygon multiPolygon = JTSUtils.createMP(geometry);
						try {
							parameterList.add(GetRelevantAreas.convertMPtoPL(multiPolygon));
						} catch (TranslationException exception) {
							TLogger.exception(exception);
						}
						numPolys++;
					}
				}
			}
		}
		createdPercept.addParameter(parameterList);
	}

	/**
	 * Returns all area that can be bought in the specified zone.
	 * @param caller The caller of the action.
	 * @param zone The zone provided to the action.
	 * @return The multiPolygon that can be built on.
	 */
	protected MultiPolygon getUsableArea(final ContextEntity caller, final Integer zone) {
		if (zone == null) {
			throw new IllegalArgumentException("Zone can't be null");
		}
		// Get a MultiPolygon of all lands combined.
		final Integer connectionID = caller.getSlotConnection().getConnectionID();
		final Integer stakeholderID = caller.getStakeholder().getID();
		MultiPolygon land = MapUtils.getZone(connectionID, zone);
		// Remove all pieces of reserved land.
		land = MapUtils.removeReservedLand(connectionID, land);
		land = JTSUtils.createMP(land.difference(MapUtils.getStakeholderLands(connectionID, stakeholderID)));
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
