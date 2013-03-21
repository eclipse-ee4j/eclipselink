/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.sessions.coordination.corba;

import org.eclipse.persistence.exceptions.RemoteCommandManagerException;
import org.eclipse.persistence.internal.helper.SerializationHelper;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.sessions.coordination.RemoteConnection;
import org.eclipse.persistence.internal.sessions.coordination.corba.*;
import org.eclipse.persistence.sessions.coordination.Command;
import org.eclipse.persistence.sessions.coordination.RemoteCommandManager;
import org.eclipse.persistence.sessions.coordination.ServiceId;
import org.eclipse.persistence.sessions.coordination.TransportManager;
import java.io.IOException;
import java.net.InetAddress;
import javax.naming.Context;

public abstract class CORBATransportManager extends TransportManager {
    protected RemoteCommandManager rcm;

    public CORBATransportManager(RemoteCommandManager rcm) {
        this.rcm = rcm;
        this.initialize();
    }

    /**
     * INTERNAL:
     * Initialize default properties for RMI.
     */
    public void initialize() {
        super.initialize();
        if (rcm.getServiceId().getURL() == null) {
            rcm.getServiceId().setURL(getDefaultLocalUrl());
        }

        namingServiceType = TransportManager.JNDI_NAMING_SERVICE;
        setInitialContextFactoryName(getDefaultInitialContextFactoryName());
    }

    /**
     * ADVANCED:
     * Return the default local URL for JNDI lookups
     */
    public String getDefaultLocalUrl() {
        try {
            // Look up the local host name and paste it in a default URL
            String localHost = InetAddress.getLocalHost().getHostName();
            return "iiop://" + localHost + ":" + DEFAULT_URL_PORT;
        } catch (IOException exception) {
            throw RemoteCommandManagerException.errorGettingHostName(exception);
        }
    }

    /**
     * ADVANCED:
     * Remove the local connection from remote accesses.  The implementation removes the local connection from JNDI or RMI registry and set it to null.
     * This method is invoked internally by TopLink when the RCM is shutdown and should not be invoked by user's application.
     */
    public void removeLocalConnection() {
        String unbindName = null;
        try {
            if (namingServiceType == JNDI_NAMING_SERVICE) {
                unbindName = rcm.getServiceId().getId();
                getContext(getLocalContextProperties()).unbind(unbindName);
            } else {
                return;
            }
        } catch (Exception exception) {
            rcm.handleException(RemoteCommandManagerException.errorUnbindingLocalConnection(unbindName, exception));
        }
        localConnection = null;
    }

    /**
     * PUBLIC:
     * Return the only supported JNDI naming service type used to look up remote connections to other service instances.
     *
     * @return The type of naming service used.
     */
    public int getNamingServiceType() {
        return TransportManager.JNDI_NAMING_SERVICE;
    }

    /**
     * PUBLIC:
     * Return the default initial context factory name for a specific CORBA orb.
     */
    public abstract String getDefaultInitialContextFactoryName();

    /**
     * PUBLIC:
     * Narrow the object using the specific generated CORBA Helper class and cast it to a <code>CORBAConnection</code>.
     */
    public abstract CORBAConnection narrow(org.omg.CORBA.Object object);

    /**
     * PUBLIC:
     * Return a new instance of the specific CORBA implementation that implements <code>CORBAConnection</code> interface.
     */
    public abstract CORBAConnection buildCORBAConnection();

    /**
     * INTERNAL:
     * Create and return a wrapper of  a CORBA remote connection to the specified service
     */
    public RemoteConnection createConnection(ServiceId connectionServiceId) {
        RemoteConnection connection = null;

        Object[] args = { connectionServiceId.getId(), connectionServiceId.getURL() };
        rcm.logDebug("looking_up_remote_conn_in_jndi", args);
        try {
            Context context = getRemoteHostContext(connectionServiceId.getURL());

            // look up the distributed connection from JNDI
            CORBAConnection connectionImpl = narrow((org.omg.CORBA.Object)context.lookup(connectionServiceId.getId()));

            // create the wrapper of  the distributed connection
            connection = new CORBARemoteCommandConnection(connectionImpl);
            connection.setServiceId(connectionServiceId);

        } catch (Exception e) {
            rcm.handleException(RemoteCommandManagerException.errorLookingUpRemoteConnection(connectionServiceId.getId(), connectionServiceId.getURL(), e));
        }
        return connection;
    }

    /**
     * INTERNAL:
     * Helper method that processes a byte[] command.  This method is called bye the CORBA implementation class
     * that implements the method byte[]  executeCommand(byte[] command)
     *
     */
    public static byte[] processCommand(byte[] command, RemoteCommandManager aRCM) {
        try {
            // deserialize byte [] to Command object
            Command deserializedCmd = (Command)SerializationHelper.deserialize(command);

            aRCM.processCommandFromRemoteConnection(deserializedCmd);
        } catch (Exception e) {
            // Log the problem encountered during deserialization or rcm processing command 
            Object[] args = { Helper.getShortClassName(command), Helper.printStackTraceToString(e) };
            aRCM.logWarning("error_executing_remote_command", args);

            // Return the byte[] of exception String in case the exception doesn't exist on the other side
            return e.toString().getBytes();
        }

        // Success - return null
        return null;
    }

    /**
     * INTERNAL:
     * Create the local command connection for this transport in a naming service and
     * return it.
     */
    public void createLocalConnection() {
        // Register the remote connection in JNDI naming service
        Object connectionImpl = buildCORBAConnection();

        Object[] args = { rcm.getServiceId().getId() };
        rcm.logDebug("register_local_connection_in_jndi", args);

        try {
            getRemoteHostContext(rcm.getUrl()).rebind(rcm.getServiceId().getId(), connectionImpl);
        } catch (Exception exception) {
            rcm.handleException(RemoteCommandManagerException.errorBindingConnection(rcm.getServiceId().toString(), exception));
            return;
        }

        localConnection = new CORBARemoteCommandConnection((CORBAConnection)connectionImpl);
        localConnection.setServiceId(rcm.getServiceId());
    }
}
