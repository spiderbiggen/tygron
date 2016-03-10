/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Answer;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.item.Building.GroundLayerType;
import nl.tytech.data.engine.other.Question;
import nl.tytech.data.engine.serializable.MapType;
import nl.tytech.data.engine.serializable.TimeState;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.StringUtils;
import nl.tytech.util.logger.TLogger;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;

/**
 * PopupData
 * <p>
 * PopupData contains a popup.
 * </p>
 *
 * @author Maxim Knepfle
 */
public class PopupData extends Item implements Question {

    public enum Type {
        /**
         * Popup related to a message, for example a complaining civilian.
         */
        MESSAGE_QUESTIONMARK(false),
        /**
         * Standard popup witch give the location of a stakeholder
         */
        STAKEHOLDER_STANDARD(false),
        /**
         * Intractable popup, with 3 buttons for answering
         */
        INTERACTION(true),
        /**
         * Intractable popup, with 3 buttons for answering and a long time object
         */
        INTERACTION_WITH_DATE(true),
        /**
         * Non-interactable popup, for waiting approval popups
         */
        INFORMATION(true),
        /**
         * A LABEL is a simple name time related text like "building houses here".
         */
        LABEL(true),
        /**
         * A LABEL is a simple name plate (e.g. "Hospital")
         */
        PERMANENT_LABEL(false);

        private final static List<Type> timeStateTypes;

        static {
            timeStateTypes = new ArrayList<Type>();
            for (Type type : values()) {
                if (type.isTimeStateType()) {
                    timeStateTypes.add(type);
                }
            }
        }

        /**
         * Returns an array of only the time state related popup types.
         *
         * @return
         */
        public static List<Type> getTimeStateTypes() {
            return timeStateTypes;
        }

        private boolean timeStateType = false;

        private Type(boolean partOfNegotiation) {
            this.timeStateType = partOfNegotiation;
        }

        public boolean isTimeStateType() {
            return timeStateType;
        }
    }

    /** Generated serialVersionUID */
    private static final long serialVersionUID = -2523818277653091684L;

    @XMLValue
    @ItemIDField("STAKEHOLDERS")
    private ArrayList<Integer> visibleForStakeholderIDs = new ArrayList<>();

    @XMLValue
    @ListOfClass(Answer.class)
    private ArrayList<Answer> answers = new ArrayList<>();

    @XMLValue
    private String title = StringUtils.EMPTY;

    @XMLValue
    private Long calendar = null;

    @XMLValue
    private String text = StringUtils.EMPTY;

    @XMLValue
    private boolean ping = false;

    @XMLValue
    private Integer linkID = Item.NONE;

    @XMLValue
    private MultiPolygon polygons = null;

    @XMLValue
    private MapLink linkType = null;

    @XMLValue
    private Type type = Type.MESSAGE_QUESTIONMARK;

    @XMLValue
    private Point point = null;

    @XMLValue
    private Boolean blinking = false;

    @ItemIDField("MODEL_DATAS")
    @XMLValue
    private Integer modelDataID = Item.NONE;

    @XMLValue
    private boolean opensAutomatically = false;

    public PopupData() {

    }

    public PopupData(Type type, MapLink linkType, Integer linkID, List<Integer> visibleForStakeholderIDs) {

        this.type = type;
        this.linkType = linkType;
        this.linkID = linkID;
        this.visibleForStakeholderIDs = new ArrayList<>(visibleForStakeholderIDs);
    }

    @Override
    public void addAnswer(Answer answer) {
        answer.setID(this.answers.size());
        this.answers.add(answer);
    }

    public MapType getActiveMap() {

        if (this.getContentMapLink() == MapLink.BUILDINGS) {
            Building building = this.getItem(MapLink.BUILDINGS, this.getContentLinkID());
            if (building.getTimeState().before(TimeState.READY)) {
                return MapType.MAQUETTE;
            } else {
                return MapType.CURRENT;
            }

        } else if (this.getContentMapLink() == MapLink.MEASURES) {
            Measure measure = this.getItem(MapLink.MEASURES, this.getContentLinkID());
            if (measure.getTimeState().before(TimeState.READY)) {
                return MapType.MAQUETTE;
            } else {
                return MapType.CURRENT;
            }
        }
        return MapType.MAQUETTE;
    }

    @Override
    public List<Answer> getAnswers() {
        return answers;
    }

    public <I extends Item> I getContentItem() {
        return this.getItem(linkType, linkID);
    }

    public Integer getContentLinkID() {
        return linkID;
    }

    public MapLink getContentMapLink() {
        return linkType;
    }

    public Long getDateMillis() {
        return calendar;
    }

