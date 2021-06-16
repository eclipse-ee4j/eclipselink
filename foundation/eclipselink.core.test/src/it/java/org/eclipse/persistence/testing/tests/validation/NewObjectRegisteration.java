/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.sessions.UnitOfWork;


public class NewObjectRegisteration extends ExceptionTest {
    protected void setup() {
        expectedException = QueryException.backupCloneIsOriginalFromSelf(new Employee());
    }

    protected void test() {

        org.eclipse.persistence.testing.models.employee.domain.Employee employee = (org.eclipse.persistence.testing.models.employee.domain.Employee)(new org.eclipse.persistence.testing.models.employee.domain.EmployeePopulator()).basicEmployeeExample1();
        org.eclipse.persistence.testing.models.employee.domain.Address address = (new org.eclipse.persistence.testing.models.employee.domain.EmployeePopulator()).addressExample1();

        UnitOfWork uow = getSession().acquireUnitOfWork();
        org.eclipse.persistence.testing.models.employee.domain.Employee workingEmployeeCopy = (org.eclipse.persistence.testing.models.employee.domain.Employee)uow.registerObject(employee);
        workingEmployeeCopy.setAddress(address);
        uow.registerObject(address);
        try {
            uow.commit();
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }
}
