package nl.tudelft.contextproject.tygron.api;

import com.esri.core.geometry.Polygon;

import nl.tudelft.contextproject.tygron.handlers.BooleanResultHandler;
import nl.tudelft.contextproject.tygron.handlers.JsonArrayResultHandler;
import nl.tudelft.contextproject.tygron.handlers.JsonObjectResultHandler;
import nl.tudelft.contextproject.tygron.handlers.StringResultHandler;
import nl.tudelft.contextproject.tygron.objects.Building;
import nl.tudelft.contextproject.tygron.objects.BuildingList;
import nl.tudelft.contextproject.tygron.objects.EconomyList;
import nl.tudelft.contextproject.tygron.objects.Function;
import nl.tudelft.contextproject.tygron.objects.FunctionMap;
import nl.tudelft.contextproject.tygron.objects.LandMap;
import nl.tudelft.contextproject.tygron.objects.Stakeholder;
import nl.tudelft.contextproject.tygron.objects.StakeholderList;
import nl.tudelft.contextproject.tygron.objects.ZoneList;
import nl.tudelft.contextproject.tygron.objects.indicators.IndicatorList;
import nl.tudelft.contextproject.util.PolygonUtil;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Contains all data relative to the session.
 * 
 */
public class Environment implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(Session.class);
  
  //The error margin for the amount of land
  private final double errorMargin = 0.10;

  // Environment oriented
  private HttpConnection apiConnection;
  private Session session;

  // Session data oriented
  private StakeholderList stakeholderList;
  private IndicatorList indicatorList;
  private ZoneList zoneList;
  private EconomyList economyList;
  private BuildingList buildingList;
  private FunctionMap functionMap;
  private LandMap landMap;
  
  private int mapWidth;

  private Thread environmentThread;

  public Environment(HttpConnection localApiConnection, Session session) {
    apiConnection = localApiConnection;
    this.session = session;
  }

  /**
   * Starts the update loop for this environment.
   */
  public void start() {
    if (environmentThread == null) {
      environmentThread = new Thread(this);
    }
    environmentThread.start();
  }

  /**
   * Main update run for the environment.
   */
  @Override
  public void run() {
    logger.debug("Running Environment update loop...");
    while (true) {
      reload();
      try {
        Thread.sleep(2500);
      } catch (InterruptedException e) {
        logger.error("Environment crashed!");
        throw new RuntimeException(e);
      }
    }
  }
  
  /**
   * Reloads all data.
   */
  public void reload() {
    loadStakeHolders();
    loadIndicators();
    loadZones();
    loadEconomies();
    loadBuildings();
    loadFunctions();
    loadLands();
  }
  /**
   * Load the stake holders into this session.
   * 
   * @return the list of stakeholders.
   */
  public StakeholderList loadStakeHolders() {
    logger.debug("Loading stakeholders");
    JSONArray data = apiConnection.execute("lists/"
        + "stakeholders/", CallType.GET, new JsonArrayResultHandler(), session);
    this.stakeholderList = new StakeholderList(data);
    
    loadActions();
    
    return this.stakeholderList;
  }

  /**
   * Return the stake holder list
   * 
   * @return the list of stakeholders.
   */
  public StakeholderList getStakeHolders() {
    return this.stakeholderList;
  }
  
  /**
   * Load actions and assign their functions to stakeholders.
   * @param stakeholderList The list of stakeholders.
   */
  private void loadActions() {
    JSONArray actionList = apiConnection.execute("lists/actionmenus/", 
        CallType.GET, new JsonArrayResultHandler(), session);
    for (int i = 0; i < actionList.length(); i++) {
      JSONObject action = actionList.getJSONObject(i).getJSONObject("ActionMenu");
      JSONArray functions = action.getJSONArray("functionTypes");
      JSONObject stakeholders = action.getJSONObject("activeForStakeholder");
      
      JSONArray keys = stakeholders.names();
      Map<Integer, JSONArray> functionMap = new HashMap<Integer, JSONArray>();
      for (int j = 0; j < keys.length(); j++) {
        if (stakeholders.getBoolean(keys.getString(j))) {
          functionMap.put(keys.getInt(j), functions);
        }
      }
      setFunctions(functionMap);
    }
  }
  
  private void setFunctions(Map<Integer, JSONArray> functionsMap) {
    for (Stakeholder stakeholder : stakeholderList) {
      JSONArray functions = functionsMap.get(stakeholder.getId());
      if (functions != null) {
        stakeholder.addAllowedFunctions(functions);
      }
    }
  }

  /**
   * Load the indicators into this session.
   * 
   * @return the list of indicators.
   */
  public IndicatorList loadIndicators() {
    logger.debug("Loading indicators");
    JSONArray data = apiConnection.execute("lists/"
        + "indicators", CallType.GET, new JsonArrayResultHandler(), session);
    this.indicatorList = new IndicatorList(data);
    return this.indicatorList;
  }

  /**
   * Return the indicator list
   * 
   * @return the list of indicators.
   */
  public IndicatorList getIndicators() {
    return this.indicatorList;
  }

  /**
   * Load the zones into this session.
   * 
   * @return the list of zones.
   */
  public ZoneList loadZones() {
    logger.debug("Loading zones");
    JSONArray data = apiConnection.execute("lists/"
        + "zones", CallType.GET, new JsonArrayResultHandler(), session);
    this.zoneList = new ZoneList(data);
    return this.zoneList;
  }

  /**
   * Return the zone list
   * 
   * @return the list of zones.
   */
  public ZoneList getZones() {
    return this.zoneList;
  }

  /**
   * Load the economies into this session.
   * 
   * @return the list of economics.
   */
  public EconomyList loadEconomies() {
    logger.debug("Loading economies");
    JSONArray data = apiConnection.execute("lists/"
        + "economies", CallType.GET, new JsonArrayResultHandler(), session);
    this.economyList = new EconomyList(data);
    return this.economyList;
  }

  /**
   * Return the economies list
   * 
   * @return the list of economics.
   */
  public EconomyList getEconomies() {
    return this.economyList;
  }

  /**
   * Load the buildings into this session.
   * 
   * @return the list of buildings.
   */
  public BuildingList loadBuildings() {
    logger.debug("Loading buildings");
    JSONArray data = apiConnection.execute("lists/"
        + "buildings", CallType.GET, new JsonArrayResultHandler(), session);
    this.buildingList = new BuildingList(data);
    return this.buildingList;
  }

  /**
   * Return the buildings list
   * 
   * @return the list of buildings.
   */
  public BuildingList getBuildings() {
    return this.buildingList;
  }
  
  /**
   * Loads all functions into this session.
   * @return A map of the functions.
   */
  public FunctionMap loadFunctions() {
    logger.debug("Loading functions");
    JSONArray data = apiConnection.execute("lists/functions", 
        CallType.GET, new JsonArrayResultHandler(), session);
    this.functionMap = new FunctionMap(data);
    return this.functionMap;
  }
  
  /**
   * Loads all lands into this session.
   * @return A map of the lands.
   */
  public LandMap loadLands() {
    logger.debug("Loading lands");
    JSONArray data = apiConnection.execute("lists/lands", 
        CallType.GET, new JsonArrayResultHandler(), session);
    this.landMap = new LandMap(data);
    return this.landMap;
  }
  
  /**
   * Builds a project on a piece of land.
   * @param stakeholder The stakeholder of the building project.
   * @param surface The desired surface of the building.
   * @return Whether the build request was sent or not.
   */
  public boolean build(Stakeholder stakeholder, double surface) {
    logger.debug("Building project started");
    Polygon availableLand = getAvailableLand(stakeholder);
    double availableSurface = availableLand.calculateArea2D();
    
    int minFloors = getMinFloors(availableLand, surface);
    int maxFloors = getMaxFloors(stakeholder);
    boolean enoughFloors = minFloors <= maxFloors;
    if (!enoughFloors) {
      logger.info("Not enough land for building");
      return false;
    }
    
    Function function = getFunction(stakeholder, minFloors);
    
    // Select a random amount of floors
    int neededFloors = minFloors;
    if (minFloors != function.getMax_floors()) {
      Random random = new Random();
      neededFloors = random.nextInt(function.getMax_floors() - minFloors) + minFloors;
    }
    double neededSurface = surface / neededFloors;
    
    Polygon selectedLand;
    if (withinMargin(availableLand, neededSurface)) {
      // If the available land is already the required size, select all of it
      selectedLand = availableLand;
    } else if (availableSurface < 5) {
      // The available land is small, so fill it entirely
      selectedLand = availableLand;
      neededFloors = (int) Math.ceil(surface / availableSurface);
    } else {
      // Otherwise, take a random piece of land
      selectedLand = getSuitableLand(availableLand, neededSurface);
    }
    
    if (selectedLand != null) {
      BuildRequest buildRequest = new BuildRequest(stakeholder, 
          function, neededFloors, selectedLand);
      apiConnection.execute("event/PlayerEventType/BUILDING_PLAN_CONSTRUCTION/", 
          CallType.POST, new StringResultHandler(), session, buildRequest);
      return true;
    } else {
      logger.info("Not enough land for building");
      return false;
    }
  }
  
  class BuildRequest extends JSONArray {
    public BuildRequest(Stakeholder stakeholder, Function function, int floors, Polygon polygon) {
      this.put(stakeholder.getId());
      this.put(function.getId());
      this.put(floors);
      this.put(PolygonUtil.toString(polygon));
    }
  }
  
  /**
   * Gets a piece of land from the available land with a certain surface.
   * @param availableLand The available land.
   * @param surface The desired surface of the land.
   * @return A piece of land with a certain surface.
   */
  private Polygon getSuitableLand(Polygon availableLand, double surface) {
    getMapWidth();
    Random random = new Random();
    Polygon selectedLand;
    Polygon intersection;
    
    double x1;
    double y1;
    double x2;
    double y2;
    // Select a random rectangle in the map and get the part of land that is available.
    do {
      x1 = random.nextDouble() * mapWidth;
      y1 = random.nextDouble() * mapWidth;
      x2 = random.nextDouble() * mapWidth;
      y2 = random.nextDouble() * mapWidth;
      selectedLand = PolygonUtil.makeRectangle(x1, y1, x2, y2);
      intersection = PolygonUtil.polygonIntersection(selectedLand, availableLand);
    } while (intersection.calculateArea2D() < surface);
    
    while (!withinMargin(intersection, surface) && intersection.calculateArea2D() != 0) {
      // Reduce the land to a square as much as possible
      if (Math.abs(x1 - x2) > Math.abs(y1 - y2)) {
        x1 = x2 > x1 ? x1 + 0.5 : x1 - 0.5;
      } else {
        y1 = y2 > y1 ? y1 + 0.5 : y1 - 0.5;
      }
      selectedLand = PolygonUtil.makeRectangle(x1, y1, x2, y2);
      intersection = PolygonUtil.polygonIntersection(selectedLand, availableLand);
    }

    // If the selected land is empty, try again
    return intersection.calculateArea2D() != 0 ? intersection :
      getSuitableLand(availableLand, surface);
  }
  
  /**
   * Gets the minimum amount of floors necessary to get the desires surface.
   * @return The minimum amount of floors.
   */
  private int getMinFloors(Polygon selectedLand, double surface) {
    return (int) Math.ceil(surface / selectedLand.calculateArea2D());
  }
  
  /**
   * Gets the maximum amount of floors possible in the stakeholder's functions.
   * @return The minimum amount of floors.
   */
  private int getMaxFloors(Stakeholder stakeholder) {
    loadFunctions();
    List<Integer> functions = stakeholder.getAllowedFunctions();
    int result = 0;
    for (int functionId : functions) {
      int maxFloors = functionMap.get(functionId).getMax_floors();
      result = max(maxFloors, result);
    }
    return result;
  }
  
  /**
   * Gets a function that fits the requested amount of floors.
   * @param stakeholder The stakeholder.
   * @param floors The requested amount of floors.
   * @return The function that fits the criteria.
   */
  private Function getFunction(Stakeholder stakeholder, int floors) {
    loadFunctions();
    List<Integer> functions = stakeholder.getAllowedFunctions();
    List<Function> functionList = new ArrayList<Function>();
    for (Integer functionId : functions) {
      Function function = functionMap.get(functionId);
      if (floors <= function.getMax_floors() && floors >= function.getMin_floors()) {
        functionList.add(function);
      }
    }
    
    // Pick a random function
    Random random = new Random();
    return functionList.get(random.nextInt(functionList.size()));
  }
  
  private boolean withinMargin(Polygon selectedLand, double surface) {
    return selectedLand.calculateArea2D() < surface * (1 + errorMargin) 
        && selectedLand.calculateArea2D() > surface * (1 - errorMargin);
  }
  
  /**
   * Buys a piece of land.
   * @param stakeholder The buyer.
   * @param surface The desired surface of the land.
   * @param cost The amount of money per unit of land.
   */
  public void buyLand(Stakeholder stakeholder, double surface, int cost) {
    logger.debug("Buying land");
    BuyLandRequest buyLandRequest = new BuyLandRequest(stakeholder, new Polygon(), cost);
    apiConnection.execute("event/PlayerEventType/MAP_BUY_LAND/", 
        CallType.POST, new StringResultHandler(), session, buyLandRequest);
  }
  
  class BuyLandRequest extends JSONArray {
    public BuyLandRequest(Stakeholder stakeholder, Polygon polygons, int cost) {
      this.put(stakeholder.getId());
      this.put(polygons);
      this.put(cost);
    }
  }

  private void getMapWidth() {
    if (mapWidth == 0) {
      mapWidth = apiConnection.execute("lists/settings/36/", 
          CallType.GET, new JsonObjectResultHandler(), session).getInt("value");
    }
  }
  
  /**
   * Select a stakeholder to play, can only be done once.
   * @param stakeholderId the stakeholder id to select.
   */
  public boolean setStakeholder(int stakeholderId) {
    boolean retValue = apiConnection.execute("event/PlayerEventType/STAKEHOLDER_SELECT", 
        CallType.POST, new BooleanResultHandler(), session, 
        new StakeHolderSelectRequest(stakeholderId,session.getClientToken()));
    logger.info("Setting stakeholder to #" + stakeholderId + ". Operation " 
        + ((retValue) ? "succes!" : "failed!" ));
    return retValue;
  }
  
  class StakeHolderSelectRequest extends JSONArray {
    public StakeHolderSelectRequest(int stakeholderId, String sessionToken) {
      this.put(stakeholderId);
      this.put(sessionToken);
    }
  }
  
  /**
   * Get all of the stakeholder's land that is free from buildings.
   * @param stakeholder The stakeholder.
   * @return The stakeholder's free land.
   */
  private Polygon getAvailableLand(Stakeholder stakeholder) {
    Polygon result = new Polygon();
    for (Integer landId : stakeholder.getOwnedLands()) {
      Polygon land = loadLands().get(landId).getPolygon();
      for (Building building : loadBuildings()) {
        if (!demolished(building)) {
          land = PolygonUtil.polygonDifference(land, building.getPolygon());
        }
      }
      result = PolygonUtil.polygonUnion(result, land);
    }
    return result;
  }
  
  private boolean demolished(Building building) {
    return building.getState().matches("(.*)DEMOLISH(.*)") || building.getState().equals("NOTHING");
  }
  
  private int max(int i1, int i2) {
    if (i1 > i2) {
      return i1;
    }
    return i2;
  }
}
