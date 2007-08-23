/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TransactionalTestCase;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


public class UnitOfWorkOldCommitIssueSQLBeforeCompletionTest extends TransactionalTestCase {

    public UnitOfWorkOldCommitIssueSQLBeforeCompletionTest() {
    }

    protected void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee emp = (Employee)uow.readObject(Employee.class);
        emp.setFirstName("Andy-Thorn");
        ((UnitOfWorkImpl)uow).setUseOldCommit(true);
        try {
            ((UnitOfWorkImpl)uow).issueSQLbeforeCompletion();
        } catch (NullPointerException ex) {
            throw new TestErrorException("The issue SQL Before Completion method called the incorrect internal method when using the old commit process");
        } finally {
            uow.release();
        }
    }
}
