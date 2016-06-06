package tygronenv.util;

import java.util.Arrays;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;

import nl.tytech.core.client.event.EventManager;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.data.engine.item.Land;
import nl.tytech.data.engine.item.Setting;
import nl.tytech.data.engine.item.Terrain;
import nl.tytech.data.engine.item.Zone;
import nl.tytech.data.engine.serializable.MapType;
import nl.tytech.util.JTSUtils;

/**
 * Utility class for performing often used operations on map elements.
 * @author Max Groenenboom
 */
public final class MapUtils {

	/**
	 * Private constructor.
	 */
	private MapUtils() {
	}

	protected static final MapType DEFAULT_MAPTYPE = MapType.MAQUETTE;

	/**
	 * Returns a Geometry of all zones with its ID in ids combined, if an empty list or no ids are given,
	 * a Geometry containing all zones will be returned.
	 * @param ids The list of zone ID's. If the list is empty, all zones will be used.
	 * @return The resulting Geometry.
	 */
	public static Geometry getZonesCombined(final Integer... ids) {
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

	/**
	 * Returns a Geometry of all land of the stakeholder with ID stakeholderID.
	 * @param stakeholderID The ID of the stakeholder.
	 * @return The resulting Geometry.
	 */
	public static MultiPolygon getMyLands(final Integer stakeholderID) {
		MultiPolygon result = JTSUtils.EMPTY;
		final ItemMap<Land> lands = EventManager.getItemMap(MapLink.LANDS);
		for (Land land : lands) {
			if (land.getOwnerID() == stakeholderID) {
				result = JTSUtils.union(result, land.getMultiPolygon());
			}
		}
		return result;
	}

	/**
	 * Removes all water from the multiPolygon mp.
	 * @param mp The MultiPolygon to perform the operation on.
	 * @return The MultiPolygon without all water.
	 */
	public static MultiPolygon removeWater(final MultiPolygon mp) {
		return removeWaterOrLand(mp, true);
	}

	/**
	 * Removes all land from the multiPolygon mp.
	 * @param mp The MultiPolygon to perform the operation on.
	 * @return The MultiPolygon without all land.
	 */
	public static MultiPolygon removeLand(final MultiPolygon mp) {
		return removeWaterOrLand(mp, false);
	}

	/**
	 * Removes all water or land from the multiPolygon mp.
	 * @param mp The MultiPolygon to perform the operation on.
	 * @param removeWater Toggles whether water or land will be removed.
	 * @return The MultiPolygon without all water or land.
	 */
	private static MultiPolygon removeWaterOrLand(final MultiPolygon mp, final boolean removeWater) {
		final ItemMap<Terrain> terrains = EventManager.getItemMap(MapLink.TERRAINS);
		MultiPolygon mpResult = mp;
		for (Terrain terrain : terrains) {
			if (terrain.getType().isWater() == removeWater) {
				mpResult = JTSUtils.difference(mpResult, terrain.getMultiPolygon(DEFAULT_MAPTYPE));
			}
		}
		return mpResult;
	}

	/**
	 * Removes all reserved land from the MultiPolygon mp.
	 * @param mp The MultiPolygon to perform the operation on.
	 * @return The MultiPolygon without all reserved land.
	 */
	public static MultiPolygon removeReservedLand(final MultiPolygon mp) {
		final Setting reservedLandSetting = EventManager.getItem(MapLink.SETTINGS, Setting.Type.RESERVED_LAND);
		MultiPolygon reservedLand = reservedLandSetting.getMultiPolygon();
		MultiPolygon mpResult = mp;
		if (JTSUtils.containsData(reservedLand)) {
			mpResult = JTSUtils.difference(mpResult, reservedLand);
		}
		return mpResult;
	}
}
