/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.tests.clientserver;

import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.server.ConnectionPool;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.TestCase;;

/**
 * Bug 3881, 2015 29 - Initial number of connections not set properly during server session creation
 * @author dminsky
 */
public class ConnectionPoolSizingTest extends TestCase {

    protected int minConnections = ConnectionPool.MIN_CONNECTIONS * 2;
    protected int maxConnections = ConnectionPool.MAX_CONNECTIONS * 2;
    protected int initialConnections = ConnectionPool.INITIAL_CONNECTIONS * 2;
    protected ServerSession serverSession;

    public ConnectionPoolSizingTest() {
        super();
        setDescription("Validate that the default connection pool's connection settings are initialized correctly");
    }

    @Override
    public void test() {
        Project project = new Project(new DatabaseLogin());
        // Use project convenience method: code coverage
        serverSession = (ServerSession)project.createServerSession(initialConnections, minConnections, maxConnections);
    }

    @Override
    public void verify() {
        assertNotNull("ServerSession is null", serverSession);
        ConnectionPool defaultPool = serverSession.getConnectionPool(ServerSession.DEFAULT_POOL);
        assertNotNull("Default connection pool is null", defaultPool);
        assertEquals("ConnectionPool has invalid minimum # of connections", minConnections, defaultPool.getMinNumberOfConnections());
        assertEquals("ConnectionPool has invalid maximum # of connections", maxConnections, defaultPool.getMaxNumberOfConnections());
        assertEquals("ConnectionPool has invalid initial # of connections", initialConnections, defaultPool.getInitialNumberOfConnections());
    }

}
