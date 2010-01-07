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

import java.lang.reflect.Method;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the performance for final, synchronized and normal method execution.
 */
public class MethodTest extends PerformanceComparisonTestCase {
    protected int index = 0;
    protected volatile int vIndex = 0;

    public MethodTest() {
        setName("MethodExecution vs SynchronizedMethod PerformanceComparisonTest");
        setDescription("This test compares the performance for final, synchronized and normal method execution.");
        addSynchronizedTest();
        addFinalTest();
        addReflectionTest();
        addInlinedTest();
        addNormalTest();
        addVolatileTest();
    }

    /**
     * Normal.
     */
    public void test() throws Exception {
        for (int index = 0; index < 100; index++) {
            doTest();
        }
    }

    /**
     * Normal.
     */
    public void doTest() throws Exception {
        // Do something simple.
        index = 0;
        index = index + 10;
    }

    /**
     * Synchronized.
     */
    public void addSynchronizedTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                for (int index = 0; index < 100; index++) {
                    doTest();
                }
            }

            public synchronized void doTest() {
                // Do something simple.
                index = 0;
                index = index + 10;
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
            public void test() {
                for (int index = 0; index < 100; index++) {
                    doTest();
                }
            }

            public void doTest() {
                synchronized (this) {
                    // Do something simple.
                    index = 0;
                    index = index + 10;
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
            public final void test() {
                for (int index = 0; index < 100; index++) {
                    doTest();
                }
            }

            public final void doTest() {
                // Do something simple.
                index = 0;
                index = index + 10;
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
            public Method method;
            public Object[] args;

            public final void test() {
                for (int index = 0; index < 100; index++) {
                    if (method == null) {
                        Class[] argTypes = {  };
                        try {
                            method = getClass().getMethod("doTest", argTypes);
                        } catch (Exception ignore) {
                        }
                        args = new Object[0];
                    }
                    try {
                        method.invoke(this, args);
                    } catch (Exception ignore) {
                    }
                }
            }

            public final void doTest() {
                // Do something simple.
                index = 0;
                index = index + 10;
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
            public void test() {
                for (int index = 0; index < 100; index++) {
                    // Do something simple.
                    MethodTest.this.index = 0;
                    MethodTest.this.index = MethodTest.this.index + 10;
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
            
            public final void doTest() {
                // Do something simple.
                index = 0;
                index = index + 10;
            }
        };
        test.setName("NormalTest");
        addTest(test);
    }

    /**
     * Volatile.
     */
    public void addVolatileTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                doTest();
            }
            
            public final void doTest() {
                // Do something simple.
                vIndex = 0;
                vIndex = vIndex + 10;
            }
        };
        test.setName("VolatileTest");
        addTest(test);
    }
}
