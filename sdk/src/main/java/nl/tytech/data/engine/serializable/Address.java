/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.serializable;

import java.io.Serializable;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.util.StringUtils;
import com.vividsolutions.jts.geom.Point;

/**
 *
 * @author Frank Baars
 *
 */
public class Address implements Serializable {

    public enum AddressParameter {
        ZIP_CODE, STREET, NUMBER, LETTER, ADDITION
    }

    /**
     *
     */
    private static final long serialVersionUID = -3060395347540498879L;

    @XMLValue
    private String zipCode = StringUtils.EMPTY;

    @XMLValue
    private String street = StringUtils.EMPTY;

    @XMLValue
    private Integer number = null;

    @XMLValue
    private String letter = StringUtils.EMPTY;

    @XMLValue
    private String addition = StringUtils.EMPTY;

    @XMLValue
    private Point point = null;

    @XMLValue
    private double surfaceSize = 0;

    private String addressCode = null;

    public Address() {

    }

    public Address(Point point, double surfaceSize, String zipCode, String street, Integer number, String letter, String addition) {
        this.point = point;
        this.zipCode = zipCode == null ? StringUtils.EMPTY : zipCode;
        this.street = street == null ? StringUtils.EMPTY : street;
        this.number = number == null ? null : number;
        this.letter = letter == null ? StringUtils.EMPTY : letter;
        this.addition = addition == null ? StringUtils.EMPTY : addition;
        this.surfaceSize = surfaceSize;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Address) {
            Address o = (Address) other;
            // return zipCode.equals(o.zipCode) && street.equals(o.street) && number.equals(o.number) && letter.equals(o.letter)
            // && addition.equals(o.addition);
            return getAddressCode().equals(o.getAddressCode());
        }
        return false;
    }

    public String getAddition() {
        return addition;
    }

    public String getAddressCode() {
        if (addressCode == null) {
            addressCode = zipCode + StringUtils.WHITESPACE + street + StringUtils.WHITESPACE + number + StringUtils.WHITESPACE + letter
                    + StringUtils.WHITESPACE + addition;
        }
        return addressCode;
    }

    public String getLetter() {
        return letter;
    }

    public Integer getNumber() {
        return number;
    }

    public Point getPoint() {
        return point;
    }

    public String getStreet() {
        return street;
    }

    public double getSurfaceSizeM2() {
        return surfaceSize;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setAddition(String newValue) {
        this.addition = newValue;
        addressCode = null;
    }

    public void setLetter(String newValue) {
        this.letter = newValue;
        addressCode = null;
    }

    public void setNumber(Integer newValue) {
        this.number = newValue;
        addressCode = null;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public void setStreet(String newValue) {
        this.street = newValue;
        addressCode = null;
    }

    public void setSurfaceSize(double surfaceSize) {
        this.surfaceSize = surfaceSize;
    }

    public void setZipCode(String newValue) {
        this.zipCode = newValue;
        addressCode = null;
    }

    @Override
    public String toString() {
        return getAddressCode();
    }
}
