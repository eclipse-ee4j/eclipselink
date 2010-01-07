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
import javax.persistence.*;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.server.*;

/**
 * <p>
 * <b>Purpose</b>: Allow for a test or set of tests to be executed and manage their execution and results.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Execute the test entity.
 * <li> Sets a flag to either log the results of test or not.
 * <li> Handles the exception returned by the test entity.
 * </ul>
 */
public class TestExecutor {

    /** It is a Stream where the test entity results are logged into */
    protected Writer log;
    /** This attribute is added to migrate tests to Ora*Tst */
    protected Writer regressionLog;
    
    /** Session used to run tests. */
    protected Session session;
    
    /** EntityManagerFactory used to run JPA tests. */
    protected EntityManagerFactory entityManagerFactory;
    
    /** Allows the original test session to be stored and repalced. */
    protected Session originalSession;

    /** A boolean, when set to true would start handling the exceptions thrown by TopLink
     * and would not when set to false. */
    protected boolean shouldHandleErrors;

    /** When set to true would log the results */
    protected boolean shouldLogResults;
    
    protected Hashtable loadedModels;

    /** Contains a collection of all the configured systems */
    protected Vector configuredSystems;

    /** This is used to stop execution thread */
    protected boolean shouldStopExecution;

    /** Used for test event progress notification */
    protected junit.framework.TestListener listener;

    /** Hold a default executor.  Used to cache the executor for tests run in JUnit. */
    protected static TestExecutor executor;

    /** Hold a default JUnit TestResult.  Used to cache the result for JUnit tests run by the executor. */
    protected static junit.framework.TestResult defaultJUnitTestResult;
    
    /** Hold JUnit TestResult but test.  Used to store the results for JUnit tests run by the executor. */
    protected static Map junitTestResults;

    /** This is used to get rid pf tests running on server(OC4J) */
    public boolean isServer = false;
    
    /** Allow only errors to be logged. */
    protected boolean shouldLogOnlyErrors = false;
    
    public static String CR = org.eclipse.persistence.internal.helper.Helper.cr();

    /**
     * Return a default executor.  Used as the executor for tests run in JUnit, or by themselves.
     */
    public static TestExecutor getDefaultExecutor() {
        if (executor == null) {
            TestExecutor testExecutor = new TestExecutor();
            testExecutor.setSession((new TestSystem()).login());
            // Ensure connect successful before setting executor.
            executor = testExecutor;
        }
        return executor;
    }
    
    /** 
     * Return if only errors should be logged.
     */
    public boolean shouldLogOnlyErrors() {
        return shouldLogOnlyErrors;
    }    
    
    /** 
     * Set if only errors should be logged.
     */
    public void setShouldLogOnlyErrors(boolean shouldLogOnlyErrors) {
        this.shouldLogOnlyErrors = shouldLogOnlyErrors;
    }

    /**
     * Set the default executor.  Used as the executor for tests run in JUnit, or by themselves.
     */
    public static void setDefaultExecutor(TestExecutor theExecutor) {
        executor = theExecutor;
    }

    /**
     * Set the default JUnit TestResult.  Used to cache the result for JUnit tests run by the executor.
     */
    public static void setDefaultJUnitTestResult(junit.framework.TestResult theTestResult) {
        defaultJUnitTestResult = theTestResult;
    }

    /**
     * Return the default JUnit TestResult.  Used to cache the result for JUnit tests run by the executor.
     */
    public static junit.framework.TestResult getDefaultJUnitTestResult() {
        return defaultJUnitTestResult;
    }

    /**
     * Set the JUnit TestResults.  Used to store the result for JUnit tests run by the executor.
     */
    public static void setJUnitTestResults(Map results) {
        junitTestResults = results;
    }

    /**
     * Return the JUnit TestResults.  Used to store the result for JUnit tests run by the executor.
     */
    public static Map getJUnitTestResults() {
        if (junitTestResults == null) {
            junitTestResults = new HashMap();
        }
        return junitTestResults;
    }

    public TestExecutor() {
        this.log = new OutputStreamWriter(System.out);
        this.shouldLogResults = true;
        this.shouldHandleErrors = false;
        this.shouldStopExecution = false;
        this.configuredSystems = new Vector();
    }

