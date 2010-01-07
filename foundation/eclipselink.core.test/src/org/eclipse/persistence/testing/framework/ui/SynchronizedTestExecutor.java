/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.framework.ui;

import org.eclipse.persistence.testing.framework.*;

public class SynchronizedTestExecutor extends Thread {
    protected junit.framework.Test test;
    protected TestExecutor executor;
    protected SynchronizedTester tester;
    protected boolean shouldRunSetupOnly;

    public SynchronizedTestExecutor(TestExecutor executor, junit.framework.Test test, SynchronizedTester tool) {
        this.executor = executor;
        this.test = test;
        this.tester = tool;
        this.shouldRunSetupOnly = false;
    }

    /**
     * Return the executor.
     */
    public TestExecutor getExecutor() {
        return executor;
    }

    /**
     * Return the testEntity.
     */
    public junit.framework.Test getTest() {
        return test;
    }

    public SynchronizedTester getTester() {
        return tester;
    }

    /**
     * PUBLIC:
     * The class implements Runnable interface thats why run method must be implemented.
     */
    public void run() {
        if (getTest() == null) {
            throw new TestErrorException("There is now test entity to run.");
        }

        try {
            if (shouldRunSetupOnly()) {
                ((TestCollection)getTest()).setupEntity();
            } else {
                getExecutor().runTest(getTest());
            }
        } catch (Throwable exception) {
            getTester().notifyException(exception);
        } finally {
            getTester().finishedTest();
        }
    }

    /**
     * Set the executor.
     */
    protected void setExecutor(TestExecutor executor) {
        this.executor = executor;
    }

    public void setShouldRunSetupOnly(boolean shouldRunSetupOnly) {
        this.shouldRunSetupOnly = shouldRunSetupOnly;
    }

    /**
     * set the testEntity.
     */
    public void setTest(junit.framework.Test test) {
        this.test = test;
    }

    protected void setTester(SynchronizedTester tester) {
        this.tester = tester;
    }

    public boolean shouldRunSetupOnly() {
        return shouldRunSetupOnly;
    }

    /**
     * Stop the execution of the currently executing test
     */
    public void stopExecution() {
        getExecutor().stopExecution();
    }
}
