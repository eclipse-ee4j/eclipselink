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
package org.eclipse.persistence.testing.tests.identitymaps;

import java.util.Vector;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.descriptors.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.tests.employee.EmployeeDeleteTest;

/**
 * Add, delete and attempt to retrieve a single object by its primary key. <p>
 * The expected results for the register test should be obtained for the put operation.
 * For NoIdentityMap, the return value of the delete operation should be null.
 * For all other identity maps, the return value of the delete operation should be
 * identical to the original value. For all identity maps, the return value of the
 * subsequent get operation should be null. <p>
 */
public class DeleteFromIdentityMapTest extends RegisterInIdentityMapTest {
    protected Object deletionResult;
    protected Object retrievalAttempt;
    protected OptimisticLockingPolicy lockingPolicy;

    public DeleteFromIdentityMapTest(Class mapClass) {
        super(mapClass);
    }

    public String getDescription() {
        return "This test verifies an object was properly retrieved from the identity map";
    }

    public void setup() {
        super.setup();
        beginTransaction();
        //store the old policy
        lockingPolicy = getSession().getDescriptor(Employee.class).getOptimisticLockingPolicy();
        getSession().getDescriptor(Employee.class).setOptimisticLockingPolicy(null);
        getSession().getDescriptor(Employee.class).getQueryManager().setDeleteQuery(null);
        getSession().getDescriptor(Employee.class).getQueryManager().setUpdateQuery(null);
    }

    public void reset() {
        super.reset();
        // Must reset queries in case they were build with an optimistic locking policy
        getSession().getDescriptor(Employee.class).setOptimisticLockingPolicy(lockingPolicy);
        getSession().getDescriptor(Employee.class).getQueryManager().setDeleteQuery(null);
        getSession().getDescriptor(Employee.class).getQueryManager().setUpdateQuery(null);

        rollbackTransaction();
    }

    public void test() {
        super.test();
        try {
            EmployeeDeleteTest.deleteDependencies(getSession(), (Employee)employees.lastElement());
            getDatabaseSession().deleteObject(employees.lastElement());
        } catch (OptimisticLockException exception) {
            if (isNoIdentityMap()) {
                throw new TestWarningException("Violated optimistic locking, Should not use Optimistic locking with NoIdentityMap");
            }
        }
    }

    public void verifyCacheIdentityMap() {
        Vector cache = (Vector)employees.clone();
        cache.setSize(11);
        cache.removeElementAt(0);
        cache.removeElementAt(0);
        checkIdentityMap(cache);
    }

    public void verifyFullIdentityMap() {
        Vector cache = (Vector)employees.clone();
        cache.setSize(employees.size() - 1);
        checkIdentityMap(cache);
    }
}
