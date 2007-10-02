/*******************************************************************************
 * Copyright (c) 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - purpose: extended JUnit4 testing for Oracle TopLink
 ******************************************************************************/

package junit.extensions.pb4;

// javase imports
import java.lang.reflect.*;
import java.util.*;

// JUnit imports
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.internal.runners.TestIntrospector;

public class PB4BeforeAndAfterRunner {

    @SuppressWarnings("serial")
    private static class FailedBefore extends Exception {
    }

    public PB4BeforeAndAfterRunner(Class<?> testClass, Object test) {
        super();
        setTestIntrospector(new TestIntrospector(testClass));
        setTest(test);
    }

    protected Properties properties = null;
    protected Object test;
    protected TestIntrospector testIntrospector;

    public PB4BeforeAndAfterRunner setProperties(Properties properties) {
        this.properties = properties;
        return this;
    }

    public Properties getProperties() {
        return properties;
    }

    protected Object getTest() {
        return test;
    }

    protected void setTest(Object test) {
        this.test = test;
    }

    protected TestIntrospector getTestIntrospector() {
        return testIntrospector;
    }

    protected void setTestIntrospector(TestIntrospector testIntrospector) {
        this.testIntrospector = testIntrospector;
    }

    protected void runUnprotected() {
    }

    protected void addFailure(Throwable targetException) {
    }

    protected void invokeMethod(Method method) throws Exception {
        if (method.getParameterTypes().length > 0) {
            method.invoke(getTest(), properties);
        } else {
            method.invoke(getTest());
        }
    }

    public void runProtected() {
        try {
            runBefores();
            runUnprotected();
        }
        catch (FailedBefore e) {
        } 
        finally {
        	runAfters();
        }
    }

    private void runBefores() throws FailedBefore {
        try {
            List<Method> befores = getTestIntrospector().getTestMethods(BeforeClass.class);
            for (Method before : befores)
                invokeMethod(before);
        } catch (InvocationTargetException e) {
            addFailure(e.getTargetException());
            throw new FailedBefore();
        } catch (Throwable e) {
            addFailure(e);
            throw new FailedBefore();
        }
    }

    private void runAfters() {
        List<Method> afters = getTestIntrospector().getTestMethods(AfterClass.class);
        for (Method after : afters)
            try {
                invokeMethod(after);
            } catch (InvocationTargetException e) {
                addFailure(e.getTargetException());
            } catch (Throwable e) {
                addFailure(e);
            }
    }

}