    @Override
    public String getDescription() {
        return null;
    }

    public Point getLocationPoint() {
        return point;
    }

    public String getLogoName() {
        return "popupIcon_questionmark";
    }

    public ModelData getModelData() {

        return getItem(MapLink.MODEL_DATAS, modelDataID);
    }

    public Integer getModelDataID() {
        return modelDataID;
    }

    public MultiPolygon getMultiPolygon() {

        if (polygons != null) {
            return polygons;
        }

        if (this.getContentMapLink() == MapLink.BUILDINGS || this.getContentMapLink() == MapLink.UPGRADE_TYPES) {
            Building building = this.getItem(MapLink.BUILDINGS, this.getContentLinkID());
            if (building.getTimeState().before(TimeState.READY)) {
                return building.getMultiPolygon(MapType.MAQUETTE);
            } else {
                return building.getMultiPolygon(MapType.CURRENT);
            }

        } else if (this.getContentMapLink() == MapLink.MEASURES) {
            MapMeasure measure = this.getItem(MapLink.MEASURES, this.getContentLinkID());
            return measure.getPolygons(GroundLayerType.VALUES);

        } else if (this.getContentMapLink() == MapLink.PANELS || this.getContentMapLink() == MapLink.SPECIAL_OPTIONS) {
            return null;

        } else if (this.getContentMapLink() == MapLink.PIPE_CLUSTERS) {
            return getPipeFlowGroupBuildingCoordinates((PipeCluster) this.getItem(MapLink.PIPE_CLUSTERS, this.getContentLinkID()));
        }
        TLogger.warning("This MapLink (" + getContentMapLink() + ") is not implemented yet for PopupData.getCoordinates()!");
        setMultiPolygon(JTSUtils.EMPTY);
        return polygons;
    }

    private MultiPolygon getPipeFlowGroupBuildingCoordinates(PipeCluster pipeFlowGroup) {

        List<Building> buildings = pipeFlowGroup.getBuildings();
        if (buildings == null || buildings.isEmpty()) {
            return JTSUtils.EMPTY;
        }

        List<Geometry> geometries = new ArrayList<>();
        for (Building building : buildings) {
            geometries.add(building.getMultiPolygon(null));
        }

        return JTSUtils.createMP(geometries);
    }

    public String getText() {
        return text;
    }

    public String getTitle() {
        return title;
    }

    public Type getType() {
        return type;
    }

    public List<Integer> getVisibleForStakeholderIDs() {
        return visibleForStakeholderIDs;
    }

    /**
     * When true an answer is required to continue the game. when false this popup will disappear automatically.
     * @return
     */
    public boolean isAnswerRequired() {

        if (this.getContentMapLink() == MapLink.BUILDINGS) {
            Building building = this.getItem(MapLink.BUILDINGS, this.getContentLinkID());
            return building.getTimeState().isAnswerRequired();
        } else if (this.getContentMapLink() == MapLink.MEASURES) {
            Measure measure = this.getItem(MapLink.MEASURES, this.getContentLinkID());
            return measure.getTimeState().isAnswerRequired();
        }
        return true;
    }

    public boolean isblinking() {
        return blinking;
    }

    /**
     * When true popup is location in tilemap area.
     * @return
     */
    public boolean isInsideTilemap() {
        Setting mapWidthSetting = this.getItem(MapLink.SETTINGS, Setting.Type.MAP_WIDTH_METERS);
        int mapWidth = mapWidthSetting.getIntValue();
        return point.getX() >= 0 && point.getX() < mapWidth && point.getY() >= 0 && point.getY() < mapWidth;
    }

    public boolean isOpeningAutomatically() {
        return opensAutomatically;
    }

    public boolean isPing() {
        return ping;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = new ArrayList<>(answers);
    }

    public void setBlinking(boolean isBlinking) {
        this.blinking = isBlinking;
    }

    public void setDateMillis(Long timeMillis) {
        this.calendar = timeMillis;
    }

    public void setLocationPoint(Point center) {
        this.point = center;
    }

    public void setModelDataID(Integer id) {
        this.modelDataID = id;
    }

    @Override
    public void setMultiPolygon(MultiPolygon polygons) {
        this.polygons = polygons;
    }

    public void setOpenAutomitically(boolean open) {
        opensAutomatically = open;
    }

    public void setPing(boolean ping) {
        this.ping = ping;
    }

    @Override
    public void setSubject(String title) {
        this.setTitle(title);
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setVisibleForStakeholderIDs(List<Integer> ids) {
        visibleForStakeholderIDs = new ArrayList<>(ids);
    }

    @Override
    public String toString() {
        return "" + point;
    }
}
