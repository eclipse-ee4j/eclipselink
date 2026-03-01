/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.logging;

import java.io.Writer;
import java.util.function.Supplier;

/**
 * SessionLog is the ever-so-simple interface used by EclipseLink to log generated messages and SQL. An implementor
 * of this interface can be passed to the EclipseLink session (via the #setSessionLog(SessionLog) method); and all
 * logging data will be passed through to the implementor via an instance of SessionLogEntry. This can be used
 * to supplement debugging; or the entries could be stored in a database instead of logged to {@linkplain System#out},
 * etc.
 * <p>
 * This class defines EclipseLink logging levels (that are used throughout EclipseLink code) with the following
 * integer values:
 * <table>
 * <caption>Logging levels</caption>
 * <tr><td>&nbsp;</td><td>{@linkplain #ALL}</td>    <td>&nbsp;</td><td>= {@value #ALL}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain #FINEST}</td> <td>&nbsp;</td><td>= {@value #FINEST}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain #FINER}</td>  <td>&nbsp;</td><td>= {@value #FINER}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain #FINE}</td>   <td>&nbsp;</td><td>= {@value #FINE}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain #CONFIG}</td> <td>&nbsp;</td><td>= {@value #CONFIG}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain #INFO}</td>   <td>&nbsp;</td><td>= {@value #INFO}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain #WARNING}</td><td>&nbsp;</td><td>= {@value #WARNING}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain #SEVERE}</td> <td>&nbsp;</td><td>= {@value #SEVERE}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain #OFF}</td>    <td>&nbsp;</td><td>= {@value #OFF}</td></tr>
 * </table>
 * <p>
 * In addition, EclipseLink categories used for logging name space are defined with the following String values:
 * <table>
 * <caption>Logging categories</caption>
 * <tr><td>&nbsp;</td><td>{@linkplain #CACHE}</td>         <td>&nbsp;</td><td>= {@value #CACHE}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain #CONNECTION}</td>    <td>&nbsp;</td><td>= {@value #CONNECTION}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain #DMS}</td>           <td>&nbsp;</td><td>= {@value #DMS}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain #EJB}</td>           <td>&nbsp;</td><td>= {@value #EJB}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain #EVENT}</td>         <td>&nbsp;</td><td>= {@value #EVENT}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain #DBWS}</td>          <td>&nbsp;</td><td>= {@value #DBWS}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain #JPARS}</td>         <td>&nbsp;</td><td>= {@value #JPARS}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain #METADATA}</td>      <td>&nbsp;</td><td>= {@value #METADATA} </td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain #METAMODEL}</td>     <td>&nbsp;</td><td>= {@value #METAMODEL}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain #MOXY}</td>          <td>&nbsp;</td><td>= {@value #MOXY}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain #PROCESSOR}</td>     <td>&nbsp;</td><td>= {@value #PROCESSOR}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain #PROPAGATION}</td>   <td>&nbsp;</td><td>= {@value #PROPAGATION}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain #PROPERTIES}</td>    <td>&nbsp;</td><td>= {@value #PROPERTIES}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain #QUERY}</td>         <td>&nbsp;</td><td>= {@value #QUERY}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain #SEQUENCING}</td>    <td>&nbsp;</td><td>= {@value #SEQUENCING}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain #SERVER}</td>        <td>&nbsp;</td><td>= {@value #SERVER}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain #SQL}</td>           <td>&nbsp;</td><td>= {@value #SQL}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain #THREAD}</td>        <td>&nbsp;</td><td>= {@value #THREAD}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain #TRANSACTION}</td>   <td>&nbsp;</td><td>= {@value #TRANSACTION}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain #WEAVER}</td>        <td>&nbsp;</td><td>= {@value #WEAVER}</td></tr>
 * </table>
 *
 * @see SessionLogEntry
 *
 * @since TOPLink/Java 3.0
 */
public interface SessionLog extends Cloneable {
    // EclipseLink log levels. They are mapped to java.util.logging.Level values.
    // Numeric constants can't be replaced with LogLevel.<level>.getId();
    /** {@code OFF} log level. */
    int OFF = 8;
    /** {@code OFF} log level name. */
    String OFF_LABEL = LogLevel.OFF.getName();

