/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import nl.tytech.core.item.annotations.AssetDirectory;
import nl.tytech.core.item.annotations.XMLValue;

/**
 * Special overlay overlaying a given image
 *
 * @author Maxim Knepfle
 */
public class ImageOverlay extends Overlay {

    public static final String OVERLAY_IMAGES = "Overlays/";

    private static final String DEFAULT_IMAGE = "empty.png";

    /**
     *
     */
    private static final long serialVersionUID = 3137271350161025072L;

    @XMLValue
    @AssetDirectory(OVERLAY_IMAGES)
    private String imageName = DEFAULT_IMAGE;

    // local value used to check if asset was updated in editor
    private int imageVersion = 1;

    public String getImageLocation() {
        return OVERLAY_IMAGES + getImageName();
    }

    public String getImageName() {
        return imageName;
    }

    public int getImageVersion() {
        return imageVersion;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
        this.imageVersion++;
    }
}
