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

import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the performance between .class and a static variable.
 */
public class ClassForNameTest extends PerformanceComparisonTestCase {
    public static Class TOPLINK = org.eclipse.persistence.Version.class;

    public ClassForNameTest() {
        setName("static vs class forName PerformanceComparisonTest");
        setDescription("Compares the performance between using class forName, .class and a static variable for the class.");
        addStaticClassTest();
        addClassForNameTest();
    }

    /**
     * .class.
     */
    public void test() throws Exception {
        TOPLINK.getName();
    }

    /**
     * Static.
     */
    public void addStaticClassTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                org.eclipse.persistence.Version.class.getName();
            }
        };
        test.setName(".classTest");
        addTest(test);
    }

    /**
     * Class.forName.
     */
    public void addClassForNameTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() throws Exception {
                Class.forName("org.eclipse.persistence.Version").getName();
            }
        };
        test.setName("ClassForNameTest");
        test.setAllowableDecrease(-90);
        addTest(test);
    }
}
