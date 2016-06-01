package tygronenv.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Translator;
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
 * It is also possible to specify a filter, with zone,[id1],[id2] or stakeholder,[id1],[id2] pairs, filtering
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

		// Get multiPolygons and filter them.
		List<PolygonItem> items = getUsableArea(caller, actionType);
		filterPolygons(items, filters);

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
		// Fetch all necessary data from the SDK.
		Integer stakeholderID = stakeholder.getID();
		ItemMap<Land> lands = EventManager.<Land>getItemMap(MapLink.LANDS);
		ItemMap<Terrain> terrains = EventManager.<Terrain> getItemMap(MapLink.TERRAINS);
		Setting reservedLandSetting = EventManager.getItem(MapLink.SETTINGS, Setting.Type.RESERVED_LAND);
		ItemMap<Building> buildings = EventManager.<Building> getItemMap(MapLink.BUILDINGS);

		// Get a MultiPolygon of all lands combined.
		MultiPolygon constructableLand = JTSUtils.EMPTY;
		for (Land land : lands) {
			if (land.getOwnerID() == stakeholderID) {
				JTSUtils.union(constructableLand, land.getMultiPolygon());
			}
		}

		// Remove all pieces of land that cannot be build on (water).
		for (Terrain terrain : terrains) {
			if (terrain.getType().isWater()) {
				constructableLand = JTSUtils.difference(constructableLand, terrain.getMultiPolygon(DEFAULT_MAPTYPE));
			}
		}

		// Remove all pieces of reserved land.
		MultiPolygon reservedLand = reservedLandSetting.getMultiPolygon();
		if (JTSUtils.containsData(reservedLand)) {
			constructableLand = JTSUtils.difference(constructableLand, reservedLand);
		}

		// Remove all pieces of occupied land.
		for (Building building : buildings) {
			constructableLand = JTSUtils.difference(constructableLand, building.getMultiPolygon(DEFAULT_MAPTYPE));
		}

		// Prepare a list of multiPolygons to return.
		List<PolygonItem> polygons = new LinkedList<PolygonItem>();
		List<Polygon> buildablePolygons = JTSUtils.getPolygons(constructableLand);
		for (Polygon polygon : buildablePolygons) {
			MultiPolygon mp = JTSUtils.createMP(polygon);
			polygons.add(new PolygonWrapper(mp));
		}
		return polygons;
	}

	/**
	 * Returns all of the MultiPolygons the specified stakeholder can demolish.
	 * @param stakeholder The stakeholder to compile a list for.
	 * @return The list of MultiPolygons.
	 */
	private List<PolygonItem> getDemolishableArea(final Stakeholder stakeholder) {
		List<PolygonItem> polygons = new LinkedList<PolygonItem>();
		ItemMap<Building> buildings = EventManager.<Building> getItemMap(MapLink.BUILDINGS);

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
	private void filterPolygons(final List<PolygonItem> polygons, final ParameterList filters) {
		// TODO Create this method //
	}
}
