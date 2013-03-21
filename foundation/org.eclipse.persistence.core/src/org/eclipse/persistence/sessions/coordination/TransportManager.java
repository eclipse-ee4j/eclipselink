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
 *     17/10/2008-1.1  Michael O'Brien 
 *       - 251005: The default JNDI InitialContextFactory is modified from
 *       OC4J: oracle.j2ee.rmi.RMIInitialContextFactory to
 *       WebLogic: weblogic.jndi.WLInitialContextFactory
 ******************************************************************************/  
package org.eclipse.persistence.sessions.coordination;

import java.util.Hashtable;
import java.util.Enumeration;
import javax.naming.Context;
import javax.naming.NamingException;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.sessions.coordination.ConnectToHostCommand;
import org.eclipse.persistence.internal.sessions.coordination.RemoteConnection;
import org.eclipse.persistence.internal.security.SecurableObjectHolder;

/**
 * <p>
 * <b>Purpose</b>: Provide an abstract class that offers a common API to handling
 * remote command connections.
 * <p>
 * <b>Description</b>: This class manages the remote connections to other RCM service
 * instances and posts the local connection to this service instance in a name
 * service so that other RCM service instances can connect to it.
 * <p>
 * @author Steven Vo
 * @since OracleAS TopLink 10<i>g</i> (9.0.4)
 */
public abstract class TransportManager {

    /** The remote command connection to this transport */
    protected RemoteConnection localConnection;

    /** The RCM that manages this transport */
    protected RemoteCommandManager rcm;

    /** The type of naming service used to look up other connections */
    protected int namingServiceType;

    /** Properties to obtain the context used for local JNDI access */
    protected Hashtable localContextProperties;

    /** Properties to obtain the context used for remote JNDI access */
    protected Hashtable remoteContextProperties;

    /** Determines whether a connection should be discarded if an error occurs on it */
    protected boolean shouldRemoveConnectionOnError;

    /** Connections to other services */
    protected Hashtable connectionsToExternalServices;

    /** Security util that is used to decrypt and encrypt password */
    SecurableObjectHolder securableObjectHolder;
    public static final boolean DEFAULT_REMOVE_CONNECTION_ON_ERROR_MODE = true;

    /** Valid values for naming service type */
    public static final int JNDI_NAMING_SERVICE = 0;
    public static final int REGISTRY_NAMING_SERVICE = 1;

    /** Defaults for RMI applications */
    public static final String DEFAULT_URL_PROTOCOL = "rmi";
    public static final String DEFAULT_IIOP_URL_PROTOCOL = "corbaname";
    public static final String DEFAULT_URL_PORT = "23791";
    public static final String DEFAULT_IIOP_URL_PORT = "5555#";
    public static final int DEFAULT_NAMING_SERVICE = JNDI_NAMING_SERVICE;

    /** Default JNDI properties for remote access */
    public static final String DEFAULT_CONTEXT_FACTORY = "weblogic.jndi.WLInitialContextFactory";
    public static final String DEFAULT_DEDICATED_CONNECTION_KEY = "dedicated.connection";
    public static final String DEFAULT_DEDICATED_CONNECTION_VALUE = "true";
    public static final String DEFAULT_USER_NAME = "admin";

    /**
     * INTERNAL:
     * Return a remote connection to the specified service
     */
    public abstract RemoteConnection createConnection(ServiceId serviceId);

    /**
     * INTERNAL:
     * Does nothing by default. In case TransportManager doesn't use DiscoveryManager
     * (createDiscoveryManager method retuns null)
     * this method called during RCM initialization to create all the necessary connections.
     * TransportManager ancestors that override createDiscoveryManager method to return null
     * must override this method, too.
     */
    public void createConnections() {
    }

    /**
     * INTERNAL:
     * This method is called by the remote command manager when this service should
     * connect back ('handshake') to the service from which this remote connection came.
     */
    public void connectBackToRemote(RemoteConnection connection) throws Exception {
        ConnectToHostCommand command = new ConnectToHostCommand();        
        command.setServiceId(rcm.getServiceId());
        connection.executeCommand(command);
    }

    /**
     * INTERNAL:
     * Return a remote connection to this service
     */
    public RemoteConnection getConnectionToLocalHost() {
        return localConnection;
    }

    /**
     * INTERNAL:
     * Put the remote connection to local host in naming service and return the  of the created remote connection
     */
    public abstract void createLocalConnection();

    /**
     * PUBLIC:
     * Return the type of naming service used to look up remote connections to other
     * service instances.
     *
     * @return The type of naming service used.
     */
    public int getNamingServiceType() {
        return namingServiceType;
    }

    /**
     * ADVANCED:
     * Set the type of naming service used to look up remote connections to other
     * service instances. The service type must be one of JNDI_NAMING_SERVICE or
     * REGISTRY_NAMING_SERVICE.
     */
    public void setNamingServiceType(int serviceType) {
        namingServiceType = serviceType;
    }

    /**
     * PUBLIC:
     * Return the user name used as the value to the SECURITY_PRINCIPAL key in the
     * cached context properties.
     */
    public String getUserName() {
        return (String)getRemoteContextProperties().get(Context.SECURITY_PRINCIPAL);
    }

