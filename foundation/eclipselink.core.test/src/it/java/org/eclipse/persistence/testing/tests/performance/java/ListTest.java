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
package org.eclipse.persistence.testing.tests.performance.java;

import java.util.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the performance between ArrayList and Vector.
 */
public class ListTest extends PerformanceComparisonTestCase {
    protected int size;

    public ListTest(int size) {
        this.size = size;
        setName("Vector vs Lists size " + size + " PerformanceComparisonTest");
        setDescription("Compares the performance between ArrayList and Vector.");
        addArrayListTest();
        addLinkedListTest();
    }

    /**
     * Vector.
     */
    public void test() throws Exception {
        Vector vector = new Vector(10);
        for (int index = 0; index < size; index++) {
            vector.add(new Integer(index));
        }
        for (int index = 0; index < size; index++) {
            Object result = vector.get(index);
        }
    }

    /**
     * ArrayList.
     */
    public void addArrayListTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                List list = new ArrayList(10);
                for (int index = 0; index < size; index++) {
                    list.add(new Integer(index));
                }
                for (int index = 0; index < size; index++) {
                    Object result = list.get(index);
                }
            }
        };
        test.setName("ArrayListTest");
        test.setAllowableDecrease(-10);
        addTest(test);
    }

    /**
     * LinkedList.
     */
    public void addLinkedListTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                List list = new LinkedList();
                for (int index = 0; index < size; index++) {
                    list.add(new Integer(index));
                }
                for (int index = 0; index < size; index++) {
                    Object result = list.get(index);
                }
            }
        };
        test.setName("LinkedListTest");
        test.setAllowableDecrease(-150);
        addTest(test);
    }
}
