/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.engine.item.CustomIndicator.CustomIndicatorType;
import nl.tytech.data.engine.other.ExcelItem;
import nl.tytech.util.StringUtils;

/**
 * Indicator that is calculated based on an Excelsheet running on the Server
 *
 * @author Maxim Knepfle
 */
public class ExcelIndicator extends Indicator implements ExcelItem {

    /**
     *
     */
    private static final long serialVersionUID = -3312131928668156364L;

    @XMLValue
    protected String fileName = StringUtils.EMPTY;

    // runtime setting do not save to XML
    private boolean excelUpdated = true;

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String getFileSubDirectory() {
        return StringUtils.capitalizeFirstLetter(MapLink.INDICATORS.name().toLowerCase()) + "/" + getID() + "/";
    }

    @Override
    public TypeInterface getType() {
        return CustomIndicatorType.EXCEL;
    }

    @Override
    public boolean isDefaultExcel() {
        return false;
    }

    @Override
    public boolean isExcelUpdated() {
        return excelUpdated;
    }

    @Override
    public void setExcelUpdated(boolean excelUpdated) {
        this.excelUpdated = excelUpdated;
    }

    @Override
    public void setFileName(String fileName) {
        this.fileName = fileName;
        this.setCalcTime(0);// reset calc time
        this.setExcelUpdated(true);
    }
}
