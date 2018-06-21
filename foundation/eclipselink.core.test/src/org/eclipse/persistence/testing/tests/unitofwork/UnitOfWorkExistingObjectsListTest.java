/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Address;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


/**
 * This test verifies that Unregistered Existing Objects will be tracked for the life of the UOW
 */
public class UnitOfWorkExistingObjectsListTest extends AutoVerifyTestCase {

    public UnitOfWorkExistingObjectsListTest() {
        setDescription("verifies that Unregistered Existing Objects will be tracked for the life of the UOW");
    }

    public void test() {
        Session session = getSession();
        UnitOfWork uow = session.acquireUnitOfWork();
        Address addr = (Address)uow.readObject(Address.class);
        uow.release();
        uow = session.acquireUnitOfWork();
        uow.setValidationLevel(UnitOfWorkImpl.None);
        Employee emp = new Employee();
        uow.registerNewObject(emp);
        emp.setAddress(addr);
        uow.assignSequenceNumbers();
        if (!((UnitOfWorkImpl)uow).getUnregisteredExistingObjects().containsKey(addr)){
            throw new TestErrorException(" Bug 294259 -  Duplicate existence checks in same UOW.  Patch Failed");
        }
        uow.release();
    }
}
