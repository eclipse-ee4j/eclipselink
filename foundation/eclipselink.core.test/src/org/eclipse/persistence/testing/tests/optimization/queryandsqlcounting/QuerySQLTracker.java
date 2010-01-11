/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.optimization.queryandsqlcounting;

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
 * This class can be used to replace the session log. It stores the SQL and TopLink queries produced
 * during a session.  This allows you to ensure proper amounts of SQL are run in various scenarios.
 */
public class QuerySQLTracker extends DefaultSessionLog {
    private SessionLog originalLog;
    private SessionEventListener listener;
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
        if ((entry.getNameSpace() != null) && entry.getNameSpace().equalsIgnoreCase(SessionLog.SQL)) {
            getSqlStatements().add(entry.getMessage());
        }
	if (!this.originalLog.shouldLog(entry.getLevel())) {
	    return;
	}
        super.log(entry);
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
     * has been logging. SQL is obtained through by getting logging statements with a SQL namespace.
     * */
    public List getSqlStatements() {
        return sqlStatements;
    }

    /**
     * Get a list of all the TopLink Queries that have been executed in while this QuerySQLTracker
     * has been logging.  Queries are gathered in the preExecuteQuery event.
     */
    public List getQueries() {
        return queries;
    }
}
