/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.identitymaps;

import org.eclipse.persistence.exceptions.OptimisticLockException;
import org.eclipse.persistence.internal.descriptors.OptimisticLockingPolicy;
import org.eclipse.persistence.internal.identitymaps.IdentityMap;
import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.tests.employee.EmployeeDeleteTest;

import java.util.Vector;

/**
 * Add, delete and attempt to retrieve a single object by its primary key. <p>
 * The expected results for the register test should be obtained for the put operation.
 * For NoIdentityMap, the return value of the delete operation should be null.
 * For all other identity maps, the return value of the delete operation should be
 * identical to the original value. For all identity maps, the return value of the
 * subsequent get operation should be null.
 */
public class DeleteFromIdentityMapTest extends RegisterInIdentityMapTest {
    protected Object deletionResult;
    protected Object retrievalAttempt;
    protected OptimisticLockingPolicy lockingPolicy;

    public DeleteFromIdentityMapTest(Class<? extends IdentityMap> mapClass) {
        super(mapClass);
    }

    @Override
    public String getDescription() {
        return "This test verifies an object was properly retrieved from the identity map";
    }

    @Override
    public void setup() {
        super.setup();
        beginTransaction();
        //store the old policy
        lockingPolicy = getSession().getDescriptor(Employee.class).getOptimisticLockingPolicy();
        getSession().getDescriptor(Employee.class).setOptimisticLockingPolicy(null);
        getSession().getDescriptor(Employee.class).getQueryManager().setDeleteQuery(null);
        getSession().getDescriptor(Employee.class).getQueryManager().setUpdateQuery(null);
    }

    @Override
    public void reset() {
        super.reset();
        // Must reset queries in case they were build with an optimistic locking policy
        getSession().getDescriptor(Employee.class).setOptimisticLockingPolicy(lockingPolicy);
        getSession().getDescriptor(Employee.class).getQueryManager().setDeleteQuery(null);
        getSession().getDescriptor(Employee.class).getQueryManager().setUpdateQuery(null);

        rollbackTransaction();
    }

    @Override
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

    @Override
    public void verifyCacheIdentityMap() {
        Vector cache = (Vector)employees.clone();
        cache.setSize(11);
        cache.remove(0);
        cache.remove(0);
        checkIdentityMap(cache);
    }

    @Override
    public void verifyFullIdentityMap() {
        Vector cache = (Vector)employees.clone();
        cache.setSize(employees.size() - 1);
        checkIdentityMap(cache);
    }
}
