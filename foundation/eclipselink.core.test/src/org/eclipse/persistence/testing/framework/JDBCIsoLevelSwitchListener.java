/*******************************************************************************
 * Copyright (c) 2010 SAP All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Created Oct 1st, 2010 - Adrian G&ouml;rler, patterned after SybaseTransactionIsolationListener
 *        bug 326777:  Some Core LRG tests hang on MaxDB.
 ******************************************************************************/
package org.eclipse.persistence.testing.framework;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.internal.databaseaccess.DatabaseAccessor;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;

/*
 * SAP MaxDB in general is configured to use transaction isolation level
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
 * @author agoerler
 */
class JDBCIsoLevelSwitchListener extends SessionEventAdapter {
    Map<Connection, Integer> connections = new HashMap<Connection, Integer>();

    public void postAcquireConnection(SessionEvent event) {
        Connection conn = ((DatabaseAccessor) event.getResult()).getConnection();
        int old;
        try {
            old = conn.getTransactionIsolation();
            if (old != Connection.TRANSACTION_READ_UNCOMMITTED) {
                conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
                connections.put(conn, old);
            }
        } catch (SQLException sqlException) {
            throw new TestProblemException("postAcquireConnection failed. ", sqlException);
        }
    }

    public void preReleaseConnection(SessionEvent event) {
        Connection conn = ((DatabaseAccessor) event.getResult()).getConnection();
        try {
            Integer old = connections.remove(conn);
            if (old != null) {
                conn.setTransactionIsolation(old);
            }
        } catch (SQLException sqlException) {
            throw new TestProblemException("preReleaseConnection failed. ", sqlException);
        }
    }
}
