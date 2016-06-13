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
import nl.tytech.util.JTSUtils;
import nl.tytech.util.logger.TLogger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Custom action to retrieve a list of all possible bits of land you can buy,
 * with an area larger than 200 and smaller then 2000.
 *
 * @author Stefan Breetveld
 */
public class GetRelevantAreasBuy implements RelevantAreasAction {

	private static final int MIN_AREA = 200, MAX_AREA = 2000;
	private static final String NAME = "buy";
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
							 final ParameterList parameters) {
		MultiPolygon usableArea = getUsableArea(caller, parameters);
		final ParameterList parameterList = new ParameterList();
		for (Polygon polygon : JTSUtils.getPolygons(usableArea)) {
			final List<Polygon> polygonList = JTSUtils.getTriangles(polygon, MIN_AREA);
			for (Geometry geometry : polygonList) {
				if (geometry.getArea() > MAX_AREA) {
					continue;
				}
				MultiPolygon multiPolygon = JTSUtils.createMP(geometry);
				try {
					parameterList.add(GetRelevantAreas.convertMPtoPL(multiPolygon));
				} catch (TranslationException exception) {
					TLogger.exception(exception);
				}
			}
		}
		createdPercept.addParameter(parameterList);
	}

	/**
	 * Returns all area that can be bought in the specified zone, or if no zones are specified
	 * return areas in all zones.
	 * @param caller The caller of the action.
	 * @param parameters The parameters provided to the action.
	 * @return The multiPolygon that can be built on.
	 */
	protected MultiPolygon getUsableArea(final ContextEntity caller, final ParameterList parameters) {
		// Get a MultiPolygon of all lands combined.
		final Integer connectionID = caller.getSlotConnection().getConnectionID();
		final List<Integer> zones = new ArrayList<>();
		if (parameters != null) {
			parameters.forEach(parameter -> {
				if (parameter instanceof Numeral) {
					zones.add(((Numeral) parameter).getValue().intValue());
				} else {
					throw new IllegalArgumentException("Zone filter can only contain numbers");
				}
			});
		}
		MultiPolygon land = JTSUtils.createMP(MapUtils.getZonesCombined(connectionID, zones));
		// Remove all pieces of reserved land.
		try {
			land = MapUtils.removeReservedLand(connectionID, land);
		} catch (NullPointerException e) {
			TLogger.exception(e);
		}
		return land;
	}

	@Override
	public String getName() {
		return String.format("get_%sable_areas", NAME);
	}

	@Override
	public String getInternalName() {
		return NAME;
	}
}
