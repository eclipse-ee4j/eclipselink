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

import java.io.*;

import java.util.*;

import org.eclipse.persistence.internal.helper.Helper;

/**
 * Stores times for runs of tests vs base-line and computes averges/stats on these.
 * The baseline is the first value in the lists, followed by each test's value.
 */
public class PerformanceComparisonTestResult extends TestResult {
    public List testCounts;
    public List testAverages;
    public List testMaxs;
    public List testMins;
    public List testStandardDeviations;

    /**
     * Percentage differences between the performance comparisons.
     */
    public List percentageDifferences;

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
    public List currentVersionResults;

    /**
     * The up to 3 results for the baseline version compared to.
     */
    public List baselineVersionResults;

    /**
     * The regression baseline % standard deviation.
     */
    public double baselineStandardDeviation;

    /**
     * The regression current % standard deviation.
     */
    public double currentStandardDeviation;

    public PerformanceComparisonTestResult() {
        this.testCounts = new ArrayList();
        this.testAverages = new ArrayList();
        this.testMaxs = new ArrayList();
        this.testMins = new ArrayList();
        this.testStandardDeviations = new ArrayList();
        this.percentageDifferences = new ArrayList();
        this.currentVersionResults = new ArrayList();
        this.baselineVersionResults = new ArrayList();
    }

    public PerformanceComparisonTestResult(TestCase testCase) {
        super(testCase);
        this.testCounts = new ArrayList();
        this.testAverages = new ArrayList();
        this.testMaxs = new ArrayList();
        this.testMins = new ArrayList();
        this.testStandardDeviations = new ArrayList();
        this.percentageDifferences = new ArrayList();
        this.currentVersionResults = new ArrayList();
        this.baselineVersionResults = new ArrayList();
    }

