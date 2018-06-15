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

import java.lang.reflect.*;
//import sun.misc.Unsafe;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the performance for field reflection.
 */
public class FieldTest extends PerformanceComparisonTestCase {
    protected Object variable;

    public FieldTest() {
        setName("FieldAccess vs Reflection PerformanceComparisonTest");
        setDescription("This test compares the performance for field reflection.");
        addSetMethodTest();
        addReflectionFieldTest();
        addReflectionMethodTest();
        //addUnsafeFieldTest();
        addAssignableSetTest();
    }

    /**
     * Normal.
     */
    public void test() throws Exception {
        variable = new String("hello");
    }

    /**
     * Set method.
     */
    public void addSetMethodTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            protected Object variable;

            public void test() {
                setVariable(new String("hello"));
            }

            /**
             * Normal.
             */
            public void setVariable(Object value) {
                variable = value;
            }
        };
        test.setName("SetMethodTest");
        addTest(test);
    }

    /**
     * Assignable set.
     */
    public void addAssignableSetTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            protected Object variable;

            public Class object = Object.class;

            public void test() {
                if (!this.getClass().isAssignableFrom(this.getClass())) {
                    throw new Error("Not assignable");
                }
                String hello = new String("hello");
                if (!object.isAssignableFrom(hello.getClass())) {
                    throw new Error("Not assignable");
                }
                variable = hello;
            }
        };
        test.setName("AssignableSetTest");
        addTest(test);
    }

    /**
     * Reflection method.
     */
    public void addReflectionMethodTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            protected Object variable;

            public Method method;

            public final void test() {
                if (method == null) {
                    Class[] argTypes = { Object.class };
                    try {
                        method = getClass().getMethod("setVariable", argTypes);
                    } catch (Exception ignore) {
                        throw new Error(ignore.toString());
                    }
                }
                try {
                    Object[] args = new Object[1];
                    args[0] = new String("hello");
                    method.invoke(this, args);
                } catch (Exception ignore) {
                    throw new Error(ignore.toString());
                }
            }

            /**
             * Normal.
             */
            public void setVariable(Object value) throws Exception {
                variable = value;
            }
        };
        test.setName("ReflectionMethodTest");
        addTest(test);
    }

    /**
     * Reflection field.
     */
    public void addReflectionFieldTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            protected Object variable;

            public Field field;

            public final void test() {
                if (field == null) {
                    try {
                        field = getClass().getDeclaredField("variable");
                        field.setAccessible(true);
                    } catch (Exception ignore) {
                        throw new Error(ignore.toString());
                    }
                }
                try {
                    field.set(this, new String("hello"));
                } catch (Exception ignore) {
                    throw new Error(ignore.toString());
                }
            }
        };
        test.setName("ReflectionFieldTest");
        addTest(test);
    }

    /**
     * Unsafe field.
     *
    public void addUnsafeFieldTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public Field field;
            public Object[] args;

            public final void test() {
                try {
                    Unsafe.getUnsafe().putObject(this, 0L, new String("hello"));
                } catch (Exception ignore) {
                    throw new Error(ignore.toString());
                }
            }
        };
        test.setName("UnsafeFieldTest");
        addTest(test);
    }*/
}
