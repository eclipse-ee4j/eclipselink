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

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.coordination.RemoteCommandManager;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestCollection;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestProblemException;
import org.eclipse.persistence.testing.framework.TestWrapper;

public abstract class BroadcastSetupHelper {
    public static final String TEST_CONTEXT_FACTORY = "org.eclipse.persistence.testing.framework.naming.InitialContextFactoryImpl";
    public static Properties CONTEXT_PROPERTIES = new Properties();
    static {
        CONTEXT_PROPERTIES.setProperty(Context.INITIAL_CONTEXT_FACTORY, TEST_CONTEXT_FACTORY);
    }

    public static Context getContext() throws NamingException {
        // should use the testing JNDI factory. It doesn't require authorization.
        return new InitialContext(CONTEXT_PROPERTIES);
    }

    // connection type to be passed to methods:
    // removeConnectionForAllSessions, removeConnectionForAllSessionsExcept,
    // createConnectionForAllSessions, createConnectionForAllSessionsExcept,
    public static final String LOCAL = "local";
    public static final String EXTERNAL = "external";
    public static final String ALL = "all";


    protected static BroadcastEventLock eventLock = new BroadcastEventLock();

    public static BroadcastEventLock getEventLock() {
        return eventLock;
    }

    // Should return true in case a message may be received by the target,
    // even though the source threw an exception on attempt to send it.
    // This strange condition happens with Oc4jJGroups.

    public boolean shouldIgnoreTargetListenerInReconnectionTest() {
        return false;
    }

    // If removeConnectionOnError is set,
    // local (listening) connection is removed in JMS case if subscriber.receive() throws exception;
    // however in Oc4jJGroups case the only (local and external) connection is not removed
    // unless message sending fails.

    public abstract boolean isLocalConnectionRemovedOnListeningError();

    // Returns errorCode of RemoteCommandManagerException thrown in case
    // creation of localConnection has failed.

    public abstract int getRcmExceptionErrorCodeOnFailureToCreateLocalConnection();

    // JMSTopicTransportManager has separate connection for sending (external) and receiving (local) messages.
    // Oc4jJGroups uses a single connection for both sending and receiving messages.

    public abstract boolean isLocalConnectionAlsoExternalConnection();

    // After internal TestCase's method test() is executed
    // waits until either the event or timeToWaitBeforeVerify is up

    public static class TestWrapperWithEventLock extends TestWrapper {
        long timeToWaitBeforeVerify;
        BroadcastEventLock eventLock;

        TestWrapperWithEventLock(TestCase test, long timeToWaitBeforeVerify, BroadcastEventLock eventLock) {
            super(test);
            this.timeToWaitBeforeVerify = timeToWaitBeforeVerify;
            this.eventLock = eventLock;
        }

        public long getTimeToWaitBeforeVerify() {
            return this.timeToWaitBeforeVerify;
        }

        public void setTimeToWaitBeforeVerify(long timeToWaitBeforeVerify) {
            this.timeToWaitBeforeVerify = timeToWaitBeforeVerify;
        }

        public BroadcastEventLock getEventLock() {
            return eventLock;
        }

        protected void test() throws Throwable {
            this.eventLock.initialize();
            super.test();
            this.eventLock.waitUntilUnlocked(this.timeToWaitBeforeVerify);
        }

        protected void verify() throws Throwable {
            try {
                super.verify();
            } catch (Exception verifyException) {
                if (this.eventLock.getState() == BroadcastEventLock.UNLOCKED_BY_TIMER) {
                    throw new TestErrorException("Target hasn't processed remote command. Consider insreasing timeToWaitBeforeVerify.", verifyException);
                } else if (this.eventLock.getState() == BroadcastEventLock.UNLOCKED_BY_SOURCE_EXCEPTION_HANDLER) {
                    throw new TestErrorException("Source has thrown an exception on attempt to propagate command. Note that the internal exception is NOT the one thrown by source.", verifyException);
                } else if (this.eventLock.getState() == BroadcastEventLock.UNLOCKED_BY_SOURCE_SESSION) {
                    throw new TestErrorException("Source has removed the remote connection. Note that the internal exception is NOT the one thrown by source.", verifyException);
                } else {
                    throw verifyException;
                }
            }
        }
    }

