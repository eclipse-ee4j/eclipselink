/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.logging;

import java.util.Date;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * SessionLogEntry is a simple container object that holds
 * all the information pertinent to a TopLink logging event.
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
public class SessionLogEntry extends org.eclipse.persistence.sessions.SessionLogEntry {

    /**
     * PUBLIC:
     * Create a new session log entry for a session
     */
    public SessionLogEntry(org.eclipse.persistence.internal.sessions.AbstractSession session) {
        super(session);
    }

    /**
     * PUBLIC:
     * Create a new session log entry for a session and an exception
     */
    public SessionLogEntry(AbstractSession session, Throwable throwable) {
        super(session, throwable);
    }

    /**
     * PUBLIC:
     * Create a new session log entry for a session and a message
     */
    public SessionLogEntry(AbstractSession session, String message) {
        super(session, message);
    }

    /**
     * PUBLIC:
     * Create a new session log entry for a session, a message and an accessor
     */
    public SessionLogEntry(AbstractSession session, String message, Accessor connection) {
        super(session, message, connection);
    }

    /**
    * OBSOLETE:
    * @deprecated replaced by SessionLogEntry(int level, Session session, String message,
    * Object[] params, Accessor connection, boolean shouldTranslate)
     */
    public SessionLogEntry(AbstractSession session, String message, boolean isDebug, Accessor connection) {
        super(session, message, isDebug, connection);
    }

    /**
    * OBSOLETE:
    * @deprecated replaced by SessionLogEntry(int level, Session session, String message,
    * Object[] params, Accessor connection, boolean shouldTranslate)
     */
    public SessionLogEntry(AbstractSession session, String message, boolean isDebug) {
        super(session, message, isDebug);
    }

    /**
     * PUBLIC:
     * Create a new session log entry for a request level, a session, a message and an accessor
     */
    public SessionLogEntry(int level, AbstractSession session, String message, Object[] params, Accessor connection, boolean shouldTranslate) {
        super(level, session, message, params, connection, shouldTranslate);
    }

    /**
     * PUBLIC:
     * Create a new session log entry for a request level, a session, a message and an accessor
     */
    public SessionLogEntry(int level, String category, AbstractSession session, String message, Object[] params, Accessor connection, boolean shouldTranslate) {
        super(level, category, session, message, params, connection, shouldTranslate);
    }

    /**
     * PUBLIC:
     * Create a new session log entry for a session, a level, a category and an exception
     */
    public SessionLogEntry(AbstractSession session, int level, String category, Throwable throwable) {
        super(session, level, category, throwable);
    }

    /**
    * PUBLIC:
    * Return the connection that generated the log entry.
    */
    public Accessor getConnection() {
        return super.getConnection();
    }

    /**
     * PUBLIC:
     * Return the date of the log entry.
     */
    public Date getDate() {
        return super.getDate();
    }

    /**
     * PUBLIC:
     * Return the exception that caused the log entry.
     */
    public Throwable getException() {
        return super.getException();
    }

    /**
     * PUBLIC:
     * Return the log entry's message.
     */
    public String getMessage() {
        return super.getMessage();
    }

    /**
     * PUBLIC:
     * Return the session that generated the log entry.
     */
    public AbstractSession getSession() {
        return super.getSession();
    }

    /**
     * PUBLIC:
     * Return the thread that was active when the log entry was generated.
     */
    public Thread getThread() {
        return super.getThread();
    }

    /**
     * PUBLIC:
     * Return the request level of the log entry.
     */
    public int getLevel() {
        return super.getLevel();
    }

    /**
     * PUBLIC:
     * Return the name space of the log entry.
     */
    public String getNameSpace() {
        return super.getNameSpace();
    }

    /**
     * PUBLIC:
     * Return the array of parameters to the message.
     */
    public Object[] getParameters() {
        return super.getParameters();
    }

    /**
     * PUBLIC:
     * Return if the message should be translated.
     */
    public boolean shouldTranslate() {
        return super.shouldTranslate();
    }

    /**
     * PUBLIC:
     * Return if the log entry was for an exception.
     */
    public boolean hasException() {
        return super.hasException();
    }

    /**
     * PUBLIC:
     * Set the connection that generated the log entry.
     */
    public void setConnection(Accessor connection) {
        super.setConnection(connection);
    }

    /**
     * PUBLIC:
     * Set the date of the log entry.
     */
    public void setDate(Date date) {
        super.setDate(date);
    }

    /**
     * PUBLIC:
     * Set the exception that caused the log entry.
     */
    public void setException(Throwable throwable) {
        super.setException(throwable);
    }

    /**
     * PUBLIC:
     * Set the entry's message.
     */
    public void setMessage(String message) {
        super.setMessage(message);
    }

    /**
     * PUBLIC:
     * Set the session that generated the log entry.
     */
    public void setSession(AbstractSession session) {
        super.setSession(session);
    }

    /**
     * PUBLIC:
     * Set the thread that was active when the log entry was generated.
     */
    public void setThread(Thread thread) {
        super.setThread(thread);
    }

    /**
     * PUBLIC:
     * Set the request level of the log entry.
     */
    public void setLevel(int level) {
        super.setLevel(level);
    }

    /**
     * PUBLIC:
     * Set the name space of the log entry.
     */
    public void setNameSpace(String nameSpace) {
        super.setNameSpace(nameSpace);
    }

    /**
    * PUBLIC:
    * Set the array of parameters to the message.
    */
    public void setParameters(Object[] params) {
        super.setParameters(params);
    }

    /**
     * PUBLIC:
     * Set if the message should be translated.
     */
    public void setShouldTranslate(boolean shouldTranslate) {
        super.setShouldTranslate(shouldTranslate);
    }

    /**
     * PUBLIC:
     * Print message.
     */
    public String toString() {
        return super.toString();
    }
}
