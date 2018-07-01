/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 1998, 2018 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     11/07/2017 - Dalia Abo Sheasha
//       - 526957 : Split the logging and trace messages
package org.eclipse.persistence.logging;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Date;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.localization.LoggingLocalization;
import org.eclipse.persistence.internal.localization.TraceLocalization;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.sessions.Session;

/**
 * Represents the abstract log that implements all the generic logging functions.
 * It contains a singleton SessionLog that logs messages from outside any EclipseLink session.
 * The singleton SessionLog can also be passed to an EclipseLink session when messages
 * are logged through that session.  When JDK1.4 is used, a singleton JavaLog is created.
 * Otherwise a singleton DefaultSessionLog is created.
 *
 * @see SessionLog
 * @see SessionLogEntry
 * @see DefaultSessionLog
 * @see JavaLog
 */
public abstract class AbstractSessionLog implements SessionLog, java.lang.Cloneable {

    /**
     * Represents the log level
     */
    protected int level;

    /**
     * Represents the singleton SessionLog
     */
    protected static SessionLog defaultLog;

    /**
     * Represents the session that owns this SessionLog
     */
    protected Session session;

    /**
     * Represents prefix to logged severe
     */
    protected static String SEVERE_PREFIX = null;

    /**
     * Represents prefix to logged warning
     */
    protected static String WARNING_PREFIX = null;

    /**
     * Represents prefix to logged info
     */
    protected static String INFO_PREFIX = null;

    /**
     * Represents prefix to logged config
     */
    protected static String CONFIG_PREFIX = null;

    /**
     * Represents prefix to logged fine
     */
    protected static String FINE_PREFIX = null;

    /**
     * Represents prefix to logged finer
     */
    protected static String FINER_PREFIX = null;

    /**
     * Represents prefix to logged finest
     */
    protected static String FINEST_PREFIX = null;

    /**
     * Cached TopLink prefix string.
     */
    protected static String TOPLINK_PREFIX = null;

    /**
     * Connection string
     */
    protected static final String CONNECTION_STRING = "Connection";

    /**
     * Thread string
     */
    protected static final String THREAD_STRING = "Thread";

    /**
     * Represents the writer that will receive the formatted log entries
     */
    protected Writer writer;

    protected static String DATE_FORMAT_STR = "yyyy.MM.dd HH:mm:ss.SSS";
    /**
     * Format use to print the current date/time.
     */
    protected DateFormat dateFormat;

    /**
     * Allows the printing of the stack to be explicitly disabled/enabled.
     * CR #3870467.
     * null value is default behavior of determining from log level.
     */
    protected Boolean shouldLogExceptionStackTrace;

    /**
     * Allows the printing of the date to be explicitly disabled/enabled.
     * CR #3870467.
     * null value is default behavior of determining from log level.
     */
    protected Boolean shouldPrintDate;

    /**
     * Allows the printing of the thread to be explicitly disabled/enabled.
     * CR #3870467.
     * null value is default behavior of determining from log level.
     */
    protected Boolean shouldPrintThread;

    /**
     * Allows the printing of the session to be explicitly disabled/enabled.
     * CR #3870467.
     * null value is default behavior of determining from log level.
     */
    protected Boolean shouldPrintSession;

    /**
     * Allows the printing of the connection to be explicitly disabled/enabled.
     * CR #4157545.
     * null value is default behavior of determining from log level.
     */
    protected Boolean shouldPrintConnection;

    /** Used to determine if bingdparameters should be logged or hidden. */
    protected Boolean shouldDisplayData;

    /**
     * Return the system default log level property value.
     * @return The system default log level property value or {@code null} if no such property is set.
     */
    private static String getDefaultLoggingLevelProperty() {
        return PrivilegedAccessHelper.getSystemProperty(PersistenceUnitProperties.LOGGING_LEVEL);
    }

    /**
     * Return the system default log level.
     * This is based on the System property "eclipselink.logging.level", or INFO if not set.
     */
    public static int getDefaultLoggingLevel() {
        return translateStringToLoggingLevel(getDefaultLoggingLevelProperty());
    }

