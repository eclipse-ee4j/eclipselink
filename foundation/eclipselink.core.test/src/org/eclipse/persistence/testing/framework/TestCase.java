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
import javax.persistence.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.server.Server;
import org.eclipse.persistence.sessions.server.ServerSession;

/**
 * <p>Purpose<b></b>:
 * All the test cases are subclassed from this class.
 * Each test case tests single feature of TopLink. Ideally a test case consists of five steps.
 * Setup: Performs all the initial setup that is required by the test,
 * such as setting up database to some state on which test would run.
 * Test: The actual test to be performed, such as writing an object.
 * Verify: Verify the test if it was performed well or not.
 * Reset: Reset the database to the state from where the test started
 * Reset Verify: Check if reset performed well or not.
 */
public abstract class TestCase extends junit.framework.TestCase implements TestEntity {
    
    /** Store the name to allow serialization, for some reason JUnit name does not serialize. */
    private String name;
    
    /** The result of the test. */
    private TestResult testResult;
    
    /** The executor used to execute the test. */
    private transient TestExecutor executor;

    /** To provide small description of the test case */
    private String description;

    /** The test collection that contains this test */
    private TestEntity container;

    /** This is used only for printing test results with proper indentation */
    private int nestedCounter;

    /** The indentation string that is added to each line of result for printing. */
    private String indentationString;
    
