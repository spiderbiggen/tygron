/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.editor.other;

import java.util.List;
import java.util.Map;
import nl.tytech.data.editor.serializable.OSMLayer;

/**
 *
 * @author Jurrian
 *
 */
public interface OSMGeoLink {

    public double getDefaultWidth();

    public Integer getID();

    public String getName();

    public Map<OSMLayer, List<String>> getSubTypes();

}