    public PerformanceComparisonTestResult(TestCase testCase, String result) {
        super(testCase, result);
        this.testCounts = new ArrayList();
        this.testAverages = new ArrayList();
        this.testMaxs = new ArrayList();
        this.testMins = new ArrayList();
        this.testStandardDeviations = new ArrayList();
        this.percentageDifferences = new ArrayList();
        this.currentVersionResults = new ArrayList();
        this.baselineVersionResults = new ArrayList();
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
     * Return the & difference between the current test average time and the
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
            List times = (List)getTestCounts().get(testIndex);
            double testAverage = PerformanceComparisonTestResult.averageResults(times);
            double testStandardDeviation = PerformanceComparisonTestResult.standardDeviationResults(times);

            // Set the test average count as the test time.
            this.setAverageTestCount((long)testAverage);

            this.testAverages.add(new Double(testAverage));
            this.testMins.add(PerformanceComparisonTestResult.minResults(times));
            this.testMaxs.add(PerformanceComparisonTestResult.maxResults(times));
            this.testStandardDeviations.add(new Double(testStandardDeviation));

            if (testIndex > 0) {
                double testBaseLineAverage = ((Number)this.testAverages.get(0)).doubleValue();

                // Difference
                double percentageDifference = 
                    PerformanceComparisonTestResult.percentageDifference(testAverage, testBaseLineAverage);
                this.percentageDifferences.add(new Double(percentageDifference));
            }
        }
    }

    /**
     * Return the list of lists of test counts.
     */
    public List getTestCounts() {
        return testCounts;
    }

    /**
     * Return the regression results from the current version run.
     */
    public List getCurrentVersionResults() {
        return currentVersionResults;
    }

    /**
     * Return the regression results from the baseline runs.
     */
    public List getBaselineVersionResults() {
        return baselineVersionResults;
    }

    /**
     * Add the test count for the test index.
     */
    public void addTestCount(long time, int test) {
        if (getTestCounts().size() <= test) {
            getTestCounts().add(new ArrayList());
        }
        ((List)getTestCounts().get(test)).add(new Long(time));
    }

    /**
     * Always log the result for analysis.
     */
    public boolean shouldLogResult() {
        return true;
    }

    /**
     * logs the result of the test on the print stream.
     */
    public void logResult(Writer log) {
        String indentationString = getTestCase().getIndentationString();
        try {
            if (hasError() || hasFatalError() || hasProblem()) {
                log.write(indentationString + "##FAILURE##" + Helper.cr());
            }
            if (!getTestCounts().isEmpty()) {
                log.write(indentationString + "RUNS:					" + ((List)getTestCounts().get(0)).size() + Helper.cr());
            }

            for (int index = 0; index < getTestCounts().size(); index++) {
                PerformanceComparisonTest test = (PerformanceComparisonTest)this.testCase;
                log.write(Helper.cr());
                if (index == 0) {
                    log.write(indentationString + "BASE_LINE TEST" + Helper.cr());
                } else {
                    test = (PerformanceComparisonTest)test.getTests().get(index - 1);
                }
                log.write(indentationString + "TEST: " + test.getName() + Helper.cr());
                log.write(indentationString + "TEST RUN TIME:				" + (test.getTestRunTime() / 1000) + " seconds" + 
                          Helper.cr());
                log.write(indentationString + "MEAN TEST COUNT:			" + this.testAverages.get(index) + Helper.cr());
                log.write(indentationString + "MAX TEST COUNT:				" + this.testMaxs.get(index) + Helper.cr());
                log.write(indentationString + "MIN TEST COUNT:				" + this.testMins.get(index) + Helper.cr());
                log.write(indentationString + "TEST % STANDARD DEVIATION:		" + this.testStandardDeviations.get(index) + 
                          Helper.cr());

                if (index > 0) {
                    log.write(indentationString + "% DIFFERENCE:				" + this.percentageDifferences.get(index - 1) + 
                              Helper.cr());
                    log.write(indentationString + "% DIFFERENCE ALLOWABLE:			" + test.getAllowableDecrease() + Helper.cr());
                }
            }

            if (getTestCase() instanceof PerformanceRegressionTest) {
                log.write(indentationString + "BASELINE VERSION:			" + getBaselineVersion() + Helper.cr());
                log.write(indentationString + "BASELINE VERSION RESULTS:		" + getBaselineVersionResults() + Helper.cr());
                log.write(indentationString + "BASELINE VERSION % STANDARD DEVIATION:	" + this.baselineStandardDeviation + 
                          Helper.cr());
                log.write(indentationString + "CURRENT VERSION RESULTS:		" + this.getCurrentVersionResults() + Helper.cr());
                log.write(indentationString + "CURRENT VERSION % STANDARD DEVIATION:	" + this.currentStandardDeviation + 
                          Helper.cr());
                log.write(indentationString + "% DIFFERENCE (last run):		" + getPercentageDifferenceLastRun() + Helper.cr());
                log.write(indentationString + "% DIFFERENCE (average):			" + getPercentageDifferenceAverage() + Helper.cr());
            }

            log.write(Helper.cr() + indentationString + "RESULT:      				" + getOutcome() + Helper.cr());
        } catch (IOException exception) {
            // Ignore.
        } catch (ArrayIndexOutOfBoundsException exception) {
            // Ignore.
        }
        try {
            if (getException() != null) {
                getException().setIndentationString(indentationString);
                log.write(getException() + org.eclipse.persistence.internal.helper.Helper.cr());
            }
            log.flush();
        } catch (IOException exception) {
        }
        LoadBuildSystem.loadBuild.addResult(this);
    }

    /**
     * Compute the max of the results.
     */
    public static Number maxResults(List times) {
        Number testMax = new Double(0);
        for (int index = 0; index < times.size(); index++) {
            Number time = (Number)times.get(index);
            if (time.doubleValue() > testMax.doubleValue()) {
                testMax = time;
            }
        }
        return testMax;
    }

    /**
     * Compute the min of the results.
     */
    public static Number minResults(List times) {
        Number testMin = new Double(0);
        for (int index = 0; index < times.size(); index++) {
            Number time = (Number)times.get(index);
            if ((testMin.doubleValue() == 0) || (time.doubleValue() < testMin.doubleValue())) {
                testMin = time;
            }
        }
        return testMin;
    }

    /**
     * Filter max and min from results.
     */
    public static List filterMaxMinResults(List times) {
        List filteredTimes = new ArrayList(times);
        if (filteredTimes.size() > 3) {
            filteredTimes.remove(maxResults(times));
            filteredTimes.remove(minResults(times));
        }
        return filteredTimes;
    }

    /**
     * Compute the average of the results rejecting the min and max.
     */
    public static double averageResults(List allTimes) {
        // Compute the average reject the min and max to improve consistency.
        List times = filterMaxMinResults(allTimes);
        double testAverage = 0;
        for (int index = 0; index < times.size(); index++) {
            double time = ((Number)times.get(index)).doubleValue();
            testAverage = testAverage + time;
        }
        testAverage = testAverage / times.size();
        return testAverage;
    }

    /**
     * Compute the standard deviation of the results rejecting the min and max.
     */
    public static double standardDeviationResults(List allTimes) {
        // Compute the average reject the min and max to improve consistency.
        double testAverage = averageResults(allTimes);

        // Compute the standard deviation reject the min and max to improve consistency.
        List times = filterMaxMinResults(allTimes);
        double testStandardDeviation = 0;
        for (int index = 0; index < times.size(); index++) {
            double time = ((Number)times.get(index)).doubleValue();
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