    //EL is not in a state to continue
    /** {@code SEVERE} log level. */
    int SEVERE = 7;
    /** {@code SEVERE} log level name. */
    String SEVERE_LABEL = LogLevel.SEVERE.getName();

    //Exceptions that don't force a stop
    /** {@code WARNING} log level. */
    int WARNING = 6;
    /** {@code WARNING} log level name. */
    String WARNING_LABEL = LogLevel.WARNING.getName();

    //Login and logout per server session with name
    /** {@code INFO} log level. */
    int INFO = 5;
    /** {@code INFO} log level name. */
    String INFO_LABEL = LogLevel.INFO.getName();

    //Configuration info
    /** {@code CONFIG} log level. */
    int CONFIG = 4;
    /** {@code CONFIG} log level name. */
    String CONFIG_LABEL = LogLevel.CONFIG.getName();

    //SQL
    /** {@code FINE} log level. */
    int FINE = 3;
    /** {@code FINE} log level name. */
    String FINE_LABEL = LogLevel.FINE.getName();

    //Previously logged under logMessage and stack trace of exceptions at WARNING level
    /** {@code FINER} log level. */
    int FINER = 2;
    /** {@code FINER} log level name. */
    String FINER_LABEL = LogLevel.FINER.getName();

    //Previously logged under logDebug
    /** {@code FINEST} log level. */
    int FINEST = 1;
    /** {@code FINEST} log level name. */
    String FINEST_LABEL = LogLevel.FINEST.getName();
    /** {@code ALL} log level. */
    int ALL = 0;
    /** {@code ALL} log level name. */
    String ALL_LABEL = LogLevel.ALL.getName();

    //EclipseLink categories used for logging category.
    /** SQL logging category. */
    String SQL = "sql";
    /** Transaction logging category. */
    String TRANSACTION = "transaction";
    /** Event logging category. */
    String EVENT = "event";
    /** Connection logging category. */
    String CONNECTION = "connection";
    /** Query logging category. */
    String QUERY = "query";
    /** Cache logging category. */
    String CACHE = "cache";
    /** Propagation logging category. */
    String PROPAGATION = "propagation";
    /** Sequencing logging category. */
    String SEQUENCING = "sequencing";
    /** JPA logging category. */
    String JPA = "jpa";
    /** EJB logging category. */
    String EJB = "ejb";
    /** DMS profiler name space. */
    String DMS = "dms";
    /** Metadata logging category. */
    String METADATA = "metadata";
    /** Monitoring logging category. */
    String MONITORING = "monitoring";
    /** Miscellaneous logging category. */
    String MISC = "misc";
    /** MOXY logging category. */
    String MOXY = "moxy";
    /** Metamodel logging category. */
    String METAMODEL = "metamodel";
    /** Weaving logging category. */
    String WEAVER = "weaver";
    /** Properties logging category. */
    String PROPERTIES = "properties";
    /** Server logging category. */
    String SERVER = "server";
    /** DDL logging category. */
    String DDL = "ddl";
    /** DBWS logging category. */
    String DBWS = "dbws";
    /** JPA RS logging category. */
    String JPARS = "jpars";
    /** ModelGen logging category. */
    String PROCESSOR = "processor";
    /** Thread logging category. */
    String THREAD = "thread";

    /** An array of all logging categories. */
    String[] loggerCatagories = new String[] {
        SQL,
        TRANSACTION,
        EVENT,
        CONNECTION,
        QUERY,
        CACHE,
        PROPAGATION,
        SEQUENCING,
        JPA,
        DBWS,
        JPARS,
        EJB,
        DMS,
        METADATA,
        MONITORING,
        MOXY,
        METAMODEL,
        WEAVER,
        PROPERTIES,
        SERVER,
        DDL,
        PROCESSOR,
        THREAD
    };

    /**
     * Log a message stored in {@linkplain SessionLogEntry}.
     * Write message content to a log writer, such as {@linkplain System#out} or a file.
     * EclipseLink will call this method whenever something.
     *
     * @param entry holds all the information to be written to the log
     */
    void log(SessionLogEntry entry);

