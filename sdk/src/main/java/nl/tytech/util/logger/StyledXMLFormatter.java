/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.util.logger;

import java.util.logging.Handler;
import java.util.logging.XMLFormatter;

/**
 * @author Jeroen Warmerdam
 */
public class StyledXMLFormatter extends XMLFormatter {

    private String xslFile;

    /**
	 * 
	 */
    public StyledXMLFormatter(String xslFile) {
        this.xslFile = xslFile;
    }

    /**
     * Return the header string for a set of XML formatted records, with style info attached
     * 
     * @param h The target handler (can be null)
     * @return a valid XML string
     */
    @Override
    public String getHead(Handler h) {

        String head = super.getHead(h);

        head = head.replace("<!DOCTYPE log SYSTEM \"logger.dtd\">\n", " ");
        int index = head.indexOf("<log>");

        String styleString = "<?xml-stylesheet type=\"text/xsl\" href=\"" + xslFile + "\"?>\n";
        head = head.substring(0, index);

        head += styleString + "<log>\n";

        return head;
    }

}