    /**
     * INTERNAL:
     * Add persistent system to the executor configuration.
     */
    public void addConfigureSystem(TestSystem system) {
        if (!configuredSystemsContainsInstanceOf(system)) {
            getConfiguredSystems().addElement(system);
        }
    }

    /**
     * The loaded models hold all model in use to allow test case
     * The access other model to reuse their to setup.
     */
    public void addLoadedModels(Vector models) {
        for (Enumeration theModels = models.elements(); theModels.hasMoreElements();) {
            TestModel model = (TestModel)theModels.nextElement();
            getLoadedModels().put(model.getName(), model);
        }
    }

    /**
     * If the system is not already configured then configure it and store it in the executor.
     */
    public void configureSystem(TestSystem system) throws Exception {
        if (!configuredSystemsContainsInstanceOf(system)) {
            system.run(getSession());
            getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
            getConfiguredSystems().addElement(system);
        }
    }

    /**
     * Return true if the configuredSystems contains an instance of the class of the TestSystem parameter.
     */
    public boolean configuredSystemsContainsInstanceOf(TestSystem system) {
        for (Enumeration configuredSystemsEnum = getConfiguredSystems().elements();
                 configuredSystemsEnum.hasMoreElements();) {
            if (configuredSystemsEnum.nextElement().getClass().equals(system.getClass())) {
                return true;
            }
        }
        return false;
    }

    /**
     * PUBLIC:
     * The executor stops handling errors and throws them to the user. This is mainly
     * for testing purpose to actully know the place of problem.
     */
    public void doNotHandleErrors() {
        setShouldHandleErrors(false);
    }

    /**
     * PUBLIC:
     * The executor would stop logging error to the log stream.
     */
    public void doNotLogResults() {
        setShouldLogResults(false);
    }

    public void doNotStopExecution() {
        setShouldStopExecution(false);
    }

    /**
     * PUBLIC:
     * To execute any test entity.
     */
    public void execute(junit.framework.Test test) throws Throwable {
        if (shouldStopExecution()) {
            return;
        }

        try {
            getSession().logMessage("Begin " + test);
            if (getListener() != null) {
                getListener().startTest(test);
            }

            // If the suite was run through JUnit, or is a Junit suite,
            // run through the result, otherwise through the executor.
            if ((getDefaultJUnitTestResult() != null) || (!(test instanceof TestEntity))) {
                junit.framework.TestResult result = getDefaultJUnitTestResult();
                if (getDefaultJUnitTestResult() == null) {
                    result = new junit.framework.TestResult();
                    result.addListener(getListener());
                    getJUnitTestResults().put(test, result);
                }
                test.run(result);
            } else {
                ((TestEntity)test).execute(this);
            }
            if (getListener() != null) {
                getListener().endTest(test);
            }
            getSession().logMessage("Finished " + test);
            if (getAbstractSession().isInTransaction()) {
                throw new TestProblemException(test + " is a faulty test, transaction was left open and must always be closed.");
            }
        } catch (Throwable exception) {
            // Always catch warnings, and handle errors if shouldHandleErrors set.
            if ((!(exception instanceof TestWarningException)) && (!shouldHandleErrors())) {
                throw exception;
            }            
            if (getListener() != null) {
                getListener().endTest(test);
            }
            getSession().logMessage("Failed " + test);
        }
    }

    /**
     * Forced system to be configured even if it is already configured.
     */
    public void forceConfigureSystem(TestSystem system) throws Exception {
        removeConfigureSystem(system);
        system.run(getSession());
        addConfigureSystem(system);
    }

    /**
     * Return all the configured systems.
     */
    public Vector getConfiguredSystems() {
        return configuredSystems;
    }

    /**
     * Used for test event progress notifiaction.
     */
    public junit.framework.TestListener getListener() {
        return listener;
    }

    /**
     * Return the model by name.
     * If missing null is returned.
     */
    public TestModel getLoadedModel(String modelsName) {
        return (TestModel)getLoadedModels().get(modelsName);
    }

    /**
     * The loaded models hold all model in use to allow test case
     * The access other model to reuse their to setup.
     */
    public Hashtable getLoadedModels() {
        return loadedModels;
    }

    /**
     * Return the log stream to print the results on. Default is console.
     * This method is added to migrate to Ora*Tst
     */
    public Writer getRegressionLog() {
        return regressionLog;
    }

