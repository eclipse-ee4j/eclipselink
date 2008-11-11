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
import javax.persistence.FlushModeType;

import org.eclipse.persistence.sessions.factories.ReferenceMode;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.config.EntityManagerProperties;
import org.eclipse.persistence.config.FlushClearCache;
import org.eclipse.persistence.internal.jpa.EntityManagerSetupImpl;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.internal.sessions.PropertiesHandler;

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
    /** Reference to the ServerSession for this deployment. */
    protected volatile ServerSession serverSession;
    /** EntityManagerSetupImpl that deployed this factory. */
    protected EntityManagerSetupImpl setupImpl;
    /** Stores if closed has been called. */
    protected boolean isOpen = true;
    /** Persistence unit properties from persistence.xml or from create factory. */
    protected Map properties;
    
    /** Default join existing transaction property, allows reading through write connection. */
    protected boolean beginEarlyTransaction;

    /** Default property, allows flush before query to be avoided. */
    protected FlushModeType flushMode = FlushModeType.AUTO;

    /** Default property, allows weak unit of work references. */
    protected ReferenceMode referenceMode = ReferenceMode.HARD;
        
    /** Default property to avoid resuming unit of work if going to be closed on commit anyway. */
    protected boolean closeOnCommit;
    
    /** Default property to avoid discover new objects in unit of work if application always uses persist. */
    protected boolean persistOnCommit = true;
    
    /**
     * Default FlashClearCache mode to be used.
     * Relevant only in case call to flush method followed by call to clear method.
     * @see org.eclipse.persistence.config.FlushClearCache
     */
    protected String flushClearCache = FlushClearCache.DEFAULT;

    /** Default to determine if does-exist should be performed on persist. */
    protected boolean shouldValidateExistence;
    
    /**
     * Will return an instance of the Factory.  Should only be called by EclipseLink.
     * @param serverSession
     */
    public EntityManagerFactoryImpl(ServerSession serverSession){
        this.serverSession = serverSession;
        processProperties(serverSession.getProperties());
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
        if (this.serverSession == null) {
            // PERF: Avoid synchronization.
            synchronized (this) {
                // DCL ok as isLoggedIn is volatile boolean, set after login is complete.
                if (this.serverSession == null) {
                    ClassLoader realLoader = setupImpl.getPersistenceUnitInfo().getClassLoader();
                    // the call top setupImpl.deploy() finishes the session creation
                    ServerSession tempServerSession = setupImpl.deploy(realLoader, properties);
                    processProperties(tempServerSession.getProperties());
                    this.serverSession = tempServerSession;
                }
            }
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

    /**
     * PUBLIC:
     * Returns an EntityManager for this deployment.
     */
    public EntityManager createEntityManager() {
        return createEntityManagerImpl(null);
    }
    
    /**
     * PUBLIC:
     * Returns an EntityManager for this deployment.
     */
    public EntityManager createEntityManager(Map properties) {
        return createEntityManagerImpl(properties);
    }

    protected EntityManagerImpl createEntityManagerImpl(Map properties) {
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
        return new EntityManagerImpl(this, properties);
    }
    
    protected void verifyOpen(){
        if (!this.isOpen){
            throw new IllegalStateException(ExceptionLocalization.buildMessage("operation_on_closed_entity_manager_factory"));
        }
    }    

    protected void finalize() throws Throwable {
        if (isOpen()) {
            close();
        }
    }
    
    /**
     * The method return user defined property passed in from EntityManagerFactory. 
     */
    public Object getProperty(String name) {
        if (name == null) {
            return null;
        }
        return getServerSession().getProperty(name);
    }
    
    /**
     * Process all EntityManager properties.
     * This allows all EntityManager properties specified in the persistence.xml, factory properties, or System properties to be preprocessed.
     * This save the cost of processing these properties each time an EntityManager is created,
     * which can add considerable overhead to both performance and concurrency as System properties are a Hashtable and synchronized.
     */
    protected void processProperties(Map properties) {
        String beginEarlyTransactionProperty = PropertiesHandler.getPropertyValueLogDebug(EntityManagerProperties.JOIN_EXISTING_TRANSACTION, properties, this.serverSession, true);
        if (beginEarlyTransactionProperty != null) {
            this.beginEarlyTransaction = "true".equalsIgnoreCase(beginEarlyTransactionProperty);
        }
        String referenceMode = PropertiesHandler.getPropertyValueLogDebug(EntityManagerProperties.PERSISTENCE_CONTEXT_REFERENCE_MODE, properties, this.serverSession, true);
        if (referenceMode != null) {
            this.referenceMode = ReferenceMode.valueOf(referenceMode);
        }
        String flushMode = PropertiesHandler.getPropertyValueLogDebug(EntityManagerProperties.PERSISTENCE_CONTEXT_FLUSH_MODE, properties, this.serverSession, true);
        if (flushMode != null) {
            this.flushMode = FlushModeType.valueOf(flushMode);
        }
        String closeOnCommit = PropertiesHandler.getPropertyValueLogDebug(EntityManagerProperties.PERSISTENCE_CONTEXT_CLOSE_ON_COMMIT, properties, this.serverSession, true);
        if (closeOnCommit != null) {
            this.closeOnCommit = "true".equalsIgnoreCase(closeOnCommit);
        }
        String persistOnCommit = PropertiesHandler.getPropertyValueLogDebug(EntityManagerProperties.PERSISTENCE_CONTEXT_PERSIST_ON_COMMIT, properties, this.serverSession, true);
        if (persistOnCommit != null) {
            this.persistOnCommit = "true".equalsIgnoreCase(persistOnCommit);
        }
        String shouldValidateExistence = PropertiesHandler.getPropertyValueLogDebug(EntityManagerProperties.VALIDATE_EXISTENCE, properties, this.serverSession, true);
        if (shouldValidateExistence != null) {
            this.shouldValidateExistence = "true".equalsIgnoreCase(shouldValidateExistence);
        }
        String flushClearCache = PropertiesHandler.getPropertyValueLogDebug(EntityManagerProperties.FLUSH_CLEAR_CACHE, properties, this.serverSession, true);
        if (flushClearCache != null) {
            this.flushClearCache = flushClearCache;
        }
    }
    
    /**
     * Return default join existing transaction property, allows reading through write connection.
     */
    public boolean getBeginEarlyTransaction() {
        return beginEarlyTransaction;
    }
    
    /**
     * Set default join existing transaction property, allows reading through write connection.
     */
    public void setBeginEarlyTransaction(boolean beginEarlyTransaction) {
        this.beginEarlyTransaction = beginEarlyTransaction;
    }

    /**
     * Return default property, allows flush before query to be avoided.
     */
    public FlushModeType getFlushMode() {
        return flushMode;
    }

    /**
     * Set default property, allows flush before query to be avoided.
     */
    public void setFlushMode(FlushModeType flushMode) {
        this.flushMode = flushMode;
    }

    /**
     * Return default property, allows weak unit of work references.
     */    
    public ReferenceMode getReferenceMode() {
        return referenceMode;
    }

    /**
     * Set default property, allows weak unit of work references.
     */    
    public void setReferenceMode(ReferenceMode referenceMode) {
        this.referenceMode = referenceMode;
    }
        
    /**
     * Return default property to avoid resuming unit of work if going to be closed on commit anyway.
     */
    public boolean getCloseOnCommit() {
        return closeOnCommit;
    }
        
    /**
     * Set default property to avoid resuming unit of work if going to be closed on commit anyway.
     */
    public void setCloseOnCommit(boolean closeOnCommit) {
        this.closeOnCommit = closeOnCommit;
    }
    
    /**
     * Return default property to avoid discover new objects in unit of work if application always uses persist.
     */
    public boolean getPersistOnCommit() {
        return persistOnCommit;
    }
    
    /**
     * Set default property to avoid discover new objects in unit of work if application always uses persist.
     */
    public void setPersistOnCommit(boolean persistOnCommit) {
        this.persistOnCommit = persistOnCommit;
    }

    /**
     * Return the default FlashClearCache mode to be used.
     * Relevant only in case call to flush method followed by call to clear method.
     * @see org.eclipse.persistence.config.FlushClearCache
     */
    public String getFlushClearCache() {
        return flushClearCache;
    }

    /**
     * Set the default FlashClearCache mode to be used.
     * Relevant only in case call to flush method followed by call to clear method.
     * @see org.eclipse.persistence.config.FlushClearCache
     */
    public void setFlushClearCache(String flushClearCache) {
        this.flushClearCache = flushClearCache;
    }

    /**
     * Return the default to determine if does-exist should be performed on persist.
     */
    public boolean shouldValidateExistence() {
        return shouldValidateExistence;
    }

    /**
     * Set the default to determine if does-exist should be performed on persist.
     */
    public void setShouldValidateExistence(boolean shouldValidateExistence) {
        this.shouldValidateExistence = shouldValidateExistence;
    }
}
