/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.sessions.remote.rmi;

import org.eclipse.persistence.internal.sessions.remote.RemoteConnection;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.exceptions.*;
import java.io.IOException;
import java.net.MulticastSocket;
import java.net.InetAddress;
import java.util.Hashtable;
import javax.naming.*;

/**
 * <p>
 * <b>PURPOSE</b>:To Provide a framework for offering customers the ability to automatically
 * connect multiple sessions for synchrnization.</p>
 * <p>
 * <b>Descripton</b>:This thread object will place a remote dispatcher in the specified JNDI space.
 * it will also monitor the specified multicast socket to allow other sessions to connect.  This
 * Particular class has been configured to use the RMI transport protocols.  This class also assumes that
 * there is a JNDI service available.  And is best used within an application server.</p>
 *
 * @author Gordon Yorke
 * @see org.eclipse.persistence.sessions.remote.CacheSynchronizationManager
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.sessions.coordination.rmi.RMITransportManager}
 */
public class RMIJNDIClusteringService extends org.eclipse.persistence.sessions.remote.AbstractJNDIClusteringService {
    private static Class DEFAULT_DISPATCHER_CLASS = org.eclipse.persistence.sessions.remote.rmi.RMIRemoteSessionControllerDispatcher.class;
    private static Class DEFAULT_CONNECTION_CLASS = org.eclipse.persistence.sessions.remote.rmi.RMIConnection.class;

    /**
     * ADVANCED:
     *     Creates a RMIJNDIClusteringService
     * @param multicastAddress The address of the multicast group
     * @param multicastPort The port the multicast group is listening on
     * @SBGen Constructor
     */
    public RMIJNDIClusteringService(String multicastAddress, int multicastPort, Session session) {
        super(multicastAddress, multicastPort, session);
    }

    /**
     * PUBLIC:
     * Creates a RMIJNDIClusteringService
     * @SBGen Constructor
     */
    public RMIJNDIClusteringService(Session session) {
        super(session);
    }

    /**
     * ADVANCED:
     * This method will register the dispatcher for this session in JNDI
     * on the specified host.  It must register the dispatcher under the SessionId
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
     */
    public void deregisterDispatcher() {
        //BUG 2700381: deregister from JNDI
        try {
            getLocalContext().unbind(getSessionId());
            RMIRemoteSessionControllerDispatcher dispatcher = (RMIRemoteSessionControllerDispatcher)getDispatcher();
            if (dispatcher != null) {
                RMIRemoteSessionControllerDispatcher.unexportObject(dispatcher, true);
            }
        } catch (Exception exception) {
            getSession().handleException(SynchronizationException.errorBindingController(getSessionId(), exception));
        }
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
            return new RMIConnection((RMIRemoteSessionController)getContext(properties).lookup(sessionId));
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
     * This is the object that will be placed in JNDI to provide remote synchronization services
     * @SBGen Method get dispatcher
     */
    public Object getDispatcher() throws java.rmi.RemoteException {
        if (this.dispatcher == null) {
            this.dispatcher = new RMIRemoteSessionControllerDispatcher(getSession());
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
            RemoteConnection connection = new RMIConnection((RMIRemoteSessionControllerDispatcher)getDispatcher());
            connection.setServiceName(getSessionId());
            return connection;
        } catch (Exception exception) {
            return null;
        }
    }
}