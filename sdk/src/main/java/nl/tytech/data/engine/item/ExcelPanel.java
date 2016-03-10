/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import nl.tytech.core.item.annotations.DoNotSaveToInit;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.util.StringUtils;

/**
 * @author Frank Baars
 */
public class ExcelPanel extends Panel {

    private static final long serialVersionUID = 2805451253961720700L;

    @XMLValue
    private String fileName = StringUtils.EMPTY;

    // runtime setting do not save to XML
    private boolean excelUpdated = true;

    @XMLValue
    @DoNotSaveToInit
    private String panelCode = StringUtils.EMPTY;

    @XMLValue
    @DoNotSaveToInit
    private long calcTime = 0;

    public long getCalcTime() {
        return calcTime;
    }

    public String getFileLocation() {
        return Setting.EXCEL_DIR + getFileName();
    }

    public String getFileName() {
        return fileName;
    }

    public String getPanelCode() {
        return panelCode;
    }

    public boolean isExcelUpdated() {
        return excelUpdated;
    }

    public void setCalcTime(long calcTime) {
        this.calcTime = calcTime;
    }

    public void setExcelUpdated(boolean excelUpdated) {
        this.excelUpdated = excelUpdated;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        this.setExcelUpdated(true);
    }

    public void setPanelCode(String panelCode) {
        this.panelCode = panelCode;
    }
}
