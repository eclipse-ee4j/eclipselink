/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.events;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TransactionalTestCase;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

/**
 * Test that session/uow events work.
 */
public class SessionEventTestCase extends TransactionalTestCase {
    public TestSessionListener listener;

    @Override
    public void reset() {
        super.reset();
        getSession().getEventManager().removeListener(this.listener);
    }

    @Override
    public void setup() {
        super.setup();
        this.listener = new TestSessionListener();
        getSession().getEventManager().addListener(this.listener);
    }

    @Override
    public void test() {
        // Bug 2834266 ensure UnitOfWork is not empty so transaction actually runs.
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee emp = (Employee)uow.readObject(Employee.class);
        emp.setFirstName(emp.getFirstName() + "-modified");
        uow.writeChanges();
        uow.commit();
    }

    @Override
    public void verify() {
        if (!this.listener.preCalculateUnitOfWork) {
            throw new TestErrorException(" The pre Calculate UnitOfWork ChangeSet event did not fire");
        }
        if (!this.listener.postCalculateUnitOfWork) {
            throw new TestErrorException("The post Calculate UnitOfWork ChangeSet event did not fire");
        }
        if (!this.listener.postCommitUnitOfWork) {
            throw new TestErrorException("The post commit event was not raised.");
        }
        if (this.listener.postAcquireClientSession) {
            throw new TestErrorException("The post acquire event was not raised but should not have been.");
        }
        if(!this.listener.preFlushUnitOfWork) {
            throw new TestErrorException("The pre UnitOfWork flush event did not fire");
        }
        if(!this.listener.postFlushUnitOfWork) {
            throw new TestErrorException("The post UnitOfWork flush event did not fire");
        }
    }
}
