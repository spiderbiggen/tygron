/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.locale;

/**
 * Our supported languages
 * @author Maxim Knepfle
 *
 */
public enum TLanguage {

    EN("English (US)"),

    NL("Dutch");

    private String exp;

    private TLanguage(String exp) {
        this.exp = exp;
    }

    public String toFullString() {
        return exp;
    }
}
