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
 *     cdelahun - Bug 214534: added JMS Cache Coordination for publishing only
 ******************************************************************************/  
package org.eclipse.persistence.sessions.coordination.jms;

import java.util.Hashtable;
import org.eclipse.persistence.exceptions.RemoteCommandManagerException;
import org.eclipse.persistence.internal.sessions.coordination.jms.JMSTopicRemoteConnection;
import org.eclipse.persistence.sessions.coordination.RemoteCommandManager;

/**
 * <p>
 * <b>Purpose</b>: Provide a transport implementation for the Remote Command Module (RCM) that both publishes
 * and subscribes to a JMS topic.  
 * <p>
 * <b>Description</b>: This class manages two connections to the same known JMS Topic:
 * external connection for publishing, local connection for receiving messages.
 * <p>
 * @author Steven Vo
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class JMSTopicTransportManager extends JMSPublishingTransportManager {

    public JMSTopicTransportManager(RemoteCommandManager rcm) {
        super(rcm);
    }
    
    /**
     * INTERNAL:
     * JMSTopicTransportManager may have only two connections: one local and one external.
     * In case the local connection doesn't exist, this method creates it.
     */
    public synchronized void createLocalConnection() {
        if(localConnection == null) {
            try {
                localConnection = createConnection(true);
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
     * In case there's no external connection attempts to create one,
     * if that's successful then (in case there is no local connection, too)
     * attempts to create local connection in a separate thread.
     * Returns clone of the original map.
     */
    public Hashtable getConnectionsToExternalServicesForCommandPropagation() {
        if(this.localConnection == null && !this.rcm.isStopped()) {
            // It's a good time to create localConnection,
            // in a new thread - to return externalConnections promptly.
            this.rcm.getServerPlatform().launchContainerRunnable(new Runnable() {
                public void run() {
                    try {
                        createLocalConnection();
                    } catch (RemoteCommandManagerException ex) {
                        // Ignore exception - user had a chance to handle it in createLocalConnection method:
                        // for instance to change host url and create a new local connection.
                    }
                }
            });
        }
        return super.getConnectionsToExternalServicesForCommandPropagation();
    }
    
    /**
     * INTERNAL:
     * caches local connection, set localConnection to null, closes the cached connection in a new thread.
     */
    public void removeLocalConnection() {
        JMSTopicRemoteConnection connectionToRemove = (JMSTopicRemoteConnection)localConnection;
        synchronized(this) {
            if(connectionToRemove == localConnection) {
                localConnection = null;
            } else {
                connectionToRemove = null;
            }
        }
        // closing connection may take time - do it outside of the synchronized block
        if(connectionToRemove != null) {
            connectionToRemove.close();
        }
    }
}