    /**
     * PUBLIC:
     * Create a new AbstractSessionLog
     */
    public AbstractSessionLog() {
        this.writer = new PrintWriter(System.out);
        this.level = getDefaultLoggingLevel();
    }

    /**
     * PUBLIC:
     * <p>
     * Return the log level.  It is used when session is not available.
     *
     * @return the log level
     */
    @Override
    public int getLevel() {
        return getLevel(null);
    }

    /**
     * PUBLIC:
     * <p>
     * Return the log level as a string value.
     */
    @Override
    public String getLevelString() {
        return LogLevel.toValue(getLevel(), LogLevel.INFO).getName();
    }

    /**
     * PUBLIC:
     * <p>
     * Return the log level for the category name space.
     *
     * @return the log level
     * @param category  the string representation of a EclipseLink category, e.g. "sql", "transaction" ...
     */
    @Override
    public int getLevel(String category) {
        return level;
    }

    /**
     * PUBLIC:
     * <p>
     * Set the log level.  It is used when session is not available.
     *
     * @param level     the new log level
     */
    @Override
    public void setLevel(int level) {
        setLevel(level, null);
    }

    /**
     * PUBLIC:
     * <p>
     * Set the log level for the category name space.
     *
     * @param level     the new log level
     * @param category  the string representation of an EclipseLink category, e.g. "sql", "transaction" ...
     */
    @Override
    public void setLevel(int level, String category) {
        this.level = level;
    }

    /**
     * PUBLIC:
     * Return true if SQL logging should log visible bind parameters. If the
     * shouldDisplayData is not set, check the session log level and return
     * true for a level greater than CONFIG.
     */
    @Override
    public boolean shouldDisplayData() {
        if (this.shouldDisplayData != null) {
            return shouldDisplayData.booleanValue();
        } else {
            return this.level < SessionLog.CONFIG;
        }
    }

    /**
     * PUBLIC:
     * <p>
     * Check if a message of the given level would actually be logged.
     * It is used when session is not available.
     *
     * @return true if the given message level will be logged
     * @param level  the log request level
     */
    @Override
    public boolean shouldLog(int level) {
        return shouldLog(level, null);
    }

    /**
     * PUBLIC:
     * <p>
     * Check if a message of the given level would actually be logged for the category name space.
     * !isOff() is checked to screen out the possibility when both
     * log level and log request level are set to OFF.
     *
     * @return true if the given message level will be logged
     * @param level  the log request level
     * @param category  the string representation of an EclipseLink category, e.g. "sql", "transaction" ...*
     */
    @Override
    public boolean shouldLog(int level, String category) {
        return (this.level <= level) && !isOff();
    }

    /**
     * PUBLIC:
     * <p>
     * Return the singleton SessionLog.  If the singleton SessionLog does not exist,
     * a new one is created based on the version of JDK being used from the Version class.
     *
     * @return the singleton SessionLog
     */
    public static SessionLog getLog() {
        if (defaultLog == null) {
            defaultLog = new DefaultSessionLog();
        }
        return defaultLog;
    }

    /**
     * PUBLIC:
     * <p>
     * Set the singleton SessionLog.
     * </p>
     *
     * @param sessionLog  a SessionLog
     */
    public static void setLog(SessionLog sessionLog) {
        defaultLog = sessionLog;
        defaultLog.setSession(null);
    }

    /**
     * PUBLIC:
     * <p>
     * Get the session.
     * </p>
     *
     * @return  session
     */
    @Override
    public Session getSession() {
        return this.session;
    }

    /**
     * PUBLIC:
     * <p>
     * Set the session.
     * </p>
     *
     * @param session  a Session
     */
    @Override
    public void setSession(Session session) {
        this.session = session;
    }

    /**
     * PUBLIC:
     * <p>
     * Log a message that does not need to be translated.  This method is intended for
     * external use when logging messages are required within the EclipseLink output.
     *
     * @param level the log request level value
     * @param message the string message - this should not be a bundle key
     */
    @Override
    public void log(int level, String message) {
        // Warning: do not use this function to pass in bundle keys as they will not get transformed into string messages
        if (!shouldLog(level)) {
            return;
        }
        //Bug#4566524  Pass in false for external use
        log(level, message, (Object[])null, false);
    }

