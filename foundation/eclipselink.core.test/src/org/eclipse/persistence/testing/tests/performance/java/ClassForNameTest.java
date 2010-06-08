/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
