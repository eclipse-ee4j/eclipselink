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

import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.EmployeePopulator;


public class NestedUnitOfWorkDeleteNestedNewObjectTest extends AutoVerifyTestCase {
    public NestedUnitOfWorkDeleteNestedNewObjectTest() {
        super();
        setDescription("Test designed to verify that a new object registered in a nested unit of work is unregistered when deleted in same unit of work.");
    }

    public void setup() {
        getAbstractSession().beginTransaction();
    }

    public void test() {

        UnitOfWork uow = getSession().acquireUnitOfWork();
        UnitOfWork nestedUow1 = uow.acquireUnitOfWork();

        Employee employee = (Employee)new EmployeePopulator().basicEmployeeExample1();
        Employee nestedEmployee = (Employee)nestedUow1.registerObject(employee);

        nestedUow1.deleteObject(nestedEmployee);
        nestedUow1.commit();
        uow.commit();
        if (((UnitOfWorkImpl)uow).getNewObjectsCloneToOriginal().containsKey(employee)) {
            throw new TestErrorException("Failed to unregister the Object in the nested unit of work");
        }

    }

    public void reset() {
        getAbstractSession().commitTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
}
