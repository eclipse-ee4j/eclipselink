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
import java.math.*;
import java.util.*;
import org.eclipse.persistence.indirection.*;

/**
 * <p><b>Purpose</b>:Results summary handles results associated with the execution of test suite and
 * test model.
 */
public class TestResultsSummary implements ResultInterface, Comparable, Serializable {
    protected BigDecimal id;
    protected ValueHolderInterface parent;
    protected String name;
    protected String description;
    protected ValueHolderInterface results;
    protected ValueHolderInterface loadBuildSummary;
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
        this.parent = new ValueHolder();
        this.loadBuildSummary = new ValueHolder();
        this.results = new ValueHolder(new Vector());
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
        this.parent = new ValueHolder();
        this.loadBuildSummary = new ValueHolder();
        this.results = new ValueHolder(new Vector());
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

    public int compareTo(Object summary) {
        return getName().compareTo(((TestResultsSummary)summary).getName());
    }

    /**
     * A new test result is added to the summary which means incrementing the attributes in
     * the summary.
     */
    public void appendTestCaseResult(TestCase testCase) {
        String testOutcome = testCase.getTestResult().getOutcome();

        incrementTotalTests();
        if (testOutcome.equals("Passed")) {
            incrementPassed();
        } else if (testOutcome.equals("Warning")) {
            incrementWarnings();
        } else if (testOutcome.equals("FatalError")) {
            incrementFatalErrors();
        } else if (testOutcome.equals("Error")) {
            incrementErrors();
        } else if (testOutcome.equals("Problem")) {
            incrementProblems();
        } else if (testOutcome.equals("You decide")) {
            ;
        } else {
            throw new Error("Wrong test result");
        }
        getResults().addElement(testCase.getTestResult());
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
        return (LoadBuildSummary)loadBuildSummary.getValue();
    }

    public ValueHolderInterface getLoadBuildSummaryHolder() {
        return loadBuildSummary;
    }

    public String getName() {
        return name;
    }

    public TestResultsSummary getParent() {
        return (TestResultsSummary)parent.getValue();
    }

    public ValueHolderInterface getParentHolder() {
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
    public Vector getResults() {
        return (Vector)results.getValue();
    }

    /**
     * Return the no. test which passed in the test suite/model.
     */
    public ValueHolderInterface getResultsHolder() {
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

    public boolean shouldLogResult() {
        return !hasPassed();
    }

    /**
     * Passed means the test was ok.
     */
    public boolean hasPassed() {
        // This is a safest way to check then doing totaltests == passedtests.
        if ((getWarnings() != 0) || (getFatalErrors() != 0) || (getProblems() != 0) || (getErrors() != 0) || didSetupFail() || (getSetupFailures() != 0) || didSetupWarn() || (getSetupWarnings() != 0)) {
            return false;
        }
        return true;
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
    public void logResult(Writer log) {
        // The indentationString adds some number of tabs to the print stream.
        String indentationString = getTestCollection().getIndentationString();

        try {
            if (didSetupFail()) {
                log.write(indentationString + org.eclipse.persistence.internal.helper.Helper.cr() + "## SETUP FAILURE: " + org.eclipse.persistence.internal.helper.Helper.cr() + getSetupException() + org.eclipse.persistence.internal.helper.Helper.cr());
                PrintWriter printWriter = new PrintWriter(log);
                getSetupException().printStackTrace(printWriter);
                log.flush();
            } else if (didSetupWarn()) {
                log.write(indentationString + "SETUP WARNING: " + org.eclipse.persistence.internal.helper.Helper.cr());
                ((TestWarningException)getSetupException()).setIndentationString(indentationString);
                log.write(getSetupException() + org.eclipse.persistence.internal.helper.Helper.cr());
                log.flush();
            } else {
                if ((getErrors() > 0) || (getFatalErrors() > 0) || (getProblems() > 0)) {
                    log.write(indentationString + "###ERRORS###" + org.eclipse.persistence.internal.helper.Helper.cr());
                }
                log.write(indentationString + "Warnings: " + getWarnings() + org.eclipse.persistence.internal.helper.Helper.cr());
                log.write(indentationString + "Problems: " + getProblems() + org.eclipse.persistence.internal.helper.Helper.cr());
                log.write(indentationString + "Errors: " + getErrors() + org.eclipse.persistence.internal.helper.Helper.cr());
                log.write(indentationString + "Fatal Errors: " + getFatalErrors() + org.eclipse.persistence.internal.helper.Helper.cr());
                log.write(indentationString + "Passed: " + getPassed() + org.eclipse.persistence.internal.helper.Helper.cr());
                log.write(indentationString + "Setup Warnings: " + getSetupWarnings() + org.eclipse.persistence.internal.helper.Helper.cr());
                log.write(indentationString + "Setup Failures: " + getSetupFailures() + org.eclipse.persistence.internal.helper.Helper.cr());
                log.write(indentationString + "Total Time: " + getTotalTime() + org.eclipse.persistence.internal.helper.Helper.cr());
                log.write(indentationString + "Total Tests: " + getTotalTests() + org.eclipse.persistence.internal.helper.Helper.cr() + org.eclipse.persistence.internal.helper.Helper.cr());
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
                log.write(indentationString + org.eclipse.persistence.internal.helper.Helper.cr() + "## SETUP FAILURE: " + org.eclipse.persistence.internal.helper.Helper.cr() + getSetupException() + org.eclipse.persistence.internal.helper.Helper.cr());
                PrintWriter printWriter = new PrintWriter(log);
                getSetupException().printStackTrace(printWriter);
                log.flush();
            } else {
                if ((getErrors() > 0) || (getFatalErrors() > 0) || (getProblems() > 0)) {
                    log.write(indentationString + "###ERRORS###" + org.eclipse.persistence.internal.helper.Helper.cr());
                }

                //log.write(indentationString + "Warnings: " + getWarnings() + org.eclipse.persistence.internal.helper.Helper.cr());
                log.write(indentationString + "Problems: " + getProblems() + org.eclipse.persistence.internal.helper.Helper.cr());
                log.write(indentationString + "Errors: " + getErrors() + org.eclipse.persistence.internal.helper.Helper.cr());
                log.write(indentationString + "Fatal Errors: " + getFatalErrors() + org.eclipse.persistence.internal.helper.Helper.cr());
                //log.write(indentationString + "Passed: " + getPassed() + org.eclipse.persistence.internal.helper.Helper.cr());
                log.write(indentationString + "Setup Failures: " + getSetupFailures() + org.eclipse.persistence.internal.helper.Helper.cr() + org.eclipse.persistence.internal.helper.Helper.cr());
                //log.write(
                //	indentationString + "Total Tests: " + getTotalTests() + org.eclipse.persistence.internal.helper.Helper.cr()
                //	+ org.eclipse.persistence.internal.helper.Helper.cr());
                log.flush();
            }
        } catch (IOException exception) {
        }
    }

    /**
     * Reset the totals.
     */
    public void resetTotals() {
        setResults(new Vector());
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

    public void setLoadBuildSummaryHolder(ValueHolderInterface holder) {
        loadBuildSummary = holder;
    }

    public void setName(String aName) {
        name = aName;
    }

    public void setParent(TestResultsSummary result) {
        parent.setValue(result);
    }

    public void setParentHolder(ValueHolderInterface holder) {
        parent = holder;
    }

    public void setPassed(int thePassed) {
        passed = thePassed;
    }

    public void setProblems(int theProblem) {
        problems = theProblem;
    }

    public void setResults(Vector theResults) {
        results.setValue(theResults);
    }

    public void setResultsHolder(ValueHolderInterface holder) {
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
