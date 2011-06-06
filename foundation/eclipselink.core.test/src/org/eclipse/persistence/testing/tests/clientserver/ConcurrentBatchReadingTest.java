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

import java.util.*;

import org.eclipse.persistence.sessions.server.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

@SuppressWarnings("deprecation")
public class ConcurrentBatchReadingTest extends TestCase {

    public static int numThreads = 8;

    public boolean isCheckerThread;
    public static Server server;
    public static boolean execute = true;
    public static Thread[] threadList = new Thread[numThreads];
    public static long[] timeList = new long[numThreads];
    public static long runTime;
    public int index;

    public ConcurrentBatchReadingTest(long runtime) {
        setDescription("Test Simulates a highly concurrent situation with ReadLocks");
        runTime = runtime;
    }

    protected ConcurrentBatchReadingTest(boolean checkerThread, int index) {
        isCheckerThread = checkerThread;
        this.index = index;
    }

    public void reset() {
        execute = false;
        for (int count = 0; count < numThreads; ++count) {
            try {
                threadList[count].join();
            } catch (InterruptedException ex) {
                throw new TestProblemException("Test thread was interrupted.  Test failed to run properly");
            }
        }
        server.logout();
    }

    public void setup() {
        execute = true;
        try {
            getSession().getLog().write("WARNING, some tests may take 3 minutes or more");
            getSession().getLog().flush();
        } catch (java.io.IOException e) {
        }

        try {

            DatabaseLogin login = (DatabaseLogin)getSession().getLogin().clone();
            server = new Server(login, numThreads, numThreads + 2);
            server.serverSession.setLogLevel(getSession().getLogLevel());
            server.serverSession.setLog(getSession().getLog());
            server.serverSession.getEventManager().addListener(new SessionEventListener());
            server.login();
            server.copyDescriptors(getSession());
        } catch (Exception ex) {
            if (ex instanceof ValidationException) {
                this.verify();
            }
        }

        for (int count = 0; count < numThreads; ++count) {
            threadList[count] = new Thread(new ConcurrentBatchReadingTest(false, count).runnable());
            timeList[count] = System.currentTimeMillis();
        }
    }

    public void test() {
        for (int count = 0; count < numThreads; ++count) {
            threadList[count].start();
        }

        Thread checker = new Thread(new ConcurrentBatchReadingTest(true, -1).runnable());
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
            throw new TestErrorException("This test took too long to run.  see bug 3124136");
        }
    }

    public Runnable runnable() {
        return new Runnable() {
                // This section represents the executing threads
                // If the type is set to checker then this set the thread
                // to watch the other threads for deadlock.  If none occurs then
                // the test will time out.

                public void run() {
                    if (isCheckerThread) {
                        watchOtherThreads();
                    } else {
                        executeUntilStopped();
                    }
                }
            };
    }

    public void watchOtherThreads() {
        long startTime = System.currentTimeMillis();
        while (((System.currentTimeMillis() - startTime) < (runTime + 30000)) && execute) {
            for (int localIdex = 0; localIdex < numThreads; ++localIdex) {
                if ((System.currentTimeMillis() - timeList[localIdex]) > 30000) {
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
        ClientSession session = server.serverSession.acquireClientSession();

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.anyOf("managedEmployees").get("lastName").like("%o%");
        Expression exp2 = builder.anyOf("managedEmployees").get("firstName").like("%a%");


        while (execute) {
            ReadAllQuery query = new ReadAllQuery(Employee.class);
            query.setShouldRefreshIdentityMapResult(true);
            if (this.index % 2 == 0) {
                query.setSelectionCriteria(expression);
            } else {
                query.setSelectionCriteria(exp2);

            }
            query.addBatchReadAttribute("phoneNumbers");
            session = server.serverSession.acquireClientSession();
            Vector vector = (Vector)session.executeQuery(query);
            Employee emp = (Employee)vector.get((this.index % vector.size()));
            emp.getPhoneNumbers();
            timeList[this.index] = System.currentTimeMillis();

        }
    }

    class SessionEventListener extends SessionEventAdapter {
        public SessionEventListener() {

        }

        public void postExecuteQuery(SessionEvent event) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {

            }
        }
    }

}