    // state
    protected boolean isCreated = false;

    public boolean isCreated() {
        return isCreated;
    }
    protected boolean isStarted = false;

    public boolean isStarted() {
        return isStarted;
    }

    // jndi names
    protected String factoryJndiName;

    String getFactoryJndiName() {
        return factoryJndiName;
    }
    protected String topicJndiName;

    String getTopicJndiName() {
        return topicJndiName;
    }

    // sessions used
    protected IdentityHashMap sessions = new IdentityHashMap(2);

    public Iterator getSessionsIterator() {
        return sessions.keySet().iterator();
    }

    // there's no public constructor - the subclasses are singletons.

    protected BroadcastSetupHelper() {
        super();
    }

    public void createFactory() throws Exception {
        if (!isCreated) {
            Object[] factoryAndTopic = internalCreateFactory();
            Context context = getContext();
            if (factoryAndTopic[0] != null) {
                context.bind(factoryJndiName, factoryAndTopic[0]);
            }
            if (factoryAndTopic[1] != null) {
                context.bind(topicJndiName, factoryAndTopic[1]);
            }
            isCreated = true;
            isStarted = false;
        }
    }

    public void startFactory() throws Exception {
        if (isCreated && !isStarted) {
            internalStartFactory();
            isStarted = true;
        }
    }

    public void stopFactory() throws Exception {
        if (isCreated && isStarted) {
            internalStopFactory();
            isStarted = false;
        }
    }

    public void destroyFactory() throws Exception {
        if (isCreated) {
            try {
                Context context = getContext();
                if (factoryJndiName != null) {
                    Object factory = context.lookup(factoryJndiName);
                    if (factory != null) {
                        context.unbind(factoryJndiName);
                    }
                }
                if (topicJndiName != null) {
                    Object topic = context.lookup(topicJndiName);
                    if (topic != null) {
                        context.unbind(topicJndiName);
                    }
                }
                internalDestroyFactory();
            } finally {
                isCreated = false;
                isStarted = false;
            }
        }
    }

    // returns array of two objects: the first is factory, the second is topic

    protected abstract Object[] internalCreateFactory() throws Exception;

    protected abstract void internalStartFactory() throws Exception;

    protected abstract void internalStopFactory() throws Exception;

    protected abstract void internalDestroyFactory() throws Exception;

    protected abstract void createTransportManager(RemoteCommandManager rcm);

    public void startCacheSynchronization(AbstractSession session, boolean isSource) {
        try {
            sessions.put(session, new Boolean(isSource));
            if (sessions.size() == 1) {
                createFactory();
                startFactory();
            }

            RemoteCommandManager rcm = new RemoteCommandManager(session);
            createTransportManager(rcm);
            session.setShouldPropagateChanges(true);
            rcm.initialize();

            getEventLock().attach(session, isSource);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new TestProblemException("exception in startCacheSynchronization ", ex);
        }
    }

    public static Object wrapAllTestCases(Object test, long timeToWait) {
        if (test instanceof TestCase) {
            return new TestWrapperWithEventLock((TestCase)test, timeToWait, eventLock);
        } else if (test instanceof TestCollection) {
            Vector tests = ((TestCollection)test).getTests();
            Vector wrappedTests = new Vector(tests.size());
            for (int i = 0; i < tests.size(); i++) {
                Object wrappedTest = wrapAllTestCases(tests.elementAt(i), timeToWait);
                if (wrappedTest != null) {
                    wrappedTests.add(wrappedTest);
                } else {
                    // must be collection - keep it
                    wrappedTests.add(tests.elementAt(i));
                }
            }
            // remove the original tests
            tests.clear();
            // add the wrapped ones
            ((TestCollection)test).addTests(wrappedTests);
            return null;
        } else {
            // Neither TestCase nor TestCollection - can't handle it
            return null;
        }
    }

