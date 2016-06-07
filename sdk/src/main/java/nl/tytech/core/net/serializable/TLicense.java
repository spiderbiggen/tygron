/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.net.serializable;

/**
 * Domain license types
 * @author Maxim Knepfle
 *
 */
public enum TLicense {

    NOT_ACTIVATED_TRIAL(0, 0, 0),

    NONE(0, 0, 0),

    USE_ONLY(0, 0, 0),

    PROJECTS1(1, 0, 0),

    PROJECTS5(5, 0, 0),

    PROJECTS10(10, 0, 0),

    PROJECTS20(20, 0, 0),

    PROJECTS30(30, 0, 0),

    UNLIMITED(Integer.MAX_VALUE, 0, 0);

    private int allowedProjects, price, eduprice;

    private TLicense(int allowedProjects, int price, int eduprice) {
        this.allowedProjects = allowedProjects;
        this.price = price;
        this.eduprice = eduprice;
    }

    public int getAllowedProjects() {
        return allowedProjects;
    }

    public int getPrice(boolean edu) {
        return edu ? eduprice : price;
    }

    public String getPriceString(boolean edu) {

        int price = getPrice(edu);
        if (this == TLicense.UNLIMITED) {
            return "Please contact Tygron";
        }
        return price + " / year, (ex tax)";
    }

    @Override
    public String toString() {

        if (this == NONE) {
            return "Deactivated";
        } else if (this == UNLIMITED) {
            return "Unlimited";
        } else if (this == USE_ONLY) {
            return "Use only, no editing";
        } else if (this == PROJECTS1) {
            return allowedProjects + " Project";
        } else {
            return allowedProjects + " Projects";
        }
    }
}
