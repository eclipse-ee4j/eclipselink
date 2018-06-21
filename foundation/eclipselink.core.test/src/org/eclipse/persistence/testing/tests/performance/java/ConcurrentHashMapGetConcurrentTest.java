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

import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.persistence.testing.framework.*;

/**
 * Measure the concurrency of ConcurrentHashMap.
 */
public class ConcurrentHashMapGetConcurrentTest extends ConcurrentPerformanceComparisonTest {
    protected ConcurrentHashMap map;
    protected Integer[] keys = new Integer[100];

    public ConcurrentHashMapGetConcurrentTest() {
        setDescription("Measure the concurrency of ConcurrentHashMap.");
        for (int index = 0; index < 100; index ++) {
            this.keys[index] = new Integer(index);
        }
    }

    public void setup() {
        super.setup();
        map = new ConcurrentHashMap(100);
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
