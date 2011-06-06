/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.logging;

import java.util.Date;
import java.io.Serializable;
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
    
    /**
     * PUBLIC:
     * Create a new session log entry for a session
     */
    public SessionLogEntry(AbstractSession session) {
        this.date = new Date();
        this.thread = Thread.currentThread();
        this.session = session;
        this.message = "";
        this.level = SessionLog.INFO;
    }

    /**
     * PUBLIC:
     * Create a new session log entry for a session and an exception
     */
    public SessionLogEntry(AbstractSession session, Throwable throwable) {
        this(session);
        this.throwable = throwable;
        this.level = SessionLog.SEVERE;
    }

    /**
     * PUBLIC:
     * Create a new session log entry for a session and a message
     */
    public SessionLogEntry(AbstractSession session, String message) {
        this(session);
        this.message = message;
    }

    /**
     * PUBLIC:
     * Create a new session log entry for a session, a message and an accessor
     */
    public SessionLogEntry(AbstractSession session, String message, Accessor connection) {
        this(session, message);
        this.connection = connection;
    }

    /**
     * PUBLIC:
     * Create a new session log entry for a request level, a session, a message and an accessor.
     * <br>Possible values for log level are listed in SessionLog.
     * @see SessionLog
     */
    public SessionLogEntry(int level, AbstractSession session, String message, Object[] params, Accessor connection, boolean shouldTranslate) {
        this(session, message, connection);
        this.level = level;
        this.parameters = params;
        this.shouldTranslate = shouldTranslate;
    }
    
    /**
     * PUBLIC:
     * Create a new session log entry for a request level, a session, a message and an accessor.
     * <br>Possible values for log level and category are listed in SessionLog.
     * @see SessionLog
     */
    public SessionLogEntry(int level, String category, AbstractSession session, String message, Object[] params, Accessor connection, boolean shouldTranslate) {
        this(level, session, message, params, connection, shouldTranslate);
        this.nameSpace = category;
    }
    
    /**
     * PUBLIC:
     * Create a new session log entry for a session, a level, a category and an exception.
     * <br>Possible values for log level and category are listed in SessionLog.
     * @see SessionLog
     */
    public SessionLogEntry(AbstractSession session, int level, String category, Throwable throwable) {
        this(session, throwable);
        this.level = level;
        this.nameSpace = category;
    }

    /**
     * PUBLIC:
     * Return the connection that generated the log entry.
     */
    public Accessor getConnection() {
        return connection;
    }

    /**
     * PUBLIC:
     * Return the date of the log entry.
     */
    public Date getDate() {
        return date;
    }

    /**
     * PUBLIC:
     * Return the exception that caused the log entry.
     */
    public Throwable getException() {
        return throwable;
    }

    /**
     * PUBLIC:
     * Return the log entry's message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * PUBLIC:
     * Return the session that generated the log entry.
     */
    public AbstractSession getSession() {
        return session;
    }

    /**
     * PUBLIC:
     * Return the thread that was active when the log entry was generated.
     */
    public Thread getThread() {
        return thread;
    }

    /**
     * PUBLIC:
     * Return the request level of the log entry.
     * <br>Possible values for log level are listed in SessionLog.
     * @see SessionLog
     */
    public int getLevel() {
        return level;
    }

    /**
     * PUBLIC:
     * Return the name space of the log entry.
     * <br>Possible values for log category (a String) are listed in SessionLog.
     * @see SessionLog
     */
    public String getNameSpace() {
        return nameSpace;
    }

    /**
     * PUBLIC:
     * Return the array of parameters to the message.
     */
    public Object[] getParameters() {
        return parameters;
    }

    /**
     * PUBLIC:
     * Return if the message should be translated.
     */
    public boolean shouldTranslate() {
        return shouldTranslate;
    }

    /**
     * PUBLIC:
     * Return if the log entry was for an exception.
     */
    public boolean hasException() {
        return getException() != null;
    }

    /**
     * PUBLIC:
     * Set the connection that generated the log entry.
     */
    public void setConnection(Accessor connection) {
        this.connection = connection;
    }

    /**
     * PUBLIC:
     * Set the date of the log entry.
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * PUBLIC:
     * Set the exception that caused the log entry.
     */
    public void setException(Throwable throwable) {
        this.throwable = throwable;
    }

    /**
     * PUBLIC:
     * Set the entry's message.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * PUBLIC:
     * Set the session that generated the log entry.
     */
    public void setSession(AbstractSession session) {
        this.session = session;
    }

    /**
     * PUBLIC:
     * Set the thread that was active when the log entry was generated.
     */
    public void setThread(Thread thread) {
        this.thread = thread;
    }

    /**
     * PUBLIC:
     * Set the request level of the log entry.
     * <br>Possible values for log level are listed in SessionLog.
     * @see SessionLog
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * PUBLIC:
     * Set the name space of the log entry.
     * <br>Possible values for log category (a String) are listed in SessionLog.
     * @see SessionLog
     */
    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    /**
    * PUBLIC:
    * Set the array of parameters to the message.
    */
    public void setParameters(Object[] params) {
        this.parameters = params;
    }

    /**
     * PUBLIC:
     * Set if the message should be translated.
     */
    public void setShouldTranslate(boolean shouldTranslate) {
        this.shouldTranslate = shouldTranslate;
    }

    /**
     * PUBLIC:
     * Print message.
     */
    public String toString() {
        return org.eclipse.persistence.internal.helper.Helper.getShortClassName(getClass()) + "(" + getMessage() + ")";
    }
}