    // Looking for the test case with the specified short class name.
    // If shouldLookUnderWrapper==true then will be checks the class name of the wrapped test -
    // but returns the wrapper test.

    public static TestCase getTestCase(Object test, String testShortClassName, boolean shouldLookUnderWrapper) {
        if (test instanceof TestCase) {
            TestCase testToLookAt = (TestCase)test;
            if (shouldLookUnderWrapper) {
                while (testToLookAt instanceof TestWrapper) {
                    testToLookAt = ((TestWrapper)testToLookAt).getWrappedTest();
                }
            }
            if (Helper.getShortClassName(testToLookAt).equals(testShortClassName)) {
                // return the wrapped test
                return (TestCase)test;
            }
        } else if (test instanceof TestCollection) {
            Iterator it = ((TestCollection)test).getTests().iterator();
            while (it.hasNext()) {
                TestCase currentTest = getTestCase(it.next(), testShortClassName, shouldLookUnderWrapper);
                if (currentTest != null) {
                    return currentTest;
                }
            }
        }
        return null;
    }

    public void stopCacheSynchronization(AbstractSession session) {
        try {
            sessions.remove(session);
            getEventLock().detach(session);
            session.setShouldPropagateChanges(false);
            session.getCommandManager().shutdown();
            session.setCommandManager(null);

            if (sessions.size() == 0) {
                // destroy the factory
                destroyFactory();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new TestProblemException("exception in stopCacheSynchronization ", ex);
        }
    }

    public void removeConnectionsForAllSessions(String connectionType) throws Exception {
        removeConnectionsForAllSessionsExcept(null, connectionType);
    }

    public void removeConnectionsForAllSessionsExcept(AbstractSession sessionToIgnore, String connectionType) throws Exception {
        Iterator it = sessions.keySet().iterator();
        while (it.hasNext()) {
            AbstractSession session = (AbstractSession)it.next();
            if (session != sessionToIgnore) {
                removeConnections(session, connectionType);
            }
        }
    }

    protected void removeConnections(AbstractSession session, String connectionType) {
        if (connectionType.equalsIgnoreCase(LOCAL)) {
            session.getCommandManager().getTransportManager().removeLocalConnection();
        } else if (connectionType.equals(EXTERNAL)) {
            session.getCommandManager().getTransportManager().removeAllConnectionsToExternalServices();
        } else if (connectionType.equals(ALL)) {
            session.getCommandManager().getTransportManager().discardConnections();
        } else {
            throw new TestProblemException("invalid connection type: " + connectionType + ". Valid types are: " + LOCAL + "; " + EXTERNAL + "; " + ALL);
        }
    }

    // by default does nothing. in JMS case sends an arbitrary message to speed up shut down of listening threads.

    protected void sendMessageToStopListenerThreads() throws Exception {
    }

    public void createConnectionsForAllSessions(String connectionType) {
        createConnectionsForAllSessionsExcept(null, connectionType);
    }

    public void createConnectionsForAllSessionsExcept(AbstractSession sessionToIgnore, String connectionType) {
        Iterator it = sessions.keySet().iterator();
        while (it.hasNext()) {
            AbstractSession session = (AbstractSession)it.next();
            if (session != sessionToIgnore) {
                createConnections(session, connectionType);
            }
        }
    }

    protected void createConnections(AbstractSession session, String connectionType) {
        if (connectionType.equalsIgnoreCase(LOCAL)) {
            session.getCommandManager().getTransportManager().createLocalConnection();
        } else if (connectionType.equals(EXTERNAL)) {
            createExternalConnection(session);
        } else if (connectionType.equals(ALL)) {
            session.getCommandManager().getTransportManager().createConnections();
        } else {
            throw new TestProblemException("invalid connection type: " + connectionType + ". Valid types are: " + LOCAL + "; " + EXTERNAL + "; " + ALL);
        }
    }

    protected abstract void createExternalConnection(AbstractSession session);
}
