/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.util.logger;

import java.io.File;
import java.io.IOException;
import java.security.AccessControlException;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.XMLFormatter;
import nl.tytech.util.OSUtils;
import nl.tytech.util.StringUtils;
import nl.tytech.util.ThreadUtils;

/**
 * TLogger
 * <p>
 * TLogger handles log and exception messages.
 * <p>
 *
 *
 * @author Jeroen Warmerdam, Alwin Lemstra, Maxim Knepfle
 */
public class TLogger {

    private static class SingletonHolder {
        private static final TLogger INSTANCE = new TLogger();
    }

    private final static Object LOCK = new Object();

    private static final int MAX_REPEAT = 10;

    /**
     * Current directory.
     */
    private final static String WORK_DIRECTORY = OSUtils.STORAGE_DIRECTORY + "Logs" + File.separator;

    public static void addHandler(Handler handler) {
        SingletonHolder.INSTANCE._addHandler(handler);
    }

    /**
     * Log an exception with a message and level
     *
     * @param level
     * @param exp
     * @param log
     */
    public static void exception(final Level level, final Throwable exp, final String log) {
        SingletonHolder.INSTANCE._log(level, exp, log);
    }

    /**
     * Log an exception
     *
     * @param exp
     */
    public static void exception(final Throwable exp) {
        exception(exp, null);
    }

    /**
     * Log an exception with a message
     *
     * @param exp
     * @param log
     */
    public static void exception(final Throwable exp, final String log) {
        exception(Level.SEVERE, exp, log);
    }

    /**
     * Log an info message
     *
     * @param log
     */
    public static void info(final String log) {
        log(Level.INFO, log);
    }

    /**
     * Log a message of level TLevel. If the system is running in debug mode, it quits the system on a >= Level.SEVERE log.
     *
     * @param level
     * @param log
     */
    public static void log(final Level level, final String log) {
        SingletonHolder.INSTANCE._log(level, log);
    }

    public static void notification(final String log) {
        log(TLevel.NOTIFICATION, log);
    }

    public static void removeHandler(Handler handler) {
        SingletonHolder.INSTANCE._removeHandler(handler);
    }

    /**
     * Set the Level that this Logger should display information from
     *
     * @param level
     */
    public static void setLevel(final Level level) {
        synchronized (LOCK) {
            SingletonHolder.INSTANCE.logger.setLevel(level);
        }
    }

    /**
     * @param logToFile the logToFile to set
     */
    // FIXME: this should be a separate handler class. just like console or syslog.
    public static void setLogToFile(boolean logToFile) {
        synchronized (LOCK) {
            SingletonHolder.INSTANCE.setLogToFileInner(logToFile);
        }
    }

    public static void setSimpleLogger(boolean simpleLogging) {
        SingletonHolder.INSTANCE._setSimpleLogger(simpleLogging);
    }

    /**
     * Log a severe log
     *
     * @param log
     */
    public static void severe(final String log) {
        log(Level.SEVERE, log);
    }

    /**
     * Log a showstopper log
     *
     * @param log
     */
    public static void showstopper(final String log) {
        log(TLevel.SHOWSTOPPER, log);
    }

    /**
     * Log a warning message
     *
     * @param log
     */
    public static void warning(final String log) {
        log(Level.WARNING, log);
    }

    private Logger logger = Logger.getLogger("global");

    private FileHandler fileHandler;
    private String lastLog = null;

    private int lastLogCounter = 0;

    /**
     * Simple logging has no handler , just system out, handlers seem to create an issue with JET in combination with Runtime.Exe()
     */
    private boolean simpleLogging = false;

    /**
     * Private constructor for singleton pattern
     */
    private TLogger() {

        try {
            logger.setLevel(Level.ALL);
        } catch (AccessControlException ex) {
            // This might be running in an applet or some other reason for not
            // being allowed to edit the settings for the named logger.
            // Replace the logger instance with an anonymous logger instead.\
            logger = Logger.getAnonymousLogger();
            logger.setLevel(Level.ALL);
        }

        // do default logging
        initLogging();

    }

    private void _addHandler(Handler handler) {
        logger.addHandler(handler);
    }

