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
package org.eclipse.persistence.testing.framework;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;

/**
 * Used to ensure the performance of an operation or fine grained use case does not
 * regress / become slower than the previous successful run of the test.
 * This accesses the test result database to query the previous test result.
 */
public abstract class PerformanceRegressionTestCase extends PerformanceComparisonTestCase implements PerformanceRegressionTest {

    /** Switch reset to true to reset the baseline comparison. */
    public static boolean reset = false;

    public PerformanceRegressionTestCase() {
        this.allowableDecrease = DEFAULT_ALLOWABLE_DECREASE;
    }

    /**
     * Load the last test result from the test result database.
     * Find only the results run on the same machine and database.
     * Compare the current test run result with the previous results
     * do determine if the test passes or fails.
     */
    public void verify() {
        super.verify();
        verify(this);
    }

    /**
     * Load the last test result from the test result database.
     * Find only the results run on the same machine and database.
     * Compare the current test run result with the previous results
     * do determine if the test passes or fails.
     */
    public static void verify(PerformanceRegressionTest test) {
        // Ensures all tests pass to reset baseline,
        // Required when tests or environment change to be slower to avoid failures.
        if (reset) {
            throw new TestWarningException("Reseting baseline.");
        }
        Session session = LoadBuildSystem.getSystem().getSession();

        // Query all previous successful test results for this test on the same machine and database.
        // Find only the baseline version, or last version run different than current version.
        // If you need to compare results against the current version you must change the TopLink version string.
        ReadAllQuery query = new ReadAllQuery(TestResult.class);
        ExpressionBuilder result = new ExpressionBuilder();
        query.setSelectionCriteria(result.get("name").equal(test.getName()).and(result.get("loadBuildSummary").get("machine").equal(LoadBuildSystem.getSummary().getMachine())).and(result.get("loadBuildSummary").get("loginChoice").equal(LoadBuildSystem.getSummary().getLoginChoice())));
        // Allow comparing to a set version through a system property.
        String currentVersion = LoadBuildSystem.getSummary().getToplinkVersion();
        String baselineVersion = null;
        if (System.getProperties().containsKey("toplink.loadbuild.baseline-version")) {
            baselineVersion = System.getProperties().getProperty("toplink.loadbuild.baseline-version");
            // System properties cannot store spaces so need to replace them from \b.
            baselineVersion = baselineVersion.replace('_', ' ');
            ((PerformanceComparisonTestResult)((TestCase)test).getTestResult()).baselineVersion = baselineVersion;
        } else {
            // Compare against the last successful version.
            ReportQuery reportQuery = new ReportQuery(TestResult.class, query.getExpressionBuilder());
            reportQuery.useDistinct();
            reportQuery.returnSingleValue();
            reportQuery.addAttribute("version", result.get("loadBuildSummary").get("toplinkVersion"));
            reportQuery.setSelectionCriteria(query.getSelectionCriteria().and((result.get("outcome").equal(TestResult.PASSED)).or(result.get("outcome").equal(TestResult.WARNING))).and(result.get("loadBuildSummary").get("toplinkVersion").notEqual(currentVersion)));
            reportQuery.addOrdering(result.get("loadBuildSummary").get("timestamp").descending());
            baselineVersion = (String) session.executeQuery(reportQuery);
        }
        query.setSelectionCriteria(query.getSelectionCriteria().and(result.get("loadBuildSummary").get("toplinkVersion").equal(baselineVersion)));
        query.addOrdering(result.get("loadBuildSummary").get("timestamp").descending());
        query.setMaxRows(10);
        query.useCursoredStream(1, 1);
        CursoredStream stream = (CursoredStream)session.executeQuery(query);
        if (!stream.hasMoreElements()) {
            throw new TestWarningException("No previous test result to compare performance with.");
        }
        TestResult lastResult = (TestResult)stream.nextElement();
        double lastCount = lastResult.getTestTime();
        PerformanceComparisonTestResult testResult = (PerformanceComparisonTestResult)((TestCase)test).getTestResult();
        testResult.getBaselineVersionResults().add(new Double(lastCount));
        // Average last 5 runs.
        int numberOfRuns = 0;
        while (stream.hasMoreElements() && (numberOfRuns < 4)) {
            TestResult nextResult = (TestResult)stream.nextElement();
            testResult.getBaselineVersionResults().add(new Double(nextResult.getTestTime()));
            numberOfRuns++;
        }
        stream.close();
        double baselineAverage = PerformanceComparisonTestResult.averageResults(testResult.getBaselineVersionResults());
        double currentCount = ((TestCase)test).getTestResult().getTestTime();
        testResult.baselineVersion = lastResult.getLoadBuildSummary().getToplinkVersion();
        testResult.percentageDifferenceLastRun = 
                PerformanceComparisonTestResult.percentageDifference(currentCount, lastCount);

        // Query the current version last 5 runs for averaging.
        query = new ReadAllQuery(TestResult.class);
        result = new ExpressionBuilder();
        query.setSelectionCriteria(result.get("name").equal(((TestCase)test).getName()).and(result.get("loadBuildSummary").get("machine").equal(LoadBuildSystem.getSummary().getMachine())).and(result.get("loadBuildSummary").get("loginChoice").equal(LoadBuildSystem.getSummary().getLoginChoice())).and(result.get("loadBuildSummary").get("toplinkVersion").equal(currentVersion)));
        query.addOrdering(result.get("loadBuildSummary").get("timestamp").descending());
        query.useCursoredStream(1, 1);
        stream = (CursoredStream)session.executeQuery(query);
        // Average last 5 runs.
        testResult.getCurrentVersionResults().add(new Double(currentCount));
        numberOfRuns = 0;
        while (stream.hasMoreElements() && (numberOfRuns < 4)) {
            TestResult nextResult = (TestResult)stream.nextElement();
            testResult.getCurrentVersionResults().add(new Double(nextResult.getTestTime()));
            numberOfRuns++;
        }
        stream.close();
        double currentAverage = PerformanceComparisonTestResult.averageResults(testResult.getCurrentVersionResults());
        testResult.percentageDifferenceAverage = 
                PerformanceComparisonTestResult.percentageDifference(currentAverage, baselineAverage);

        testResult.baselineStandardDeviation = 
                PerformanceComparisonTestResult.standardDeviationResults(testResult.getBaselineVersionResults());
        testResult.currentStandardDeviation = 
                PerformanceComparisonTestResult.standardDeviationResults(testResult.getCurrentVersionResults());

        if (testResult.percentageDifferenceAverage < test.getAllowableDecrease()) {
            throw new TestErrorException("Test is " + ((long)testResult.percentageDifferenceAverage) + 
                                         "% slower than last successful execution.");
        }
    }
}
