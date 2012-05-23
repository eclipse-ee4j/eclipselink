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

import java.io.*;
import java.math.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.indirection.*;

/**
 * <p><b>Purpose</b>:Class stores the test exception if raised and decides whether the test was
 * successfull or not.
 */
public class TestResult implements ResultInterface, Comparable, Serializable {
    public static final String ERROR = "Error";
    public static final String FATAL_ERROR = "FatalError";
    public static final String PASSED = "Passed";
    public static final String PROBLEM = "Problem";
    public static final String WARNING = "Warning";
    protected String name;
    protected String description;
    protected BigDecimal id;
    protected String outcome;
    protected transient ValueHolderInterface summary;
    protected transient ValueHolderInterface loadBuildSummary;
    protected EclipseLinkException exception;
    protected transient TestCase testCase;
    protected long totalTime;
    protected long testTime;

    public TestResult() {
        summary = new ValueHolder();
        loadBuildSummary = new ValueHolder();
    }

    public TestResult(TestCase testCase) {
        this.outcome = "Passed";
        this.testCase = testCase;
        try {
            this.name = testCase.getName();
            this.description = testCase.getDescription();
        } catch (NullPointerException e) {
        }
        summary = new ValueHolder();
        loadBuildSummary = new ValueHolder();
    }

    public TestResult(TestCase testCase, String result) {
        this.outcome = result;
        this.testCase = testCase;
        this.name = testCase.getName();
        this.description = testCase.getDescription();
        summary = new ValueHolder();
        loadBuildSummary = new ValueHolder();
    }

    public int compareTo(Object summary) {
        return getName().compareTo(((TestResult)summary).getName());
    }

    public String getDescription() {
        return description;
    }

    public EclipseLinkException getException() {
        return exception;
    }

    public boolean shouldLogResult() {
        return !hasPassed();
    }

    /**
     * Not return warning stacktrace
     */
    public String getExceptionStackTraceForDatabase() {
        if ((exception != null) && !hasWarning()) {
            StringWriter writer = new StringWriter();
            exception.printStackTrace(new PrintWriter(writer));
            String trace = writer.toString();

            // Trim to 2000
            // Must trim for writing to the database.
            if (trace.length() >= 2000) {
                trace = trace.substring(0, 1995);
            }
            return trace;
        }
        return null;
    }

    /**
     * Not return warning stacktrace
     */
    public String getExceptionStackTrace() {
        if (exception != null) {
            StringWriter writer = new StringWriter();
            exception.printStackTrace(new PrintWriter(writer));
            String trace = writer.toString();
            return trace;
        }
        return null;
    }

    public BigDecimal getId() {
        return id;
    }

