/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.sessions.remote.corba.sun;

import java.io.IOException;
import java.net.MulticastSocket;
import java.net.InetAddress;
import java.util.Hashtable;
import javax.naming.*;
import org.eclipse.persistence.internal.sessions.remote.RemoteConnection;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.exceptions.*;

/**
 * <p>
 * <b>PURPOSE</b>:To Provide a framework for offering customers the ability to automatically
 * connect multiple sessions for synchrnization.
 * <p>
 * <b>Descripton</b>:This thread object will place a remote dispatcher in the specified JNDI space.
 * it will also monitor the specified multicast socket to allow other sessions to connect.  This
 * Particular class has been configured to use the RMI transport protocols.  This class also assumes that
 * there is a JNDI service available.  And is best used within an application server.
 *
 * @author Gordon Yorke
 * @see org.eclipse.persistence.sessions.remote.CacheSynchronizationManager
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.sessions.coordination.corba.sun.SunCORBATransportManager}
 */
public class CORBAJNDIClusteringService extends org.eclipse.persistence.sessions.remote.AbstractJNDIClusteringService {
    private static Class DEFAULT_DISPATCHER_CLASS = org.eclipse.persistence.sessions.remote.corba.sun.CORBARemoteSessionControllerDispatcher.class;
    private static Class DEFAULT_CONNECTION_CLASS = org.eclipse.persistence.sessions.remote.corba.sun.CORBAConnection.class;

    /**
     * ADVANCED:
     *     Creates a CORBAJNDIClusteringService
     * @param multicastAddress The address of the multicast group
     * @param multicastPort The port the multicast group is listening on
     * @param jndiHostURL The URL of the JNDI service
     */
    public CORBAJNDIClusteringService(String multicastAddress, int multicastPort, Session session) {
        super(multicastAddress, multicastPort, session);
    }

    /**
     * PUBLIC:
     * Creates a CORBAJNDIClusteringService
     * @param jndiHostURL The URL of the JNDI service
     * @SBGen Constructor
     */
    public CORBAJNDIClusteringService(Session session) {
        super(session);
    }

    /**
     * ADVANCED:
     * This method will register the dispatcher for this session in JNDI
     * on the specified host.  It must register the dispatcher under the SessionId
     * @param jndiHostURL This is the URL that will be used to register the synchronization service
     */
    public void registerDispatcher() {
        try {
            getLocalContext().rebind(getSessionId(), getDispatcher());
        } catch (Exception exception) {
            getSession().handleException(SynchronizationException.errorBindingController(getSessionId(), exception));
        }
    }

    /**
     * ADVANCED:
     * This method will deregister the dispatcher for this session from JNDI
     * on the specified host.  It must deregister the dispatcher under the SessionId
     * @param jndiHostURL This is the URL that will be used to register the synchronization service
     */
    public void deregisterDispatcher() {
        //BUG 2700381: deregister from JNDI
        try {
            getLocalContext().unbind(getSessionId());
        } catch (Exception exception) {
            getSession().handleException(SynchronizationException.errorBindingController(getSessionId(), exception));
        }
    }

    /**
     * INTERNAL:
     * THis method is called by the cache synchronization manager when this server should
     * connect back ('handshake') to the server from which this remote connection came.
     * Passing whole connections back does not work with Corba, so instead we pass the information
     * necessary to recreate the connection and then recreate it when the command arrives.
     */
    public void connectBackToRemote(RemoteConnection connection) throws Exception {
        org.eclipse.persistence.internal.sessions.remote.ConnectToJNDISessionCommand command = new org.eclipse.persistence.internal.sessions.remote.ConnectToJNDISessionCommand();
        command.setSessionId(getSessionId());
        command.setJndiURL((String)getInitialContextProperties().get(Context.PROVIDER_URL));
        connection.processCommand(command);
    }

    /**
     * ADVANCED:
     *     This method should return a remote connection of the appropraite type for
     * use in the synchronizatio
     */
    public RemoteConnection createRemoteConnection(String sessionId, String jndiHostURL) {
        try {
            Hashtable properties = (Hashtable)getInitialContextProperties().clone();
            properties.put(Context.PROVIDER_URL, jndiHostURL);
            return new CORBAConnection((CORBARemoteSessionController)getContext(properties).lookup(sessionId));
        } catch (NamingException exception) {
            getSession().handleException(SynchronizationException.errorLookingUpController(sessionId, exception));
        }
        return null;
    }

    /**
     * ADVANCED:
     *      Returns the socket that will be used for the multicast communication.
     * By default this will be java.net.MulticastSocket
     * @SBGen Method get communicationSocket
     */
    public MulticastSocket getCommunicationSocket() {
        if (this.communicationSocket == null) {
            try {
                this.communicationSocket = new MulticastSocket(getMulticastPort());
                this.communicationSocket.setTimeToLive(getTimeToLive());
                this.communicationSocket.joinGroup(InetAddress.getByName(getMulticastGroupAddress()));
            } catch (IOException ex) {
                SynchronizationException topException = SynchronizationException.errorJoiningMulticastGroup(ex);
                getSession().handleException(topException);
            }
        }
        return this.communicationSocket;
    }

    /**
     * ADVANCED:
     * Returns the active JNDI Context to store the remote service in
     * @return
     * @deprecated Since 4.5
     * @SBGen Method get context
     */
    public Context getContext(String jndiHostURL) {
        try {
            Hashtable properties = new Hashtable(2);
            properties.put(Context.PROVIDER_URL, jndiHostURL);
            if (getUserName() != null) {
                properties.put(Context.SECURITY_PRINCIPAL, getUserName());
            }
            if (getPassword() != null) {
                properties.put(Context.SECURITY_CREDENTIALS, getPassword());
            }
            return new javax.naming.InitialContext(properties);
        } catch (NamingException exception) {
            getSession().handleException(SynchronizationException.errorLookingUpController(jndiHostURL, exception));
        }
        return null;
    }

    /**
     * ADVANCED:
     * This is the object that will be placed in JNDI to provide remote synchronization services
     * @return
     * @SBGen Method get dispatcher
     */
    public Object getDispatcher() throws java.rmi.RemoteException {
        if (this.dispatcher == null) {
            this.dispatcher = new CORBARemoteSessionControllerDispatcher(getSession());
        }
        return this.dispatcher;
    }

    /**
     * ADVANCED:
     * This method should return a Remote Connection of the appropriate type that references the
     * Remote dispatcher for this Session
     */
    public RemoteConnection getLocalRemoteConnection() {
        try {
            RemoteConnection connection = new CORBAConnection((CORBARemoteSessionControllerDispatcher)getDispatcher());
            connection.setServiceName(getSessionId());
            return connection;
        } catch (Exception exception) {
            return null;
        }
    }
}