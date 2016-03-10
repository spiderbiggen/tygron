/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import nl.tytech.data.engine.item.CustomIndicator.CustomIndicatorType;

/**
 * Excel indicator extended with HEAT functionality
 * @author Frank Baars
 *
 */
public class HeatExcelIndicator extends ExcelIndicator {

    /**
     *
     */
    private static final long serialVersionUID = -1029166600179635602L;

    @Override
    public TypeInterface getType() {
        return CustomIndicatorType.HEAT_EXCEL;
    }

}
