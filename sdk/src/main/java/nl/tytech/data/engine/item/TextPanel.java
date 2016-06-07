/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.util.StringUtils;

/**
 * @author Frank Baars
 */
public class TextPanel extends Panel {

    private static final long serialVersionUID = -526843113040439635L;

    @XMLValue
    private String text = StringUtils.EMPTY;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
