/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.serializable;

import java.util.Calendar;

/**
 * Period in time for Building Styles.
 *
 * @author Maxim Knepfle
 *
 */
public enum ConstructionPeriod {

    ANCIENT(Integer.MIN_VALUE),

    CLASSIC(1500),

    PRE_WAR(1880),

    POST_WAR(1945),

    CONTEMPORARY(1970),

    FUTURISTIC(Calendar.getInstance().get(Calendar.YEAR));

    private static final int UNKNOWN_YEAR = 9999;

    public static final ConstructionPeriod[] VALUES = values();

    public static final ConstructionPeriod get(int constructionYear) {

        ConstructionPeriod selectedPeriod = ANCIENT;
        for (ConstructionPeriod period : ConstructionPeriod.VALUES) {
            if (period.getStartOfPeriod() > constructionYear) {
                return selectedPeriod;
            }

            selectedPeriod = period;
        }

        if (UNKNOWN_YEAR == constructionYear) {
            return CONTEMPORARY;
        }

        return FUTURISTIC;
    }

    private int startOfPeriod;

    private ConstructionPeriod(int startOfPeriod) {
        this.startOfPeriod = startOfPeriod;
    }

    public int getStartOfPeriod() {
        return startOfPeriod;
    }
}
