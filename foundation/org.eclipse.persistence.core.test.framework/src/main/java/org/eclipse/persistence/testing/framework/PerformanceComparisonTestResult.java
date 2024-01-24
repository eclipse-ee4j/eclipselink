/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores times for runs of tests vs base-line and computes averges/stats on these.
 * The baseline is the first value in the lists, followed by each test's value.
 */
public class PerformanceComparisonTestResult extends TestResult {
    public List<List<Number>> testCounts;
    public List<Double> testAverages;
    public List<Number> testMaxs;
    public List<Number> testMins;
    public List<Double> testStandardDeviations;

    /**
     * Percentage differences between the performance comparisons.
     */
    public List<Double> percentageDifferences;

    /** Records the average test count. */
    public double averageTestCount;

    /**
     * The % difference between the current test average time and the
     * last successful run of the test on the baseline version.
     * Used for regression tests.
     */
    protected double percentageDifferenceLastRun;

    /**
     * The % difference between the current test average time and the
     * average of the last 3 successful runs of the test on the baseline version.
     * Used for regression tests.
     */
    protected double percentageDifferenceAverage;

    /**
     * The TopLink version that this test is comparing against.
     * This version defaults to the last test run, but can be set through the
     * System property toplink.loadbuild.baseline-version to another version such
     * as the last release.
     * Used for regression tests.
     */
    protected String baselineVersion;

    /**
     * The up to 3 results for the current version.
     */
    public List<Number> currentVersionResults;

    /**
     * The up to 3 results for the baseline version compared to.
     */
    public List<Number> baselineVersionResults;

    /**
     * The regression baseline % standard deviation.
     */
    public double baselineStandardDeviation;

    /**
     * The regression current % standard deviation.
     */
    public double currentStandardDeviation;

    public PerformanceComparisonTestResult() {
        this.testCounts = new ArrayList<>();
        this.testAverages = new ArrayList<>();
        this.testMaxs = new ArrayList<>();
        this.testMins = new ArrayList<>();
        this.testStandardDeviations = new ArrayList<>();
        this.percentageDifferences = new ArrayList<>();
        this.currentVersionResults = new ArrayList<>();
        this.baselineVersionResults = new ArrayList<>();
    }

    public PerformanceComparisonTestResult(TestCase testCase) {
        super(testCase);
        this.testCounts = new ArrayList<>();
        this.testAverages = new ArrayList<>();
        this.testMaxs = new ArrayList<>();
        this.testMins = new ArrayList<>();
        this.testStandardDeviations = new ArrayList<>();
        this.percentageDifferences = new ArrayList<>();
        this.currentVersionResults = new ArrayList<>();
        this.baselineVersionResults = new ArrayList<>();
    }

    public PerformanceComparisonTestResult(TestCase testCase, String result) {
        super(testCase, result);
        this.testCounts = new ArrayList<>();
        this.testAverages = new ArrayList<>();
        this.testMaxs = new ArrayList<>();
        this.testMins = new ArrayList<>();
        this.testStandardDeviations = new ArrayList<>();
        this.percentageDifferences = new ArrayList<>();
        this.currentVersionResults = new ArrayList<>();
        this.baselineVersionResults = new ArrayList<>();
    }

    /**
     * Return the average test count.
     */
    public double getAverageTestCount() {
        return averageTestCount;
    }

    /**
     * Set the average test count.
     */
    public void setAverageTestCount(double averageTestCount) {
        this.averageTestCount = averageTestCount;
        // Also set this as the test time as this is what is stored in the db.
        this.testTime = (long)averageTestCount;
    }

    /**
     * Return the % difference between the current test average time and the
     * last successful run of the test on the baseline version.
     */
    public double getPercentageDifferenceLastRun() {
        return percentageDifferenceLastRun;
    }

    /**
     * Return the &amp; difference between the current test average time and the
     * average of the last 3 successful runs of the test on the baseline version.
     */
    public double getPercentageDifferenceAverage() {
        return percentageDifferenceAverage;
    }

