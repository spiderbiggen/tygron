/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.locale;

import java.util.Currency;
import java.util.Locale;
import nl.tytech.util.SkipObfuscation;

// Egyptian POUND
// Israeli SHEKEL
// South African RAND
// Turkish NEW TURKISH LIRA
// UAE DIRHAM
// Australian DOLLAR
// Chinese YUANRENMINBI
// Hong Kong DOLLAR
// Indian RUPEE
// Indonesian RUPIAH
// Japanese YEN
// Malaysian RINGGIT
// New Zealand DOLLAR
// Pakistani RUPEE
// Singapore DOLLAR
// South Korean WON
// Taiwanese DOLLAR
// Thai BAHT
// Argentinean PESO
// Brazilian REAL
// Canadian DOLLAR
// Chilean PESO
// Dominican PESO
// Mexican PESO
// British POUND
// Czech KORUNA
// Danish KRONE
// European EURO
// Hungarian FORINT
// Norwegian KRONE
// Polish ZLOTY
// Russian RUBLE
// Swedish KRONA
// Swiss FRANC
// BITCOIN

/**
 * @author Jurrian Hartveldt
 */
public enum TCurrency implements SkipObfuscation {

    DOLLAR_US(Locale.US),

    EURO(Locale.GERMANY),

    POUND_BRITISH(Locale.UK);

    public static final TCurrency[] VALUES = values();

    private String currencyCharacter;
    private String currencyName;

    TCurrency(Locale notationLocale) {
        Currency currency = Currency.getInstance(notationLocale);
        this.currencyName = currency.getDisplayName(notationLocale);
        this.currencyCharacter = currency.getSymbol(notationLocale);
    }

    public String getCurrencyCharacter() {
        return currencyCharacter;
    }

    public String getDisplayName() {
        return currencyName;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
