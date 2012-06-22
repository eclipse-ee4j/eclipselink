/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.queries;

import java.util.Vector;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.QueryRedirector;
import org.eclipse.persistence.sessions.DatabaseRecord;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

/**
 * Tests queries that use a redirect. With a redirect we would see null pointer 
 * exception . This only happens with redirectors because if there is no 
 * redirector then the original query is not cloned when it is executed against 
 * the UOW's parent  Session. So by the time we register the Objects, after the 
 * query has been executed against the parent session, the descriptor will have 
 * been set.  
 * When there is a redirector, the Descriptor is never set on the original query 
 * because the parent Session is actually dealing with a cloned query.  So you 
 * get a NullPointerException during registration of the Objects returned from 
 * the query. 
 * BUG# 2692956
 * 
 * @author Guy Pelletier
 * @version 1.0 January 08/03
 */
public class

RedirectQueryOnUOWTest extends TestCase {
    Exception m_exceptionCaught;

    public RedirectQueryOnUOWTest() {
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    protected void setup() {
        m_exceptionCaught = null;
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    public void test() {
        ReadAllQuery r = 
            new ReadAllQuery(Employee.class, (new ExpressionBuilder()).get("firstName").equal("Jill"));
        r.setRedirector(new StupidRedirector());

        UnitOfWork uow = getSession().acquireUnitOfWork();

        try {
            Vector employees = (Vector)uow.executeQuery(r);
        } catch (NullPointerException e) {
            m_exceptionCaught = e;
        }
    }

    protected void verify() {
        if (m_exceptionCaught != null) {
            throw new TestErrorException("NullPointerException was thrown when executing a query with a redirect", 
                                         m_exceptionCaught);
        }
    }
}

class StupidRedirector implements QueryRedirector {
    public Object invokeQuery(DatabaseQuery arg0, org.eclipse.persistence.sessions.Record arg1, Session arg2) {
        // change code to do the correct class cast.
        // Also for this test case to be relevant a completely different query 
        // must be executed.
        // This bug was a flaw with the pre session read refactoring design, so
        // the test is no longer really relevant.
        ReadAllQuery stupidQuery = 
            new ReadAllQuery(Employee.class, (new ExpressionBuilder()).get("firstName").equal("Bob"));
        return ((AbstractSession)arg2).executeQuery(stupidQuery, (DatabaseRecord)arg1);
    }
}
