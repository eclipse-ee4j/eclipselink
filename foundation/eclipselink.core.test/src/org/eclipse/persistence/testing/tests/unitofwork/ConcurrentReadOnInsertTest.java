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
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestWarningException;


public class ConcurrentReadOnInsertTest extends AutoVerifyTestCase {
    // used as monitor for test
    public static ConcurrentAddress readAddress;
    protected ConcurrentAddress addressClone;

    //used in remote unit of work tests to control when the lock is released
    public static int depth;

    public ConcurrentReadOnInsertTest() {
    }

    public void setup() {
        // The purpose of this test is to test multi-threaded server execution,
        // remote is for a single client, so this test is not relevant.
        if (getSession().isDistributedSession()) {
            throw new TestWarningException("Test unavailable on Remote UnitOfWork.");
        }
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test() {
        ConcurrentAddress address = new ConcurrentAddress();
        address.setCity("Toronto");
        address.setPostalCode("L5J2B5");
        address.setProvince("ONT");
        address.setStreet("1450 Acme Cr., suite 4");
        address.setCountry("Canada");
        Thread thread = new Thread(this.runnable());
        UnitOfWork uow = getSession().acquireUnitOfWork();
        addressClone = (ConcurrentAddress)uow.registerObject(address);
        //used by thread as a monitor and process control
        ConcurrentReadOnInsertTest.readAddress = new ConcurrentAddress();
        try {
            synchronized (ConcurrentReadOnInsertTest.readAddress) {
                ConcurrentReadOnInsertTest.depth = 1;

                //start the thread and wait for the thread to get under way before continueing.
                thread.start();
                ConcurrentReadOnInsertTest.readAddress.wait();
            }
        } catch (Exception ex) {
            //just ignore
        }
        uow.commit();
        try {
            thread.join();
        } catch (Exception ex) {
            //just an inturrupt ignore
        }
        if (address != ConcurrentReadOnInsertTest.readAddress) {
            throw new TestErrorException(" The object returned from the Cache and the object merged into the cache are of different identity. see bug 2632705");
        }
    }

    public Runnable runnable() {
        return new Runnable() {
                public void run() {
                    try {
                        synchronized (readAddress) {
                            //wake up the unit of work
                            readAddress.notifyAll();
                            readAddress.wait();
                        }
                    } catch (Exception ex) {
                        //just an inturrupt ignore
                    }
                    readAddress = (ConcurrentAddress)getSession().readObject(addressClone);
                }
            };
    }

    public void reset() {
        try {
            UnitOfWork unitOfWork = getSession().acquireUnitOfWork();
            unitOfWork.deleteObject(ConcurrentReadOnInsertTest.readAddress);
            unitOfWork.commit();
        } finally {
            ConcurrentReadOnInsertTest.readAddress = null;
        }
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
}
