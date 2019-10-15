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
package org.eclipse.persistence.testing.tests.performance.reading;

import org.eclipse.persistence.testing.models.bigbad.*;
import org.eclipse.persistence.testing.tests.performance.PerformanceTest;

/**
 * This tests the performance of read-object queries.
 * Its purpose is to compare the test result with previous release/label results.
 * It also provides a useful test for profiling performance.
 */
public class ReadObjectByPrimaryKeyBigBadObjectTest extends PerformanceTest {
    protected BigBadObject object;

    public ReadObjectByPrimaryKeyBigBadObjectTest() {
        setDescription("This tests the performance of read-object queries.");
    }

    /**
     * Find any address.
     */
    public void setup() {
        super.setup();
        object = (BigBadObject)getSession().readObject(BigBadObject.class);
        // Fully load the cache.
        allObjects = getSession().readAllObjects(BigBadObject.class);
    }

    /**
     * Read employee and clear the cache, test database read.
     */
    public void test() throws Exception {
        if (!shouldCache()) {
            getSession().getIdentityMapAccessor().removeFromIdentityMap(object);
        }
        getSession().readObject(object);
    }
}
