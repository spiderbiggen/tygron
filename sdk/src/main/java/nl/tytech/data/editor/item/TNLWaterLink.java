/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.editor.item;

import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.item.annotations.XMLValue;

/**
 *
 * @author Jurrian
 *
 */
public class TNLWaterLink extends BehaviorGeoLink {

    public enum TNLHoofdafwatering {
        JA("ja"), //
        NEE("nee"); //

        public static final String TNL_TAG = "hoofdafwatering";

        public static TNLHoofdafwatering[] VALUES = values();

        public static TNLHoofdafwatering getDefault() {
            return NEE;
        }

        public static TNLHoofdafwatering getEnumForValue(String name) {
            for (TNLHoofdafwatering type : VALUES) {
                if (name != null && name.contains(type.name)) {
                    return type;
                }
            }
            return getDefault();
        }

        private String name;

        private TNLHoofdafwatering(String name) {
            this.name = name;
        }
    }

    public enum TNLWaterFunctie {
        DRINK_WATER_BEKKEN("drinkwaterbekken"), //
        HAVEN("haven"), //
        NATUURBAD("natuurbad"), //
        VLOEIVELD("vloeiveld"), //
        VISKWEKERIJ("viskwekerij"), //
        VISTRAP("vistrap"), //
        WATERVAL("waterval"), //
        WATERZUIVERING("waterzuivering"), //
        ZWEMBAD("zwembad"), //
        OVERIG("overig"), //
        ONBEKEND("onbekend");//

        public static final String TNL_TAG = "functie";

        public static TNLWaterFunctie[] VALUES = values();

        public static TNLWaterFunctie getDefault() {
            return ONBEKEND;
        }

        public static TNLWaterFunctie getEnumForValue(String name) {
            for (TNLWaterFunctie type : VALUES) {
                if (name != null && name.contains(type.name)) {
                    return type;
                }
            }
            return getDefault();
        }

        private String name;

        private TNLWaterFunctie(String name) {
            this.name = name;
        }
    }

    public enum TNLWaterInfrastructuur {
        VERBINDING("verbinding"), //
        KRUISING("kruising"), //
        OVERIG("overig watergebied");//

        public static final String TNL_TAG = "typeinfrastructuurwaterdeel";

        public static TNLWaterInfrastructuur[] VALUES = values();

        public static TNLWaterInfrastructuur getDefault() {
            return OVERIG;
        }

        public static TNLWaterInfrastructuur getEnumForValue(String name) {
            for (TNLWaterInfrastructuur type : VALUES) {
                if (name != null && name.contains(type.name)) {
                    return type;
                }
            }
            return getDefault();
        }

        private String name;

        private TNLWaterInfrastructuur(String name) {
            this.name = name;
        }
    }

    public enum TNLWaterType {
        ZEE("zee"), //
        WATERLOOP("waterloop"), //
        MEER("meer, plas, ven, vijver"), //
        SLOOT("greppel, droge sloot"), //
        DROOGVALLEND("droogvallend"), //
        BRON("bron, wel"), //
        ONBEKEND("onbekend");//

        public static final String TNL_TAG = "typewater";

        public static TNLWaterType[] VALUES = values();

        public static TNLWaterType getDefault() {
            return ONBEKEND;
        }

        public static TNLWaterType getEnumForValue(String name) {
            for (TNLWaterType type : VALUES) {
                if (name != null && name.contains(type.name)) {
                    return type;
                }
            }
            return getDefault();
        }

        private String name;

        private TNLWaterType(String name) {
            this.name = name;
        }
    }

    public enum TNLWaterVoorkomen {
        MET_RIET("met riet"), //
        OVERIG("overig");//

        public static final String TNL_TAG = "voorkomenwater";

        public static TNLWaterVoorkomen[] VALUES = values();

        public static TNLWaterVoorkomen getDefault() {
            return OVERIG;
        }

        public static TNLWaterVoorkomen getEnumForValue(String name) {
            for (TNLWaterVoorkomen type : VALUES) {
                if (name != null && name.contains(type.name)) {
                    return type;
                }
            }
            return getDefault();
        }

        private String name;

        private TNLWaterVoorkomen(String name) {
            this.name = name;
        }
    }

    /**
     *
     */
    private static final long serialVersionUID = -5338020748053612302L;

    @XMLValue
    private ArrayList<TNLWaterInfrastructuur> infrastructures = new ArrayList<>();
    @XMLValue
    private ArrayList<TNLWaterType> waterTypes = new ArrayList<>();
    @XMLValue
    private ArrayList<TNLWaterFunctie> waterFunctions = new ArrayList<>();
    @XMLValue
    private ArrayList<TNLHoofdafwatering> mainDrainages = new ArrayList<>();
    @XMLValue
    private ArrayList<TNLWaterVoorkomen> appearances = new ArrayList<>();

    public TNLWaterLink() {

    }

    public List<TNLWaterVoorkomen> getAppearances() {
        return appearances;
    }

    public List<TNLWaterInfrastructuur> getInfrastructures() {
        return infrastructures;
    }

    public List<TNLHoofdafwatering> getMainDrainages() {
        return mainDrainages;
    }

    public List<TNLWaterFunctie> getWaterFunctions() {
        return waterFunctions;
    }

    public List<TNLWaterType> getWaterTypes() {
        return waterTypes;
    }

}
