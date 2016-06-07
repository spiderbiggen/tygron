/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.engine.serializable.CustomColorArea;
import nl.tytech.data.engine.serializable.LegendEntry;
import nl.tytech.util.color.TColor;
import nl.tytech.util.logger.TLogger;

/**
 *
 * @author Frank Baars
 *
 */
public class CustomColorOverlay extends Overlay {

    /**
     *
     */
    private static final long serialVersionUID = 4660123660508080106L;

    @XMLValue
    @ListOfClass(CustomColorArea.class)
    private ArrayList<CustomColorArea> customColorAreas = new ArrayList<CustomColorArea>();

    public void addCustomColorArea(String name, TColor color) {
        int maxID = 0;
        for (CustomColorArea customColorArea : customColorAreas) {
            maxID = Math.max(maxID, customColorArea.getID());
        }

        if (maxID >= Byte.MAX_VALUE - 1) {
            // TODO: (Frank) Time to compact!
            TLogger.warning("ColorAreas list ran out of ids and is full for now!!");
        } else {
            Integer customColorAreaID = maxID + 1;
            CustomColorArea customColorArea = new CustomColorArea(customColorAreaID, name, color);
            this.customColorAreas.add(customColorArea);
            addCustomColorAreaLegendEntry(customColorArea);

        }
    }

    private void addCustomColorAreaLegendEntry(CustomColorArea customColorArea) {
        List<LegendEntry> entries = this.getLegend();
        int index = getCustomColorAreaIndex(customColorArea.getID());
        entries.add(index, new LegendEntry(customColorArea.getName(), customColorArea.getColor()));
    }

    public CustomColorArea getCustomColorArea(Integer customColorAreaID) {
        for (CustomColorArea colorArea : customColorAreas) {
            if (colorArea.getID().equals(customColorAreaID)) {
                return colorArea;
            }
        }
        return null;
    }

    private int getCustomColorAreaIndex(Integer customColorAreaID) {
        int index = 0;
        for (CustomColorArea customColorArea : customColorAreas) {
            if (customColorArea.getID().equals(customColorAreaID)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    public List<CustomColorArea> getCustomColorAreas() {
        return customColorAreas;
    }

    private void refreshLegendEntry(CustomColorArea customColorArea) {
        Integer customColorAreaID = customColorArea.getID();
        int index = getCustomColorAreaIndex(customColorAreaID);
        if (index == -1) {
            TLogger.warning(CustomColorArea.class.getSimpleName() + "(id=" + customColorAreaID
                    + ") is not in the list anymore! Cannot update its legend entry!");
            return;
        }

        removeCustomColorAreaLegendEntry(customColorArea);
        addCustomColorAreaLegendEntry(customColorArea);
    }

    public void removeCustomColorArea(Integer customColorAreaID) {
        CustomColorArea customColorArea = getCustomColorArea(customColorAreaID);
        if (customColorArea == null) {
            TLogger.warning(
                    "customColorAreaOverlay does not contain " + CustomColorArea.class.getSimpleName() + " with id" + customColorAreaID);
            return;
        }

        removeCustomColorAreaLegendEntry(customColorArea);
        customColorAreas.remove(customColorArea);

    }

    private void removeCustomColorAreaLegendEntry(CustomColorArea customColorArea) {
        int index = getCustomColorAreaIndex(customColorArea.getID());
        List<LegendEntry> entries = this.getLegend();
        entries.remove(index);
    }

    public void setCustomColorAreaColor(Integer customColorAreaID, TColor customColorAreaColor) {
        CustomColorArea customColorArea = getCustomColorArea(customColorAreaID);
        if (customColorArea == null) {
            TLogger.warning(
                    "customColorAreaOverlay does not contain " + CustomColorArea.class.getSimpleName() + " with id" + customColorAreaID);
            return;
        }

        customColorArea.setColor(customColorAreaColor);
        refreshLegendEntry(customColorArea);
    }

    public void setCustomColorAreaName(Integer customColorAreaID, String name) {
        CustomColorArea customColorArea = getCustomColorArea(customColorAreaID);
        if (customColorArea == null) {
            TLogger.warning(
                    "customColorAreaOverlay does not contain " + CustomColorArea.class.getSimpleName() + " with id" + customColorAreaID);
            return;
        }
        customColorArea.setName(name);
        refreshLegendEntry(customColorArea);

    }
}
