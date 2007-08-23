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

import java.io.Writer;
import org.eclipse.persistence.sessions.Session;

/**
 * SessionLog is the ever-so-simple interface used by
 * TopLink to log generated messages and SQL. An implementor of
 * this interface can be passed to the TopLink session
 * (via the #setSessionLog(SessionLog) method); and
 * all logging data will be passed through to the implementor
 * via an instance of SessionLogEntry. This can be used
 * to supplement debugging; or the entries could be stored
 * in a database instead of logged to System.out; etc.
 *
 *  @see AbstractSessionLog
 *  @see SessionLogEntry
 *  @see Session
 *
 * @since TOPLink/Java 3.0
 */
public interface SessionLog extends org.eclipse.persistence.sessions.SessionLog, Cloneable {

    /**
    * PUBLIC:
    * TopLink will call this method whenever something
    * needs to be logged (messages, SQL, etc.).
    * All the pertinent information will be contained in
    * the specified entry.
    *
    * @param entry org.eclipse.persistence.sessions.LogEntry
    */
    public void log(org.eclipse.persistence.logging.SessionLogEntry entry);

    /**
    * By default the stack trace is logged for SEVERE all the time and at FINER level for WARNING or less,
    * this can be turned off.
    */
    public boolean shouldLogExceptionStackTrace();

    /**
     * By default the date is always printed, this can be turned off.
     */
    public boolean shouldPrintDate();

    /**
     * By default the thread is logged at FINE or less level, this can be turned off.
     */
    public boolean shouldPrintThread();

    /**
     * By default the connection is always printed whenever available, this can be turned off.
     */
    public boolean shouldPrintConnection();

    /**
     * By default the Session is always printed whenever available, this can be turned off.
     */
    public boolean shouldPrintSession();

    /**
    * By default stack trace is logged for SEVERE all the time and at FINER level for WARNING or less.
    * This can be turned off.
    */
    public void setShouldLogExceptionStackTrace(boolean flag);

    /**
     * By default date is printed, this can be turned off.
     */
    public void setShouldPrintDate(boolean flag);

    /**
     * By default the thread is logged at FINE or less level, this can be turned off.
     */
    public void setShouldPrintThread(boolean flag);

    /**
     * By default the connection is always printed whenever available, this can be turned off.
     */
    public void setShouldPrintConnection(boolean flag);

    /**
     * By default the Session is always printed whenever available, this can be turned off.
     */
    public void setShouldPrintSession(boolean flag);

    /**
    * PUBLIC:
    * Return the writer to which an accessor writes logged messages and SQL.
    * If not set, this reference usually defaults to a writer on System.out.
    * To enable logging, logMessages must be turned on in the session.
    */
    public Writer getWriter();

    /**
     * PUBLIC:
     * Set the writer to which an accessor writes logged messages and SQL.
     * If not set, this reference usually defaults to a writer on System.out.
     * To enable logging, logMessages() is used on the session.
     */
    public void setWriter(Writer log);

    /**
     * PUBLIC:
     * Return the log level.  Used when session is not available.
     */
    public int getLevel();

    /**
     * PUBLIC:
     * Return the log level.  category is only needed where name space
     * is available.
     */
    public int getLevel(String category);

    /**
     * PUBLIC:
     * Set the log level.  Used when session is not available.
     */
    public void setLevel(int level);

    /**
     * PUBLIC:
     * Set the log level.  Category is only needed where name space
     * is available.
     */
    public void setLevel(int level, String category);

    /**
     * PUBLIC:
     * Check if a message of the given level would actually be logged.
     * Used when session is not available.
     */
    public boolean shouldLog(int level);

    /**
     * PUBLIC:
     * Check if a message of the given level would actually be logged.
     * Category is only needed where name space is available.
     */
    public boolean shouldLog(int level, String category);

    /**
     * PUBLIC:
     * Log a message that does not need to be translated.  This method is intended for
     * external use when logging messages are wanted within the TopLink output.
     */
    public void log(int level, String message);

    /**
     * PUBLIC:
     * Log a message with one parameter that needs to be translated.
     */
    public void log(int level, String message, Object param);

    /**
     * PUBLIC:
     * Log a message with two parameters that needs to be translated.
     */
    public void log(int level, String message, Object param1, Object param2);

    /**
     * PUBLIC:
     * Log a message with three parameters that needs to be translated.
     */
    public void log(int level, String message, Object param1, Object param2, Object param3);

    /**
     * PUBLIC:
     * This method is called when the log request is from somewhere session is not available.
     * The message needs to be translated.
     */
    public void log(int level, String message, Object[] arguments);

    /**
     * PUBLIC:
     * This method is called when the log request is from somewhere session is not available.
     * shouldTranslate flag determines if the message needs to be translated.
     */
    public void log(int level, String message, Object[] arguments, boolean shouldTranslate);

    /**
     * PUBLIC:
     * This method is called when a throwable at finer level needs to be logged.
     */
    public void throwing(Throwable throwable);

    /**
     * PUBLIC:
     * This method is called when a severe level message needs to be logged.
     * The message will be translated
     */
    public void severe(String message);

    /**
     * PUBLIC:
     * This method is called when a warning level message needs to be logged.
     * The message will be translated
     */
    public void warning(String message);

    /**
     * PUBLIC:
     * This method is called when a info level message needs to be logged.
     * The message will be translated
     */
    public void info(String message);

    /**
     * PUBLIC:
     * This method is called when a config level message needs to be logged.
     * The message will be translated
     */
    public void config(String message);

    /**
     * PUBLIC:
     * This method is called when a fine level message needs to be logged.
     * The message will be translated
     */
    public void fine(String message);

    /**
     * PUBLIC:
     * This method is called when a finer level message needs to be logged.
     * The message will be translated
     */
    public void finer(String message);

    /**
     * PUBLIC:
     * This method is called when a finest level message needs to be logged.
     * The message will be translated
     */
    public void finest(String message);

    /**
     * PUBLIC:
     * Log a throwable with level.
     */
    public void logThrowable(int level, Throwable throwable);

    /**
     * PUBLIC:
     * Get the session that owns this SessionLog.
     */
    public Session getSession();

    /**
     * PUBLIC:
     * Set the session that owns this SessionLog.
     */
    public void setSession(Session session);
    
    /**
     * PUBLIC:
     * Clone the log.
     */
    public Object clone();
}
