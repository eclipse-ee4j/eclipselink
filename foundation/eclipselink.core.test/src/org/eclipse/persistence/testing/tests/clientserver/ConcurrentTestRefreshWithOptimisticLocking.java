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
package org.eclipse.persistence.testing.tests.clientserver;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;

@SuppressWarnings("deprecation")
public class ConcurrentTestRefreshWithOptimisticLocking extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    public static int numThreads = 6;
    public boolean isCheckerThread;
    public static Server server;
    public static boolean execute = true;
    public static Thread[] threadList = new Thread[numThreads];
    public static long[] timeList = new long[numThreads];
    public static long runTime;
    public int index;
    public boolean oldVersionRefresh;

    public ConcurrentTestRefreshWithOptimisticLocking(long runtime) {
        setDescription("Test Simulates a highly concurrent situation with refreshing and optimistic locks");
        ConcurrentTestRefreshWithOptimisticLocking.runTime = runtime;
    }

    protected ConcurrentTestRefreshWithOptimisticLocking(boolean checkerThread, int index) {
        this.isCheckerThread = checkerThread;
        this.index = index;
    }

    public void reset() {
        ConcurrentTestRefreshWithOptimisticLocking.execute = false;
        for (int count = 0; count < numThreads; ++count) {
            try {
                ConcurrentTestRefreshWithOptimisticLocking.threadList[count].join();
            } catch (InterruptedException ex) {
                throw new TestProblemException("Test thread was interrupted.  Test failed to run properly");
            }
        }
        ConcurrentTestRefreshWithOptimisticLocking.server.serverSession.getClassDescriptor(DeadLockEmployee.class).setShouldOnlyRefreshCacheIfNewerVersion(this.oldVersionRefresh);
        ConcurrentTestRefreshWithOptimisticLocking.server.logout();
    }

    public void setup() {
        ConcurrentTestRefreshWithOptimisticLocking.execute = true;
        try {
            getSession().getLog().write("WARNING, some tests may take 3 minutes or more");
            getSession().getLog().flush();
        } catch (java.io.IOException e) {
        }

        try {
            DatabaseLogin login = (DatabaseLogin)getSession().getLogin().clone();
            ConcurrentTestRefreshWithOptimisticLocking.server = new Server(login, numThreads, numThreads + 2);
            ConcurrentTestRefreshWithOptimisticLocking.server.serverSession.setSessionLog(getSession().getSessionLog());
            ConcurrentTestRefreshWithOptimisticLocking.server.login();
            ConcurrentTestRefreshWithOptimisticLocking.server.copyDescriptors(getSession());
            ClassDescriptor empDesc = this.server.serverSession.getClassDescriptor(DeadLockEmployee.class);
            this.oldVersionRefresh = empDesc.shouldOnlyRefreshCacheIfNewerVersion();
            empDesc.onlyRefreshCacheIfNewerVersion();

        } catch (ValidationException ex) {
            this.verify();
        }

        for (int count = 0; count < numThreads; ++count) {
            ConcurrentTestRefreshWithOptimisticLocking.threadList[count] = new Thread(new ConcurrentTestRefreshWithOptimisticLocking(false, count).runnable());
            ConcurrentTestRefreshWithOptimisticLocking.timeList[count] = System.currentTimeMillis();
        }
    }

    public void test() {
        for (int count = 0; count < numThreads; ++count) {
            ConcurrentTestRefreshWithOptimisticLocking.threadList[count].start();
        }

        Thread checker = new Thread(new ConcurrentTestRefreshWithOptimisticLocking(true, -1).runnable());
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
            ConcurrentTestRefreshWithOptimisticLocking.execute = true;
            getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
            throw new TestErrorException("This test caused a deadlock in TopLink.  see bug 33883838");
        }
    }

    public Runnable runnable() {
        return new Runnable() {
                // This section represents the executing threads
                // If the type is set to checker then this set the thread
                // to watch the other threads for deadlock.  If none occurs then
                // the test will time out.
                public void run() {
                    if (org.eclipse.persistence.testing.tests.clientserver.ConcurrentTestRefreshWithOptimisticLocking.this.isCheckerThread) {
                        watchOtherThreads();
                    } else {
                        executeUntilStopped();
                    }
                }
            };
    }

    public void watchOtherThreads() {
        long startTime = System.currentTimeMillis();
        while (((System.currentTimeMillis() - startTime) < (runTime + 30000)) && ConcurrentTestRefreshWithOptimisticLocking.execute) {
            for (int localIdex = 0; localIdex < numThreads; ++localIdex) {
                if ((System.currentTimeMillis() - ConcurrentTestRefreshWithOptimisticLocking.timeList[localIdex]) > 30000) {
                    // System.out.println(" Number: " + localIdex + " time: " + (System.currentTimeMillis() -  this.timeList[localIdex]));
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
        Session session = ConcurrentTestRefreshWithOptimisticLocking.server.serverSession.acquireClientSession();
        DeadLockAddress address = (DeadLockAddress)session.readObject(org.eclipse.persistence.testing.tests.clientserver.DeadLockAddress.class);
        DeadLockEmployee employee = (DeadLockEmployee)session.readObject(DeadLockEmployee.class);
        ReadObjectQuery query;
        if ((this.index % 2) != 0) {
            query = new ReadObjectQuery(address);
            query.refreshIdentityMapResult();
            query.setCascadePolicy(DatabaseQuery.CascadeAllParts);
        } else {
            query = new ReadObjectQuery(employee);
            query.refreshIdentityMapResult();
            query.setCascadePolicy(DatabaseQuery.CascadeAllParts);
        }
        while (ConcurrentTestRefreshWithOptimisticLocking.execute) {
            ConcurrentTestRefreshWithOptimisticLocking.timeList[this.index] = System.currentTimeMillis();
            session.executeQuery(query);
        }
        // System.out.println("BeingShutDown");
    }
}
