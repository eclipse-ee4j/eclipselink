/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
package org.eclipse.persistence.internal.jpa;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.internal.jpa.EntityManagerSetupImpl;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;

/**
* <p>
* <b>Purpose</b>: Provides the implementation for the EntityManager Factory.
* <p>
* <b>Description</b>: This class will store a reference to the active ServerSession.  When a request
* is made for an EntityManager an new EntityManager is created with the ServerSession and returned.
* The primary consumer of these EntityManager is assumed to be either the Container.    There is
* one EntityManagerFactory per deployment.
* @see javax.persistence.EntityManager
* @see org.eclipse.persistence.jpa.JpaEntityManager
* @see org.eclipse.persistence.jpa.EntityManagerFactory
* 
* @author  gyorke
* @since   TopLink 10.1.3 EJB 3.0 Preview
*/
public class EntityManagerFactoryImpl implements EntityManagerFactory {
    // This stores a reference to the ServerSession for this deployment.
    protected volatile ServerSession serverSession;
    protected EntityManagerSetupImpl setupImpl;
    protected boolean isOpen = true;
    protected Map properties;

    /**
     * Will return an instance of the Factory.  Should only be called by EclipseLink.
     * @param serverSession
     */
    public EntityManagerFactoryImpl(ServerSession serverSession){
        this.serverSession = serverSession;
    }
    
    public EntityManagerFactoryImpl(EntityManagerSetupImpl setupImpl, Map properties){
        this.setupImpl = setupImpl;
        this.properties = properties;
    }
    
    /**
     * INTERNAL:
     * Returns the ServerSession that the Factory will be using and initializes it if it is not available.
     * This method makes use of the partially constructed session stored in our setupImpl and
     * completes its construction
     */
    public ServerSession getServerSession(){
        if (serverSession == null) {
            // PERF: Avoid synchronization.
            synchronized (this) {
                // DCL ok as isLoggedIn is volatile boolean, set after login is complete.
                if (serverSession == null) {
                    ClassLoader realLoader = setupImpl.getPersistenceUnitInfo().getClassLoader();
                    // the call top setupImpl.deploy() finishes the session creation
                    serverSession = setupImpl.deploy(realLoader, properties);
                }
            }
        }
        return serverSession;
    }
    
    /**
     * Closes this factory, releasing any resources that might be held by this factory. After
     * invoking this method, all methods on the instance will throw an
     * {@link IllegalStateException}, except for {@link #isOpen}, which will return
     * <code>false</code>.
     */
    public synchronized void close(){
        verifyOpen();
        isOpen = false;
        setupImpl.undeploy();
    }


    /**
     * Indicates whether or not this factory is open. Returns <code>true</code> until a call
     * to {@link #close} is made.
     */
    public boolean isOpen(){
       return isOpen;
    }

    /**
     * PUBLIC:
     * Returns an EntityManager for this deployment
     */
    public EntityManager createEntityManager() {
        return createEntityManagerImpl(false);
    }
    
    /**
     * PUBLIC:
     * Returns an EntityManager for this deployment
     */
    public EntityManager createEntityManager(Map properties) {
        return createEntityManagerImpl(properties, false);
    }
    
    protected EntityManagerImpl createEntityManagerImpl(boolean extended) {
        return createEntityManagerImpl(null, extended);
    }

    protected EntityManagerImpl createEntityManagerImpl(Map properties, boolean extended) {
        verifyOpen();

        ServerSession session = getServerSession();
        if (!session.isLoggedIn()) {
            // PERF: Avoid synchronization.
            synchronized (session) {
                // DCL ok as isLoggedIn is volatile boolean, set after login is complete.
                if (!session.isLoggedIn()) {
                    session.login();
                }
            }
        }
        return createEntityManagerImplInternal(properties, extended);
    }

    //TODO change the way create works to deal with how the specification works with persistence contexts
    protected EntityManagerImpl createEntityManagerImplInternal(Map properties, boolean extended) {
        return new EntityManagerImpl(this, properties, false, extended);
    }
    
    protected void verifyOpen(){
        if (!isOpen){
            throw new IllegalStateException(ExceptionLocalization.buildMessage("operation_on_closed_entity_manager_factory"));
        }
    }    

    protected void finalize() throws Throwable {
        if(isOpen()) {
            close();
        }
    }
    
    /**
     * The method return user defined property passed in from EntityManagerFactory. 
     */
    public Object getProperty(String name) {
        if(name==null){
            return null;
        }
        return this.getServerSession().getProperty(name);
    }
}