    /**
     * PUBLIC:
     * <p>
     * Log a message with one parameter that needs to be translated.
     *
     * @param level  the log request level value
     * @param message  the string message
     * @param param  a parameter of the message
     */
    @Override
    public void log(int level, String message, Object param) {
        if (!shouldLog(level)) {
            return;
        }
        log(level, message, new Object[] { param });
    }

    /**
     * PUBLIC:
     * <p>
     * Log a message with one parameter that needs to be translated.
     *
     * @param level  the log request level value
     * @param message  the string message
     * @param param  a parameter of the message
     */
    @Override
    public void log(int level, String category, String message, Object param) {
        if (!shouldLog(level, category)) {
            return;
        }
        log(level, category, message, new Object[] { param }, true);
    }

    /**
     * PUBLIC:
     * <p>
     * Log a message with two parameters that needs to be translated.
     *
     * @param level the log request level value
     * @param message the string message
     * @param param1  a parameter of the message
     * @param param2  second parameter of the message
     */
    @Override
    public void log(int level, String message, Object param1, Object param2) {
        if (!shouldLog(level)) {
            return;
        }
        log(level, message, new Object[] { param1, param2 });
    }

    /**
     * PUBLIC:
     * <p>
     * Log a message with two parameters that needs to be translated.
     *
     * @param level the log request level value
     * @param message the string message
     * @param param1  a parameter of the message
     * @param param2  second parameter of the message
     */
    @Override
    public void log(int level, String category, String message, Object param1, Object param2) {
        if (!shouldLog(level)) {
            return;
        }
        log(level, category, message, new Object[] { param1, param2 }, true);
    }

    /**
     * PUBLIC:
     * <p>
     * Log a message with three parameters that needs to be translated.
     *
     * @param level the log request level value
     * @param message the string message
     * @param param1  a parameter of the message
     * @param param2  second parameter of the message
     * @param param3  third parameter of the message
     */
    @Override
    public void log(int level, String message, Object param1, Object param2, Object param3) {
        if (!shouldLog(level)) {
            return;
        }
        log(level, message, new Object[] { param1, param2, param3 });
    }

    /**
     * PUBLIC:
     * <p>
     * Log a message with three parameters that needs to be translated.
     *
     * @param level the log request level value
     * @param message the string message
     * @param param1  a parameter of the message
     * @param param2  second parameter of the message
     * @param param3  third parameter of the message
     */
    @Override
    public void log(int level, String category, String message, Object param1, Object param2, Object param3) {
        if (!shouldLog(level)) {
            return;
        }
        log(level, category, message, new Object[] { param1, param2, param3 }, true);
    }

    /**
     * PUBLIC:
     * <p>
     * Log a message with four parameters that needs to be translated.
     *
     * @param level the log request level value
     * @param message the string message
     * @param param1  a parameter of the message
     * @param param2  second parameter of the message
     * @param param3  third parameter of the message
     * @param param4  third parameter of the message
     */
    @Override
    public void log(int level, String message, Object param1, Object param2, Object param3, Object param4) {
        if (!shouldLog(level)) {
            return;
        }
        log(level, message, new Object[] { param1, param2, param3, param4 });
    }

    /**
     * PUBLIC:
     * <p>
     * Log a message with four parameters that needs to be translated.
     *
     * @param level the log request level value
     * @param message the string message
     * @param param1  a parameter of the message
     * @param param2  second parameter of the message
     * @param param3  third parameter of the message
     * @param param4  third parameter of the message
     */
    @Override
    public void log(int level, String category, String message, Object param1, Object param2, Object param3, Object param4) {
        if (!shouldLog(level)) {
            return;
        }
        log(level, category, message, new Object[] { param1, param2, param3, param4 }, true);
    }

