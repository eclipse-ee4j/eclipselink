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

    private String sessionId;

    /**
     * @deprecated Use {@link #getThread()} instead
     */
    @Deprecated(forRemoval=true, since="4.0.9")
    protected transient Thread thread;

    private final Integer connectionId;

    /**
     * @deprecated Use {@link #getMessage()} instead
     */
    @Deprecated(forRemoval=true, since="4.0.9")
    protected String message;

    /**
     * @deprecated Use {@link #getException()} instead
     */
    @Deprecated(forRemoval=true, since="4.0.9")
    protected Throwable throwable;

    /**
     * @deprecated Use {@link #getLevel()} instead
     */
    @Deprecated(forRemoval=true, since="4.0.9")
    protected int level;

    /**
     * @deprecated Use {@link #getNameSpace()} instead
     */
    @Deprecated(forRemoval=true, since="4.0.9")
    protected String nameSpace;

    /**
     * @deprecated Use {@link #getParameters()} instead
     */
    @Deprecated(forRemoval=true, since="4.0.9")
    protected Object[] parameters;

    /**
     * @deprecated Will be removed from the class, translation will not be part of the logger
     */
    @Deprecated(forRemoval=true, since="4.0.9")
    protected boolean shouldTranslate;

    /**
     * @deprecated Use {@link #getSourceClassName()} instead
     */
    @Deprecated(forRemoval=true, since="4.0.9")
    protected String sourceClassName;

    /**
     * @deprecated Use {@link #getSourceMethodName()} instead
     */
    @Deprecated(forRemoval=true, since="4.0.9")
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
     * @deprecated Translation will not be part of the logger, {@code shouldTranslate} will be removed
     */
    @Deprecated(forRemoval=true, since="4.0.9")
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
     * @deprecated Will be removed from the class, translation will not be part of the logger
     */
    @Deprecated(forRemoval=true, since="4.0.9")
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
     * Set the session identifier that generated the log entry.
     *
     * @param sessionId the session identifier
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
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
     * @deprecated Will be removed from the class, translation will not be part of the logger
     */
    @Deprecated(forRemoval=true, since="4.0.9")
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
