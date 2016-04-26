/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.net.event;

import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.core.net.Network;
import nl.tytech.core.net.Network.AppType;
import nl.tytech.core.net.event.RemoteServicesEvent.ServiceEventType;
import nl.tytech.core.net.serializable.BucketWaiter;
import nl.tytech.core.net.serializable.Domain.Filter;
import nl.tytech.core.net.serializable.Invite;
import nl.tytech.core.net.serializable.JoinReply;
import nl.tytech.core.net.serializable.ProjectData;
import nl.tytech.core.net.serializable.ProjectData.Permission;
import nl.tytech.core.net.serializable.ProjectData.PermissionType;
import nl.tytech.core.net.serializable.SaveData;
import nl.tytech.core.net.serializable.SlotData;
import nl.tytech.core.net.serializable.SlotInfo;
import nl.tytech.core.net.serializable.TLicense;
import nl.tytech.core.net.serializable.TokenPair;
import nl.tytech.core.net.serializable.User.AccessLevel;
import nl.tytech.data.core.item.ChatMessage;
import nl.tytech.locale.TLanguage;

/**
 * Basic Service events, like starting a session
 * @author Maxim Knepfle
 *
 */
public enum IOServiceEventType implements ServiceEventType {

    @EventParamData(desc = "Get the actual server time in millis, usefull for synchronizing client-server clocks.", params = {})
    GET_SERVER_TIME(Long.class, AccessLevel.JOIN_ONLY),

    @EventParamData(desc = "Get a listing of waiting client in my domain.", params = {})
    GET_WAITING_CLIENTS_LIST(BucketWaiter[].class, AccessLevel.HOST_SESSION),

    @EventParamData(desc = "Update server data like assets checksums", params = {})
    UPDATE_SERVER_DATA(Boolean.class, AccessLevel.SUPER_USER),

    @EventParamData(desc = "Get all active sessions for the given domain", params = { "Domain name" })
    GET_DOMAIN_SESSIONS(SlotData[].class, AccessLevel.HOST_SESSION, String.class),

    @EventParamData(desc = "Get all active sessions on this server", params = {})
    GET_ALL_SESSIONS(AccessLevel.SUPER_USER),

    @EventParamData(desc = "Get a listing of the projects that I am allowed to manage for given domain", params = { "Domain name", })
    GET_VISIBLE_DOMAIN_PROJECTS(ProjectData[].class, AccessLevel.DOMAIN_ADMIN, String.class),

    @EventParamData(desc = "Put the waiting clinet with wait token in the given session.", params = { "Client waiting token",
            "Server Slot ID" })
    PUT_WAITING_CLIENT_IN_SESSION(AccessLevel.HOST_SESSION, String.class, Integer.class),

    @EventParamData(desc = "Get all saved sessions in given Domain for project name.", params = { "Domain", "Projectname" })
    GET_PROJECT_SESSION_SAVES(SaveData[].class, AccessLevel.HOST_SESSION, String.class, String.class),

    @EventParamData(desc = "Validate the startup parameters", params = { "Project name", "Language: NL, EN" })
    VALIDATE_START_PARAMS(Boolean.class, AccessLevel.JOIN_ONLY, String.class, String.class),

    @EventParamData(desc = "Send invite to someone to join the session", params = { "Server token",
            "ID of stakeholder to play by default.", "Public or private invite to someone in your domain.", "Their email address" })
    INVITE(AccessLevel.JOIN_ONLY, String.class, Integer.class, Boolean.class, String.class),

    @EventParamData(desc = "Join a session", params = { "Server slot ID", "My application type: EDITOR, PARTICIPANT, FACILITATOR, BEAMER ",
            "My client address (optional)", "My client computer name (optional)", "My client token for rejoining (optional)" })
    JOIN_SESSION(JoinReply.class, AccessLevel.JOIN_ONLY, Integer.class, AppType.class, String.class, String.class, String.class),

    @EventParamData(desc = "Get all sessions in my domain that I can join.")
    GET_MY_JOINABLE_SESSIONS(SlotInfo[].class, AccessLevel.JOIN_ONLY),

    @EventParamData(desc = "Get all sessions in my domain that I can continue, given my token pairs.", params = { "TokenPair array" })
    GET_CONTINUABLE_SESSIONS(SlotInfo[].class, AccessLevel.JOIN_ONLY, TokenPair[].class),

    @EventParamData(desc = "Exit project with given project slot ID, clienttoken and a boolean that defines if the session must be kept alive or that it can be closed when this one is the last logged in user.", params = {
            "Server slot ID", "Client session token", "Keep alive?" })
    CLOSE_SESSION(Boolean.class, AccessLevel.JOIN_ONLY, Integer.class, String.class, Boolean.class),

    @EventParamData(desc = "Create a new project with given name and language, returns project data info.", params = { "Project name",
            "Language: NL, EN" })
    CREATE_NEW_PROJECT(ProjectData.class, AccessLevel.EDITOR, String.class, TLanguage.class),

    /**
     * Load project for given projectName projectInstanceName, and saveName.
     */
    LOAD_SAVED_SESSION(Integer.class, AccessLevel.HOST_SESSION, Network.SessionType.class, String.class, String.class, String.class,
            String.class),

    /**
     * Start a new project with the given language (or when null the default) and trigger a event bundle (or none).
     */
    @EventParamData(desc = "Start a new session", params = { "SessionType: SINGLE, MULTI, EDITOR", "Project file name",
            "Language: NL, EN (optional)", "Trigger bundle ID (optional)", "Group token (optional)" })
    START_NEW_SESSION(Integer.class, AccessLevel.HOST_SESSION, Network.SessionType.class, String.class, TLanguage.class, Integer.class,
            String.class),

