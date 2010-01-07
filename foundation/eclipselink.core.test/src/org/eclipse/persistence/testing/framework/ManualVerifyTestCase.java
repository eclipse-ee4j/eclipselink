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
package org.eclipse.persistence.testing.framework;

import org.eclipse.persistence.logging.SessionLog;

/**
 * <p>Purpose<b></b>:All the test cases are subclassed from this class. Each test case tests single
 * feature of TopLink. Ideally a test case consists of five steps.
 * Setup: Performs all the initial setup that is required by the test, such as setting
 *              up database to some state on which test would run.
 * Test: The actual test to be performed, such as writing an object.
 * Verify: Verify the test if it was performed well or not.
 * Reset: Reset the database to the state from where the test started
 * Reset Verify: Check if reset performed well or not.
 */
public abstract class ManualVerifyTestCase extends TestCase {

    /**
     * Executes this test case.
     */
    public void execute(TestExecutor executor) {
        setTestResult(new TestResult(this, "You decide"));
        setExecutor(executor);
        boolean handleErrors = getExecutor().shouldHandleErrors();
        int logLevel = getSession().getLogLevel();

        try {
            setup();

            getSession().setLogLevel(SessionLog.FINEST);
            getExecutor().doNotHandleErrors();

            test();

            getSession().setLogLevel(logLevel);

            reset();
            resetVerify();
        } catch (Throwable runtimeException) {
            TestErrorException EclipseLinkException = new TestErrorException("Error occurred.", runtimeException);
            setTestException(EclipseLinkException);
            throw EclipseLinkException;
        }
        getExecutor().setShouldHandleErrors(handleErrors);
    }
}