    public long getTestTime() {
        return testTime;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTestTime(long testTime) {
        this.testTime = testTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    /**
     * This is use to return null when the test no error but warning
     */
    public EclipseLinkException getLoadBuildException() {
        return exception;
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

    /**
     * Return the outcome of the test.
     * This is one of, "Passed", "Warning", "Error", "FatalError"
     */
    public String getOutcome() {
        return outcome;
    }

    /**
     * Depending upon the result of the exception the test outcome is decided.
     */
    public String getOutcomeForException(EclipseLinkException exception) {
        if (exception == null) {
            return PASSED;
        } else if (exception instanceof TestErrorException) {
            return ERROR;
        } else if (exception instanceof TestProblemException) {
            return PROBLEM;
        } else if (exception instanceof TestWarningException) {
            return WARNING;
        } else {
            return FATAL_ERROR;
        }
    }

    public TestResultsSummary getSummary() {
        return (TestResultsSummary)summary.getValue();
    }

    public ValueHolderInterface getSummaryHolder() {
        return summary;
    }

    public TestCase getTestCase() {
        return testCase;
    }

    public void setTestCase(TestCase testCase) {
        this.testCase = testCase;
    }

    /**
     * A error means that the verify detected a failure in the test.
     */
    public boolean hasError() {
        return getOutcome().equals(ERROR);
    }

    /**
     * A fatal error means that an unhandled exception occurred during the test.
     */
    public boolean hasFatalError() {
        return getOutcome().equals(FATAL_ERROR);
    }

    /**
     * Passed means the test was ok.
     */
    public boolean hasPassed() {
        return getOutcome().equals(PASSED);
    }

    /**
     * A problem means a error occurred during setup or reset.
     */
    public boolean hasProblem() {
        return getOutcome().equals(PROBLEM);
    }

    /**
     * A warning means the test did not pass, but there may be a reason for it too fail on this platform.
     */
    public boolean hasWarning() {
        return getOutcome().equals(WARNING);
    }

    /**
     * Return if the test failed (did not pass or throw warning).
     */
    public boolean hasFailed() {
        return !(hasPassed() || hasWarning());
    }
    
    /**
     * logs the result of the test on the print stream.
     */
    public void logRegressionResult(Writer log) {
        String indentationString = getTestCase().getIndentationString();
        try {
            log.write(indentationString + "RESULT:      " + getOutcome() + org.eclipse.persistence.internal.helper.Helper.cr());
            log.flush();
        } catch (IOException exception) {
        }
        LoadBuildSystem.loadBuild.addResult(this);
    }

    /**
     * logs the result of the test on the print stream.
     */
    public void logResult(Writer log) {
        String indentationString = getTestCase().getIndentationString();
        try {
            if (hasError() || hasFatalError() || hasProblem()) {
                log.write(indentationString + "##FAILURE##" + org.eclipse.persistence.internal.helper.Helper.cr());
            }
            log.write(indentationString + "TEST TIME:      " + getTestTime() + org.eclipse.persistence.internal.helper.Helper.cr());
            log.write(indentationString + "TOTAL TIME:      " + getTotalTime() + org.eclipse.persistence.internal.helper.Helper.cr());
            log.write(indentationString + "RESULT:      " + getOutcome() + org.eclipse.persistence.internal.helper.Helper.cr());
            if (getException() != null) {
                getException().setIndentationString(indentationString);
                // Do not print the stack for warnings, just the exception.
                if (hasWarning()) {
                    log.write(getException() + org.eclipse.persistence.internal.helper.Helper.cr());
                } else {
                    log.write(indentationString);
                    log.flush();
                    log.write(getExceptionStackTrace() + org.eclipse.persistence.internal.helper.Helper.cr());
                }
            }
            log.flush();
        } catch (IOException exception) {
        }
        LoadBuildSystem.loadBuild.addResult(this);
    }

    public void setDescription(String aDescription) {
        description = aDescription;
    }

    /**
     * Set the exception raised by the test case in the result.
     * Set the outcome dependant on the exeception.
     */
    public void setException(EclipseLinkException anException) {
        exception = anException;
        setOutcome(getOutcomeForException(anException));
    }

    /**
     * Return the exception raised by the test.
     */
    public void setExceptionStackTrace(String stackTrace) {
        if (stackTrace != null) {
            if (exception == null) {
                exception = new TestException(stackTrace);
            }
        }
    }

    public void setId(BigDecimal anId) {
        id = anId;
    }

    public void setLoadBuildSummary(LoadBuildSummary summary) {
        if (loadBuildSummary == null) {
            loadBuildSummary = new ValueHolder();
        }
        loadBuildSummary.setValue(summary);
    }

    public void setLoadBuildSummaryHolder(ValueHolderInterface holder) {
        loadBuildSummary = holder;
    }

    public void setName(String aName) {
        name = aName;
    }

    /**
     * Set the outcome of the test.
     * This is one of, "Passed", "Warning", "Error", "FatalError"
     */
    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public void setSummary(TestResultsSummary summary) {
        if (this.summary == null) {
            this.summary = new ValueHolder();
        }
        this.summary.setValue(summary);
    }

    public void setSummaryHolder(ValueHolderInterface holder) {
        summary = holder;
    }

    public void setupTestResult(TestCase testCase) {
        this.testCase = testCase;
    }
}
