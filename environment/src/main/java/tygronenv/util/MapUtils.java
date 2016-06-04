package tygronenv.util;

import java.util.Arrays;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;
import nl.tytech.core.client.event.EventManager;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.data.engine.item.Zone;
import nl.tytech.util.JTSUtils;

/**
 * Utility class for performing often used operations on map elements.
 * @author Max Groenenboom
 */
public class MapUtils {
	/**
	 * Returns a Geometry of all zones with its ID in ids combined, if an empty list or no ids are given,
	 * a Geometry containing all zones will be returned.
	 * @param ids The list of zone ID's. If the list is empty, all zones will be used.
	 * @return The resulting Geometry.
	 */
	public static Geometry getZonesCombined(Integer... ids) {
		final boolean getAll = ids.length == 0;
		final ItemMap<Zone> zones = EventManager.getItemMap(MapLink.ZONES);
		Geometry result = JTSUtils.EMPTY;
		List<Integer> idList = Arrays.asList(ids);
		for (Zone zone : zones) {
			if (getAll || idList.contains(zone.getID())) {
				result = result.union(zone.getMultiPolygon());
			}
		}
		return result;
	}
}
