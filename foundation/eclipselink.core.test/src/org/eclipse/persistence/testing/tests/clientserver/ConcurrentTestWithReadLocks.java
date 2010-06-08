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
package org.eclipse.persistence.testing.tests.clientserver;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.sessions.DatabaseLogin;

@SuppressWarnings("deprecation")
public class ConcurrentTestWithReadLocks extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    public static int numThreads = 8;
    public boolean isCheckerThread;
    public static Server server;
    public static boolean execute = true;
    public static Thread[] threadList = new Thread[numThreads];
    public static long[] timeList = new long[numThreads];
    public static long runTime;
    public int index;

    public ConcurrentTestWithReadLocks(long runtime) {
        setDescription("Test Simulates a highly concurrent situation with ReadLocks");
        ConcurrentTestWithReadLocks.runTime = runtime;
    }

    protected ConcurrentTestWithReadLocks(boolean checkerThread, int index) {
        this.isCheckerThread = checkerThread;
        this.index = index;
    }

    public void reset() {
        ConcurrentTestWithReadLocks.execute = false;
        for (int count = 0; count < numThreads; ++count) {
            try {
                ConcurrentTestWithReadLocks.threadList[count].join();
            } catch (InterruptedException ex) {
                throw new TestProblemException("Test thread was interrupted.  Test failed to run properly");
            }
        }
        ConcurrentTestWithReadLocks.server.logout();
    }

    public void setup() {
        ConcurrentTestWithReadLocks.execute = true;
        try {
            getSession().getLog().write("WARNING, some tests may take 3 minutes or more");
            getSession().getLog().flush();
        } catch (java.io.IOException e) {
        }

        try {
            DatabaseLogin login = (DatabaseLogin)getSession().getLogin().clone();
            ConcurrentTestWithReadLocks.server = new Server(login, numThreads, numThreads + 2);
            ConcurrentTestWithReadLocks.server.serverSession.setSessionLog(getSession().getSessionLog());
            ConcurrentTestWithReadLocks.server.login();
            ConcurrentTestWithReadLocks.server.copyDescriptors(getSession());
        } catch (ValidationException ex) {
            this.verify();
        }

        for (int count = 0; count < numThreads; ++count) {
            ConcurrentTestWithReadLocks.threadList[count] = new Thread(new ConcurrentTestWithReadLocks(false, count).runnable());
            ConcurrentTestWithReadLocks.timeList[count] = System.currentTimeMillis();
        }
    }

    public void test() {
        for (int count = 0; count < numThreads; ++count) {
            ConcurrentTestWithReadLocks.threadList[count].start();
        }

        Thread checker = new Thread(new ConcurrentTestWithReadLocks(true, -1).runnable());
        checker.start();
        try {
            checker.join();
        } catch (InterruptedException ex) {
            throw new TestProblemException("Test thread was interrupted.  Test failed to run properly");
        }
    }

    public void verify() {
        if (!execute) {
            for (int count = 0; count < numThreads; ++count) {
                threadList[count].stop();
            }
            getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
            throw new TestErrorException("This test caused a deadlock in TopLink.  see bug 3049635");
        }
    }

    public Runnable runnable() {
        return new Runnable() {
                // This section represents the executing threads
                // If the type is set to checker then this set the thread
                // to watch the other threads for deadlock.  If none occurs then
                // the test will time out.
                public void run() {
                    if (org.eclipse.persistence.testing.tests.clientserver.ConcurrentTestWithReadLocks.this.isCheckerThread) {
                        watchOtherThreads();
                    } else {
                        executeUntilStopped();
                    }
                }
            };
    }

    public void watchOtherThreads() {
        long startTime = System.currentTimeMillis();
        while (((System.currentTimeMillis() - startTime) < (runTime + 30000)) && ConcurrentTestWithReadLocks.execute) {
            for (int localIdex = 0; localIdex < numThreads; ++localIdex) {
                if ((System.currentTimeMillis() - ConcurrentTestWithReadLocks.timeList[localIdex]) > 30000) {
                    execute = false;
                    break;
                }
            }
            try {
                Thread.sleep(30000);
            } catch (InterruptedException ex) {
                throw new TestProblemException("Test thread was interrupted.  Test failed to run properly");
            }
        }
    }

    public void executeUntilStopped() {
        Session session = ConcurrentTestWithReadLocks.server.serverSession.acquireClientSession();
        DeadLockEmployee employee = (DeadLockEmployee)session.readObject(DeadLockEmployee.class);

        //    session.release();
        while (ConcurrentTestWithReadLocks.execute) {
            //     session = this.server.serverSession.acquireClientSession();
            ConcurrentTestWithReadLocks.timeList[this.index] = System.currentTimeMillis();

            /* Aquire Unit Of Work */
            UnitOfWork uow = session.acquireUnitOfWork();

            /* Register */
            DeadLockEmployee workingEmp = (DeadLockEmployee)uow.registerObject(employee);

            /* make an Refresh */
            uow.refreshObject(workingEmp);
            uow.commit();
        }
    }
}
