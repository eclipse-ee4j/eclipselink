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
