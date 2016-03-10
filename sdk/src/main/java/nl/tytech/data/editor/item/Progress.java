/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.editor.item;

import nl.tytech.core.item.annotations.AssetDirectory;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.item.ActionMenu;
import nl.tytech.util.StringUtils;

/**
 * Shows GEO progress on loading a new map in the Wizard
 * @author Maxim Knepfle
 *
 */
public class Progress extends Item {

    private static final long serialVersionUID = -4710120365118092729L;

    private final static String BASE_FAIL = "Unable to contact data source: ";

    private String name = StringUtils.EMPTY;

    private String data = StringUtils.EMPTY;

    @AssetDirectory(ActionMenu.GUI_IMAGES_GUICATEGORY_ICONS)
    private String iconName = "category_house.png";

    private boolean failed = false;

    private double progress = 0;

    public Progress() {

    }

    public Progress(String name, String data, String icon) {
        this.name = name;
        this.data = data;
        this.iconName = icon;
    }

    @Override
    public String getDescription() {

        if (this.isFailed()) {
            if (StringUtils.containsData(data)) {
                return BASE_FAIL + data;
            } else {
                return name + "failed";
            }
        }

        if (StringUtils.containsData(data)) {
            return name + " (source: " + data + ")";
        } else {
            return name;
        }
    }

    public String getIconLocation() {
        if (iconName == null) {
            return null;
        }
        return ActionMenu.GUI_IMAGES_GUICATEGORY_ICONS + iconName;
    }

    public double getProgress() {
        return progress;
    }

    public boolean isFailed() {
        return failed;
    }

    public boolean isFinished() {
        return progress >= 1d;
    }

    public boolean isStarted() {
        return progress > 0;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    public void setProgress(double newProgress) {
        this.progress = newProgress;
    }

    @Override
    public String toString() {
        return name + " " + StringUtils.toPercentage(this.getProgress());
    }
}
