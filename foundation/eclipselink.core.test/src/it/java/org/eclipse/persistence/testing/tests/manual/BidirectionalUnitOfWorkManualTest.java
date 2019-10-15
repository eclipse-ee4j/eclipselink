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
package org.eclipse.persistence.testing.tests.manual;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.mapping.*;

public class BidirectionalUnitOfWorkManualTest extends ManualVerifyTestCase {
    public BidirectionalUnitOfWorkManualTest() {
        setDescription("In bidirectional insert without sequencing the first insert statement should not include foreign key field.");
    }

    protected void setup() {
        beginTransaction();
    }

    public void reset() {
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    protected void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();

        Computer example = new Computer();
        example.setDescription("IBM PS2");
        example.notMacintosh();
        example.setMonitor(Monitor.example1());
        example.getMonitor().setComputer(example);
        example.setSerialNumber("1124345-1876212-2");

        uow.registerObject(example);
        uow.commit();
    }
}
