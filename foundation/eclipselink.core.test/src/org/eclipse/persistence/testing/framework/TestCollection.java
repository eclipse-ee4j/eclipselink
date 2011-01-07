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

import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.sessions.*;

/**
 * <p>Purpose<b></b>: TestCollection is a collection of test suites and models. When a test collection is executed
 * all the test entities registered with it are executed one by one.
 */
public abstract class TestCollection extends junit.framework.TestSuite implements TestEntity {

    /** Store the name to allow serialization, for some reason JUnit name does not serialize. */
    private String name;
    
    /** Stores all the tests */
    private Vector tests;

    /** Stores the summary of the tests */
    private TestResultsSummary summary;

    /** Description of the test collection */
    private String description;
    private transient TestExecutor executor;

    /** The test collection that contains this test */
    private TestEntity container;

    /** This is used only for printing test results with proper indentation */
    private int nestedCounter;

    /** The indentation string that is added to each line of result for printing. */
    private String indentationString;

    /** Stores the last tests that were run. */
    private Vector finishedTests;

    /** A flag that indicates if the test collection is inside SRG. */
    protected boolean isSRG;

    public TestCollection() {
        initialize();
    }

    /**
     * Constructs a TestSuite from the given class with the given name.
     */
    public TestCollection(Class theClass, String name) {
        super(theClass, name);
        initialize();
    }

    /**
     * Constructs a TestSuite from the given class. Adds all the methods
     * starting with "test" as test cases to the suite.
     */
    public TestCollection(final Class theClass) {
        super(theClass);
        initialize();
    }

    public void initialize() {
        description = "";
        nestedCounter = INITIAL_VALUE;
        tests = new Vector();
        finishedTests = new Vector();
        summary = new TestResultsSummary(this);
        if ((getName() == null) || (getName().length() == 0)) {
            setName(getClass().getName().substring(getClass().getName().lastIndexOf('.') + 1));
        } else {
            setName(getName());
        }
    }
    
    /**
     * Reset the JUnit name in case serialization looses it.
     */
    public String getName() {
        if (super.getName() == null) {
            setName(this.name); 
        }
        return super.getName();
    }
    
    /**
     * Store the test name locally to ensure it can serialize.
     */
    public void setName(String name) {
       super.setName(name);
       this.name = name;
    }

    /**
     * Adds a test to itself
     */
    public final void addTest(junit.framework.Test test) {
        super.addTest(test);
        if (test instanceof TestEntity) {
            ((TestEntity)test).setContainer(this);
        }
        getTests().add(test);
    }

    public abstract void addTests();

    public void addSRGTests() {
    }

    /**
     * Adds a test collection to itself
     */
    public final void addTests(Vector theTests) {
        for (Enumeration allTests = theTests.elements(); allTests.hasMoreElements();) {
            junit.framework.Test test = (junit.framework.Test)allTests.nextElement();
            addTest(test);
        }
    }