    /**
     * PUBLIC:
     * <p>
     * Log a message with an array of parameters that needs to be translated.
     *
     * @param level the log request level value
     * @param message the string message
     * @param params array of parameters to the message
     */
    @Override
    public void log(int level, String message, Object[] params) {
        log(level, message, params, true);
    }

    /**
     * PUBLIC:
     * <p>
     * Log a message with an array of parameters that needs to be translated.
     *
     * @param level the log request level value
     * @param message the string message
     * @param params array of parameters to the message
     */
    @Override
    public void log(int level, String category, String message, Object[] params) {
        log(level, category, message, params, true);
    }

    /**
     * PUBLIC:
     * <p>
     * Log a message.  shouldTranslate determines if the message needs to be translated.
     *
     * @param level the log request level
     * @param message the string message
     * @param params array of parameters to the message
     * @param shouldTranslate true if the message needs to be translated
     */
    @Override
    public void log(int level, String message, Object[] params, boolean shouldTranslate) {
        if (!shouldLog(level)) {
            return;
        }
        log(new SessionLogEntry(level, null, message, params, null, shouldTranslate));
    }

    /**
     * PUBLIC:
     * <p>
     * Log a message.  shouldTranslate determines if the message needs to be translated.
     *
     * @param level the log request level
     * @param message the string message
     * @param category the log category
     * @param params array of parameters to the message
     * @param shouldTranslate true if the message needs to be translated
     */
    @Override
    public void log(int level, String category, String message, Object[] params, boolean shouldTranslate) {
        if (!shouldLog(level, category)) {
            return;
        }
        log(new SessionLogEntry(level, category, null, message, params, null, shouldTranslate));
    }

    /**
     * PUBLIC:
     * <p>
     * Log a SessionLogEntry
     *
     * @param sessionLogEntry SessionLogEntry that holds all the information for an EclipseLink logging event
     */
    @Override
    public abstract void log(SessionLogEntry sessionLogEntry);

    /**
     * By default the session (and its connection is available) are printed,
     * this can be turned off.
     */
    @Override
    public boolean shouldPrintSession() {
        return (shouldPrintSession == null) || shouldPrintSession.booleanValue();
    }

    /**
     * By default the session (and its connection is available) are printed,
     * this can be turned off.
     */
    @Override
    public void setShouldPrintSession(boolean shouldPrintSession) {
        if (shouldPrintSession) {
            this.shouldPrintSession = Boolean.TRUE;
        } else {
            this.shouldPrintSession = Boolean.FALSE;
        }
    }

    /**
     * By default the connection is printed, this can be turned off.
     */
    @Override
    public boolean shouldPrintConnection() {
        return (shouldPrintConnection == null) || shouldPrintConnection.booleanValue();
    }

    /**
     * By default the connection is printed, this can be turned off.
     */
    @Override
    public void setShouldPrintConnection(boolean shouldPrintConnection) {
        if (shouldPrintConnection) {
            this.shouldPrintConnection = Boolean.TRUE;
        } else {
            this.shouldPrintConnection = Boolean.FALSE;
        }
    }

    /**
     * By default the stack is logged for FINER or less (finest).
     * The logging of the stack can also be explicitly turned on or off.
     */
    @Override
    public boolean shouldLogExceptionStackTrace() {
        if (shouldLogExceptionStackTrace == null) {
            return getLevel() <= FINER;
        } else {
            return shouldLogExceptionStackTrace.booleanValue();
        }
    }

    /**
     * PUBLIC:
     * Set whether bind parameters should be displayed when logging SQL.
     */
    @Override
    public void setShouldDisplayData(Boolean shouldDisplayData) {
        this.shouldDisplayData = shouldDisplayData;
    }

    /**
     * By default the stack is logged for FINER or less (finest).
     * The logging of the stack can also be explicitly turned on or off.
     */
    @Override
    public void setShouldLogExceptionStackTrace(boolean shouldLogExceptionStackTrace) {
        if (shouldLogExceptionStackTrace) {
            this.shouldLogExceptionStackTrace = Boolean.TRUE;
        } else {
            this.shouldLogExceptionStackTrace = Boolean.FALSE;
        }
    }

