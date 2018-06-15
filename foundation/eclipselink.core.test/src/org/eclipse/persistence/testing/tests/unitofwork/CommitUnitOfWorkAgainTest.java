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

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


public class CommitUnitOfWorkAgainTest extends UnitOfWorkEventTest {
    public void setup() {
        super.setup();
        setDescription("Test after uow.commit(), uow must be released and not in active");

    }

    public void test() {
        //Test CR#2189
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee anEmployee = (Employee)uow.readObject(Employee.class);
        anEmployee.setLastName("TOPLink");
        uow.commit();

        //After this point, UOW must be released.
        //If you try to commit again, it must throw a exception.[TOPLink-7068]
        try {
            anEmployee.setLastName("EclipseLink");
            uow.commit();
            throw new TestErrorException("UOW is not throwing an exception when it is committed for a second time.");
        } catch (ValidationException exception) {
            if (!(exception.getErrorCode() == ValidationException.CANNOT_COMMIT_UOW_AGAIN)) {
                throw new TestErrorException("UOW is throwing exception " + exception.getErrorCode() +
                                             " instead of CANNOT_COMMIT_UOW_AGAIN when it is committed for a second time.");
            }
        }
    }
}