    /**
     * By default the stack trace is logged for SEVERE all the time and at FINER level for WARNING or less,
     * this can be turned off.
     */
    boolean shouldLogExceptionStackTrace();

    /**
     * By default the date is always printed, this can be turned off.
     */
    boolean shouldPrintDate();

    /**
     * By default the thread is logged at FINE or less level, this can be turned off.
     */
    boolean shouldPrintThread();

    /**
     * Return whether bind parameters should be displayed when logging SQL, default is true.
     */
    boolean shouldDisplayData();

    /**
     * By default the connection is always printed whenever available, this can be turned off.
     */
    boolean shouldPrintConnection();

    /**
     * By default the Session is always printed whenever available, this can be turned off.
     */
    boolean shouldPrintSession();

    /**
     * Set whether bind parameters should be displayed when logging SQL.
     */
    void setShouldDisplayData(Boolean shouldDisplayData);

    /**
     * By default stack trace is logged for SEVERE all the time and at FINER level for WARNING or less.
     * This can be turned off.
     */
    void setShouldLogExceptionStackTrace(boolean flag);

    /**
     * By default date is printed, this can be turned off.
     */
    void setShouldPrintDate(boolean flag);

    /**
     * By default the thread is logged at FINE or less level, this can be turned off.
     */
    void setShouldPrintThread(boolean flag);

    /**
     * By default the connection is always printed whenever available, this can be turned off.
     */
    void setShouldPrintConnection(boolean flag);

    /**
     * By default the Session is always printed whenever available, this can be turned off.
     */
    void setShouldPrintSession(boolean flag);

    /**
     * Returns the writer to which logged messages and SQL are written.
     * If not set, this reference typically defaults to a writer on {@linkplain System#out}.
     * To enable logging, {@code logMessages} must be turned on in the session.
     *
     * @return the writer used for logging messages and SQL
     */
    Writer getWriter();

    /**
     * Sets the writer to which logged messages and SQL are written.
     * If not set, this reference typically defaults to a writer on {@linkplain System#out}.
     * To enable logging, {@code logMessages} must be turned on in the session.
     *
     * @param log the writer to be used for logging messages and SQL
     */
    void setWriter(Writer log);

    /**
     * Returns the log level.
     * Used when session is not available.
     *
     * @return the current log level
     */
    int getLevel();

    /**
     * Returns the log level name.
     *
     * @return the current log level name
     */
    String getLevelString();

    /**
     * Returns the log level for provided category.
     * See {@linkplain SessionLog class description} for the list of available categories.
     *
     * @param category the log category
     * @return the current log level
     */
    int getLevel(String category);

    /**
     * Sets the log level.
     * Used when session is not available. See {@linkplain SessionLog class description} for the list
     * of available levels.
     *
     * @param level the log level
     */
    void setLevel(int level);

    /**
     * Sets the log level for provided category.
     * See {@linkplain SessionLog class description} for the list of available levels and categories.
     *
     * @param level the log level
     * @param category the log category
     */
    void setLevel(int level, String category);

    /**
     * Whether a message of the given level would actually be logged.
     * Used when session is not available. See {@linkplain SessionLog class description} for the list
     * of available levels.
     *
     * @param level the log level
     * @return value of {@code true} when message would be logged or {@code false} otherwise
     */
    boolean shouldLog(int level);

    /**
     * Whether a message of the given level would actually be logged for provided category.
     * See {@linkplain SessionLog class description} for the list of available levels and categories.
     *
     * @param level the log level
     * @param category the log category
     * @return value of {@code true} when message would be logged or {@code false} otherwise
     */

    boolean shouldLog(int level, String category);

    /**
     * Log a message with message content supplier.
     * See {@linkplain SessionLog class description} for the list of available levels.
     *
     * @param level the log level
     * @param messageSupplier the message string supplier
     */
    void log(int level, Supplier<String> messageSupplier);

    /**
     * Log a message with message content supplier for provided category.
     * See {@linkplain SessionLog class description} for the list of available levels and categories.
     *
     * @param level the log level
     * @param category the log category
     * @param messageSupplier the message string supplier
     */
    void log(int level, String category, Supplier<String> messageSupplier);

