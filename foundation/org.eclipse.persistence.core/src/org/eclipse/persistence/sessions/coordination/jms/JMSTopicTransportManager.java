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
package org.eclipse.persistence.sessions.coordination.jms;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import org.eclipse.persistence.exceptions.RemoteCommandManagerException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.sessions.coordination.jms.JMSTopicRemoteConnection;
import org.eclipse.persistence.sessions.coordination.broadcast.BroadcastTransportManager;
import org.eclipse.persistence.sessions.coordination.RemoteCommandManager;
import org.eclipse.persistence.sessions.coordination.TransportManager;

/**
 * <p>
 * <b>Purpose</b>: Provide a JMS transport implementation for the Remote Command Module (RCM).
 * <p>
 * <b>Description</b>: This class manages two connections to the same known JMS Topic:
 * external connection for publishing, local connection for receiving messages.
 * <p>
 * @author Steven Vo
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class JMSTopicTransportManager extends BroadcastTransportManager {
    protected String connectionFactoryName;

    public static final String DEFAULT_TOPIC = "jms/EclipseLinkTopic";
    public static final String DEFAULT_CONNECTION_FACTORY = "jms/EclipseLinkTopicConnectionFactory";

    public JMSTopicTransportManager(RemoteCommandManager rcm) {
        super(rcm);
    }

    /**
     * INTERNAL:
     * JMSTopicTransportManager has maximum one external connection.
     * Verify there are no external connections,
     * create a new external connection, 
     * add it to external connections' map.
     */
    public void createExternalConnection() {
        synchronized(connectionsToExternalServices) {
            if(connectionsToExternalServices.isEmpty()) {
                try {
                    connectionsToExternalServices.put(rcm.getServiceId().getId(), createConnection(false));
                } catch (RemoteCommandManagerException rcmException) {
                    // to recover handle RemoteCommandManagerException.ERROR_CREATING_JMS_CONNECTION:
                    // after changing something (for instance jmsHostUrl)
                    // call createExternalConnection method again.
                    rcm.handleException(rcmException);
                }
            }            
        }
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
     * This method creates JMSTopicRemoteConnection to be used by this TransportManager.
     * Don't confuse this method with no-op createConnection(ServiceId serviceId).
     */
    protected JMSTopicRemoteConnection createConnection(boolean isLocalConnectionBeingCreated) throws RemoteCommandManagerException {
        Context remoteHostContext = null;
        try {
            remoteHostContext = getRemoteHostContext(getTopicHostUrl());
            TopicConnectionFactory connectionFactory = getTopicConnectionFactory(remoteHostContext);
            Topic topic = getTopic(remoteHostContext);    
            TopicConnection topicConnection = connectionFactory.createTopicConnection();
            // external connection is a puiblisher; local connection is a subscriber
            return new JMSTopicRemoteConnection(rcm, topicConnection, topic, isLocalConnectionBeingCreated);
        } catch (Exception ex) {
            RemoteCommandManagerException rcmException;
            if(isLocalConnectionBeingCreated) {
                rcmException = RemoteCommandManagerException.errorCreatingLocalJMSConnection(topicName, connectionFactoryName, ex);
            } else {
                rcmException = RemoteCommandManagerException.errorCreatingJMSConnection(topicName, connectionFactoryName, ex);
            }
            throw rcmException;
        } finally {
            if(remoteHostContext != null) {
                try {
                    remoteHostContext.close();
                } catch (NamingException namingException) {
                    // ignore
                }
            }
        }
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
        // closing connection may take time - do it outside of the synchronzed block
        if(connectionToRemove != null) {
            connectionToRemove.close();
        }
    }

    /**
     * INTERNAL:
     * JMTopicTransportManager doesn't use DiscoveryManager, therefore
     * this method is called during RCM initialization to create all the necessary connections.
     */
    public void createConnections() {
        createExternalConnection();
        createLocalConnection();
    }

    /**
     * PUBLIC:
     * Configure the JMS Topic Connection Factory Name for the JMS Topic connections.
     */
    public void setTopicConnectionFactoryName(String newTopicConnectionFactoryName) {
        connectionFactoryName = newTopicConnectionFactoryName;
    }

    /**
     * PUBLIC:
     * Return the JMS Topic Connection Factory Name for the JMS Topic connections.
     */
    public String getTopicConnectionFactoryName() {
        return connectionFactoryName;
    }

    /**
     * INTERNAL:
     * Initialize default properties.
     */
    public void initialize() {
        super.initialize();
        topicName = DEFAULT_TOPIC;
        connectionFactoryName = DEFAULT_CONNECTION_FACTORY;
    }

    /**
     * PUBLIC:
     * Return the URL of the machine on the network that hosts the JMS Topic.  This is a required property and must be configured.
     */
    public String getTopicHostUrl() {
        return (String)getRemoteContextProperties().get(Context.PROVIDER_URL);
    }

    /**
     * PUBLIC:
     * Configure the URL of the machine on the network that hosts the JMS Topic. This is a required property and must be configured.
     */
    public void setTopicHostUrl(String jmsHostUrl) {
        getRemoteContextProperties().put(Context.PROVIDER_URL, jmsHostUrl);
        rcm.getServiceId().setURL(jmsHostUrl);
    }

    /**
     * ADVANCED:
     * This funcation is not supported for naming service other than JNDI or TransportManager.JNDI_NAMING_SERVICE.
     */
    public void setNamingServiceType(int serviceType) {
        if (serviceType != TransportManager.JNDI_NAMING_SERVICE) {
            throw ValidationException.operationNotSupported("setNamingServiceType");
        }
    }

    /**
     * INTERNAL:
     */
    protected Topic getTopic(Context remoteHostContext) {
        try {
            return (Topic)remoteHostContext.lookup(topicName);
        } catch (NamingException e) {
            RemoteCommandManagerException rcmException = RemoteCommandManagerException.errorLookingUpRemoteConnection(topicName, rcm.getUrl(), e);
            rcm.handleException(rcmException);
            // If the handler hasn't thrown the exception rethrow it here - it's impossible to recover.
            throw rcmException;
        }
    }

    /**
     * INTERNAL:
     */
    protected TopicConnectionFactory getTopicConnectionFactory(Context remoteHostContext) {
        try {
            return (TopicConnectionFactory)remoteHostContext.lookup(connectionFactoryName);
        } catch (NamingException e) {
            RemoteCommandManagerException rcmException = RemoteCommandManagerException.errorLookingUpRemoteConnection(connectionFactoryName, rcm.getUrl(), e);
            rcm.handleException(rcmException);
            // If the handler hasn't thrown the exception rethrow it here - it's impossible to recover.
            throw rcmException;
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
        if(this.getConnectionsToExternalServices().isEmpty() && !this.rcm.isStopped()) {
            this.createExternalConnection();
            if(this.localConnection == null) {
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
        }
        return super.getConnectionsToExternalServicesForCommandPropagation();
    }
}
