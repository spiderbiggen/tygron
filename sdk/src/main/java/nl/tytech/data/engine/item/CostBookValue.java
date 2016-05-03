/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.engine.item.Building.Detail;
import nl.tytech.data.engine.item.ClientWord.ClientTerms;

/**
 * Book value that is negative (costing you something)
 *
 * @author Maxim Knepfle
 *
 */
public class CostBookValue extends BookValue {

    public enum Cost implements Type {

        /**
         * Default cost type when now state is relevant.
         */
        DEFAULT(ClientTerms.COST_DEFAULT),

        /**
         * Money that is reserved to construct something.
         */
        RESERVED_CONSTRUCTION(ClientTerms.COST_RESERVED_CONSTRUCTION, Detail.CONSTRUCTION_COST),

        /**
         * Money that is reserved to upgrade something.
         */
        RESERVED_UPGRADE(ClientTerms.COST_RESERVED_UPGRADE),

        /**
         * Costs to construct something
         */
        CONSTRUCTION(ClientTerms.COST_CONSTRUCTION, Detail.CONSTRUCTION_COST),

        /**
         * Money that is reserved to construct something.
         */
        RESERVED_DEMOLISH(ClientTerms.COST_RESERVED_DEMOLISH, Detail.DEMOLISH_COST),

        /**
         * Costs to demolish something
         */
        DEMOLISH(ClientTerms.COST_DEMOLISH, Detail.DEMOLISH_COST),

        /**
         * Maintenance costs
         *
         */
        MAINTENANCE(ClientTerms.COST_MAINTENANCE),

        /**
         * Upgrade cost
         */
        UPGRADE(ClientTerms.COST_UPGRADE),

        PRODUCTION(ClientTerms.PRODUCTION),

        PRODUCT_PURCHASES(ClientTerms.PRODUCT_PURCHASES),

        PRODUCTION_TAX(ClientTerms.PRODUCTION_TAX),

        EXCISE_TAX(ClientTerms.EXCISE_TAX),

        EXPORT_TAX(ClientTerms.EXPORT_TAX),

        IMPORT_TAX(ClientTerms.IMPORT_TAX),

        PRODUCT_TRANSPORTATION(ClientTerms.PRODUCT_TRANSPORTATION),

        BUY_OUT(ClientTerms.COST_BUYOUT);

        private ClientTerms term = null;

        private Detail buildingDetail = null;

        private Cost(ClientTerms term) {
            this.term = term;
        }

        private Cost(ClientTerms term, Detail buildingDetail) {

            this.buildingDetail = buildingDetail;
            this.term = term;
        }

        public Detail getDetail() {
            return buildingDetail;
        }

        @Override
        public ClientTerms getTranslationTerm() {
            return term;
        }
    }

    /**
     *
     */
    private static final long serialVersionUID = 946868301600695458L;

    @XMLValue
    private Cost costType = Cost.DEFAULT;

    public CostBookValue() {
    }

    public CostBookValue(final Stakeholder stakeholder, final MapLink mapLink, Integer linkID, Cost cost, final String name,
            final double value) {
        super(stakeholder, mapLink, linkID, name, value);
        this.costType = cost;
    }

    @Override
    public Cost getType() {
        return costType;
    }

    public void setType(Cost newType) {
        this.costType = newType;
    }
}