    /**
     * By default the date is always printed, but can be turned off.
     */
    @Override
    public boolean shouldPrintDate() {
        return (shouldPrintDate == null) || (shouldPrintDate.booleanValue());
    }

    /**
     * By default the date is always printed, but can be turned off.
     */
    @Override
    public void setShouldPrintDate(boolean shouldPrintDate) {
        if (shouldPrintDate) {
            this.shouldPrintDate = Boolean.TRUE;
        } else {
            this.shouldPrintDate = Boolean.FALSE;
        }
    }

    /**
     * By default the thread is logged for FINE or less (finer,etc.).
     * The logging of the thread can also be explicitly turned on or off.
     */
    @Override
    public boolean shouldPrintThread() {
        if (shouldPrintThread == null) {
            return getLevel() <= FINE;
        } else {
            return shouldPrintThread.booleanValue();
        }
    }

    /**
     * By default the thread is logged for FINE or less (finer,etc.).
     * The logging of the thread can also be explicitly turned on or off.
     */
    @Override
    public void setShouldPrintThread(boolean shouldPrintThread) {
        if (shouldPrintThread) {
            this.shouldPrintThread = Boolean.TRUE;
        } else {
            this.shouldPrintThread = Boolean.FALSE;
        }
    }

    /**
     * PUBLIC:
     * <p>
     * Return the writer that will receive the formatted log entries.
     *
     * @return the log writer
     */
    @Override
    public Writer getWriter() {
        return writer;
    }

    /**
     * PUBLIC:
     * <p>
     * Set the writer that will receive the formatted log entries.
     *
     * @param writer  the log writer
     */
    @Override
    public void setWriter(Writer writer) {
        this.writer = writer;
    }


    /**
     * PUBLIC:
     * <p>
     * Set the writer that will receive the formatted log entries.
     *
     * @param outputstream  the log writer
     */
    public void setWriter(OutputStream outputstream) {
        this.writer = new OutputStreamWriter(outputstream);
    }

    /**
     * PUBLIC:
     * Return the date format to be used when printing a log entry date.
     * @return the date format
     */
    public DateFormat getDateFormat() {
        return dateFormat;
    }

    /**
     * Return the specified date and/or time information in string.
     * The format will be determined by the date format settings.
     */
    protected String getDateString(Date date) {
        if (getDateFormat() != null) {
            return getDateFormat().format(date);

        }

        if (date == null) {
            return null;
        }

        // Since we currently do not have a thread-safe way to format dates,
        // we will use ConversionManager to build the string.
        return ConversionManager.getDefaultManager().convertObject(date, String.class).toString();
    }

    /**
     * Return the supplement detail information including date, session, thread, connection,
     * source class name and source method name.
     */
    protected String getSupplementDetailString(SessionLogEntry entry) {
        StringWriter writer = new StringWriter();

        if (shouldPrintDate()) {
            writer.write(getDateString(entry.getDate()));
            writer.write("--");
        }
        if (shouldPrintSession() && (entry.getSession() != null)) {
            writer.write(this.getSessionString(entry.getSession()));
            writer.write("--");
        }
        if (shouldPrintConnection() && (entry.getConnection() != null)) {
            writer.write(this.getConnectionString(entry.getConnection()));
            writer.write("--");
        }
        if (shouldPrintThread()) {
            writer.write(this.getThreadString(entry.getThread()));
            writer.write("--");
        }
        if (entry.getSourceClassName() != null) {
            writer.write(entry.getSourceClassName());
            writer.write("--");
        }
        if (entry.getSourceMethodName() != null) {
            writer.write(entry.getSourceMethodName());
            writer.write("--");
        }
        return writer.toString();
    }

    /**
     * Return the current session including the type and id.
     */
    protected String getSessionString(Session session) {
        // For bug 3422759 the session to log against should be the one in the
        // event, not the static one in the SessionLog, for there are many
        // sessions but only one SessionLog.
        if (session != null) {
            return ((org.eclipse.persistence.internal.sessions.AbstractSession)session).getLogSessionString();
        } else {
            return "";
        }
    }

