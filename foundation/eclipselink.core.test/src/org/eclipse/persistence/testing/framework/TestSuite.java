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

import java.io.*;
import java.util.*;

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
    public TestSuite(Class theClass, String name) {
        super(theClass, name);
    }

    /**
     * Constructs a TestSuite from the given class. Adds all the methods
     * starting with "test" as test cases to the suite.
     */
    public TestSuite(final Class theClass) {
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
    public void addTests() {
        ;
    }

    /**
     * Executes all the test entities in the collection.
     */
    public void execute(TestExecutor executor) throws Throwable {
        setSummary(new TestResultsSummary(this));
        setExecutor(executor);
        setupEntity();
        setFinishedTests(new Vector());
        long startTime = System.currentTimeMillis();
        for (Enumeration tests = getTests().elements(); tests.hasMoreElements();) {
            junit.framework.Test test = (junit.framework.Test)tests.nextElement();
            if ((TestExecutor.getDefaultJUnitTestResult() != null) && TestExecutor.getDefaultJUnitTestResult().shouldStop()) {
                break;
            }
            executor.execute(test);
            getFinishedTests().addElement(test);
        }
        long endTime = System.currentTimeMillis();
        getSummary().setTotalTime(endTime - startTime);
        setFinishedTests((Vector)getTests().clone());
    }

    /**
     * Format the test output on the print stream.
     */
    protected void logFootNote(Writer log) {
        try {
            log.write(org.eclipse.persistence.internal.helper.Helper.cr() + getIndentationString() + "RESULTS OF TEST SUITE: " + getName() + org.eclipse.persistence.internal.helper.Helper.cr());
        } catch (IOException exception) {
        }
    }

    /**
     * Format the test output on the print stream.
     * This method is added to migrate tests to Ora*Tst
     */
    protected void logRegressionHeadNote(Writer log) {
        try {
            log.write(org.eclipse.persistence.internal.helper.Helper.cr() + getIndentationString() + "TEST SUITE NAME: " + getName() + org.eclipse.persistence.internal.helper.Helper.cr());
            log.write(getIndentationString() + "SUITE DESCRIPTION: " + getDescription() + org.eclipse.persistence.internal.helper.Helper.cr());
        } catch (IOException exception) {
        }
    }

    /**
     * Format the test output on the print stream.
     */
    protected void logHeadNote(Writer log) {
        try {
            log.write(org.eclipse.persistence.internal.helper.Helper.cr() + org.eclipse.persistence.internal.helper.Helper.cr() + getIndentationString() + "VERSION: " + org.eclipse.persistence.sessions.DatabaseLogin.getVersion());
            log.write(org.eclipse.persistence.internal.helper.Helper.cr() + getIndentationString() + "TEST SUITE NAME: " + getName() + org.eclipse.persistence.internal.helper.Helper.cr());
            log.write(getIndentationString() + "SUITE DESCRIPTION: " + getDescription() + org.eclipse.persistence.internal.helper.Helper.cr());
        } catch (IOException exception) {
        }
    }

    public void reset() {
        return;
    }

    public void resetEntity() {
        // Clear the cache to help make test runs more stable for erroronious test cases.
        // Use old API to be able to run on 9.0.4 for perf testing.
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    public void setup() {
        return;
    }

    public void setupEntity() throws Throwable {
        try {
            setup();
        } catch (Throwable exception) {
            getSummary().setSetupException(exception);
            throw exception;
        }
    }
}
