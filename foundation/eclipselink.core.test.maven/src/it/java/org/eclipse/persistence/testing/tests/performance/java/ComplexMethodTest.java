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
package org.eclipse.persistence.testing.tests.performance.java;

import java.util.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the performance for final, synchronized and normal method execution.
 */
public class ComplexMethodTest extends PerformanceComparisonTestCase {
    protected HashMap map;

    public ComplexMethodTest() {
        setName("ComplexMethodExecution vs SynchronizedMethod PerformanceComparisonTest");
        setDescription("This test compares the performance for final, synchronized and normal method execution.");
        addSynchronizedTest();
        addSynchronizedBlockTest();
        addFinalTest();

        this.map = new HashMap();
        this.map.put(this, this);
    }

    /**
     * Normal.
     */
    public void test() throws Exception {
        Object value = this.map.get(this);
        this.map.put(this, value);
    }

    /**
     * Synchronized.
     */
    public void addSynchronizedTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public synchronized void test() {
                Object value = ComplexMethodTest.this.map.get(this);
                ComplexMethodTest.this.map.put(this, value);
            }
        };
        test.setName("SynchronizedTest");
        test.setAllowableDecrease(-200);
        addTest(test);
    }

    /**
     * Synchronized block.
     */
    public void addSynchronizedBlockTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                synchronized (this) {
                    Object value = ComplexMethodTest.this.map.get(this);
                    ComplexMethodTest.this.map.put(this, value);
                }
            }
        };
        test.setName("SynchronizedBlockTest");
        test.setAllowableDecrease(-200);
        addTest(test);
    }

    /**
     * Final.
     */
    public void addFinalTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public final void test() {
                Object value = ComplexMethodTest.this.map.get(this);
                ComplexMethodTest.this.map.put(this, this);
            }
        };
        test.setName("FinalTest");
        addTest(test);
    }
}