    /**
     * Log a message.
     * <p>
     * The message won't be translated. This method is intended for external use when logging messages
     * are wanted within the EclipseLink output. See {@linkplain SessionLog class description} for the list
     * of available levels.
     *
     * @param level the log level
     * @param message the message string
     */
    void log(int level, String message);

    /**
     * Log a message with one parameter.
     * The message will be translated. See {@linkplain SessionLog class description} for the list
     * of available levels.
     *
     * @param level the log level
     * @param message the message string
     * @param param the message parameter
     */
    void log(int level, String message, Object param);

    /**
     * Log a message with one parameter for provided category.
     * The message will be translated. See {@linkplain SessionLog class description} for the list
     * of available levels and categories.
     *
     * @param level the log level
     * @param category the log category
     * @param message the message string
     * @param param the message parameter
     */
    void log(int level, String category, String message, Object param);

    /**
     * Log a message with two parameters.
     * The message will be translated. See {@linkplain SessionLog class description} for the list
     * of available levels.
     *
     * @param level the log level
     * @param message the message string
     * @param param1 the 1st message parameter
     * @param param2 the 2nd message parameter
     */
    void log(int level, String message, Object param1, Object param2);

    /**
     * Log a message with two parameters for provided category.
     * The message will be translated. See {@linkplain SessionLog class description} for the list
     * of available levels and categories.
     *
     * @param level the log level
     * @param category the log category
     * @param message the message string
     * @param param1 the 1st message parameter
     * @param param2 the 2nd message parameter
     */
    void log(int level, String category, String message, Object param1, Object param2);

     /**
     * Log a message with three parameters.
     * The message will be translated. See {@linkplain SessionLog class description} for the list
     * of available levels.
     *
     * @param level the log level
     * @param message the message string
     * @param param1 the 1st message parameter
     * @param param2 the 2nd message parameter
     * @param param3 the 3rd message parameter
     */
    void log(int level, String message, Object param1, Object param2, Object param3);

    /**
     * Log a message with three parameters for provided category.
     * The message will be translated. See {@linkplain SessionLog class description} for the list
     * of available levels and categories.
     *
     * @param level the log level
     * @param category the log category
     * @param message the message string
     * @param param1 the 1st message parameter
     * @param param2 the 2nd message parameter
     * @param param3 the 3rd message parameter
     */
    void log(int level, String category, String message, Object param1, Object param2, Object param3);

    /**
     * Log a message with four parameters.
     * The message will be translated. See {@linkplain SessionLog class description} for the list
     * of available levels.
     *
     * @param level the log level
     * @param message the message string
     * @param param1 the 1st message parameter
     * @param param2 the 2nd message parameter
     * @param param3 the 3rd message parameter
     * @param param4 the 4th message parameter
     */
    void log(int level, String message, Object param1, Object param2, Object param3, Object param4);

    /**
     * Log a message with four parameters for provided category.
     * The message will be translated. See {@linkplain SessionLog class description} for the list
     * of available levels and categories.
     *
     * @param level the log level
     * @param category the log category
     * @param message the message string
     * @param param1 the 1st message parameter
     * @param param2 the 2nd message parameter
     * @param param3 the 3rd message parameter
     * @param param4 the 4th message parameter
     */
    void log(int level, String category, String message, Object param1, Object param2, Object param3, Object param4);

    /**
     * Log a message with parameters array.
     * The message will be translated. See {@linkplain SessionLog class description} for the list
     * of available levels.
     *
     * @param level the log level
     * @param message the message string
     * @param parameters array of the message parameters
     */
    void log(int level, String message, Object[] parameters);

    /**
     * Log a message with parameters array for provided category.
     * The message will be translated. See {@linkplain SessionLog class description} for the list
     * of available levels and categories.
     *
     * @param level the log level
     * @param category the log category
     * @param message the message string
     * @param parameters array of the message parameters
     */
    void log(int level, String category, String message, Object[] parameters);

    /**
     * Log a message with parameters array and translation flag.
     * The message will be translated when {@code shouldTranslate} is set to {@code true}.
     * See {@linkplain SessionLog class description} for the list of available levels.
     *
     * @param level the log level
     * @param message the message string
     * @param parameters array of the message parameters
     * @param shouldTranslate value of {@code true} if the message needs to be translated or {@code false} otherwise
     */
    @Deprecated(forRemoval=true, since="4.0.9")
    void log(int level, String message, Object[] parameters, boolean shouldTranslate);

