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

import org.eclipse.persistence.sessions.*;

/**
 * Used to compare the performance of two different task/processes.
 * Defines a test and testBaseLine method and compares the performance of the two.
 * Use the static REPEATS for number of repeats, and computes the averge difference/etc.
 * Note that setup should do any setup and the test should only do what is being compared.
 */
public abstract class PerformanceComparisonTestCase extends TestCase implements PerformanceComparisonTest {

    /**
     * The allowable percentage difference for test compared to the baseline.
     */
    public double allowableDecrease;

    /**
     * The total time (ms) to run each of the test runs.
     * This is used to compute the number of test iterations to use,
     * the tests are then still timed based on that number of iterations.
     */
    public long testRunTime;

    /**
     * List of performance tests, the source test is the baseline,
     * n other tests can be added to compare with.
     */
    protected List tests;

    /**
     * The count of the iterations.
     * This is maintained by the test to allow concurrent tests to manage the count.
     */
    protected volatile int iterations;

    public PerformanceComparisonTestCase() {
        this.testRunTime = DEFAULT_TEST_TIME;
        this.allowableDecrease = DEFAULT_ALLOWABLE_DECREASE;
        this.tests = new ArrayList();
    }

    /**
     * Return the count of the iterations.
     * This is maintained by the test to allow concurrent tests to manage the count.
     */
    public int getIterations() {
        return iterations;
    }

    /**
     * Increment the iteration count.
     * This is maintained by the test to allow concurrent tests to manage the count.
     */
    public void incrementIterations() {
        this.iterations++;
    }

    /**
     * Reset the iteration count.
     * This is maintained by the test to allow concurrent tests to manage the count.
     */
    public void resetIterations() {
        this.iterations = 0;
    }

    /**
     * Allows any test specific setup before starting the test run.
     */
    public void startTest() {
    }

    /**
     * Allows any test specific setup before starting the test run.
     */
    public void endTest() {
    }

    /**
     * Return the total time (ms) to run each of the test runs.
     * This is used to compute the number of test iterations to use,
     * the tests are then still timed based on that number of iterations.
     */
    public long getTestRunTime() {
        return testRunTime;
    }

    /**
     * Set the total time (ms) to run each of the test runs.
     * This is used to compute the number of test iterations to use,
     * the tests are then still timed based on that number of iterations.
     */
    public void setTestRunTime(long testRunTime) {
        this.testRunTime = testRunTime;
    }

    /**
     * Return the allowable percentage difference for test compared to the baseline.
     */
    public double getAllowableDecrease() {
        return allowableDecrease;
    }

    /**
     * Set the allowable percentage difference for test compared to the baseline.
     */
    public void setAllowableDecrease(double allowableDecrease) {
        this.allowableDecrease = allowableDecrease;
    }

    /**
     * Return the performance tests to compare the base-line with.
     */
    public List getTests() {
        return tests;
    }

    /**
     * Add a performance tests to compare the base-line with.
     */
    public void addTest(TestCase test) {
        getTests().add(test);
    }


    /**
     * Executes this test case.
     * Log some debug info as perf tests sometimes take a long time or hang.
     */
    public void execute(TestExecutor executor) {
        System.out.println("Begin:" + getName());
        try {
            super.execute(executor);
        } finally {
            System.out.println("End:" + getName() + ":" + getTestResult().getTotalTime());
        }
    }
    
    /**
     * Executes this test comparison with the base-line.
     * Static allow reuse with EJB tests.
     */
    public void executeTest() throws Throwable {
        executeTest(this);
    }

    /**
     * Executes this test comparison with the base-line.
     * Static allow reuse with EJB tests.
     */
    public static void executeTest(PerformanceComparisonTest performanceTest) throws Throwable {
        PerformanceComparisonTestResult result = new PerformanceComparisonTestResult((TestCase)performanceTest, "Passed");
        ((TestCase)performanceTest).setTestResult(result);
        try {
            // Repeat the test and baseline for the number of repeats.
            for (int index = 0; index < REPEATS; index++) {
                long startTime, endTime;
                try {
                    System.gc();
                    Thread.sleep(1000);
                    performanceTest.startTest();
                    performanceTest.resetIterations();
                    startTime = System.currentTimeMillis();
                    endTime = startTime;
                    // Count how many times the test can be invoked in the run time.
                    // This allows for the test run time to be easily changed.
                    while ((startTime + performanceTest.getTestRunTime()) >= endTime) {
                        ((TestCase)performanceTest).test();
                        performanceTest.incrementIterations();
                        endTime = System.currentTimeMillis();
                    }
                } finally {
                    performanceTest.endTest();
                }
                result.addTestCount(performanceTest.getIterations(), 0);

                for (int testIndex = 0; testIndex < performanceTest.getTests().size(); testIndex++) {
                    PerformanceComparisonTest test = (PerformanceComparisonTest)performanceTest.getTests().get(testIndex);
                    ((TestCase)test).setExecutor(((TestCase)performanceTest).getExecutor());
                    try {
                        test.startTest();
                        performanceTest.resetIterations();
                        System.gc();
                        Thread.sleep(1000);
                        startTime = System.currentTimeMillis();
                        endTime = startTime;
                        // Count how many times the test can be invoked in the run time.
                        // This allows for the test run time to be easily changed.
                        while ((startTime + performanceTest.getTestRunTime()) >= endTime) {
                            ((TestCase)test).test();
                            performanceTest.incrementIterations();
                            endTime = System.currentTimeMillis();
                        }
                    } finally {
                        test.endTest();
                    }
                    result.addTestCount(performanceTest.getIterations(), testIndex + 1);
                }
            }
        } finally {
            result.computeResults();
        }
    }

    /**
     * Verify each test comparison is within the set limits.
     * Static allow reuse with EJB tests.
     */
    public void verify() {
        verify(this);
    }

    /**
     * Verify each test comparison is within the set limits.
     * Static allow reuse with EJB tests.
     */
    public static void verify(PerformanceComparisonTest performanceTest) {
        PerformanceComparisonTestResult result = (PerformanceComparisonTestResult)((TestCase)performanceTest).getTestResult();
        for (int index = 0; index < result.percentageDifferences.size(); index++) {
            double difference = ((Number)result.percentageDifferences.get(index)).doubleValue();
            PerformanceComparisonTest test = (PerformanceComparisonTest)performanceTest.getTests().get(index);
            double allowable = test.getAllowableDecrease();
            if (difference < allowable) {
                throw new TestErrorException("Decrease lower than allowable detected, decrease: " + difference + 
                                             " allowed: " + allowable + " test: " + test.getName());
            }
        }
    }
    
    /**
     * Build and return an emulated session for isolated Java performance from the database.
     */
    public Session buildEmulatedSession() {
        try {
            Class.forName(getSession().getLogin().getDriverClassName());
        } catch (Exception ignore) {}
        Project project = getSession().getProject().clone();
        DatabaseLogin login = (DatabaseLogin)project.getLogin().clone();
        try {
            Class.forName(login.getDriverClassName());
        } catch (Exception ignore) {}
        //login.useDirectDriverConnect();
        login.setDriverClass(org.eclipse.persistence.testing.tests.performance.emulateddb.EmulatedDriver.class);
        login.setConnectionString("emulate:" + login.getConnectionString());
        project.setLogin(login);
        DatabaseSession session = project.createDatabaseSession();
        session.setSessionLog(getSession().getSessionLog());
        session.login();
        
        return session;
    }
}
