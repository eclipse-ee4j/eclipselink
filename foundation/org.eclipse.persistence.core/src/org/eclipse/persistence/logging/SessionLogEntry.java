/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
import java.util.Date;

import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * SessionLogEntry is a simple container object that holds
 * all the information pertinent to an EclipseLink logging event.
 * It has a date/time stamp indicating when the event took
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
    protected Date date;
    protected transient AbstractSession session;
    protected transient Thread thread;
    protected transient Accessor connection;
    protected String message;
    protected Throwable throwable;
    protected int level;
    protected String nameSpace;
    protected Object[] parameters;
    protected boolean shouldTranslate;
    protected String sourceClassName;
    protected String sourceMethodName;

    /**
     * Create a new session log entry for a session.
     *
     * @param session the session
     */
    public SessionLogEntry(AbstractSession session) {
        this.date = new Date();
        this.thread = Thread.currentThread();
        this.session = session;
        this.message = "";
        this.level = SessionLog.INFO;
    }

    /**
     * Create a new session log entry for a session and a throwable.
     *
     * @param session the session
     * @param throwable the throwable
     */
    public SessionLogEntry(AbstractSession session, Throwable throwable) {
        this(session);
        this.throwable = throwable;
        this.level = SessionLog.SEVERE;
    }

    /**
     * Create a new session log entry for a session and a message.
     *
     * @param session the session
     * @param message the message
     */
    public SessionLogEntry(AbstractSession session, String message) {
        this(session);
        this.message = message;
    }

    /**
     * Create a new session log entry for a session, a message and an accessor.
     *
     * @param session the session
     * @param message the message
     * @param connection the accessor
     */
    public SessionLogEntry(AbstractSession session, String message, Accessor connection) {
        this(session, message);
        this.connection = connection;
    }

    /**
     * Create a new session log entry for a request level, a session, a message,
     * parameters and an accessor. <br>
     * Possible values for log level are listed in SessionLog.
     *
     * @param level the log level
     * @param session the session
     * @param message the message
     * @param params array of parameters
     * @param connection the accessor
     * @param shouldTranslate true if the entry should be translated
     *
     * @see SessionLog
     */
    public SessionLogEntry(int level, AbstractSession session, String message, Object[] params, Accessor connection, boolean shouldTranslate) {
        this(session, message, connection);
        this.level = level;
        this.parameters = params;
        this.shouldTranslate = shouldTranslate;
    }

    /**
     * Create a new session log entry for a request level, a category, a session,
     * a message, parameters and an accessor. <br>
     * Possible values for log level and category are listed in SessionLog.
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
     */
    public SessionLogEntry(int level, String category, AbstractSession session, String message, Object[] params, Accessor connection, boolean shouldTranslate) {
        this(level, session, message, params, connection, shouldTranslate);
        this.nameSpace = category;
    }

    /**
     * Create a new session log entry for a session, a level, a category and an
     * exception. <br>
     * Possible values for log level and category are listed in SessionLog.
     *
     * @param session the session
     * @param level the log level
     * @param category the category
     * @param throwable the exception
     *
     * @see SessionLog
     */
    public SessionLogEntry(AbstractSession session, int level, String category, Throwable throwable) {
        this(session, throwable);
        this.level = level;
        this.nameSpace = category;
    }

    /**
     * Return the connection that generated the log entry.
     *
     * @return the connection accessor
     */
    public Accessor getConnection() {
        return connection;
    }

    /**
     * Return the date of the log entry.
     *
     * @return the date
     */
    public Date getDate() {
        return date;
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
    public AbstractSession getSession() {
        return session;
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
        return getMessage() != null && !(getMessage().length() == 0);
    }

    /**
     * Set the connection that generated the log entry.
     *
     * @param connection the connection
     */
    public void setConnection(Accessor connection) {
        this.connection = connection;
    }

    /**
     * Set the date of the log entry.
     *
     * @param date the date
     */
    public void setDate(Date date) {
        this.date = date;
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
    public void setSession(AbstractSession session) {
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
        return org.eclipse.persistence.internal.helper.Helper.getShortClassName(getClass()) + "(" + getMessage() + ")";
    }
}