    /**
     * Return the specified connection information.
     */
    protected String getConnectionString(Accessor connection) {
        // Bug 3630182 - if possible, print the actual connection's hashcode instead of just the accessor
        if (connection.getDatasourceConnection() == null){
            return CONNECTION_STRING + "(" + String.valueOf(System.identityHashCode(connection)) + ")";
        } else {
             return CONNECTION_STRING + "(" + String.valueOf(System.identityHashCode(connection.getDatasourceConnection())) + ")";
        }
    }

    /**
     * Return the specified thread information.
     */
    protected String getThreadString(Thread thread) {
        return THREAD_STRING + "(" + String.valueOf(thread) + ")";
    }

    /**
     * Print the prefix string representing EclipseLink logging
     */

    //Bug3135111  Prefix strings are not translated until the first time they are used.
    protected void printPrefixString(int level, String category) {
        try {
            switch (level) {
            case SEVERE:
                if (SEVERE_PREFIX == null) {
                    SEVERE_PREFIX = LoggingLocalization.buildMessage("toplink_severe");
                }
                this.getWriter().write(SEVERE_PREFIX);
                break;
            case WARNING:
                if (WARNING_PREFIX == null) {
                    WARNING_PREFIX = LoggingLocalization.buildMessage("toplink_warning");
                }
                this.getWriter().write(WARNING_PREFIX);
                break;
            case INFO:
                if (INFO_PREFIX == null) {
                    INFO_PREFIX = LoggingLocalization.buildMessage("toplink_info");
                }
                this.getWriter().write(INFO_PREFIX);
                break;
            case CONFIG:
                if (CONFIG_PREFIX == null) {
                    CONFIG_PREFIX = LoggingLocalization.buildMessage("toplink_config");
                }
                this.getWriter().write(CONFIG_PREFIX);
                break;
            case FINE:
                if (FINE_PREFIX == null) {
                    FINE_PREFIX = LoggingLocalization.buildMessage("toplink_fine");
                }
                this.getWriter().write(FINE_PREFIX);
                break;
            case FINER:
                if (FINER_PREFIX == null) {
                    FINER_PREFIX = LoggingLocalization.buildMessage("toplink_finer");
                }
                this.getWriter().write(FINER_PREFIX);
                break;
            case FINEST:
                if (FINEST_PREFIX == null) {
                    FINEST_PREFIX = LoggingLocalization.buildMessage("toplink_finest");
                }
                this.getWriter().write(FINEST_PREFIX);
                break;
            default:
                if (TOPLINK_PREFIX == null) {
                    TOPLINK_PREFIX = LoggingLocalization.buildMessage("toplink");
                }
                this.getWriter().write(TOPLINK_PREFIX);
            }
            if (category != null) {
                this.getWriter().write(category);
                this.getWriter().write(": ");
            }
        } catch (IOException exception) {
            throw ValidationException.logIOError(exception);
        }
    }


