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
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


/**
 * CR#2631.This test verify Unit of Work hasChange() functionality
 * Return false if there have been no other changes then deleting objects.
 * This is computationaly expensive and should be avoided on large object graphs.
 */
public class UOWHasOnlyDeletesTest extends AutoVerifyTestCase {
    private boolean hasChanges;

    public UOWHasOnlyDeletesTest() {
        setDescription("This test verifies that hasChange() should return true if there have been no other changes than deleting objects");
    }

    public void setup() {
        getAbstractSession().beginTransaction();
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee employee = (Employee)uow.readObject(Employee.class);
        uow.deleteObject(employee);
        hasChanges = uow.hasChanges();
    }

    public void verify() {
        //check to see what is return. should return false
        if (!hasChanges) {
            throw new TestErrorException("Testcase has failed. UOW hasChange doesn't work correctly.  Does not detect deleted objects.");
        }
    }
}
