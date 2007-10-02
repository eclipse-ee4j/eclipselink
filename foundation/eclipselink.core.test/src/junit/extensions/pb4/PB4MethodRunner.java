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

// JUnit imports
import org.junit.internal.runners.TestMethodRunner;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

public class PB4MethodRunner extends TestMethodRunner {

    protected Description description;
    protected RunNotifier notifier;

    public PB4MethodRunner(Object test, Method method, RunNotifier notifier, Description description) {
        super(test, method, notifier, description);
        setDescription(description);
        setNotifier(notifier);
    }

    @Override
    protected void executeMethodBody() throws IllegalAccessException, InvocationTargetException {
        try {
            super.executeMethodBody();
        } catch (InvocationTargetException e) {
            Throwable actual = e.getTargetException();
            if (actual instanceof IgnoreException) {
                Description d = getDescription();
                d.addChild(Description.createSuiteDescription(actual.getMessage()));
                RunNotifier notifier = getNotifier();
                if (notifier instanceof PB4RunNotifier) {
                    ((PB4RunNotifier)notifier).fireTestIgnoredAtRuntime(new PB4Description(d));
                }
                else {
                    notifier.fireTestIgnored(description);
                }
            } else {
                throw e;
            }
        }
    }

    protected Description getDescription() {
        return description;
    }

    protected void setDescription(Description description) {
        this.description = description;
    }

    protected RunNotifier getNotifier() {
        return notifier;
    }

    protected void setNotifier(RunNotifier notifier) {
        this.notifier = notifier;
    }

}
