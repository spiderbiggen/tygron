/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.item.annotations.AssetDirectory;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.Item;
import nl.tytech.util.StringUtils;

/**
 * Video file playable in viewport
 *
 * @author Maxim Knepfle
 *
 */
public class Video extends Item {

    public enum Type {

        CONTENT, INTRO;
    }

    /**
     *
     */
    private static final long serialVersionUID = -6754893695766076518L;

    private static final String VIDEO_DIR = "Videos/";

    @XMLValue
    private String name = StringUtils.EMPTY;

    @XMLValue
    private Type type = Type.CONTENT;

    @XMLValue
    @AssetDirectory(VIDEO_DIR)
    private String fileName = StringUtils.EMPTY;

    @XMLValue
    @ItemIDField("EVENT_BUNDLES")
    private ArrayList<Integer> eventBundleIDs = new ArrayList<>();

    public List<Integer> getEventBundleIDs() {
        return eventBundleIDs;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public String getVideoLocation() {
        if (!StringUtils.containsData(fileName)) {
            return StringUtils.EMPTY;
        }
        return VIDEO_DIR + fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return getName();
    }
}
