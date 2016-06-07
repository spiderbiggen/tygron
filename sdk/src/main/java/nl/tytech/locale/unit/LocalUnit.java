/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.locale.unit;

/**
 *
 * @author Frank Baars
 *
 */
public interface LocalUnit {

    public LocalUnit getDefault();

    public double getMaxValue();

    public String getPostFix();

    public double getRelativeSingleUnitValue();

    public LocalUnit[] getValues();

    public double toLocalValue(double amount);

    public double toSIValue(double amount);
}