    /**
     * PUBLIC:
     * Set the date format to be used when printing a log entry date.
     * <p>Note: the JDK's <tt>java.text.SimpleDateFormat</tt> is <b>NOT</b> thread-safe.<br>
     * The user is <b>strongly</b> advised to consider using Apache Commons<br>
     * <tt>org.apache.commons.lang.time.FastDateFormat</tt> instead.</p>
     *
     * @param dateFormat java.text.DateFormat
     */
    public void setDateFormat(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    /**
     * Return the formatted message based on the information from the given SessionLogEntry.
     * The message will either be translated and formatted or formatted only depending
     * on if the shouldTranslate flag is set to true of false.
     */
    protected String formatMessage(SessionLogEntry entry) {
        String message = entry.getMessage();
        if (entry.shouldTranslate()) {
            if (entry.getLevel() > FINE) {
                message = LoggingLocalization.buildMessage(message, entry.getParameters());
            } else {
                message = TraceLocalization.buildMessage(message, entry.getParameters(), true);
            }
        } else {
            //Bug5976657, if there are entry parameters and the string "{0" contained in the message
            //body, we assume it needs to be formatted.
            if (entry.getParameters()!=null && entry.getParameters().length>0 && message.indexOf("{0") >= 0) {
                message = java.text.MessageFormat.format(message, entry.getParameters());
            }
        }
        return message;
    }

    /**
     * INTERNAL:
     * Translate the string value of the log level to the constant value.
     * If value is null or invalid use the default.
     */
    public static int translateStringToLoggingLevel(String loggingLevel) {
        final LogLevel logLevel = LogLevel.toValue(loggingLevel);
        return logLevel != null ? logLevel.getId() : LogLevel.INFO.getId();
    }

    /**
     * PUBLIC:
     * <p>
     * Log a throwable at FINER level.
     *
     * @param throwable a Throwable
     */
    @Override
    public void throwing(Throwable throwable) {
        if (shouldLog(FINER)) {
            SessionLogEntry entry = new SessionLogEntry(null, throwable);
            entry.setLevel(FINER);
            log(entry);
        }
    }

    /**
     * PUBLIC:
     * <p>
     * This method is called when a severe level message needs to be logged.
     * The message will be translated
     *
     * @param message  the message key
     */
    @Override
    public void severe(String message) {
        log(SEVERE, message, (Object[])null);
    }

    /**
     * PUBLIC:
     * <p>
     * This method is called when a warning level message needs to be logged.
     * The message will be translated
     *
     * @param message  the message key
     */
    @Override
    public void warning(String message) {
        log(WARNING, message, (Object[])null);
    }

    /**
     * PUBLIC:
     * <p>
     * This method is called when a info level message needs to be logged.
     * The message will be translated
     *
     * @param message  the message key
     */
    @Override
    public void info(String message) {
        log(INFO, message, (Object[])null);
    }

    /**
     * PUBLIC:
     * <p>
     * This method is called when a config level message needs to be logged.
     * The message will be translated
     *
     * @param message  the message key
     */
    @Override
    public void config(String message) {
        log(CONFIG, message, (Object[])null);
    }

    /**
     * PUBLIC:
     * <p>
     * This method is called when a fine level message needs to be logged.
     * The message will be translated
     *
     * @param message  the message key
     */
    @Override
    public void fine(String message) {
        log(FINE, message, (Object[])null);
    }

    /**
     * PUBLIC:
     * <p>
     * This method is called when a finer level message needs to be logged.
     * The message will be translated
     *
     * @param message  the message key
     */
    @Override
    public void finer(String message) {
        log(FINER, message, (Object[])null);
    }

    /**
     * PUBLIC:
     * <p>
     * This method is called when a finest level message needs to be logged.
     * The message will be translated
     *
     * @param message  the message key
     */
    @Override
    public void finest(String message) {
        log(FINEST, message, (Object[])null);
    }

    /**
     * PUBLIC:
     * <p>
     * Log a throwable with level.
     *
     * @param level  the log request level value
     * @param throwable  a Throwable
     */
    @Override
    public void logThrowable(int level, Throwable throwable) {
        // Must not create the log if not logging as is a performance issue.
        if (shouldLog(level)) {
            log(new SessionLogEntry(null, level, null, throwable));
        }
    }

    /**
     * PUBLIC:
     * <p>
     * Log a throwable with level.
     *
     * @param level  the log request level value
     * @param throwable  a Throwable
     */
    @Override
    public void logThrowable(int level, String category, Throwable throwable) {
        // Must not create the log if not logging as is a performance issue.
        if (shouldLog(level, category)) {
            log(new SessionLogEntry(null, level, category, throwable));
        }
    }

    /**
     * INTERNAL:
     * Check if the log level is set to off.
     */
    public boolean isOff() {
        return this.level == OFF;
    }

    /**
     * INTERNAL:
     * Each session owns its own session log because session is stored in the session log
     */
    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (Exception exception) {
            throw new AssertionError(exception);
        }
    }
    /**
     * INTERNAL:
     * Translate the string value of the log level to the constant value.
     * If value is null or invalid use the default.
     */
    public static String translateLoggingLevelToString(final int loggingLevel) {
        return LogLevel.toValue(loggingLevel, LogLevel.INFO).getName();
    }

}
