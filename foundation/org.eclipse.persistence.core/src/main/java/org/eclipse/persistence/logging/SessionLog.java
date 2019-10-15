/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.sessions.Session;

/**
 * SessionLog is the ever-so-simple interface used by
 * EclipseLink to log generated messages and SQL. An implementor of
 * this interface can be passed to the EclipseLink session
 * (via the #setSessionLog(SessionLog) method); and
 * all logging data will be passed through to the implementor
 * via an instance of SessionLogEntry. This can be used
 * to supplement debugging; or the entries could be stored
 * in a database instead of logged to System.out, etc.
 * <p>
 * This class defines EclipseLink logging levels (that are used throughout EclipseLink code) with the following integer values:
 * <table>
 * <caption>Logging levels</caption>
 * <tr><td>&nbsp;</td><td>ALL</td>    <td>&nbsp;</td><td>= {@value #ALL}</td></tr>
 * <tr><td>&nbsp;</td><td>FINEST</td> <td>&nbsp;</td><td>= {@value #FINEST}</td></tr>
 * <tr><td>&nbsp;</td><td>FINER</td>  <td>&nbsp;</td><td>= {@value #FINER}</td></tr>
 * <tr><td>&nbsp;</td><td>FINE</td>   <td>&nbsp;</td><td>= {@value #FINE}</td></tr>
 * <tr><td>&nbsp;</td><td>CONFIG</td> <td>&nbsp;</td><td>= {@value #CONFIG}</td></tr>
 * <tr><td>&nbsp;</td><td>INFO</td>   <td>&nbsp;</td><td>= {@value #INFO}</td></tr>
 * <tr><td>&nbsp;</td><td>WARNING</td><td>&nbsp;</td><td>= {@value #WARNING}</td></tr>
 * <tr><td>&nbsp;</td><td>SEVERE</td> <td>&nbsp;</td><td>= {@value #SEVERE}</td></tr>
 * <tr><td>&nbsp;</td><td>OFF</td>    <td>&nbsp;</td><td>= {@value #OFF}</td></tr>
 * </table>
 * <p>
 * In addition, EclipseLink categories used for logging name space are defined with the following String values:
 * <table>
 * <caption>Logging categories</caption>
 * <tr><td>&nbsp;</td><td>{@link #CACHE}</td>         <td>&nbsp;</td><td>= {@value #CACHE}</td></tr>
 * <tr><td>&nbsp;</td><td>{@link #CONNECTION}</td>    <td>&nbsp;</td><td>= {@value #CONNECTION}</td></tr>
 * <tr><td>&nbsp;</td><td>{@link #DMS}</td>           <td>&nbsp;</td><td>= {@value #DMS}</td></tr>
 * <tr><td>&nbsp;</td><td>{@link #EJB}</td>           <td>&nbsp;</td><td>= {@value #EJB}</td></tr>
 * <tr><td>&nbsp;</td><td>{@link #EVENT}</td>         <td>&nbsp;</td><td>= {@value #EVENT}</td></tr>
 * <tr><td>&nbsp;</td><td>{@link #DBWS}</td>          <td>&nbsp;</td><td>= {@value #DBWS}</td></tr>
 * <tr><td>&nbsp;</td><td>{@link #JPARS}</td>         <td>&nbsp;</td><td>= {@value #JPARS}</td></tr>
 * <tr><td>&nbsp;</td><td>{@link #METADATA}</td>      <td>&nbsp;</td><td>= {@value #METADATA} </td></tr>
 * <tr><td>&nbsp;</td><td>{@link #METAMODEL}</td>     <td>&nbsp;</td><td>= {@value #METAMODEL}</td></tr>
 * <tr><td>&nbsp;</td><td>{@link #MOXY}</td>          <td>&nbsp;</td><td>= {@value #MOXY}</td></tr>
 * <tr><td>&nbsp;</td><td>{@link #PROCESSOR}</td>     <td>&nbsp;</td><td>= {@value #PROCESSOR}</td></tr>
 * <tr><td>&nbsp;</td><td>{@link #PROPAGATION}</td>   <td>&nbsp;</td><td>= {@value #PROPAGATION}</td></tr>
 * <tr><td>&nbsp;</td><td>{@link #PROPERTIES}</td>    <td>&nbsp;</td><td>= {@value #PROPERTIES}</td></tr>
 * <tr><td>&nbsp;</td><td>{@link #QUERY}</td>         <td>&nbsp;</td><td>= {@value #QUERY}</td></tr>
 * <tr><td>&nbsp;</td><td>{@link #SEQUENCING}</td>    <td>&nbsp;</td><td>= {@value #SEQUENCING}</td></tr>
 * <tr><td>&nbsp;</td><td>{@link #SERVER}</td>        <td>&nbsp;</td><td>= {@value #SERVER}</td></tr>
 * <tr><td>&nbsp;</td><td>{@link #SQL}</td>           <td>&nbsp;</td><td>= {@value #SQL}</td></tr>
 * <tr><td>&nbsp;</td><td>{@link #TRANSACTION}</td>   <td>&nbsp;</td><td>= {@value #TRANSACTION}</td></tr>
 * <tr><td>&nbsp;</td><td>{@link #WEAVER}</td>        <td>&nbsp;</td><td>= {@value #WEAVER}</td></tr>
 * </table>
 *
 * @see AbstractSessionLog
 * @see SessionLogEntry
 * @see Session
 *
 * @since TOPLink/Java 3.0
 */
