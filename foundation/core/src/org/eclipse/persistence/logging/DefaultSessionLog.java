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

import java.io.IOException;
import java.io.Writer;

/**
 * <p><b>Purpose</b>: Default log used for the session when message logging is enabled.
 * The session can log information such as,<ul>
 * <li> all SQL executed
 * <li> informational messages
 * <li> debugging information
 * <li> all exceptions that occur within TopLink
 * </ul>
 * As well information about the message can be logged such as,<ul>
 * <li> the session logging the message
 * <li> the connection executing the SQL
 * <li> the thread in which the log entry occured
 * <li> the exact time (to milliseconds) that the log entry occured
 * <li> the stack trace to the exception
 * </ul>
 * @see SessionLog
 * @see AbstractSessionLog
 *
 * @author Big Country
 */
public class DefaultSessionLog extends org.eclipse.persistence.sessions.DefaultSessionLog {

    /**
     * PUBLIC:
     * Create a new default session log.
     */
    public DefaultSessionLog() {
        super();
    }

    /**
     * PUBLIC:
     * Create a new default session log for the given writer.
     */
    public DefaultSessionLog(Writer writer) {
        super(writer);
    }

    /**
    * Initialize the log to be backward-compatible with
    * the original TopLink log.
    */
    protected void initialize() {
        super.initialize();
    }

    /**
     * Initialize the log.
     */
    protected void initialize(Writer writer) {
        super.initialize(writer);
    }

    /**
     * PUBLIC:
     * Log the entry.
     * This writes the log entries information to a writer such as System.out or a file.
     */
    public void log(org.eclipse.persistence.logging.SessionLogEntry entry) {
        super.log(entry);
    }

    /**
     * PUBLIC:
     * Set the writer that will receive the
     * formatted log entries for a file name.
     */
    public void setWriter(String aFileName) {
        super.setWriter(aFileName);
    }

    /**
     * PUBLIC:
     * For the given writer, return it's associated filename.
     * If associated writer does not have a filename, return null.
     */

    //Added for F2104: Properties.XML  .. gn
    public String getWriterFilename() {
        return super.getWriterFilename();
    }

    /**
     * Append the specified message information to the writer.
     */
    protected void writeMessage(String message) throws IOException {
        super.writeMessage(message);
    }

    /**
     * Append the separator string to the writer.
     */
    protected void writeSeparator() throws IOException {
        super.writeSeparator();
    }
}
