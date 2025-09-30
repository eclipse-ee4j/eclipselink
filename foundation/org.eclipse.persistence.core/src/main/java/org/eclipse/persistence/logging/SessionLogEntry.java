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

import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.sessions.Session;

import java.io.Serializable;
import java.time.Instant;

/**
 * SessionLogEntry is a simple container object that holds
 * all the information pertinent to an EclipseLink logging event.
 * It has a time stamp indicating when the event took
 * place. It holds the session, thread, and accessor
 * responsible for the event. And it holds whatever message
 * was passed through to be logged.
 *
 * @see SessionLog
 * @see DefaultSessionLog
 *
 * @author Big Country
 * @since TOPLink/Java 3.0
 */
public class SessionLogEntry implements Serializable {

    // Empty message value.
    private static final String EMPTY_MESSAGE = "";
    /**
     * The session that generated the log entry.
     *
     * @deprecated Use {@link #getConnectionId()} instead, Accessor instance won't be available
     */
    @Deprecated(forRemoval=true, since="4.0.9")
    // Only connection ID will be stored in 5.x, but 4.x must be backward compatible
    protected transient Session session;
    private final String sessionId;
    protected transient Thread thread;
    /**
     * The connection that generated the log entry.
     *
     * @deprecated Use {@link #getConnectionId()} instead, Accessor instance won't be available
     */
    @Deprecated(forRemoval=true, since="4.0.9")
    // Only connection ID will be stored in 5.x, but 4.x must be backward compatible
    protected transient Accessor connection;
    private final Integer connectionId;
    protected String message;
    protected Throwable throwable;
    protected int level;
    protected String nameSpace;
    protected Object[] parameters;
    protected boolean shouldTranslate;
    protected String sourceClassName;
    protected String sourceMethodName;

    private Instant timeStamp;

    // Constructor for all the parameters
    private SessionLogEntry(int level, String nameSpace, String sessionId, String message, Object[] parameters, Integer connectionId, boolean shouldTranslate, Throwable throwable) {
        this.sessionId = sessionId;
        this.thread = Thread.currentThread();
        this.connectionId = connectionId;
        this.message = message != null ? message : EMPTY_MESSAGE;
        this.throwable = throwable;
        this.level = level;
        this.nameSpace = nameSpace;
        this.parameters = parameters;
        this.shouldTranslate = shouldTranslate;
        this.sourceClassName = null;
        this.sourceMethodName = null;
        this.timeStamp = Instant.now();
        // To be removed in master
        this.session = null;
        this.connection = null;
    }

    /**
     * Create a new session log entry for the specified log level, category, session,
     * message, parameters, and datasource connection identifier.
     * <p>
     * The log entry is created with the current thread and timestamp. The exception
     * associated with the log entry is set to null.
     *
     * @param level the log level of the entry
     * @param category the category of the log entry
     * @param sessionId the identifier of the session that generated the log entry
     * @param message the log message
     * @param params an array of parameters associated with the log message
     * @param connectionId the identifier of the datasource connection that generated the log entry
     * @param shouldTranslate whether the log message should be translated
     *
     * @see SessionLog
     */
    public SessionLogEntry(int level, String category, String sessionId, String message, Object[] params, Integer connectionId, boolean shouldTranslate) {
        this(level, category, sessionId, message, params, connectionId, shouldTranslate, null);
    }

    /**
     * Creates a new session log entry for the specified log level, category, session, message, and throwable.
     * <p>
     * The log entry is created with the current thread and timestamp. The category is set to the provided
     * category, and the parameters are set to an empty array. The log message is set to the provided message.
     * <p>
     * The log message is not translated by default.
     *
     * @param level the log level of the entry
     * @param category the category of the log entry
     * @param sessionId the identifier of the session that generated the log entry
     * @param message the log message
     * @param throwable the exception that caused the log entry
     *
     * @see SessionLog
     */
    public SessionLogEntry(int level, String category, String sessionId, String message, Throwable throwable) {
        this(level, category, sessionId, message, null, null, false, throwable);
    }

    /**
     * Create a new session log entry for a session.
     *
     * @param session the session that generated the log entry
     *
     * @see SessionLog
     * @deprecated Use {@link #SessionLogEntry(int, String, String, String, Throwable)}
     *             with the following parameters:<ul>
     *             <li>level set to {@link SessionLog#INFO}</li>
     *             <li>sessionId set to {@link Session#getSessionId()} or {@code null} when {@code session} is {@code null}</li>
     *             <li>category set to {@code null}</li>
     *             <li>message set to {@code ""}</li>
     *             <li>throwable set to {@code null}</li></ul>
     */
    @Deprecated(forRemoval=true, since="4.0.9")
    public SessionLogEntry(Session session) {
        this(SessionLog.INFO, null, session != null ? session.getSessionId() : null, EMPTY_MESSAGE, null);
        this.session = session;
    }

