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

import java.lang.reflect.*;

/**
 * <b>Purpose:</b> Allow for very simple Unit tests.
 *
 * The drawback with the current test framework is that because each test case
 * corresponds 1-1 to a java class, in the worst case there will be one java
 * class for each test case.
 * <p>
 * This can be alleviated by writing generic test cases, (i.e. that take many
 * parameters), or in the worst case testing many things with a single test
 * case / java class.  The latter is messy and the former works well with
 * functional tests (where you can execute the same code but in different
 * ways), but is fairly useless when it comes to Unit testing.  How for example
 * can one write a generic test case for testing getters and setters?
 * <p>
 * A unit test can be added to any TestSuite, and works well when setup/reset is
 * extremely simple or not required, and the test method itself is only a couple
 * of lines long.  In this case the entire test itself is just a method in
 * that TestSuite, named _test{TestName}.
 * <p>
 * <b>Example:</b> To add a test called CreateTest, to test Expression.create(),
 * define a method called _testCreateTest in ExpressionUnitTestSuite,
 * and in addTests call:
 *     addTest(new UnitTestCase("CreateTest"));
 * When the test is executed ExpressionUnitTestSuite setup() and reset()
 * will be called, along with _testCreateTest().
 *
 * @since 9.0.4
 * @author Stephen McRitchie
 */
public class UnitTestCase extends AutoVerifyTestCase {
    public UnitTestCase(String name) {
        setName(name);
    }

    public void reset() {
        ((TestSuite)getContainer()).reset();
    }

    public void setup() {
        ((TestSuite)getContainer()).setup();
    }

    public void test() {
        String methodName = "_test" + getName();
        try {
            Class[] args = {  };
            java.lang.reflect.Method method = getContainer().getClass().getMethod(methodName, args);
            Object[] objectList = {  };
            method.invoke(getContainer(), objectList);
        } catch (NoSuchMethodException nsme) {
            throw new TestProblemException("Unit test could not be found", nsme);
        } catch (IllegalAccessException iae) {
            throw new TestProblemException("Unit test could not be found", iae);
        } catch (InvocationTargetException ite) {
            if (ite.getTargetException() instanceof TestException) {
                throw (TestException)ite.getTargetException();
            } else {
                throw new TestErrorException("Unit test failed due to exception.", ite.getTargetException());
            }
        }
    }

    /**
     * Unit tests are so simple that they only require a test method.
     */
    public void verify() {
        // Do nothing.
    }
}
