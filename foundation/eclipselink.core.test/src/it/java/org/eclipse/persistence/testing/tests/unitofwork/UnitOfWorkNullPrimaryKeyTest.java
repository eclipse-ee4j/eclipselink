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

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


/**
 * This test verifies that setting a primary key field to null in a
 * unit of work clone will yeild a validation exception on commit.
 */
public class UnitOfWorkNullPrimaryKeyTest extends AutoVerifyTestCase {

    public UnitOfWorkNullPrimaryKeyTest() {
        setDescription("Tests to ensure the correct exception is thrown when a PK is set to null on a UOW clone.");
    }

    public void test() {
        Session session = getSession();
        UnitOfWork uow = session.acquireUnitOfWork();

        Employee emp = (Employee)uow.readObject(Employee.class);
        emp.setId(null);
        Exception exception = null;
        try {
            uow.commit();
        } catch (Exception expectedException) {
            exception = expectedException;
        }
        if ((exception == null) || (!(exception instanceof ValidationException))) {
            throw new TestErrorException("The expected Validation exception was not thrown", exception);
        }
    }
}
