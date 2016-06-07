/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.editor.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.editor.other.OSMGeoLink;
import nl.tytech.data.editor.serializable.OSMLayer;
import nl.tytech.util.StringUtils;

/**
 * @author Jurrian Hartveldt
 */
public class OSMBehaviorGeoLink extends BehaviorGeoLink implements OSMGeoLink {

    private static final long serialVersionUID = -3323471193971434223L;

    @XMLValue
    private HashMap<OSMLayer, List<String>> subTypes = new HashMap<>();

    @Override
    public double getDefaultWidth() {
        return 10d;
    }

    @Override
    public String getDescription() {
        return "OSM-" + super.getDescription();
    }

    @Override
    public Map<OSMLayer, List<String>> getSubTypes() {
        return subTypes;
    }

    public void setSubTypes(Map<OSMLayer, List<String>> subTypes) {
        this.subTypes = new HashMap<>(subTypes);
    }

    @Override
    public String toString() {
        return StringUtils.EMPTY + this.getPriority() + "-OSM) " + getName();
    }
}
