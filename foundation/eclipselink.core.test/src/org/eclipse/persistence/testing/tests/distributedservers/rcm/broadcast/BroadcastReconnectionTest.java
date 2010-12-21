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
package org.eclipse.persistence.testing.tests.distributedservers.rcm.broadcast;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.persistence.exceptions.ExceptionHandler;
import org.eclipse.persistence.exceptions.RemoteCommandManagerException;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.coordination.TransportManager;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestWrapper;

public class BroadcastReconnectionTest extends TestWrapper {
    BroadcastSetupHelper helper;
    BroadcastEventLock eventLock;
    ArrayList localConnectionCreators;

    boolean shouldDestroyFactory;
    boolean shouldRemoveConnectionOnError;
    boolean shouldRemoveConnectionOnErrorOriginal;

    boolean sourceHasThrownErrorPropagatingCommandException;
    boolean sourceHasRemovedRemoteConnection;

    Exception firstRunTestException;
    Exception firstRunVerifyException;

    // take any test that tests sending and receiving remote command

    public BroadcastReconnectionTest(BroadcastSetupHelper.TestWrapperWithEventLock test, boolean shouldDestroyFactory, boolean shouldRemoveConnectionOnError, BroadcastSetupHelper helper) {
        super(test);
        this.helper = helper;
        this.eventLock = test.getEventLock();

        this.shouldDestroyFactory = shouldDestroyFactory;
        this.shouldRemoveConnectionOnError = shouldRemoveConnectionOnError;
        setName("BroadcastReconnectionTest: shouldDestroyFactory = " + shouldDestroyFactory + "; shouldRemoveConnectionOnError = " + shouldRemoveConnectionOnError);
        setDescription("Invalidates RemoteConnection then removes it and runs cache sync test again");
    }

    protected TransportManager getTransportManager() {
        return ((AbstractSession)getSession()).getCommandManager().getTransportManager();
    }

    // Exception handler tries to re-creates local connection in a new thread.
    // Used only in case shouldRemoveConnectionOnError==true and 
    // there is an exception thrown by local connection while it listens:
    // that happens in JMS case, doesn't happen with Oc4jJGroups.

    class LocalConnectionCreator implements ExceptionHandler, Runnable {
        // implements ExceptionHandler - used only in case shouldRemoveConnectionOnError==true
        AbstractSession session;
        ExceptionHandler originalExceptionHandler;
        boolean isActive;
        boolean hasReconnected;

        LocalConnectionCreator(AbstractSession session) {
            this.originalExceptionHandler = session.getExceptionHandler();
            session.setExceptionHandler(this);
            this.session = session;
        }

        public Object handleException(RuntimeException exception) {
            if (exception instanceof RemoteCommandManagerException) {
                if (((RemoteCommandManagerException)exception).getErrorCode() == helper.getRcmExceptionErrorCodeOnFailureToCreateLocalConnection()) {
                    if (isActive) {
                        // already trying to reconnect - eat the exception.
                    } else {
                        isActive = true;
                        hasReconnected = false;
                        session.getServerPlatform().launchContainerRunnable(this);
                    }
                    return null;
                }
            }
            if (originalExceptionHandler != null) {
                return originalExceptionHandler.handleException(exception);
            } else {
                throw exception;
            }
        }

        public void run() {
            while (isActive) {
                // no need for try block - we are eating the exception (see handleException method)
                session.getCommandManager().getTransportManager().createLocalConnection();
                if (session.getCommandManager().getTransportManager().getConnectionToLocalHost() != null) {
                    // success!
                    isActive = false;
                    hasReconnected = true;
                }
            }
        }

        void clear() {
            if (isActive) {
                isActive = false;
            }
            session.setExceptionHandler(originalExceptionHandler);
        }
    }

