/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.serializable;

import nl.tytech.data.engine.item.ClientWord.ClientTerms;

/**
 *
 * @author Frank Baars
 *
 */
public enum CalculationSpaceType {

    SURFACE_SPACE("Ground surface area", ClientTerms.PER_SURFACE_SPACE),

    FLOOR_SPACE("Floor space", ClientTerms.PER_FLOOR_SPACE);

    private String name;
    private ClientTerms clientTerm;

    private CalculationSpaceType(String name, ClientTerms clientTerm) {
        this.name = name;
        this.clientTerm = clientTerm;
    }

    public ClientTerms getClientTerm() {
        return clientTerm;
    }

    @Override
    public String toString() {
        return name;
    }
}
