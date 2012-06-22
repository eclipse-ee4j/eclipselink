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
package org.eclipse.persistence.testing.tests.transparentindirection;

import java.util.*;
import java.lang.reflect.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * provide support for zunit stuff...
 */
public class ZTestCase extends AutoVerifyTestCase {
    private final String zName;

    /**
     * Construct a test with the specified name.
     */
    public ZTestCase(String zName) {
        super();
        this.zName = zName;
        this.setName(this.getName() + "." + zName + "()");
        this.initialize();
    }

    /**
     * Assert that the elements in two vectors are equal. If they are not,
     * throw an AssertionFailedError. Order of the elements is significant.
     * @param message the error message
     * @param expected the expected value of an vector
     * @param actual the actual value of an vector
     */
    protected void assertElementsEqual(String message, Vector expected, Vector actual) {
        if (expected == actual) {
            return;
        }
        if (expected.size() != actual.size()) {
            this.assertTrue(this.notEqualsMessage(message, expected, actual), false);
        }
        for (int i = 0; i < expected.size(); i++) {
            Object e1 = expected.elementAt(i);
            Object e2 = actual.elementAt(i);
            if (e1 == null) {// avoid null pointer exception
                if (e2 != null) {
                    this.assertTrue(this.notEqualsMessage(message, expected, actual), false);
                }
            } else {
                if (!e1.equals(e2)) {
                    this.assertTrue(this.notEqualsMessage(message, expected, actual), false);
                }
            }
        }
    }

    /**
     * Assert that the elements in two vectors are equal. If they are not,
     * throw an AssertionFailedError. Order of the elements is significant.
     * @param expected the expected value of an vector
     * @param actual the actual value of an vector
     */
    protected void assertElementsEqual(Vector expected, Vector actual) {
        this.assertElementsEqual("", expected, actual);
    }

    /**
     * Assert that the elements in two vectors are equal. If they are not,
     * throw an AssertionFailedError. The order of the elements is ignored.
     * @param message the error message
     * @param expected the expected value of an vector
     * @param actual the actual value of an vector
     */
    protected void assertUnorderedElementsEqual(String message, Vector expected, Vector actual) {
        if (expected == actual) {
            return;
        }
        if (expected.size() != actual.size()) {
            this.assertTrue(this.notEqualsMessage(message, expected, actual), false);
        }
        Vector temp = (Vector)actual.clone();
        for (int i = 0; i < expected.size(); i++) {
            Object e1 = expected.elementAt(i);
            if (e1 == null) {// avoid null pointer exception
                if (!this.removeNullElement(temp)) {
                    this.assertTrue(this.notEqualsMessage(message, expected, actual), false);
                }
            } else {
                if (!temp.removeElement(e1)) {
                    this.assertTrue(this.notEqualsMessage(message, expected, actual), false);
                }
            }
        }
    }

    /**
     * Assert that the elements in two vectors are equal. If they are not,
     * throw an AssertionFailedError. The order of the elements is ignored.
     * @param expected the expected value of an vector
     * @param actual the actual value of an vector
     */
    protected void assertUnorderedElementsEqual(Vector expected, Vector actual) {
        this.assertUnorderedElementsEqual("", expected, actual);
    }

    /**
     * Return the zunit name of the test.
     */
    public String getZName() {
        return zName;
    }

    /**
     * Initialize the instance. Useful for subclasses.
     */
    protected void initialize() {
    }

    /**
     * invoke the test method
     */
    protected void invokeTest() throws Throwable {
        Method method = this.methodNamed(this.getZName());
        try {
            method.invoke(this, new Object[0]);
        } catch (IllegalAccessException iae) {
            throw new RuntimeException("The method '" + method + "' (and its class) must be public.");
        } catch (InvocationTargetException ite) {
            ite.fillInStackTrace();
            throw ite.getTargetException();
        }
    }

    /**
     * Return the zero-argument method with the specified name.
     */
    private Method methodNamed(String name) {
        try {
            return this.getClass().getMethod(zName, new Class[0]);
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException("Method named " + name + " not found.");
        }
    }

    /**
     * Return a nicely formatted message for when the actual value did not
     * equal the expected one.
     */
    protected String notEqualsMessage(String message, Object expected, Object actual) {
        StringBuffer buffer = new StringBuffer(250);
        if ((message != null) && (message.length() != 0)) {
            buffer.append(message);
            buffer.append(" ");
        }
        buffer.append("expected: \"");
        buffer.append(expected);
        buffer.append("\" but was: \"");
        buffer.append(actual);
        buffer.append("\"");
        return buffer.toString();
    }

    /**
     * Remove the first null element found in the specified vector.
     * Return true if a null element was found and removed.
     * Return false if a null element was not found.
     */
    private boolean removeNullElement(Vector v) {
        for (int i = 0; i < v.size(); i++) {
            if (v.elementAt(i) == null) {
                v.removeElementAt(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Override the TestCase method to call the zunit method.
     */
    public void reset() {
        this.tearDown();
    }

    /**
     * Override the TestCase method to call the zunit method.
     */
    protected void setup() {
        this.setUp();
    }

    /**
     * Set up the fixture (e.g. open a network connection).
     * This method is called before a test is executed.
     */
    protected void setUp() {
    }

    /**
     * Tear down the fixture (e.g. close a network connection).
     * This method is called after a test is executed.
     */
    protected void tearDown() {
    }

    /**
     * Reflectively invoke the test method.
     */
    protected void test() throws Exception {
        try {
            this.invokeTest();
        } catch (Throwable ex) {
            throw new TestErrorException("Error in test Case: " + this.getName(), ex);
        }
    }
}