    /**
     * Return the log stream to print the results on. Default is console.
     */
    public Writer getLog() {
        if (getSession() == null) {
            return log;
        }
        return getSession().getLog();
    }
    
    /**
     * Create a new entity manager from the entity manager factory.
     * This entity manager is initialized from META-INF/persistence.xml.
     */
    public EntityManager createEntityManager() {
        return entityManagerFactory.createEntityManager();
    }
    
    /**
     * Return the executor entity manager factory.
     * This lazy initializes from the "performance" persistent unit using the default provider,
     * and configures the TopLink properties to connect to the executor's session's login.
     */
    public EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null) {
            Map properties = new HashMap();
            properties.put(PersistenceUnitProperties.JDBC_DRIVER, getSession().getLogin().getDriverClassName());
            properties.put(PersistenceUnitProperties.JDBC_URL, getSession().getLogin().getConnectionString());
            properties.put(PersistenceUnitProperties.JDBC_USER, getSession().getLogin().getUserName());
            properties.put(PersistenceUnitProperties.JDBC_PASSWORD, getSession().getLogin().getPassword());
            properties.put(PersistenceUnitProperties.LOGGING_LEVEL, getSession().getSessionLog().getLevelString());
            entityManagerFactory = Persistence.createEntityManagerFactory("performance", properties);
        }
        return entityManagerFactory;
    }
    
    /**
     * Set the executor entity manager factory.
     */
    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }
    
    /**
     * Return the session cast to DatabaseSession.
     */
    public org.eclipse.persistence.sessions.DatabaseSession getDatabaseSession() {
        return (org.eclipse.persistence.sessions.DatabaseSession)getSession();
    }

    /**
     * Return the session cast to AbstractSession.
     */
    public org.eclipse.persistence.internal.sessions.AbstractSession getAbstractSession() {
        return (org.eclipse.persistence.internal.sessions.AbstractSession)getSession();
    }

    /**
     * Return the session.
     */
    public Session getSession() {
        return session;
    }

    /**
     * Return the original session.
     */
    public Session getOriginalSession() {
        return originalSession;
    }

    /**
     * Set the original session.
     */
    public void setOriginalSession(Session originalSession) {
        this.originalSession = originalSession;
    }
    
    /**
     * Swap the current session with the new session.
     * This allows a test or model to customize the session it uses.
     */
    public void swapSession(Session newSession) {
        setOriginalSession(getSession());
        setSession(newSession);
    }
    
    /**
     * Swap the current session with a new database session with the same login,
     * but no descriptors.
     */
    public void swapCleanDatabaseSession() {
        DatabaseSession session = new Project(getSession().getLogin()).createDatabaseSession();
        session.setSessionLog(getSession().getSessionLog());
        session.login();
        swapSession(session);        
    }
    
    /**
     * Swap the current session with the new server session.
     */
    public void swapServerSession() {
        Server session = getSession().getProject().createServerSession();
        session.setSessionLog(getSession().getSessionLog());
        session.login();
        swapSession(session);
    }
    
    /**
     * Swap the current session with the new session.
     * This allows a test or model to customize the session it uses.
     */
    public void resetSession() {
        if(getOriginalSession() != null) {
            if(getDatabaseSession().isConnected()) {
                getDatabaseSession().logout();
            }
            setSession(getOriginalSession());
            setOriginalSession(null);
        }
    }

    /**
     * PUBLIC:
     * Starts handling errors. Even if some error is raised Executor just
     * catches it and goes on to execute the next test.
     */
    public void handleErrors() {
        setShouldHandleErrors(true);
    }

    public void initializeConfiguredSystems() {
        setConfiguredSystems(new Vector());
    }

    /**
     * This logs out from session.
     */
    protected void logout() {
        if (session != null) {
            ((DatabaseSession)session).logout();
        }
    }

    /**
     * Logs the result for the given test entity if logResults is true.
     * This method is added to migrate tests to Ora*Tst
     */
    public void logRegressionResultForTestEntity(junit.framework.Test test) {
        logResultForTestEntity(test, true);
    }

    /**
     * Logs the result for the given test entity if logResults is true.
     */
    public void logResultForTestEntity(junit.framework.Test test) {
        logResultForTestEntity(test, false);
    }
    
    /**
     * Logs the result for the given test entity if logResults is true.
     */
    public void logResultForTestEntity(junit.framework.Test test, boolean regression) {
        Writer log = getLog();
        if (regression) {
            log = getRegressionLog();
        }
        if (shouldStopExecution()) {
            try {
                log.write("!!! THE TEST EXECUTION WAS INTERRUPTED !!!");
                log.flush();
            } catch (IOException e) {
            }
        }

        if (shouldLogResults()) {
            if (test instanceof TestEntity) {
                TestEntity testEntity = (TestEntity)test;
                testEntity.resetNestedCounter();
                if (regression) {
                    testEntity.logRegressionResult(log);
                } else {
                    testEntity.logResult(log, shouldLogOnlyErrors());
                }
                testEntity.resetNestedCounter();
            } else {
                logJUnitResult(test, log, "");
            }
        }
        try {
            log.flush();
        } catch (IOException e) {
        }
    }

    /**
     * Log any JUnit results if present.
     */
    public static void logJUnitResult(junit.framework.Test test, Writer log, String indent) {
        try {
            log.write(CR);
            log.write(CR);
            log.write(indent + "TEST MODEL NAME: (JUnit test): " + test);
            log.write(CR);
            junit.framework.TestResult result = (junit.framework.TestResult) TestExecutor.getJUnitTestResults().get(test);
            if (result == null) {
                log.write(indent + "## SETUP FAILURE ## (no tests run)");                
                log.write(CR);
                log.flush();
                return;
            }
            if ((result.failureCount() > 0) || (result.errorCount() > 0)) {
                log.write(indent + "###ERRORS###" + CR);
            }
            log.write(CR);
            log.write(indent + "Errors: (failures): " + result.failureCount());
            log.write(CR);
            log.write(indent + "Fatal Errors: (errors): " + result.errorCount());
            log.write(CR);
            log.write(indent + "Passed: " + (result.runCount() - result.errorCount() - result.failureCount()));
            log.write(CR);
            log.write(indent + "Total Tests: " + result.runCount());
            log.write(CR);
            if (result.failureCount() > 0) {
                log.write(CR);
                log.write(indent + "Failures:");
                log.write(CR);
                for (Enumeration failures = result.failures(); failures.hasMoreElements();) {
                    junit.framework.TestFailure failure = (junit.framework.TestFailure)failures.nextElement();
                    String testString = failure.failedTest().toString();
                    int startIndex = testString.indexOf("(");
                    if (startIndex != -1) {
                        log.write(indent + "TEST SUITE NAME: " + testString.substring(startIndex + 1, testString.length() - 1));
                        log.write(CR);
                    }
                    log.write(indent + "TEST NAME: " + testString);
                    log.write(CR);
                    log.write(indent + "##FAILURE##" + CR);
                    log.write(indent + "RESULT:      Error (failure)");
                    log.write(CR);
                    log.write(indent + failure.trace());
                    log.write(CR);
                }
            }
            if (result.errorCount() > 0) {
                log.write(CR);
                log.write(indent + "Errors:");
                log.write(CR);
                for (Enumeration errors = result.errors(); errors.hasMoreElements();) {
                    junit.framework.TestFailure error = (junit.framework.TestFailure)errors.nextElement();
                    String testString = error.failedTest().toString();
                    int startIndex = testString.indexOf("(");                    
                    if (startIndex != -1) {
                        log.write(indent + "TEST SUITE NAME: " + testString.substring(startIndex + 1, testString.length() - 1));
                        log.write(CR);
                    }
                    log.write(indent + "TEST NAME: " + testString);
                    log.write(CR);
                    log.write(indent + "##FAILURE##" + CR);
                    log.write(indent + "RESULT:      FatalError (error)");
                    log.write(CR);
                    log.write(indent + error.trace());
                    log.write(CR);
                }
            }
            log.write(CR);
            log.flush();
        } catch (IOException exception) {
        }
    }

    /**
     * Log the results to the log stream.
     */
    public void logResults() {
        setShouldLogResults(true);
    }

    /**
     * Public:
     * This method is used if we use dos promt to run our test cases.
     */
    public static void main(String[] arguments) {
        try {
            TestExecutor executor = new TestExecutor();

            //		executor.handleErrors();
            //		executor.doNotLogResults();
            executor.execute((TestEntity)Class.forName(arguments[0]).newInstance());
        } catch (Throwable exception) {
            System.out.println(exception.toString());
        }
    }

    public void removeConfigureSystem(TestSystem system) {
        removeFromConfiguredSystemsInstanceOf(system);
    }

    /**
     * If an instance of the same class as the parameter exists in the Vector of configuredSystems
     * then remove it.
     */
    public void removeFromConfiguredSystemsInstanceOf(TestSystem system) {
        // find and record the systems to remove
        Vector systemsToRemove = new Vector();
        for (Enumeration systemEnum = getConfiguredSystems().elements();
                 systemEnum.hasMoreElements();) {
            TestSystem aSystem = (TestSystem)systemEnum.nextElement();
            if (aSystem.getClass().equals(system.getClass())) {
                systemsToRemove.addElement(aSystem);
            }
        }

        // Do the removing
        for (Enumeration systemsToRemoveEnum = systemsToRemove.elements();
                 systemsToRemoveEnum.hasMoreElements();) {
            getConfiguredSystems().removeElement(systemsToRemoveEnum.nextElement());
        }
    }

    /**
     * The loaded models hold all model in use to allow test case
     * to access other model to reuse their to setup.
     */
    public void resetLoadedModels() {
        setLoadedModels(new Hashtable());
    }

    /**
     * PUBLIC:
     * This method executes the test entity. This method sets the session by using test
     * entity default login and once the execution is over it explicitily logs out.
     */
    public void runTest(junit.framework.Test test) throws Throwable {
        boolean hasSession = true;

        setShouldStopExecution(false);
        if ((getSession() == null) && (test instanceof TestEntity)) {
            TestEntity testEntity = (TestEntity)test;
            hasSession = false;
            if (shouldHandleErrors()) {
                try {
                    setSession(testEntity.defaultLogin());
                } catch (Exception exception) {
                    logout();
                    return;
                }
            } else {
                setSession(testEntity.defaultLogin());
            }
        }

        try {
            execute(test);
            //This line is added to migrate tests to Ora*Tst
            if (getRegressionLog() != null) {
                logRegressionResultForTestEntity(test);
            }
            logResultForTestEntity(test);
        } finally {
            if (!hasSession) {
                logout();
            }
        }
    }

    /**
     * Set configured systems.
     */
    public void setConfiguredSystems(Vector configuredSystems) {
        this.configuredSystems = configuredSystems;
    }

    /**
     * Used for test event progress notification.
     */
    public void setListener(junit.framework.TestListener listener) {
        this.listener = listener;
    }

    /**
     * The loaded models hold all model in use to allow test case
     * The access other model to reuse their to setup.
     */
    protected void setLoadedModels(Hashtable loadedModels) {
        this.loadedModels = loadedModels;
    }

    /**
     * PUBLIC:
     * Set the log stream to which test results should be logged on.
     * This method is added to migrate to Ora*Tst
     */
    public void setRegressionLog(Writer writer) {
        this.regressionLog = writer;
    }

    /**
     * PUBLIC:
     * Set the log stream to which test results should be logged on.
     */
    public void setLog(Writer writer) {
        this.log = writer;
    }

    /**
     * PUBLIC:
     * Set the session
     */
    public void setSession(Session theSession) {
        // Don't allow bad tests to set session to null.
        if (theSession == null) {
            return;
        }
        session = theSession;
    }

    public void setShouldHandleErrors(boolean aBoolean) {
        shouldHandleErrors = aBoolean;
    }

    public void setShouldLogResults(boolean aBoolean) {
        shouldLogResults = aBoolean;
    }

    public void setShouldStopExecution(boolean aBoolean) {
        shouldStopExecution = aBoolean;
    }

    /**
     * Returns if errors are to be handled or not.
     */
    public boolean shouldHandleErrors() {
        return shouldHandleErrors;
    }

    /**
     * Returns if results should be logged or not.
     */
    public boolean shouldLogResults() {
        return shouldLogResults;
    }

    /**
     * Returns if test entities should be execute or not.
     */
    public boolean shouldStopExecution() {
        return shouldStopExecution;
    }

    public void stopExecution() {
        setShouldStopExecution(true);
    }
}