    /**
     * ADVANCED:
     * Set the user name used as the value to the SECURITY_PRINCIPAL key in the
     * cached context properties.
     */
    public void setUserName(String userName) {
        getRemoteContextProperties().put(Context.SECURITY_PRINCIPAL, userName);
    }

    /**
     * PUBLIC:
     * Return the password used as the value to the SECURITY_CREDENTIALS key in 
     * the cached context properties.
     */
    public String getPassword() {
        return (String) decrypt(getEncryptedPassword());
    }
    
    /**
     * PUBLIC:
     * Return the encrypted (assumed) password used as the value to the 
     * SECURITY_CREDENTIALS key in the cached context properties.
     */
    public String getEncryptedPassword() {
        return (String) getRemoteContextProperties().get(Context.SECURITY_CREDENTIALS);
    }

    /**
     * ADVANCED:
     * Set the password used as the value to the SECURITY_CREDENTIALS key in the
     * cached context properties.
     */
    public void setPassword(String password) {
        if (password != null) {
            getRemoteContextProperties().put(Context.SECURITY_CREDENTIALS, encrypt(password));
        }
    }

    /**
     * ADVANCED:
     * Set the encrypted password used as the value to the SECURITY_CREDENTIALS key in the
     * cached context properties.
     */
    public void setEncryptedPassword(String encryptedPassword) {
        getRemoteContextProperties().put(Context.SECURITY_CREDENTIALS, encryptedPassword);
    }

    /**
     * PUBLIC:
     * Return the context factory name used as the value to the INITIAL_CONTEXT_FACTORY
     * key in the cached context properties.
     */
    public String getInitialContextFactoryName() {
        return (String)getRemoteContextProperties().get(Context.INITIAL_CONTEXT_FACTORY);
    }

    /**
     * ADVANCED:
     * Set the context factory name used as the value to the INITIAL_CONTEXT_FACTORY
     * key in the cached context properties.
     */
    public void setInitialContextFactoryName(String contextFactoryName) {
        getRemoteContextProperties().put(Context.INITIAL_CONTEXT_FACTORY, contextFactoryName);
    }

    /**
     * INTERNAL:
     * Helper method to get a naming context.
     */
    public Context getContext(Hashtable contextProperties) {
        try {
            return new javax.naming.InitialContext(contextProperties);
        } catch (NamingException exception) {
            RemoteCommandManagerException rcmException = RemoteCommandManagerException.errorObtainingContext(exception);
            rcm.handleException(RemoteCommandManagerException.errorObtainingContext(exception));
            throw rcmException;
        }
    }

    /**
     * ADVANCED:
     * Return the cached properties that will be used to create the initial context
     * when doing remote JNDI lookups.
     */
    public Hashtable getRemoteContextProperties() {
        return remoteContextProperties;
    }

    /**
     * ADVANCED:
     * Set the cached properties that will be used to create the initial context
     * when doing remote JNDI lookups.
     */
    public void setRemoteContextProperties(Hashtable properties) {
        remoteContextProperties = properties;
    }

    /**
     * ADVANCED:
     * Return the properties that will be used to create the initial context
     * for local JNDI access.
     */
    public Hashtable getLocalContextProperties() {
        if (localContextProperties == null) {
            localContextProperties = new Hashtable();
        }
        return localContextProperties;
    }

    /**
     * ADVANCED:
     * Set the properties that will be used to create the initial context
     * for local JNDI access.
     */
    public void setLocalContextProperties(Hashtable properties) {
        localContextProperties = properties;
    }

    /**
     * INTERNAL:
     */
    public RemoteCommandManager getRemoteCommandManager() {
        return rcm;
    }

    /**
     * INTERNAL:
     * Add a remote Connection to a remote service.
     */
    public void addConnectionToExternalService(RemoteConnection connection) {
        if (connection == null) {
            return;
        }
        try {
            connectBackToRemote(connection);
            connectionsToExternalServices.put(connection.getServiceId().getId(), connection);
            Object[] args = { connection.getServiceId() };
            rcm.logDebug("received_connection_from", args);
        } catch (Exception exception) {
            try {
                rcm.handleException(CommunicationException.errorSendingConnectionService(connection.toString(), exception));
            } catch (RuntimeException reThrownException) {
                Object[] args = { connection.getServiceId(), reThrownException };
                rcm.logWarning("problem_adding_connection", args);
                if (!shouldRemoveConnectionOnError) {
                    throw reThrownException;
                }
            }
        }
    }

    /**
     * INTERNAL:
     * Remove a remote connection from the list of connections to receive remote commands.
     */
    public void removeConnectionToExternalService(RemoteConnection connection) {
        synchronized (connectionsToExternalServices) {
            connectionsToExternalServices.remove(connection.getServiceId().getId());
            connection.close();
        }
    }

    /**
     * INTERNAL:
     * Remove all remote connections from the list.
     */
    public void removeAllConnectionsToExternalServices() {
        synchronized (connectionsToExternalServices) {
            Enumeration connections = connectionsToExternalServices.elements();
            connectionsToExternalServices = new Hashtable(3);

            while (connections.hasMoreElements()) {
                ((RemoteConnection)connections.nextElement()).close();
            }
        }
    }

