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

import java.util.*;
import org.eclipse.persistence.internal.identitymaps.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the performance between deferred and normal locking.
 */
public class DeferredvsWriteLockTest extends PerformanceComparisonTestCase {
    public DeferredvsWriteLockTest() {
        setName("DeferredvsWriteLockTest");
        setDescription("This test compares the performance between deferred and normal locking.");
        addLockTest();
    }

    /**
     * Acquire deferred lock.
     */
    public void test() throws Exception {
        CacheKey key = new CacheKey(new Vector());
        key.acquireDeferredLock();
        key.releaseDeferredLock();
    }

    /**
     * Acquire lock.
     */
    public void addLockTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                CacheKey key = new CacheKey(new Vector());
                key.acquire();
                key.release();
            }
        };
        test.setName("LockTest");
        test.setAllowableDecrease(20);
        addTest(test);
    }
}
