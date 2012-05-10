/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     11/17/2010-2.2 Michael O'Brien  
 *       - 325605: Do not track SQL logs that are at the FINEST level 
 *       these may be SQL warnings or other ORM warnings that happen to use the SQL category
 ******************************************************************************/  
package org.eclipse.persistence.testing.framework;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.persistence.logging.DefaultSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.logging.SessionLogEntry;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.SessionEventListener;

/**
 * This class can be used to replace the session log. It stores the SQL and EclipseLink queries produced
 * during a session.  This allows you to ensure proper amounts of SQL are run in various scenarios.
 */
public class QuerySQLTracker extends DefaultSessionLog {
    private SessionLog originalLog;
    private SessionEventListener listener;
    // Track SQL statements
    private List sqlStatements = new ArrayList();
    private List queries = new ArrayList();

    /**
     * Instantiating a QuerySQLTracker will replace the session's log with the QuerySQLTracker
     * and store the old log.  The old log will be replaced when remove() is called.
     * @param session
     */
    public QuerySQLTracker(Session session) {
        this.originalLog = session.getSessionLog();
        setLevel(SessionLog.FINEST);
        setSession(session);
        setWriter(this.originalLog.getWriter());
        getSession().setSessionLog(this);
        this.listener = buildListener();
        getSession().getEventManager().addListener(this.listener);
    }

    /**
     * remove() should be called when a test completes to clean up the log and allow
     * logging to progress as normal.
     * */
    public void remove() {
        getSession().setSessionLog(originalLog);
        getSession().getEventManager().removeListener(this.listener);
    }

    public synchronized void log(SessionLogEntry entry) {
        // Extend SessionLog.log() by also adding SQL statements into a tracking List that are above the FINEST level
        if ((entry.getNameSpace() != null) && entry.getNameSpace().equalsIgnoreCase(SessionLog.SQL) 
                && entry.getLevel() > SessionLog.FINER) {  // we will not use shouldLog(level, category) in case the implementation there changes
            getSqlStatements().add(entry.getMessage());
        }
        if (!this.originalLog.shouldLog(entry.getLevel())) {
            return;
        }
        this.originalLog.log(entry);
    }

    private SessionEventListener buildListener() {
        return new SessionEventAdapter() {
                private QuerySQLTracker tracker = QuerySQLTracker.this;

                public void preExecuteQuery(SessionEvent event) {
                    this.tracker.getQueries().add(event.getQuery());
                }
            };
    }

    public SessionEventListener getListener() {
        return listener;
    }

    /**
     * Get a list of all the SQL strings that have been executed in while this QuerySQLTracker
     * has been logging. SQL is obtained through by getting logging statements with a SQL namespace.<br>
     * Logs that are categorized as FINE and below will not be tracked.
     * 
     * */
    public List getSqlStatements() {
        return sqlStatements;
    }

    /**
     * Get a list of all the EclipseLink Queries that have been executed in while this QuerySQLTracker
     * has been logging.  Queries are gathered in the preExecuteQuery event.
     */
    public List getQueries() {
        return queries;
    }
}
