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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Vector;

/**
 * <p><b>Purpose</b>:This is a JUnit Test wrapper for TopLink.
 * It simply calls the method corresponding to the test name.
 * Setup: Performs setUp().
 * Test: Runs the named Test.
 * Verify: Not Required.
 * Reset: Performs tearDown()
 * Reset Verify: Not Required.
 */
public class JUnitTestCase extends AutoVerifyTestCase {
    protected junit.framework.TestCase testCase;

    public JUnitTestCase(junit.framework.TestCase testCase) {
        this.testCase = testCase;
        setName(testCase.getName());
    }

    /**
     * Run the JUnit "setUp" method.
     */
    @Override
    public void setup() throws Throwable {
        try {
            Method setUp = testCase.getClass().getMethod("setUp");
            setUp.setAccessible(true);
            setUp.invoke(testCase);
        } catch (InvocationTargetException exception) {
            throw exception.getTargetException();
        } catch (Exception exception) {
            throw new TestException("Test Case: " + this.testCase.getClass() + " failed to setup: " + this.testCase.getName() + " with:" + exception, exception);
        }
    }

    /**
     * Run the JUnit "tearDown" method.
     */
    @Override
    public void reset() throws Throwable {
        try {
            Method tearDown = testCase.getClass().getMethod("tearDown");
            tearDown.setAccessible(true);
            tearDown.invoke(testCase);
        } catch (InvocationTargetException exception) {
            throw exception.getTargetException();
        } catch (Exception exception) {
            throw new TestException("Test Case: " + this.testCase.getClass() + " failed to reset: " + this.testCase.getName() + " with:" + exception, exception);
        }
    }

    /**
     * Run the JUnit "runTest" method.
     */
    @Override
    public void test() throws Throwable {
        try {
            Method runTest = null;
            try {
                runTest = testCase.getClass().getMethod(testCase.getName());
            } catch (NoSuchMethodException exc) {
                runTest = testCase.getClass().getMethod("runTest");
            }
            runTest.setAccessible(true);
            runTest.invoke(testCase);
        } catch (InvocationTargetException exception) {
            throw exception.getTargetException();
        } catch (Exception exception) {
            throw new TestException("Test Case: " + this.testCase.getClass() + " failed to run: " + this.testCase.getName() + " with:" + exception, exception);
        }
    }

    /**
     * Use this method to add JUnitTestCases to TestSuite or TestModel.
     * Example:
     * testSuite.addTests(JUnitTestCase.suite(MyJUnitTest.class));
     * will have the same effect as
     * testSuite.addTest(new JUnitTestCase(new MyJUnitTest("testA"));
     * testSuite.addTest(new JUnitTestCase(new MyJUnitTest("testB"));
     * testSuite.addTest(new JUnitTestCase(new MyJUnitTest("testC"));
     * where
     * class MyJUnitTest {
     *   void testA() {...
     *   void testB() {...
     *   void testC() {...
     * }
     */
    public static Vector<JUnitTestCase> suite(Class<?> junitTestCaseClass) {
        if (!(junit.framework.TestCase.class.isAssignableFrom(junitTestCaseClass))) {
            throw new TestProblemException("Class " + junitTestCaseClass + " is not derived from junit.framework.TestCase");
        }
        junit.framework.TestSuite suite;
        try {
            Method suiteMethod = junitTestCaseClass.getMethod("suite");
            suiteMethod.setAccessible(true);
            junit.framework.Test test = (junit.framework.Test)suiteMethod.invoke(null, new Object[0]);
            while(test instanceof junit.extensions.TestDecorator) {
                test = ((junit.extensions.TestDecorator)test).getTest();
            }
            suite = (junit.framework.TestSuite)test;
        } catch (NoSuchMethodException noSuchMethodEx) {
            suite = new junit.framework.TestSuite(junitTestCaseClass);
        } catch (InvocationTargetException invocationEx) {
            throw new TestProblemException("suite method failed on class " + junitTestCaseClass.getName() + " with InvocationTargetException, targetException: " + invocationEx.getTargetException().toString(), invocationEx.getTargetException());
        } catch (Exception exception) {
            throw new TestProblemException("suite method failed on class " + junitTestCaseClass.getName() + " with: " + exception, exception);
        }
        Vector<JUnitTestCase> testsOut = new Vector<>(suite.countTestCases());
        Enumeration<Test> tests = suite.tests();
        while (tests.hasMoreElements()) {
            junit.framework.TestCase testCaseToAdd = (junit.framework.TestCase)tests.nextElement();
            testsOut.add(new JUnitTestCase(testCaseToAdd));
        }
        return testsOut;
    }
}
