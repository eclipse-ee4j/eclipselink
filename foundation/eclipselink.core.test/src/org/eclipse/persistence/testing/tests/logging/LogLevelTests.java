/*******************************************************************************
 * Copyright (c) 2015  Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      Tomas Kraus - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.logging;

import org.eclipse.persistence.logging.LogLevelHelper;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestSuite;

/**
 * Unit tests for EclipseLink log levels enumeration.
 */
public class LogLevelTests extends TestCase {

    /**
     * Add tests from this class into provided test suite.
     * @param suite Test suite where to add tests.
     */
    public static void addTests(final TestSuite suite) {
        suite.addTest(new TestLength());
        suite.addTest(new TestToValue());
        suite.addTest(new TestShouldLog());
    }

    /**
     * Creates an instance of jUnit tests for EclipseLink log levels enumeration.
     */
    public LogLevelTests() {
        super();
    }

    /**
     * Creates an instance of jUnit tests for EclipseLink log levels enumeration.
     * @param name jUnit test name.
     */
    public LogLevelTests(final String name) {
        super();
        setName(name);
    }

    /**
     * Setup unit test.
     */
    public void setup() {
    }

    /**
     * Clean this test suite.
     */
    public void reset() {
    }

    /**
     * Test {@code LogLevel.length} value.
     */
    public static final class TestLength extends LogLevelTests {

        /**
         * Creates an instance of jUnit test.
         */
        public TestLength() {
            super();
        }

        /**
         * Creates an instance of jUnit test.
         * @param name jUnit test name.
         */
        public TestLength(final String name) {
            super(name);
        }

        /**
         * jUnit test method.
         */
        @Override
        public void test() {
            LogLevelHelper.testLength();
        }

    }

    /**
     * Test {@code LogLevel.toValue(String)} method.
     */
    public static final class TestToValue extends LogLevelTests {

        /**
         * Creates an instance of jUnit test.
         */
        public TestToValue() {
            super();
        }

        /**
         * Creates an instance of jUnit test.
         * @param name jUnit test name.
         */
        public TestToValue(final String name) {
            super(name);
        }

        /**
         * jUnit test method.
         */
        @Override
        public void test() {
            LogLevelHelper.testToValueString();
            LogLevelHelper.testToValueInt();
            LogLevelHelper.testToValueIntFallBack();
        }

    }

    /**
     * Test {@code LogLevel.toValue(String)} method.
     */
    public static final class TestShouldLog extends LogLevelTests {

        /**
         * Creates an instance of jUnit test.
         */
        public TestShouldLog() {
            super();
        }

        /**
         * Creates an instance of jUnit test.
         * @param name jUnit test name.
         */
        public TestShouldLog(final String name) {
            super(name);
        }

        /**
         * jUnit test method.
         */
        @Override
        public void test() {
            LogLevelHelper.testShouldLogOnLogLevel();
            LogLevelHelper.testShouldLogOnId();
        }

    }
}
