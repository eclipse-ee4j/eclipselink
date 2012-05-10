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
package org.eclipse.persistence.testing.tests.simultaneous;

import java.io.*;
import java.util.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.server.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.framework.ui.SynchronizedTester;
import org.eclipse.persistence.testing.framework.ui.SynchronizedTestExecutor;

public class MultithreadTestCase extends AutoVerifyTestCase {
    protected TestCase[] test;
    protected int numberOfTests;
    protected boolean useSequenceConnectionPool = false;

    protected class TestEventListenerImpl implements junit.framework.TestListener {
        public static final int INITIAL = 0;
        public static final int STARTED = 1;
        public static final int FINISHED = 2;
        private int state;

        private TestEventListenerImpl() {
            state = INITIAL;
        }

        public void addError(junit.framework.Test test, Throwable error) {
        }

        public void addFailure(junit.framework.Test test, junit.framework.AssertionFailedError error) {
        }

        public void endTest(junit.framework.Test test) {
            state = FINISHED;
        }

        public void startTest(junit.framework.Test test) {
            state = STARTED;
        }

        public boolean isStarted() {
            return state == STARTED;
        }

        public boolean isFinished() {
            return state == FINISHED;
        }
    }
    ;

    protected TestEventListenerImpl[] testExecutorListener;
    protected TestExecutorWithClientSession[] testExecutorWithClientSession;
    protected SynchronizedTestExecutor[] testThread;

    protected class SynchronizedTesterImpl implements SynchronizedTester {
        public static final int INITIAL = 0;
        public static final int FINISHED = 1;
        private int state;
        private Throwable exception;

        private SynchronizedTesterImpl() {
            state = INITIAL;
        }

        public void finishedTest() {
            state = FINISHED;
        }

        public void notifyException(Throwable exception) {
            this.exception = exception;
        }

        public boolean isFinished() {
            return state == FINISHED;
        }

        public Throwable getException() {
            return exception;
        }
    }
    ;

    protected SynchronizedTesterImpl[] testThreadListener;
    protected Hashtable allowedExceptions;
    private Session originalSession;

    /**
     * Default constructor for MultithreadTestCase
     * Added to allow easier subclassing.  This constructor should only be used by subclasses.
     * addTests() must be called immediately after this constructor by the subclass
     */
    protected MultithreadTestCase() {
        super();
        setDescription("Runs several tests simultaneously");
        allowedExceptions = new Hashtable();
        addAllowedException("org.eclipse.persistence.exceptions.OptimisticLockException");
    }

    // MultithreadTestCase runs simultaneously each of the TestCases
    // passed in Vector tests.
    public MultithreadTestCase(Vector test) {
        this();
        setTests(test);
    }

    // If an exception occur in one of concurrently run tests,
    // MultithreadTestCase assigned this exception (and therefore status "Failed"),
    // unless the exception is on allowedExceptions list (see verify()).
    // By default, only org.eclipse.persistence.exceptions.OptimisticLockException
    // is on this list.
    // Use addAllowedException and removeAllowedException to add/remove
    // an exception to allowedExceptions list.
    public boolean addAllowedException(String exceptionClassName) {
        try {
            allowedExceptions.put(exceptionClassName, Class.forName(exceptionClassName));
            return true;
        } catch (ClassNotFoundException classNotFoundException) {
            return false;
        }
    }

    public void removeAllowedException(String exceptionClassName) {
        allowedExceptions.remove(exceptionClassName);
    }

    /**
     * Set the tests in this test case.  Refactored for easier subclassing.
     */
    public void setTests(Vector test) {
        if ((test != null) && !test.isEmpty()) {
            numberOfTests = test.size();

            this.test = new TestCase[numberOfTests];
            test.toArray(this.test);

            testExecutorWithClientSession = new TestExecutorWithClientSession[numberOfTests];
            testExecutorListener = new TestEventListenerImpl[numberOfTests];
            testThread = new SynchronizedTestExecutor[numberOfTests];
            testThreadListener = new SynchronizedTesterImpl[numberOfTests];
        }
    }

    protected void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        setupSession();