    protected void setup() throws Throwable {
        // save originals to restore back in reset
        shouldRemoveConnectionOnErrorOriginal = getTransportManager().shouldRemoveConnectionOnError();
        // set the new values for duration of the test
        getTransportManager().setShouldRemoveConnectionOnError(shouldRemoveConnectionOnError);
        // JMS only: if creation of local (listening) JMS connection fails
        // (which it will after the factory is destroyed) then keep trying to re-create listening connection.
        // Should succeed after the factory is recreated.
        // Note that there's no need for that for the main session (the one that sends messages):
        // the listening connection for it will be recreated together with the sending connection -
        // on attempt to send a message.
        if (shouldRemoveConnectionOnError && helper.isLocalConnectionRemovedOnListeningError()) {
            localConnectionCreators = new ArrayList();
            Iterator it = helper.getSessionsIterator();
            while (it.hasNext()) {
                AbstractSession session = (AbstractSession)it.next();
                if (session != getSession()) {
                    localConnectionCreators.add(new LocalConnectionCreator(session));
                }
            }
        }
        // Shutdown the factory - after this sending and receiving remote commands should fail
        if (shouldDestroyFactory) {
            helper.destroyFactory();
        } else {
            stopFactory();
        }
        // disable the target listener.
        // need this in Oc4jJGroups case: the message sent through closed (or destroyed)
        // factory is still delivered to the target - and it happens before the
        // exception is thrown on the source.
        // EventLock would only accept the first unlocking event - and therefore unless
        // UNLOCKED_BY_TARGET_LISTENER is disabled exception (or connection removal)
        // would not show up as a EventLock's state after the test is complete.
        if (helper.shouldIgnoreTargetListenerInReconnectionTest()) {
            eventLock.disableState(BroadcastEventLock.UNLOCKED_BY_TARGET_LISTENER);
        }

        // setup the internal test for the first run
        super.setup();
    }

    protected void test() throws Throwable {
        try {
            // This is expected to fail - the factory has been shut down.
            super.test();
        } catch (Exception ex) {
            // Because message published in a separate thread no exception is thrown in the main thread.
            // Exception is never thrown
            firstRunTestException = ex;
        }
        // Because the factory was either stopped or destroyed verify should fail.
        // However eventLock allows the wrapped test to proceed as soon as either 
        // exception is thrown or connection is removed on the source - therefore there's no guarantee
        // that the message failed to be sent by the source will fail to be eventually recieved by the target -
        // may be if we waited a little bit more it would have reached the target.
        // It's not the case with OracleAQ-based JMS, but it seem to happen with Oc4jJGroups:
        // sending message throws exception on the source, but the message still reaches the target.
        try {
            super.verify();
        } catch (Exception ex) {
            // ignore
            firstRunVerifyException = ex;
        } finally {
            // reset the internal test - the first run is complete
            super.reset();
        }

        // look at the state of eventLock to see whether any events of interest have occurred during super.test()
        int state = eventLock.getState();

        // should be equal to !shouldRemoveConnectionOnError:
        // exception is thrown only in case connection is NOT to be removed on error
        sourceHasThrownErrorPropagatingCommandException = state == BroadcastEventLock.UNLOCKED_BY_SOURCE_EXCEPTION_HANDLER;
        // should be equal to shouldRemoveConnectionOnError: 
        // connection is removed only in case connection is to be removed on error
        sourceHasRemovedRemoteConnection = state == BroadcastEventLock.UNLOCKED_BY_SOURCE_SESSION;

        // repair connections and recreate the factory in case the factory was destroyed
        if (shouldDestroyFactory) {
            // repair all connections, restart factory.
            resetConnections();
        } else {
            // restart the factory
            helper.startFactory();
        }

        if (shouldRemoveConnectionOnError || shouldDestroyFactory) {
            // In External connection has been removed either as a result of the first run,
            // or by restConnection method,
            // therefore the second run will be immediately unlocked with
            // UNLOCKED_BY_SOURCE_SESSION unless we disable it.
            eventLock.disableState(BroadcastEventLock.UNLOCKED_BY_SOURCE_SESSION);
        }

        // now attempt to run the internal test again -
        // connections have been repaired therefore now it should pass.
        super.setup();
        super.test();
    }

    protected void verify() throws Throwable {
        if (shouldRemoveConnectionOnError) {
            if (sourceHasThrownErrorPropagatingCommandException) {
                throw new TestErrorException("With shouldRemoveConnectionOnError==true there should've been NO ErrorPropagatingCommand RCMException thrown");
            }
            if (!sourceHasRemovedRemoteConnection) {
                throw new TestErrorException("With shouldRemoveConnectionOnError==true remote connection should've been removed");
            }
        } else {
            if (!sourceHasThrownErrorPropagatingCommandException) {
                throw new TestErrorException("With shouldRemoveConnectionOnError==false there should've been ErrorPropagatingCommand RCMException thrown");
            }
            if (sourceHasRemovedRemoteConnection) {
                throw new TestErrorException("With shouldRemoveConnectionOnError==false remote connection should've NOT been removed");
            }
        }
        if (firstRunTestException != null) {
            throw new TestErrorException("Unexpectedly firstRunTestException was thrown: ", firstRunTestException);
        }

        // look at the state of eventLock to see whether any events of interest have occurred during super.test()
        int state = eventLock.getState();
        if (helper.shouldIgnoreTargetListenerInReconnectionTest()) {
            // because targetListener is disabled, the successful result would unlock by timer -
            // that means there were no exceptions or connection removals.
            if (state != BroadcastEventLock.UNLOCKED_BY_TIMER) {
                throw new TestErrorException("Unexpected state " + state + " after the second run, BroadcastEventLock.UNLOCKED_BY_TIMER was expected");
            }
        } else {
            if (state != BroadcastEventLock.UNLOCKED_BY_TARGET_LISTENER) {
                throw new TestErrorException("Unexpected state " + state + " after the second run, BroadcastEventLock.UNLOCKED_BY_TARGET_LISTENER was expected");
            }
            if (firstRunVerifyException == null) {
                throw new TestErrorException("Unexpectedly firstRunVerifyException was NOT thrown");
            }
        }

        // verify the second run of internal test - now it should pass
        super.verify();
    }

