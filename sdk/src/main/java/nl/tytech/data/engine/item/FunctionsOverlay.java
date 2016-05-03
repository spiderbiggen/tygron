/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.util.color.TColor;

/**
 * Special overlay that can highlight functions
 *
 * @author Maxim Knepfle
 */
public class FunctionsOverlay extends Overlay {

    /**
     *
     */
    private static final long serialVersionUID = 3137271350161025071L;

    @XMLValue
    @ItemIDField("FUNCTIONS")
    private ArrayList<Integer> functions = new ArrayList<>();

    @XMLValue
    private TColor functionsColor = TColor.RED;

    @XMLValue
    private TColor restColor = TColor.WHITE;

    public List<Integer> getFunctionIDs() {
        return functions;
    }

    public List<Function> getFunctions() {
        return this.getItems(MapLink.FUNCTIONS, getFunctionIDs());
    }

    public TColor getFunctionsColor() {
        return functionsColor;
    }

    public TColor getRestColor() {
        return restColor;
    }

    public void setFunctionsColor(TColor functionsColor) {
        this.functionsColor = functionsColor;
    }

    public void setRestColor(TColor restColor) {
        this.restColor = restColor;
    }

}
