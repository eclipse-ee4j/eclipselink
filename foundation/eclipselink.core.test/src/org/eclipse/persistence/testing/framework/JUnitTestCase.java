/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

import java.lang.reflect.*;
import java.util.Enumeration;
import java.util.Vector;

/**
 * <p>Purpose<b></b>:This is a JUnit Test wrapper for TopLink.
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
    public void setup() throws Throwable {
        try {
            Method setUp = testCase.getClass().getMethod("setUp", new Class[0]);
            setUp.setAccessible(true);
            setUp.invoke(testCase, new Object[0]);
        } catch (InvocationTargetException exception) {
            throw exception.getTargetException();
        } catch (Exception exception) {
            throw new TestException("Test Case: " + this.testCase.getClass() + " failed to setup: " + this.testCase.getName() + " with:" + exception.toString(), exception);
        }
    }

    /**
     * Run the JUnit "tearDown" method.
     */
    public void reset() throws Throwable {
        try {
            Method tearDown = testCase.getClass().getMethod("tearDown", new Class[0]);
            tearDown.setAccessible(true);
            tearDown.invoke(testCase, new Object[0]);
        } catch (InvocationTargetException exception) {
            throw exception.getTargetException();
        } catch (Exception exception) {
            throw new TestException("Test Case: " + this.testCase.getClass() + " failed to reset: " + this.testCase.getName() + " with:" + exception.toString(), exception);
        }
    }

    /**
     * Run the JUnit "runTest" method.
     */
    public void test() throws Throwable {
        try {
            Method runTest = null;
            try {
                runTest = testCase.getClass().getMethod(testCase.getName(), new Class[0]);
            } catch (NoSuchMethodException exc) {
                runTest = testCase.getClass().getMethod("runTest", new Class[0]);
            }
            runTest.setAccessible(true);
            runTest.invoke(testCase, new Object[0]);
        } catch (InvocationTargetException exception) {
            throw exception.getTargetException();
        } catch (Exception exception) {
            throw new TestException("Test Case: " + this.testCase.getClass() + " failed to run: " + this.testCase.getName() + " with:" + exception.toString(), exception);
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
    public static Vector suite(Class junitTestCaseClass) {
        if (!(junit.framework.TestCase.class.isAssignableFrom(junitTestCaseClass))) {
            throw new TestProblemException("Class " + junitTestCaseClass + " is not derived from junit.framework.TestCase");
        }
        junit.framework.TestSuite suite;
        try {
            Method suiteMethod = junitTestCaseClass.getMethod("suite", new Class[0]);
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
            throw new TestProblemException("suite method failed on class " + junitTestCaseClass.getName() + " with: " + exception.toString(), exception);
        }
        Vector testsOut = new Vector(suite.countTestCases());
        Enumeration tests = suite.tests();
        while (tests.hasMoreElements()) {
            junit.framework.TestCase testCaseToAdd = (junit.framework.TestCase)tests.nextElement();
            testsOut.addElement(new JUnitTestCase(testCaseToAdd));
        }
        return testsOut;
    }
}
