/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import nl.tytech.core.item.annotations.AssetDirectory;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.Item;
import nl.tytech.util.StringUtils;

/**
 *
 * @author Jeroen Warmerdam
 *
 */
public class Sound extends Item {

    /**
     *
     */
    private static final long serialVersionUID = -6754893695766076516L;
    public static final String SOUND_DIR = "Sounds/";
    public static final String DEFAULT = "beep.ogg";

    @XMLValue
    private String soundName = StringUtils.EMPTY;

    @XMLValue
    @AssetDirectory(SOUND_DIR)
    private String soundFilename = DEFAULT;

    @XMLValue
    private double soundVolume = 1.0; // LOUD!!

    @XMLValue
    private boolean loopIt = false;

    @XMLValue
    private boolean background = false;

    public boolean getLoopIt() {
        return loopIt;
    }

    public String getSoundFilename() {
        if (!StringUtils.containsData(soundFilename)) {
            return StringUtils.EMPTY;
        }
        return SOUND_DIR + soundFilename;
    }

    public String getSoundName() {

        return soundName;
    }

    public double getSoundVolume() {
        return soundVolume;
    }

    public boolean isBackgroundSound() {
        return background;
    }

    public void setIsBackgroundSound(boolean isBackgroundSound) {
        this.background = isBackgroundSound;
    }

    public void setLoopIt(boolean loopIt) {
        this.loopIt = loopIt;
    }

    public void setSoundFilename(String name) {

        this.soundFilename = name;
    }

    public void setSoundName(String name) {
        this.soundName = name;
    }

    public void setSoundVolume(double volume) {
        this.soundVolume = volume;
    }

    @Override
    public String toString() {
        return soundName;
    }
}
