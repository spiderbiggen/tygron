/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.item.annotations.AssetDirectory;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.UniqueNamedItem;

/**
 * Contributor
 * <p>
 * Party that made a contribution to developing this case.
 * </p>
 * @author Maxim Knepfle
 */
public class Contributor extends UniqueNamedItem {

    public static final String GUI_IMAGES_CONTRIBUTORS = "Gui/Images/Contributors/";
    public static final String DEFAULT_IMAGE = "noicon.png";

    private static final long serialVersionUID = 6950084912593726229L;

    @AssetDirectory(GUI_IMAGES_CONTRIBUTORS)
    @XMLValue
    private String iconFileName = DEFAULT_IMAGE;

    // local value used to check if asset was updated in editor
    private int iconVersion = 1;

    @XMLValue
    @ListOfClass(String.class)
    private ArrayList<String> persons = new ArrayList<String>();

    public int getIconVersion() {
        return iconVersion;
    }

    public String getImageFileName() {
        return iconFileName;
    }

    public String getImageLocation() {
        return GUI_IMAGES_CONTRIBUTORS + iconFileName;
    }

    public List<String> getPersons() {
        return persons;
    }

    public void setIconFileName(String iconFileName) {
        this.iconFileName = iconFileName;
        this.iconVersion++;
    }
}
