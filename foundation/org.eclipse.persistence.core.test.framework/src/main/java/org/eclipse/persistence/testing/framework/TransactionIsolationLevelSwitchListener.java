/*
 * Copyright (c) 2010, 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2010, 2015 Dies Koper (Fujitsu) All rights reserved.
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
//     Created Jan 10, 2010 - Dies Koper (Fujitsu), patterned after SybaseTransactionIsolationListener
//        bug 288715: Tests: Several Core LRG tests hang on Symfoware.
package org.eclipse.persistence.testing.framework;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.internal.databaseaccess.DatabaseAccessor;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;

/*
 * Apache Derby and Symfoware in general are configured to use transaction isolation level
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
public class TransactionIsolationLevelSwitchListener extends SessionEventAdapter {
    private final String statement;
    Map<Connection, String> connections = new HashMap<Connection, String>();

    public TransactionIsolationLevelSwitchListener(DatabasePlatform platform) {
        if (platform.isDerby()) {
            statement = "set isolation ";
        } else if (platform.isMaxDB()) {
            statement = "set transaction isolation level ";
        } else {
            statement = "";
        }
    }

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
                stmt1.execute(statement + "READ UNCOMMITTED");

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
                stmt.execute(statement + isolationLevel);
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
