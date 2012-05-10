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
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the performance of for loops.
 */
public class ForLoopTest extends PerformanceComparisonTestCase {
    public Vector list;
    public List arrayList;

    public ForLoopTest() {
        setName("ForLoop vs Enumeration PerformanceComparisonTest");
        setDescription("This test compares the performance of index for loops vs enumeration/iterators.");
        addIndexArrayListTest();
        addEnumeratorTest();
        addIteratorTest();
        addIteratorArrayListTest();
        addForTest();
        addForArrayListTest();
        list = new Vector();
        arrayList = new ArrayList();
        for (int index = 0; index < 100; index++) {
            list.add(new Object());
            arrayList.add(new Object());
        }
    }

    /**
     * index loop.
     */
    public void test() throws Exception {
        int size = list.size();
        for (int index = 0; index < size; index++) {
            Object object = list.get(index);
            object.hashCode();
        }
    }

    /**
     * enumeration loop.
     */
    public void addEnumeratorTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                for (Enumeration enumtr = list.elements(); enumtr.hasMoreElements();) {
                    Object object = enumtr.nextElement();
                    object.hashCode();
                }
            }
        };
        test.setName("EnumerationTest");
        test.setAllowableDecrease(-10);
        addTest(test);
    }

    /**
     * iterator loop.
     */
    public void addIteratorTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                for (Iterator iter = list.iterator(); iter.hasNext();) {
                    Object object = iter.next();
                    object.hashCode();
                }
            }
        };
        test.setName("IteratorTest");
        test.setAllowableDecrease(-70);
        addTest(test);
    }

    /**
     * iterator loop.
     */
    public void addIteratorArrayListTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                for (Iterator iter = arrayList.iterator(); iter.hasNext();) {
                    Object object = iter.next();
                    object.hashCode();
                }
            }
        };
        test.setName("ArrayListIteratorTest");
        test.setAllowableDecrease(-70);
        addTest(test);
    }

    /**
     * for loop.
     */
    public void addForArrayListTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                for (Object object : arrayList) {
                    object.hashCode();
                }
            }
        };
        test.setName("ForArrayListTest");
        test.setAllowableDecrease(-70);
        addTest(test);
    }

    /**
     * for loop.
     */
    public void addForTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                for (Object object : list) {
                    object.hashCode();
                }
            }
        };
        test.setName("ForTest");
        test.setAllowableDecrease(-70);
        addTest(test);
    }

    /**
     * index loop.
     */
    public void addIndexArrayListTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                int size = arrayList.size();
                for (int index = 0; index < size; index++) {
                    Object object = arrayList.get(index);
                    object.hashCode();
                }
            }
        };
        test.setName("IndexArrayListTest");
        test.setAllowableDecrease(-70);
        addTest(test);
    }
}
