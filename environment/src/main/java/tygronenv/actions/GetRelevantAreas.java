package tygronenv.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.vividsolutions.jts.geom.MultiPolygon;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Translator;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import eis.iilang.ParameterList;
import eis.iilang.Percept;
import nl.tytech.core.client.event.EventManager;
import nl.tytech.core.event.Event;
import nl.tytech.core.event.EventListenerInterface;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.data.engine.item.Building;
import nl.tytech.data.engine.item.Stakeholder;
import nl.tytech.data.engine.serializable.MapType;
import nl.tytech.util.logger.TLogger;
import tygronenv.TygronEntity;

/**
 * Creates a list of areas that can be used with the corresponding actionType.
 * Possible actionTypes are "build", "demolish", and none more at the moment (it's a WIP).
 * It is also possible to specify a filter, with zone,<id1>,<id2> or stakeholder,<id1>,<id2> pairs, filtering
 * only on pieces of land that are owned by the specified stakeholder, or are in the specified zone.
 * @author Max_G
 */
public class GetRelevantAreas implements CustomAction, EventListenerInterface {

	private static final Translator TRANSLATOR = Translator.getInstance();

	private ItemMap<Building> buildings;

	/**
	 * Constructor for this CustomAction. It adds an eventListener for Buildings and Lands.
	 */
	public GetRelevantAreas() {
		EventManager.addListener(this, MapLink.BUILDINGS, MapLink.LANDS);
	}

	@Override
	public Percept call(final TygronEntity caller, final LinkedList<Parameter> parameters) {
		try {
			Percept result = new Percept("relevant_areas");
			System.out.println(parameters); // TODO remove //

			// Get parameters.
			Iterator<Parameter> params = parameters.iterator();
			Number callID = ((Numeral) params.next()).getValue();
			String actionType = ((Identifier) params.next()).getValue();
			ParameterList filters = null;
			Parameter filterParam = params.next();
			if (filterParam instanceof Identifier) {
				filters = new ParameterList();
			} else {
				filters = (ParameterList) filterParam;
			}

			// Get multiPolygons.
			List<MultiPolygon> polygons = getUsableArea(caller, actionType);
			System.out.println("Got polygons:");
			System.out.println(polygons); // TODO remove //

			// Filter multiPolygons.
			filterPolygons(polygons, filters);
			System.out.println("Filtered polygons:");
			System.out.println(polygons); // TODO remove //

			// Create result parameters.
			result.addParameter(new Numeral(callID));
			ParameterList areas = new ParameterList();
			try {
				for (MultiPolygon polygon : polygons) {
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
			System.out.println("Resulting Percept:");
			System.out.println(result);

			return result;
		} catch (Exception e) {
			TLogger.exception(e, e.getMessage());
			throw e;
		}
	}

	/**
	 * Creates a list of MultiPolygons that can be used for the specified actionType, by the specified
	 * stakeholder.
	 * @param caller The tygronEntity that called the action.
	 * @param actionType The type of the action. Can be "build" or "demolish".
	 * @return A list of MultiPolygons the stakeholder can use.
	 */
	private List<MultiPolygon> getUsableArea(final TygronEntity caller, final String actionType) {
		Stakeholder stakeholder = caller.getStakeholder();
		switch (actionType) {
		case "build":
			return getBuildableArea(stakeholder);
		case "demolish":
			return getDemolishableArea(stakeholder);
		default:
			return new ArrayList<MultiPolygon>();
		}
	}

	/**
	 * Returns all of the MultiPolygons the specified stakeholder can build on.
	 * @param stakeholder The stakeholder to compile a list for.
	 * @return The list of MultiPolygons.
	 */
	private List<MultiPolygon> getBuildableArea(final Stakeholder stakeholder) {
		// TODO Create this method //
		return new ArrayList<MultiPolygon>();
	}

	/**
	 * Returns all of the MultiPolygons the specified stakeholder can demolish.
	 * @param stakeholder The stakeholder to compile a list for.
	 * @return The list of MultiPolygons.
	 */
	private List<MultiPolygon> getDemolishableArea(final Stakeholder stakeholder) {
		List<MultiPolygon> polygons = new ArrayList<MultiPolygon>(buildings.size());
		buildings.forEach((building) -> polygons.add(building.getMultiPolygon(MapType.MAQUETTE)));
		return polygons;
	}

	/**
	 * Filters all polygons according the the specified filters.
	 * @param polygons The list of polygons to filter on.
	 * @param filters The filters.
	 */
	private void filterPolygons(final List<MultiPolygon> polygons, final ParameterList filters) {
		// TODO Create this method //
	}

	@Override
	public String getName() {
		return "get_relevant_areas";
	}

	@Override
	public void notifyListener(final Event event) {
		switch ((MapLink) event.getType()) {
		case BUILDINGS:
			buildings = event.<ItemMap<Building>>getContent(MapLink.COMPLETE_COLLECTION);
		default:
			break;
		}
	}
}
