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
 * This test compares the performance between HashMap and Hashtable.
 */
public class LinkedHashMapTest extends PerformanceComparisonTestCase {
    public static int ITERATIONS = 50000;
    protected Object[] values;

    public LinkedHashMapTest() {
        setName("LinkedHashMap vs LinkedList PerformanceComparisonTest");
        setDescription("Compares the performance between LinkedHashMap and Hashtable with LinkedList.");
        addLinkedListTest();

        this.values = new Object[100];
        for (int size = 0; size < 100; size++) {
            this.values[size] = new Object();
        }
    }

    /**
     * LinkedHashMap.
     */
    public void test() throws Exception {
        LinkedHashMap map = new LinkedHashMap(10);
        for (int size = 0; size < 100; size++) {
            map.put(values[size], values[size]);
        }
    }

    /**
     * Linked List.
     */
    public void addLinkedListTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                LinkedList list = new LinkedList();
                for (int size = 0; size < 100; size++) {
                    list.add(values[size]);
                    if (list.size() > 10) {
                        list.removeFirst();
                    }
                }
            }
        };
        test.setName("LinkedListTest");
        test.setAllowableDecrease(40);
        addTest(test);
    }
}
