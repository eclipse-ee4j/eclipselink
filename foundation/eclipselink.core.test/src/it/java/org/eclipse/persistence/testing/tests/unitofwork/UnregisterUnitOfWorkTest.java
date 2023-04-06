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

import java.util.Collection;
import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


public class UnregisterUnitOfWorkTest extends AutoVerifyTestCase {
    public UnregisterUnitOfWorkTest() {
        setDescription("Check the SQL to see that no inserts are issued.");
    }

    @Override
    protected void test() {

        /*******************/

        /* DEEP UNREGISTER */

        /*******************/
        Employee employee =
            (org.eclipse.persistence.testing.models.employee.domain.Employee)(new org.eclipse.persistence.testing.models.employee.domain.EmployeePopulator()).employeeExample1();

        UnitOfWorkImpl uow = (UnitOfWorkImpl)getSession().acquireUnitOfWork();
        Employee workingCopy = (Employee)uow.registerObject(employee);
        workingCopy.setFirstName("firstName");
        uow.deepUnregisterObject(workingCopy);
        if (!uow.getCloneMapping().isEmpty()) {
            throw new TestErrorException("Deep unregister object did not work");
        }

        if (!uow.getNewObjectsOriginalToClone().isEmpty()) {
            throw new TestErrorException("Deep unregister object did not work");
        }

        if (!uow.getNewObjectsCloneToOriginal().isEmpty()) {
            throw new TestErrorException("Deep unregister object did not work");
        }

        if (!uow.getPrimaryKeyToNewObjects().isEmpty()) {
            throw new TestErrorException("Deep unregister object did not work");
        }

        uow.commit();

        uow = (UnitOfWorkImpl)getSession().acquireUnitOfWork();
        Vector workingCopies = uow.readAllObjects(Employee.class);

        for (Enumeration enumtr = workingCopies.elements(); enumtr.hasMoreElements(); ) {
            uow.deepUnregisterObject(enumtr.nextElement());
        }

        if (!uow.getCloneMapping().isEmpty()) {
            throw new TestErrorException("Deep unregister object did not work");
        }

        if (!uow.getNewObjectsOriginalToClone().isEmpty()) {
            throw new TestErrorException("Deep unregister object did not work");
        }

        if (!uow.getNewObjectsCloneToOriginal().isEmpty()) {
            throw new TestErrorException("Deep unregister object did not work");
        }

        if (!uow.getPrimaryKeyToNewObjects().isEmpty()) {
            throw new TestErrorException("Deep unregister object did not work");
        }

        uow.commit();

        /*******************/
        /*  UNREGISTER     */
        /*******************/
        uow = (UnitOfWorkImpl)getSession().acquireUnitOfWork();
        workingCopy = (Employee)uow.registerObject(employee);
        workingCopy.getAddress();
        workingCopy.getProjects();

        workingCopy.setFirstName("firstName");
        uow.unregisterObject(workingCopy);
        Employee newWorkingCopy = (Employee)uow.registerObject(employee);

        if (workingCopy == newWorkingCopy) {
            throw new TestErrorException("Cascade private parts unregister object did not work");
        }

        if (workingCopy.getAddress() == newWorkingCopy.getAddress()) {
            throw new TestErrorException("Cascade private parts unregister object did not work");
        }

        for (Enumeration enumtr = workingCopy.getProjects().elements(); enumtr.hasMoreElements(); ) {
            if (!newWorkingCopy.getProjects().contains(enumtr.nextElement())) {
                throw new TestErrorException("Cascade private parts unregister object did not work");
            }
        }

        uow.commit();

        /**********************/
        /* SHALLOW UNREGISTER */
        /**********************/
        uow = (UnitOfWorkImpl)getSession().acquireUnitOfWork();
        workingCopy = (Employee)uow.registerObject(employee);
        workingCopy.getAddress();

        workingCopy.setFirstName("firstName");
        uow.shallowUnregisterObject(workingCopy);
        newWorkingCopy = (Employee)uow.registerObject(employee);

        if (workingCopy == newWorkingCopy) {
            throw new TestErrorException("Shallow unregister object did not work");
        }

        if (workingCopy.getAddress() != newWorkingCopy.getAddress()) {
            throw new TestErrorException("Shallow unregister object did not work");
        }

        uow.commit();

    }
}
