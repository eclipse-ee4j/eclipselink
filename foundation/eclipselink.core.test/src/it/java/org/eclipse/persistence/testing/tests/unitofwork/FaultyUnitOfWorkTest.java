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
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.models.employee.domain.Address;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


public class FaultyUnitOfWorkTest extends AutoVerifyTestCase {
    public Employee unitOfWorkEmployeeCopy;
    public UnitOfWork unitOfWork;
    public Address addressOfEmployeeInSession;
    public Address anotherAddress;
    public Employee employeeInSession;

    protected void setup() {
        getAbstractSession().beginTransaction();

        // Read some object from the database.
        this.employeeInSession = (Employee)getSession().readAllObjects(Employee.class).firstElement();
        this.addressOfEmployeeInSession = employeeInSession.getAddress();
        this.addressOfEmployeeInSession.setCity("LKO");

        Employee employee = (Employee)getSession().readAllObjects(Employee.class).lastElement();
        this.anotherAddress = employee.getAddress();
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    protected void test() {
        // Acquire first unit of work
        this.unitOfWork = getSession().acquireUnitOfWork();

        this.unitOfWorkEmployeeCopy = (Employee)this.unitOfWork.registerObject(this.employeeInSession);
        this.unitOfWorkEmployeeCopy.setAddress(this.addressOfEmployeeInSession);

        try {
            this.unitOfWork.commit();
        } catch (QueryException exception) {
            if (exception.getErrorCode() != QueryException.BACKUP_CLONE_IS_ORIGINAL_FROM_PARENT) {
                throw exception;
            }
        }

        // Acquire first unit of work again for the next test
        this.unitOfWork = getSession().acquireUnitOfWork();

        this.unitOfWorkEmployeeCopy = (Employee)this.unitOfWork.registerObject(this.employeeInSession);
        this.unitOfWorkEmployeeCopy.setAddress(this.anotherAddress);

        try {
            this.unitOfWork.commit();
        } catch (QueryException exception) {
            if (exception.getErrorCode() != QueryException.BACKUP_CLONE_IS_ORIGINAL_FROM_PARENT) {
                throw exception;
            }
        }
    }
}
