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
package org.eclipse.persistence.testing.tests.isolatedsession;

import java.util.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.IsolatedClientSession;
import org.eclipse.persistence.queries.*;

/**
 * Ensure isolated query results are cached in the isolated cache.
 */
public class VerifyQueryCacheIsolated extends ClientServerTest {
    protected Vector readEmployees = null;
    protected ReadAllQuery query = null;
    protected Session session = null;

    public VerifyQueryCacheIsolated(boolean isExclusive) {
        super(isExclusive);
        setDescription("This test verifies that certain data from a query cache is only available to the client");
    }

    public void setup() {
        super.setup();
        query = new ReadAllQuery(IsolatedEmployee.class);
        query.cacheQueryResults();
    }

    public void test() {
        session = (Session)this.clients.get(0);
        if (!(session instanceof IsolatedClientSession)) {
            throw new TestErrorException("The session created was not an Isolated Session");
        }
        session.executeQuery(query);
        readEmployees = (Vector)session.executeQuery(query);
    }

    public void verify() {
        if (this.server.getIdentityMapAccessorInstance().getQueryResult(query, null, false) != null) {
            throw new TestErrorException("The query results were not isolated");
        }
        if (((AbstractSession)session).getIdentityMapAccessorInstance().getQueryResult(query, null, false) == null) {
            throw new TestErrorException("The query results were not saved in the identity map.");
        }
        if (readEmployees.size() != 2) {
            throw new TestErrorException("The query results were not returned.");
        }
    }
}