    /**
     * Return the TopLink version that this test is comparing against.
     * This version defaults to the last test run, but can be set through the
     * System property toplink.loadbuild.baseline-version to another version such
     * as the last release.
     */
    public String getBaselineVersion() {
        return baselineVersion;
    }

    /**
     * Compute the test result averages and stand dev.
     */
    public void computeResults() {
        for (int testIndex = 0; testIndex < getTestCounts().size(); testIndex++) {
            List<Number> times = getTestCounts().get(testIndex);
            double testAverage = PerformanceComparisonTestResult.averageResults(times);
            double testStandardDeviation = PerformanceComparisonTestResult.standardDeviationResults(times);

            // Set the test average count as the test time.
            this.setAverageTestCount((long)testAverage);

            this.testAverages.add(testAverage);
            this.testMins.add(PerformanceComparisonTestResult.minResults(times));
            this.testMaxs.add(PerformanceComparisonTestResult.maxResults(times));
            this.testStandardDeviations.add(testStandardDeviation);

            if (testIndex > 0) {
                double testBaseLineAverage = ((Number)this.testAverages.get(0)).doubleValue();

                // Difference
                double percentageDifference =
                    PerformanceComparisonTestResult.percentageDifference(testAverage, testBaseLineAverage);
                this.percentageDifferences.add(percentageDifference);
            }
        }
    }

    /**
     * Return the list of lists of test counts.
     */
    public List<List<Number>> getTestCounts() {
        return testCounts;
    }

    /**
     * Return the regression results from the current version run.
     */
    public List<Number> getCurrentVersionResults() {
        return currentVersionResults;
    }

    /**
     * Return the regression results from the baseline runs.
     */
    public List<Number> getBaselineVersionResults() {
        return baselineVersionResults;
    }

    /**
     * Add the test count for the test index.
     */
    public void addTestCount(long time, int test) {
        if (getTestCounts().size() <= test) {
            getTestCounts().add(new ArrayList<>());
        }
        getTestCounts().get(test).add(time);
    }

    /**
     * Always log the result for analysis.
     */
    @Override
    public boolean shouldLogResult() {
        return true;
    }

    /**
     * logs the result of the test on the print stream.
     */
    @Override
    public void logResult(Writer log) {
        String indentationString = getTestCase().getIndentationString();
        try {
            if (hasError() || hasFatalError() || hasProblem()) {
                log.write(indentationString + "##FAILURE##" + System.lineSeparator());
            }
            if (!getTestCounts().isEmpty()) {
                log.write(indentationString + "RUNS:                    " + getTestCounts().get(0).size() + System.lineSeparator());
            }

            for (int index = 0; index < getTestCounts().size(); index++) {
                PerformanceComparisonTest test = (PerformanceComparisonTest)this.testCase;
                log.write(System.lineSeparator());
                if (index == 0) {
                    log.write(indentationString + "BASE_LINE TEST" + System.lineSeparator());
                } else {
                    test = (PerformanceComparisonTest)test.getTests().get(index - 1);
                }
                log.write(indentationString + "TEST: " + test.getName() + System.lineSeparator());
                log.write(indentationString + "TEST RUN TIME:                " + (test.getTestRunTime() / 1000) + " seconds" +
                          System.lineSeparator());
                log.write(indentationString + "MEAN TEST COUNT:            " + this.testAverages.get(index) + System.lineSeparator());
                log.write(indentationString + "MAX TEST COUNT:                " + this.testMaxs.get(index) + System.lineSeparator());
                log.write(indentationString + "MIN TEST COUNT:                " + this.testMins.get(index) + System.lineSeparator());
                log.write(indentationString + "TEST % STANDARD DEVIATION:        " + this.testStandardDeviations.get(index) +
                          System.lineSeparator());

                if (index > 0) {
                    log.write(indentationString + "% DIFFERENCE:                " + this.percentageDifferences.get(index - 1) +
                              System.lineSeparator());
                    log.write(indentationString + "% DIFFERENCE ALLOWABLE:            " + test.getAllowableDecrease() + System.lineSeparator());
                }
            }

            if (getTestCase() instanceof PerformanceRegressionTest) {
                log.write(indentationString + "BASELINE VERSION:            " + getBaselineVersion() + System.lineSeparator());
                log.write(indentationString + "BASELINE VERSION RESULTS:        " + getBaselineVersionResults() + System.lineSeparator());
                log.write(indentationString + "BASELINE VERSION % STANDARD DEVIATION:    " + this.baselineStandardDeviation +
                          System.lineSeparator());
                log.write(indentationString + "CURRENT VERSION RESULTS:        " + this.getCurrentVersionResults() + System.lineSeparator());
                log.write(indentationString + "CURRENT VERSION % STANDARD DEVIATION:    " + this.currentStandardDeviation +
                          System.lineSeparator());
                log.write(indentationString + "% DIFFERENCE (last run):        " + getPercentageDifferenceLastRun() + System.lineSeparator());
                log.write(indentationString + "% DIFFERENCE (average):            " + getPercentageDifferenceAverage() + System.lineSeparator());
            }

            log.write(System.lineSeparator() + indentationString + "RESULT:                      " + getOutcome() + System.lineSeparator());
        } catch (IOException | ArrayIndexOutOfBoundsException exception) {
            // Ignore.
        }
        try {
            if (getException() != null) {
                getException().setIndentationString(indentationString);
                log.write(getException() + System.lineSeparator());
            }
            log.flush();
        } catch (IOException exception) {
        }
        LoadBuildSystem.loadBuild.addResult(this);
    }

