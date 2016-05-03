/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.net.serializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import nl.tytech.data.core.item.Item;
import nl.tytech.util.StringUtils;

/**
 * User definition class
 * @author Maxim Knepfle
 *
 */
public class User implements Serializable {

    public enum AccessLevel {

        JOIN_ONLY("User can only join or get invited to sessions hosted by others."),

        HOST_SESSION("User is allowed to host single or multi user sessions."),

        EDITOR("User is allowed to host or join a edit sessions."),

        DOMAIN_ADMIN("User can manage projects and other users in his/her domain."),

        SUPER_USER("Super user can also manage projects and users of other domains.");

        private String explaination;

        private AccessLevel(String explaination) {
            this.explaination = explaination;
        }

        public String getExplaination() {
            return explaination;
        }
    }

    private static final String GENERIC_STUDENT_NAME = "student@";

    public final static int NAME_MIN_LENGHT = 3;

    public final static int NAME_MAX_LENGHT = 50;

    public final static int PASSWD_MIN_LENGHT = 8;

    public final static int PASSWD_MAX_LENGHT = 20;

    public final static String PUBLIC_ACCOUNT = "@" + Domain.PUBLIC;

    /**
     *
     */
    private static final long serialVersionUID = -6386648876639626665L;

    /**
     * Fixed length of mac address: e.g. D4-3D-7E-00-F8-EC
     */
    private static final int MAC_ADDRESS_LENGTH = 17;



    public final static String DEFAULT_FIRST_NAME = "First Name";

    public final static String DEFAULT_LAST_NAME = "Family Name";

    /**
     * Checks if given account name is a "public" name.
     * @param userName
     * @return
     */
    public static boolean isPublicAccountName(String userName) {

        if (userName == null) {
            return false;
        }
        if (!userName.endsWith(PUBLIC_ACCOUNT)) {
            return false;
        }
        if (userName.length() != (PUBLIC_ACCOUNT.length() + MAC_ADDRESS_LENGTH)) {
            return false;
        }
        // passed all tests
        return true;
    }

    private String userName = StringUtils.EMPTY;

    private String nickName = StringUtils.EMPTY;

    private String phone = StringUtils.EMPTY;

    private long lastLogin = Item.NONE;

    private String domain = StringUtils.EMPTY;

    private String firstName = DEFAULT_FIRST_NAME;

    private String lastName = DEFAULT_LAST_NAME;

    private String maxOption = StringUtils.EMPTY;

    private ArrayList<String> macs = new ArrayList<>();

    private ArrayList<String> recentProjects = new ArrayList<>();

    private boolean active = true;

    public User() {

    }

    public User(String name, String nickName, String domain, AccessLevel maxOption) {
        this.userName = name;
        this.nickName = nickName;
        this.domain = domain;
        this.maxOption = maxOption.toString();
    }

    public String getDomain() {
        return domain;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getFullName() {
        return this.isPublicAccount() ? this.getFirstName() : this.getFirstName() + " " + this.getLastName();
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public String getLastName() {
        return lastName;
    }

    public List<String> getMacs() {
        return macs;
    }

    public AccessLevel getMaxAccessLevel() {
        return StringUtils.containsData(maxOption) ? AccessLevel.valueOf(maxOption) : null;
    }

    public String getMaxOption() {
        return maxOption;
    }

    public String getNickName() {
        return nickName;
    }

    public String getPhone() {
        return phone;
    }

    public ArrayList<String> getRecentProjects() {
        return recentProjects;
    }

    public String getUserName() {
        return userName;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isGenericStudent() {
        return this.userName.startsWith(GENERIC_STUDENT_NAME);
    }

    public boolean isInfoComplete() {

        if (!StringUtils.containsData(this.userName)) {
            return false;
        }
        if (!StringUtils.containsData(this.domain)) {
            return false;
        }
        if (!StringUtils.validValue(this.firstName, 1, 50, false, true) || DEFAULT_FIRST_NAME.equals(this.firstName.trim())) {
            return false;
        }
        if (!StringUtils.validValue(this.lastName, 1, 50, false, true) || DEFAULT_LAST_NAME.equals(this.lastName.trim())) {
            return false;
        }
        // student info is limited
        if (this.isGenericStudent()) {
            return true;
        }
        if (!StringUtils.containsData(this.phone) || !StringUtils.validPhone(this.phone)) {
            return false;
        }
        return true;
    }

    public boolean isPublicAccount() {
        return isPublicAccountName(this.userName);
    }

    public boolean isSuperUser() {
        return this.getMaxAccessLevel() == AccessLevel.SUPER_USER;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public void setLastName(String lastname) {
        this.lastName = lastname;
    }

    public void setMacs(ArrayList<String> macs) {
        this.macs = macs;
    }

    public void setMaxOption(String maxOption) {
        this.maxOption = maxOption;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setRecentProjects(ArrayList<String> recentProjects) {
        this.recentProjects = recentProjects;
    }

    public void setUserName(String name) {
        this.userName = name;
    }

    @Override
    public String toString() {
        return this.getUserName();
    }
}
