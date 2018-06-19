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
package org.eclipse.persistence.testing.tests.sessioncache;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.*;

public class ReadObjectAlreadyInSessionCacheTest extends TestCase {
    private Employee objectInCache;
    private int newSalary;

    public ReadObjectAlreadyInSessionCacheTest() {
        setDescription("The test that change are merged into the object in the session cache");
    }

    protected void setup() {
        checkNoWaitSupported();

        // Flush the cache
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        //now read an employee so it's in the session cache
        objectInCache = (Employee)getSession().readObject(Employee.class);

        getAbstractSession().beginTransaction();
    }

    protected void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        ReadObjectQuery query = new ReadObjectQuery(objectInCache);
        query.setLockMode(ObjectLevelReadQuery.LOCK_NOWAIT);
        Employee emp = (Employee)uow.executeQuery(query);
        newSalary = emp.getSalary() + 1;
        emp.setSalary(newSalary);
        uow.commit();
    }

    protected void verify() {
        //ensure changes were merged into the session cache
        Employee verifyObject = (Employee)getSession().readObject(objectInCache);
        int salary = verifyObject.getSalary();
        if (salary != newSalary) {
            throw new TestErrorException("Changes were not merged into Session Cache");
        }
    }

    public void reset() throws Exception {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
}
