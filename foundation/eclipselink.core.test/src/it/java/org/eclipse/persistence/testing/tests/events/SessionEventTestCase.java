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
package org.eclipse.persistence.testing.tests.events;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * Test that session/uow events work.
 */
public class SessionEventTestCase extends TransactionalTestCase {
    public TestSessionListener listener;

    public void reset() {
        super.reset();
        getSession().getEventManager().removeListener(this.listener);
    }

    public void setup() {
        super.setup();
        this.listener = new TestSessionListener();
        getSession().getEventManager().addListener(this.listener);
    }

    public void test() {
        // Bug 2834266 ensure UnitOfWork is not empty so transaction actually runs.
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee emp = (Employee)uow.readObject(Employee.class);
        emp.setFirstName(emp.getFirstName() + "-modified");
        uow.commit();
    }

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
    }
}
