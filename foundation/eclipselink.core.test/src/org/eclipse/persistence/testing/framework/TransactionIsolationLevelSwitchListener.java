/*******************************************************************************
 * Copyright (c) 2010 Dies Koper (Fujitsu) All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Created Jan 10, 2010 - Dies Koper (Fujitsu), patterned after SybaseTransactionIsolationListener
 *        bug 288715: Tests: Several Core LRG tests hang on Symfoware.
 ******************************************************************************/
package org.eclipse.persistence.testing.framework;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.internal.databaseaccess.DatabaseAccessor;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;

/*
 * Symfoware in general is configured to use transaction isolation level
 * READ_COMMITTED or SERIALIZABLE.<br/>
 * That causes a few tests to hang, or fail with an error message saying that the table is locked
 * (depending on a setting on the database side): these tests begin transaction, update a row,
 * then (before the transaction has been committed) attempt to read the row through another connection.
 * 
 * To allow these reads to go through (and read the uncommitted data) connection isolation level
 * should be temporary switched to READ_UNCOMMITTED.
 * 
 * This class switches the acquired connection to READ_UNCOMMITTED and then sets back the original
 * isolation level before connection is released.
 * 
 * Note that for the above scenario only read connections require level READ_UNCOMMITTED.
 * 
 * @author Dies Koper
 */
public class TransactionIsolationLevelSwitchListener extends
        SessionEventAdapter {
    Map<Connection, String> connections = new HashMap<Connection, String>();

    public void postAcquireConnection(SessionEvent event) {
        Connection conn = ((DatabaseAccessor) event.getResult())
                .getConnection();
        Statement stmt1 = null;
        String isolationLevel = "";
        try {
            int i = conn.getTransactionIsolation();
            switch (i) {
            case 1:
                isolationLevel = "READ UNCOMMITTED";
                break;
            case 2:
                isolationLevel = "READ COMMITTED";
                break;
            case 4:
                isolationLevel = "REPEATABLE READ";
                break;
            case 8:
                isolationLevel = "SERIALIZABLE";
                break;
            }
            if (i > 0) {
                // If conn1 began transaction and updated the row (but hasn't
                // committed the transaction yet),
                // then conn2 should be able to read the original (not updated)
                // state of the row.
                // Without this conn2 reading the row hangs on Symfoware.
                stmt1 = conn.createStatement();
                stmt1.execute("set transaction isolation level READ UNCOMMITTED");

                connections.put(conn, isolationLevel);
            }
        } catch (SQLException sqlException) {
            throw new TestProblemException("postAcquireConnection failed. ",
                    sqlException);
        } finally {
            if (stmt1 != null) {
                try {
                    stmt1.close();
                } catch (SQLException ex) {
                    // Ignore
                }
            }
        }
    }

    public void preReleaseConnection(SessionEvent event) {
        Connection conn = ((DatabaseAccessor) event.getResult())
                .getConnection();
        Statement stmt = null;
        try {
            String isolationLevel = connections.remove(conn);
            if (isolationLevel != null) {
                // reset the original transaction isolation.
                stmt = conn.createStatement();
                stmt.execute("set transaction isolation level "
                        + isolationLevel);
                stmt.close();
            }
        } catch (SQLException sqlException) {
            throw new TestProblemException("preReleaseConnection failed. ",
                    sqlException);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    // Ignore
                }
            }
        }
    }
}
