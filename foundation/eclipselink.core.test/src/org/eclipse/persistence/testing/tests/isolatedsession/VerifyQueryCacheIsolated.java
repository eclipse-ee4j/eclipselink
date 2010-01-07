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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
