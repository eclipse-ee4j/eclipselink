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
package org.eclipse.persistence.testing.tests.unitofwork.transactionisolation;

import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


/**
 * Tests the Session read refactoring / reading through the write connection
 * properly feature.
 * <p>
 * Tests that if I create a new object in the UnitOfWork and then writeChanges,
 * and then read the object back in via the UnitOfWork, still nothing is
 * placed in the shared cache.
 * @author  smcritch
 */
public class TransactionIsolationNoNewObjectsTest extends AutoVerifyTestCase {
    UnitOfWork unitOfWork;

    protected void setup() throws Exception {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        unitOfWork = getSession().acquireUnitOfWork();
    }

    public void reset() throws Exception {
        if (unitOfWork != null) {
            getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
            unitOfWork.release();
            unitOfWork = null;
        }
    }

    public void test() {
        Employee employee = new Employee();

        // no need to set attributes.
        Employee newClone = (Employee)unitOfWork.registerObject(employee);

        unitOfWork.writeChanges();

        // employee should now have primary key set, so can try reading it in
        // unit of work.
        // However can not do reads after write changes, so get a unitofwork against
        // the same write connection, and read from there.
        UnitOfWork parallelUow = unitOfWork.getParent().acquireUnitOfWork();
        parallelUow.beginEarlyTransaction();

        try {
            Employee newCloneInParallelUow = (Employee)parallelUow.readObject(newClone);
            strongAssert(newCloneInParallelUow != null, 
                         "As the parallel Uow shares the same write connection, should " + 
                         "be able to see parallel objects.");
        } finally {
            parallelUow.release();
        }

        ReadObjectQuery cacheQuery = new ReadObjectQuery(Employee.class);
        cacheQuery.checkCacheOnly();
        Employee original = (Employee)getSession().executeQuery(cacheQuery);

        strongAssert(original == null, 
                     "Reading back a new object from the write connection should " + "not have cached objects in the shared cache. " + 
                     original);
    }
}
