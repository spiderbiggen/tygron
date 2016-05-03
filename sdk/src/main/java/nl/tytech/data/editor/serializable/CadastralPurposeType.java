/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.editor.serializable;

import java.util.ArrayList;
import java.util.List;
import nl.tytech.data.engine.serializable.Category;
import nl.tytech.util.StringUtils;
import nl.tytech.util.logger.TLogger;

/**
 *
 * General purpose of the residence as registered by the Dutch government, linked to the internal FunctionCategory.
 *
 * @author Jurrian Hartveldt
 * @specialization GIS
 */
public enum CadastralPurposeType {

    LIVING("woonfunctie", 5, Category.NORMAL, Category.SOCIAL, Category.LUXE, Category.SENIOR, Category.STUDENT), //
    GATHERING("bijeenkomstfunctie", 3, Category.SHOPPING), //
    PRISON("celfunctie", 1, Category.OTHER), //
    HEALTHCARE("gezondheidszorgfunctie", 10, Category.HEALTHCARE), //
    INDUSTRIAL("industriefunctie", 10, Category.INDUSTRY), //
    OFFICE("kantoorfunctie", 10, Category.OFFICES), //
    LODGING("logiesfunctie", 3, Category.SHOPPING), //
    EDUCATIONAL("onderwijsfunctie", 10, Category.EDUCATION), //
    SPORTS("sportfunctie", 5, Category.LEISURE), //
    SHOPPING("winkelfunctie", 10, Category.SHOPPING), //
    MULTIPLE("meervoudige functie", 10, Category.values()), //
    OTHER("overige gebruiksfunctie", 1, Category.OTHER);

    public static final String BAG_TAG = "gebruiksdoel";

    public static final CadastralPurposeType[] VALUES = values();

    public static final CadastralPurposeType getDutch(String dutchCadastralKey) {

        for (CadastralPurposeType type : VALUES) {
            if (type.getDutchCadastralKey().equals(dutchCadastralKey)) {
                return type;
            }
        }

        TLogger.warning("Couldn't interpret key: " + dutchCadastralKey + ", passing default.");

        return OTHER;
    }

    public static final CadastralPurposeType[] getNY(String buildingClass) {

        List<CadastralPurposeType> possible = new ArrayList<>();
        if (!StringUtils.containsData(buildingClass)) {
            possible.add(CadastralPurposeType.OTHER);
        } else if (buildingClass.startsWith("A") || buildingClass.startsWith("B") || buildingClass.startsWith("C")
                || buildingClass.startsWith("D")) {
            possible.add(CadastralPurposeType.LIVING);
        } else if (buildingClass.startsWith("E") || buildingClass.startsWith("F") || buildingClass.startsWith("G")) {
            possible.add(CadastralPurposeType.INDUSTRIAL);
        } else if (buildingClass.startsWith("H")) {
            possible.add(CadastralPurposeType.LODGING);
        } else if (buildingClass.startsWith("I")) {
            possible.add(CadastralPurposeType.HEALTHCARE);
        } else if (buildingClass.startsWith("J")) {
            possible.add(CadastralPurposeType.GATHERING);
        } else if (buildingClass.startsWith("K")) {
            possible.add(CadastralPurposeType.SHOPPING);
        } else if (buildingClass.startsWith("L")) {
            possible.add(CadastralPurposeType.LIVING);
        } else if (buildingClass.startsWith("M") || buildingClass.startsWith("N")) {
            possible.add(CadastralPurposeType.GATHERING);
        } else if (buildingClass.startsWith("O")) {
            possible.add(CadastralPurposeType.OFFICE);
        } else if (buildingClass.startsWith("P")) {
            possible.add(CadastralPurposeType.GATHERING);
        } else if (buildingClass.startsWith("Q")) {
            possible.add(CadastralPurposeType.SPORTS);
        } else if (buildingClass.startsWith("R")) {
            possible.add(CadastralPurposeType.LIVING);
        } else if (buildingClass.startsWith("S")) {
            possible.add(CadastralPurposeType.MULTIPLE);
        } else if (buildingClass.startsWith("T") || buildingClass.startsWith("U")) {
            possible.add(CadastralPurposeType.OTHER);
        } else if (buildingClass.startsWith("V")) {
            /**
             * Vacant land, skip building
             *
             */
            return null;
        } else if (buildingClass.startsWith("W")) {
            possible.add(CadastralPurposeType.EDUCATIONAL);
        } else if (buildingClass.startsWith("Y") || buildingClass.startsWith("Z")) {
            possible.add(CadastralPurposeType.OTHER);
        } else {
            TLogger.severe("Couldn't interpret key: " + buildingClass + ", passing null.");
            return null;
        }

        return possible.toArray(new CadastralPurposeType[possible.size()]);
    }

    private List<Category> functionCategories = new ArrayList<Category>();

    private String dutchCadastralKey;
    private int matchingCertaintyScore;

    CadastralPurposeType(String dutchCadastralKey, int matchingCertaintyScore, Category... functionCategories) {
        this.dutchCadastralKey = dutchCadastralKey;
        this.matchingCertaintyScore = matchingCertaintyScore;
        addFunctionCategories(functionCategories);

    }

    private void addFunctionCategories(Category... functionCategories) {
        for (Category fc : functionCategories) {
            this.functionCategories.add(fc);
        }
    }

    public String getDutchCadastralKey() {
        return dutchCadastralKey;
    }

    public List<Category> getFunctionCategories() {
        return functionCategories;
    }

    public int getMatchingCertaintyScore() {
        return matchingCertaintyScore;
    }

}
