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
package org.eclipse.persistence.testing.tests.sessionbroker;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.WriteObjectTest;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


public class SequnceSettingOnRemoteTest extends WriteObjectTest {
    public SequnceSettingOnRemoteTest() {
        setDescription("The test for sequence setup within the remote session");
    }

    protected void setup() {
        getAbstractSession().beginTransaction();
    }

    protected void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee newEmployee = new Employee();
        newEmployee.setFirstName("Al");
        newEmployee.setLastName("Gore");
        uow.assignSequenceNumber(newEmployee);
        if (newEmployee.getId() == null) {
            throw new TestErrorException("Sequencing setup at the remote session fails.");
        }
        uow.registerObject(newEmployee);

        uow.commit();
    }

    protected void verify() {

    }
}