public interface SessionLog extends Cloneable {
    // EclipseLink log levels. They are mapped to java.util.logging.Level values.
    // Numeric constants can't be replaced with LogLevel.<level>.getId();
    int OFF = 8;
    String OFF_LABEL = LogLevel.OFF.getName();

    //EL is not in a state to continue
    int SEVERE = 7;
    String SEVERE_LABEL = LogLevel.SEVERE.getName();

    //Exceptions that don't force a stop
    int WARNING = 6;
    String WARNING_LABEL = LogLevel.WARNING.getName();

    //Login and logout per server session with name
    int INFO = 5;
    String INFO_LABEL = LogLevel.INFO.getName();

    //Configuration info
    int CONFIG = 4;
    String CONFIG_LABEL = LogLevel.CONFIG.getName();

    //SQL
    int FINE = 3;
    String FINE_LABEL = LogLevel.FINE.getName();

    //Previously logged under logMessage and stack trace of exceptions at WARNING level
    int FINER = 2;
    String FINER_LABEL = LogLevel.FINER.getName();

    //Previously logged under logDebug
    int FINEST = 1;
    String FINEST_LABEL = LogLevel.FINEST.getName();
    int ALL = 0;
    String ALL_LABEL = LogLevel.ALL.getName();

    //EclipseLink categories used for logging name space.
    String SQL = "sql";
    String TRANSACTION = "transaction";
    String EVENT = "event";
    String CONNECTION = "connection";
    String QUERY = "query";
    String CACHE = "cache";
    String PROPAGATION = "propagation";
    String SEQUENCING = "sequencing";
    String JPA = "jpa";
    String EJB = "ejb";
    String DMS = "dms";
    String METADATA = "metadata";
    String MONITORING = "monitoring";
    String MISC = "misc";
    String MOXY = "moxy";

