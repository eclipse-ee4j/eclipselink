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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

// JUnit imports
import org.junit.Ignore;
import org.junit.Test;
import org.junit.internal.runners.TestIntrospector;
import org.junit.internal.runners.TestMethodRunner;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.manipulation.Sortable;
import org.junit.runner.manipulation.Sorter;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;

public class PB4TestClassMethodsRunner extends Runner implements Filterable, Sortable {

    protected final List<Method> fTestMethods;
    protected final Class<?> fTestClass;

    public PB4TestClassMethodsRunner(Class<?> klass) {
        fTestClass = klass;
        TestIntrospector testIntrospector = new TestIntrospector(klass);
        fTestMethods = testIntrospector.getTestMethods(Test.class);
        fTestMethods.addAll(testIntrospector.getTestMethods(Ignore.class));
    }

    @Override
    public void run(RunNotifier notifier) {
        if (fTestMethods.isEmpty()) {
            testAborted(notifier, getDescription(),
            	new Exception("No runnable methods"));
        }
        for (Method method : fTestMethods) {
            if (notifier instanceof PB4RunNotifier) {
            	PB4RunNotifier pb4RunNotifier = (PB4RunNotifier) notifier;
    			if (pb4RunNotifier.shadowCopyOfPleaseStop) {
    				throw new StoppedByUserException();
    			}
    		}
            invokeTestMethod(method, notifier);
        }
    }

    private void testAborted(RunNotifier notifier, Description description, Throwable cause) {
        notifier.fireTestStarted(description);
        notifier.fireTestFailure(new Failure(description, cause));
        notifier.fireTestFinished(description);
    }

    @Override
    public Description getDescription() {
        Description spec = Description.createSuiteDescription(getName());
        List<Method> testMethods = fTestMethods;
        for (Method method : testMethods)
            spec.addChild(methodDescription(method));
        return spec;
    }

    protected String getName() {
        return getTestClass().getName();
    }

    protected Object createTest() throws Exception {
        return getTestClass().getConstructor().newInstance();
    }

    protected void invokeTestMethod(Method method, RunNotifier notifier) {
        Object test;
        try {
            test = createTest();
        } catch (InvocationTargetException e) {
            testAborted(notifier, methodDescription(method), e.getCause());
            return;
        } catch (Exception e) {
            testAborted(notifier, methodDescription(method), e);
            return;
        }
        createMethodRunner(test, method, notifier).run();
    }

    protected TestMethodRunner createMethodRunner(Object test, Method method, RunNotifier notifier) {
        return new PB4MethodRunner(test, method, notifier, methodDescription(method));
    }

    protected String testName(Method method) {
        return method.getName();
    }

    protected Description methodDescription(Method method) {
        return Description.createTestDescription(getTestClass(), testName(method));
    }

    public void filter(Filter filter) throws NoTestsRemainException {
        for (Iterator<Method> iter = fTestMethods.iterator(); iter.hasNext();) {
            Method method = iter.next();
            if (!filter.shouldRun(methodDescription(method)))
                iter.remove();
        }
        if (fTestMethods.isEmpty())
            throw new NoTestsRemainException();
    }

    public void sort(final Sorter sorter) {
        Collections.sort(fTestMethods, new Comparator<Method>() {
            public int compare(Method o1, Method o2) {
                return sorter.compare(methodDescription(o1), methodDescription(o2));
            }
        });
    }

    protected Class<?> getTestClass() {
        return fTestClass;
    }
}
