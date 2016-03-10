/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.UniqueNamedItem;

/**
 * Defines a Pipe's cost, diameter, etc.
 * @author Maxim Knepfle
 *
 */
public class PipeDefinition extends UniqueNamedItem {

    /**
     *
     */
    private static final long serialVersionUID = -6091749457747835078L;

    public static final float HEAT_PIPE_M_MULTIPLIER = 10;

    @XMLValue
    private double priceM = 1;

    @XMLValue
    private double diameterM = 1;

    public double getDiameterM() {
        return diameterM;
    }

    public double getPriceM() {
        return priceM;
    }

    public void setDiameterM(double diameterM) {
        this.diameterM = diameterM;
    }

    public void setPriceM(double priceM) {
        this.priceM = priceM;
    }

    // DN800(6042, 2367640, 16197 * 35, 4.07f),
    //
    // DN700(4849, 1598235, 12017 * 35, 3.74f),
    //
    // DN600(4019, 1078860, 8621 * 35, 3.40f),
    //
    // DN500(3262, 728255, 6531 * 35, 3.12f),
    //
    // DN400(2599, 491604, 5747 * 35, 2.84f),
    //
    // DN300(1894, 331860, 2873 * 35, 2.56f),
    //
    // DN250(1576, 224027, 2090 * 35, 2.31f),
    //
    // DN200(1294, 151224, 1462 * 35, 2.08f),
    //
    // DN150(1004, 102089, 900 * 35, 1.84f),
    //
    // DN125(849, 68890, 677 * 35, 1.65f),
    //
    // DN100(706, 46516, 408 * 35, 1.46f),
    //
    // DN80(590, 31400, 221 * 35, 1.32f),
    //
    // DN65(504, 21207, 139 * 35, 1.18f),
    //
    // DN50(417, 14296, 74 * 35, 1.04f),
    //
    // DN40(358, 9657, 41 * 35, 0.89f),
    //
    // DN32(309, 6532, 27 * 35, 0.80f);

}