    String METAMODEL = "metamodel";
    String WEAVER = "weaver";
    String PROPERTIES = "properties";
    String SERVER = "server";
    String DDL = "ddl";
    String DBWS = "dbws";
    String JPARS = "jpars";
    /** ModelGen logging name space. */
    String PROCESSOR = "processor";

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
        PROCESSOR
    };

    /**
     * PUBLIC:
     * EclipseLink will call this method whenever something
     * needs to be logged (messages, SQL, etc.).
     * All the pertinent information will be contained in
     * the specified entry.
     *
     * @param entry org.eclipse.persistence.sessions.LogEntry
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
     * PUBLIC:
     * Return the writer to which an accessor writes logged messages and SQL.
     * If not set, this reference usually defaults to a writer on System.out.
     * To enable logging, logMessages must be turned on in the session.
     */
    Writer getWriter();

    /**
     * PUBLIC:
     * Set the writer to which an accessor writes logged messages and SQL.
     * If not set, this reference usually defaults to a writer on System.out.
     * To enable logging, logMessages() is used on the session.
     */
    void setWriter(Writer log);

    /**
     * PUBLIC:
     * Return the log level.  Used when session is not available.
     * <p>
     * The EclipseLink logging levels returned correspond to:<br>
     * <table>
     * <caption>Logging levels</caption>
     * <tr><td>{@link #ALL}</td>    <td>&nbsp;</td><td>= {@value #ALL}</td>
     * <tr><td>{@link #FINEST}</td> <td>&nbsp;</td><td>= {@value #FINEST}</td>
     * <tr><td>{@link #FINER}</td>  <td>&nbsp;</td><td>= {@value #FINER}</td>
     * <tr><td>{@link #FINE}</td>   <td>&nbsp;</td><td>= {@value #FINE}</td>
     * <tr><td>{@link #CONFIG}</td> <td>&nbsp;</td><td>= {@value #CONFIG}</td>
     * <tr><td>{@link #INFO}</td>   <td>&nbsp;</td><td>= {@value #INFO}</td>
     * <tr><td>{@link #WARNING}</td><td>&nbsp;</td><td>= {@value #WARNING}</td>
     * <tr><td>{@link #SEVERE}</td> <td>&nbsp;</td><td>= {@value #SEVERE}</td>
     * <tr><td>{@link #OFF}</td>    <td>&nbsp;</td><td>= {@value #OFF}</td>
     * </table>
     */
    int getLevel();

    /**
     * PUBLIC:
     * <p>
     * Return the log level as a string value.
     */
    String getLevelString();

    /**
     * PUBLIC:
     * Return the log level; category is only needed where name space
     * is available.
     * <p>
     * The EclipseLink logging levels returned correspond to:<br>
     * <table>
     * <caption>Logging levels</caption>
     * <tr><td>{@link #ALL}</td>    <td>&nbsp;</td><td>= {@value #ALL}</td>
     * <tr><td>{@link #FINEST}</td> <td>&nbsp;</td><td>= {@value #FINEST}</td>
     * <tr><td>{@link #FINER}</td>  <td>&nbsp;</td><td>= {@value #FINER}</td>
     * <tr><td>{@link #FINE}</td>   <td>&nbsp;</td><td>= {@value #FINE}</td>
     * <tr><td>{@link #CONFIG}</td> <td>&nbsp;</td><td>= {@value #CONFIG}</td>
     * <tr><td>{@link #INFO}</td>   <td>&nbsp;</td><td>= {@value #INFO}</td>
     * <tr><td>{@link #WARNING}</td><td>&nbsp;</td><td>= {@value #WARNING}</td>
     * <tr><td>{@link #SEVERE}</td> <td>&nbsp;</td><td>= {@value #SEVERE}</td>
     * <tr><td>{@link #OFF}</td>    <td>&nbsp;</td><td>= {@value #OFF}</td>
     * </table>
     * <p>
     * The EclipseLink categories for the logging name space are:<br>
     * <table>
     * <caption>Logging categories</caption>
     * <tr><td>&nbsp;</td><td>{@link #CACHE}</td>           <td>&nbsp;</td><td>= {@value #CACHE}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #CONNECTION}</td>      <td>&nbsp;</td><td>= {@value #CONNECTION}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #DMS}</td>             <td>&nbsp;</td><td>= {@value #DMS}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #EJB}</td>             <td>&nbsp;</td><td>= {@value #EJB}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #EVENT}</td>           <td>&nbsp;</td><td>= {@value #EVENT}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #DBWS}</td>            <td>&nbsp;</td><td>= {@value #DBWS}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #JPARS}</td>           <td>&nbsp;</td><td>= {@value #JPARS}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #METAMODEL}</td>       <td>&nbsp;</td><td>= {@value #METAMODEL}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #MOXY}</td>            <td>&nbsp;</td><td>= {@value #MOXY}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #PROCESSOR}</td>       <td>&nbsp;</td><td>= {@value #PROCESSOR}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #PROPAGATION}</td>     <td>&nbsp;</td><td>= {@value #PROPAGATION}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #PROPERTIES}</td>      <td>&nbsp;</td><td>= {@value #PROPERTIES}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #QUERY}</td>           <td>&nbsp;</td><td>= {@value #QUERY}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #SEQUENCING}</td>      <td>&nbsp;</td><td>= {@value #SEQUENCING}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #SERVER}</td>          <td>&nbsp;</td><td>= {@value #SERVER}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #SQL}</td>             <td>&nbsp;</td><td>= {@value #SQL}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #TRANSACTION}</td>     <td>&nbsp;</td><td>= {@value #TRANSACTION}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #WEAVER}</td>          <td>&nbsp;</td><td>= {@value #WEAVER}</td></tr>
     * </table>
     */
    int getLevel(String category);

    /**
     * PUBLIC:
     * Set the log level.  Used when session is not available.
     * <p>
     * The EclipseLink logging levels available are:<br>
     * <table>
     * <caption>Logging levels</caption>
     * <tr><td>{@link #ALL}</td>    <td>&nbsp;</td><td>= {@value #ALL}</td>
     * <tr><td>{@link #FINEST}</td> <td>&nbsp;</td><td>= {@value #FINEST}</td>
     * <tr><td>{@link #FINER}</td>  <td>&nbsp;</td><td>= {@value #FINER}</td>
     * <tr><td>{@link #FINE}</td>   <td>&nbsp;</td><td>= {@value #FINE}</td>
     * <tr><td>{@link #CONFIG}</td> <td>&nbsp;</td><td>= {@value #CONFIG}</td>
     * <tr><td>{@link #INFO}</td>   <td>&nbsp;</td><td>= {@value #INFO}</td>
     * <tr><td>{@link #WARNING}</td><td>&nbsp;</td><td>= {@value #WARNING}</td>
     * <tr><td>{@link #SEVERE}</td> <td>&nbsp;</td><td>= {@value #SEVERE}</td>
     * <tr><td>{@link #OFF}</td>    <td>&nbsp;</td><td>= {@value #OFF}</td>
     * </table>
     */
    void setLevel(int level);

    /**
     * PUBLIC:
     * Set the log level.  Category is only needed where name space
     * is available.
     * <p>
     * The EclipseLink logging levels available are:<br>
     * <table>
     * <caption>Logging levels</caption>
     * <tr><td>{@link #ALL}</td>    <td>&nbsp;</td><td>= {@value #ALL}</td>
     * <tr><td>{@link #FINEST}</td> <td>&nbsp;</td><td>= {@value #FINEST}</td>
     * <tr><td>{@link #FINER}</td>  <td>&nbsp;</td><td>= {@value #FINER}</td>
     * <tr><td>{@link #FINE}</td>   <td>&nbsp;</td><td>= {@value #FINE}</td>
     * <tr><td>{@link #CONFIG}</td> <td>&nbsp;</td><td>= {@value #CONFIG}</td>
     * <tr><td>{@link #INFO}</td>   <td>&nbsp;</td><td>= {@value #INFO}</td>
     * <tr><td>{@link #WARNING}</td><td>&nbsp;</td><td>= {@value #WARNING}</td>
     * <tr><td>{@link #SEVERE}</td> <td>&nbsp;</td><td>= {@value #SEVERE}</td>
     * <tr><td>{@link #OFF}</td>    <td>&nbsp;</td><td>= {@value #OFF}</td>
     * </table>
     * <p>
     * The EclipseLink categories for the logging name space are:<br>
     * <table>
     * <caption>Logging categories</caption>
     * <tr><td>&nbsp;</td><td>{@link #CACHE}</td>           <td>&nbsp;</td><td>= {@value #CACHE}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #CONNECTION}</td>      <td>&nbsp;</td><td>= {@value #CONNECTION}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #DMS}</td>             <td>&nbsp;</td><td>= {@value #DMS}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #EJB}</td>             <td>&nbsp;</td><td>= {@value #EJB}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #EVENT}</td>           <td>&nbsp;</td><td>= {@value #EVENT}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #DBWS}</td>            <td>&nbsp;</td><td>= {@value #DBWS}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #JPARS}</td>           <td>&nbsp;</td><td>= {@value #JPARS}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #METAMODEL}</td>       <td>&nbsp;</td><td>= {@value #METAMODEL}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #MOXY}</td>            <td>&nbsp;</td><td>= {@value #MOXY}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #PROCESSOR}</td>       <td>&nbsp;</td><td>= {@value #PROCESSOR}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #PROPAGATION}</td>     <td>&nbsp;</td><td>= {@value #PROPAGATION}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #PROPERTIES}</td>      <td>&nbsp;</td><td>= {@value #PROPERTIES}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #QUERY}</td>           <td>&nbsp;</td><td>= {@value #QUERY}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #SEQUENCING}</td>      <td>&nbsp;</td><td>= {@value #SEQUENCING}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #SERVER}</td>          <td>&nbsp;</td><td>= {@value #SERVER}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #SQL}</td>             <td>&nbsp;</td><td>= {@value #SQL}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #TRANSACTION}</td>     <td>&nbsp;</td><td>= {@value #TRANSACTION}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #WEAVER}</td>          <td>&nbsp;</td><td>= {@value #WEAVER}</td></tr>
     * </table>
     */
    void setLevel(int level, String category);

    /**
     * PUBLIC:
     * Check if a message of the given level would actually be logged.
     * Used when session is not available.
     * <p>
     * The EclipseLink logging levels available are:
     * <table>
     * <caption>Logging levels</caption>
     * <tr><td>{@link #ALL}</td>    <td>&nbsp;</td><td>= {@value #ALL}</td>
     * <tr><td>{@link #FINEST}</td> <td>&nbsp;</td><td>= {@value #FINEST}</td>
     * <tr><td>{@link #FINER}</td>  <td>&nbsp;</td><td>= {@value #FINER}</td>
     * <tr><td>{@link #FINE}</td>   <td>&nbsp;</td><td>= {@value #FINE}</td>
     * <tr><td>{@link #CONFIG}</td> <td>&nbsp;</td><td>= {@value #CONFIG}</td>
     * <tr><td>{@link #INFO}</td>   <td>&nbsp;</td><td>= {@value #INFO}</td>
     * <tr><td>{@link #WARNING}</td><td>&nbsp;</td><td>= {@value #WARNING}</td>
     * <tr><td>{@link #SEVERE}</td> <td>&nbsp;</td><td>= {@value #SEVERE}</td>
     * <tr><td>{@link #OFF}</td>    <td>&nbsp;</td><td>= {@value #OFF}</td>
     * </table>
     */
    boolean shouldLog(int level);

    /**
     * PUBLIC:
     * Check if a message of the given level would actually be logged.
     * Category is only needed where name space is available.
     * <p>
     * The EclipseLink logging levels available are:<br>
     * <table>
     * <caption>Logging levels</caption>
     * <tr><td>{@link #ALL}</td>    <td>&nbsp;</td><td>= {@value #ALL}</td>
     * <tr><td>{@link #FINEST}</td> <td>&nbsp;</td><td>= {@value #FINEST}</td>
     * <tr><td>{@link #FINER}</td>  <td>&nbsp;</td><td>= {@value #FINER}</td>
     * <tr><td>{@link #FINE}</td>   <td>&nbsp;</td><td>= {@value #FINE}</td>
     * <tr><td>{@link #CONFIG}</td> <td>&nbsp;</td><td>= {@value #CONFIG}</td>
     * <tr><td>{@link #INFO}</td>   <td>&nbsp;</td><td>= {@value #INFO}</td>
     * <tr><td>{@link #WARNING}</td><td>&nbsp;</td><td>= {@value #WARNING}</td>
     * <tr><td>{@link #SEVERE}</td> <td>&nbsp;</td><td>= {@value #SEVERE}</td>
     * <tr><td>{@link #OFF}</td>    <td>&nbsp;</td><td>= {@value #OFF}</td>
     * </table>
     * <p>
     * The EclipseLink categories for the logging name space are:<br>
     * <table>
     * <caption>Logging categories</caption>
     * <tr><td>&nbsp;</td><td>{@link #CACHE}</td>           <td>&nbsp;</td><td>= {@value #CACHE}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #CONNECTION}</td>      <td>&nbsp;</td><td>= {@value #CONNECTION}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #DMS}</td>             <td>&nbsp;</td><td>= {@value #DMS}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #EJB}</td>             <td>&nbsp;</td><td>= {@value #EJB}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #EVENT}</td>           <td>&nbsp;</td><td>= {@value #EVENT}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #DBWS}</td>            <td>&nbsp;</td><td>= {@value #DBWS}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #JPARS}</td>           <td>&nbsp;</td><td>= {@value #JPARS}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #METAMODEL}</td>       <td>&nbsp;</td><td>= {@value #METAMODEL}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #MOXY}</td>            <td>&nbsp;</td><td>= {@value #MOXY}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #PROCESSOR}</td>       <td>&nbsp;</td><td>= {@value #PROCESSOR}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #PROPAGATION}</td>     <td>&nbsp;</td><td>= {@value #PROPAGATION}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #PROPERTIES}</td>      <td>&nbsp;</td><td>= {@value #PROPERTIES}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #QUERY}</td>           <td>&nbsp;</td><td>= {@value #QUERY}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #SEQUENCING}</td>      <td>&nbsp;</td><td>= {@value #SEQUENCING}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #SERVER}</td>          <td>&nbsp;</td><td>= {@value #SERVER}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #SQL}</td>             <td>&nbsp;</td><td>= {@value #SQL}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #TRANSACTION}</td>     <td>&nbsp;</td><td>= {@value #TRANSACTION}</td></tr>
     * <tr><td>&nbsp;</td><td>{@link #WEAVER}</td>          <td>&nbsp;</td><td>= {@value #WEAVER}</td></tr>
     * </table>
     */
    boolean shouldLog(int level, String category);

    /**
     * PUBLIC:
     * Log a message that does not need to be translated.  This method is intended for
     * external use when logging messages are wanted within the EclipseLink output.
     * <p>
     * The EclipseLink logging levels available are:<br>
     * <table>
     * <caption>Logging levels</caption>
     * <tr><td>{@link #ALL}</td>    <td>&nbsp;</td><td>= {@value #ALL}</td>
     * <tr><td>{@link #FINEST}</td> <td>&nbsp;</td><td>= {@value #FINEST}</td>
     * <tr><td>{@link #FINER}</td>  <td>&nbsp;</td><td>= {@value #FINER}</td>
     * <tr><td>{@link #FINE}</td>   <td>&nbsp;</td><td>= {@value #FINE}</td>
     * <tr><td>{@link #CONFIG}</td> <td>&nbsp;</td><td>= {@value #CONFIG}</td>
     * <tr><td>{@link #INFO}</td>   <td>&nbsp;</td><td>= {@value #INFO}</td>
     * <tr><td>{@link #WARNING}</td><td>&nbsp;</td><td>= {@value #WARNING}</td>
     * <tr><td>{@link #SEVERE}</td> <td>&nbsp;</td><td>= {@value #SEVERE}</td>
     * <tr><td>{@link #OFF}</td>    <td>&nbsp;</td><td>= {@value #OFF}</td>
     * </table>
     */
    void log(int level, String message);

    /**
     * PUBLIC:
     * Log a message with one parameter that needs to be translated.
     * <p>
     * The EclipseLink logging levels available are:<br>
     * <table>
     * <caption>Logging levels</caption>
     * <tr><td>{@link #ALL}</td>    <td>&nbsp;</td><td>= {@value #ALL}</td>
     * <tr><td>{@link #FINEST}</td> <td>&nbsp;</td><td>= {@value #FINEST}</td>
     * <tr><td>{@link #FINER}</td>  <td>&nbsp;</td><td>= {@value #FINER}</td>
     * <tr><td>{@link #FINE}</td>   <td>&nbsp;</td><td>= {@value #FINE}</td>
     * <tr><td>{@link #CONFIG}</td> <td>&nbsp;</td><td>= {@value #CONFIG}</td>
     * <tr><td>{@link #INFO}</td>   <td>&nbsp;</td><td>= {@value #INFO}</td>
     * <tr><td>{@link #WARNING}</td><td>&nbsp;</td><td>= {@value #WARNING}</td>
     * <tr><td>{@link #SEVERE}</td> <td>&nbsp;</td><td>= {@value #SEVERE}</td>
     * <tr><td>{@link #OFF}</td>    <td>&nbsp;</td><td>= {@value #OFF}</td>
     * </table>
     */
    void log(int level, String message, Object param);

    /**
     * PUBLIC:
     * Log a message with one parameter that needs to be translated.
     * <p>
     * The EclipseLink logging levels available are:<br>
     * <table>
     * <caption>Logging levels</caption>
     * <tr><td>{@link #ALL}</td>    <td>&nbsp;</td><td>= {@value #ALL}</td>
     * <tr><td>{@link #FINEST}</td> <td>&nbsp;</td><td>= {@value #FINEST}</td>
     * <tr><td>{@link #FINER}</td>  <td>&nbsp;</td><td>= {@value #FINER}</td>
     * <tr><td>{@link #FINE}</td>   <td>&nbsp;</td><td>= {@value #FINE}</td>
     * <tr><td>{@link #CONFIG}</td> <td>&nbsp;</td><td>= {@value #CONFIG}</td>
     * <tr><td>{@link #INFO}</td>   <td>&nbsp;</td><td>= {@value #INFO}</td>
     * <tr><td>{@link #WARNING}</td><td>&nbsp;</td><td>= {@value #WARNING}</td>
     * <tr><td>{@link #SEVERE}</td> <td>&nbsp;</td><td>= {@value #SEVERE}</td>
     * <tr><td>{@link #OFF}</td>    <td>&nbsp;</td><td>= {@value #OFF}</td>
     * </table>
     */
    void log(int level, String category, String message, Object param);

    /**
     * PUBLIC:
     * Log a message with two parameters that needs to be translated.
     * <p>
     * The EclipseLink logging levels available are:<br>
     * <table>
     * <caption>Logging levels</caption>
     * <tr><td>{@link #ALL}</td>    <td>&nbsp;</td><td>= {@value #ALL}</td>
     * <tr><td>{@link #FINEST}</td> <td>&nbsp;</td><td>= {@value #FINEST}</td>
     * <tr><td>{@link #FINER}</td>  <td>&nbsp;</td><td>= {@value #FINER}</td>
     * <tr><td>{@link #FINE}</td>   <td>&nbsp;</td><td>= {@value #FINE}</td>
     * <tr><td>{@link #CONFIG}</td> <td>&nbsp;</td><td>= {@value #CONFIG}</td>
     * <tr><td>{@link #INFO}</td>   <td>&nbsp;</td><td>= {@value #INFO}</td>
     * <tr><td>{@link #WARNING}</td><td>&nbsp;</td><td>= {@value #WARNING}</td>
     * <tr><td>{@link #SEVERE}</td> <td>&nbsp;</td><td>= {@value #SEVERE}</td>
     * <tr><td>{@link #OFF}</td>    <td>&nbsp;</td><td>= {@value #OFF}</td>
     * </table>
     */
    void log(int level, String message, Object param1, Object param2);

    /**
     * PUBLIC:
     * Log a message with two parameters that needs to be translated.
     * <p>
     * The EclipseLink logging levels available are:<br>
     * <table>
     * <caption>Logging levels</caption>
     * <tr><td>{@link #ALL}</td>    <td>&nbsp;</td><td>= {@value #ALL}</td>
     * <tr><td>{@link #FINEST}</td> <td>&nbsp;</td><td>= {@value #FINEST}</td>
     * <tr><td>{@link #FINER}</td>  <td>&nbsp;</td><td>= {@value #FINER}</td>
     * <tr><td>{@link #FINE}</td>   <td>&nbsp;</td><td>= {@value #FINE}</td>
     * <tr><td>{@link #CONFIG}</td> <td>&nbsp;</td><td>= {@value #CONFIG}</td>
     * <tr><td>{@link #INFO}</td>   <td>&nbsp;</td><td>= {@value #INFO}</td>
     * <tr><td>{@link #WARNING}</td><td>&nbsp;</td><td>= {@value #WARNING}</td>
     * <tr><td>{@link #SEVERE}</td> <td>&nbsp;</td><td>= {@value #SEVERE}</td>
     * <tr><td>{@link #OFF}</td>    <td>&nbsp;</td><td>= {@value #OFF}</td>
     * </table>
     */
    void log(int level, String category, String message, Object param1, Object param2);

    /**
     * PUBLIC:
     * Log a message with three parameters that needs to be translated.
     * <p>
     * The EclipseLink logging levels available are:<br>
     * <table>
     * <caption>Logging levels</caption>
     * <tr><td>{@link #ALL}</td>    <td>&nbsp;</td><td>= {@value #ALL}</td>
     * <tr><td>{@link #FINEST}</td> <td>&nbsp;</td><td>= {@value #FINEST}</td>
     * <tr><td>{@link #FINER}</td>  <td>&nbsp;</td><td>= {@value #FINER}</td>
     * <tr><td>{@link #FINE}</td>   <td>&nbsp;</td><td>= {@value #FINE}</td>
     * <tr><td>{@link #CONFIG}</td> <td>&nbsp;</td><td>= {@value #CONFIG}</td>
     * <tr><td>{@link #INFO}</td>   <td>&nbsp;</td><td>= {@value #INFO}</td>
     * <tr><td>{@link #WARNING}</td><td>&nbsp;</td><td>= {@value #WARNING}</td>
     * <tr><td>{@link #SEVERE}</td> <td>&nbsp;</td><td>= {@value #SEVERE}</td>
     * <tr><td>{@link #OFF}</td>    <td>&nbsp;</td><td>= {@value #OFF}</td>
     * </table>
     */
    void log(int level, String message, Object param1, Object param2, Object param3);

    /**
     * PUBLIC:
     * Log a message with three parameters that needs to be translated.
     * <p>
     * The EclipseLink logging levels available are:<br>
     * <table>
     * <caption>Logging levels</caption>
     * <tr><td>{@link #ALL}</td>    <td>&nbsp;</td><td>= {@value #ALL}</td>
     * <tr><td>{@link #FINEST}</td> <td>&nbsp;</td><td>= {@value #FINEST}</td>
     * <tr><td>{@link #FINER}</td>  <td>&nbsp;</td><td>= {@value #FINER}</td>
     * <tr><td>{@link #FINE}</td>   <td>&nbsp;</td><td>= {@value #FINE}</td>
     * <tr><td>{@link #CONFIG}</td> <td>&nbsp;</td><td>= {@value #CONFIG}</td>
     * <tr><td>{@link #INFO}</td>   <td>&nbsp;</td><td>= {@value #INFO}</td>
     * <tr><td>{@link #WARNING}</td><td>&nbsp;</td><td>= {@value #WARNING}</td>
     * <tr><td>{@link #SEVERE}</td> <td>&nbsp;</td><td>= {@value #SEVERE}</td>
     * <tr><td>{@link #OFF}</td>    <td>&nbsp;</td><td>= {@value #OFF}</td>
     * </table>
     */
    void log(int level, String category, String message, Object param1, Object param2, Object param3);

    /**
     * PUBLIC:
     * Log a message with four parameters that needs to be translated.
     * <p>
     * The EclipseLink logging levels available are:<br>
     * <table>
     * <caption>Logging levels</caption>
     * <tr><td>{@link #ALL}</td>    <td>&nbsp;</td><td>= {@value #ALL}</td>
     * <tr><td>{@link #FINEST}</td> <td>&nbsp;</td><td>= {@value #FINEST}</td>
     * <tr><td>{@link #FINER}</td>  <td>&nbsp;</td><td>= {@value #FINER}</td>
     * <tr><td>{@link #FINE}</td>   <td>&nbsp;</td><td>= {@value #FINE}</td>
     * <tr><td>{@link #CONFIG}</td> <td>&nbsp;</td><td>= {@value #CONFIG}</td>
     * <tr><td>{@link #INFO}</td>   <td>&nbsp;</td><td>= {@value #INFO}</td>
     * <tr><td>{@link #WARNING}</td><td>&nbsp;</td><td>= {@value #WARNING}</td>
     * <tr><td>{@link #SEVERE}</td> <td>&nbsp;</td><td>= {@value #SEVERE}</td>
     * <tr><td>{@link #OFF}</td>    <td>&nbsp;</td><td>= {@value #OFF}</td>
     * </table>
     */
    void log(int level, String message, Object param1, Object param2, Object param3, Object param4);

    /**
     * PUBLIC:
     * Log a message with four parameters that needs to be translated.
     * <p>
     * The EclipseLink logging levels available are:<br>
     * <table>
     * <caption>Logging levels</caption>
     * <tr><td>{@link #ALL}</td>    <td>&nbsp;</td><td>= {@value #ALL}</td>
     * <tr><td>{@link #FINEST}</td> <td>&nbsp;</td><td>= {@value #FINEST}</td>
     * <tr><td>{@link #FINER}</td>  <td>&nbsp;</td><td>= {@value #FINER}</td>
     * <tr><td>{@link #FINE}</td>   <td>&nbsp;</td><td>= {@value #FINE}</td>
     * <tr><td>{@link #CONFIG}</td> <td>&nbsp;</td><td>= {@value #CONFIG}</td>
     * <tr><td>{@link #INFO}</td>   <td>&nbsp;</td><td>= {@value #INFO}</td>
     * <tr><td>{@link #WARNING}</td><td>&nbsp;</td><td>= {@value #WARNING}</td>
     * <tr><td>{@link #SEVERE}</td> <td>&nbsp;</td><td>= {@value #SEVERE}</td>
     * <tr><td>{@link #OFF}</td>    <td>&nbsp;</td><td>= {@value #OFF}</td>
     * </table>
     */
    void log(int level, String category, String message, Object param1, Object param2, Object param3, Object param4);

    /**
     * PUBLIC:
     * This method is called when the log request is from somewhere session is not available.
     * The message needs to be translated.
     * <p>
     * The EclipseLink logging levels available are:<br>
     * <table>
     * <caption>Logging levels</caption>
     * <tr><td>{@link #ALL}</td>    <td>&nbsp;</td><td>= {@value #ALL}</td>
     * <tr><td>{@link #FINEST}</td> <td>&nbsp;</td><td>= {@value #FINEST}</td>
     * <tr><td>{@link #FINER}</td>  <td>&nbsp;</td><td>= {@value #FINER}</td>
     * <tr><td>{@link #FINE}</td>   <td>&nbsp;</td><td>= {@value #FINE}</td>
     * <tr><td>{@link #CONFIG}</td> <td>&nbsp;</td><td>= {@value #CONFIG}</td>
     * <tr><td>{@link #INFO}</td>   <td>&nbsp;</td><td>= {@value #INFO}</td>
     * <tr><td>{@link #WARNING}</td><td>&nbsp;</td><td>= {@value #WARNING}</td>
     * <tr><td>{@link #SEVERE}</td> <td>&nbsp;</td><td>= {@value #SEVERE}</td>
     * <tr><td>{@link #OFF}</td>    <td>&nbsp;</td><td>= {@value #OFF}</td>
     * </table>
     */
    void log(int level, String message, Object[] arguments);

    /**
     * PUBLIC:
     * This method is called when the log request is from somewhere session is not available.
     * The message needs to be translated.
     * <p>
     * The EclipseLink logging levels available are:<br>
     * <table>
     * <caption>Logging levels</caption>
     * <tr><td>{@link #ALL}</td>    <td>&nbsp;</td><td>= {@value #ALL}</td>
     * <tr><td>{@link #FINEST}</td> <td>&nbsp;</td><td>= {@value #FINEST}</td>
     * <tr><td>{@link #FINER}</td>  <td>&nbsp;</td><td>= {@value #FINER}</td>
     * <tr><td>{@link #FINE}</td>   <td>&nbsp;</td><td>= {@value #FINE}</td>
     * <tr><td>{@link #CONFIG}</td> <td>&nbsp;</td><td>= {@value #CONFIG}</td>
     * <tr><td>{@link #INFO}</td>   <td>&nbsp;</td><td>= {@value #INFO}</td>
     * <tr><td>{@link #WARNING}</td><td>&nbsp;</td><td>= {@value #WARNING}</td>
     * <tr><td>{@link #SEVERE}</td> <td>&nbsp;</td><td>= {@value #SEVERE}</td>
     * <tr><td>{@link #OFF}</td>    <td>&nbsp;</td><td>= {@value #OFF}</td>
     * </table>
     */
    void log(int level, String category, String message, Object[] arguments);

    /**
     * PUBLIC:
     * This method is called when the log request is from somewhere session is not available.
     * shouldTranslate flag determines if the message needs to be translated.
     * <p>
     * The EclipseLink logging levels available are:<br>
     * <table>
     * <caption>Logging levels</caption>
     * <tr><td>{@link #ALL}</td>    <td>&nbsp;</td><td>= {@value #ALL}</td>
     * <tr><td>{@link #FINEST}</td> <td>&nbsp;</td><td>= {@value #FINEST}</td>
     * <tr><td>{@link #FINER}</td>  <td>&nbsp;</td><td>= {@value #FINER}</td>
     * <tr><td>{@link #FINE}</td>   <td>&nbsp;</td><td>= {@value #FINE}</td>
     * <tr><td>{@link #CONFIG}</td> <td>&nbsp;</td><td>= {@value #CONFIG}</td>
     * <tr><td>{@link #INFO}</td>   <td>&nbsp;</td><td>= {@value #INFO}</td>
     * <tr><td>{@link #WARNING}</td><td>&nbsp;</td><td>= {@value #WARNING}</td>
     * <tr><td>{@link #SEVERE}</td> <td>&nbsp;</td><td>= {@value #SEVERE}</td>
     * <tr><td>{@link #OFF}</td>    <td>&nbsp;</td><td>= {@value #OFF}</td>
     * </table>
     */
    void log(int level, String message, Object[] arguments, boolean shouldTranslate);

    /**
     * PUBLIC:
     * This method is called when the log request is from somewhere session is not available.
     * shouldTranslate flag determines if the message needs to be translated.
     * <p>
     * The EclipseLink logging levels available are:<br>
     * <table>
     * <caption>Logging levels</caption>
     * <tr><td>{@link #ALL}</td>    <td>&nbsp;</td><td>= {@value #ALL}</td>
     * <tr><td>{@link #FINEST}</td> <td>&nbsp;</td><td>= {@value #FINEST}</td>
     * <tr><td>{@link #FINER}</td>  <td>&nbsp;</td><td>= {@value #FINER}</td>
     * <tr><td>{@link #FINE}</td>   <td>&nbsp;</td><td>= {@value #FINE}</td>
     * <tr><td>{@link #CONFIG}</td> <td>&nbsp;</td><td>= {@value #CONFIG}</td>
     * <tr><td>{@link #INFO}</td>   <td>&nbsp;</td><td>= {@value #INFO}</td>
     * <tr><td>{@link #WARNING}</td><td>&nbsp;</td><td>= {@value #WARNING}</td>
     * <tr><td>{@link #SEVERE}</td> <td>&nbsp;</td><td>= {@value #SEVERE}</td>
     * <tr><td>{@link #OFF}</td>    <td>&nbsp;</td><td>= {@value #OFF}</td>
     * </table>
     */
    void log(int level, String category, String message, Object[] arguments, boolean shouldTranslate);

    /**
     * PUBLIC:
     * This method is called when a throwable at finer level needs to be logged.
     */
    void throwing(Throwable throwable);

    /**
     * PUBLIC:
     * This method is called when a severe level message needs to be logged.
     * The message will be translated
     */
    void severe(String message);

    /**
     * PUBLIC:
     * This method is called when a warning level message needs to be logged.
     * The message will be translated
     */
    void warning(String message);

    /**
     * PUBLIC:
     * This method is called when a info level message needs to be logged.
     * The message will be translated
     */
    void info(String message);

    /**
     * PUBLIC:
     * This method is called when a config level message needs to be logged.
     * The message will be translated
     */
    void config(String message);

    /**
     * PUBLIC:
     * This method is called when a fine level message needs to be logged.
     * The message will be translated
     */
    void fine(String message);

    /**
     * PUBLIC:
     * This method is called when a finer level message needs to be logged.
     * The message will be translated
     */
    void finer(String message);

    /**
     * PUBLIC:
     * This method is called when a finest level message needs to be logged.
     * The message will be translated
     */
    void finest(String message);

    /**
     * PUBLIC:
     * Log a {@link Throwable} with level.
     * <p>
     * The EclipseLink logging levels available are:<br>
     * <table>
     * <caption>Logging levels</caption>
     * <tr><td>{@link #ALL}</td>    <td>&nbsp;</td><td>= {@value #ALL}</td>
     * <tr><td>{@link #FINEST}</td> <td>&nbsp;</td><td>= {@value #FINEST}</td>
     * <tr><td>{@link #FINER}</td>  <td>&nbsp;</td><td>= {@value #FINER}</td>
     * <tr><td>{@link #FINE}</td>   <td>&nbsp;</td><td>= {@value #FINE}</td>
     * <tr><td>{@link #CONFIG}</td> <td>&nbsp;</td><td>= {@value #CONFIG}</td>
     * <tr><td>{@link #INFO}</td>   <td>&nbsp;</td><td>= {@value #INFO}</td>
     * <tr><td>{@link #WARNING}</td><td>&nbsp;</td><td>= {@value #WARNING}</td>
     * <tr><td>{@link #SEVERE}</td> <td>&nbsp;</td><td>= {@value #SEVERE}</td>
     * <tr><td>{@link #OFF}</td>    <td>&nbsp;</td><td>= {@value #OFF}</td>
     * </table>
     */
    void logThrowable(int level, Throwable throwable);

    /**
     * PUBLIC:
     * Log a throwable with level.
     * <p>
     * The EclipseLink logging levels available are:<br>
     * <table>
     * <caption>Logging levels</caption>
     * <tr><td>{@link #ALL}</td>    <td>&nbsp;</td><td>= {@value #ALL}</td>
     * <tr><td>{@link #FINEST}</td> <td>&nbsp;</td><td>= {@value #FINEST}</td>
     * <tr><td>{@link #FINER}</td>  <td>&nbsp;</td><td>= {@value #FINER}</td>
     * <tr><td>{@link #FINE}</td>   <td>&nbsp;</td><td>= {@value #FINE}</td>
     * <tr><td>{@link #CONFIG}</td> <td>&nbsp;</td><td>= {@value #CONFIG}</td>
     * <tr><td>{@link #INFO}</td>   <td>&nbsp;</td><td>= {@value #INFO}</td>
     * <tr><td>{@link #WARNING}</td><td>&nbsp;</td><td>= {@value #WARNING}</td>
     * <tr><td>{@link #SEVERE}</td> <td>&nbsp;</td><td>= {@value #SEVERE}</td>
     * <tr><td>{@link #OFF}</td>    <td>&nbsp;</td><td>= {@value #OFF}</td>
     * </table>
     */
    void logThrowable(int level, String category, Throwable throwable);

    /**
     * PUBLIC:
     * Get the session that owns this SessionLog.
     */
    Session getSession();

    /**
     * PUBLIC:
     * Set the session that owns this SessionLog.
     */
    void setSession(Session session);

    /**
     * PUBLIC:
     * Clone the log.
     */
    Object clone();
}
