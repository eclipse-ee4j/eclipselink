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
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.SmallProject;


/**
 * Test that merging changes in a unit of work works successfully.
 * References: CR 2188.
 */
public class CollectionMappingMergeObjectTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    Exception e;

    public CollectionMappingMergeObjectTest() {
        setDescription("Test that merging changes in a unit of work works successfully.");
    }

    public void setup() {
        //
        getAbstractSession().beginTransaction();
    }

    public void reset() {
        //
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        e = null;
    }

    public void test() {
        //
        Session session = getSession();
        session.getIdentityMapAccessor().initializeIdentityMaps();

        UnitOfWork uow1 = session.acquireUnitOfWork();
        Employee emp = new Employee();
        emp = (Employee)uow1.registerObject(emp);
        uow1.commitAndResume();
        ReadObjectQuery readObjectQuery = new ReadObjectQuery(emp);
        readObjectQuery.refreshIdentityMapResult();
        readObjectQuery.dontCascadeParts();
        session.executeQuery(readObjectQuery);

        UnitOfWork uow2 = session.acquireUnitOfWork();
        SmallProject small = new SmallProject();
        small = (SmallProject)uow2.registerObject(small);
        uow2.commitAndResume();

        readObjectQuery = new ReadObjectQuery(small);
        readObjectQuery.refreshIdentityMapResult();
        readObjectQuery.dontCascadeParts();
        session.executeQuery(readObjectQuery);

        small = (SmallProject)session.readObject(small);
        small = (SmallProject)uow1.registerObject(small);
        emp.addProject(small);

        try {
            uow1.commit();
        } catch (Exception e) {
            this.e = e;
            throw new TestErrorException("An exception should not have been thrown when commiting a unit of work.");
        }
    }

    public void verify() {
        //
        if (e != null) {
            throw new TestErrorException("An exception should not have been thrown when commiting a unit of work: " + 
                                         e.toString());
        }
    }
}
