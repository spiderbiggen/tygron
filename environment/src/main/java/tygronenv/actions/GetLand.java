package tygronenv.actions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.prep.PreparedGeometry;
import com.vividsolutions.jts.geom.prep.PreparedGeometryFactory;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Translator;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import eis.iilang.ParameterList;
import eis.iilang.Percept;
import nl.tytech.core.client.event.EventManager;
import nl.tytech.core.client.net.SlotConnection;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.engine.item.ActionMenu;
import nl.tytech.data.engine.item.Building;
import nl.tytech.data.engine.item.Function;
import nl.tytech.data.engine.item.Function.PlacementType;
import nl.tytech.data.engine.item.Land;
import nl.tytech.data.engine.item.Setting;
import nl.tytech.data.engine.item.Stakeholder;
import nl.tytech.data.engine.item.Terrain;
import nl.tytech.data.engine.item.Zone;
import nl.tytech.data.engine.serializable.MapType;
import nl.tytech.util.JTSUtils;
import tygronenv.TygronEntity;

public class GetLand implements CustomAction {
	private static final Translator TRANSLATOR = Translator.getInstance();

	@Override
	public Percept call(TygronEntity caller, SlotConnection slotConnection, LinkedList<Parameter> parameters) {
		Stakeholder st = caller.getStakeholder();
		
		Percept res = new Percept("resultPercept");
		
		List<Polygon> polys = getBuildablePolygons(st.getID(),0);
		//List<Polygon> polys = getBuildableLand(MapType.MAQUETTE, 4, 0, PlacementType.LAND);
		try {
			if(polys.size() == 0) {
				System.out.println("LEGEEEEEEEEEE LIJST");
			}
			for(int i=0; i< 5 && i < polys.size(); i++) {
					Geometry geo = polys.get(i);
					MultiPolygon multi = JTSUtils.createMP(geo);
					multi = JTSUtils.createSquare(multi.getEnvelopeInternal());
					List<Polygon> listPolygon = JTSUtils.getTriangles(polys.get(i), 200);

					
					ParameterList parameterList = new ParameterList();
					for(Polygon p: listPolygon) {
						if(i  > 7) break;
						if(!(p.getArea() < 500 && p.getArea() > 200)) continue;
						System.out.println("Letting through: " + p.getArea());
						
						Geometry g = p;
						MultiPolygon mp = JTSUtils.createMP(g);
						parameterList.add(new ParameterList(
								TRANSLATOR.translate2Parameter(mp)[0],
								new Numeral(mp.getArea())
						));
					//	System.out.println("adding");
						i++;
					}
					res.addParameter(parameterList);
					
				/*	res.addParameter(new ParameterList(
							TRANSLATOR.translate2Parameter(multi)[0],
							new Numeral(polys.get(i).getArea())
					));*/
			}
			
		} catch (TranslationException e) {
			// TODO Auto-generated catch block
			System.out.println("FOUT TRANSLATEEEEEEEEEEEEEEEEEEEEEEEEEN");
			e.printStackTrace();
		} catch(Exception e) {
			System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
			e.printStackTrace();
		}
		System.out.println(res.toProlog());
		return res;
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "get_land";
	}
	
	
	public List<Polygon> getBuildableLand(MapType mapType, Integer stakeholderID, Integer zoneID,
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
	//	for (Polygon polygon : buildablePolygons) {
	//		TLogger.info(polygon.toString());
	//	}
		return buildablePolygons;
	}

	public List<Polygon> getBuildablePolygons(Integer stakeholderID, Integer zoneID) {
		zoneID = 0;
		
		
		Function function = null;
		actionMenuLoop: for (ActionMenu actionMenu : EventManager.<ActionMenu> getItemMap(MapLink.ACTION_MENUS)) {
			if (actionMenu.isBuildable(stakeholderID))
				for (Function buildableFunction : actionMenu.getFunctionTypeOptions()) {
					if (buildableFunction.getPlacementType() != PlacementType.WATER) {
						function = buildableFunction;
					}
					break actionMenuLoop;
				}
		}


		List<Polygon> buildablePolygons = getBuildableLand(MapType.MAQUETTE, stakeholderID, zoneID,
				function.getPlacementType());
		
		return buildablePolygons;
/*
		eventHandler.resetUpdate(MapLink.ACTION_LOGS);

		MultiPolygon selectedPlot = JTSUtils.createMP(buildablePolygons.get(0));
		TLogger.info("Size selected plot: " + selectedPlot.getArea());
		int floors = function.getDefaultFloors();
		buildActionLogID = slotConnection.fireServerEvent(true, ParticipantEventType.BUILDING_PLAN_CONSTRUCTION,
				stakeholderID, function.getID(), floors, selectedPlot);
*/

	}
	

}
