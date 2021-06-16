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
