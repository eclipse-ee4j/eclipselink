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

import java.lang.reflect.Method;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the performance for final, synchronized and normal method execution.
 */
public class MethodTest extends PerformanceComparisonTestCase {
    protected int index = 0;

    public MethodTest() {
        setName("MethodExecution vs SynchronizedMethod PerformanceComparisonTest");
        setDescription("This test compares the performance for final, synchronized and normal method execution.");
        addSynchronizedTest();
        addSynchronizedBlockTest();
        addFinalTest();
        addReflectionTest();
        addInlinedTest();
        addNormalTest();
        addNormalTest2();
        addNormalTest3();
        addVolatileTest();
    }

    /**
     * Normal.
     */
    public void test() throws Throwable {
        for (int index = 0; index < 100; index++) {
            doTest();
        }
    }

    /**
     * Normal.
     */
    public void doTest() {
        // Do something simple.
        index = index + 10;
        if (index > 1000) {
            index = 0;
        }
    }

    /**
     * Synchronized.
     */
    public void addSynchronizedTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            protected int index2 = 0;
            
            public void test() {
                for (int index = 0; index < 100; index++) {
                    doTest();
                }
            }

            public synchronized void doTest() {
                // Do something simple.
                index2 = index2 + 10;
                if (index2 > 1000) {
                    index2 = 0;
                }
            }
        };
        test.setName("SynchronizedTest");
        test.setAllowableDecrease(-10);
        addTest(test);
    }

    /**
     * Synchronized block.
     */
    public void addSynchronizedBlockTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            protected int index2 = 0;
            
            public void test() {
                for (int index = 0; index < 100; index++) {
                    doTest();
                }
            }

            public void doTest() {
                synchronized (this) {
                    // Do something simple.
                    index2 = index2 + 10;
                    if (index2 > 1000) {
                        index2 = 0;
                    }
                }
            }
        };
        test.setName("SynchronizedBlockTest");
        addTest(test);
    }

    /**
     * Final.
     */
    public void addFinalTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            protected int index2 = 0;
            
            public final void test() {
                for (int index = 0; index < 100; index++) {
                    doTest();
                }
            }

            public final void doTest() {
                // Do something simple.
                index2 = index2 + 10;
                if (index2 > 1000) {
                    index2 = 0;
                }
            }
        };
        test.setName("FinalTest");
        addTest(test);
    }

    /**
     * Reflection.
     */
    public void addReflectionTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            protected int index2 = 0;
            
            public Method method;
            public Object[] args;

            /**
             * Allows any test specific setup before starting the test run.
             */
            public void startTest() {
                if (method == null) {
                    Class[] argTypes = {  };
                    try {
                        method = getClass().getMethod("doTest", argTypes);
                    } catch (Exception ignore) {
                        throw new RuntimeException(ignore);
                    }
                    args = new Object[0];
                }
            }
            
            public final void test() {
                for (int index = 0; index < 100; index++) {
                    try {
                        method.invoke(this, args);
                    } catch (Exception ignore) {
                        throw new RuntimeException(ignore);
                    }
                }
            }

            public void doTest() {
                // Do something simple.
                index2 = index2 + 10;
                if (index2 > 1000) {
                    index2 = 0;
                }
            }
        };
        test.setName("ReflectionTest");
        addTest(test);
    }
    
    /**
     * Inlined.
     */
    public void addInlinedTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            protected int index2 = 0;
            
            public void test() {
                for (int index = 0; index < 100; index++) {
                    // Do something simple.
                    index2 = index2 + 10;
                    if (index2 > 1000) {
                        index2 = 0;
                    }
                }
            }
        };
        test.setName("InlinedTest");
        addTest(test);
    }
    
    /**
     * Normal.
     */
    public void addNormalTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            
            public void test() {
                for (int index = 0; index < 100; index++) {
                    doTest();
                }
            }
            
            public void doTest() {
                // Do something simple.
                index = index + 10;
                if (index > 1000) {
                    index = 0;
                }
            }
        };
        test.setName("NormalTest");
        addTest(test);
    }
    
    /**
     * Normal.
     */
    public void addNormalTest2() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                for (int index = 0; index < 100; index++) {
                    MethodTest.this.doTest();
                }
            }
        };
        test.setName("NormalTest2");
        addTest(test);
    }
    
    /**
     * Normal.
     */
    public void addNormalTest3() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            protected int index2 = 0;
            
            public void test() {
                for (int index = 0; index < 100; index++) {
                    doTest();
                }
            }
            
            public void doTest() {
                // Do something simple.
                index2 = index2 + 10;
                if (index2 > 1000) {
                    index2 = 0;
                }
            }
        };
        test.setName("NormalTest3");
        addTest(test);
    }

    /**
     * Volatile.
     */
    public void addVolatileTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            protected volatile int index2 = 0;
            
            public void test() {
                for (int index = 0; index < 100; index++) {
                    doTest();
                }
            }
            
            public void doTest() {
                // Do something simple.
                index2 = index2 + 10;
                if (index2 > 1000) {
                    index2 = 0;
                }
            }
        };
        test.setName("VolatileTest");
        addTest(test);
    }
}
