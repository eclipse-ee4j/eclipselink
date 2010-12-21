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

import java.util.*;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.server.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * We have added support so that there can be multiple queries
 * with the same name (each with different argument sets) cached
 * on DescriptorQueryManager.
 * This test verifies that by adding (using, and removing) more
 * than one named query (with the same name but with different
 * argument sets) to Descriptor Query Manager.
 *
 * Separate test case exists for ClientSession - NamedQueriesClientSessionTest
 * Class created on Mar 12/2002; CR#3716 in StarTeam; Predrag
 */
public class NamedQueriesDescriptorQueryManagerTest extends MultiNameQueriesTestCase {
    protected Server serverSession;
    protected ClientSession clientSession;
    protected Exception caughtException;
    protected ClassDescriptor descriptor;

    /**
    * Employee.class used to add two identically named NamedQueries
    */
    public NamedQueriesDescriptorQueryManagerTest() {
        setDescription("Verifies if a Named Query with different argument sets" + " can be cached on DescriptorQueryManager");
    }

    public void reset() {
        // do not want to keep named queries on employee descriptor
        descriptor.getQueryManager().removeQuery("namedQuerySameName");
        this.clientSession.release();
        this.serverSession.logout();

        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    // end of reset()
    public void setup() {
        org.eclipse.persistence.sessions.Project proj = new org.eclipse.persistence.testing.models.employee.relational.EmployeeProject();
        proj.setDatasourceLogin(getSession().getDatasourceLogin().clone());
        this.serverSession = proj.createServerSession(1, 1);
        this.serverSession.useReadConnectionPool(1, 1);
        this.serverSession.setSessionLog(getSession().getSessionLog());
        this.serverSession.login();
        this.clientSession = this.serverSession.acquireClientSession();

        setDescriptorNamedQueries(Employee.class);
        addNamedQueryFirstName();
        addNamedQueryFirstAndLastName();
        serverSession.getIdentityMapAccessor().initializeIdentityMaps();
    }

    // end of setup()
    public ClassDescriptor getDescriptorNamedQueries() {
        return descriptor;
    }

    // end of getDescriptorNamedQuery
    public void setDescriptorNamedQueries(Class cls) {
        this.descriptor = serverSession.getClassDescriptor(cls);
    }

    // end of setDescriptorNamedQuery    
    public void useNamedQueryFirstName() {
        // do not use the following session's API,
        // public Object executeQuery(queryName, argumentValues)
        // since it looks for query in ClientSession, not in DescriptorQueryManager
        Vector empsByFirstName = (Vector)clientSession.executeQuery("namedQuerySameName", Employee.class, new String("Jill"));
    }

    // end of useNamedQueryFirstName
    public void useNamedQueryFirstAndLastName() {
        Vector empsByFirstAndLastName = (Vector)clientSession.executeQuery("namedQuerySameName", Employee.class, new String("Jill"), new String("May"));
    }

    // end of useNamedQueryFirstAndLastName
    public void addNamedQueryFirstName() {
        descriptor.getQueryManager().addQuery("namedQuerySameName", getNamedQueryFirstName());
    }

    // end of addNamedQueryFirstName
    public void addNamedQueryFirstAndLastName() {
        descriptor.getQueryManager().addQuery("namedQuerySameName", getNamedQueryFirstAndLastName());
    }

    // end of addNamedQueryFirstAndLastName    
    public void test() {
        // Can more than one named query co-exist with the same name?
        // Same name "namedQuerySameName" added twice to the very same
        // descriptor - Employee.class, with different argument sets
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
            throw new org.eclipse.persistence.testing.framework.TestErrorException("Multiple queries with the same named cached on the DescriptorQueryManager.\n" + "Each with different argument sets.\n" + "This exception thrown while testing test case.\n" + "----- NamedQueriesDescriptorQueryManagerTest() -----\n");
        }
    }
    // end of verify()
}// end of public class NamedQueriesDescriptorQueryManagerTest
