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
package org.eclipse.persistence.testing.tests.performance.reading;

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.performance.Address;
import org.eclipse.persistence.testing.tests.performance.PerformanceTest;

/**
 * This tests the performance of read-object queries.
 * Its purpose is to compare the test result with previous release/label results.
 * It also provides a useful test for profiling performance.
 */
public class ReadObjectByPrimaryKeyAddressTest extends PerformanceTest {
    protected Address address;

    public ReadObjectByPrimaryKeyAddressTest() {
        setDescription("This tests the performance of read-object queries.");
    }

    /**
     * Find any address.
     */
    public void setup() {
        super.setup();
        Expression expression = new ExpressionBuilder().get("postalCode").equal("L5J2B5");
        address = (Address)getSession().readObject(Address.class, expression);
        // Fully load the cache.
        allObjects = getSession().readAllObjects(Address.class);
    }

    /**
     * Read employee and clear the cache, test database read.
     */
    public void test() throws Exception {
        if (!shouldCache()) {
            getSession().getIdentityMapAccessor().removeFromIdentityMap(address);
        }
        getSession().readObject(address);
    }
}
