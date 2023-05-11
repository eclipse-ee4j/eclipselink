/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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
//     dminsky - initial API and implementation
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.server.*;

/**
 * Test to ensure that a connection is released by a ServerSession after a query
 * is executed with a query timeout value set, and a cursor is returned.
 * EL Bug 244241 - connection not released on query timeout when cursor used
 * @author dminsky
 */
public class QueryTimeoutConnectionReleasedTest extends TestCase {

    protected boolean queryTimeoutExceeded;
    protected int preConnectionsAvailable;
    protected int postConnectionsAvailable;
    protected ServerSession serverSession;

    public QueryTimeoutConnectionReleasedTest() {
        setDescription("Test the ServerSession releases pooled connections with a query returning a cursor and query timeout set");
    }

    @Override
    public void setup() {
        preConnectionsAvailable = 0;
        postConnectionsAvailable = 0;
        queryTimeoutExceeded = false;
        serverSession = (ServerSession)getSession().getProject().createServerSession(5,5);
        DatabaseLogin clonedLogin = (DatabaseLogin) getSession().getLogin().clone();
        serverSession.setLogin(clonedLogin);
        serverSession.login();
    }

    @Override
    public void test() {
        if (getSession().getPlatform().isSymfoware()) {
            throwWarning("Test QueryTimeoutConnectionReleasedTest skipped for this platform, "
                    + "the driver does not support query timeout. (bug 304905)");
        }
        if (getSession().getPlatform().isMaxDB()) {
            throwWarning("Test QueryTimeoutConnectionReleasedTest skipped for this platform, "
                    + "the driver does not support query timeout. (bug 326503)");
        }
        if (getSession().getPlatform().isHANA()) {
            throwWarning("Test QueryTimeoutConnectionReleasedTest skipped for this platform, "
                    + "the driver does not support query timeout. (bug 384135)");
        }
        preConnectionsAvailable = serverSession.getReadConnectionPool().getConnectionsAvailable().size();
        try {
            DataReadQuery query = new DataReadQuery();
            String sqlString = "SELECT SUM(e.EMP_ID) from EMPLOYEE e , EMPLOYEE b, EMPLOYEE c, EMPLOYEE d, EMPLOYEE f, EMPLOYEE g, EMPLOYEE h, EMPLOYEE g";
            query.setSQLString(sqlString);
            query.setQueryTimeout(1);
            query.useCursoredStream(1, 1);
            serverSession.acquireClientSession().executeQuery(query);
        } catch (Exception e) {
            if (e instanceof DatabaseException) {
                queryTimeoutExceeded = true;
            }
        } finally {
            postConnectionsAvailable = serverSession.getReadConnectionPool().getConnectionsAvailable().size();
        }
    }

    @Override
    public void verify() {
        // It is expected that the query timeout was exceeded, and also the number of connections available after the exception is the same as before
        if (queryTimeoutExceeded) {
            if (preConnectionsAvailable != postConnectionsAvailable) {
                throw new TestErrorException("Number of connections available in pool is: " + postConnectionsAvailable + " expected: " + preConnectionsAvailable);
            }
        } else {
            throw new TestErrorException("Query timeout was not exceeded, this is required for correct testing");
        }
    }

    @Override
    public void reset() {
        if (this.serverSession != null) {
            serverSession.logout();
            serverSession.release();
        }
    }

}
