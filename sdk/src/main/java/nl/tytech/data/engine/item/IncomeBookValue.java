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
 * BookValue that is positive (generates income)
 * @author Maxim Knepfle
 *
 */
public class IncomeBookValue extends BookValue {

    public enum Income implements Type {

        /**
         * Default cost type when now state is relevant.
         */
        DEFAULT(ClientTerms.INCOME_DEFAULT),

        /**
         * Budget at start of session
         */
        START_BUDGET(ClientTerms.BUDGET),

        /**
         * Extra budget given at start of level.
         */
        LEVEL_START_BUDGET_INCREASE(ClientTerms.BUDGET),

        /**
         * Money given each year.
         */
        ANNUAL_BUDGET_INCREASE(ClientTerms.BUDGET),

        /**
         * Money that recived by selling a building.
         */
        BUILDING_SALE(ClientTerms.INCOME_SALES, Detail.SELL_PRICE),

        CONTRIBUTION(ClientTerms.COST_CONTRIBUTION),

        PRODUCTION(ClientTerms.PRODUCTION),

        PRODUCT_SALES(ClientTerms.PRODUCT_SALES),

        PRODUCTION_TAX(ClientTerms.PRODUCTION_TAX),

        EXCISE_TAX(ClientTerms.EXCISE_TAX),

        EXPORT_TAX(ClientTerms.EXPORT_TAX),

        IMPORT_TAX(ClientTerms.IMPORT_TAX),

        PRODUCT_TRANSPORTATION(ClientTerms.PRODUCT_TRANSPORTATION);

        private ClientTerms term;
        private Detail buildingDetail;

        private Income(ClientTerms term) {
            this(term, null);
        }

        private Income(ClientTerms term, Detail buildingDetail) {
            this.term = term;
            this.buildingDetail = buildingDetail;
        }

        public Detail getDetail() {
            return this.buildingDetail;
        }

        @Override
        public ClientTerms getTranslationTerm() {
            return term;
        }
    }

    /**
     *
     */
    private static final long serialVersionUID = -1096671485716525649L;

    @XMLValue
    private Income incomeType = Income.DEFAULT;

    public IncomeBookValue() {

    }

    public IncomeBookValue(final Stakeholder stakeholder, final MapLink mapLink, Integer linkID, Income incomeType, final String name,
            final double value) {
        super(stakeholder, mapLink, linkID, name, value);
        this.incomeType = incomeType;
    }

    @Override
    public Income getType() {
        return incomeType;
    }
}
