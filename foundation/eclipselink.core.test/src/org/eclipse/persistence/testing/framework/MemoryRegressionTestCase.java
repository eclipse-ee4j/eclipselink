/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.framework;

import java.util.*;

/**
 * Used to measure the amount of memory required for a task.
 */
public abstract class MemoryRegressionTestCase extends PerformanceRegressionTestCase {

    public MemoryRegressionTestCase() {
        this.testRunTime = DEFAULT_TEST_TIME;
        this.allowableDecrease = DEFAULT_ALLOWABLE_DECREASE;
        this.tests = new ArrayList();
    }
    
    /**
     * Executes this test measuring the memory delta.
     */
    public void executeTest() throws Throwable {
        PerformanceComparisonTestResult result = new PerformanceComparisonTestResult(this, "Passed");
        setTestResult(result);
        try {
            // Repeat the test and baseline for the number of repeats.
            for (int index = 0; index < REPEATS; index++) {
                long startMemory, endMemory;
                try {
                    startTest();
                    forceGC();
                    startMemory = Runtime.getRuntime().freeMemory();
                    test();
                    forceGC();
                    endMemory = Runtime.getRuntime().freeMemory();
                } finally {
                    endTest();
                }
                result.addTestCount(startMemory - endMemory, 0);

                for (int testIndex = 0; testIndex < getTests().size(); testIndex++) {
                    PerformanceComparisonTest test = (PerformanceComparisonTest)getTests().get(testIndex);
                    ((TestCase)test).setExecutor(getExecutor());
                    try {
                        test.startTest();
                        forceGC();
                        startMemory = Runtime.getRuntime().freeMemory();
                        endMemory = startMemory;
                        ((TestCase)test).test();
                        forceGC();
                        endMemory = Runtime.getRuntime().freeMemory();
                    } finally {
                        test.endTest();
                    }
                    result.addTestCount(startMemory - endMemory, testIndex + 1);
                }
            }
        } finally {
            result.computeResults();
        }
    }
}