    /**
     * Compute the max of the results.
     */
    public static Number maxResults(List<Number> times) {
        Number testMax = (double) 0;
        for (Number time : times) {
            if (time.doubleValue() > testMax.doubleValue()) {
                testMax = time;
            }
        }
        return testMax;
    }

    /**
     * Compute the min of the results.
     */
    public static Number minResults(List<Number> times) {
        Number testMin = (double) 0;
        for (Number time : times) {
            if ((testMin.doubleValue() == 0) || (time.doubleValue() < testMin.doubleValue())) {
                testMin = time;
            }
        }
        return testMin;
    }

    /**
     * Filter max and min from results.
     */
    public static List<Number> filterMaxMinResults(List<Number> times) {
        List<Number> filteredTimes = new ArrayList<>(times);
        if (filteredTimes.size() > 3) {
            filteredTimes.remove(maxResults(times));
            filteredTimes.remove(minResults(times));
        }
        return filteredTimes;
    }

    /**
     * Compute the average of the results rejecting the min and max.
     */
    public static double averageResults(List<Number> allTimes) {
        // Compute the average reject the min and max to improve consistency.
        List<Number> times = filterMaxMinResults(allTimes);
        double testAverage = 0;
        for (Number number : times) {
            double time = number.doubleValue();
            testAverage = testAverage + time;
        }
        testAverage = testAverage / times.size();
        return testAverage;
    }

    /**
     * Compute the standard deviation of the results rejecting the min and max.
     */
    public static double standardDeviationResults(List<Number> allTimes) {
        // Compute the average reject the min and max to improve consistency.
        double testAverage = averageResults(allTimes);

        // Compute the standard deviation reject the min and max to improve consistency.
        List<Number> times = filterMaxMinResults(allTimes);
        double testStandardDeviation = 0;
        for (Number number : times) {
            double time = number.doubleValue();
            testStandardDeviation = testStandardDeviation + Math.pow(time - testAverage, 2);
        }
        testStandardDeviation = testStandardDeviation / times.size();
        testStandardDeviation = Math.sqrt(testStandardDeviation);
        // As percent of average
        testStandardDeviation = (testStandardDeviation / testAverage) * 100;
        return testStandardDeviation;
    }

    /**
     * Compute the percentage difference between the values.
     */
    public static double percentageDifference(double result, double baseline) {
        // Difference
        double percentageDifference = ((result - baseline) / baseline) * 100;
        // If negative calculate from the inverse spective to get a more meaningful result.
        if (percentageDifference < 0) {
            percentageDifference = -1 * ((baseline - result) / result) * 100;
        }
        return percentageDifference;
    }
}
