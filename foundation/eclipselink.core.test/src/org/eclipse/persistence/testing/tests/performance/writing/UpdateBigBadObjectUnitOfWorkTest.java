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
package org.eclipse.persistence.testing.tests.performance.writing;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.bigbad.*;
import org.eclipse.persistence.testing.tests.performance.PerformanceTest;

/**
 * This tests the performance of unit of work updates.
 * Its purpose is to compare the test result with previous release/label results.
 * It also provides a useful test for profiling performance.
 */
public class UpdateBigBadObjectUnitOfWorkTest extends PerformanceTest {
    protected BigBadObject object;

    public UpdateBigBadObjectUnitOfWorkTest() {
        setDescription("This tests the performance of unit of work updates.");
    }

    /**
     * Find any employee.
     */
    public void setup() {
        super.setup();
        object = (BigBadObject)getSession().readObject(BigBadObject.class);
    }

    /**
     * Read address and clear the cache, test database read.
     */
    public void test() throws Exception {
        super.test();
        UnitOfWork uow = getSession().acquireUnitOfWork();
        BigBadObject object = (BigBadObject)uow.readObject(this.object);
        if (object.string07.equals("Toronto")) {
            object.string07 = "Ottawa";
        } else {
            object.string07 = "Toronto";
        }
        uow.commit();
    }
}