    /**
     * INTERNAL:
     */
    public Hashtable getConnectionsToExternalServices() {
        return connectionsToExternalServices;
    }

    /**
     * INTERNAL:
     * Returns clone of the original map.
     */
    public Hashtable getConnectionsToExternalServicesForCommandPropagation() {
        return (Hashtable)connectionsToExternalServices.clone();
    }

    /**
     * PUBLIC:
     * Set whether connections to remote services should be disconnected when an
     * error occurs.
     */
    public void setShouldRemoveConnectionOnError(boolean shouldRemoveConnectionOnError) {
        this.shouldRemoveConnectionOnError = shouldRemoveConnectionOnError;
    }

    /**
     * PUBLIC:
     * Return whether connections to remote services should be disconnected when an
     * error occurs.
     */
    public boolean shouldRemoveConnectionOnError() {
        return shouldRemoveConnectionOnError;
    }

    /**
     * INTERNAL SECURITY:
     * Set encryption class that will be loaded by the SecurableObjectHolder
     */
    public void setEncryptionClassName(String encryptionClassName) {
        SecurableObjectHolder oldHolder = securableObjectHolder;
        
        // re-initialize encryption mechanism
        securableObjectHolder = new SecurableObjectHolder();
        securableObjectHolder.setEncryptionClassName(encryptionClassName);

        // re-encrypt password
        if (hasPassword()) {
            setPassword(oldHolder.getSecurableObject().decryptPassword(getEncryptedPassword()));
        }
    }

    /**
     * INTERNAL:
     * @return true if a non null password has been set.
     */
    protected boolean hasPassword() { 
        return getRemoteContextProperties().containsKey(Context.SECURITY_CREDENTIALS) && getRemoteContextProperties().get(Context.SECURITY_CREDENTIALS) != null;
    }
    
    /**
     * INTERNAL:
     * Initialize default properties.
     */
    public void initialize() {
        this.shouldRemoveConnectionOnError = DEFAULT_REMOVE_CONNECTION_ON_ERROR_MODE;
        this.connectionsToExternalServices = new Hashtable(2);

        remoteContextProperties = new Hashtable();
        // Factory is not require inside the server, do not default.
        //remoteContextProperties.put(Context.INITIAL_CONTEXT_FACTORY, DEFAULT_CONTEXT_FACTORY);
        remoteContextProperties.put(DEFAULT_DEDICATED_CONNECTION_KEY, DEFAULT_DEDICATED_CONNECTION_VALUE);
        // User/password are not required inside the server, do not default.
        //remoteContextProperties.put(Context.SECURITY_PRINCIPAL, DEFAULT_USER_NAME);

        this.securableObjectHolder = new SecurableObjectHolder();
    }

    /**
     * INTERNAL:
     * Security method.
     */
    protected String encrypt(String pwd) {
        return securableObjectHolder.getSecurableObject().encryptPassword(pwd);
    }

    /**
     * INTERNAL:
     * Security method called by the children classes
     */
    protected String decrypt(String encryptedPwd) {
        return securableObjectHolder.getSecurableObject().decryptPassword(encryptedPwd);
    }

    /**
     * INTERNAL:
     * Return the context used for looking up in the JNDI space of the specified remote URL.
     */
    public Context getRemoteHostContext(String remoteHostURL) {
        Hashtable remoteProperties = (Hashtable)getRemoteContextProperties().clone();
        // Host is only required if JMS connection factory is not accessible from local JNDI.
        if (remoteHostURL != null) {
            remoteProperties.put(Context.PROVIDER_URL, remoteHostURL);
        }
        Object[] args = { remoteProperties };
        rcm.logDebug("context_props_for_remote_lookup", args);

        if (hasPassword()) {
            // decrypt password just before looking up context. Calling 
            // getPassword() will decrypt it.
            remoteProperties.put(Context.SECURITY_CREDENTIALS, getPassword());
        }

        return getContext(remoteProperties);
    }

    /**
     * ADVANCED:
     * Factory of new DiscoveryManager for different transports.  The RemoteCommandManger uses this method to create its DicscoveryManager.
     * Sub-class of TransportManager should return special discovery if required.  The default is discovery type is DiscoveryManager;
     * If this method returns null then during initialization RemoteCommandManager 
     * calls createConnections method.
     */
    public DiscoveryManager createDiscoveryManager() {
        return new DiscoveryManager(rcm);
    }

    /**
     * INTERNAL:
     * Remove all remote connections for its list and the local connection from JNDI or JMS Subsriber
     */
    public void discardConnections() {
        removeLocalConnection();
        removeAllConnectionsToExternalServices();
    }

    /**
     * ADVANCED:
     * Remove the local connection from remote accesses.  The implementation should set remove the local connection from JNDI or JMS and set it to null.
     * This method is invoked internally by TopLink when the RCM is shutdown and should not be invoked by user's application.
     */
    public abstract void removeLocalConnection();
}
