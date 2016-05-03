/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.net.serializable;

import java.io.Serializable;

/**
 *
 * @author Maxim Knepfle
 *
 */
public class SaveData implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 6874618008696400543L;

    private String saveName;

    private String projectDomain;

    // Note: NOT project domain but session domain, e.g. customer domain saving universal project climategame)
    private String sessionDomain;

    private String projectName;

    private int projectVersion;

    private String info;

    private String language;

    private String token;

    private String sessionName;

    public SaveData() {

    }

    public String getInfo() {
        return info;
    }

    public String getLanguage() {
        return language;
    }

    public String getProjectDomain() {
        return projectDomain;
    }

    public String getProjectName() {
        return projectName;
    }

    public int getProjectVersion() {
        return projectVersion;
    }

    public String getSaveName() {
        return saveName;
    }

    public String getSessionDomain() {
        return sessionDomain;
    }

    public String getSessionName() {
        return sessionName;
    }

    public String getToken() {
        return token;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setProjectDomain(String projectDomain) {
        this.projectDomain = projectDomain;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setProjectVersion(int projectVersion) {
        this.projectVersion = projectVersion;
    }

    public void setSaveName(String saveName) {
        this.saveName = saveName;
    }

    public void setSessionDomain(String sessionDomain) {
        this.sessionDomain = sessionDomain;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