        for (int i = 0; i < numberOfTests; i++) {
            // Need this for proper idention of while printing test results
            test[i].setContainer(this);

            // To run a test we need an executor.
            // To run simultaneously several tests we need a personal
            // executor for each of them.
            // For each test an instance of a subclass of TestExecutor is created:
            // TestExecutorWithClientSession:
            //   carries clientSession;
            //   readdresses some method calls to its parent - TestExecutor, disallows other ones;
            testExecutorWithClientSession[i] = new TestExecutorWithClientSession(getExecutor());

            // That's an optional TestExecutorListener.
            // Currently not used.
            testExecutorListener[i] = new TestEventListenerImpl();
            testExecutorWithClientSession[i].setListener(testExecutorListener[i]);

            // That's a ThreadListener - it receives call backs from
            // the thread - currently used is testFinished notification.
            testThreadListener[i] = new SynchronizedTesterImpl();

            test[i].setExecutor(testExecutorWithClientSession[i]);
                 
            // That's a personal thread for the test
            testThread[i] = new SynchronizedTestExecutor(testExecutorWithClientSession[i], test[i], testThreadListener[i]);
            testThread[i].setName("Test Thread " + i);
        }
    }

    protected void setupSession() {
        originalSession = getSession();
        Session newSession = setupNewSession(originalSession, useSequenceConnectionPool);
        getExecutor().setSession(newSession);
    }

    // In order for MultithreadTestCase to run, it needs
    // a ServerSession to be returned by TestExecutor.getSession().
    // This method takes a session originally held by TestExecutor 
    // (typically DatabaseSession) - and returns the ServerSession
    // which should be set as a Session into TestExecutor.
    // The possible variants:
    //   case originalSession is a ServerSession - will use it as a new session;
    //   case originalSession is a ClientSession - will use its parent as a new session.
    //
    // This is a static method so that it could be used by other classes.
    //
    // Use resetOriginalSession before setting the originalSession
    // back into TestExecutor.
    static public Session setupNewSession(Session originalSession, boolean useSequenceConnectionPool) {
        // Note that because ServerSession.isDatabaseSession() returns true
        // it is important to call isServerSession() before
        // isDatabaseSession() is called 
        if (originalSession.isServerSession()) {
            return originalSession;
        } else if (originalSession.isDatabaseSession()) {
            // The following piece mostly copied from ClientServerTestModel
            // The only thing added is initializePreallocatedSequences() -
            // don't want to re-use the sequence numbers allocated by originalSession.
            DatabaseSession databaseSession = (DatabaseSession)originalSession;
            DatabaseLogin login = (DatabaseLogin)databaseSession.getLogin().clone();
            databaseSession.getSequencingControl().initializePreallocated();
            Server serverSession = new ServerSession(login, 5, 5);
            serverSession.setSessionLog(databaseSession.getSessionLog());
            if (useSequenceConnectionPool) {
                serverSession.getSequencingControl().setShouldUseSeparateConnection(true);
            } else {
                serverSession.getSequencingControl().setShouldUseSeparateConnection(false);                
            }
            serverSession.login();

            Vector descriptors = new Vector();
            for (Iterator iterator = databaseSession.getDescriptors().values().iterator();
                     iterator.hasNext();) {
                descriptors.addElement(iterator.next());
            }
            serverSession.addDescriptors(descriptors);

            return serverSession;
        } else if (originalSession.isClientSession()) {
            ClientSession clientSession = (ClientSession)originalSession;
            ServerSession serverSession = clientSession.getParent();
            return serverSession;
        } else {
            // Should never happen
            return null;
        }
    }

    // Note that currently there is nothing done
    // to resolve possible deadlocks.
    protected void test() {
        //run test threads
        for (int i = 0; i < numberOfTests; i++) {
            testThread[i].start();
        }

        //waiting for test threads to complete
        int numberOfCompletedTests;
        do {
            numberOfCompletedTests = 0;
            for (int i = 0; i < numberOfTests; i++) {
                if (testThreadListener[i].isFinished()) {
                    numberOfCompletedTests++;
                }
            }
        } while (numberOfCompletedTests < numberOfTests);
    }

    protected void verify() {
        EclipseLinkException exception = null;
        for (int i = 0; (i < numberOfTests) && (exception == null); i++) {
            exception = test[i].getTestResult().getException();
            if (exception != null) {
                for (Enumeration enumtr = allowedExceptions.elements();
                         enumtr.hasMoreElements() && (exception != null);) {
                    if (((Class)(enumtr.nextElement())).isInstance(exception)) {
                        exception = null;
                    }
                }
                if (exception != null) {
                    setTestException(exception);
                }
            }
        }
    }

    // the originalSession != null check is needed
    // in case reset is called more than once -
    // which may happen in case of test failure due to
    // the resent changes in AutoVerifyTestCase.
    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        if (originalSession != null) {
            resetSession();
        }
    }

    protected void resetSession() {
        resetOriginalSession(originalSession, getSession());
        getExecutor().setSession(originalSession);
        originalSession = null;
    }

    static public void resetOriginalSession(Session originalSession, Session newSession) {
        // Note that because ServerSession.isDatabaseSession() returns true
        // it is important to call isServerSession() before
        // isDatabaseSession() is called 
        if (originalSession.isServerSession()) {
            // Assuming that originalSession == newSession
            // (see setNewSession(..))
            return;
        } else if (originalSession.isDatabaseSession()) {
            ((DatabaseSession)newSession).logout();

            DatabaseSession databaseSession = (DatabaseSession)originalSession;

            // Is this necessary? Don't know.
            // Just copied it from ClientServerTestModel
            databaseSession.logout();
            databaseSession.login();
        }
    }

    public void logResult(Writer log) {
        super.logResult(log);

        for (int i = 0; i < numberOfTests; i++) {
            try {
                log.write(org.eclipse.persistence.internal.helper.Helper.cr() + Helper.getTabs(getNestedCounter() + 1) + "Test Thread " + i);
            } catch (IOException exception) {
            }
            test[i].logResult(log);
        }
    }

    public void useSequenceConnectionPool() {
        useSequenceConnectionPool = true;
    }
}
