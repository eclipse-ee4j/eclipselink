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

import java.util.HashMap;
import java.util.Map;

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

    // Number of tests in each suite, and how many of them are still to run, both keyed by test class.
    // One pair of counters was enough while every suite was built immediately before it ran. Surefire
    // 3.6.0 builds them all up front, which left a single pair of counters describing whichever suite
    // was built last: suiteTearDown() then dropped the schema and closed the shared factory in the
    // middle of the run, and everything after that failed on a closed EntityManagerFactory.
    private static final Map<Class<?>, Integer> SUITE_SIZE = new HashMap<>();
    private static final Map<Class<?>, Integer> TESTS_TO_RUN = new HashMap<>();

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
        Map<Class<?>, Integer> sizes = new HashMap<>();
        for (AbstractSuite test : tests) {
            suite.addTest(test);
            sizes.merge(test.getClass(), 1, Integer::sum);
        }
        // Assigned rather than accumulated, so that building the same suite twice is harmless.
        SUITE_SIZE.putAll(sizes);
        TESTS_TO_RUN.putAll(sizes);
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
        Class<?> suiteClass = getClass();
        // A test that reaches here without having been added through suite(...) - a single method
        // selected on the command line, say - counts as a suite of one, so it still gets the factory
        // and the schema it needs.
        int suiteSize = SUITE_SIZE.computeIfAbsent(suiteClass, testClass -> 1);
        int testsToRun = TESTS_TO_RUN.computeIfAbsent(suiteClass, testClass -> 1);
        if (testsToRun == suiteSize) {
            suiteSetUp();
        }
        TESTS_TO_RUN.put(suiteClass, testsToRun - 1);
    }

    /**
     * This method is called after a test is executed.
     * This method implements {@link #suiteTearDown()} call, so it shall be called by child class if overwritten.
     */
    @Override
    public void tearDown() {
        super.tearDown();
        if (TESTS_TO_RUN.getOrDefault(getClass(), -1) == 0) {
            suiteTearDown();
        }
    }

    @Override
    public void clearCache() {
        emf.getCache().evictAll();
        super.clearCache();
    }

}
