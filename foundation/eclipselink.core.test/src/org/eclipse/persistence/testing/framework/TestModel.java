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
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Login;
import org.eclipse.persistence.testing.framework.TestExecutor;
import org.eclipse.persistence.sessions.factories.SessionManager;

/**
 * <p><b>Purpose</b>: Test model is a collection of test suites and/or sub test models. When a
 * test model is executed all the test suites and models registered with it are
 * executed one by one.
 */
public class TestModel extends TestCollection {

    /** Configurations that must be set before this model is run */
    private Vector requiredSystems;

    /** The model will force these Configurations to be set even if they are set before */
    private Vector forcedRequiredSystems;

    /** Used to store tests added when the model is built. */
    private Vector originalTests;

    /** Keep track of setup state. */
    private boolean isSetup;

    /** Ensure the login and log are not corrupted if the session is changed. */
    private Login login;
    private SessionLog sessionLog;

    /**
     * Flag used to determine if tables should be recreated/populated before each test model.
     * true means tests will take longer to run, but run more consistently as bad tests will not effect other models.
     * Uses system property "org.eclipse.persistence.testing.reset-system".
     */
    private static Boolean shouldResetSystemAfterEachTestModel;

    /**
     * Flag used to determine if tables should be recreated/populated before each test model.
     * true means tests will take longer to run, but run more consistently as bad tests will not effect other models.
     * Uses system property "org.eclipse.persistence.testing.reset-system".
     */
    public static boolean shouldResetSystemAfterEachTestModel() {
        if (shouldResetSystemAfterEachTestModel == null) {
            String systemProperty = System.getProperty("org.eclipse.persistence.testing.reset-system");
            if (systemProperty == null) {
                shouldResetSystemAfterEachTestModel = Boolean.TRUE;
            } else {
                if (systemProperty.equals("false")) {
                    shouldResetSystemAfterEachTestModel = Boolean.FALSE;
                } else {
                    shouldResetSystemAfterEachTestModel = Boolean.TRUE;
                }
            }
        }
        return shouldResetSystemAfterEachTestModel.booleanValue();
    }

    /**
     * Flag used to determine if tables should be recreated/populated before each test model.
     * true means tests will take longer to run, but run more consistently as bad tests will not effect other models.
     * Uses system property "org.eclipse.persistence.testing.reset-system".
     */
    public static void setShouldResetSystemAfterEachTestModel(boolean value) {
        if (value) {
            shouldResetSystemAfterEachTestModel = Boolean.TRUE;
        } else {
            shouldResetSystemAfterEachTestModel = Boolean.FALSE;
        }
    }

    public TestModel() {
        this.requiredSystems = new Vector();
        this.forcedRequiredSystems = new Vector();
        this.isSetup = false;
        this.originalTests = new Vector();
    }

    /**
     * The system add will be forced to be initialized again even if it is already configured.
     * Basically this means that required system will recreate new database.
     */
    public final void addForcedRequiredSystem(TestSystem requiredSystem) {
        getForcedRequiredSystems().addElement(requiredSystem);
    }

    /**
     * The system add will be forced to be initialized again even if it is already configured.
     * Basically this means that required system will recreate new database. This method should always
     * be overwritten in the subclasses if the model needs some persistence system to be already configured
     * before it is run.
     */
    public void addForcedRequiredSystems() {
    }

    /**
     * The system add will be configured if it has not been already configured.
     * Basically this means that required system will recreate new database.
     */
    public final void addRequiredSystem(TestSystem requiredSystem) {
        getRequiredSystems().addElement(requiredSystem);
    }

    /**
     * The system add will be configured if it has not been already configured.
     * Basically this means that required system will recreate new database.
     * This method should always be overwritten in the subclasses if the model
     * needs some persistence system to be already configured before it is run.
     */
    public void addRequiredSystems() {
    }

    /**
     * The subclasses must overwrite this method. To add tests to the model.
     * It could be collection of test suites or test models themselves.
     */
    public void addTests() {
    }

    /**
     * The subclasses must overwrite this method. To add tests to the model.
     * It could be collection of test suites or test models themselves.
     */
    public void addSRGTests() {
    }

    /**
     * Build the required systems, but ensure that the variable is not modified.
     */
    public Vector buildForcedRequiredSystems() {
        Vector constructedSystems = (Vector)getForcedRequiredSystems().clone();
        addForcedRequiredSystems();
        Vector allSystems = getForcedRequiredSystems();
        setForcedRequiredSystems(constructedSystems);
        return allSystems;
    }

    /**
     * Build the required systems, but ensure that the variable is not modified.
     */
    public Vector buildRequiredSystems() {
        Vector constructedSystems = (Vector)getRequiredSystems().clone();
        addRequiredSystems();
        Vector allSystems = getRequiredSystems();
        setRequiredSystems(constructedSystems);
        return allSystems;
    }

