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

import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Vector;

/**
 * <p><b>Purpose</b>:Results summary handles results associated with the execution of test suite and
 * test model.
 */
public class TestResultsSummary implements ResultInterface, Comparable<TestResultsSummary>, Serializable {
    protected BigDecimal id;
    protected ValueHolderInterface<TestResultsSummary> parent;
    protected String name;
    protected String description;
    protected ValueHolderInterface<Vector<TestResult>> results;
    protected ValueHolderInterface<LoadBuildSummary> loadBuildSummary;
    protected Throwable setupException;
    protected int passed;
    protected int warnings;
    protected int errors;
    protected int problems;
    protected int fatalErrors;
    protected int setupFailures;
    protected int setupWarnings;
    protected int totalTests;
    protected transient TestCollection testCollection;
    protected long totalTime;

    public TestResultsSummary() {
        this.name = "";
        this.description = "";
        this.parent = new ValueHolder<>();
        this.loadBuildSummary = new ValueHolder<>();
        this.results = new ValueHolder<>(new Vector<>());
        this.passed = 0;
        this.warnings = 0;
        this.errors = 0;
        this.fatalErrors = 0;
        this.setupFailures = 0;
        this.setupWarnings = 0;
        this.totalTests = 0;
    }

    public TestResultsSummary(TestCollection testCollection) {
        this.testCollection = testCollection;
        this.name = testCollection.getName();
        this.description = testCollection.getDescription();
        this.parent = new ValueHolder<>();
        this.loadBuildSummary = new ValueHolder<>();
        this.results = new ValueHolder<>(new Vector<>());
        this.passed = 0;
        this.warnings = 0;
        this.errors = 0;
        this.fatalErrors = 0;
        this.totalTests = 0;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    @Override
    public int compareTo(TestResultsSummary summary) {
        return getName().compareTo(summary.getName());
    }

    /**
     * A new test result is added to the summary which means incrementing the attributes in
     * the summary.
     */
    public void appendTestCaseResult(TestCase testCase) {
        String testOutcome = testCase.getTestResult().getOutcome();

        incrementTotalTests();
        switch (testOutcome) {
            case "Passed" -> incrementPassed();
            case "Warning" -> incrementWarnings();
            case "FatalError" -> incrementFatalErrors();
            case "Error" -> incrementErrors();
            case "Problem" -> incrementProblems();
            case "You decide" -> {
                ;
            }
            default -> throw new Error("Wrong test result");
        }
        getResults().add(testCase.getTestResult());
        testCase.getTestResult().setSummary(this);
        LoadBuildSystem.loadBuild.addSummary(this);
    }

    /**
     * Add two test summaries
     */
    public void appendTestCollectionResult(TestCollection testCollection) {
        TestResultsSummary testSummary = testCollection.getSummary();

        setErrors(getErrors() + testSummary.getErrors());
        setPassed(getPassed() + testSummary.getPassed());
        setWarnings(getWarnings() + testSummary.getWarnings());
        setProblems(getProblems() + testSummary.getProblems());
        setFatalErrors(getFatalErrors() + testSummary.getFatalErrors());
        setTotalTests(getTotalTests() + testSummary.getTotalTests());
        setSetupFailures(getSetupFailures() + testSummary.getSetupFailures());
        setSetupWarnings(getSetupWarnings() + testSummary.getSetupWarnings());
        if (testSummary.didSetupFail()) {
            setSetupFailures(getSetupFailures() + 1);
        } else if (testSummary.didSetupWarn()) {
            setSetupWarnings(getSetupWarnings() + 1);
        }
        testCollection.getSummary().setParent(this);
        LoadBuildSystem.loadBuild.addSummary(this);
    }

    /**
     * Add the Junit test results.
     */
    public void appendTestResult(junit.framework.TestResult result) {
        setErrors(getErrors() + result.failureCount());
        setPassed(getPassed() + result.runCount() - result.failureCount() - result.errorCount());
        setFatalErrors(getFatalErrors() + result.errorCount());
        setTotalTests(getTotalTests() + result.runCount());
        LoadBuildSystem.loadBuild.addSummary(this);
    }

    public boolean didSetupFail() {
        return getSetupException() != null && !didSetupWarn();
    }

    public boolean didSetupWarn() {
        return getSetupException() != null && (getSetupException() instanceof TestWarningException);
    }

    public String getDescription() {
        return description;
    }

    /**
     * Return the no. of errors in the test suite/model.
     */
    public int getErrors() {
        return errors;
    }

    /**
     * Return the no. of fatal errors in the test suite/model.
     */
    public int getFatalErrors() {
        return fatalErrors;
    }

    public void setSetupFailures(int setupFailures) {
        this.setupFailures = setupFailures;
    }

    public int getSetupFailures() {
        return setupFailures;
    }

    public void setSetupWarnings(int setupWarnings) {
        this.setupWarnings = setupWarnings;
    }

    public int getSetupWarnings() {
        return setupWarnings;
    }

    public BigDecimal getId() {
        return id;
    }

    public LoadBuildSummary getLoadBuildSummary() {
        return loadBuildSummary.getValue();
    }

    public ValueHolderInterface<LoadBuildSummary> getLoadBuildSummaryHolder() {
        return loadBuildSummary;
    }

    public String getName() {
        return name;
    }

    public TestResultsSummary getParent() {
        return parent.getValue();
    }

    public ValueHolderInterface<TestResultsSummary> getParentHolder() {
        return parent;
    }

    /**
     * Return the no. test which passed in the test suite/model.
     */
    public int getPassed() {
        return passed;
    }

    /**
     * Return the number of problems in the test suite/model.
     */
    public int getProblems() {
        return problems;
    }

    /**
     * Return the no. test which passed in the test suite/model.
     */
    public Vector<TestResult> getResults() {
        return results.getValue();
    }

    /**
     * Return the no. test which passed in the test suite/model.
     */
    public ValueHolderInterface<Vector<TestResult>> getResultsHolder() {
        return results;
    }

    public Throwable getSetupException() {
        return setupException;
    }

    public String getSetupExceptionStackTrace() {
        if (setupException != null) {
            StringWriter writer = new StringWriter();
            setupException.printStackTrace(new PrintWriter(writer));
            return writer.toString();
        }
        return null;
    }

    /**
     * INTERNAL:
     * This class knows about its associated test collection.
     */
    public TestCollection getTestCollection() {
        return testCollection;
    }

    /**
     * Return the no. tests in the test suite/model.
     */
    public int getTotalTests() {
        return totalTests;
    }

    /**
     * Return the no. warnings in the test suite/model.
     */
    public int getWarnings() {
        return warnings;
    }

    @Override
    public boolean shouldLogResult() {
        return !hasPassed();
    }

    /**
     * Passed means the test was ok.
     */
    @Override
    public boolean hasPassed() {
        // This is a safest way to check then doing totaltests == passedtests.
        return (getWarnings() == 0) && (getFatalErrors() == 0) && (getProblems() == 0) && (getErrors() == 0) && !didSetupFail() && (getSetupFailures() == 0) && !didSetupWarn() && (getSetupWarnings() == 0);
    }

    protected void incrementErrors() {
        setErrors(getErrors() + 1);
    }

    protected void incrementFatalErrors() {
        setFatalErrors(getFatalErrors() + 1);
    }

    protected void incrementPassed() {
        setPassed(getPassed() + 1);
    }

    protected void incrementProblems() {
        setProblems(getProblems() + 1);
    }

    protected void incrementTotalTests() {
        setTotalTests(getTotalTests() + 1);
    }

    protected void incrementWarnings() {
        setWarnings(getWarnings() + 1);
    }

    /**
     * Print itself on the print stream.
     */
    @Override
    public void logResult(Writer log) {
        // The indentationString adds some number of tabs to the print stream.
        String indentationString = getTestCollection().getIndentationString();

        try {
            if (didSetupFail()) {
                log.write(indentationString + System.lineSeparator() + "## SETUP FAILURE: " + System.lineSeparator() + getSetupException() + System.lineSeparator());
                PrintWriter printWriter = new PrintWriter(log);
                getSetupException().printStackTrace(printWriter);
                log.flush();
            } else if (didSetupWarn()) {
                log.write(indentationString + "SETUP WARNING: " + System.lineSeparator());
                ((TestWarningException)getSetupException()).setIndentationString(indentationString);
                log.write(getSetupException() + System.lineSeparator());
                log.flush();
            } else {
                if ((getErrors() > 0) || (getFatalErrors() > 0) || (getProblems() > 0)) {
                    log.write(indentationString + "###ERRORS###" + System.lineSeparator());
                }
                log.write(indentationString + "Warnings: " + getWarnings() + System.lineSeparator());
                log.write(indentationString + "Problems: " + getProblems() + System.lineSeparator());
                log.write(indentationString + "Errors: " + getErrors() + System.lineSeparator());
                log.write(indentationString + "Fatal Errors: " + getFatalErrors() + System.lineSeparator());
                log.write(indentationString + "Passed: " + getPassed() + System.lineSeparator());
                log.write(indentationString + "Setup Warnings: " + getSetupWarnings() + System.lineSeparator());
                log.write(indentationString + "Setup Failures: " + getSetupFailures() + System.lineSeparator());
                log.write(indentationString + "Total Time: " + getTotalTime() + System.lineSeparator());
                log.write(indentationString + "Total Tests: " + getTotalTests() + System.lineSeparator() + System.lineSeparator());
                log.flush();
            }
        } catch (IOException exception) {
        }
    }

    /**
     * Print itself on the print stream for the diff trace of the SRG.
     */
    public void logRegressionResult(Writer log) {
        // The indentationString adds some number of tabs to the print stream.
        // Eliminate the numbers of PASSED, WARNING and TOTAL TESTS in SRG and LRG log. ET
        String indentationString = getTestCollection().getIndentationString();

        try {
            if (didSetupFail()) {
                log.write(indentationString + System.lineSeparator() + "## SETUP FAILURE: " + System.lineSeparator() + getSetupException() + System.lineSeparator());
                PrintWriter printWriter = new PrintWriter(log);
                getSetupException().printStackTrace(printWriter);
                log.flush();
            } else {
                if ((getErrors() > 0) || (getFatalErrors() > 0) || (getProblems() > 0)) {
                    log.write(indentationString + "###ERRORS###" + System.lineSeparator());
                }

                //log.write(indentationString + "Warnings: " + getWarnings() + System.lineSeparator());
                log.write(indentationString + "Problems: " + getProblems() + System.lineSeparator());
                log.write(indentationString + "Errors: " + getErrors() + System.lineSeparator());
                log.write(indentationString + "Fatal Errors: " + getFatalErrors() + System.lineSeparator());
                //log.write(indentationString + "Passed: " + getPassed() + System.lineSeparator());
                log.write(indentationString + "Setup Failures: " + getSetupFailures() + System.lineSeparator() + System.lineSeparator());
                //log.write(
                //    indentationString + "Total Tests: " + getTotalTests() + System.lineSeparator()
                //    + System.lineSeparator());
                log.flush();
            }
        } catch (IOException exception) {
        }
    }

    /**
     * Reset the totals.
     */
    public void resetTotals() {
        setResults(new Vector<>());
        setPassed(0);
        setWarnings(0);
        setErrors(0);
        setFatalErrors(0);
        setSetupFailures(0);
        setSetupWarnings(0);
        setTotalTests(0);
    }

    public void setDescription(String aDescription) {
        description = aDescription;
    }

    public void setErrors(int anError) {
        errors = anError;
    }

    public void setFatalErrors(int theFatalError) {
        fatalErrors = theFatalError;
    }

    public void setId(BigDecimal anId) {
        id = anId;
    }

    public void setLoadBuildSummary(LoadBuildSummary summary) {
        loadBuildSummary.setValue(summary);
    }

    public void setLoadBuildSummaryHolder(ValueHolderInterface<LoadBuildSummary> holder) {
        loadBuildSummary = holder;
    }

    public void setName(String aName) {
        name = aName;
    }

    public void setParent(TestResultsSummary result) {
        parent.setValue(result);
    }

    public void setParentHolder(ValueHolderInterface<TestResultsSummary> holder) {
        parent = holder;
    }

    public void setPassed(int thePassed) {
        passed = thePassed;
    }

    public void setProblems(int theProblem) {
        problems = theProblem;
    }

    public void setResults(Vector<TestResult> theResults) {
        results.setValue(theResults);
    }

    public void setResultsHolder(ValueHolderInterface<Vector<TestResult>> holder) {
        results = holder;
    }

    public void setSetupException(Throwable setupException) {
        this.setupException = setupException;
    }

    public void setSetupExceptionStackTrace(String stackTrace) {
        if (stackTrace != null) {
            if (setupException != null) {
                setupException = new Exception(stackTrace);
            }
        }
    }

    /**
     * This class knows about its associated test collection.
     */
    public void setTestCollection(TestCollection testCollection) {
        this.testCollection = testCollection;
    }

    public void settName(String aName) {
        name = aName;
    }

    public void setTotalTests(int tests) {
        totalTests = tests;
    }

    public void setWarnings(int theWarnings) {
        warnings = theWarnings;
    }
}
