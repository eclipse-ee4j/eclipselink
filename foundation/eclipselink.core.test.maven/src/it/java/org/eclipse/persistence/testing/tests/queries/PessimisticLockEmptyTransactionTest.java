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
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * Test for Bug 2834266 - tests a change made to the bug fix after the fix was checked in.
 * Ensures proper clean up occurs for transactions that are begun prematurely but do not make
 * any changes.
 * Uses pessimistic locking as a means of getting a transaction to begin prematurely
 */
public class PessimisticLockEmptyTransactionTest extends AutoVerifyTestCase {
    protected UnitOfWork uow = null;

    // anonymous inner class for event handling.
    protected SessionEventAdapter eventAdapter = new SessionEventAdapter() {
        public void postCommitUnitOfWork(SessionEvent event) {
            if (((AbstractSession)event.getSession()).isInTransaction()) {
                stillInTransaction();
            }
        }
    };
    protected boolean stillInTransaction = false;

    public PessimisticLockEmptyTransactionTest() {
        setDescription("Test to ensure that transactions using Pessimistic Locking which do not " + " make modifications properly close their transactions.");
    }

    public void stillInTransaction() {
        stillInTransaction = true;
    }

    public void setup() {
        checkSelectForUpateSupported();
        // HANA supports SELECT FOR UPDATE but not with queries that select from multiple tables
        if (getSession().getPlatform().isHANA()) {
            throw new TestWarningException("This database does not support FOR UPDATE on multiple tables");
        }

        getSession().getEventManager().addListener(eventAdapter);
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        stillInTransaction = false;
    }

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee employeeObject = (Employee)uow.readObject(Employee.class);
        uow.refreshAndLockObject(employeeObject);
        uow.commit();
    }

    public void verify() {
        if (stillInTransaction) {
            throw new TestErrorException("Unit of Work Commit did not close the transaction for empty transaction using Pessimistic Locking.");
        }
    }

    public void reset() {
        getSession().getEventManager().removeListener(eventAdapter);
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        // If lock failed must ensure transaction is rolledback.
        if (getAbstractSession().isInTransaction()) {
            rollbackTransaction();
        }
    }
}
