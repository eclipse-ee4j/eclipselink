/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.base;

import java.util.Map;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.internal.jpa.EntityManagerSetupImpl;

/**
* <p>
* <b>Purpose</b>: Provides the implementation for the EntityManager Factory.
* <p>
* <b>Description</b>: This class will store a reference to the active ServerSession.  When a request
* is made for an EntityManager an new EntityManager is created with the ServerSession and returned.
* The primary consumer of these EntityManager is assumed to be either the Container.    There is
* one EntityManagerFactory per deployment.
* @see javax.persistence.EntityManager
* @see org.eclipse.persistence.jpa.EntityManager
* @see javax.persistence.EntityManagerFactory
*/

/*  @author  gyorke  
 *  @since   TopLink 10.1.3 EJB 3.0 Preview
 */

public abstract class EntityManagerFactoryImpl {
    // This stores a reference to the ServerSession for this deployement.
    protected ServerSession serverSession;
    protected EntityManagerSetupImpl setupImpl;
    protected boolean isOpen = true;
    protected Map properties;

    protected abstract EntityManagerImpl createEntityManagerImplInternal(Map properties, boolean extended);

    /**
     * Will return an instance of the Factory.  Should only be called by TopLink.
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
    public synchronized ServerSession getServerSession(){
        if (serverSession == null){   
            ClassLoader realLoader = setupImpl.getPersistenceUnitInfo().getClassLoader();
            // the call top setupImpl.deploy() finishes the session creation
            serverSession = setupImpl.deploy(realLoader, properties);
        }
        return this.serverSession;
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

    protected EntityManagerImpl createEntityManagerImpl(boolean extended) {
        return createEntityManagerImpl(null, extended);
    }

    protected synchronized EntityManagerImpl createEntityManagerImpl(Map properties, boolean extended) {
        verifyOpen();

        if (!getServerSession().isConnected()) {
            getServerSession().login();
        }
        return createEntityManagerImplInternal(properties, extended);
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
     * @param name
     * @return
     */
    public Object getProperty(String name) {
        Object propertyValue=null;
        if(name==null){
            return null;
        }
        return this.getServerSession().getProperty(name);
    }

}
