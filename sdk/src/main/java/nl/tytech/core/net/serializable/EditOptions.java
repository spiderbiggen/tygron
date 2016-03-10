/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.net.serializable;

import javafx.scene.paint.Color;

/**
 * Editor level
 * @author Maxim Knepfle
 *
 */
public enum EditOptions {

    /**
     * Simple interaction
     */
    GREEN("Basic", Color.GREEN, "Contains all basic functionality to create a project."),

    /**
     * Advanced Interaction
     */
    @Deprecated
    ORANGE("Advanced", Color.ORANGE, "Includes also the more advanced (undocumented) options."),

    /**
     * Advanced plus Heat
     */
    HEAT("Heat", Color.RED, "Includes heat functionality."),

    /**
     * RD experts only
     */
    RED("Expert", Color.RED, "Expert only.");

    private String name;
    private Color color;
    private String description;

    private EditOptions(String name, Color color, String description) {
        this.name = name;
        this.color = color;
        this.description = description;
    }

    public Color getColor() {
        return color;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

}
