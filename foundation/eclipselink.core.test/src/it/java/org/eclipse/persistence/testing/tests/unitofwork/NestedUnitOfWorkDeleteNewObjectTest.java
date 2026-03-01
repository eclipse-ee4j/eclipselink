/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.EmployeePopulator;


public class NestedUnitOfWorkDeleteNewObjectTest extends AutoVerifyTestCase {
    @Override
    public void setup() {
        getAbstractSession().beginTransaction();
    }

    @Override
    public void test() {

        UnitOfWork uow = getSession().acquireUnitOfWork();
        UnitOfWork nestedUow1 = uow.acquireUnitOfWork();
        Employee original = (Employee)new EmployeePopulator().basicEmployeeExample1();
        Employee employee = (Employee)nestedUow1.registerObject(original);
        nestedUow1.commit();
        UnitOfWork nestedUOW2 = uow.acquireUnitOfWork();
        nestedUOW2.deleteObject(nestedUOW2.registerObject(original));
        nestedUOW2.commit();
        uow.commit();
        if (((UnitOfWorkImpl)uow).getNewObjectsCloneToOriginal().containsKey(original)) {
            throw new TestErrorException("Failed to move the deleted new object into the parent UOW");
        }

        if (((UnitOfWorkImpl)uow).getPrimaryKeyToNewObjects().values().stream().anyMatch(c -> c.contains(original))) {
            throw new TestErrorException("Failed to move the deleted new object into the parent UOW");
        }

    }

    @Override
    public void reset() {
        getAbstractSession().commitTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
}
