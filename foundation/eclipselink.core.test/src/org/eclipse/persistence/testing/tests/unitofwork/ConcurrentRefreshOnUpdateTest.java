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
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.Cursor;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


public class ConcurrentRefreshOnUpdateTest extends AutoVerifyTestCase {
    // used as monitor for test
    public static Employee lock;

    public static boolean writerWaiting = false;
    public static boolean readerWaiting = false;
    // stores the version of the object
    public static int version;

    public DescriptorEventAdapter adaptor;

    //used in remote unit of work tests to control when the lock is released
    public static int depth;

    public boolean writer;

    //session for the threads
    public Session session;

    public ConcurrentRefreshOnUpdateTest() {
        adaptor = new ConcurrentRefreshEventAdapter();
    }

    public ConcurrentRefreshOnUpdateTest(boolean writer, Session session) {
        this.writer = writer;
        this.session = session;
    }

    public void setup() {
        if (getSession().isDistributedSession()) {
            throw new TestWarningException("Test unavailable on Remote UnitOfWork because of timing issues");
        }
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        getSession().getDescriptor(Employee.class).getEventManager().addListener(this.adaptor);
    }

    public void test() {
        ConcurrentRefreshOnUpdateTest.lock = (Employee)getSession().readObject(Employee.class);
        Object primaryKey = ConcurrentRefreshOnUpdateTest.lock.getId();

        ConcurrentRefreshOnUpdateTest.version = 
                ((Number)getSession().getDescriptor(Employee.class).getOptimisticLockingPolicy().getWriteLockValue(ConcurrentRefreshOnUpdateTest.lock, 
                                                                                                                   primaryKey, 
                                                                                                                   (AbstractSession)getSession())).intValue();
        String oldName = ConcurrentRefreshOnUpdateTest.lock.getFirstName();


        Thread thread1 = null;
        Thread thread2 = null;
        try {
            thread1 = new Thread(new ConcurrentRefreshOnUpdateTest(true, getSession()).runnable());
            //start the thread and wait for the thread to get under way before continueing.
            thread1.start();
            thread2 = new Thread(new ConcurrentRefreshOnUpdateTest(false, getSession()).runnable());
            //start the thread and wait for the thread to get under way before continueing.
            thread2.start();
        } catch (Exception ex) {
            //just ignore
        }
        try {
            thread1.join();
            thread2.join();
        } catch (Exception ex) {
            //just an interrupt ignore
        }
        if (ConcurrentRefreshOnUpdateTest.version != 
            ((Number)getSession().getDescriptor(Employee.class).getOptimisticLockingPolicy().getWriteLockValue(ConcurrentRefreshOnUpdateTest.lock, 
                                                                                                               primaryKey, 
                                                                                                               (AbstractSession)getSession())).intValue()) {
            if (ConcurrentRefreshOnUpdateTest.lock.getFirstName().equals(oldName)) {
                throw new TestErrorException("The refresh over wrote new data but left the new version");
            }
        }

    }

    public Runnable runnable() {
        return new Runnable() {
                public void run() {
                    if (writer) {
                        UnitOfWork uow = session.acquireUnitOfWork();
                        Employee empClone = (Employee)uow.registerObject(ConcurrentRefreshOnUpdateTest.lock);
                        empClone.setFirstName("The New City Name" + System.currentTimeMillis());
                        synchronized (ConcurrentRefreshOnUpdateTest.lock) {
                            if (ConcurrentRefreshOnUpdateTest.readerWaiting) {
                                ConcurrentRefreshOnUpdateTest.lock.notifyAll();
                            } else {
                                ConcurrentRefreshOnUpdateTest.writerWaiting = true;
                                try {
                                    ConcurrentRefreshOnUpdateTest.lock.wait(30000);
                                } catch (InterruptedException ex) {
                                    //ignore
                                }
                                ConcurrentRefreshOnUpdateTest.writerWaiting = false;
                            }
                        }
                        uow.commit();
                    } else {
                        ReadAllQuery query = new ReadAllQuery(Employee.class);
                        query.setShouldRefreshIdentityMapResult(true);
                        query.useCursoredStream(0, 1);
                        query.setSelectionCriteria(new ExpressionBuilder().get("id").equal(ConcurrentRefreshOnUpdateTest.lock.getId()));
                        Cursor cursor = (Cursor)session.executeQuery(query);

                        //wait for both thread to set up
                        synchronized (ConcurrentRefreshOnUpdateTest.lock) {
                            if (ConcurrentRefreshOnUpdateTest.writerWaiting) {
                                ConcurrentRefreshOnUpdateTest.lock.notifyAll();
                            } else {
                                ConcurrentRefreshOnUpdateTest.readerWaiting = true;
                                try {
                                    ConcurrentRefreshOnUpdateTest.lock.wait(30000);
                                } catch (InterruptedException ex) {
                                    //ignore
                                }
                                ConcurrentRefreshOnUpdateTest.readerWaiting = false;
                            }
                        }
                        Thread.yield();
                        // writer is commiting, wait for postMerge event
                        synchronized (ConcurrentRefreshOnUpdateTest.lock) {
                            if (!ConcurrentRefreshOnUpdateTest.writerWaiting) {
                                ConcurrentRefreshOnUpdateTest.readerWaiting = true;
                                try {
                                    ConcurrentRefreshOnUpdateTest.lock.wait(30000);
                                } catch (InterruptedException ex) {

                                } //ignore
                                ConcurrentRefreshOnUpdateTest.readerWaiting = false;
                            }
                        }
                        if (cursor.hasMoreElements()) {
                            cursor.nextElement();
                        }
                        cursor.close();
                    }
                }
            };
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        getSession().getDescriptor(Employee.class).getEventManager().removeListener(this.adaptor);
    }
}
