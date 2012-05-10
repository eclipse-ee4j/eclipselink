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

import java.util.*;

import org.eclipse.persistence.sessions.server.*;
import org.eclipse.persistence.sessions.*;

/**
 * We have added support so that there can be multiple queries
 * with the same name (each with different argument sets) cached
 * on the UnitOfWork.
 * This test verifies that by adding (using, and removing) more
 * than one named query (with the same name but with different
 * argument sets) to UnitOfWork.
 *
 * Separate test case will test addQuery on DescriptorQueryManager and ClientSession
 * Class created on Mar 12/2002; CR#3716 in StarTeam; Predrag
 */
public class NamedQueriesUOWTest extends MultiNameQueriesTestCase {
    protected Server serverSession;
    protected UnitOfWork uow;
    protected Exception caughtException;

    /**
    * Employee.class used to add two identically named NamedQueries
    */
    public NamedQueriesUOWTest() {
        setDescription("Verifies if a Named Queries with different argument sets" + " can be cached on UnitOfWork");
    }

    public void reset() {
        // do not want to keep named queries on serverSession
        uow.removeQuery("namedQuerySameName");
        this.uow.release();
        this.serverSession.logout();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
        org.eclipse.persistence.sessions.Project proj = new org.eclipse.persistence.testing.models.employee.relational.EmployeeProject();
        proj.setDatasourceLogin(getSession().getDatasourceLogin().clone());
        this.serverSession = proj.createServerSession(1, 1);
        this.serverSession.useReadConnectionPool(1, 1);
        this.serverSession.setSessionLog(getSession().getSessionLog());
        this.serverSession.login();

        this.uow = this.serverSession.acquireUnitOfWork();
        serverSession.getIdentityMapAccessor().initializeIdentityMaps();
        addNamedQueryFirstName();
        addNamedQueryFirstAndLastName();
    }

    // end of setup()
    public void useNamedQueryFirstName() {
        Vector empsByFirstName = (Vector)uow.executeQuery("namedQuerySameName", new String("Jill"));
    }

    // end of useNamedQueryFirstName
    public void useNamedQueryFirstAndLastName() {
        Vector empsByFirstAndLastName = (Vector)uow.executeQuery("namedQuerySameName", new String("Jill"), new String("May"));
    }

    // end of useNamedQueryFirstAndLastName
    public void addNamedQueryFirstName() {
        uow.addQuery("namedQuerySameName", getNamedQueryFirstName());
    }

    // end of addNamedQueryFirstName
    public void addNamedQueryFirstAndLastName() {
        uow.addQuery("namedQuerySameName", getNamedQueryFirstAndLastName());
    }

    // end of addNamedQueryFirstAndLastName    
    public void test() {
        // Can more than one named query co-exist with the same name?
        // Same name "namedQuerySameName" added twice to the very same
        // UnitOfWork, with different argument sets
        try {
            useNamedQueryFirstName();
            useNamedQueryFirstAndLastName();
        } catch (ClassCastException e) {
            caughtException = e;
        }
    }

    // end of test()
    public void verify() {
        if (caughtException != null) {
            throw new org.eclipse.persistence.testing.framework.TestErrorException("Multiple queries with the same named cached on the UnitOfWork.\n" + "Each with different argument sets.\n" + "This exception thrown while testing test case.\n" + "----- NamedQueriesUOWTest() -----\n");
        }
    }
    // end of verify()
}// end of public class NamedQueriesUOWTest
