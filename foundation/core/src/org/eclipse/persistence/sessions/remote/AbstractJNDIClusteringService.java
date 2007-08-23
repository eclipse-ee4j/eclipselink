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

import java.util.Hashtable;
import javax.naming.*;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.exceptions.*;

/**
 * <p>
 * <b>Purpose</b>:To Provide a framework for offering customers the ability to automatically
 * connect multiple sessions for synchrnization.
 * <p>
 * <b>Descripton</b>:This thread object will place a remote dispatcher in the specified JNDI space.
 * it will also monitor the specified multicast socket to allow other sessions to connect.
 *
 * @author Gordon Yorke
 * @see org.eclipse.persistence.sessions.remote.CacheSynchronizationManager
 * @see org.eclipse.persistence.sessions.remote.AbstractClusteringService
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.sessions.coordination.TransportManager}
 */
public abstract class AbstractJNDIClusteringService extends AbstractClusteringService {

    /**
     * Holds the reference to the context to be used to register the dispatcher object
     */
    protected Context localContext;
    protected String userName;
    protected char[] password;
    protected Hashtable contextProperties;

    /**
     * PUBLIC:
     * Creates an AbstractJNDIClusteringService
     * @param jndiHostName The host name of the JNDI service.
     */
    public AbstractJNDIClusteringService(Session session) {
        this(DEFAULT_MULTICAST_GROUP, DEFAULT_MULTICAST_PORT, session);
    }

    /**
     * ADVANCED:
     *     Creates an AbstractJNDIClusteringService
     * @param multicastAddress The address of the multicast group
     * @param multicastPort The port the multicast group is listening on.
     */
    public AbstractJNDIClusteringService(String multicastAddress, int multicastPort, Session session) {
        super(multicastAddress, multicastPort, session);
    }

    /**
     * INTERNAL:
     * Returns the active JNDI Context to store the remote service in.
     */
    public Context getLocalContext() {
        if (this.localContext == null) {
            ((org.eclipse.persistence.internal.sessions.AbstractSession)getSession()).log(SessionLog.FINEST, SessionLog.PROPAGATION, "getting_local_initial_context");
            this.localContext = getContext(getInitialContextProperties());
        }
        return this.localContext;
    }

    /**
     * ADVANCED:
     * Returns the active JNDI Context to store the remote service in
     * @deprecated Since 4.5
     */
    public Context getContext(String jndiHostURL) {
        try {
            Hashtable properties = new Hashtable(2);

            //need to add context properties like context factory name
            properties.put(Context.PROVIDER_URL, jndiHostURL);
            return new javax.naming.InitialContext(properties);
        } catch (NamingException exception) {
            getSession().handleException(SynchronizationException.errorLookingUpController(jndiHostURL, exception));
        }
        return null;
    }

    /**
     * INTERNAL:
     * Returns the active JNDI Context to store the remote service in
     */
    public Context getContext(Hashtable properties) {
        try {
            return new javax.naming.InitialContext(properties);
        } catch (NamingException exception) {
            getSession().handleException(SynchronizationException.errorLookingUpController(properties.toString(), exception));
        }
        return null;
    }

    /**
     * Sets the active JNDI Context to store the remote service in.
      */
    public void setContext(Context context) {
        this.localContext = context;
    }

    public void setLocalHostURL(String url) {
        this.localHostURL = url;
        // For bug 2700794 must insure this is added to initialContextProperties.
        this.getInitialContextProperties().put(Context.PROVIDER_URL, url);
    }

    /**
     * ADVANCED:
     * This method allows the user to get the Context properties that will be used to create the initial context.
     */
    public Hashtable getInitialContextProperties() {
        if (this.contextProperties == null) {
            this.contextProperties = new Hashtable(2);
        }
        return this.contextProperties;
    }

    /**
     * ADVANCED:
     * This method allows the user to set the Context properties for creating the initial context
     * for a JNDI connection if the properties have not allready been set globally.  Usually if
     * TopLink is running within the same VM as this attribute is not required.  Use this method if
     * TopLink is having problems connecting to the JNDI Service, or JMS Service
     */
    public void setInitialContextProperties(Hashtable properties) {
        this.contextProperties = properties;
    }

    /**
     * ADVANCED:
     * returns the Username if one is required to access the JNDI service
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     * Sets the Username if one is required to access the JNDI service
     */
    public void setUserName(String userName) {
        this.userName = userName;
        getInitialContextProperties().put(Context.SECURITY_PRINCIPAL, userName);
    }

    /**
     * ADVANCED:
     * returns the Username if one is required to access the JNDI service
     */
    public String getPassword() {
        // Bug 4117441 - Secure programming practices, store password in char[]
        if (this.password != null) {
            return new String(this.password);
        } else {
            // respect de-referencing of passwords
            return null;
        }
    }

    /**
     * Sets the Password if one is required to access the JNDI service
     */
    public void setPassword(String password) {
        // Bug 4117441 - Secure programming practices, store password in char[]
        if (password != null) {
            this.password = password.toCharArray();
        } else {
            // respect de-referencing of passwords
            this.password = null;
        }
        getInitialContextProperties().put(Context.SECURITY_CREDENTIALS, password);
    }

    /**
     * ADVANCED:
     * Use this method to set the Initial Conext Factory for accessing JNDI.
     * This method is only required if the user is having difficulties getting TopLink
     * to connect to JNDI or JMS
     */
    public void setInitialContextFactoryName(String initialContextFactory) {
        getInitialContextProperties().put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
    }
}