    /**
     * Delete a project named : projectName
     */
    DELETE_PROJECT(Boolean.class, AccessLevel.EDITOR, String.class),

    /**
     * Delete a domain named : domainName
     */
    DELETE_DOMAIN(Boolean.class, AccessLevel.SUPER_USER, String.class),

    /**
     * Get chat messages of my domain
     */
    GET_DOMAIN_MESSAGES(ChatMessage[].class, AccessLevel.JOIN_ONLY, Integer.class),

    /**
     * Send message to my domain.
     */
    SEND_DOMAIN_MESSAGE(AccessLevel.JOIN_ONLY, String.class),

    /**
     * Send email of e.g. a bug report to the RD team.
     */
    SEND_EMAIL(AccessLevel.JOIN_ONLY, String.class),

    /**
     * Send email of e.g. a bug report to the RD team without logging in.
     */
    SEND_SUPPORT_EMAIL(AccessLevel.JOIN_ONLY, String.class),

    /**
     * Kill a running project session with given projectslot ID.
     */
    KILL_SESSION(Boolean.class, AccessLevel.HOST_SESSION, Integer.class),

    /**
     * Check waiting bucket for invites
     */
    CHECK_WAITING_BUCKET(Integer.class, AccessLevel.JOIN_ONLY, String.class, String.class),

    /**
     * Change license of given domain.
     */
    CHANGE_DOMAIN_LICENSE(AccessLevel.DOMAIN_ADMIN, String.class, TLicense.class),

    /**
     * Public or private project
     */
    SET_TEMPLATE(Boolean.class, AccessLevel.DOMAIN_ADMIN, String.class, Boolean.class),

    /**
     * Get amount of projects in my domain.
     */
    GET_AMOUNT_DOMAIN_PROJECTS(Integer.class, AccessLevel.EDITOR),

    /**
     * Change the domain of the project.
     */
    @EventParamData(desc = "Change the domain of the project.", params = { "Project name", "Domain name", })
    CHANGE_PROJECT_DOMAIN(Boolean.class, AccessLevel.SUPER_USER, String.class, String.class),

    /**
     * Change the owner of the project.
     */
    CHANGE_PROJECT_CREATOR(Boolean.class, AccessLevel.EDITOR, String.class, String.class),

    /**
     * Get the project data
     */
    GET_PROJECT_DATA(ProjectData.class, AccessLevel.JOIN_ONLY, String.class),

    /**
     * Trigger a message warning for all clients connected to sessions.
     */
    SUPER_USER_MESSAGE(AccessLevel.SUPER_USER, String.class),

    /**
     * Message in update box in app statup.
     */
    SET_UPDATE_MESSAGE(AccessLevel.SUPER_USER, String.class),

    /**
     * Message in update box in app statup.
     */
    GET_UPDATE_MESSAGE(String.class, AccessLevel.JOIN_ONLY),

    /**
     * Get the score for the given videocard name
     */
    GET_VIDEO_CARD_SCORE(Integer.class, AccessLevel.JOIN_ONLY, String.class),

    /**
     * Get the score for the given cpu name
     */
    GET_CPU_SCORE(Integer.class, AccessLevel.JOIN_ONLY, String.class),

    /**
     * Get all template projects for me
     */
    GET_DOMAIN_STARTABLE_TEMPLATES(ProjectData[].class, AccessLevel.EDITOR, String.class),

    /**
     * Get names of all projects
     */
    GET_PROJECT_NAMES(String[].class, AccessLevel.SUPER_USER, Filter.class),

    /**
     * Change permission on project
     */
    SET_PROJECT_PERMISSION(Boolean.class, AccessLevel.EDITOR, String.class, PermissionType.class, Permission.class),

    GET_MY_STARTABLE_PROJECTS(ProjectData[].class, AccessLevel.EDITOR),

    GET_DOMAIN_STARTABLE_PROJECTS(ProjectData[].class, AccessLevel.JOIN_ONLY, String.class),

    GET_INVITE(Invite.class, AccessLevel.HOST_SESSION, String.class),

    SET_SESSION_KEEP_ALIVE(Boolean.class, AccessLevel.EDITOR, Integer.class, Boolean.class),

    IS_SESSION_KEEP_ALIVE(Boolean.class, AccessLevel.EDITOR, String.class),

    SAVE_SESSION(String.class, AccessLevel.HOST_SESSION, Integer.class, String.class),

    SAVE_PROJECT_INIT(String.class, AccessLevel.EDITOR, Integer.class),

    SAVE_PROJECT_INIT_AS(String.class, AccessLevel.EDITOR, Integer.class, String.class, String.class),

    SET_ACTIVE_PROJECT_VERSION(Boolean.class, AccessLevel.EDITOR, String.class, Integer.class),

    ADD_PROJECT_VERSION(String.class, AccessLevel.EDITOR, Integer.class, String.class);

    public static final String NAME_USED_ERROR = "Project name is already used, please select another one.";

    private final List<Class<?>> classes = new ArrayList<Class<?>>();

    private AccessLevel eventLevel;

    private Class<?> responseClass = null;

    private IOServiceEventType(AccessLevel level, Class<?>... c) {
        this(null, level, c);
    }

    private IOServiceEventType(Class<?> responseClass, AccessLevel level, Class<?>... c) {

        this.responseClass = responseClass;
        // set level
        eventLevel = level;
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
        return eventLevel;
    }

    @Override
    public List<Class<?>> getClasses() {
        return classes;
    }

    @Override
    public Class<?> getResponseClass() {
        return this.responseClass;
    }

    @Override
    public boolean isServerSide() {
        return true;
    }
}
