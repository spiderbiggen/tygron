/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.net.serializable;

/**
 *
 * @author Maxim Knepfle
 *
 */
public interface SettingType {

    public String getDefaultValue();

    public Class<?> getValueType();

    public int ordinal();

}
