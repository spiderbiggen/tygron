/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.other;

/**
 * Excel Item
 * <p>
 * Excel Item is an interface for items that have excel sheets for session calculations.
 * </p>
 * @author Frank Baars
 */
public interface ExcelItem {

    public String getFileName();

    public String getFileSubDirectory();

    public Integer getID();

    public boolean isDefaultExcel();

    public boolean isExcelUpdated();

    public void setExcelUpdated(boolean excelUpdated);

    public void setFileName(String fileName);
}
