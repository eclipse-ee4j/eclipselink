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

import java.util.Vector;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestWarningException;


public class ConcurrentRefreshOnCloneTest extends AutoVerifyTestCase {
    // used as monitor for test
    public static ConcurrentAddress lock;

    //stores the cloned copy of the lock object
    public static ConcurrentAddress clone;

    //stores a copy of the original values
    public static ConcurrentAddress original;
    public static boolean cloneWaiting = false;
    public static boolean readerWaiting = false;

    //attribute stores if the thread has waiting to prevent the back clone from entering a wait state
    public static boolean waited = false;

    //used in remote unit of work tests to control when the lock is released
    public static int depth;
    public boolean toClone;

    //session for the threads
    public static Session session;

    //stores the original cache isolation level for this session
    int originalIsolationLevel;

    public ConcurrentRefreshOnCloneTest() {
    }

    public ConcurrentRefreshOnCloneTest(boolean toClone) {
        this.toClone = toClone;
    }

    public void setup() {
        if (getSession().isDistributedSession()) {
            throw new TestWarningException("Test unavailable on Remote UnitOfWork because of timing issues");
        }
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        ConcurrentRefreshOnCloneTest.session = getSession();
        ConcurrentRefreshOnCloneTest.waited = false;
        ConcurrentAddress newAddress = new ConcurrentAddress();
        newAddress.id = new BigDecimal(15);
        newAddress.city = "Meductic";
        newAddress.country = "Canada";
        newAddress.postalCode = "C1C 1C1";
        newAddress.province = "New Brunswick";
        newAddress.street = "St. Jermone";
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.registerObject(newAddress);
        uow.commit();
        ConcurrentRefreshOnCloneTest.lock = newAddress;

        this.originalIsolationLevel = getSession().getLogin().getCacheTransactionIsolation();

        getSession().getLogin().setCacheTransactionIsolation(org.eclipse.persistence.sessions.DatasourceLogin.SYNCHRONIZED_READ_ON_WRITE);
    }

    public void test() {
        ConcurrentRefreshOnCloneTest.lock.city = "Some Large City";
        ConcurrentRefreshOnCloneTest.lock.country = "Some Small Country";
        ConcurrentRefreshOnCloneTest.lock.postalCode = "A1A 1A1";
        ConcurrentRefreshOnCloneTest.lock.province = "No Nation";
        ConcurrentRefreshOnCloneTest.lock.street = "No Name";

        ConcurrentRefreshOnCloneTest.original = (ConcurrentAddress)ConcurrentRefreshOnCloneTest.lock.clone();
        Vector primaryKey = new Vector(1);
        primaryKey.add(ConcurrentRefreshOnCloneTest.lock.getId());

        Thread thread1 = null;
        Thread thread2 = null;
        try {
            thread1 = new Thread(new ConcurrentRefreshOnCloneTest(true).runnable());
            //start the thread and wait for the thread to get under way before continueing.
            thread1.start();
            thread2 = new Thread(new ConcurrentRefreshOnCloneTest(false).runnable());
            //start the thread and wait for the thread to get under way before continueing.
            thread2.start();
        } catch (Exception ex) {
            System.out.println("Exception" + ex.toString());
            //just ignore
        }
        try {
            thread1.join();
            thread2.join();
        } catch (Exception ex) {
            //just an inturrupt ignore
        }
        if (getAbstractSession().compareObjectsDontMatch(ConcurrentRefreshOnCloneTest.lock, 
                                                         ConcurrentRefreshOnCloneTest.clone)) {
            if (getAbstractSession().compareObjectsDontMatch(ConcurrentRefreshOnCloneTest.original, 
                                                             ConcurrentRefreshOnCloneTest.clone)) {
                throw new TestErrorException("The test failed to clone the object without data corruption");
            }
        }
    }

    public Runnable runnable() {
        return new Runnable() {
                public void run() {
                    if (ConcurrentRefreshOnCloneTest.this.toClone) {
                        UnitOfWork uow = ConcurrentRefreshOnCloneTest.session.acquireUnitOfWork();
                        ConcurrentRefreshOnCloneTest.clone = 
                                (ConcurrentAddress)uow.registerObject(ConcurrentRefreshOnCloneTest.lock);
                    } else {
                        try {
                            //sleep here to let the register catch up,
                            //can not wait here as the cloning thread may never wait if a read lock is acquired
                            Thread.sleep(5000);
                        } catch (Exception ex) {
                        }
                        ReadObjectQuery query = new ReadObjectQuery(ConcurrentAddress.class);
                        query.setShouldRefreshIdentityMapResult(true);
                        query.setSelectionCriteria(new ExpressionBuilder().get("id").equal(ConcurrentRefreshOnCloneTest.lock.getId()));
                        ConcurrentRefreshOnCloneTest.session.executeQuery(query);
                        try {
                            if (ConcurrentRefreshOnCloneTest.lock != null) {
                                synchronized (ConcurrentRefreshOnCloneTest.lock) {
                                    ConcurrentRefreshOnCloneTest.lock.notifyAll();
                                }
                            }
                        } catch (Exception ex) {
                        }
                    }
                }
            };
    }

    public void reset() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.deleteObject(ConcurrentRefreshOnCloneTest.original);
        ConcurrentRefreshOnCloneTest.lock = null;
        uow.commit();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        getSession().getLogin().setCacheTransactionIsolation(this.originalIsolationLevel);
    }
}
