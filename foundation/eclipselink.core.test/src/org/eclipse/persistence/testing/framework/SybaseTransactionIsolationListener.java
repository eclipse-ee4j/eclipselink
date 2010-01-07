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
 *     Created July 9, 2008 - ailitchev 
 *        bug 240210: Tests: Several LRG tests hang on Sybase
 *     Changed Dec 17, 2008 - etang
 *        Move the class from org.eclipse.persistence.testing.tests.unitofwork 
 *        to org.eclipse.persistence.testing.framework
 ******************************************************************************/  
package org.eclipse.persistence.testing.framework;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.eclipse.persistence.internal.databaseaccess.DatabaseAccessor;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.TestProblemException;

/*
 * According to http://manuals.sybase.com/onlinebooks/group-as/asg1250e/sqlug/@Generic__BookTextView/53713;pt=52735/*
 * The SQL92 standard defines four levels of isolation for transactions. 
 * Each isolation level specifies the kinds of actions that are not permitted while concurrent transactions are executing. 
 * Higher levels include the restrictions imposed by the lower levels:
 * 
 * Level 0 - ensures that data written by one transaction represents the actual data. 
 * It prevents other transactions from changing data that has already been modified 
 * (through an insert, delete, update, and so on) by an uncommitted transaction. 
 * The other transactions are blocked from modifying that data until the transaction commits. 
 * However, other transactions can still read the uncommitted data, which results in dirty reads.
 * 
 * Level 1 - prevents dirty reads. 
 * Such reads occur when one transaction modifies a row, and a second transaction reads that row before the first transaction commits the change. 
 * If the first transaction rolls back the change, the information read by the second transaction becomes invalid. 
 * This is the default isolation level supported by Adaptive Server.
 * ....
 * 
 * Apparently Sybase versions 12.5 and 15 has Level 1 set as default.
 * That causes several tests to hang: these tests begin transaction, update a row,
 * then (before the transaction has been committed) attempt to read the row through another connection.
 * 
 * To allow these reads to go through (and read the uncommitted data) connection isolation level
 * should be temporary switched to Level 0.
 * 
 * This class switches the acquired connection to Level 0 and then sets back the original isolation level before connection is released.
 * 
 * Note that for the above scenario only read connection require Level 0.
 * 
 * This solution works only on Sybase 15 but fails on Sybase 12.5 with:
 * com.sybase.jdbc3.jdbc.SybSQLException: The optimizer could not find a unique index which it could use to perform an isolation level 0 scan on table 'RESPONS'.
 * 
 */
public class SybaseTransactionIsolationListener extends SessionEventAdapter {
    HashMap<Connection, Integer> connections = new HashMap<Connection, Integer>();
    public static int requiredVersion = 15;
    // verify that it's version 15 or higher - this doesn't work with version 12.5
    public static boolean isDatabaseVersionSupported(ServerSession serverSession) {
        DatabaseAccessor accessor = (DatabaseAccessor)serverSession.allocateReadConnection();
        int version;
        try {
            String strVersion = accessor.getConnectionMetaData().getDatabaseProductVersion();
            int iStart = strVersion.indexOf("/") + 1;
            int iEnd = strVersion.indexOf(".");
            String strIntVersion = strVersion.substring(iStart, iEnd);
            version = Integer.parseInt(strIntVersion);
        } catch (SQLException ex) {
            throw new TestProblemException("failed to obtain database version number", ex);
        }
        return version >= requiredVersion;
    }
    public void postAcquireConnection(SessionEvent event) {
        Connection conn = ((DatabaseAccessor)event.getResult()).getConnection();
        Statement stmt1 = null;
        Statement stmt2 = null;
        ResultSet result = null;
        Integer isolationLevel;
        try {
            stmt1 = conn.createStatement();
            result = stmt1.executeQuery("select @@isolation");
            result.next();
            isolationLevel = new Integer(result.getInt(1));
            if(isolationLevel > 0) {
                // If conn1 began transaction and updated the row (but hasn't committed the transaction yet),
                // then conn2 should be able to read the original (not updated) state of the row.
                // Without this conn2 reading the row hangs on Sybase.
                stmt2 = conn.createStatement();
                stmt2.execute("set transaction isolation level 0");
                stmt2.close();

                connections.put(conn, isolationLevel);

            }
        } catch (SQLException sqlException) {
            throw new TestProblemException("postAcquireConnection failed. ", sqlException);
        } finally {
            if(result != null) {
                try {
                    result.close();
                } catch (SQLException ex) {
                    // Ignore
                }
            }
            if(stmt1 != null) {
                try {
                    stmt1.close();
                } catch (SQLException ex) {
                    // Ignore
                }
            }
            if(stmt2 != null) {
                try {
                    stmt2.close();
                } catch (SQLException ex) {
                    // Ignore
                }
            }
        }
    }
    public void preReleaseConnection(SessionEvent event) {
        Connection conn = ((DatabaseAccessor)event.getResult()).getConnection();
        Statement stmt = null;
        try {
            Integer isolationLevel = connections.remove(conn);
            if (isolationLevel != null){
                // reset the original transaction isolation.
                stmt = conn.createStatement();
                stmt.execute("set transaction isolation level " + isolationLevel);
                stmt.close();
            }
        } catch (SQLException sqlException) {
            throw new TestProblemException("preReleaseConnection failed. ", sqlException);
        } finally {
            if(stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    // Ignore
                }
            }
        }
    }
}
