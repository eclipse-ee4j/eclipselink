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
package org.eclipse.persistence.testing.tests.distributedservers.rcm.broadcast;

import org.eclipse.persistence.sessions.Session;

import org.eclipse.persistence.testing.tests.distributedservers.DistributedServer;
import org.eclipse.persistence.testing.tests.distributedservers.rcm.RCMDistributedServersModel;

public abstract class BroadcastDistributedServersModel extends RCMDistributedServersModel {

    // maximum wait time to give a chance to remote command recipient to process it before verification.
    public long timeToWaitBeforeVerify;

    public void addTests() {
        super.addTests();

        // substitute each test with wrapped test - an instance of
        // BroadcastSetupHelper.TestWrapperWithEventLock, which contains
        // the original test as an internal test.
        // The wrapper methods call the respective methods on the internal test,
        // adding a wait after internalTest.test() method is called 
        // (in case there was merge with changes on the source side) 
        // until either the target side merges the sent changes or exception occurs.
        BroadcastSetupHelper.wrapAllTestCases(this, timeToWaitBeforeVerify);

        // Any TestCase which merges changes on the source side
        // and sends remote command to the target will do.
        // Because all the tests are already wrapped, this test is wrapped, too;
        // though the class name check is performed on the wrapped test.
        String testShortClassName = "UpdateToNullTest";
        BroadcastSetupHelper.TestWrapperWithEventLock test = (BroadcastSetupHelper.TestWrapperWithEventLock)BroadcastSetupHelper.getTestCase(this, testShortClassName, true);

        // shouldDestrotyFactory = false; shouldRemoveConnectionOnError = false
        this.addTest(new BroadcastReconnectionTest(test, false, false, getHelper()));
        // shouldDestrotyFactory = false; shouldRemoveConnectionOnError = true
        this.addTest(new BroadcastReconnectionTest(test, false, true, getHelper()));
        // shouldDestrotyFactory = true; shouldRemoveConnectionOnError = false
        this.addTest(new BroadcastReconnectionTest(test, true, false, getHelper()));
        // shouldDestrotyFactory = true; shouldRemoveConnectionOnError = true
        this.addTest(new BroadcastReconnectionTest(test, true, true, getHelper()));
    }


    public void startCacheSynchronization() {
        getHelper().startCacheSynchronization((org.eclipse.persistence.internal.sessions.AbstractSession)getSession(), true);
    }

    public void stopCacheSynchronization() {
        getHelper().stopCacheSynchronization((org.eclipse.persistence.internal.sessions.AbstractSession)getSession());
    }

    /**
     * Overridden by sbuclasses.
     */
    public abstract DistributedServer createDistributedServer(Session session);

    protected abstract BroadcastSetupHelper getHelper();
}
