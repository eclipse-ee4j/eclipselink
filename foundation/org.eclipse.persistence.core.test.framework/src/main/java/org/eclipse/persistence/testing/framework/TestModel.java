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
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Login;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.factories.SessionManager;

import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p><b>Purpose</b>: Test model is a collection of test suites and/or sub test models. When a
 * test model is executed all the test suites and models registered with it are
 * executed one by one.
 */
public class TestModel extends TestCollection {

    /** Configurations that must be set before this model is run */
    private Vector<TestSystem> requiredSystems;

    /** The model will force these Configurations to be set even if they are set before */
    private Vector<TestSystem> forcedRequiredSystems;

    /** Used to store tests added when the model is built. */
    private Vector<Test> originalTests;

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
        return shouldResetSystemAfterEachTestModel;
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
        this.requiredSystems = new Vector<>();
        this.forcedRequiredSystems = new Vector<>();
        this.isSetup = false;
        this.originalTests = new Vector<>();
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
    @Override
    public void addTests() {
    }

    /**
     * The subclasses must overwrite this method. To add tests to the model.
     * It could be collection of test suites or test models themselves.
     */
    @Override
    public void addSRGTests() {
    }

    /**
     * Build the required systems, but ensure that the variable is not modified.
     */
    public Vector<TestSystem> buildForcedRequiredSystems() {
        @SuppressWarnings({"unchecked"})
        Vector<TestSystem> constructedSystems = (Vector<TestSystem>)getForcedRequiredSystems().clone();
        addForcedRequiredSystems();
        Vector<TestSystem> allSystems = getForcedRequiredSystems();
        setForcedRequiredSystems(constructedSystems);
        return allSystems;
    }

    /**
     * Build the required systems, but ensure that the variable is not modified.
     */
    public Vector<TestSystem> buildRequiredSystems() {
        @SuppressWarnings({"unchecked"})
        Vector<TestSystem> constructedSystems = (Vector<TestSystem>)getRequiredSystems().clone();
        addRequiredSystems();
        Vector<TestSystem> allSystems = getRequiredSystems();
        setRequiredSystems(constructedSystems);
        return allSystems;
    }

    /**
     * Goes through each systems and configures them.
     */
    private void configure() throws Exception {
        Vector<TestSystem> systems = buildRequiredSystems();

        for (Iterator<TestSystem> iterator = systems.iterator(); iterator.hasNext();) {
            TestSystem system = iterator.next();

            // To improve test consistency always force systems to be reset.
            if (shouldResetSystemAfterEachTestModel()) {
                getExecutor().forceConfigureSystem(system);
            } else {
                getExecutor().configureSystem(system);
            }
        }

        systems = buildForcedRequiredSystems();

        for (Iterator<TestSystem> iterator = systems.iterator(); iterator.hasNext();) {
            TestSystem system = iterator.next();
            getExecutor().forceConfigureSystem(system);
        }
    }

    /**
     * Executes all the test entities in the collection.
     */
    @Override
    public void execute(TestExecutor executor) throws Throwable {
        setSummary(new TestResultsSummary(this));
        setExecutor(executor);
        long startTime = System.nanoTime();
        try {
            setupEntity();
            setFinishedTests(new Vector<>());
            try {
                for (Iterator<Test> iterator = getTests().iterator(); iterator.hasNext();) {
                    junit.framework.Test test = iterator.next();
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
            long endTime = System.nanoTime();
            getSummary().setTotalTime(endTime - startTime);
        }
    }

    /**
     * Return all the required systems that need to be configured even if they are already configured.
     */
    public Vector<TestSystem> getForcedRequiredSystems() {
        return forcedRequiredSystems;
    }

    /**
     * Return test that existed before setup.
     */
    protected Vector<Test> getOriginalTests() {
        return originalTests;
    }

    /**
     * Return all the required systems that need to be configured if they are not already configured.
     */
    public Vector<TestSystem> getRequiredSystems() {
        return requiredSystems;
    }

    public boolean isSetup() {
        return isSetup;
    }

    /**
     * Format the test output on the print stream.
     */
    @Override
    protected void logFootNote(Writer log) {
        try {
            log.write(System.lineSeparator() + getIndentationString() + "RESULTS OF TEST MODEL: " + getName() + System.lineSeparator());
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
            log.write(System.lineSeparator() + getIndentationString() + "TEST MODEL NAME: " + getName() + System.lineSeparator());
            log.write(getIndentationString() + "MODEL DESCRIPTION: " + getDescription() + System.lineSeparator());
        } catch (IOException exception) {
        }
    }

    /**
     * Format the test output on the print stream.
     */
    @Override
    protected void logHeadNote(Writer log) {
        try {
            log.write(System.lineSeparator() + getIndentationString() + "VERSION: " + org.eclipse.persistence.sessions.DatabaseLogin.getVersion());
            log.write(System.lineSeparator() + getIndentationString() + "TEST MODEL NAME: " + getName() + System.lineSeparator());
            log.write(getIndentationString() + "MODEL DESCRIPTION: " + getDescription() + System.lineSeparator());
        } catch (IOException exception) {
        }
    }

    /**
     * This is a optional method and it should be overridden if their is something
     * that test collection should perform after running itself.
     */
    public void reset() {
    }

    /**
     * If setupEntity has been called then this must be called to reset the model again.
     */
    @Override
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
            getExecutor().setConfiguredSystems(new Vector<>());

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
            if (this.login instanceof DatabaseLogin login) {
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
            SessionManager.getManager().setSessions(new ConcurrentHashMap<>());
            getDatabaseSession().login();
        }
        setIsSetup(false);
    }

    /**
     * Set all the required systems that need to be configured even if they are already configured.
     */
    public void setForcedRequiredSystems(Vector<TestSystem> systems) {
        this.forcedRequiredSystems = systems;
    }

    protected void setIsSetup(boolean isSetup) {
        this.isSetup = isSetup;
    }

    /**
     * Set the test that existed before setup.
     */
    protected void setOriginalTests(Vector<Test> originalTests) {
        this.originalTests = originalTests;
    }

    /**
     * Set all the required sytems that need to be configured if they are not already configured.
     */
    public void setRequiredSystems(Vector<TestSystem> systems) {
        this.requiredSystems = systems;
    }

    /**
     * This is a optional method and it should be overridden if their is something
     * that test collection should perform before running itself.
     */
    public void setup() {
    }

    /**
     * To set up the model also look at resetEntity
     */
    @Override
    @SuppressWarnings({"unchecked"})
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
            setOriginalTests((Vector<Test>)getTests().clone());
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
    @Override
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
    @Override
    public Enumeration<Test> tests() {
        if (isSetup() || (!getTests().isEmpty())) {
            return super.tests();
        }
        return getFinishedTests().elements();
    }

    /**
     * Returns the test at the given index.
     * If not setup, return the finished tests.
     */
    @Override
    public junit.framework.Test testAt(int index) {
        if (isSetup() || (!getTests().isEmpty())) {
            return super.testAt(index);
        }
        return getFinishedTests().elementAt(index);
    }
}
