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
package org.eclipse.persistence.testing.tests.performance.java;

import java.util.HashMap;
import org.eclipse.persistence.testing.framework.*;

/**
 * Measure the concurrency of HashMap.
 */
public class HashMapGetConcurrentTest extends ConcurrentPerformanceComparisonTest {
    protected HashMap map;
    protected Integer[] keys = new Integer[100];

    public HashMapGetConcurrentTest() {
        setDescription("Measure the concurrency of HashMap.");
        for (int index = 0; index < 100; index ++) {
            this.keys[index] = new Integer(index);
        }
    }

    public void setup() {
        super.setup();
        map = new HashMap(100);
        for (int index = 0; index < 100; index++) {
            map.put(new Integer(index), new Integer(index));
        }
    }

    public void runTask() throws Exception {
        for (int index = 0; index < 100; index ++) {
            Integer value = (Integer)this.map.get(this.keys[index]);
        }
    }
}
