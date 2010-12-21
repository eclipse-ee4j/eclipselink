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

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.server.*;

/**
 * This class verifies that DatabaseExeption thrown on the execution of a CursoredStream query
 * on a ClientSession is not correctly rethrown by TopLink.
 * This resulted in a NullPointerException being thrown instead of the true exception.
 */
public class CursoredStreamClientSessionTest extends TestCase {
    protected Server serverSession;
    protected ClientSession clientSession;
    protected Exception caughtException;

    /**
     * CursoredStreamClientSessionTest constructor comment.
     */
    public CursoredStreamClientSessionTest() {
        setDescription("Verifies if a CursoredStream query execution on a ClientSession correctly throws a DatabaseException");
    }

    public void reset() {
        this.clientSession.release();
        this.serverSession.logout();
    }

    public void setup() {
        org.eclipse.persistence.sessions.Project proj = 
            new org.eclipse.persistence.testing.models.employee.relational.EmployeeProject();
        proj.setDatasourceLogin(getSession().getDatasourceLogin().clone());
        this.serverSession = proj.createServerSession(1, 1);
        this.serverSession.useReadConnectionPool(1, 1);
        this.serverSession.setSessionLog(getSession().getSessionLog());
        this.serverSession.login();
        this.clientSession = this.serverSession.acquireClientSession();
    }

    public void test() {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Employee.class);
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression exp = builder.getField("HOOYAH").equal("Jill");
        query.setSelectionCriteria(exp);
        query.useCursoredStream(1, 1);

        try {
            // Expect to throw a DatabaseException here.  A NullPointerException is thrown instead.
            CursoredStream stream = (CursoredStream)clientSession.executeQuery(query);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    public void verify() {
        if (caughtException == null) {
            throw new TestErrorException("The proper exception was not thrown:\n" + 
                                         "caught exception was null! \n");
        }

        if (caughtException instanceof NullPointerException) {
            throw new TestErrorException("a nullpointer exception was incorrectly thrown");
        }
    }
}
