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

import java.util.Vector;

import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TransactionalTestCase;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


public class CanChangeReadOnlySetTest extends TransactionalTestCase {
    public EclipseLinkException testException;

    public CanChangeReadOnlySetTest() {
        setDescription("Test the canChangeReadOnlySet flag to verify functionality.");
    }

    public void test() {
        try {
            Employee employeeFromCache = (Employee)getSession().readObject(Employee.class);
            UnitOfWork uow = getSession().acquireUnitOfWork();
            uow.registerObject(employeeFromCache);
            Vector vector = new Vector(1);
            vector.add(Employee.class);
            uow.addReadOnlyClasses(vector);
        } catch (ValidationException exception) {
            this.testException = exception;
        }
    }

    public void verify() {
        if ((this.testException == null) || (this.testException.getErrorCode() != 7040)) {
            throw new TestErrorException("No exception or incorrect exception thrown.  Expected ValidationException cannotModifyReadOnlyClassesSetAfterUsingUnitOfWork got:" +
                                         this.testException);
        }
    }
}
