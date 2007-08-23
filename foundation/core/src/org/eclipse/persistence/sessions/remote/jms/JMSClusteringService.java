/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.sessions.remote.jms;

import org.eclipse.persistence.internal.sessions.remote.RemoteConnection;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.logging.SessionLog;
import javax.naming.*;
import javax.jms.*;
import java.net.*;

/**
 * <p>
 * <b>PURPOSE</b>:To Provide a framework for offering customers the ability to automatically
 * connect multiple sessions for synchrnization.</p>
 * <p>
 * <b>Descripton</b>:This object will connect the TopLink Session to an existing JMS Topic from
 * an existing JMS Connection Factory.</p>
 *
 * @author Gordon Yorke
 * @see org.eclipse.persistence.sessions.remote.CacheSynchronizationManager
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.sessions.coordination.jms.JMSTopicTransportManager}
 */
public class JMSClusteringService extends org.eclipse.persistence.sessions.remote.AbstractJNDIClusteringService {
    protected String topicName;
    protected String connectionFactoryName;
    protected TopicConnection listeningConnection;

    /**
     * PUBLIC:
     * Creates a JMSClusteringService
     * @SBGen Constructor
     */
    public JMSClusteringService(Session session) {
        super(session);
        this.reconnectionPolicy = new JMSDistributedSessionReconnectPolicy(this);
    }

    /**
     * INTERNAL:
     * This method is called by the cache synchronization manager when this server should
     * connect back ('handshake') to the server from which this remote connection came.
     */
    public void connectBackToRemote(RemoteConnection connection) throws Exception {
        //here we can register a listener to the remote JMS service
        TopicConnectionFactory connectionFactory = (TopicConnectionFactory)getContext(getInitialContextProperties()).lookup(getTopicConnectionFactoryName());
        Topic topic = (Topic)getContext(getInitialContextProperties()).lookup(connection.getServiceName());
        this.listeningConnection = connectionFactory.createTopicConnection();
        TopicSession topicSession = this.listeningConnection.createTopicSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
        REMOVE_MessageListener listener = new REMOVE_MessageListener(getSession());
        TopicSubscriber subscriber = topicSession.createSubscriber(topic);
        subscriber.setMessageListener(listener);
        this.listeningConnection.start();

    }

    /**
     * ADVANCED:
     *     This method should return a remote connection of the appropraite type for
     * use in the synchronization
     */
    public RemoteConnection createRemoteConnection() throws JMSException, NamingException {
        TopicConnectionFactory connectionFactory = (TopicConnectionFactory)getContext(getInitialContextProperties()).lookup(getTopicConnectionFactoryName());
        Topic topic = (Topic)getContext(getInitialContextProperties()).lookup(getTopicName());
        TopicConnection topicConnection = connectionFactory.createTopicConnection();
        TopicSession topicSession = topicConnection.createTopicSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
        TopicPublisher topicPublisher = topicSession.createPublisher(topic);
        JMSConnection connection = new JMSConnection(topicSession, topicPublisher);
        connection.setServiceName(getTopicName());
        return connection;
    }

    /**
     * ADVANCED:
     *     This method should return a remote connection of the appropraite type for
     * use in the synchronization
     * Not Used
     */
    public RemoteConnection createRemoteConnection(String sessionId, String jndiHostURL) {
        //not used
        return null;
    }

    /**
     * ADVANCED:
     * This method will register the dispatcher for this session in JNDI
     * on the specified host.  It must register the dispatcher under the SessionId
     * @param jndiHostURL This is the URL that will be used to register the synchronization service
     * not used
     */
    public void registerDispatcher() {
        //not used
    }

    /**
     * ADVANCED:
     * This method will deregister the dispatcher for this session from JNDI
     * on the specified host.  It must deregister the dispatcher under the SessionId
     * @param jndiHostURL This is the URL that will be used to register the synchronization service
     * not used
     */
    public void deregisterDispatcher() {
        //BUG 2700381: deregister 
        //not used
    }

    /**
     * ADVANCED:
     *      Returns the socket that will be used for the multicast communication.
     * By default this will be java.net.MulticastSocket
     * Not Used.
     * @SBGen Method get communicationSocket
     */
    public MulticastSocket getCommunicationSocket() {
        //not used
        return null;
    }

    /**
     * ADVANCED:
     * This is the object that will be placed in JNDI to provide remote synchronization services
     * Not Used.
     * @SBGen Method get dispatcher
     */
    public Object getDispatcher() throws java.rmi.RemoteException {
        //Not used
        return null;
    }

    /**
     * PUBLIC:
     * Use this method to get the Connection Factory Name for the JMS Topic connections
     */
    public String getTopicConnectionFactoryName() {
        if (this.connectionFactoryName == null) {
            return "";
        }
        return this.connectionFactoryName;
    }

    /**
     * PUBLIC:
     * return the JMS Topic name for the Topic that this clustering service will be connecting to.
     */
    public String getTopicName() {
        return this.topicName;
    }

    /**
     * INTERNAL:
     * Initializes the clustering service and starts again
     */
    public void initialize() {
        this.stopListening();
        try {
            Thread.sleep(500);
            //ensure that the listening socket stops
        } catch (InterruptedException exception) {
        }
        this.localContext = null;
        this.communicationSocket = null;
        this.start();
    }

    /**
     * This is the main execution method of this class.  It will create a socket to listen to and
     * register the dispatcher for this class in JNDI
     */
    public void run() {
        setSessionId(buildSessionId());
        if (getTopicName() == null) {
            throw JMSProcessingException.noTopicSet(null);
        }
        retreiveRemoteSessions();

    }

    /**
     * ADVANCED:
     * This method should return a Remote Connection of the appropriate type that references the
     * Remote dispatcher for this Session
     * NOT USED
     */
    public RemoteConnection getLocalRemoteConnection() {
        //not used
        return null;
    }

    /**
     * INTERNAL:
     * Use this method to notify the SynchronizationManager that we have to connect to a new Session
     * that has just joined the network
     */
    public void retreiveRemoteSessions() {
        try {
            RemoteConnection newConnection = createRemoteConnection();
            getSession().getCacheSynchronizationManager().addRemoteConnection(newConnection);
        } catch (NamingException exception) {
            getSession().handleException(SynchronizationException.errorLookingUpJMSService(getTopicName(), exception));
        } catch (JMSException exception) {
            getSession().handleException(SynchronizationException.errorLookingUpJMSService(getTopicName(), exception));
        }
    }

    /**
     * PUBLIC:
     * Use this method to set the Connection Factory Name for the JMS Topic connections
     */
    public void setTopicConnectionFactoryName(String connectionFactoryName) {
        this.connectionFactoryName = connectionFactoryName;
    }

    /**
     * PUBLIC:
     * sets the JMS Topic name for the Topic that this clustering service will be connecting to.
     * This is a required setting and must be set.
     */
    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    /**
     * ADVANCED:
     * Uses to stop the Listener thread for a dropped session
     */
    public void stopListening() {
        this.stopListening = true;
        try {
            if (this.listeningConnection != null) {
                this.listeningConnection.close();
            }
        } catch (JMSException exception) {
            ((org.eclipse.persistence.internal.sessions.AbstractSession)getSession()).log(SessionLog.WARNING, SessionLog.PROPAGATION, "exception_thrown_when_attempting_to_close_listening_topic_connection", exception);
        }
    }
}