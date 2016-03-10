/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.util.logger;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 *
 * @author Alwin
 *
 */
public class Console extends Handler {

    public Console() {
        this.setFormatter(new EclipseFormatter());
    }

    @Override
    public void close() throws SecurityException {
    }

    @Override
    public void flush() {
    }

    @Override
    public void publish(LogRecord record) {

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        int index = 0;
        for (StackTraceElement ste : stackTrace) {
            if (!(ste.getClassName().equals("nl.tytech.util.logger.TLogger") || ste.getClassName().equals("java.lang.Thread")
                    || ste.getClassName().equals("nl.tytech.util.logger.Console") || ste.getClassName().equals("java.util.logging.Logger"))) {
                break;
            }
            index++;
        }

        record.setSourceClassName("(" + stackTrace[index].getFileName() + ":" + stackTrace[index].getLineNumber() + ")");
        record.setSourceMethodName(stackTrace[index].getMethodName());
        if (record.getLevel().intValue() >= Level.SEVERE.intValue()) {
            System.err.print(getFormatter().format(record));
        } else if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
            System.err.print(getFormatter().format(record));
        } else {
            System.out.print(getFormatter().format(record));
        }
    }
}
