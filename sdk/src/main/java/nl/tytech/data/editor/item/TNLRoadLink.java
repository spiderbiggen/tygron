/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.editor.item;

import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;

/**
 *
 * @author Jurrian
 *
 */
public class TNLRoadLink extends FunctionGeoLink {

    public enum TNLRoadHardnessType {

        /**
         * Note: Maxim: order is IMPORTANT, first check longer names then smaller when using contains!
         */
        HALFVERHARD("half verhard"), //
        ONVERHARD("onverhard"), //
        VERHARD("verhard"), //
        ONBEKEND("onbekend"); //

        public final static String TNL_TAG = "verhardingstype";
        public final static TNLRoadHardnessType[] VALUES = values();

        public static TNLRoadHardnessType getEnumForValue(String name) {
            for (TNLRoadHardnessType type : VALUES) {
                if (name != null && name.contains(type.name)) {
                    return type;
                }
            }
            return ONBEKEND;
        }

        private String name;

        private TNLRoadHardnessType(String name) {
            this.name = name;
        }
    }

    public enum TNLRoadInfraType {
        CONNECTION("verbinding"), //
        INTERSECTION("kruising");

        public final static String TNL_TAG = "typeinfrastructuurwegdeel";
        public final static TNLRoadInfraType[] VALUES = values();

        public static TNLRoadInfraType getEnumForValue(String name) {
            for (TNLRoadInfraType type : VALUES) {
                if (name != null && name.contains(type.name)) {
                    return type;
                }
            }
            return CONNECTION;
        }

        private String name;

        private TNLRoadInfraType(String name) {
            this.name = name;
        }
    }

    public enum TNLRoadType {

        AUTOSNELWEG("autosnelweg"), //
        HOOFDWEG("hoofdweg"), //
        REGIONALE_WEG("regionale weg"), //
        ROLBAAN("rolbaan"), //
        PLATFORM("platform"), //
        STARTBAAN("startbaan"), //
        LANDINGSBAAN("landingsbaan"), //
        LOKALE_WEG("lokale weg"), //
        STRAAT("straat"), //
        ONBEKEND("onbekend"), //
        OVERIG("overig"), //
        ;//

        public final static String TNL_TAG = "typeweg";

        public final static TNLRoadType[] VALUES = values();

        public static TNLRoadType getEnumForValue(String name) {
            for (TNLRoadType type : VALUES) {
                if (name != null && name.contains(type.name)) {
                    return type;
                }
            }
            return ONBEKEND;
        }

        private String name;

        private TNLRoadType(String name) {
            this.name = name;
        }

    }

    public enum TNLRoadUsageType {

        // GEMENGD_VERKEER("gemengd verkeer"), // not usefull to detect! can be everything!
        SNELVERKEER("snelverkeer"), //
        VLIEGVERKEER("vliegverkeer"), //
        BUSVERKEER("busverkeer"), //
        FIETSERS("fietsers, bromfietsers"), //
        PARKEREN("parkeren"), //
        PARKEREN_CARPOOL("parkeren: carpoolplaats"), //
        PARKEREN_PR("parkeren: P+R parkeerplaats"), //
        RUITERS("ruiters"), //
        VOETGANGERS("voetgangers"), //
        ONBEKEND("onbekend"), //
        OVERIG("overig"), //
        ;//

        public final static String TNL_TAG = "hoofdverkeersgebruik";

        public final static TNLRoadUsageType[] VALUES = values();

        public static TNLRoadUsageType getEnumForValue(String name) {
            for (TNLRoadUsageType type : VALUES) {
                if (name != null && name.contains(type.name)) {
                    return type;
                }
            }
            return ONBEKEND;
        }

        private String name;

        private TNLRoadUsageType(String name) {
            this.name = name;
        }

    }

    public final static String TNL_NAME_TAG = "STRAATNAAM_NL_CSV";

    /**
     *
     */
    private static final long serialVersionUID = 4354829238829605068L;

    @XMLValue
    private boolean trees = false;

    @XMLValue
    @ListOfClass(TNLRoadUsageType.class)
    private ArrayList<TNLRoadUsageType> usageType = new ArrayList<>();

    @XMLValue
    @ListOfClass(TNLRoadType.class)
    private ArrayList<TNLRoadType> roadType = new ArrayList<>();

    @XMLValue
    @ListOfClass(TNLRoadHardnessType.class)
    private ArrayList<TNLRoadHardnessType> hardnessType = new ArrayList<>();

    public TNLRoadLink() {

    }

    public List<TNLRoadHardnessType> getHardnessTypes() {
        return hardnessType;
    }

    public List<TNLRoadType> getRoadTypes() {
        return roadType;
    }

    public List<TNLRoadUsageType> getUsageTypes() {
        return usageType;
    }

    public boolean hasTrees() {
        return trees;
    }

    public boolean isBridge() {
        return this.getFunction().isBridgeFunction();
    }

    public boolean isIntersection() {
        return this.getFunction().isIntersectionFunction();
    }
}
