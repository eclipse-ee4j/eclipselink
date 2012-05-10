/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.performance.java;

import java.util.*;

import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the performance between HashMap and Hashtable.
 */
public class MapTest extends PerformanceComparisonTestCase {
    protected Object[] values;
    protected int size;

    public MapTest(int size) {
        this.size = size;
        setName("Hashtable vs Maps size " + size + " PerformanceComparisonTest");
        setDescription("Compares the performance between HashMap and Hashtable.");
        addHashtable2Test();
        addHashMapTest();
        addLinkedMapTest();
        addIdentityHashMapTest();
        addHashSetTest();
        addConcurrentHashMapTest();

        this.values = new Object[size];
        for (int index = 0; index < size; index++) {
            this.values[index] = new Object();
        }
    }

    /**
     * Hashtable.
     */
    public void test() throws Exception {
        Hashtable table = new Hashtable(10);
        for (int index = 0; index < size; index++) {
            table.put(values[index], values[index]);
        }
        for (int index = 0; index < size; index++) {
            Object result = table.get(values[index]);
        }
        for (int index = 0; index < size; index++) {
            Object result = table.remove(values[index]);
        }
    }

    /**
     * HashMap.
     */
    public void addHashMapTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
                public void test() {
                    Map map = new HashMap(10);
                    for (int index = 0; index < size; index++) {
                        map.put(values[index], values[index]);
                    }
                    for (int index = 0; index < size; index++) {
                        Object result = map.get(values[index]);
                    }
                    for (int index = 0; index < size; index++) {
                        Object result = map.remove(values[index]);
                    }
                }
            };
        test.setName("HashMapTest");
        addTest(test);
    }

    /**
     * HashMap.
     */
    public void addHashtable2Test() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
                public void test() {
                    Map map = new Hashtable(10);
                    for (int index = 0; index < size; index++) {
                        map.put(values[index], values[index]);
                    }
                    for (int index = 0; index < size; index++) {
                        Object result = map.get(values[index]);
                    }
                    for (int index = 0; index < size; index++) {
                        Object result = map.remove(values[index]);
                    }
                }
            };
        test.setName("Hashtable2Test");
        addTest(test);
    }

    /**
     * LinkedMap.
     */
    public void addLinkedMapTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
                public void test() {
                    Map map = new LinkedHashMap(10);
                    for (int index = 0; index < size; index++) {
                        map.put(values[index], values[index]);
                    }
                    for (int index = 0; index < size; index++) {
                        Object result = map.get(values[index]);
                    }
                    for (int index = 0; index < size; index++) {
                        Object result = map.remove(values[index]);
                    }
                }
            };
        test.setName("LinkedMapTest");
        test.setAllowableDecrease(-20);
        addTest(test);
    }

    /**
     * IdentityHashMap.
     */
    public void addIdentityHashMapTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
                public void test() {
                    Map map = new IdentityHashMap(10);
                    for (int index = 0; index < size; index++) {
                        map.put(values[index], values[index]);
                    }
                    for (int index = 0; index < size; index++) {
                        Object result = map.get(values[index]);
                    }
                    for (int index = 0; index < size; index++) {
                        Object result = map.remove(values[index]);
                    }
                }
            };
        test.setName("IdentityHashMapTest");
        test.setAllowableDecrease(-60);
        addTest(test);
    }

    /**
     * ConcurrentHashMap.
     */
    public void addConcurrentHashMapTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
                public void test() {
                    Map map = new ConcurrentHashMap(10);
                    for (int index = 0; index < size; index++) {
                        map.put(values[index], values[index]);
                    }
                    for (int index = 0; index < size; index++) {
                        Object result = map.get(values[index]);
                    }
                    for (int index = 0; index < size; index++) {
                        Object result = map.remove(values[index]);
                    }
                }
            };
        test.setName("ConcurrentHashMapTest");
        test.setAllowableDecrease(-5);
        addTest(test);
    }

    /**
     * HashSet.
     */
    public void addHashSetTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
                public void test() {
                    Set map = new HashSet(10);
                    for (int index = 0; index < size; index++) {
                        map.add(values[index]);
                    }
                    for (int index = 0; index < size; index++) {
                        boolean result = map.contains(values[index]);
                    }
                    for (int index = 0; index < size; index++) {
                        boolean result = map.remove(values[index]);
                    }
                }
            };
        test.setName("HashSetTest");
        test.setAllowableDecrease(0);
        addTest(test);
    }
}
