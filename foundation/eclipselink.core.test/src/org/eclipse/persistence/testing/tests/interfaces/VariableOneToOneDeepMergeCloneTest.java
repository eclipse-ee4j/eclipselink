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
package org.eclipse.persistence.testing.tests.interfaces;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.interfaces.*;

public class VariableOneToOneDeepMergeCloneTest extends TransactionalTestCase {
    public Employee emp;

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        this.emp = Employee.example1();
        this.emp.asset1.asset = Vehicle.example1();
        uow.registerObject(this.emp);
        uow.commit();
        uow = getSession().acquireUnitOfWork();
        Employee empClone = (Employee)uow.registerObject(this.emp);
        Employee newClone = (Employee)empClone.clone();
        ((Vehicle)newClone.asset1.asset).make = "Beamer";
        uow.deepMergeClone(newClone);
        uow.commit();
        if (!((AbstractSession)getSession()).compareObjects(newClone, this.emp)) {
            throw new TestErrorException("Failed to merge the Variable 1 to 1 properly");
        }
    }
}
