/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.sessions.remote;

import java.security.AccessController;
import java.util.*;
import java.lang.reflect.Constructor;
import org.eclipse.persistence.internal.sessions.remote.*;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.security.PrivilegedGetConstructorFor;
import org.eclipse.persistence.internal.security.PrivilegedInvokeConstructor;
import org.eclipse.persistence.internal.sessions.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.logging.SessionLog;

/**
 * <b>Purpose</b>: Allow for a cluster or group of application servers or TopLink sessions to distributely broadcast
 * cache changes to the other servers to synchronize the state of the cache object.
 * <p><b>Description</b>: This allows for applications that require multiple application servers to decrease the possibilty of
 * stale data in the TopLink cache.
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.sessions.coordination.CommandManager}
 */
public class CacheSynchronizationManager {

    /** This attribute is used to store the list of RemoteConnections that changes should be sent to */
    protected Hashtable remoteConnections;

    /** @DEPRECATED This attribute holds the remoteSessionControllerDispatcher that this Session is making available to other sessions for synchronization */
    protected Object remoteSessionController;

    /** This attribute holds the remoteSessionControllerDispatcher that this Session is making available to other sessions for synchronization */
    protected RemoteConnection remoteSessionConnection;

    /** This attribute holds a reference back to the session */
    protected Session session;

    /** This attribute flag is used to determine if the propigation should be synchronous or asynchronous */
    protected boolean isAsynchronous;

    /** This attribute designates if conects to remote servers should be removed if an error occurs
     * The default is true
     */
    protected boolean shouldRemoveConnectionOnError;

    /**
     * The URL of the JNDI host where the Dispatcher will be placed
     */
    protected String localHostURL;

    /**
      * This attribute holds the reference to the SynchronizationService
      */
    protected Class clusteringServiceClassType;

    /**
     *  This attribute holds the Synchronization Service
     */
    protected AbstractClusteringService clusteringService;

    /**
     * PUBLIC:
     * Creates a CacheSynchronizationManager.
     */
    public CacheSynchronizationManager() {
        this.remoteConnections = new Hashtable(3);
        this.isAsynchronous = true;
        this.shouldRemoveConnectionOnError = true;
    }

    /**
     * PUBLIC:
     * Creates a CacheSynchronizationManager.
     * @param clusteringServiceClassType the class that will be instantiated to connect all nodes
     */
    public CacheSynchronizationManager(Class clusteringServiceClassType) {
        this.remoteConnections = new Hashtable(3);
        this.isAsynchronous = true;
        this.shouldRemoveConnectionOnError = true;
        this.clusteringServiceClassType = clusteringServiceClassType;
    }

    /**
     * PUBLIC:
     * Creates a CacheSynchronizationManager.
     * @deprecated As of version 4.0
     * @param controller This must be a globally available RemoteSessionController
     */
    public CacheSynchronizationManager(Object controller) {
        this.remoteSessionController = controller;
        this.remoteConnections = new Hashtable(3);
        this.isAsynchronous = true;
        this.shouldRemoveConnectionOnError = true;
    }

