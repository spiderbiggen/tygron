package nl.tytech.sdk.example;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.prep.PreparedGeometry;
import com.vividsolutions.jts.geom.prep.PreparedGeometryFactory;

import nl.tytech.core.client.event.EventManager;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.engine.item.Building;
import nl.tytech.data.engine.item.Function.PlacementType;
import nl.tytech.data.engine.item.Land;
import nl.tytech.data.engine.item.Setting;
import nl.tytech.data.engine.item.Terrain;
import nl.tytech.data.engine.item.Zone;
import nl.tytech.data.engine.serializable.MapType;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.logger.TLogger;

public class SDKTestUtil {

	public static List<Polygon> getBuildableLand(MapType mapType, Integer stakeholderID, Integer zoneID,
			PlacementType placementType) {
		Zone zone = EventManager.getItem(MapLink.ZONES, zoneID);

		//
		MultiPolygon constructableLand = zone.getMultiPolygon();
		for (Terrain terrain : EventManager.<Terrain> getItemMap(MapLink.TERRAINS)) {
			if (placementType == PlacementType.LAND && terrain.getType().isWater()) {
				constructableLand = JTSUtils.difference(constructableLand, terrain.getMultiPolygon(mapType));

			} else if (placementType == PlacementType.WATER && !terrain.getType().isWater()) {
				constructableLand = JTSUtils.difference(constructableLand, terrain.getMultiPolygon(mapType));

			}
		}

		// Reserved land is land currently awaiting land transaction
		Setting reservedLandSetting = EventManager.getItem(MapLink.SETTINGS, Setting.Type.RESERVED_LAND);
		MultiPolygon reservedLand = reservedLandSetting.getMultiPolygon();
		if (JTSUtils.containsData(reservedLand)) {
			constructableLand = JTSUtils.difference(constructableLand, reservedLand);
		}

		List<Geometry> myLands = new ArrayList<>();
		for (Land land : EventManager.<Land> getItemMap(MapLink.LANDS)) {
			if (land.getOwnerID().equals(stakeholderID)) {
				MultiPolygon mp = JTSUtils.intersection(constructableLand, land.getMultiPolygon());
				if (JTSUtils.containsData(mp)) {
					myLands.add(mp);
				}
			}
		}

		MultiPolygon myLandsMP = JTSUtils.createMP(myLands);
		// (Frank) For faster intersection checks, used prepared geometries.
		PreparedGeometry prepMyLand = PreparedGeometryFactory.prepare(myLandsMP);
		for (Building building : EventManager.<Building> getItemMap(MapLink.BUILDINGS)) {
			if (prepMyLand.intersects(building.getMultiPolygon(mapType))) {
				myLandsMP = JTSUtils.difference(myLandsMP, building.getMultiPolygon(mapType));
			}
		}

		List<Polygon> buildablePolygons = JTSUtils.getPolygons(myLandsMP);
		for (Polygon polygon : buildablePolygons) {
			TLogger.info(polygon.toString());
		}
		return buildablePolygons;
	}

	public static List<Polygon> getBuyableLand(Integer stakeholderID, Integer zoneID) {

		Zone zone = EventManager.getItem(MapLink.ZONES, zoneID);

		//
		MultiPolygon constructableLand = zone.getMultiPolygon();

		// Reserved land is land currently awaiting land transaction
		Setting reservedLandSetting = EventManager.getItem(MapLink.SETTINGS, Setting.Type.RESERVED_LAND);
		MultiPolygon reservedLand = reservedLandSetting.getMultiPolygon();
		if (JTSUtils.containsData(reservedLand)) {
			constructableLand = JTSUtils.difference(constructableLand, reservedLand);
		}

		List<Geometry> buyableLands = new ArrayList<>();
		for (Land land : EventManager.<Land> getItemMap(MapLink.LANDS)) {
			if (!land.getOwnerID().equals(stakeholderID)) {
				MultiPolygon mp = JTSUtils.intersection(constructableLand, land.getMultiPolygon());
				if (JTSUtils.containsData(mp)) {
					buyableLands.add(mp);
				}
			}
		}

		MultiPolygon myLandsMP = JTSUtils.createMP(buyableLands);
		return JTSUtils.getPolygons(myLandsMP);
	}
}