    public TestCase() {
        description = "";
        nestedCounter = INITIAL_VALUE;
        testResult = new TestResult(this);
        setName(getClass().getName().substring(getClass().getName().lastIndexOf('.') + 1));
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
     * Append test case result to the test results summary.
     */
    public void appendTestResult(TestResultsSummary summary) {
        summary.appendTestCaseResult(this);
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
     * Return if the two objects match.
     */
    public boolean compareObjects(Object source, Object target) {
        return getAbstractSession().compareObjects(source, target);
    }

    /**
     * Check the database and ensure the object and its parts have been fully deleted.
     */
    public boolean verifyDelete(Object object) {
        return getAbstractSession().verifyDelete(object);
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
     * Executes this test case.
     * Note:
     * Only RuntimeExceptions are caught because all EclipseLink Exceptions are derived from
     * RuntimeException. This takes care of other java runtime exceptions also.
     */
    public void execute(TestExecutor executor) {
        boolean executeFailed = false;
        setTestResult(new TestResult(this, "Passed"));
        setExecutor(executor);

        long startTime = System.currentTimeMillis();
        try {
            try {
                setUp();
            } catch (EclipseLinkException exception) {
                executeFailed = true;
                setTestException(exception);
                throw exception;
            } catch (Throwable exception) {
                executeFailed = true;
                TestProblemException problem = new TestProblemException("Problem in the setup method of the test: " + getName());
                problem.setInternalException(exception);
                setTestException(problem);
                throw problem;
            }
    
            try {
                executeTest();    
                verify();
            } catch (EclipseLinkException exception) {
                executeFailed = true;
                if (getTestException() == null) {
                    setTestException(exception);
                    throw exception;
                }
            } catch (Throwable runtimeException) {
                executeFailed = true;
                TestErrorException topLinkException = new TestErrorException("Fatal error occurred.", runtimeException);
                if (getTestException() == null) {
                    setTestException(topLinkException);
                    throw topLinkException;
                }
            }
        } finally {
            try {
                tearDown();
            } catch (EclipseLinkException exception) {
                executeFailed = true;
                if (getTestException() == null) {
                    setTestException(exception);
                    throw exception;
                }
            } catch (Throwable exception) {
                executeFailed = true;
                TestProblemException problem = new TestProblemException("Problem in the reset method of the test");
                problem.setInternalException(exception);
                if (getTestException() == null) {
                    setTestException(problem);
                    throw problem;
                }
            } finally {
                long endTime = System.currentTimeMillis();
                getTestResult().setTotalTime(endTime - startTime);

                // If a failure occurred allow recreation of the database and initialize the identity maps.
                if (executeFailed) {
                    // If this test is not local allow for cleanup.
                    if (!isLocalTest()) {
                        cleanAfterExecuteFailed();
                    }
                }

                // Check for faulty tests leaving transaction open.
                if (getAbstractSession().isInTransaction()) {
                    try {
                        int count = 0;
                        while (getAbstractSession().isInTransaction() && (count < 10)) {
                            getAbstractSession().rollbackTransaction();
                            count++;
                        }
                    } catch (Throwable ignore) {
                    }
                    TestProblemException problem = new TestProblemException(this + " is a faulty test, transaction was left open and must always be closed.");
                    problem.setInternalException(getTestException());
                    setTestException(problem);
                    throw problem;
                }
            }
        }
    }

    /**
     * Sets up the fixture, for example, open a network connection.
     * This method is called before a test is executed.
     * Calls old setup method by default.
     */
    protected void setUp() throws Exception {
        try {
            setup();
        } catch (Throwable exception) {
            if (exception instanceof Exception) {
                throw (Exception)exception;
            } else {
                throw new TestErrorException("Fatal errored in setUp.", exception);
            }
        }
    }

    /**
     * Tears down the fixture, for example, close a network connection.
     * This method is called after a test is executed.
     * Calls old reset method by default.
     */
    protected void tearDown() throws Exception {
        try {
            reset();
            resetVerify();
        } catch (Throwable exception) {
            if (exception instanceof Exception) {
                throw (Exception)exception;
            } else {
                throw new TestErrorException("Fatal errored in tearDown.", exception);
            }
        }
    }

    /**
     * If there is no executor,
     * Create a default executor and run the test.
     */
    public void runBare() throws Throwable {
        TestExecutor executor = getExecutor();
        if (executor == null) {
            executor = TestExecutor.getDefaultExecutor();
        }
        try {
        	execute(executor);
        } catch (TestWarningException exception) {
        	System.out.println("WARNING: " + exception);
        }
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
    public TestExecutor getExecutor() {
        return executor;
    }

    /**
     * Get the indentaitonString
     */
    public String getIndentationString() {
        return indentationString;
    }

    /**
     * Return the nested counter value
     */
    public int getNestedCounter() {
        return nestedCounter;
    }

    /**
     * Return the test result. The testResult stores the result of this
     * test.
     */
    public ResultInterface getReport() {
        return getTestResult();
    }

    /**
     * Create a new entity manager from the entity manager factory.
     * This entity manager is initialized from META-INF/persistence.xml.
     */
    public EntityManager createEntityManager() {
        return getExecutor().createEntityManager();
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
     * Return the database session.
     */
    public org.eclipse.persistence.sessions.Session getSession() {
        return getExecutor().getSession();
    }

    /**
     * Return and cast the session to a Server.
     */
    public Server getServerSession() {
        return (Server)getExecutor().getSession();
    }

    /**
     * Begin a transaction, cast to AbstractSession to work with all session types.
     */
    public void beginTransaction() {
        getAbstractSession().beginTransaction();
    }

    /**
     * Commit a transaction, cast to AbstractSession to work with all session types.
     */
    public void commitTransaction() {
        getAbstractSession().commitTransaction();
    }

    /**
     * Rollback a transaction, cast to AbstractSession to work with all session types.
     */
    public void rollbackTransaction() {
        getAbstractSession().rollbackTransaction();
    }

    public EclipseLinkException getTestException() {
        return getTestResult().getException();
    }

    /**
     * Return the test result. The testResult stores the result of this
     * test.
     */
    public TestResult getTestResult() {
        return testResult;
    }

    public void incrementNestedCounter() {
        setNestedCounter(getNestedCounter() + 1);
    }

    /**
     * The result of the test is logged on to the specified print stream.
     * This method is added to migrate tests to Ora*Tst
     */
    public void logRegressionResult(Writer log) {
        computeNestedLevel();
        setIndentationString(Helper.getTabs(getNestedCounter()));

        try {
            log.write(org.eclipse.persistence.internal.helper.Helper.cr() + getIndentationString() + "TEST NAME:   " + getName() + org.eclipse.persistence.internal.helper.Helper.cr());
            log.write(getIndentationString() + "TEST DESCRIPTION: " + getDescription() + org.eclipse.persistence.internal.helper.Helper.cr());
            log.flush();
        } catch (IOException exception) {
        }
        getTestResult().logRegressionResult(log);
    }

    /**
     * The result of the test is logged on to the specified print stream.
     */
    public void logResult(Writer log, boolean shouldLogOnlyErrors) {
        logResult(log);
    }
    
    /**
     * The result of the test is logged on to the specified print stream.
     */
    public void logResult(Writer log) {
        computeNestedLevel();
        setIndentationString(Helper.getTabs(getNestedCounter()));

        try {
            log.write(org.eclipse.persistence.internal.helper.Helper.cr() + getIndentationString() + "VERSION:   " + org.eclipse.persistence.sessions.DatabaseLogin.getVersion());
            log.write(org.eclipse.persistence.internal.helper.Helper.cr() + getIndentationString() + "TEST NAME:   " + getName() + org.eclipse.persistence.internal.helper.Helper.cr());
            log.write(getIndentationString() + "TEST DESCRIPTION: " + getDescription() + org.eclipse.persistence.internal.helper.Helper.cr());
            log.flush();
        } catch (IOException exception) {
        }
        getTestResult().logResult(log);
    }

    public boolean requiresDatabase() {
        return true;
    }

    /**
     * This is a optional method in the test cases. It should be overridden only if somthing has to
     * be reset back to the state from where the test started.
     */
    public void reset() throws Throwable {
        return;
    }

    /**
     * Reset the entity.
     */
    public void resetEntity() {
        try {
            reset();
        } catch (Throwable runtimeException) {
            TestProblemException validationException = new TestProblemException("Reset problem occurred.", runtimeException);
            if (getTestException() == null) {
                setTestException(validationException);
            }
            throw validationException;
        }
    }

    public void resetNestedCounter() {
        setNestedCounter(INITIAL_VALUE);
    }

    /**
     * This is a mandatory method in the test cases only if reset has been overridden.
     * The method should check if reset method really reset the databse back to the state
     * from where the test started.
     */
    protected void resetVerify() throws Throwable {
        return;
    }

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
     * The exception raised by the test is stored in the result. Eventually the result
     * decides the outcome of the test depending upon the kind of exception. No exception is
     * stored if test runs well.
     */
    public void setTestException(EclipseLinkException exception) {
        getTestResult().setException(exception);
    }

    /**
     * Set the test result.
     */
    public void setReport(ResultInterface testResult) {
        setTestResult((TestResult)testResult);
    }
        
    /**
     * Set the test result. The testResult stores the result of the
     * test.
     */
    public void setTestResult(TestResult testResult) {
        this.testResult = testResult;
        testResult.setTestCase(this);
    }

    /**
     * The first step in testing process. The method is overridden if their is something
     * that test should perform before running the actual test.
     */
    protected void setup() throws Throwable {
        return;
    }

    /**
     * Override to run the test and assert its state.
     * @exception Throwable if any exception is thrown
     */
    protected void runTest() throws Throwable {
        // Nothing by default.
    }

    /**
     * Allow for test to intercept running of test.
     */
    public void executeTest() throws Throwable {
        test();
    }

    /**
     * Test can be define as test, or runTest,
     * or if the name is a method on this class,
     * this method is run reflectively by default.
     */
    protected void test() throws Throwable {
        runTest();
    }

    /**
     * Print the test as its name.
     */
    public String toString() {
        return getName();
    }

    /**
     * Verification of test is done here if the test ran properly or not. Subclasses should
     * override it.
     */
    protected void verify() throws Throwable {
        return;
    }

    /**
     * Throw a test error exception.
     * Errors indicate the test failed.
     */
    public void throwError(String message) {
        throw new TestErrorException(message);
    }

    /**
     * Throw a test error exception.
     * Errors indicate the test failed.
     */
    public void throwError(String message, Throwable exception) {
        throw new TestErrorException(message, exception);
    }

    /**
     * Throw a test warning exception.
     * Warning indicate the test did not fail, but did not pass either,
     * the test could not be run or the result could not be verified.
     */
    public void throwWarning(String message) {
        throw new TestWarningException(message);
    }

    /**
     * Sometimes a test prefers to perform a number of small checks/assertions
     * in the test() method, rather than calling a single test() followed
     * by a single verify().
     * <p>
     * In line with JUNIT style testing, would be called assert if assert
     * did not become a reserved keyword in JDK 1.4.2.
     */
    public void strongAssert(boolean assertion, String errorMessage) {
        if (!assertion) {
            throw new TestErrorException(errorMessage);
        }
    }

    /**
     * Same as strongAssert but only throws a warning.  I.e. if the check
     * is how the feature is implemented but not really crucial to how it works,
     * just throw a warning and fix the test later.
     * @param assertion
     * @param warningMessage
     */
    public void weakAssert(boolean assertion, String warningMessage) {
        if (!assertion) {
            throw new TestWarningException(warningMessage);
        }
    }

    /**
     * Answer true to a local test check if the class name ends in "Local"
     */
    private boolean isLocalTest() {
        return getClass().getName().endsWith("Local");
    }

    /**
     * If the execute fails, do the necessary cleanup to ensure the next test gets no residue
     */
    public void cleanAfterExecuteFailed() {
    }
    
    /**
     * Throws a warning of pessimistic locking/select for update is not supported for this test platform.
     * Currently testing supports select for update on Oracle, MySQL, SQLServer, TimesTen.
     * Some of the other platforms may have some support for select for update, but the databases we test with
     * for these do not have sufficient support to pass the tests.
     * Derby, Firebird and Symfoware (bug 304903) have some support, but does not work with joins (2008-12-01).
     */
    public void checkSelectForUpateSupported() {
        DatabasePlatform platform = getSession().getPlatform();
        if (platform.isFirebird() || platform.isAccess() || platform.isSybase() || platform.isSQLAnywhere() || platform.isDerby() || platform.isHSQL() || platform.isSymfoware()) {
            throw new TestWarningException("This database does not support FOR UPDATE");
        }
    }

    /**
     * Throws a warning of pessimistic locking/select for update nowait is not supported for this test platform.
     * Currently testing supports nowait on Oracle, SQLServer, PostgreSQL.
     */
    public void checkNoWaitSupported() {
        DatabasePlatform platform = getSession().getPlatform();
        if (!(platform.isOracle() || platform.isSQLServer() || platform.isPostgreSQL())) {
            throw new TestWarningException("This database does not support NOWAIT");        
        }
    }


    /**
     * Throws a warning if the test database is using serializable transaction isolation.
     */
    public void checkTransactionIsolation() {
        DatabasePlatform platform = getSession().getPlatform();
        if (platform.isSybase()) {
            if (SybaseTransactionIsolationListener.isDatabaseVersionSupported((ServerSession)getAbstractSession().getParent())) {
                SybaseTransactionIsolationListener listener = new SybaseTransactionIsolationListener();
                getAbstractSession().getParent().getEventManager().addListener(listener);
            } else {
                throw new TestWarningException("The test requires Sybase version "+SybaseTransactionIsolationListener.requiredVersion+" or higher");
            }
        } else if (platform.isSQLServer()) {
            throw new TestWarningException("This test requires transaction isolation setup on SQLServer database which is currently not set in tlsvrdb6");
        } else if (platform.isSQLAnywhere()) {
            throw new TestWarningException("This test requires transaction isolation setup on SQLAnywhere database which is currently not set");
        } else if (platform.isDB2()) {
            throw new TestWarningException("This test requires transaction isolation setup on DB2 database which is currently not set");
        } else if (platform.isSymfoware()) {
            TransactionIsolationLevelSwitchListener listener = new TransactionIsolationLevelSwitchListener();
            getAbstractSession().getParent().getEventManager().addListener(listener);
        }
    }
    
    /**
     * Return if stored procedures are supported for the database platform for the test database.
     */
    public static boolean supportsStoredProcedures(Session session) {
        DatabasePlatform platform = session.getPlatform();
        return platform.isOracle() || platform.isSybase() || platform.isMySQL() || platform.isSQLServer() || platform.isSymfoware();
    }
}
