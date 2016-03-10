/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.engine.item.CustomIndicator.CustomIndicatorType;
import nl.tytech.util.StringUtils;

/**
 * Indicator that is calculated based on an Excelsheet running on the Server
 *
 * @author Maxim Knepfle
 */
public class ExcelIndicator extends Indicator {

    /**
     *
     */
    private static final long serialVersionUID = -3312131928668156364L;

    @XMLValue
    private String fileName = StringUtils.EMPTY;

    // runtime setting do not save to XML
    private boolean excelUpdated = true;

    public String getFileLocation() {
        return Setting.EXCEL_DIR + getFileName();
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public TypeInterface getType() {
        return CustomIndicatorType.EXCEL;
    }

    public boolean isExcelUpdated() {
        return excelUpdated;
    }

    public void setExcelUpdated(boolean excelUpdated) {
        this.excelUpdated = excelUpdated;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        this.setCalcTime(0);//reset calc time
        this.setExcelUpdated(true);
    }
}
