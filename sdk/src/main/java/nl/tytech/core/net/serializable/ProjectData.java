/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.net.serializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import nl.tytech.core.net.serializable.User.AccessLevel;
import nl.tytech.locale.TLanguage;
import nl.tytech.util.logger.TLogger;

/**
 *
 * @author Maxim Knepfle
 *
 */
public class ProjectData implements Comparable<ProjectData>, Serializable {

    public enum Permission {
        NONE, READ, WRITE;
    }

    public enum PermissionType {
        OWNER, DOMAIN, SUPPORT;
    }

    public static final int MINIMUM_NAME_LENGTH = 3;

    public static final int MAXIMUM_NAME_LENGTH = 20;

    /**
     *
     */
    private static final long serialVersionUID = 6874618008696400543L;

    public static boolean isValidProjectName(String name) {
        return name.matches("[a-z0-9_-]{" + MINIMUM_NAME_LENGTH + "," + MAXIMUM_NAME_LENGTH + "}");
    }

    /**
     * Full Project name as it appears in the menu.
     */
    private String fullName = "My New Project";// GAMENAME

    /**
     * Language versions
     */

    private String[] languages = new String[] { TLanguage.NL.name() };// LANGUAGES

    /**
     * Permissions (defaults for old projects, ne one get WRITE READ NONE
     */
    private String[] permissions = new String[] { Permission.WRITE.name(), Permission.NONE.name(), Permission.NONE.name() };// PERMISSIONS

    /**
     * Shared with all
     */
    private boolean universal = false;// UNIVERSAL

    /**
     * Original username of the creator of this project.
     */
    private String owner = "?";// CREATOR

    /**
     * When true project is a template project
     */
    private boolean template = false;// TEMPLATE

    private String description = "";// DESCRIPTION

    private String domain = "";

    private String fileName = "";

    private String[] versions = new String[] { "Base Version" };

    private int activeVersion = 0;

    public ProjectData() {

    }

    @Override
    public int compareTo(ProjectData o) {
        return this.toString().compareToIgnoreCase(o.toString());
    }

    public int getActiveVersion() {
        return activeVersion;
    }

    public String getDescription() {
        return description;
    }

    public String getDomain() {
        return domain;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFullName() {
        return fullName;
    }

    public String[] getLanguages() {
        return languages;
    }

    public List<TLanguage> getLanguagesList() {

        List<TLanguage> langList = new ArrayList<>();
        for (String lang : this.languages) {
            langList.add(TLanguage.valueOf(lang.trim()));
        }

        // sort
        Collections.sort(langList);
        return langList;
    }

    public String getOwner() {
        return owner;
    }

    public Permission getPermission(PermissionType type) {
        return Permission.valueOf(this.permissions[type.ordinal()]);
    }

    public String[] getPermissions() {
        return permissions;
    }

    public String[] getVersions() {
        return versions;
    }

    public boolean isDeleteable(User user) {

        if (user == null) {
            TLogger.severe("Checking deleteable for NULL user!");
            return false;
        }

        // universal template project (shared with all) not deleteable
        if (this.isUniversal()) {
            return false;
        }

        // admins of this domain may always delete it.
        if (user.getDomain().equals(this.getDomain()) && user.getMaxAccessLevel().ordinal() >= AccessLevel.DOMAIN_ADMIN.ordinal()) {
            return true;
        }

        /**
         * Else only with write rights
         */
        return this.isWritable(user);
    }

    public boolean isPermissionable(User user) {

        if (user == null) {
            TLogger.severe("Checking writable for NULL user!");
            return false;
        }

        // universal template project (shared with all) not permissionable
        if (this.isUniversal()) {
            return false;
        }
        /**
         * Owner may always change permission, templates etc.
         */
        if (user.getUserName().equals(owner)) {
            return true;
        }

        /**
         * Domain admin may also change this.
         */
        if (user.getDomain().equals(domain) && user.getMaxAccessLevel().ordinal() >= AccessLevel.DOMAIN_ADMIN.ordinal()) {
            return true;
        }
        /**
         * Else only with write rights
         */
        return this.isWritable(user);
    }

    /**
     * When true this project is readable for this user
     */
    public boolean isReadable(User user) {

        if (user == null) {
            TLogger.severe("Checking readbility for NULL user!");
            return false;
        }
        // universal template project (shared with all)
        if (this.isUniversal()) {
            return true;
        }
        if (user.getUserName().equals(owner)) {
            return this.getPermission(PermissionType.OWNER) != Permission.NONE;
        }
        if (user.getDomain().equals(domain)) {
            return this.getPermission(PermissionType.DOMAIN) != Permission.NONE;
        }
        if (user.isSuperUser()) {
            return this.getPermission(PermissionType.SUPPORT) != Permission.NONE;
        }
        return false;
    }

    public boolean isReadOnly() {

        if (isUniversal()) {
            return true;
        }

        for (PermissionType type : PermissionType.values()) {
            if (this.getPermission(type) == Permission.WRITE) {
                return false;
            }
        }
        return true;
    }

    public boolean isTemplate() {
        return this.template;
    }

    public boolean isUniversal() {
        return this.universal;
    }

    public boolean isVisible(User user) {

        if (user == null) {
            TLogger.severe("Checking visbility for NULL user!");
            return false;
        }
        // universals, superusers and owners may always see it
        if (this.isUniversal() || user.isSuperUser() || user.getUserName().equals(owner)) {
            return true;
        }

        if (user.getDomain().equals(domain)) {
            // domain admins may always see the project
            if (user.getMaxAccessLevel().ordinal() >= AccessLevel.DOMAIN_ADMIN.ordinal()) {
                return true;
            }
            // other user only when permission allows so
            return this.getPermission(PermissionType.DOMAIN) != Permission.NONE;
        }
        return false;
    }

    public boolean isWritable(User user) {

        if (user == null) {
            TLogger.severe("Checking writable for NULL user!");
            return false;
        }
        // universal template project (shared with all) not writable
        if (this.isUniversal()) {
            return false;
        }
        if (user.getUserName().equals(owner)) {
            return this.getPermission(PermissionType.OWNER) == Permission.WRITE;
        }
        if (user.getDomain().equals(domain)) {
            return this.getPermission(PermissionType.DOMAIN) == Permission.WRITE;
        }
        if (user.isSuperUser()) {
            return this.getPermission(PermissionType.SUPPORT) == Permission.WRITE;
        }
        return false;
    }

    public void setActiveVersion(int activeVersion) {
        this.activeVersion = activeVersion;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setLanguages(String[] languages) {
        this.languages = languages;
    }

    public void setOwner(String creator) {
        this.owner = creator;
    }

    public void setPermission(PermissionType type, Permission permission) {
        this.permissions[type.ordinal()] = permission.name();
    }

    public void setPermissions(String[] permissions) {
        this.permissions = permissions;
    }

    public void setTemplate(boolean template) {
        this.template = template;
    }

    public void setUniversal(boolean universal) {
        this.universal = universal;
    }

    public void setVersions(String[] versions) {
        this.versions = versions;
    }

    @Override
    public String toString() {
        return getFileName();
    }
}
