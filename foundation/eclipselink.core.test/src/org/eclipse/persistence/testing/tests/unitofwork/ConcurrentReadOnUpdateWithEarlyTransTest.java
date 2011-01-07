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
package org.eclipse.persistence.testing.tests.unitofwork;

import java.math.BigDecimal;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestWarningException;


public class ConcurrentReadOnUpdateWithEarlyTransTest extends TestCase {
    // used as monitor for test
    public static ConcurrentAddress readAddress;
    public ConcurrentAddress originalReference;
    public ConcurrentAddress readObject;

    //used in remote unit of work tests to control when the lock is released
    public static int whosWaiting;
    public static final int READER = 2;
    public static final int WRITER = 1;
    public static ThreadLocal threadId = new ThreadLocal();

    public ConcurrentReadOnUpdateWithEarlyTransTest() {
    }

    public void setup() {
        // The purpose of this test is to test multi-threaded server execution,
        // remote is for a single client, so this test is not relevant.
        if (getSession().isDistributedSession()) {
            throw new TestWarningException("Test unavailable on Remote UnitOfWork.");
        }
        readAddress = new ConcurrentAddress();
        readAddress.setId(new BigDecimal(8));
        readAddress.setCity("Toronto");
        readAddress.setPostalCode("L5J2B5");
        readAddress.setProvince("ONT");
        readAddress.setStreet("1450 Acme Cr., suite 4");
        readAddress.setCountry("Canada");
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.registerObject(readAddress);
        uow.commit();
        ConcurrentAddress.RUNNING_TEST = ConcurrentAddress.READ_ON_UPDATE_EARLY_TRANS;
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        ConcurrentPerson person = (ConcurrentPerson)uow.readObject(ConcurrentPerson.class);
        originalReference = person.getAddress();
        uow.beginEarlyTransaction();
        ConcurrentAddress addressClone = 
            (ConcurrentAddress)uow.readObject(ConcurrentReadOnUpdateWithEarlyTransTest.readAddress);
        person.setAddress(addressClone);
        //begin multi threading
        Thread loadThread = new Thread(this.runnable());
        loadThread.start();
        threadId.set("Writer");
        try {
            //let the reader start
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }
        uow.commit();
        try {
            loadThread.join();
        } catch (InterruptedException ex) {
        }
    }

    public Runnable runnable() {
        return new Runnable() {
                public void run() {
                    threadId.set("Reader");
                    synchronized (ConcurrentReadOnUpdateWithEarlyTransTest.readAddress) {
                        ConcurrentReadOnUpdateWithEarlyTransTest.whosWaiting = 
                                ConcurrentReadOnUpdateWithEarlyTransTest.READER;
                        try {
                            ConcurrentReadOnUpdateWithEarlyTransTest.readAddress.wait(30000);
                        } catch (InterruptedException ex) {
                        }
                    }
                    readObject = 
                            (ConcurrentAddress)getSession().readObject(ConcurrentReadOnUpdateWithEarlyTransTest.readAddress);
                    synchronized (ConcurrentReadOnUpdateWithEarlyTransTest.readAddress) {
                        ConcurrentReadOnUpdateWithEarlyTransTest.readAddress.notifyAll(); //wake writer
                    }
                }
            };
    }

    public void verify() {
        ConcurrentPerson person = (ConcurrentPerson)getSession().readObject(ConcurrentPerson.class);
        if (person.getAddress() != readObject) {
            throw new TestErrorException("Object Identity Lost during merge");
        }
    }

    public void reset() {
        ConcurrentAddress.RUNNING_TEST = ConcurrentAddress.NONE;
        try {
            UnitOfWork unitOfWork = getSession().acquireUnitOfWork();
            ((ConcurrentPerson)unitOfWork.readObject(ConcurrentPerson.class)).setAddress((ConcurrentAddress)unitOfWork.registerObject(originalReference));
            unitOfWork.deleteObject(ConcurrentReadOnUpdateWithEarlyTransTest.readAddress);
            unitOfWork.commit();
        } finally {
            ConcurrentReadOnInsertTest.readAddress = null;
        }
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
}
