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

import junit.framework.Test;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Vector;

/**
 * <p><b>Purpose</b>: Test suite is a collection of test cases and/or sub test suites. When a
 * test suite is executed all the test cases and suites registed with it are
 * executed one by one.
 */
public class TestSuite extends TestCollection {
    public TestSuite() {
        this.isSRG = false;
        addTests();
    }

    /**
     * Constructs a TestSuite from the given class with the given name.
     */
    public TestSuite(Class<?> theClass, String name) {
        super(theClass, name);
    }

    /**
     * Constructs a TestSuite from the given class. Adds all the methods
     * starting with "test" as test cases to the suite.
     */
    public TestSuite(final Class<?> theClass) {
        super(theClass);
    }

    public TestSuite(boolean isSRG) {
        this.isSRG = isSRG;
        if (isSRG) {
            addSRGTests();
        } else {
            addTests();
        }
    }

    /**
     * The subclasses must overwrite this method. To add tests to the suite.
     * It could be collection of test cases or test suites themselves.
     */
    @Override
    public void addTests() {
        ;
    }

    /**
     * Executes all the test entities in the collection.
     */
    @Override
    @SuppressWarnings({"unchecked"})
    public void execute(TestExecutor executor) throws Throwable {
        setSummary(new TestResultsSummary(this));
        setExecutor(executor);
        computeNestedLevel();
        setupEntity();
        setFinishedTests(new Vector<>());
        if (getNestedCounter() < 1) {
            System.out.println();
            System.out.println("Running " + getSummary().getName());
        }
        long startTime = System.nanoTime();
        for (Iterator<Test> iterator = getTests().iterator(); iterator.hasNext();) {
            junit.framework.Test test = iterator.next();
            if ((TestExecutor.getDefaultJUnitTestResult() != null) && TestExecutor.getDefaultJUnitTestResult().shouldStop()) {
                break;
            }
            executor.execute(test);
            getFinishedTests().add(test);
        }
        long endTime = System.nanoTime();
        getSummary().setTotalTime(endTime - startTime);
        setFinishedTests((Vector<Test>)getTests().clone());
        if (getNestedCounter() < 1) {
            computeResultSummary();
            System.out.printf("Tests run: %d, Failures: %d, Errors: %d, Skipped: %d, Time elapsed: %.3f sec",
                    getSummary().getPassed(), getSummary().getErrors() + getSummary().getSetupFailures(),
                    getSummary().getFatalErrors(),
                    getSummary().getWarnings() + getSummary().getProblems() + getSummary().getSetupWarnings(),
                    getSummary().getTotalTime() / 1e9);
            System.out.println();
        }
        reset();
    }

    /**
     * Format the test output on the print stream.
     */
    @Override
    protected void logFootNote(Writer log) {
        try {
            log.write(System.lineSeparator() + getIndentationString() + "RESULTS OF TEST SUITE: " + getName() + System.lineSeparator());
        } catch (IOException exception) {
        }
    }

    /**
     * Format the test output on the print stream.
     * This method is added to migrate tests to Ora*Tst
     */
    @Override
    protected void logRegressionHeadNote(Writer log) {
        try {
            log.write(System.lineSeparator() + getIndentationString() + "TEST SUITE NAME: " + getName() + System.lineSeparator());
            log.write(getIndentationString() + "SUITE DESCRIPTION: " + getDescription() + System.lineSeparator());
        } catch (IOException exception) {
        }
    }

    /**
     * Format the test output on the print stream.
     */
    @Override
    protected void logHeadNote(Writer log) {
        try {
            log.write(System.lineSeparator() + System.lineSeparator() + getIndentationString() + "VERSION: " + org.eclipse.persistence.sessions.DatabaseLogin.getVersion());
            log.write(System.lineSeparator() + getIndentationString() + "TEST SUITE NAME: " + getName() + System.lineSeparator());
            log.write(getIndentationString() + "SUITE DESCRIPTION: " + getDescription() + System.lineSeparator());
        } catch (IOException exception) {
        }
    }

    public void reset() {
    }

    @Override
    public void resetEntity() {
        // Clear the cache to help make test runs more stable for erroronious test cases.
        // Use old API to be able to run on 9.0.4 for perf testing.
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    public void setup() {
    }

    @Override
    public void setupEntity() {
        try {
            setup();
        } catch (Throwable exception) {
            getSummary().setSetupException(exception);
            throw exception;
        }
    }
}
