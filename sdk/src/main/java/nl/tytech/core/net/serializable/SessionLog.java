/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.net.serializable;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import nl.tytech.core.net.Network;
import nl.tytech.util.StringUtils;
import nl.tytech.util.logger.TLogger;

/**
 * Logging of sessions
 * @author Maxim Knepfle
 *
 */
public class SessionLog implements Serializable {

    private static class LogComparator implements Comparator<SessionLog> {

        @Override
        public int compare(SessionLog o1, SessionLog o2) {
            if (o1.getStartupTime() == o2.getStartupTime())
                return 0;
            else if (o1.getStartupTime() < o2.getStartupTime())
                return 1;
            else
                return -1;
        }
    }

    public final static Comparator<SessionLog> START_TIME_COMPARATOR = new LogComparator();

    /**
     *
     */
    private static final long serialVersionUID = 8405302259594226584L;

    public final static String LOG_POSTFIX_DEFINITIONS = ".def";

    private long startupTime = -1;

    private String gameName = StringUtils.EMPTY;

    private String userName = StringUtils.EMPTY;

    private String sessionType = StringUtils.EMPTY;

    private String token = StringUtils.EMPTY;

    private String domainName = StringUtils.EMPTY;

    private boolean publicSession = false;

    private long shutdownTime = -1;

    public SessionLog() {
    }

    public SessionLog(String domainName, String userName, String gameName, String sessionType, boolean publicSession, String token,
            long startupTime) {

        this.domainName = domainName;
        this.userName = userName;
        this.gameName = gameName;
        this.sessionType = sessionType;
        this.startupTime = startupTime;
        this.token = token;
        this.publicSession = publicSession;
    }

    public String getDomainName() {
        return this.domainName;
    }

    public String getGameName() {
        return gameName;
    }

    public String getSessionType() {
        return sessionType;
    }

    public Network.SessionType getSessionTypeEnum() {
        if (!StringUtils.containsData(sessionType)) {
            TLogger.severe("SessionType cannot be null in session log");
            return null;
        }
        for (Network.SessionType gameTypeEnum : Network.SessionType.values()) {
            if (gameTypeEnum.name().equals(sessionType.trim())) {
                return gameTypeEnum;
            }
        }
        TLogger.severe("SessionType cannot be null in session log");
        return null;
    }

    public long getShutdownTime() {
        return shutdownTime;
    }

    public long getStartupTime() {
        return startupTime;
    }

    public String getToken() {
        return token;
    }

    public String getUserName() {
        return userName;
    }

    public boolean isPublicSession() {
        return publicSession;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public void setPublicSession(boolean publicSession) {
        this.publicSession = publicSession;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public void setShutdownTime(long shutdownTime) {
        this.shutdownTime = shutdownTime;
    }

    public void setStartupTime(long startupTime) {
        this.startupTime = startupTime;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(startupTime);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        String result = gameName + " (" + (this.publicSession ? "Public " : "Private ")
                + StringUtils.capitalizeWithSpacedUnderScores(sessionType) + "): " + sdf.format(cal.getTime());
        if (shutdownTime > 0) {
            cal.setTimeInMillis(shutdownTime);
            result += " --- " + sdf.format(cal.getTime());
        }
        if (StringUtils.containsData(userName)) {
            result += " started by: " + userName;
        }
        return result;
    }
}