    public final void addServerTest(TestCase theTest) {
        TestCase serverTestCase;
        Class serverTestCaseClass;
        Object[] args = new Object[1];

        try {

            /** The code below is doing a roundabout version of the following (because of packaging problems):
             *
             * org.eclipse.persistence.testing.framework.ejb.ServerTestCase serverTestCase;
             * serverTestCase = new org.eclipse.persistence.testing.framework.ejb.ServerTestCase(
             *   (org.eclipse.persistence.testing.ejb20.cmp.wls.TestFramework.EJBTestCase)theTest);
             */
            serverTestCaseClass = Class.forName("org.eclipse.persistence.testing.framework.ejb.ServerTestCase");
            args[0] = theTest;
            serverTestCase = (TestCase)serverTestCaseClass.getConstructors()[0].newInstance(args);

            serverTestCase.setName(theTest.getName());
            addTest(serverTestCase);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Append test summaries of the collection of tests.
     */
    public void appendTestResult(TestResultsSummary summary) {
        summary.appendTestCollectionResult(this);
    }

    /**
     * Computes the level for indentation.
     */
    public void computeNestedLevel() {
        TestEntity testContainer = getContainer();

        if ((testContainer != null) && (testContainer.getNestedCounter() != INITIAL_VALUE)) {
            setNestedCounter(testContainer.getNestedCounter() + 1);
        } else {
            incrementNestedCounter();
        }
    }

    /**
     * The session is initialized to the default login from the Persistent System
     * if no explicit login is done for testing. This method must be overridden in
     * the subclasses if different login is required.
     */
    public Session defaultLogin() {
        return (new TestSystem()).login();
    }

    /**
     * Return test collection which contains this test entity.
     */
    public TestEntity getContainer() {
        return container;
    }

    /**
     * Return the description of the test.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Return the executor.
     */
    protected TestExecutor getExecutor() {
        return executor;
    }

    public Vector getFinishedTests() {
        return finishedTests;
    }

    /**
     * Return the indentaitonString
     */
    public String getIndentationString() {
        return indentationString;
    }

    /**
     * Return the nested counter. This is used finally used to do proper indentation.
     */
    public int getNestedCounter() {
        return nestedCounter;
    }

    /**
     * Return the summary of the test execution.
     */
    public ResultInterface getReport() {
        return getSummary();
    }

    /**
     * Return the session.
     */
    protected org.eclipse.persistence.sessions.Session getSession() {
        return getExecutor().getSession();
    }

    /**
     * Return the session cast to DatabaseSession.
     */
    public org.eclipse.persistence.sessions.DatabaseSession getDatabaseSession() {
        return (org.eclipse.persistence.sessions.DatabaseSession)getExecutor().getSession();
    }

    /**
     * Return the session cast to AbstractSession.
     */
    public org.eclipse.persistence.internal.sessions.AbstractSession getAbstractSession() {
        return (org.eclipse.persistence.internal.sessions.AbstractSession)getExecutor().getSession();
    }

    /**
     * Return the summary of the test execution.
     */
    protected TestResultsSummary getSummary() {
        return summary;
    }

    /**
     * Return all the test entities associated with this collection.
     */
    public Vector getTests() {
        return tests;
    }
    
    /**
     * Counts the number of test cases that will be run by this test.
     * If the tests have not been added yet just return 1.
     */
    public int countTestCases() {
        if (getTests().isEmpty()) {
            return 1;
        }
        return super.countTestCases();
    }

    /**
     * Returns the number of tests in this suite
     */
    public int testCount() {
        return getTests().size();
    }

    /**
     * Returns the tests as an enumeration
     */
    public Enumeration tests() {
        return getTests().elements();
    }

    /**
     * Returns the test at the given index
     */
    public junit.framework.Test testAt(int index) {
        return (junit.framework.Test)getTests().elementAt(index);
    }

    public void incrementNestedCounter() {
        setNestedCounter(getNestedCounter() + 1);
    }

    /**
     * Runs the tests and collects their result in a TestResult.
     */
    public void run(junit.framework.TestResult result) {
        TestExecutor.setDefaultJUnitTestResult(result);
        try {
            TestExecutor executor = getExecutor();
            if (executor == null) {
                executor = TestExecutor.getDefaultExecutor();
            }
            execute(executor);
        } catch (Throwable exception) {
            if (exception instanceof TestWarningException) {
                    System.out.println("WARNING: " + exception);
            } else {
                result.addError(this, exception);
            }
        }
    }

    /**
     * Add foot note the the result for printing.
     */
    abstract protected void logFootNote(Writer log);

    /**
     * Add head note the the result for printing.
     * This method is added to migrate tests to Ora*Tst
     */
    abstract protected void logRegressionHeadNote(Writer log);

    /**
     * Add head note the the result for printing.
     */
    abstract protected void logHeadNote(Writer log);

    /**
     * Logs the result summary of the suite on the print stream.
     * This method is added to migrate tests to Ora*Tst
     */
    public void logRegressionResult(Writer log) {
        logResult(log, false, true);
    }

    /**
     * Logs the result summary of the suite on the print stream.
     */
    public void logResult(Writer log) {
        logResult(log, false, false);
    }
    
    /**
     * Logs the result summary of the suite on the print stream.
     */
    public void logResult(Writer log, boolean logOnlyErrors) {
        logResult(log, logOnlyErrors, false);
    }
    
    /**
     * Logs the result summary of the suite on the print stream.
     */
    public void logResult(Writer log, boolean logOnlyErrors, boolean regression) {
        computeResultSummary();
        computeNestedLevel();
        setIndentationString(Helper.getTabs(getNestedCounter()));

        if (regression) {
            logRegressionHeadNote(log);
        } else {
            logHeadNote(log);
        }

        for (Enumeration tests = getFinishedTests().elements(); tests.hasMoreElements();) {
            junit.framework.Test test = ((junit.framework.Test)tests.nextElement());
            if (test instanceof TestEntity) {
                TestEntity testEntity = (TestEntity)test;
                if (regression) {
                    if (!(testEntity instanceof TestCase) || !(testEntity.getReport().hasPassed() || ((TestResult)testEntity.getReport()).hasWarning())) {
                        testEntity.logRegressionResult(log);
                    }
                } else {                    
                    if ((!(testEntity instanceof TestCase)) || testEntity.getReport().shouldLogResult()) {
                        if (!logOnlyErrors || !(testEntity instanceof TestCollection) || !testEntity.getReport().hasPassed()) {
                            testEntity.logResult(log, logOnlyErrors);
                        }
                    }
                }
            } else {
                TestExecutor.logJUnitResult(test, log, Helper.getTabs(getNestedCounter() + 1));
            }
        }

        logFootNote(log);
        if (regression) {
            getSummary().logRegressionResult(log);
        } else {
            getSummary().logResult(log);
        }
    }    
    
    /**
     * Compute the reuslt summary.
     */
    public void computeResultSummary() {
        getSummary().resetTotals();
        for (Enumeration tests = getFinishedTests().elements(); tests.hasMoreElements();) {
            junit.framework.Test test = ((junit.framework.Test)tests.nextElement());
            if (test instanceof TestCase) {
                TestCase testEntity = (TestCase)test;
                testEntity.appendTestResult(getSummary());
            } else if (test instanceof TestCollection) {
                TestCollection testEntity = (TestCollection)test;
                testEntity.computeResultSummary();
                testEntity.appendTestResult(getSummary());
            } else {
                junit.framework.TestResult result = (junit.framework.TestResult) TestExecutor.getJUnitTestResults().get(test);
                getSummary().appendTestResult(result);
            }
        }
    }

    /**
     * Remove the test colleciton.
     */
    protected void removeTest(TestEntity test) {
        getTests().removeElement(test);
    }

    public boolean requiresDatabase() {
        return true;
    }

    public abstract void resetEntity();

    /**
     * Reinitialize the nested counter value.
     */
    public void resetNestedCounter() {
        setNestedCounter(INITIAL_VALUE);
        for (Enumeration tests = getTests().elements(); tests.hasMoreElements();) {
            Object test = tests.nextElement();
            if (test instanceof TestEntity) {
                ((TestEntity)test).resetNestedCounter();
            }
        }
    }

    /**
     * Set the test collection which contains this test
     */
    public void setContainer(TestEntity testEntity) {
        container = testEntity;
    }

    /**
     * Set the description of the test.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Set the executor.
     */
    public void setExecutor(TestExecutor anExecutor) {
        executor = anExecutor;
    }

    public void setFinishedTests(Vector finishedTests) {
        this.finishedTests = finishedTests;
    }

    /**
     * Set the indentaitonString
     */
    public void setIndentationString(String indentationString) {
        this.indentationString = indentationString;
    }

    /**
     * Set the nested counter value.
     */
    public void setNestedCounter(int level) {
        this.nestedCounter = level;
    }

    /**
     * Set the summary of the test collection.
     */
    protected void setSummary(TestResultsSummary theSummary) {
        summary = theSummary;
        theSummary.setTestCollection(this);
    }

    /**
     * Set the test result.
     */
    public void setReport(ResultInterface summary) {
        setSummary((TestResultsSummary)summary);
    }

    protected final void setTests(Vector theTests) {
        tests = theTests;
    }

    public abstract void setupEntity() throws Throwable;

    /**
     * Print the test as its name.
     */
    public String toString() {
        return getName();
    }
}
