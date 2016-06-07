/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.net.event;

import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.core.net.event.RemoteServicesEvent.ServiceEventType;
import nl.tytech.core.net.serializable.Domain;
import nl.tytech.core.net.serializable.Domain.Filter;
import nl.tytech.core.net.serializable.Hardware;
import nl.tytech.core.net.serializable.SessionLog;
import nl.tytech.core.net.serializable.User;
import nl.tytech.core.net.serializable.User.AccessLevel;

/**
 * Events related to online user management.
 * @author Maxim Knepfle
 *
 */
public enum UserServiceEventType implements ServiceEventType {

    GET_MY_DOMAIN(Domain.class, AccessLevel.JOIN_ONLY),

    GET_DOMAIN_NAMES(String[].class, AccessLevel.SUPER_USER, Filter.class),

    GET_USER_NAMES(String[].class, AccessLevel.SUPER_USER, Filter.class),

    GET_DOMAINS(Domain[].class, AccessLevel.SUPER_USER, Filter.class),

    DELETE_USER(Boolean.class, AccessLevel.DOMAIN_ADMIN, String.class),

    GET_MY_USER(User.class, AccessLevel.JOIN_ONLY),

    GET_MY_HASH_KEY(String.class, AccessLevel.JOIN_ONLY),

    GET_DOMAIN_USERS(User[].class, AccessLevel.DOMAIN_ADMIN, String.class),

    GET_DOMAIN_USER_NAMES(String[].class, AccessLevel.HOST_SESSION, String.class),

    /**
     * Update user of given ID: FirstName, LastName, phone and access level.
     */
    @EventParamData(desc = "Update the values for the given user.", params = { "Domain name", "User name/email", "Nick name", "First name",
            "Family name", "Phone number", "User Access Level: e.g. HOST_SESSION, DOMAIN_ADMIN" })
    UPDATE_USER_DATA(Boolean.class, AccessLevel.HOST_SESSION, String.class, String.class, String.class, String.class, String.class,
            String.class, AccessLevel.class),

    /**
     * Create new user with given username
     */
    NEW_USER(Boolean.class, AccessLevel.DOMAIN_ADMIN, String.class, String.class),

    /**
     * Create new domain with given name
     */
    NEW_DOMAIN(Boolean.class, AccessLevel.SUPER_USER, String.class),

    /**
     * Change password and return the new hashkey
     */
    CHANGE_USER_PASSWD(String.class, AccessLevel.JOIN_ONLY, String.class, String.class),

    /**
     * Get given domain
     */
    GET_DOMAIN(Domain.class, AccessLevel.SUPER_USER, String.class),

    /**
     * Get given domain session logs since millis
     */
    GET_SESSION_LOGS(SessionLog[].class, AccessLevel.SUPER_USER, String.class, Long.class),

    /**
     * Get given domain session logs
     */
    GET_USER_HARDWARE(Hardware[].class, AccessLevel.SUPER_USER, String.class),

    /**
     * Get given session log
     */
    GET_SESSION_LOG(SessionLog.class, AccessLevel.SUPER_USER, String.class),

    /**
     * Set true if domain is educational institute
     */
    SET_DOMAIN_EDUCATION(AccessLevel.SUPER_USER, String.class, Boolean.class),

    /**
     * Set true if user is active
     */
    SET_USER_ACTIVE(AccessLevel.DOMAIN_ADMIN, String.class, String.class, Boolean.class),

    /**
     * Create trial account
     */
    CREATE_TRIAL_ACCOUNT(Boolean.class, AccessLevel.JOIN_ONLY, String.class, String.class, String.class, String.class, String.class,
            String.class, String.class),

    /**
     * Change expire date of given domain.
     */
    SET_DOMAIN_EXPIRE_DATE(AccessLevel.SUPER_USER, String.class, Long.class),

    /**
     * Send user hardware info for better support.
     */
    SEND_HARDWARE_INFO(Boolean.class, AccessLevel.JOIN_ONLY, String.class, String.class, String.class, String.class, String.class),

    /**
     * Update grouo contact info
     */
    SET_DOMAIN_CONTACT_INFO(AccessLevel.SUPER_USER, String.class, String.class, String.class, String.class, String.class, String.class,
            String.class, String.class),

    /**
     * Activate trial account with emailed code;
     */
    ACTIVATE_TRIAL_ACCOUNT(Boolean.class, AccessLevel.JOIN_ONLY, String.class, Integer.class),

    /**
     * Reset my password
     */
    RESET_PASSWD(Boolean.class, AccessLevel.JOIN_ONLY, String.class),

    /**
     * Get user by name
     */
    GET_USER(User.class, AccessLevel.SUPER_USER, String.class),

    /**
     * Get details description of domain
     */
    GET_DOMAIN_DETAILS(String.class, AccessLevel.SUPER_USER, String.class),

    GET_ESRI_REFRESH_TOKEN(String.class, AccessLevel.JOIN_ONLY),

    SET_ESRI_REFRESH_TOKEN(Boolean.class, AccessLevel.JOIN_ONLY, String.class);

    private final List<Class<?>> classes = new ArrayList<Class<?>>();

    private AccessLevel acceslevel = AccessLevel.JOIN_ONLY;

    private Class<?> responseClass = null;

    private UserServiceEventType(AccessLevel level, Class<?>... c) {
        this(null, level, c);
    }

    private UserServiceEventType(Class<?> responseClass, AccessLevel level, Class<?>... c) {

        this.responseClass = responseClass;

        // set level
        acceslevel = level;
        // maybe more
        for (Class<?> classz : c) {
            classes.add(classz);
        }
    }

    @Override
    public boolean canBePredefined() {
        return false;
    }

    @Override
    public AccessLevel getAccessLevel() {
        return acceslevel;
    }

    @Override
    public List<Class<?>> getClasses() {
        return classes;
    }

    @Override
    public Class<?> getResponseClass() {
        return responseClass;
    }

    @Override
    public boolean isServerSide() {
        return true;
    }
}
