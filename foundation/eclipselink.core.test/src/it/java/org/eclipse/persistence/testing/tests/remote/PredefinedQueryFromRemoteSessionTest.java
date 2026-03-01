/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.remote;

import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.server.ClientSession;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

import java.util.Vector;

/**
 * PredefinedQueryFromRemoteSessionTest checks that named queries are accessable
 * from subclasses of the class they are defined in.
 */
public class PredefinedQueryFromRemoteSessionTest extends TestCase {

    // Class members
    public static final String QUERY_NAME = "ServerSessionQuery23679";
    public static final String TEST_NAME = "PredefinedQueryFromRemoteSessionTest";

    protected static org.eclipse.persistence.sessions.server.ServerSession serverSession;

    protected Vector employees;
    protected static Exception storedException;

    /**
     * PredefinedQueryFromRemoteSessionTest constructor comment.
     */
    public PredefinedQueryFromRemoteSessionTest() {
        super();
        setDescription("Tests that named queries defined in ServerSession work in RemoteSession.");
    }

    @Override
    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        serverSession.removeQuery(QUERY_NAME);
    }

    @Override
    protected void setup() {
        // get the server session from the RemoteModel - it is strange that getServerSession() returns a client session
        serverSession = ((ClientSession)org.eclipse.persistence.testing.tests.remote.RemoteModel.getServerSession()).getParent();

    }

    @Override
    protected void test() {

        Vector employees = new Vector();
        storedException = null;

        // Add a named query to the ServerSession
        ReadAllQuery raq = new ReadAllQuery(org.eclipse.persistence.testing.models.employee.domain.Employee.class);
        raq.addAscendingOrdering("firstName");
        serverSession.addQuery(QUERY_NAME, raq);

        // Create a remote connection(and RemoteSession) from the ServerSession
        // Read the named query from the RemoteSession
        try {
            employees = (Vector)getSession().executeQuery(QUERY_NAME);
        } catch (Exception e) {
            setStoredException(new TestErrorException("Error reading vector of Employees in test:" + TEST_NAME));
            return;
        }
        if (employees.size() <= 0) {
            setStoredException(new TestErrorException("No Employees returned in test:" + TEST_NAME));
        }
    }

    @Override
    protected void verify() throws Exception {
        // If any errors, throw them here
        if (storedException != null) {
            throw storedException;
        }
    }

    protected static void setStoredException(Exception e) {
        if (storedException == null) {
            storedException = e;
        }
    }

} // end test case