    /**
     * Log a message with parameters array and translation flag for provided category.
     * The message will be translated when {@code shouldTranslate} is set to {@code true}.
     * See {@linkplain SessionLog class description} for the list of available levels and categories.
     *
     * @param level the log level
     * @param category the log category
     * @param message the message string
     * @param parameters array of the message parameters
     * @param shouldTranslate value of {@code true} if the message needs to be translated or {@code false} otherwise
     */
    @Deprecated(forRemoval=true, since="4.0.9")
    void log(int level, String category, String message, Object[] parameters, boolean shouldTranslate);

    /**
     * Log a {@linkplain Throwable} at {@linkplain #FINER} level.
     *
     * @param throwable the {@linkplain Throwable}
     */
    void throwing(Throwable throwable);

    /**
     * Log a {@linkplain #SEVERE} level message.
     * The message will be translated.
     *
     * @param message the message key
     */
    void severe(String message);

    /**
     * Log a {@linkplain #SEVERE} level message.
     * Logs a message with message content supplier.
     *
     * @param messageSupplier the message string supplier
     */
    void severe(Supplier<String> messageSupplier);

    /**
     * Log a {@linkplain #WARNING} level message.
     * The message will be translated.
     *
     * @param message the message key
     */
    void warning(String message);

    /**
     * Log a {@linkplain #WARNING} level message.
     * Logs a message with message content supplier.
     *
     * @param messageSupplier the message string supplier
     */
    void warning(Supplier<String> messageSupplier);

    /**
     * Log an {@linkplain #INFO} level message.
     * The message will be translated.
     *
     * @param message the message key
     */
    void info(String message);

    /**
     * Log an {@linkplain #INFO} level message.
     * Logs a message with message content supplier.
     *
     * @param messageSupplier the message string supplier
     */
    void info(Supplier<String> messageSupplier);

    /**
     * Log a {@linkplain #CONFIG} level message.
     * The message will be translated.
     *
     * @param message the message key
     */
    void config(String message);

    /**
     * Log a {@linkplain #CONFIG} level message.
     * Logs a message with message content supplier.
     *
     * @param messageSupplier the message string supplier
     */
    void config(Supplier<String> messageSupplier);

    /**
     * Log a {@linkplain #FINE} level message.
     * The message will be translated.
     *
     * @param message the message key
     */
    void fine(String message);

    /**
     * Log a {@linkplain #FINE} level message.
     * Logs a message with message content supplier.
     *
     * @param messageSupplier the message string supplier
     */
    void fine(Supplier<String> messageSupplier);

    /**
     * Log a {@linkplain #FINER} level message.
     * The message will be translated.
     *
     * @param message the message key
     */
    void finer(String message);

    /**
     * Log a {@linkplain #FINER} level message.
     * Logs a message with message content supplier.
     *
     * @param messageSupplier the message string supplier
     */
    void finer(Supplier<String> messageSupplier);

    /**
     * Log a {@linkplain #FINEST} level message.
     * The message will be translated.
     *
     * @param message the message key
     */
    void finest(String message);

    /**
     * Log a {@linkplain #FINEST} level message.
     * Logs a message with message content supplier.
     *
     * @param messageSupplier the message string supplier
     */
    void finest(Supplier<String> messageSupplier);

    /**
     * Log a {@linkplain Throwable}.
     * See {@linkplain SessionLog class description} for the list of available levels.
     *
     * @param level the log level
     * @param throwable the {@linkplain Throwable}
     */
    void logThrowable(int level, Throwable throwable);

    /**
     * Return the name of the session.
     *
     * @return the name of the session
     */
    String getSessionName();

    /**
     * Set the name of the session.
     *
     * @param sessionName the name of the session
     */
    void setSessionName(String sessionName);

    /**
     * Log a {@linkplain Throwable} for provided category.
     * See {@linkplain SessionLog class description} for the list of available levels and categories.
     *
     * @param level the log level
     * @param category the log category
     * @param throwable the {@linkplain Throwable}
     */
    void logThrowable(int level, String category, Throwable throwable);

    /**
     * Clone the log.
     *
     * @return the cloned log
     */
    Object clone();
}