    /**
     * Create a new session log entry for the specified session and throwable.
     *
     * @param session   the session that generated the log entry
     * @param throwable the exception that caused the log entry
     *
     * @see SessionLog
     * @deprecated Use {@link #SessionLogEntry(int, String, String, String, Throwable)}
     *             with the following parameters:<ul>
     *             <li>level set to {@link SessionLog#SEVERE}</li>
     *             <li>sessionId set to {@link Session#getSessionId()} or {@code null} when {@code session} is {@code null}</li>
     *             <li>category set to {@code null}</li>
     *             <li>message set to {@code ""}</li></ul>
     */
    @Deprecated(forRemoval=true, since="4.0.9")
    public SessionLogEntry(Session session, Throwable throwable) {
        this(SessionLog.SEVERE, null, session != null ? session.getSessionId() : null, EMPTY_MESSAGE, throwable);
        this.session = session;
    }

    /**
     * Create a new session log entry for a session and a message.
     *
     * @param session the session that generated the log entry
     * @param message the log message to be recorded
     *
     * @see SessionLog
     * @deprecated Use {@link #SessionLogEntry(int, String, String, String, Throwable)}
     *             with the following parameters:<ul>
     *             <li>level set to {@link SessionLog#INFO}</li>
     *             <li>sessionId set to {@link Session#getSessionId()} or {@code null} when {@code session} is {@code null}</li>
     *             <li>category set to {@code null}</li>
     *             <li>throwable set to {@code null}</li></ul>
     */
    @Deprecated(forRemoval = true, since = "4.0.9")
    public SessionLogEntry(Session session, String message) {
        this(SessionLog.INFO, null, session != null ? session.getSessionId() : null, message, null);
        this.session = session;
    }

    /**
     * Create a new session log entry for the specified session, message, and accessor.
     *
     * @param session    the session that generated the log entry
     * @param message    the log message
     * @param connection the accessor that generated the log entry
     *
     * @see SessionLog
     * @deprecated Use {@link #SessionLogEntry(int, String, String, String, Object[], Integer, boolean)}
     *             with the following parameters:<ul>
     *             <li>level set to {@link SessionLog#INFO}</li>
     *             <li>category set to {@code null}</li>
     *             <li>sessionId set to {@link Session#getSessionId()} or {@code null} when {@code session} is {@code null}</li>
     *             <li>params set to {@code null}</li>
     *             <li>connectionId set to {@link Accessor#getConnectionId()} or {@code null} when {@code connection} is {@code null}</li>
     *             <li>shouldTranslate set to {@code false}</li></ul>
     */
    @Deprecated(forRemoval=true, since="4.0.9")
    public SessionLogEntry(Session session, String message, Accessor connection) {
        this(SessionLog.INFO, null, session != null ? session.getSessionId() : null, message, null, connection != null ? connection.getConnectionId() : null, false);
        this.session = session;
        this.connection = connection;
    }

    /**
     * Create a new session log entry for the specified log level, session, message,
     * parameters, and datasource connection.
     *
     * @param level           the log level of the entry
     * @param session         the session that generated the log entry
     * @param message         the log message
     * @param params          an array of parameters associated with the log message
     * @param connection      the datasource connection that generated the log entry
     * @param shouldTranslate whether the log message should be translated
     *
     * @see SessionLog
     * @deprecated Use {@link #SessionLogEntry(int, String, String, String, Object[], Integer, boolean)}
     *             with the following parameters:<ul>
     *             <li>category set to {@code null}</li>
     *             <li>sessionId set to {@link Session#getSessionId()} or {@code null} when {@code session} is {@code null}</li></ul>
     */
    @Deprecated(forRemoval=true, since="4.0.9")
    public SessionLogEntry(int level, Session session, String message, Object[] params, Accessor connection, boolean shouldTranslate) {
        this(level, null, session != null ? session.getSessionId() : null, message, params, connection != null ? connection.getConnectionId() : null, shouldTranslate, null);
        this.session = session;
        this.connection = connection;
    }

    /**
     * Create a new session log entry for the specified log level, category, session,
     * message, parameters, and datasource connection.
     *
     * @param level the log level
     * @param category the category
     * @param session the session
     * @param message the message
     * @param params array of parameters
     * @param connection the accessor
     * @param shouldTranslate true if the entry should be translated
     *
     * @see SessionLog
     * @deprecated Use {@link #SessionLogEntry(int, String, String, String, Object[], Integer, boolean)}
     *             with the following parameters:<ul>
     *             <li>sessionId set to {@link Session#getSessionId()} or {@code null} when {@code session} is {@code null}</li></ul>
     */
    @Deprecated(forRemoval=true, since="4.0.9")
    public SessionLogEntry(int level, String category, Session session, String message, Object[] params, Accessor connection, boolean shouldTranslate) {
        this(level, category, session != null ? session.getSessionId() : null, message, params, connection != null ? connection.getConnectionId() : null, shouldTranslate, null);
        this.session = session;
        this.connection = connection;
    }