    /**
     * ADVANCED:
     * THis method is called during the Login of the session to set up the synchronization service
     */
    public void initialize() {
        //If there is no synchronization service then skip
        if ((this.clusteringServiceClassType == null) && (this.clusteringService == null)) {
            return;
        }
        getRemoteConnections().clear();
        try {
            Class[] params = new Class[1];
            params[0] = Session.class;
            Object[] args = new Object[1];
            args[0] = getSession();
            if (this.clusteringService == null) {
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    Constructor constructor = (Constructor)AccessController.doPrivileged(new PrivilegedGetConstructorFor(clusteringServiceClassType, params, false));
                    this.clusteringService = (AbstractClusteringService)AccessController.doPrivileged(new PrivilegedInvokeConstructor(constructor, args));
                }else{
                    Constructor constructor = PrivilegedAccessHelper.getConstructorFor(clusteringServiceClassType, params, false);
                    this.clusteringService = (AbstractClusteringService)PrivilegedAccessHelper.invokeConstructor(constructor, args);
                }
            }
            if ((this.localHostURL != null) && (this.clusteringService.getLocalHostURL() == null)) {
                this.clusteringService.setLocalHostURL(this.localHostURL);
            }
            this.clusteringService.initialize();
        } catch (Exception exception) {
            throw SynchronizationException.errorGettingSyncService(exception);
        }
    }

    /**
     * INTERNAL:
     * Get the name of the clusteringServiceClassType as a String.
     * Used for persisting.
     */
    public String getclusteringServiceClassTypeName() {
        return getClusteringServiceClassType().getName();
    }

    /**
     *INTERNAL:
     * Given the name of a class, set my clusteringServiceClassType
     * to point at that class.
     * Used for persisting
     */
    public void setClusteringServiceClassTypeName(String aClassName) {
        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                this.setClusteringServiceClassType((Class)AccessController.doPrivileged(new PrivilegedClassForName(aClassName)));
            }else{
                this.setClusteringServiceClassType(PrivilegedAccessHelper.getClassForName(aClassName));
            }
        } catch (Exception exception) {
            System.out.println("Error setting clusteringServiceClassType " + exception);
        }
    }

    /**
     * PUBLIC:
     * Add a remote Connection for cache synchronisation.
     * This connection will be updated with the changes that occur to this session's cache.
     */
    public void addRemoteConnection(RemoteConnection connection) {
        if (connection == null) {
            return;
        }
        if (connection.getServiceName() == "") {
            connection.setServiceName(String.valueOf(System.identityHashCode(connection)));
        }
        try {
            if (getClusteringService() == null) {
                //This block of code is here for backward compatability.  This 'handshaking' should
                //be done by the ClusteringService.
                if (getSessionRemoteController() != null) {
                    connection.addRemoteControllerForSynchronization(getSessionRemoteController());
                } else {
                    ConnectToSessionCommand command = new ConnectToSessionCommand();
                    command.setRemoteConnection(getSessionRemoteConnection());
                    connection.processCommand(command);
                }
            } else {
                getClusteringService().connectBackToRemote(connection);
            }
            getRemoteConnections().put(connection.getServiceName(), connection);
            ((org.eclipse.persistence.internal.sessions.AbstractSession)getSession()).log(SessionLog.FINEST, SessionLog.PROPAGATION, "received_connection_from", connection.getServiceName());

        } catch (Exception exception) {
            try {
                getSession().handleException(CommunicationException.errorSendingConnectionService(connection.getServiceName(), exception));
            } catch (RuntimeException reThrownException) {
                ((org.eclipse.persistence.internal.sessions.AbstractSession)getSession()).log(SessionLog.WARNING, SessionLog.PROPAGATION, "problem_adding_remote_connection", reThrownException);
                if (!shouldRemoveConnectionOnError()) {
                    //if the user does not care then just throw away this connection
                    // otherwise
                    throw reThrownException;
                }
            }
        }
    }

    /**
     * PUBLIC:
     * Connect to all known servers, that have been specified through addRemoteConnections.
     * This notifies the other servers that this server is alive and desires it's cache to be
     * synchronized with their caches.
     * The other server will then send all committed changes back through the remote connection to this server.
     * Note: any errors that occur will be ignored and this will continue to connect to the other servers,
     * these errors will be logged to the session's log.
     */
    public void connectToAllRemoteServers() {
        Enumeration distributedSessionsEnum = getRemoteConnections().elements();
        while (distributedSessionsEnum.hasMoreElements()) {
            RemoteConnection connection = (RemoteConnection)distributedSessionsEnum.nextElement();
            try {
                connection.addRemoteControllerForSynchronization(getSessionRemoteController());
            } catch (Exception exception) {
                getSession().handleException(CommunicationException.unableToConnect(connection.getServiceName(), exception));
            }
        }
    }

    /**
     * PUBLIC:
     * Returns the list of remote connections for cache synchronisation.
     */
    public Hashtable getRemoteConnections() {
        return remoteConnections;
    }

    /**
     * INTERNAL:
     * This method returns the session referenced by this policy
     */
    public Session getSession() {
        return session;
    }

    /**
     * ADVANCED:
     * Returns the remoteSession controller dispatcher that this
     * session has made available for synchronization.
     * @deprecated Since 4.0
     */
    public Object getSessionRemoteController() {
        return remoteSessionController;
    }

    /**
     * ADVANCED:
     * Returns the remoteSession controller dispatcher that this
     * session has made available for synchronization.
     */
    public RemoteConnection getSessionRemoteConnection() {
        return remoteSessionConnection;
    }

    /**
     * INTERNAL:
     * Propigate the changes to distributed caches.
     */
    public void propagateChanges(UnitOfWorkChangeSet changeSet) {
        Vector errors = new Vector(4);
        if (!changeSet.hasChanges()) {
            return;
        }
        ((org.eclipse.persistence.internal.sessions.AbstractSession)getSession()).log(SessionLog.FINEST, SessionLog.PROPAGATION, "sending_changeset_to_network");
        SynchronizeCacheCommand command = new SynchronizeCacheCommand();
        command.setChangeSet(changeSet);
        command.setSourceSessionId(getClusteringService().getSessionId());

        if (isAsynchronous) {
            ChangeSetPropagator propagator = new ChangeSetPropagator(this, command);
            propagator.start();
            return;
        }

        Enumeration distributedSessions = getRemoteConnections().elements();
        while (distributedSessions.hasMoreElements()) {
            RemoteConnection connection = (RemoteConnection)distributedSessions.nextElement();
            try {
                connection.processCommand(command);
            } catch (Exception exception) {
                try {
                    RemoteConnection newRemoteConnection = getClusteringService().reconnect(connection);
                    addRemoteConnection(newRemoteConnection);
                    connection.processCommand(command);
                } catch (Exception exception2) {
                    ((org.eclipse.persistence.internal.sessions.AbstractSession)getSession()).log(SessionLog.FINER, SessionLog.PROPAGATION, "failed_to_reconnect_remote_connection");
                    try {
                        getSession().handleException(CacheSynchCommunicationException.failedToReconnect(connection.getServiceName(), exception2));
                    } catch (CacheSynchCommunicationException commException) {
                        errors.addElement(commException);
                    }
                    if (shouldRemoveConnectionOnError()) {
                        removeRemoteConnection(connection);
                        ((org.eclipse.persistence.internal.sessions.AbstractSession)getSession()).log(SessionLog.FINER, SessionLog.PROPAGATION, "dropping_connection", connection.getServiceName());
                    }
                    try {
                        getSession().handleException(CacheSynchCommunicationException.unableToPropagateChanges(connection.getServiceName(), exception2));
                        getSession().handleException(CommunicationException.unableToPropagateChanges(connection.getServiceName(), exception));
                    } catch (CacheSynchCommunicationException cacheSynchCommException) {
                        errors.addElement(cacheSynchCommException);
                    } catch (CommunicationException commException) {
                        errors.addElement(commException);
                    }
                }
            }
        }
        if (!errors.isEmpty()) {
            throw new CacheSynchronizationException(errors, changeSet);
        }
    }

    /**
     * PUBLIC:
     * Remove all remote connections for cache synchronisation.
     * All remote connections will no longer be updated with the changes
     */
    public void removeAllRemoteConnections() {
        this.remoteConnections = new Hashtable(3);
    }

    /**
     * PUBLIC:
     * Remove a remote connection for cache synchronisation.
     * This connection will no longer be updated with the changes.
     */
    public void removeRemoteConnection(RemoteConnection connection) {
        synchronized (getRemoteConnections()) {
            getRemoteConnections().remove(connection.getServiceName());
        }
    }

    /**
     * ADVANCED:
     * This method is used to set if the propagation of the change sets should be asynchronous.
     * This means that the client will return from the commit before the changes may have been
     * propigated.
     */
    public void setIsAsynchronous(boolean isAsynchronous) {
        this.isAsynchronous = isAsynchronous;
    }

    /**
     * PUBLIC:
     *  Return whether the propagation of the change sets should be asynchronous.
     *  Asynchronous propagation means that the client will return from the commit before the changes may have been
     *  propigated.
     *  @return boolean
     */
    public boolean isAsynchronous() {
        return this.isAsynchronous;
    }

    /**
     * INTERNAL:
     * Set the session.
     */
    public void setSession(Session session) {
        this.session = session;
    }

    /**
     * ADVANCED:
     * Sets the remoteSession controller dispatcher that this
     * session has made available for synchronization.
     * @deprecated As of version 4.0
     */
    public void setSessionRemoteController(Object remoteSessionController) {
        this.remoteSessionController = remoteSessionController;
    }

    /**
     * ADVANCED:
     * Sets the remoteSession connection that this
     * session will make available for synchronization.  Other session will communicate with
     * this session through this connection
     */
    public void setSessionRemoteConnection(RemoteConnection remoteSessionConnection) {
        this.remoteSessionConnection = remoteSessionConnection;
    }

    /**
     * PUBLIC:
     * Allow whether connections to remote servers should be disconnected when an error occurs
     */
    public void setShouldRemoveConnectionOnError(boolean shouldRemoveConnectionOnError) {
        this.shouldRemoveConnectionOnError = shouldRemoveConnectionOnError;
    }

    /**
     * PUBLIC:
     * Return whether connections to remote servers should be disconnected when an error occurs
     */
    public boolean shouldRemoveConnectionOnError() {
        return shouldRemoveConnectionOnError;
    }

    /**
     * INTERNAL:
     * Used to return the running synchronization service
     * @SBGen Method get clusteringService
     */
    public AbstractClusteringService getClusteringService() {
        // SBgen: Get variable
        return (clusteringService);
    }

    /**
     * ADVANCED:
     * Use this method to set the clusteringService used to synchronize the distributed sessions
     * The class supplied must subclass AbstractClusteringService
     * @param clusteringService
     * @SBGen Method set clusteringService
     */
    public void setClusteringService(AbstractClusteringService clusteringService) {
        // SBgen: Assign variable
        this.clusteringService = clusteringService;

        // CR 3565
        if (clusteringService != null) {
            this.setClusteringServiceClassType(clusteringService.getClass());

            // For bug 2700794 a user can still set the localHostURL on the
            // receiver, even though this.setLocalHostURL(...) is deprecated. 
            if ((this.localHostURL != null) && (getClusteringService().getLocalHostURL() == null)) {
                setLocalHostURL(this.localHostURL);
            }
        }
    }

    /**
     * ADVANCED:
     * Use this method to set the class type to be used to synchronize the distributed sessions
     * The class supplied must subclass AbstractClusteringService
     * @param clusteringServiceClassType
     * @SBGen Method set clusteringServiceClassType
     */
    public void setClusteringServiceClassType(Class clusteringServiceClassType) {
        // SBgen: Assign variable
        this.clusteringServiceClassType = clusteringServiceClassType;
    }

    /**
     * INTERNAL:
     * Return the class type used ot automatically synchronize distributed sessions
     * @SBGen Method get clusteringServiceClassType
     */
    public Class getClusteringServiceClassType() {
        // SBgen: Get variable
        return (clusteringServiceClassType);
    }

    /**
     * This method will return the Host URL of the JNDI service for this node
     * @SBGen Method get jndiHostURL
     * @deprecated since 4.5 now set on ClusteringService
     */
    public String getLocalHostURL() {
        // SBgen: Get variable
        if (getClusteringService() != null) {
            return getClusteringService().getLocalHostURL();
        }
        return (localHostURL);
    }

    /**
     * This method will set the Host address of the JNDI service
     * @param jndiHostURL
     * @SBGen Method set jndiHostURL
     * @deprecated since 4.5 now set on ClusteringService
     */
    public void setLocalHostURL(String localHostURL) {
        // SBgen: Assign variable
        if (getClusteringService() != null) {
            getClusteringService().setLocalHostURL(localHostURL);
        } else {
            this.localHostURL = localHostURL;
        }
    }

    /**
     * INTERNAL:
     * This method is used to Stop Listening to the remote services.  This should only happen
     * when the session is logged out
     */
    public void stopListening() {
        if (this.getClusteringService() != null) {
            this.getClusteringService().stopListening();
            //BUG 2700381: deregister from JNDI
            this.getClusteringService().deregisterDispatcher();
        }
        this.getRemoteConnections().clear();
    }
}