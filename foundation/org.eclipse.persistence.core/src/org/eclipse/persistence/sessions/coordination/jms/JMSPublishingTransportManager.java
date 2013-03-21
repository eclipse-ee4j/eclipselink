/*******************************************************************************  
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.  
 * This program and the accompanying materials are made available under the  
 * terms of the Eclipse Public License v1.0, which accompanies this distribution  
 * and is available at http://www.eclipse.org/legal/epl-v10.html.  
 *  
 * Contributors: 
 *     cdelahun - Bug 214534: added JMS Cache Coordination for publishing only
 ******************************************************************************/    
package org.eclipse.persistence.sessions.coordination.jms;
 
import java.util.Hashtable;

import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;
import javax.naming.Context;
import javax.naming.NamingException;

import org.eclipse.persistence.exceptions.RemoteCommandManagerException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.sessions.coordination.jms.JMSTopicRemoteConnection;
import org.eclipse.persistence.sessions.coordination.broadcast.BroadcastTransportManager;
import org.eclipse.persistence.sessions.coordination.RemoteCommandManager;
import org.eclipse.persistence.sessions.coordination.TransportManager;

/**
 * <p>
 * <b>Purpose</b>: Provide a transport implementation for the Remote Command Module (RCM) that publishes
 * to a JMS topic.  
 * <p>
 * <b>Description</b>: This class manages two connections: an external connection for publishing to JMS, 
 *  and a local connection which can be used to process JMS messages received from an application JMS listener.
 * <p>
 * @author Chris Delahunt
 * @since EclipseLink 2.1
 */
public class JMSPublishingTransportManager extends BroadcastTransportManager {
    protected String connectionFactoryName;
    
    /* 
     * flag used to toggle between spec compliant behavior and legacy behavior.  Default false causes external JMSTopicRemoteConnection to cache the 
     * TopicConnectionFactory and obtain TopicConnections etc only when executeCommand is called, releasing them immediately.  
     * True causes TopicConnection, TopicSession and TopicPublisher to be held and used for every executeCommand call.
     * A value of true has spec implications, as TopicPublishers are not concurrent objects, but was added in for applications
     * that may have worked previously but encounter performance issues with repeatedly creating TopicConnections etc.
     */
    protected boolean reuseJMSTopicPublisher = false;

    public static final String DEFAULT_TOPIC = "jms/EclipseLinkTopic";
    public static final String DEFAULT_CONNECTION_FACTORY = "jms/EclipseLinkTopicConnectionFactory";
    /**  
     * PUBLIC:  
     * Creates a JMSPublishingOnlyTopicTransportManager  
     */  
    public JMSPublishingTransportManager(RemoteCommandManager rcm) {  
        super(rcm);  
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
            // external connection is a publisher; local connection is a subscriber
            return new JMSTopicRemoteConnection(rcm, connectionFactory, topic, isLocalConnectionBeingCreated, reuseJMSTopicPublisher);
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
     * JMSTopicTransportManager doesn't use DiscoveryManager, therefore
     * this method is called during RCM initialization to create all the necessary connections.
     */
    public void createConnections() {
        createExternalConnection();
        createLocalConnection();
    }

    /**
     * INTERNAL:
     * JMSPublishingTransportManager has maximum one external connection.
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
     * JMSPublishingTransportManager has only two connections: one local and one external.
     * In case the local connection doesn't exist, this method creates it and holds it to be used
     * when processing incoming JMS messages.  The stored local connection on JMSPublishingTransportManager
     * does not connect to topicConnection, and instead must be used from an MDB when a message is received
     */
    public void createLocalConnection() {
        if(localConnection == null) {
            localConnection = new JMSTopicRemoteConnection(rcm);
        }
    }
    

    /**
     * INTERNAL:
     * In case there's no external connection attempts to create one.
     * Returns clone of the original map.
     */
    public Hashtable getConnectionsToExternalServicesForCommandPropagation() {
        if(this.getConnectionsToExternalServices().isEmpty() && !this.rcm.isStopped()) {
            this.createExternalConnection();
        }
        return super.getConnectionsToExternalServicesForCommandPropagation();
    }
    
    /**
     * PUBLIC:
     * flag used to toggle between j2EE/JMS spec compliant behavior and legacy behavior.  Default value false causes external
     *  JMSTopicRemoteConnection to cache the TopicConnectionFactory and obtain TopicConnections, TopicSession and TopicPublishers 
     *  every time executeCommand is called, and then closing them immediately.  This is JMS and J2EE compliant, as the TopicConnection
     *  is never reused in different threads.
     * True causes TopicConnection, TopicSession and TopicPublisher to be cached within the JMSTopicRemoteConnection and used for 
     *  every executeCommand call.  These objects can potentially used concurrently, which the JMS spec does not force 
     *  providers to support.  
     */
    public boolean getReuseJMSTopicPublisher(){
        return this.reuseJMSTopicPublisher;
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
     * PUBLIC:
     * Return the JMS Topic Connection Factory Name for the JMS Topic connections.
     */
    public String getTopicConnectionFactoryName() {
        return connectionFactoryName;
    }

    /**
     * PUBLIC:
     * Return the URL of the machine on the network that hosts the JMS Topic.  This is a required property and must be configured.
     */
    public String getTopicHostUrl() {
        return (String)getRemoteContextProperties().get(Context.PROVIDER_URL);
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
     * INTERNAL:
     * No-op, as the local connection does not need to be removed from JMSPublishingTransportManager.
     * An application must close the connection directly if it is using the local connection as a listener.
     */
    public void removeLocalConnection() {}

    /**
     * ADVANCED:
     * This function is not supported for naming service other than JNDI or TransportManager.JNDI_NAMING_SERVICE.
     */
    public void setNamingServiceType(int serviceType) {
        if (serviceType != TransportManager.JNDI_NAMING_SERVICE) {
            throw ValidationException.operationNotSupported("setNamingServiceType");
        }
    }
    
    /**
     * PUBLIC:
     * flag used to toggle between j2EE/JMS spec compliant behavior and legacy behavior.  Default value false causes external
     *  JMSTopicRemoteConnection to cache the TopicConnectionFactory and obtain TopicConnections, TopicSession and TopicPublishers 
     *  every time executeCommand is called, and then closing them immediately.  This is JMS and J2EE compliant, as the TopicConnection
     *  is never reused in different threads.
     * True causes TopicConnection, TopicSession and TopicPublisher to be cached within the JMSTopicRemoteConnection and used for 
     *  every executeCommand call.  These objects can potentially used concurrently, which the JMS spec does not force 
     *  providers to support.  
     *  
     *  @param reuseJMSTopicPublisher
     */
    public void setShouldReuseJMSTopicPublisher(boolean reuseJMSTopicPublisher){
        this.reuseJMSTopicPublisher = reuseJMSTopicPublisher;
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
     * Configure the URL of the machine on the network that hosts the JMS Topic. This is a required property and must be configured.
     */
    public void setTopicHostUrl(String jmsHostUrl) {
        getRemoteContextProperties().put(Context.PROVIDER_URL, jmsHostUrl);
        rcm.getServiceId().setURL(jmsHostUrl);
    }
}  
