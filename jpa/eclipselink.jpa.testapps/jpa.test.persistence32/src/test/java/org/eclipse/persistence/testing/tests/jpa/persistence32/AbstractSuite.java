/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.testing.tests.jpa.persistence32;

import jakarta.persistence.Persistence;
import junit.framework.TestSuite;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper;

/**
 * Abstract {@link JUnitTestCase} suite.
 * Adds {@link #suiteSetUp()} and {@link #suiteTearDown()} methods executed before and after
 * whole test suite execution.
 * Contains suite wide {@link jakarta.persistence.EntityManagerFactory} instance initialized
 * by {@link #getPersistenceUnitName()}.
 * {@link jakarta.persistence.EntityManagerFactory} and database schema are initialized
 * in {@link #suiteSetUp()} method. Database schema is dropped in {@link #suiteTearDown()} method.
 *
 */
public abstract class AbstractSuite extends JUnitTestCase {

    // Total number of tests in this suite
    private static int TEST_COUNT = 0;
    // Test counter (decreasing from TEST_COUNT to 0) to trigger suiteSetUp/suiteTearDown
    private static int testCounter;

    // EntityManagerFactory instance shared by the whole suite
    static JpaEntityManagerFactory emf = null;

    /**
     * Build test suite.
     * Adds model test setup as first and model test cleanup as last test
     * in the returned tests collection.
     * Using this metod is mandatory for suite creation.
     *
     * @param name name of the suite
     * @param tests tests to add to the suite
     * @return collection of tests to execute
     */
    static TestSuite suite(String name, AbstractSuite... tests) {
        TestSuite suite = new TestSuite();
        suite.setName(name);
        for (AbstractSuite test : tests) {
            suite.addTest(test);
        }
        testCounter = TEST_COUNT = suite.testCount();
        return suite;
    }

    /**
     * Creates an instance of {@link AbstractSuite}.
     */
    public AbstractSuite() {
        super();
    }

    /**
     * Creates an instance of {@link AbstractSuite} with custom test case name.
     *
     * @param name name of the test case
     */
    public AbstractSuite(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    /**
     * Initialize the test suite.
     * This method is being executed before the whole test suite. This method initializes the suite wide
     * {@link jakarta.persistence.EntityManagerFactory} instance and creates the database schema.
     * Child class may overwrite this method to do additional initialization but should also call this method too.
     */
    protected void suiteSetUp() {
        emf = Persistence.createEntityManagerFactory(
                        getPersistenceUnitName(),
                        JUnitTestCaseHelper.getDatabaseProperties(getPersistenceUnitName()))
                .unwrap(EntityManagerFactoryImpl.class);
        emf.getSchemaManager().create(true);
    }

    /**
     * Clean up the test suite.
     * This method is being executed after the whole test suite. This method drops the database schema
     * and closes the suite wide {@link jakarta.persistence.EntityManagerFactory} instance.
     * Child class may overwrite this method to do additional cleanup but should also call this method too.
     */
    protected void suiteTearDown() {
        emf.getSchemaManager().drop(true);
        emf.close();
    }

    /**
     * This method is called before a test is executed.
     * This method implements {@link #suiteSetUp()} call, so it shall be called by child class if overwritten.
     */
    @Override
    public void setUp() {
        super.setUp();
        if (testCounter == TEST_COUNT) {
            suiteSetUp();
        }
        testCounter--;
    }

    /**
     * This method is called after a test is executed.
     * This method implements {@link #suiteTearDown()} call, so it shall be called by child class if overwritten.
     */
    @Override
    public void tearDown() {
        super.tearDown();
        if (testCounter == 0) {
            suiteTearDown();
        }
    }

    @Override
    public void clearCache() {
        emf.getCache().evictAll();
        super.clearCache();
    }

}
