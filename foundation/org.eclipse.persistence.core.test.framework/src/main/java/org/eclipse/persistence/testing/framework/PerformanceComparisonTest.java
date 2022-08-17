/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
 package org.eclipse.persistence.testing.framework;

import java.util.List;

/**
 * Performance test interface used for sharing behavoir and
 * result with EJB and core.
 */
public interface PerformanceComparisonTest extends TestEntity {
    /** Number of times to repeat the test run (to compute average/stddv) */
    int REPEATS = 5;

    /** Default total time to run each test run (default is 2s). */
    int DEFAULT_TEST_TIME = 10000;

    /** Default for allowable % difference between tests and baseline test. */
    double DEFAULT_ALLOWABLE_DECREASE = -5;

    /**
     * Return the count of the iterations.
     * This is maintained by the test to allow concurrent tests to manage the count.
     */
    int getIterations();

    /**
     * Increment the iteration count.
     * This is maintained by the test to allow concurrent tests to manage the count.
     */
    void incrementIterations();

    /**
     * Reset the iteration count.
     * This is maintained by the test to allow concurrent tests to manage the count.
     */
    void resetIterations();

    /**
     * Allows any test specific setup before starting the test run.
     */
    void startTest();

    /**
     * Allows any test specific setup before starting the test run.
     */
    void endTest();

    /**
     * Return the total time (ms) to run each of the test runs.
     * This is used to compute the number of test iterations to use,
     * the tests are then still timed based on that number of iterations.
     */
    long getTestRunTime();

    /**
     * Set the total time (ms) to run each of the test runs.
     * This is used to compute the number of test iterations to use,
     * the tests are then still timed based on that number of iterations.
     */
    void setTestRunTime(long testRunTime);

    /**
     * Return the allowable percentage difference for test compared to the baseline.
     */
    double getAllowableDecrease();

    /**
     * Set the allowable percentage difference for test compared to the baseline.
     */
    void setAllowableDecrease(double allowableDecrease);

    /**
     * Return the performance tests to compare the base-line with.
     */
    List<TestCase> getTests();

    /**
     * Add a performance tests to compare the base-line with.
     */
    void addTest(TestCase test);

}
