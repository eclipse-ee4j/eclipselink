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
package org.eclipse.persistence.testing.tests.mapping;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.mapping.Computer;
import org.eclipse.persistence.testing.models.mapping.Monitor;

public class BidirectionalUOWInsertAndDeleteTest extends AutoVerifyTestCase {
    public BidirectionalUOWInsertAndDeleteTest() {
        setDescription("Test bidirectional insert, then delete, in a unit of work.");
    }

    public void reset() {
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    protected void setup() {
        beginTransaction();

        UnitOfWork uow = getSession().acquireUnitOfWork();

        Computer computer = new Computer();
        Monitor monitor = Monitor.example1();
        computer.setDescription("IBM PS2");
        computer.notMacintosh();
        computer.setMonitor(monitor);
        computer.getMonitor().setComputer(computer);
        computer.setSerialNumber("1124345-1876212-2");

        uow.registerObject(computer);

        uow.commit();
    }

    protected void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();

        uow.deleteObject(uow.readObject(Computer.class, new ExpressionBuilder().get("serialNumber").equal("1124345-1876212-2")));
        uow.commit();
    }
}
