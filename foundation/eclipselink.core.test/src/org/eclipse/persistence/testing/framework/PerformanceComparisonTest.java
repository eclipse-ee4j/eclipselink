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
 package org.eclipse.persistence.testing.framework;

import java.util.*;

/**
 * Performance test interface used for sharing behavoir and
 * result with EJB and core.
 */
public interface PerformanceComparisonTest extends TestEntity {
    /** Number of times to repeat the test run (to compute average/stddv) */
    public static int REPEATS = 5;

    /** Default total time to run each test run (default is 2s). */
    public static int DEFAULT_TEST_TIME = 10000;

    /** Default for allowable % difference between tests and baseline test. */
    public static double DEFAULT_ALLOWABLE_DECREASE = -5;

    /**
     * Return the count of the iterations.
     * This is maintained by the test to allow concurrent tests to manage the count.
     */
    public int getIterations();

    /**
     * Increment the iteration count.
     * This is maintained by the test to allow concurrent tests to manage the count.
     */
    public void incrementIterations();

    /**
     * Reset the iteration count.
     * This is maintained by the test to allow concurrent tests to manage the count.
     */
    public void resetIterations();

    /**
     * Allows any test specific setup before starting the test run.
     */
    public void startTest();

    /**
     * Allows any test specific setup before starting the test run.
     */
    public void endTest();

    /**
     * Return the total time (ms) to run each of the test runs.
     * This is used to compute the number of test iterations to use,
     * the tests are then still timed based on that number of iterations.
     */
    public long getTestRunTime();

    /**
     * Set the total time (ms) to run each of the test runs.
     * This is used to compute the number of test iterations to use,
     * the tests are then still timed based on that number of iterations.
     */
    public void setTestRunTime(long testRunTime);

    /**
     * Return the allowable percentage difference for test compared to the baseline.
     */
    public double getAllowableDecrease();

    /**
     * Set the allowable percentage difference for test compared to the baseline.
     */
    public void setAllowableDecrease(double allowableDecrease);

    /**
     * Return the performance tests to compare the base-line with.
     */
    public List getTests();

    /**
     * Add a performance tests to compare the base-line with.
     */
    public void addTest(TestCase test);

}