    public void reset() throws Throwable {
        if (localConnectionCreators != null) {
            Iterator it = localConnectionCreators.iterator();
            while (it.hasNext()) {
                LocalConnectionCreator localConnectionCreator = (LocalConnectionCreator)it.next();
                localConnectionCreator.clear();
            }
            localConnectionCreators = null;
        }
        if (shouldRemoveConnectionOnError) {
            // External connections for sessions other than main are still invalid - let's remove them.
            // Otherwise in case shouldRemoveConnectionOnError is set to false (either here or in another test)
            // the sessions will be stuck with the dead connections.
            helper.removeConnectionsForAllSessionsExcept((AbstractSession)getSession(), "external");
        }
        getTransportManager().setShouldRemoveConnectionOnError(shouldRemoveConnectionOnErrorOriginal);
        firstRunTestException = null;
        firstRunVerifyException = null;
        // some states could have been disabled - set back the original states' enabling.
        eventLock.enableAllStates();
        // reset internal test
        super.reset();
    }

    protected void stopFactory() throws Exception {
        helper.stopFactory();
    }

    protected void resetConnections() throws Exception {
        // the "main" session casted to AbstractSession
        AbstractSession abstractSession = (AbstractSession)getSession();

        // remove dead connections that haven't been already automatically removed.
        if (shouldRemoveConnectionOnError) {
            // getSession()'s external connection is already removed.
            // It will be automatically repaired next time it attempts sending a message.

            // External connections for all other sessions are still there:
            // they didn't send messages => haven't thrown an error => haven't been removed.
            // In these tests other sessions never send any messages, therefore we
            // don't really care about their external connections. However would the
            // other sessions attempt to send a message their invalid external connection
            // would throw exception and will be substituted with the new connection
            // (provided shouldRemoveConnectionOnError is still set to true).

            // remove local connections.
            if (helper.isLocalConnectionRemovedOnListeningError()) {
                // JMS case.
                // All local connections should be removed on listening error -
                // subscriber.receive method throwing JMSException. Nothing to do.
            } else {
                if (helper.isLocalConnectionAlsoExternalConnection()) {
                    // Oc4jJGroups case - local connections for sessions other than the main one
                    // should be removed.
                    // The local connection is the same as external connection:
                    // external connection for the main session was removed => local was removed, too.
                    helper.removeConnectionsForAllSessionsExcept(abstractSession, "local");
                } else {
                    // Currently never get here - provided for consistency only.
                    helper.removeConnectionsForAllSessions("local");
                }
            }
        } else {
            // remove all connections for all sessions.
            helper.removeConnectionsForAllSessions("all");
        }
        // recreate the factory
        helper.createFactory();
        // start the factory
        helper.startFactory();

        // Re-create connections except those that will be re-created automatically
        // (those could be re-created "by hand", too - but that's not necessary).

        // No need to recreate external connections - they are automatically
        // re-creted on attempt to send a message.

        // Recreate local connections.
        if (localConnectionCreators != null) {
            // JMS shouldRemoveConnectionOnError==true case:
            // local connections on sessions other than main should be restored by exception handlers -
            // just wait until they are done.
            boolean allReconnected;
            do {
                allReconnected = true;
                Iterator it = localConnectionCreators.iterator();
                while (it.hasNext()) {
                    LocalConnectionCreator localConnectionCreator = (LocalConnectionCreator)it.next();
                    allReconnected = allReconnected && localConnectionCreator.hasReconnected;
                }
                if (!allReconnected) {
                    Thread.sleep(1000);
                }
            } while (!allReconnected);
        } else {
            // for main session localConnection will be created simultaneously with the external one:
            // on attempt to send a message.
            helper.createConnectionsForAllSessionsExcept(abstractSession, "local");
        }
    }
}
