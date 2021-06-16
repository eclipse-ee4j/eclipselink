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

import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


public class PrepareUnitOfWorkTest extends UnitOfWorkEventTest {

    public void setup() {
        super.setup();
        setDescription("Test the prepareUnitOfWork Event");
        SessionEventAdapter tvAdapter = new SessionEventAdapter() {
                // Listen for PrepareUnitOfWorkEvents

                public void prepareUnitOfWork(SessionEvent event) {
                    setEventTriggered(true);
                }
            };
        getSession().getEventManager().addListener(tvAdapter);
    }

    public void test() {
        UnitOfWork tvUnitOfWork = getSession().acquireUnitOfWork();
        Employee employee = (Employee)tvUnitOfWork.readObject(Employee.class);

        // Bug 2834266 ensure UnitOfWork is not empty so transaction actually runs.
        employee.setFirstName(employee.getFirstName() + "modified");
        tvUnitOfWork.commit();
    }

    public void verify() {
        if (getSession().isRemoteSession()) {
            throw new TestWarningException("This feature is not supported at remoteSession");
        }
        if (!isEventTriggered()) {
            throw new TestErrorException("The prepareUnitOfWork event was not triggered.");
        }
    }
}
