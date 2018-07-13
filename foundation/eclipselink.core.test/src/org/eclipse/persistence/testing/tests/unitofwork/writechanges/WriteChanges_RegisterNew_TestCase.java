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
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


/**
 * @author  smcritch
 */
public class WriteChanges_RegisterNew_TestCase extends AutoVerifyTestCase {
    protected Exception exception;

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        try {
            uow.writeChanges();
            uow.registerNewObject(new Employee());
        } catch (Exception e) {
            exception = e;
        } finally {
            uow.release();
        }
    }

    public void verify() {
        if (exception == null) {
            throw new TestErrorException("Exception not thrown attempting to registerNewObject after writeChanges.");
        } else if (!(exception instanceof ValidationException)) {
            throw new TestErrorException("Wrong exception type thrown.", exception);
        } else {
            ValidationException ve = (ValidationException)exception;
            if (ve.getErrorCode() != ValidationException.UNIT_OF_WORK_IN_TRANSACTION_COMMIT_PENDING) {
                throw new TestErrorException("Wrong exception thrown.", ve);
            }
        }
    }

    public void reset() {
        exception = null;
    }
}