    /**
     * Goes through each systems and configures them.
     */
    private void configure() throws Exception {
        Vector systems = buildRequiredSystems();

        for (Enumeration enumtr = systems.elements(); enumtr.hasMoreElements();) {
            TestSystem system = (TestSystem)enumtr.nextElement();

            // To improve test consistency always force systems to be reset.
            if (shouldResetSystemAfterEachTestModel()) {
                getExecutor().forceConfigureSystem(system);
            } else {
                getExecutor().configureSystem(system);
            }
        }

        systems = buildForcedRequiredSystems();

        for (Enumeration enumtr = systems.elements(); enumtr.hasMoreElements();) {
            TestSystem system = (TestSystem)enumtr.nextElement();
            getExecutor().forceConfigureSystem(system);
        }
    }

    /**
     * Executes all the test entities in the collection.
     */
    public void execute(TestExecutor executor) throws Throwable {
        setSummary(new TestResultsSummary(this));
        setExecutor(executor);
        long startTime = System.currentTimeMillis();
        try {
            setupEntity();
            setFinishedTests(new Vector());
            try {
                for (Enumeration tests = getTests().elements(); tests.hasMoreElements();) {
                    junit.framework.Test test = (junit.framework.Test)tests.nextElement();
                    if ((TestExecutor.getDefaultJUnitTestResult() != null) && TestExecutor.getDefaultJUnitTestResult().shouldStop()) {
                            break;
                    }
                    executor.execute(test);
                    getFinishedTests().addElement(test);
                }
            } catch (Throwable exception) {
                try {
                    resetEntity();
                } catch (Throwable ignore) {
                }
                throw exception;
            }
            resetEntity();
        } finally {
            long endTime = System.currentTimeMillis();
            getSummary().setTotalTime(endTime - startTime);
        }
    }

    /**
     * Return all the required systems that need to be configured even if they are already configured.
     */
    public Vector getForcedRequiredSystems() {
        return forcedRequiredSystems;
    }

    /**
     * Return test that existed before setup.
     */
    protected Vector getOriginalTests() {
        return originalTests;
    }

    /**
     * Return all the required systems that need to be configured if they are not already configured.
     */
    public Vector getRequiredSystems() {
        return requiredSystems;
    }

    public boolean isSetup() {
        return isSetup;
    }

    /**
     * Format the test output on the print stream.
     */
    protected void logFootNote(Writer log) {
        try {
            log.write(org.eclipse.persistence.internal.helper.Helper.cr() + getIndentationString() + "RESULTS OF TEST MODEL: " + getName() + org.eclipse.persistence.internal.helper.Helper.cr());
        } catch (IOException exception) {
        }
    }

    /**
     * Format the test output on the print stream.
     * This method is added to migrate tests to Ora*Tst
     */
    protected void logRegressionHeadNote(Writer log) {
        try {
            log.write(org.eclipse.persistence.internal.helper.Helper.cr() + getIndentationString() + "TEST MODEL NAME: " + getName() + org.eclipse.persistence.internal.helper.Helper.cr());
            log.write(getIndentationString() + "MODEL DESCRIPTION: " + getDescription() + org.eclipse.persistence.internal.helper.Helper.cr());
        } catch (IOException exception) {
        }
    }

    /**
     * Format the test output on the print stream.
     */
    protected void logHeadNote(Writer log) {
        try {
            log.write(org.eclipse.persistence.internal.helper.Helper.cr() + getIndentationString() + "VERSION: " + org.eclipse.persistence.sessions.DatabaseLogin.getVersion());
            log.write(org.eclipse.persistence.internal.helper.Helper.cr() + getIndentationString() + "TEST MODEL NAME: " + getName() + org.eclipse.persistence.internal.helper.Helper.cr());
            log.write(getIndentationString() + "MODEL DESCRIPTION: " + getDescription() + org.eclipse.persistence.internal.helper.Helper.cr());
        } catch (IOException exception) {
        }
    }

    /**
     * This is a optional method and it should be overridden if their is something
     * that test collection should perform after running itself.
     */
    public void reset() {
        return;
    }

