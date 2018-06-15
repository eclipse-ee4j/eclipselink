/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.tests.distributedservers.rcm.jgroups;

import org.eclipse.persistence.exceptions.RemoteCommandManagerException;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.coordination.RemoteCommandManager;
import org.eclipse.persistence.sessions.coordination.TransportManager;
import org.eclipse.persistence.sessions.coordination.jms.JMSTopicTransportManager;
import org.eclipse.persistence.testing.tests.distributedservers.rcm.broadcast.BroadcastSetupHelper;

public class JGroupsSetupHelper extends BroadcastSetupHelper {
    protected static JGroupsSetupHelper helper;

    public static JGroupsSetupHelper getHelper() {
        if (helper == null) {
            helper = new JGroupsSetupHelper();
        }
        return helper;
    }

    protected JGroupsSetupHelper() {
        super();
    }

    // If removeConnectionOnError is set,
    // local (listening) connection is removed in JMS case if subscriber.receive() throws exception;
    // however in Oc4jJGroups case the only (local and external) connection is not removed
    // unless message sending fails.
    @Override
    public boolean isLocalConnectionRemovedOnListeningError() {
        return false;
    }

    // Returns errorCode of RemoteCommandManagerException thrown in case
    // creation of localConnection has failed.
    @Override
    public int getRcmExceptionErrorCodeOnFailureToCreateLocalConnection() {
        return RemoteCommandManagerException.ERROR_CREATING_LOCAL_JMS_CONNECTION;
    }

    // JMSTopicTransportManager has separate connection for sending (external) and receiving (local) messages.
    // Oc4jJGroups uses a single connection for both sending and receiving messages.
    @Override
    public boolean isLocalConnectionAlsoExternalConnection() {
        return true;
    }

    @Override
    protected void createTransportManager(RemoteCommandManager rcm) {
        try {
            TransportManager transport = (TransportManager)Class.forName("org.eclipse.persistence.sessions.coordination.jgroups.JGroupsTransportManager").newInstance();
            rcm.setTransportManager(transport);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    // Sends an arbitrary message to speed up shut down of listening threads.
    @Override
    protected void sendMessageToStopListenerThreads() throws Exception {

    }

    @Override
    protected void createExternalConnection(AbstractSession session) {
        ((JMSTopicTransportManager)session.getCommandManager().getTransportManager()).createExternalConnection();
    }

    @Override
    protected void internalDestroyFactory() throws Exception {
    }

    @Override
    protected void internalStartFactory() throws Exception {
    }

    @Override
    protected void internalStopFactory() throws Exception {
    }

    @Override
    protected Object[] internalCreateFactory() throws Exception {
        return null;
    }

    @Override
    public void createFactory() throws Exception {
        if (!isCreated) {
            isCreated = true;
            isStarted = false;
        }
    }
}
