/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.editor.item;

import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.item.annotations.XMLValue;

/**
 * @author Frank Baars
 */
public abstract class TNLTerrainLink extends GeoLink {

    public enum TNLTerrainLandUse {

        AANLEGSTEIGER("aanlegsteiger"), //
        AKKERLAND("akkerland"), //
        BEBOUWD_GEBIED("bebouwd gebied"), //
        BOOMGAARD("boomgaard"), //
        BOOMKWEKERIJ("boomkrekerij"), //
        BOS_GEMENGD_BOS("bos: gemengd bos"), //
        BOS_GRIEND("bos: griend"), //
        BOS_LOOFBOS("bos: loofbos"), //
        BOS_NAALDBOS("bos: naaldbos"), //
        DODENAKKER("dodenakker"), //
        DODENAKKER_MET_BOS("donenakker met bos"), //
        FRUITKWEKERIJ("fruitkwekerij"), //
        GRASLAND("grasland"), //
        HEIDE("heide"), //
        LAADPERRON("laadperron"), //
        BASSALTBLOKKEN("basaltblokken, steenglooiing"), //
        POPULIEREN("populieren"), //
        SPOORBAANLICHAAM("spoorbaanlichaam"), //
        ZAND("zand"), //
        OVERIG("overig"), //
        ONBEKEND("onbekend"), //
        ;

        public static final String TNL_TAG = "typelandgebruik";

        public static TNLTerrainLandUse[] VALUES = values();

        public static TNLTerrainLandUse getDefault() {
            return ONBEKEND;
        }

        public static TNLTerrainLandUse getEnumForValue(String name) {
            for (TNLTerrainLandUse type : VALUES) {
                if (name != null && name.contains(type.name)) {
                    return type;
                }
            }
            return getDefault();
        }

        private String name;

        private TNLTerrainLandUse(String name) {
            this.name = name;
        }
    }

    /**
     *
     */
    private static final long serialVersionUID = -8289827208920464674L;

    @XMLValue
    private ArrayList<TNLTerrainLandUse> landUses = new ArrayList<>();

    public List<TNLTerrainLandUse> getLandUses() {
        return landUses;
    }

    public abstract boolean mustContainHouses();

}
