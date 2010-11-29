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
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.server.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import java.util.*;

/**
 *  CR#4099 (Testing to see if it is fixed by CR#3716)
 *  Ensure a query which is added to the server session can be retrieved by the
 *  client session.
 */
public class ServerClientNamedQueryTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    private DatabaseLogin login = null;
    private Server serverSession = null;
    private ClientSession clientSession = null;
    private QueryException caughtException = null;

    public void setup() {
        // Create a server session based on the current login
        org.eclipse.persistence.sessions.Project proj = new org.eclipse.persistence.testing.models.employee.relational.EmployeeProject();
        proj.setDatasourceLogin(getSession().getDatasourceLogin().clone());
        serverSession = proj.createServerSession(1, 1);
        serverSession.useReadConnectionPool(1, 1);
        this.serverSession.setSessionLog(getSession().getSessionLog());
        serverSession.login();
        clientSession = serverSession.acquireClientSession();

        // Build a query which can be used for the test.  Add it to the server session
        ExpressionBuilder emp = new ExpressionBuilder();
        Expression firstNameExpression = emp.get("firstName").equal(emp.getParameter("firstName"));
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Employee.class);
        query.setSelectionCriteria(firstNameExpression);
        query.addArgument("firstName");

        serverSession.addQuery("serverClientNamedQuery", query);

    }

    public void test() {
        try {
            // Ensure the query can be run without exception
            // Prior to the fix, a QueryException would be thrown.
            Vector empsByFirstName = (Vector)clientSession.executeQuery("serverClientNamedQuery", "Jill");

        } catch (QueryException exception) {
            caughtException = exception;
        }
    }

    public void verify() {
        if (caughtException != null) {
            throw new TestErrorException("A query added to the Server Session could not be " + "found on the Client Session. Error message: " + caughtException.toString());
        }
    }

    public void reset() {
        serverSession.removeQuery("serverClientNamedQuery");
        clientSession.release();
        serverSession.logout();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
}
