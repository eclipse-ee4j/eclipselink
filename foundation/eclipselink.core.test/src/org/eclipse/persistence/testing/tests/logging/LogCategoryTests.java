/*******************************************************************************
 * Copyright (c) 2015, 2016  Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.logging.LogCategoryHelper;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestSuite;

/**
 * Unit tests for EclipseLink logging categories enumeration.
 */
public class LogCategoryTests extends TestCase {

    /**
     * Add tests from this class into provided test suite.
     * @param suite Test suite where to add tests.
     */
    public static void addTests(final TestSuite suite) {
        suite.addTest(new TestLength());
        suite.addTest(new TestToValue());
        suite.addTest(new TestGetNameSpace());
    }

    /**
     * Creates an instance of jUnit tests for EclipseLink logging categories enumeration.
     */
    public LogCategoryTests() {
        super();
    }

    /**
     * Creates an instance of jUnit tests for EclipseLink logging categories enumeration.
     * @param name jUnit test name.
     */
    public LogCategoryTests(final String name) {
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
     * Test {@code LogCategory.length} value.
     */
    public static final class TestLength extends LogCategoryTests {

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
            LogCategoryHelper.testLength();
        }

    }

    /**
     * Test {@code LogCategory.toValue(String)} method.
     */
    public static final class TestToValue extends LogCategoryTests {

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
            LogCategoryHelper.testToValue();
        }

    }

    /**
     * Test {@code LogCategory.getNameSpace(String)} method.
     */
    public static final class TestGetNameSpace extends LogCategoryTests {

        /**
         * Creates an instance of jUnit test.
         */
        public TestGetNameSpace() {
            super();
        }

        /**
         * Creates an instance of jUnit test.
         * @param name jUnit test name.
         */
        public TestGetNameSpace(final String name) {
            super(name);
        }

        /**
         * jUnit test method.
         * @throws NoSuchFieldException If there is a problem in {@code ReflectionHelper} call.
         * @throws SecurityException If there is a problem in {@code ReflectionHelper} call.
         * @throws IllegalArgumentException If there is a problem in {@code ReflectionHelper} call.
         * @throws IllegalAccessException If there is a problem in {@code ReflectionHelper} call.
         */
        @Override
        public void test() throws ReflectiveOperationException {
            LogCategoryHelper.testGetNameSpace();
        }

    }

}
