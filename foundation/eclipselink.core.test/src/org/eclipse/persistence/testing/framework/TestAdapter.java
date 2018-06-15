/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.framework;


/**
 * <b>Purpose</b>:Wraps any existing test, allowing for tests within tests within ...
 * <p>
 * <b>Responsibilities</b>:Wraps an existing test, and executes that test
 * within its own test() method.
 * <p>Ideal for running any test under a different session configuration.
 * <p>If the wrapped test fails, the error message will take the context it was
 * running under into consideration.
 * <p>If the wrapping test fails, the wrapped test will never get executed,
 * meaning a particular instance can be executed once with no wrapped test to
 * test a particular setup, and again to test behavior under a particular setup.
 *
 * @author Stephen McRitchie
 * @since 10 to test flashback feature under different session configurations.
 */
public class TestAdapter extends TestCase {
    public TestCase wrappedTest;
    protected boolean wrappedTestPassed = true;

    public TestAdapter(TestCase wrappedTest) {
        super();
        this.wrappedTest = wrappedTest;
        setName(super.getName() + ":" + wrappedTest.getName());
        setContainer(wrappedTest.getContainer());
        //setDescription("Using TimeAwareSession: " + wrappedTest.getDescription());
    }

    protected void setup() throws Throwable {
        super.setup();
    }

    public void reset() throws Throwable {
        super.reset();
    }

    protected void test() throws Throwable {
        try {
            if (wrappedTest != null) {
                wrappedTest.execute(getExecutor());
            }
        } catch (Throwable e) {
            setTestResult(wrappedTest.getTestResult());
            wrappedTest.getTestResult().setName(getName());
            // This is needed to avoid a NPE on the indentation string.
            wrappedTest.getTestResult().testCase = this;
            throw e;
        }
    }

    protected void verify() {
    }
}