    private void _log(final Level level, String log) {

        // already shutdown or non active?
        if (logger.getLevel() == null && !simpleLogging) {
            return;
        }

        // ignore all below my level
        if (logger.getLevel() != null && logger.getLevel().intValue() > Level.WARNING.intValue()) {
            return;
        }

        if (log.equals(lastLog)) {
            lastLog = log;
            lastLogCounter++;
            if (lastLogCounter == MAX_REPEAT) {
                log = ".... truncated after " + MAX_REPEAT + " duplicate logs.";
            }
            if (lastLogCounter > MAX_REPEAT) {
                return;
            }
        } else {
            lastLog = log;
            lastLogCounter = 0;
        }

        // Log the message
        if (simpleLogging) {
            System.out.println("[" + Thread.currentThread().getName() + ": id:" + Thread.currentThread().getId() + " p:"
                    + Thread.currentThread().getPriority() + "]\t" + log);
        } else {
            logger.log(level, "[" + Thread.currentThread().getName() + ": id:" + Thread.currentThread().getId() + " p:"
                    + Thread.currentThread().getPriority() + "]\t" + log);
        }

        // Possible stop of JVM
        if (level.equals(TLevel.SHOWSTOPPER)) {
            // show where it went wrong
            Thread.dumpStack();
            // let user know what going on, before directly closing the JVM
            ThreadUtils.sleepInterruptible(5000);
            System.exit(1);
        }
    }

    private void _log(final Level level, final Throwable exp, final String log) {
        this._log(level, convertThrowableToMessage(exp, log));
    }

    private void _removeHandler(Handler handler) {
        logger.removeHandler(handler);
    }

    private void _setSimpleLogger(boolean simpleLogging) {
        this.simpleLogging = simpleLogging;
    }

    /**
     * Convert the Throwable to a human readable string
     */
    private String convertThrowableToMessage(final Throwable exp, final String details) {

        StringBuffer buffer = new StringBuffer();

        // Read the cause of the exception
        String cause = "?";
        String message = "?";

        try {
            cause = exp.getCause().toString();
        } catch (Exception e) {
        }

        try {
            message = exp.getMessage();
        } catch (Exception e) {
        }

        // Recreate the stack trace in a String
        String stack = StringUtils.EMPTY;

        StackTraceElement[] stackArray = exp.getStackTrace();
        for (int i = 0; i < stackArray.length; i++) {
            stack += stackArray[i] + "\n";
        }

        // Read the file line
        String fileLine = StringUtils.EMPTY;

        if (stackArray.length > 0) {
            fileLine += stackArray[0].getFileName() + " line " + stackArray[0].getLineNumber();
        } else {
            fileLine = "Unknown";
        }
        System.err.println("\nERROR: " + fileLine + ", check Error Report in the log dir.\n");

        buffer.append("Error Report:\n");
        buffer.append(exp);
        buffer.append("\nDate:\n");
        buffer.append(getDate());
        buffer.append("\nCause:\n");
        buffer.append(cause);
        buffer.append("\nMessage:\n");
        buffer.append(message);
        buffer.append("\nFile & Line\n");
        buffer.append(fileLine);
        buffer.append("\nStackTrace:\n");
        buffer.append(stack);
        if (details != null && details.length() > 0) {
            buffer.append("\nExtra Details:\n");
            buffer.append(details);
        }

        exp.printStackTrace();
        return buffer.toString();
    }

    /**
     * get timestamp Y-M-D-H-M-S-L formatted
     */
    private String getDate() {

        return String.format("%1$tY-%1$tm-%1$te-%1$tH-%1$tM-%1$tS-%1$tL", Calendar.getInstance());
    }

    private void initLogging() {
        try {
            // remove old handlers, we don't want the console logging of java
            Logger root = Logger.getLogger(StringUtils.EMPTY);
            while (root.getHandlers().length != 0) {
                root.removeHandler(root.getHandlers()[0]);
            }

            // set console handler, the tlogger will fallthrough to this logger
            root.addHandler(new Console());
            logger.setFilter(null);
        } catch (SecurityException e) {
            logger.warning("Can't remove default logger in a secure environment.");
        }
    }

    /**
     * The non-static inner method for setting whether the handler should write to file
     *
     * @param logToFile
     */
    protected void setLogToFileInner(boolean logToFile) {

        if (logToFile) {
            // add file handler
            try {
                if (fileHandler == null) {
                    // Check if the correct directory exists
                    File file = new File(WORK_DIRECTORY);
                    if (!file.exists()) {
                        file.mkdirs();
                        file.createNewFile();
                    }
                    fileHandler = new FileHandler(WORK_DIRECTORY + "log-" + getDate() + ".xml", true);
                    XMLFormatter formatter = new StyledXMLFormatter("logs.xsl");
                    fileHandler.setFormatter(formatter);
                    logger.addHandler(fileHandler);
                }
            } catch (SecurityException e) {
                System.err.println(e);
                // halt the system
                throw new RuntimeException();
            } catch (IOException e) {
                // halt the system
                System.err.println(e);
                throw new RuntimeException();
            }
        } else if (fileHandler != null) {
            // remove file handler
            logger.removeHandler(fileHandler);
            fileHandler = null;
        }
    }
}