    /**
     * Creates a new session log entry for the specified session, log level, category, and exception.
     *
     * @param session    the session that generated the log entry
     * @param level      the log level of the entry
     * @param category   the category of the log entry
     * @param throwable  the exception associated with the log entry
     *
     * @see SessionLog
     * @deprecated Use {@link #SessionLogEntry(int, String, String, String, Throwable)}
     *             with the following parameters:<ul>
     *             <li>message set to {@code ""}</li></ul>
     */
    @Deprecated(forRemoval=true, since="4.0.9")
    public SessionLogEntry(Session session, int level, String category, Throwable throwable) {
        this(level, category, session != null ? session.getSessionId() : null, EMPTY_MESSAGE, throwable);
        this.session = session;
    }

    /**
     * Return the connection that generated the log entry.
     *
     * @return the connection accessor
     * @deprecated Use {@link #getConnectionId()} instead
     */
    @Deprecated(forRemoval=true, since="4.0.9")
    public Accessor getConnection() {
        return connection;
    }

    /**
     * Return the datasource connection identifier that generated the log entry.
     *
     * @return the datasource connection identifier
     */
    public Integer getConnectionId() {
        return connectionId;
    }

    public Instant getTimeStamp() {
        return timeStamp;
    }

    /**
     * Return the exception that caused the log entry.
     *
     * @return the exception
     */
    public Throwable getException() {
        return throwable;
    }

    /**
     * Return the log entry's message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Return the session that generated the log entry.
     *
     * @return the session
     */
    public Session getSession() {
        return session;
    }

    /**
     * Return the session identifier that generated the log entry.
     *
     * @return the session identifier
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Return the thread that was active when the log entry was generated.
     *
     * @return the thread
     */
    public Thread getThread() {
        return thread;
    }

    /**
     * Return the request level of the log entry. <br>
     * Possible values for log level are listed in SessionLog.
     *
     * @return the request level of the log entry
     * @see SessionLog
     */
    public int getLevel() {
        return level;
    }

    /**
     * Return the name space of the log entry. <br>
     * Possible values for log category (a String) are listed in SessionLog.
     *
     * @return the name space of the log entry
     * @see SessionLog
     */
    public String getNameSpace() {
        return nameSpace;
    }

    /**
     * @return the array of parameters to the message.
     */
    public Object[] getParameters() {
        return parameters;
    }

    /**
     * @return the source class name to the message
     */
    public String getSourceClassName() {
        return sourceClassName;
    }

    /**
     * @return the source method name to the message
     */
    public String getSourceMethodName() {
        return sourceMethodName;
    }

    /**
     * @return if the message should be translated.
     */
    public boolean shouldTranslate() {
        return shouldTranslate;
    }

    /**
     * @return if the log entry was for an exception.
     */
    public boolean hasException() {
        return getException() != null;
    }

    /**
     * @return if the log entry has a message
     */
    public boolean hasMessage() {
        return getMessage() != null && !getMessage().isEmpty();
    }

    /**
     * Set the connection that generated the log entry.
     *
     * @param connection the connection
     * @deprecated Accessor instance will be removed and replaced with readonly {@code connectionId}
     */
    @Deprecated(forRemoval=true, since="4.0.9")
    public void setConnection(Accessor connection) {
        this.connection = connection;
    }

    public void setTimeStamp(Instant timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * Set the exception that caused the log entry.
     *
     * @param throwable the exception
     */
    public void setException(Throwable throwable) {
        this.throwable = throwable;
    }

    /**
     * Set the entry's message.
     *
     * @param message the message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Set the session that generated the log entry.
     *
     * @param session the session
     */
    public void setSession(Session session) {
        this.session = session;
    }

    /**
     * Set the thread that was active when the log entry was generated.
     *
     * @param thread the thread
     */
    public void setThread(Thread thread) {
        this.thread = thread;
    }

    /**
     * Set the request level of the log entry. <br>
     * Possible values for log level are listed in SessionLog.
     *
     * @param level the log level
     *
     * @see SessionLog
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * Set the name space of the log entry. <br>
     * Possible values for log category (a String) are listed in SessionLog.
     *
     * @param nameSpace the log category
     *
     * @see SessionLog
     */
    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    /**
     * Set the array of parameters to the message.
     *
     * @param params array of parameters
     */
    public void setParameters(Object[] params) {
        this.parameters = params;
    }

    /**
     * Set if the message should be translated.
     *
     * @param shouldTranslate true if the message should be translated, false otherwise
     */
    public void setShouldTranslate(boolean shouldTranslate) {
        this.shouldTranslate = shouldTranslate;
    }

    /**
     * Set the source class name to the message.
     *
     * @param sourceClassName source class name
     */
    public void setSourceClassName(String sourceClassName) {
        this.sourceClassName = sourceClassName;
    }

    /**
     * Set the source method name to the message.
     *
     * @param sourceMethodName source method name
     */
    public void setSourceMethodName(String sourceMethodName) {
        this.sourceMethodName = sourceMethodName;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + getMessage() + ")";
    }
}
