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
package org.eclipse.persistence.testing.tests.unitofwork.writechanges;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.queries.UpdateObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


/**
 * @author  smcritch
 */
public class WriteChanges_OLModifyQuery_TestCase extends AutoVerifyTestCase {
    protected Exception exception;

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        try {
            Employee employee = (Employee)uow.readObject(Employee.class);
            uow.writeChanges();

            employee.setFirstName("Stephen");
            uow.executeQuery(new UpdateObjectQuery(employee));
        } catch (Exception e) {
            exception = e;
        } finally {
            uow.release();
        }
    }

    public void verify() {
        if (exception == null) {
            throw new TestErrorException("Exception not thrown when attempting an ObjectLevelDeleteAllQuery after writeChanges().");
        } else if (!(exception instanceof ValidationException)) {
            throw new TestErrorException("Wrong exception thrown.", exception);
        }
    }

    public void reset() {
        exception = null;
    }
}
