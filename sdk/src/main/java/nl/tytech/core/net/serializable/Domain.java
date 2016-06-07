/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.net.serializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import nl.tytech.data.core.item.Item;
import nl.tytech.util.StringUtils;

/**
 * Domain contains a collection of Users that share projects an rights.
 * @author Maxim Knepfle
 *
 */
public class Domain implements Serializable {

    public enum Filter {

        ALL,

        EXCEPT_TYGRON,

        EDUCATIONAL,

        COMMERCIAL,

        EXPIRED,

        ACTIVE,

        TRIAL,
    }

    public static final int MINIMUM_NAME_LENGTH = 3;

    public static final int MAXIMUM_NAME_LENGTH = 50;

    private final static long DEFAULT_EXPIRE_DATE;

    static {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2024);
        DEFAULT_EXPIRE_DATE = cal.getTimeInMillis();
    }

    /**
     *
     */
    private static final long serialVersionUID = 945988661600523150L;

    /**
     * Special public domain, free access
     */
    public final static String PUBLIC = "public";

    /**
     * Special public domain, free access
     */
    public final static String TYGRON = "tygron";

    public static boolean isValidDomainName(String name) {
        return name.matches("[a-z0-9@._-]{" + MINIMUM_NAME_LENGTH + "," + MAXIMUM_NAME_LENGTH + "}");
    }

    private TLicense license = TLicense.PROJECTS10;

    private String name = StringUtils.EMPTY;

    private String contactEmail = StringUtils.EMPTY;

    private String licenseNumber = StringUtils.EMPTY;

    private String contactPhone = StringUtils.EMPTY;

    private String contactFirstname = StringUtils.EMPTY;

    private String contactLastname = StringUtils.EMPTY;

    private String organisation = StringUtils.EMPTY;

    private long expireDate = DEFAULT_EXPIRE_DATE;

    private int activationCode = Item.NONE;

    private long creationDate = 0;

    private boolean education = false;

    private Integer id;

    @Deprecated
    private ArrayList<SessionLog> log = new ArrayList<SessionLog>();

    public Domain() {

    }

    public int getActivationCode() {
        return activationCode;
    }

    public int getAllowedProjects() {
        return license.getAllowedProjects();
    }

    public String getContactEmail() {
        return this.contactEmail;
    }

    public String getContactFirstname() {
        return contactFirstname;
    }

    public String getContactLastname() {
        return contactLastname;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public long getExpireDate() {
        return expireDate;
    }

    public Integer getId() {
        return id;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    @Deprecated
    public List<SessionLog> getLog() {
        return log;
    }

    public String getName() {
        return name;
    }

    public String getOrganisation() {
        return organisation;
    }

    public TLicense getTLicense() {
        return license;
    }

    public boolean isEducation() {
        return education;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > this.getExpireDate();
    }

    public boolean isInfoComplete() {

        return StringUtils.containsData(this.contactEmail) && StringUtils.containsData(this.contactFirstname)
                && StringUtils.containsData(this.contactLastname) && StringUtils.containsData(this.contactPhone)
                && StringUtils.containsData(this.organisation) && StringUtils.containsData(this.licenseNumber);

    }

    public void setActivationCode(int activationCode) {
        this.activationCode = activationCode;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public void setContactFirstname(String contactFirstname) {
        this.contactFirstname = contactFirstname;
    }

    public void setContactLastname(String contactLastname) {
        this.contactLastname = contactLastname;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public void setEducation(boolean education) {
        this.education = education;
    }

    public void setExpireDate(long expireDate) {
        this.expireDate = expireDate;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    @Deprecated
    public void setLog(ArrayList<SessionLog> log) {
        this.log = log;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public void setTLicense(TLicense license) {
        this.license = license;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
