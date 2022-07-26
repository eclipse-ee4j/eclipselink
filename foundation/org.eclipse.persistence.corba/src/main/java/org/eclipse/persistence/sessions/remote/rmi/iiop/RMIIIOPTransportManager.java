/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.sessions.remote.rmi.iiop;

import org.eclipse.persistence.exceptions.RemoteCommandManagerException;
import org.eclipse.persistence.internal.sessions.coordination.RemoteConnection;
import org.eclipse.persistence.internal.sessions.coordination.rmi.RMIRemoteCommandConnection;
import org.eclipse.persistence.internal.sessions.coordination.rmi.RMIRemoteCommandConnectionImpl;
import org.eclipse.persistence.internal.sessions.coordination.rmi.RMIRemoteConnection;
import org.eclipse.persistence.sessions.coordination.RemoteCommandManager;
import org.eclipse.persistence.sessions.coordination.rmi.RMITransportManager;

import javax.naming.Context;
import java.io.IOException;
import java.net.InetAddress;
import javax.rmi.PortableRemoteObject;

/**
 * <p>
 * <b>Purpose</b>: Provide an RMI-IIOP transport implementation for RCM.
 * </p><p>
 * <b>Description</b>: This class manages the RMI remote connections to other
 * RCM service instances and posts the local RMI connection to this service instance
 * in a name service so that other RCM service instances can connect to it.
 * </p>
 */
public class RMIIIOPTransportManager extends RMITransportManager {

    public RMIIIOPTransportManager(RemoteCommandManager rcm) {
        super(rcm);
    }

    /**
     * INTERNAL:
     * Look the specified remote object up in JNDI and return a Connection to it.
     */
    protected RemoteConnection createConnectionFromJNDI(String remoteObjectIdentifier, String hostURL) {
        Object[] args = { remoteObjectIdentifier, hostURL };
        rcm.logDebug("looking_up_remote_conn_in_jndi", args);
        try {
            Context context = getRemoteHostContext(hostURL);
                return new RMIRemoteConnection((RMIRemoteCommandConnection) PortableRemoteObject.narrow(
                        context.lookup(remoteObjectIdentifier), RMIRemoteCommandConnection.class));
        } catch (Exception e) {
            try {
                rcm.handleException(RemoteCommandManagerException.errorLookingUpRemoteConnection(remoteObjectIdentifier, hostURL, e));
            } catch (Exception ex2) {
                // Must catch this exception and log a debug message
                rcm.logDebug("unable_to_look_up_remote_conn_in_jndi", args);
            }
        }
        return null;
    }

    /**
     * INTERNAL:
     * Put the local command connection of this transport in JNDI and return it
     */
    protected void createLocalConnectionInJNDI() {
        try {
            // Register the remote connection in JNDI naming service
            RMIRemoteCommandConnection remoteConnectionObject = new RMIRemoteCommandConnectionImpl(rcm);
            Object[] args = { rcm.getServiceId().getId() };
            rcm.logDebug("register_local_connection_in_jndi", args);
            getLocalHostContext().rebind(rcm.getServiceId().getId(), remoteConnectionObject);
            localConnection = new RMIRemoteConnection(remoteConnectionObject);
        } catch (Exception exception) {
            rcm.handleException(RemoteCommandManagerException.errorBindingConnection(rcm.getServiceId().toString(), exception));
        }
    }

    /**
     * INTERNAL:
     * Return the default local URL for JNDI lookups
     */
    public String getDefaultLocalUrl() {
        try {
            // Look up the local host name and paste it in a default URL
            String localHost = InetAddress.getLocalHost().getHostName();
            return DEFAULT_IIOP_URL_PROTOCOL + "::" + localHost + ":" + DEFAULT_IIOP_URL_PORT;
        } catch (IOException exception) {
            throw RemoteCommandManagerException.errorGettingHostName(exception);
        }
    }

}
