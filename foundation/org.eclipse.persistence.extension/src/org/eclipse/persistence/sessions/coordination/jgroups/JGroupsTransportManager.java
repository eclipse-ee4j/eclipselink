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
//     Oracle - initial design and implementation
package org.eclipse.persistence.sessions.coordination.jgroups;

import java.util.Map;

import org.eclipse.persistence.exceptions.RemoteCommandManagerException;
import org.eclipse.persistence.internal.sessions.coordination.RemoteConnection;
import org.eclipse.persistence.internal.sessions.coordination.jgroups.JGroupsRemoteConnection;
import org.eclipse.persistence.sessions.coordination.broadcast.BroadcastTransportManager;
import org.eclipse.persistence.sessions.coordination.RemoteCommandManager;
import org.jgroups.JChannel;

/**
 * <p>
 * <b>Purpose</b>: Provide a transport implementation for the Remote Command Module (RCM) that publishes
 * to a JGroup channel.
 * <p>
 * JGroups is a library for distributed communications.
 * <p>
 * If issues are encountered with the default setting try,<br>
 * -Djava.net.preferIPv4Stack=true
 * <p>
 * @author James Sutherland
 * @since EclipseLink 2.6
 */
public class JGroupsTransportManager extends BroadcastTransportManager {
    protected String configFile = "";

    /**
     * PUBLIC:
     * Creates a JGroupsTransportManager.
     */
    public JGroupsTransportManager() {
    }

    /**
     * PUBLIC:
     * Creates a JGroupsTransportManager.
     */
    public JGroupsTransportManager(RemoteCommandManager rcm) {
        super(rcm);
    }

    /**
     * INTERNAL:
     * This method creates JGroupsRemoteConnection to be used by this TransportManager.
     * It uses a JGroups JChannel using the RCM channel name and specified (or defaulted) config file.
     * Channel is set to not receive own messages.
     */
    protected JGroupsRemoteConnection createConnection(boolean isLocalConnectionBeingCreated) throws RemoteCommandManagerException {
        try {
            JChannel channel;
            if (configFile != null && !configFile.isEmpty()) {
                channel = new JChannel(configFile);
            } else {
                channel = new JChannel();
            }
            channel.connect(this.rcm.getChannel());
            channel.setDiscardOwnMessages(true);
            return new JGroupsRemoteConnection(this.rcm, channel, isLocalConnectionBeingCreated);
        } catch (Exception ex) {
            throw RemoteCommandManagerException.errorCreatingJGroupsConnection(this.configFile, ex);
        }
    }

    /**
     * INTERNAL:
     * JGroups does not require a DiscoveryManager, therefore
     * this method is called during RCM initialization to create all the necessary connections.
     */
    @Override
    public void createConnections() {
        createLocalConnection();
        createExternalConnection();
    }

    /**
     * INTERNAL:
     * JGroups only has a single connection.
     * Verify there are no external connections, use the local connection as the external connection.
     */
    public void createExternalConnection() {
        synchronized (this.connectionsToExternalServices) {
            if (this.connectionsToExternalServices.isEmpty()) {
                try {
                    this.connectionsToExternalServices.put(this.rcm.getServiceId().getId(), this.localConnection);
                } catch (RemoteCommandManagerException rcmException) {
                    // to recover handle RemoteCommandManagerException.ERROR_CREATING_JMS_CONNECTION:
                    // after changing something (for instance config)
                    // call createExternalConnection method again.
                    rcm.handleException(rcmException);
                }
            }
        }
    }

    /**
     * INTERNAL:
     * JGroups only requires a single connection.
     * In case the local connection doesn't exist, this method creates it.
     */
    @Override
    public synchronized void createLocalConnection() {
        if (this.localConnection == null) {
            try {
                this.localConnection = createConnection(true);
            } catch (RemoteCommandManagerException rcmException) {
                // to recover handle RemoteCommandManagerException.ERROR_CREATING_LOCAL_JMS_CONNECTION:
                // after changing something (for instance jmsHostUrl)
                // call createLocalConnection method again.
                rcm.handleException(rcmException);
            }
        }
    }


    /**
     * INTERNAL:
     * In case there's no external connection attempts to create one.
     * Returns external connections (only a single connection for JGroups).
     */
    @Override
    public Map<String, RemoteConnection> getConnectionsToExternalServicesForCommandPropagation() {
        if (getConnectionsToExternalServices().isEmpty() && !this.rcm.isStopped()) {
            createExternalConnection();
        }
        return super.getConnectionsToExternalServicesForCommandPropagation();
    }

    /**
     * INTERNAL:
     * No-op, as the local connection does not need to be removed as JGroups manages registration.
     */
    @Override
    public void removeLocalConnection() {}

    /**
     * PUBLIC:
     * Return the JGroups config xml file name.
     */
    public String getConfigFile() {
        return configFile;
    }

    /**
     * PUBLIC:
     * Set the JGroups config xml file name.
     */
    @Deprecated
    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }
     
    @Override
    public void setConfig(String config) {
        configFile = config;
    }
}
