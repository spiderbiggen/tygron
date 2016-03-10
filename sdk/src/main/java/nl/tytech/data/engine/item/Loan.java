/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import nl.tytech.core.item.annotations.DoNotSaveToInit;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.item.Moment;
import nl.tytech.locale.TCurrency;
import nl.tytech.locale.unit.UnitSystem;
import nl.tytech.locale.unit.UnitSystemType;
import nl.tytech.locale.unit.UnitType;
import nl.tytech.util.StringUtils;

/**
 * Loan
 * <p>
 * Bank Loan
 * </p>
 * @author Maxim Knepfle
 */
public class Loan extends Item {

    public enum LoanState {
        PRO_FORMA, ACTIVE, PAYED;
    }

    private static final long serialVersionUID = 6950084912593726229L;

    /**
     * Calculate total loan cost (amount + intrest) based on monthly equal payback.
     * @param initialAmount
     * @param yearlyIntrest
     * @param years
     * @return
     */
    private static double calculateIntialAmountPlusIntrest(double initialAmount, double yearlyIntrest, double years) {

        double montlyIntrest = Math.pow((1d + yearlyIntrest), (1d / 12d)) - 1d;
        double montlyCost = initialAmount * montlyIntrest / (1d - Math.pow((1d + montlyIntrest), -years * 12d));
        return montlyCost * years * 12d;
    }

    @XMLValue
    private String name = StringUtils.EMPTY;

    @XMLValue
    private double initialAmount = 0;

    @DoNotSaveToInit
    @XMLValue
    private Long startDate = null;

    @XMLValue
    private int years = 0;

    @XMLValue
    private Integer stakeholderID = Item.NONE;

    @XMLValue
    private double interest = 0;

    public Loan() {

    }

    public Loan(String name, Integer stakeholderID, double amount, double intrest, int years) {
        this.name = name;
        this.stakeholderID = stakeholderID;
        this.initialAmount = amount;
        this.interest = intrest;
        this.years = years;
    }

    public double getAnnualPayback() {

        double value = getInitialAmountPlusIntrest();
        return value / years;
    }

    public Long getFinishDate() {

        if (startDate == null) {
            return null;
        }
        return startDate + Moment.YEAR * years;
    }

    public double getInitialAmount() {
        return initialAmount;
    }

    public double getInitialAmountPlusIntrest() {
        return calculateIntialAmountPlusIntrest(initialAmount, interest, years);
    }

    public double getInterest() {
        return interest;
    }

    public String getPaybackDescription() {
        long today = this.getLord().getSimTimeMillis();
        long paybackID = years - ((this.getFinishDate() - today) / Moment.YEAR);
        return this.toString() + " " + paybackID + "/" + years;
    }

    public double getRemainingAmountPlusIntrest() {
        if (startDate == null) {
            return getInitialAmountPlusIntrest();
        }

        long milis = this.getLord().getSimTimeMillis() - startDate;
        double years = milis / Moment.YEAR;
        double payback = this.getAnnualPayback() * years;
        return getInitialAmountPlusIntrest() - payback;
    }

    public Stakeholder getStakeholder() {
        return this.getItem(MapLink.STAKEHOLDERS, this.stakeholderID);
    }

    public Integer getStakeholderID() {
        return stakeholderID;
    }

    public Long getStartDate() {
        return startDate;
    }

    public LoanState getState() {
        if (startDate == null) {
            return LoanState.PRO_FORMA;
        }
        if (getRemainingAmountPlusIntrest() <= 0) {
            return LoanState.PAYED;
        }
        return LoanState.ACTIVE;
    }

    public int getYears() {
        return years;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    @Override
    public String toString() {

        Setting setting = this.getItem(MapLink.SETTINGS, Setting.Type.CURRENCY);
        TCurrency activeCurrency = setting.getEnumValue(TCurrency.class);
        Setting setting2 = this.getItem(MapLink.SETTINGS, Setting.Type.MEASUREMENT_SYSTEM_TYPE);
        UnitSystem unitSystem = setting2.getEnumValue(UnitSystemType.class).getImpl();
        String loanAmount = StringUtils.toMoney(this.getInitialAmount(), unitSystem, activeCurrency);
        String interest = unitSystem.toLocalValueWithUnit(getInterest() * 100f, UnitType.PERCENTAGE);
        return name + " " + (getID() + 1) + ": " + loanAmount + " @ " + interest;
    }
}
