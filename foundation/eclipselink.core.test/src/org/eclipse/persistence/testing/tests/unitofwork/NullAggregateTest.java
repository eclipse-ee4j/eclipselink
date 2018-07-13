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

import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TransactionalTestCase;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.EmploymentPeriod;


public class NullAggregateTest extends TransactionalTestCase {
    public org.eclipse.persistence.testing.models.employee.domain.Employee workingCopy;
    public org.eclipse.persistence.testing.models.employee.domain.Employee cloneCopy;
    public UnitOfWork unitOfWork;

    public NullAggregateTest() {
        setDescription("Test that the unit of work mergeClone still works when object identity is lost.");
    }

    public void reset() {
        super.reset();
    }

    public void setup() {
        super.setup();
        this.unitOfWork = getSession().acquireUnitOfWork();
        this.workingCopy = (Employee)getSession().readObject(Employee.class);
        this.cloneCopy = (Employee)unitOfWork.registerObject(this.workingCopy);
        this.cloneCopy.setPeriod(null);
        this.unitOfWork.commit();
        this.unitOfWork = getSession().acquireUnitOfWork();
        this.cloneCopy = (Employee)unitOfWork.registerObject(this.workingCopy);
    }

    public void test() {
        try {
            this.cloneCopy.setPeriod(new EmploymentPeriod(new java.sql.Date(System.currentTimeMillis()),
                                                          Helper.dateFromYearMonthDate(2001, 10, 15)));
            this.unitOfWork.commit();
        } catch (Exception exception) {
            throw new TestErrorException("Aggregate could not be assigned to an initially null field");
        }
    }

    public void verify() {
    }
}
