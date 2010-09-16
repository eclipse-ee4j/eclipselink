/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dclarke - Dynamic Persistence
 *       http://wiki.eclipse.org/EclipseLink/Development/Dynamic 
 *       (https://bugs.eclipse.org/bugs/show_bug.cgi?id=200045)
 *             - initial JPA Employee example using XML (bug 217884)
 * 		         ported from earlier Oracle TopLink examples
 *     mnorman - tweaks to work from Ant command-line,
 *               get database properties from System, etc.
 *
 ******************************************************************************/
/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * 		dclarke 
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.jpa.dynamic;

//javase imports
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//EclipseLink imports
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.DefaultSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.FetchGroupTracker;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;

/**
 * 
 * @author dclarke
 * @since EclipseLink 1.1.2
 */
public class QuerySQLTracker extends SessionEventAdapter {
    private List<QueryResult> queries;

    /**
     * Constructs and installs the event listener and sql tracking session log
     * 
     * @param session
     */
    private QuerySQLTracker(Session session) {
        session.getEventManager().addListener(this);
        session.setSessionLog(new SQLTrackingSessionLog(session, this));
        reset();
    }

    public static QuerySQLTracker install(Session session) {
        if (session.getSessionLog() instanceof SQLTrackingSessionLog) {
            return ((SQLTrackingSessionLog) session.getSessionLog()).getTracker();
        }
        return new QuerySQLTracker(session);
    }

    public static void uninstall(Session session) {
        if (session.getSessionLog() instanceof SQLTrackingSessionLog) {
            SQLTrackingSessionLog trackingLog = (SQLTrackingSessionLog) session.getSessionLog();
            QuerySQLTracker tracker = trackingLog.getTracker();
            session.getEventManager().removeListener(tracker);
            session.setSessionLog(trackingLog.originalLog);
        }
    }

    /**
     * Helper method to retrieve a tracker from a session where it was installed
     * If the session exists but does not have a tracler installed then an
     * exception is thrown.
     */
    public static QuerySQLTracker getTracker(Session session) {
        if (session == null) {
            return null;
        }
        SessionLog sessionLog = session.getSessionLog();

        if (sessionLog instanceof QuerySQLTracker.SQLTrackingSessionLog) {
            return ((QuerySQLTracker.SQLTrackingSessionLog) sessionLog).getTracker();
        }
        throw new RuntimeException("Could not retireve QuerySQLTracke from session: " + session);
    }

    /**
     * Reset the lists of SQL and queries being tracked
     */
    public void reset() {
        this.queries = new ArrayList<QueryResult>();
    }

    public List<QueryResult> getQueries() {
        return this.queries;
    }

    protected QuerySQLTracker.QueryResult getCurrentResult() {
        if (getQueries().size() == 0) {
            getQueries().add(new QueryResult(null));
            // throw new RuntimeException("Received SQL without a Query ???");
        }
        return getQueries().get(getQueries().size() - 1);
    }

    public int getTotalSQLCalls() {
        int totalSQLCalls = 0;

        for (QueryResult result : getQueries()) {
            totalSQLCalls += result.sqlStatements.size();
        }

        return totalSQLCalls;
    }

    public int getTotalCalls(String startsWith) {
        int calls = 0;
        for (QueryResult result : getQueries()) {
            String sub = result.resultString.substring(0, startsWith.length());
            if (sub.equalsIgnoreCase(startsWith)) {
                calls++;
            }
        }
        return calls;
    }

    public int getTotalSQLCalls(String startsWith) {
        int sqlCalls = 0;

        for (QueryResult result : getQueries()) {
            for (String sql : result.sqlStatements) {
                String sub = sql.substring(0, startsWith.length());
                if (sub.equalsIgnoreCase(startsWith)) {
                    sqlCalls++;
                }
            }
        }

        return sqlCalls;
    }

    public int getTotalSQLSELECTCalls() {
        return getTotalSQLCalls("SELECT");
    }

    public int getTotalSQLINSERTCalls() {
        return getTotalSQLCalls("INSERT");
    }

    public int getTotalSQLUPDATECalls() {
        return getTotalSQLCalls("UPDATE");
    }

    public int getTotalSQLDELETECalls() {
        return getTotalSQLCalls("DELETE");
    }

    public void preExecuteQuery(SessionEvent event) {
        // System.err.println("*** QuerySQLTracker.preExecuteQuery(" +
        // event.getQuery() + ")");
        // Thread.dumpStack();
        QueryResult result = new QueryResult(event.getQuery());
        getQueries().add(result);
    }

    public void postExecuteQuery(SessionEvent event) {
        if (getCurrentResult().query == null) {
            getCurrentResult().setQuery(event.getQuery());
        }
        getCurrentResult().setResult(event.getResult(), event.getSession());
    }

    public class QueryResult {
        private DatabaseQuery query;
        private String resultString = null;
        public List<String> sqlStatements = new ArrayList<String>();

        QueryResult(DatabaseQuery q) {
            query = q;
        }

        protected void setQuery(DatabaseQuery query) {
            this.query = query;
        }

        @SuppressWarnings("unchecked")
        protected void setResult(Object queryResult) {
            setResult(queryResult, null);
        }

        @SuppressWarnings("unchecked")
        protected void setResult(Object queryResult, Session session) {
            StringWriter writer = new StringWriter();
            writer.write(Helper.getShortClassName(query));
            writer.write("[" + System.identityHashCode(query) + "]");
            writer.write(" result = ");

            Object result = queryResult;
            if (queryResult instanceof Collection) {
                result = ((Collection) queryResult).toArray();
            }

            if (result == null) {
                writer.write("NONE");
            } else {
                if (result instanceof Object[]) {
                    Object[] results = (Object[]) result;
                    writer.write("<" + results.length + "> [");
                    for (int index = 0; index < results.length; index++) {
                        if (index > 0) {
                            writer.write(", ");
                        }
                        boolean writePkOnly = false;
                        Object object = results[index];
                        // if session is provided then may extract pk from
                        // object
                        if (session != null) {
                            if (object instanceof FetchGroupTracker) {
                                FetchGroupTracker tracker = (FetchGroupTracker) object;
                                // object.toString may trigger loading of the
                                // whole object. To avoid that write the pk
                                // only.
                                if (tracker._persistence_getFetchGroup() != null) {
                                    writePkOnly = true;
                                }
                            }
                        }
                        if (writePkOnly) {
                            writer.write(Helper.getShortClassName(object) + "("
                                    + session.getDescriptor(object.getClass()).getObjectBuilder().extractPrimaryKeyFromObject(object, (AbstractSession) session) + ")");
                        } else {
                            writer.write(object + "");
                        }
                    }
                    writer.write("]");
                    resultString = writer.toString();
                } else {
                    writer.write(result.toString());
                }
            }

            this.resultString = writer.toString();
        }

        public void addSQL(String sql) {
            sqlStatements.add(sql);
        }

        public String toString() {
            if (this.resultString == null) {
                setResult(null);
            }
            return this.resultString;
        }
    }

    /**
     * This custom SessionLog implementation wraps the existing one and
     * redirects all SQL calls to the tracker. All messages are also passed to
     * the original tracker.
     */
    public class SQLTrackingSessionLog extends DefaultSessionLog {

        private QuerySQLTracker tracker;

        private SessionLog originalLog;

        protected SQLTrackingSessionLog(Session session, QuerySQLTracker aTracker) {
            this.tracker = aTracker;
            this.originalLog = session.getSessionLog();
            setSession(session);
            setWriter(this.originalLog.getWriter());
        }

        public QuerySQLTracker getTracker() {
            return this.tracker;
        }

        public synchronized void log(SessionLogEntry entry) {

            if (entry.getNameSpace() != null && entry.getNameSpace().equalsIgnoreCase(SessionLog.SQL)) {
                getTracker().getCurrentResult().addSQL(entry.getMessage());
            }
            if (!this.originalLog.shouldLog(entry.getLevel())) {
                return;
            }

            this.originalLog.log(entry);
        }
    }

    public void printResults(String prefix) {
        System.out.println(prefix + "-QuerySQLTracker-Queries:");

        int sql = 0;
        for (int index = 0; index < getQueries().size(); index++) {
            QueryResult result = getQueries().get(index);

            System.out.println("\t" + (index + 1) + "> " + result);

            for (int sqlNum = 0; sqlNum < result.sqlStatements.size(); sqlNum++) {
                sql++;
                System.out.println("\t\t" + (index + 1) + "." + (sqlNum + 1) + "-" + sql + "> " + result.sqlStatements.get(sqlNum));
            }
        }

        System.out.println(prefix + "-QuerySQLTracker-Queries: " + getQueries().size());
        System.out.println(prefix + "-QuerySQLTracker-INSERT: " + getTotalSQLINSERTCalls());
        System.out.println(prefix + "-QuerySQLTracker-SELECT: " + getTotalSQLSELECTCalls());
        System.out.println(prefix + "-QuerySQLTracker-UPDATE: " + getTotalSQLUPDATECalls());
        System.out.println(prefix + "-QuerySQLTracker-DELETE: " + getTotalSQLDELETECalls());
    }

}
