/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.junit.failover;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.tests.junit.failover.emulateddriver.EmulatedConnection;
import org.eclipse.persistence.testing.tests.junit.failover.emulateddriver.EmulatedDriver;
import org.junit.Assert;
import org.junit.Test;

/**
 * This test will test TopLink Failover support within a TopLink Connection
 * pool. It is expected that upon a connection failure TopLink will remove all
 * broken connections From the TopLink Pool and then reattempt connection to the
 * datasource.
 *
 * @author gyorke
 *
 */
public class ConnectionPoolFailoverTest extends FailoverBase<ServerSession> {

    @Override
    protected ServerSession createSession(Project p) {
        return (ServerSession) p.createServerSession();
    }

    @Test
    public void fullDatabaseFailureTest() {
        try {
            List<Accessor> list = getEmulatedSession().getReadConnectionPool().getConnectionsAvailable();
            for (Accessor accessor : list) {
                ((EmulatedConnection) accessor.getConnection()).causeCommError();
            }
            EmulatedDriver.fullFailure = true;
            getEmulatedSession().acquireClientSession().readObject(Address.class);
        } catch (DatabaseException ex) {
            return; // Exception expected
        } finally {
            EmulatedDriver.fullFailure = false;
        }
        Assert.fail("Should have thrown exception as database connection is unavailable.");
    }

    @Test
    public void connectionPoolFailureTest() {
        List<Accessor> connections = new ArrayList<>();
        // prime connection pools.
        for (int i = 0; i < 10; ++i) {
            connections.add(getEmulatedSession().getConnectionPool("default").acquireConnection());
        }
        for (Accessor accessor : connections) {
            getEmulatedSession().getConnectionPool("default").releaseConnection(accessor);
        }
        List<Accessor> list = getEmulatedSession().getReadConnectionPool().getConnectionsAvailable();
        for (Accessor accessor : list) {
            ((EmulatedConnection) accessor.getConnection()).causeCommError();
        }

        for (int i = 0; i < 4; ++i) {
            try {
                getEmulatedSession().acquireClientSession().readObject(Address.class);
            } catch (DatabaseException ex) {
                Assert.fail("Should have reconnected an not thrown exception.");
            }
        }
        connections = new ArrayList<Accessor>();
        // prime connection pools.
        for (int i = 0; i < 10; ++i) {
            connections.add(getEmulatedSession().getConnectionPool("default").acquireConnection());
        }
        for (Accessor accessor : connections) {
            getEmulatedSession().getConnectionPool("default").releaseConnection(accessor);
        }
        list = getEmulatedSession().getReadConnectionPool().getConnectionsAvailable();
        for (Accessor accessor : list) {
            ((EmulatedConnection) accessor.getConnection()).causeCommError();
        }

        for (int i = 0; i < 4; ++i) {
            try {
                ReadObjectQuery query = new ReadObjectQuery(Address.class);
                query.setQueryTimeout(10000);
                getEmulatedSession().acquireClientSession().executeQuery(query);
            } catch (DatabaseException ex) {
                if (i != 0) {
                    Assert.fail("Should have reconnected and not thrown exception.");
                }
            }
        }
    }

}