    /**
     * If setupEntity has been called then this must be called to reset the model again.
     */
    public void resetEntity() {
        if (isSetup()) {
            setTests(getOriginalTests());
            setIsSetup(false);
        }
        if(this.getSummary().didSetupWarn()) {
            return;
        }
        reset();
        // To improve test consistency cleanup the session and executor better.
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        if (shouldResetSystemAfterEachTestModel()) {            
            getExecutor().setConfiguredSystems(new Vector());

            // Logout and clean/reset the session in case test model failed ungracefully.
            if (getSession().isDatabaseSession()) {
                try {
                    getDatabaseSession().logout();
                } catch (Exception ignore) {
                }
            }
            if (this.login == null) {
                this.login = getSession().getDatasourceLogin();
                this.sessionLog = getSession().getSessionLog();
            }
            // Check if login or log were corrupted.
            if (this.login.getClass() != getSession().getDatasourceLogin().getClass()) {
                System.out.println("Login changed by test model:" + this);
            }
            if (this.sessionLog.getLevel() != getSession().getSessionLog().getLevel()) {
                System.out.println("Log level changed by test model:" + this);
            }
            if (this.login instanceof DatabaseLogin) {
                DatabaseLogin login = (DatabaseLogin)this.login;
                if (login.shouldBindAllParameters() != getSession().getLogin().shouldBindAllParameters()) {
                    System.out.println("Binding changed by test model:" + this);
                }
                if (login.shouldCacheAllStatements() != getSession().getLogin().shouldCacheAllStatements()) {
                    System.out.println("Statement caching changed by test model:" + this);
                }
                if (login.shouldUseBatchWriting() != getSession().getLogin().shouldUseBatchWriting()) {
                    System.out.println("Batch writing changed by test model:" + this);
                }
                if (login.shouldUseJDBCBatchWriting() != getSession().getLogin().shouldUseJDBCBatchWriting()) {
                    System.out.println("JDBC batch writing changed by test model:" + this);
                }
                if (login.shouldUseNativeSQL() != getSession().getLogin().shouldUseNativeSQL()) {
                    System.out.println("Native SQL changed by test model:" + this);
                }
                if (login.getTableQualifier() != getSession().getLogin().getTableQualifier()) {
                    System.out.println("Table qualifier changed by test model:" + this);
                }
            }
            DatabaseSession session = new Project(this.login).createDatabaseSession();
            session.setSessionLog(this.sessionLog);
            getExecutor().setSession(session);
            // Check if default conversion manager was corrupted.
            if (!ConversionManager.getDefaultManager().shouldUseClassLoaderFromCurrentThread()) {
                System.out.println("ConversionManager corrupted by test model:" + this);
            }
            ConversionManager.setDefaultManager(null);
            getSession().getDatasourceLogin().getDatasourcePlatform().setConversionManager(null);
            SessionManager.getManager().setSessions(new ConcurrentHashMap<String, Session>());
            getDatabaseSession().login();
        }
        setIsSetup(false);
    }

    /**
     * Set all the required systems that need to be configured even if they are already configured.
     */
    public void setForcedRequiredSystems(Vector systems) {
        this.forcedRequiredSystems = systems;
    }

    protected void setIsSetup(boolean isSetup) {
        this.isSetup = isSetup;
    }

    /**
     * Set the test that existed before setup.
     */
    protected void setOriginalTests(Vector originalTests) {
        this.originalTests = originalTests;
    }

    /**
     * Set all the required sytems that need to be configured if they are not already configured.
     */
    public void setRequiredSystems(Vector systems) {
        this.requiredSystems = systems;
    }

    /**
     * This is a optional method and it should be overridden if their is something
     * that test collection should perform before running itself.
     */
    public void setup() {
        return;
    }

    /**
     * To set up the model also look at resetEntity
     */
    public void setupEntity() throws Throwable {
        if (isSetup()) {
            return;
        }
        if (getSession() != null) {
            // Force field names to uppercase for postgres.
            if (getSession().getDatasourcePlatform().isPostgreSQL()) {
                getSession().getPlatform().setShouldForceFieldNamesToUpperCase(true);
            }
            this.login = getSession().getDatasourceLogin().clone();
            this.sessionLog = (SessionLog)getSession().getSessionLog().clone();
        }
        try {
            setOriginalTests((Vector)getTests().clone());
            configure();
            setup();
            if (isSRG) {
                addSRGTests();
            } else {
                addTests();
            }

            // Check for faulty models leaving transaction open.
            if ((getAbstractSession() != null) && getAbstractSession().isInTransaction()) {
                try {
                    int count = 0;
                    while (getAbstractSession().isInTransaction() && (count < 10)) {
                        getAbstractSession().rollbackTransaction();
                        count++;
                    }
                } catch (Throwable ignore) {
                }
                TestProblemException exception = new TestProblemException(this + " is a faulty test, transaction was left open and must always be closed.");
                throw exception;
            }
        } catch (Throwable exception) {
            getSummary().setSetupException(exception);

            try {
                resetEntity();
            } catch (Throwable resetException) {
            }

            throw exception;
        }

        setIsSetup(true);
    }

    /**
     * Returns the number of tests in this suite.
     * If not setup, return the finished tests.
     */
    public int testCount() {
        if (isSetup() || (!getTests().isEmpty())) {
            return super.testCount();
        }
        return getFinishedTests().size();
    }

    /**
     * Returns the tests as an enumeration.
     * If not setup, return the finished tests.
     */
    public Enumeration tests() {
        if (isSetup() || (!getTests().isEmpty())) {
            return super.tests();
        }
        return getFinishedTests().elements();
    }

    /**
     * Returns the test at the given index.
     * If not setup, return the finished tests.
     */
    public junit.framework.Test testAt(int index) {
        if (isSetup() || (!getTests().isEmpty())) {
            return super.testAt(index);
        }
        return (junit.framework.Test)getFinishedTests().elementAt(index);
    }
}
