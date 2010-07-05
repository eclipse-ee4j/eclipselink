/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     07/05/2010-2.1.1 Guy Pelletier 
 *       - 317708: Exception thrown when using LAZY fetch on VIRTUAL mapping
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.metamodel.Metamodel;
import org.eclipse.persistence.config.EntityManagerProperties;
import org.eclipse.persistence.config.FlushClearCache;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.indirection.IndirectionPolicy;
import org.eclipse.persistence.internal.jpa.querydef.CriteriaBuilderImpl;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.PropertiesHandler;
import org.eclipse.persistence.queries.FetchGroupTracker;
import org.eclipse.persistence.sessions.factories.ReferenceMode;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;

/**
 * <p>
 * <b>Purpose</b>: Provides the implementation for the EntityManager Factory.
 * <p>
 * <b>Description</b>: This class will store a reference to the active
 * ServerSession. When a request is made for an EntityManager an new
 * EntityManager is created with the ServerSession and returned. The primary
 * consumer of these EntityManager is assumed to be either the Container. There
 * is one EntityManagerFactory per deployment.
 * 
 * @see javax.persistence.EntityManager
 * @see org.eclipse.persistence.jpa.JpaEntityManager
 * @see org.eclipse.persistence.jpa.EntityManagerFactory
 * 
 * @author gyorke
 * @since TopLink Essentials - JPA 1.0
 * 
 *        03/19/2009-2.0 Michael O'Brien - 266912: JPA 2.0 Metamodel API (part
 *        of the JSR-317 EJB 3.1 Criteria API)
 */
public class EntityManagerFactoryImpl implements EntityManagerFactory, PersistenceUnitUtil {
    /** Reference to Cache Interface. */
    protected Cache myCache;
    /** Reference to the ServerSession for this deployment. */
    protected volatile ServerSession serverSession;
    /** EntityManagerSetupImpl that deployed this factory. */
    protected EntityManagerSetupImpl setupImpl;
    /** Stores if closed has been called. */
    protected boolean isOpen = true;
    /** Persistence unit properties from create factory. */
    protected Map properties;

    /**
     * INTERNAL: The following properties passed to createEMF cached and
     * processed on the emf directly. None of these properties processed during
     * predeploy or deploy.
     **/
    protected static final Set<String> supportedNonServerSessionProperties = PersistenceUnitProperties.getSupportedNonServerSessionProperties();

    /**
     * Default join existing transaction property, allows reading through write
     * connection.
     */
    protected boolean beginEarlyTransaction;

    /** Default property, allows flush before query to be avoided. */
    protected FlushModeType flushMode = FlushModeType.AUTO;

    /** Default property, allows weak unit of work references. */
    protected ReferenceMode referenceMode = ReferenceMode.HARD;

    /**
     * Default property to avoid resuming unit of work if going to be closed on
     * commit anyway.
     */
    protected boolean closeOnCommit;

    /**
     * Default property to avoid discover new objects in unit of work if
     * application always uses persist.
     */
    protected boolean persistOnCommit = true;

    /**
     * Default FlashClearCache mode to be used. Relevant only in case call to
     * flush method followed by call to clear method.
     * 
     * @see org.eclipse.persistence.config.FlushClearCache
     */
    protected String flushClearCache = FlushClearCache.DEFAULT;

    /** Default to determine if does-exist should be performed on persist. */
    protected boolean shouldValidateExistence;    

    /** Allow updates to be ordered by id to avoid possible deadlocks. */
    protected boolean shouldOrderUpdates;
    
    protected boolean commitWithoutPersistRules;

    /**
     * Will return an instance of the Factory. Should only be called by
     * EclipseLink.
     * 
     * @param serverSession
     */
    public EntityManagerFactoryImpl(ServerSession serverSession) {
        this.serverSession = serverSession;
        processProperties(serverSession.getProperties());
    }

    public EntityManagerFactoryImpl(EntityManagerSetupImpl setupImpl, Map properties) {
        this.setupImpl = setupImpl;
        this.properties = properties;
    }

    /**
     * INTERNAL: Returns the ServerSession that the Factory will be using and
     * initializes it if it is not available. This method makes use of the
     * partially constructed session stored in our setupImpl and completes its
     * construction
     */
    public ServerSession getServerSession() {
        if (this.serverSession == null) {
            // PERF: Avoid synchronization.
            synchronized (this) {
                // DCL ok as isLoggedIn is volatile boolean, set after login is
                // complete.
                if (this.serverSession == null) {
                    ClassLoader realLoader = setupImpl.getPersistenceUnitInfo().getClassLoader();
                    // splitProperties[0] contains
                    // supportedNonServerSessionProperties; [1] - all the rest.
                    Map[] splitProperties = EntityManagerFactoryProvider.splitSpecifiedProperties(properties, supportedNonServerSessionProperties);
                    // keep only non server session properties - the rest will
                    // be either cached in the server session or ignored
                    properties = splitProperties[0];
                    Map serverSessionProperties = splitProperties[1];
                    // the call top setupImpl.deploy() finishes the session
                    // creation
                    // If the user has provided a loader (e.g. DynamicClassLoader), use it!
                    ClassLoader providedLoader = (ClassLoader) serverSessionProperties.get(PersistenceUnitProperties.CLASSLOADER);
                    if (providedLoader != null) {
                        realLoader = providedLoader;
                    }
                    ServerSession tempServerSession = setupImpl.deploy(realLoader, serverSessionProperties);
                    // discard all but non server session properties from server
                    // session properties.
                    Map tempProperties = EntityManagerFactoryProvider.keepSpecifiedProperties(tempServerSession.getProperties(), supportedNonServerSessionProperties);
                    // properties override server session properties
                    Map propertiesToProcess = EntityManagerFactoryProvider.mergeMaps(properties, tempProperties);
                    processProperties(propertiesToProcess);
                    this.serverSession = tempServerSession;
                }
            }
        }
        return this.serverSession;
    }

    /**
     * Closes this factory, releasing any resources that might be held by this
     * factory. After invoking this method, all methods on the instance will
     * throw an {@link IllegalStateException}, except for {@link #isOpen}, which
     * will return <code>false</code>.
     */
    public synchronized void close() {
        verifyOpen();
        isOpen = false;
        // Do not invalidate the metaModel field
        // (a reopened emf will re-populate the same metaModel)
        // (a new persistence unit will generate a new metaModel)
        if (setupImpl != null) {
            // 260511 null check so that closing a EM
            // created from the constructor no longer throws a NPE
            setupImpl.undeploy();
        }
    }

    /**
     * Indicates whether or not this factory is open. Returns <code>true</code>
     * until a call to {@link #close} is made.
     */
    public boolean isOpen() {
        return isOpen;
    }

    /**
     * PUBLIC: Returns an EntityManager for this deployment.
     */
    public EntityManager createEntityManager() {
        return createEntityManagerImpl(null);
    }

    /**
     * PUBLIC: Returns an EntityManager for this deployment.
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
                // DCL ok as isLoggedIn is volatile boolean, set after login is
                // complete.
                if (!session.isLoggedIn()) {
                    session.login();
                }
            }
        }
        return new EntityManagerImpl(this, properties);
    }

    protected void verifyOpen() {
        if (!this.isOpen) {
            throw new IllegalStateException(ExceptionLocalization.buildMessage("operation_on_closed_entity_manager_factory"));
        }
    }

    protected void finalize() throws Throwable {
        if (isOpen()) {
            close();
        }
    }

    /**
     * The method return user defined property passed in from
     * EntityManagerFactory.
     */
    public Object getProperty(String name) {
        if (name == null) {
            return null;
        }
        if (properties != null) {
            Object value = properties.get(name);
            if (value != null) {
                return value;
            }
        }
        return getServerSession().getProperty(name);
    }

    /**
     * Process all EntityManager properties. This allows all EntityManager
     * properties specified in the persistence.xml, factory properties, or
     * System properties to be preprocessed. This save the cost of processing
     * these properties each time an EntityManager is created, which can add
     * considerable overhead to both performance and concurrency as System
     * properties are a Hashtable and synchronized. ATTENTION: If you add a new
     * property to be processed in this method please also add the property's
     * name to PersistenceUnitProperties.supportedNonServerSessionProperties
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
        String commitWithoutPersist = PropertiesHandler.getPropertyValueLogDebug(EntityManagerProperties.PERSISTENCE_CONTEXT_COMMIT_WITHOUT_PERSIST_RULES, properties, this.serverSession, true);
        if (commitWithoutPersist != null) {
            this.commitWithoutPersistRules = "true".equalsIgnoreCase(commitWithoutPersist);
        }
        String shouldValidateExistence = PropertiesHandler.getPropertyValueLogDebug(EntityManagerProperties.VALIDATE_EXISTENCE, properties, this.serverSession, true);
        if (shouldValidateExistence != null) {
            this.shouldValidateExistence = "true".equalsIgnoreCase(shouldValidateExistence);
        }
        String shouldOrderUpdates = PropertiesHandler.getPropertyValueLogDebug(EntityManagerProperties.ORDER_UPDATES, properties, this.serverSession, true);
        if (shouldOrderUpdates != null) {
            this.shouldOrderUpdates = "true".equalsIgnoreCase(shouldOrderUpdates);
        }
        String flushClearCache = PropertiesHandler.getPropertyValueLogDebug(EntityManagerProperties.FLUSH_CLEAR_CACHE, properties, this.serverSession, true);
        if (flushClearCache != null) {
            this.flushClearCache = flushClearCache;
        }
    }

    /**
     * Return default join existing transaction property, allows reading through
     * write connection.
     */
    public boolean getBeginEarlyTransaction() {
        return beginEarlyTransaction;
    }

    /**
     * Set default join existing transaction property, allows reading through
     * write connection.
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
     * Return default property to avoid resuming unit of work if going to be
     * closed on commit anyway.
     */
    public boolean getCloseOnCommit() {
        return closeOnCommit;
    }

    /**
     * Set default property to avoid resuming unit of work if going to be closed
     * on commit anyway.
     */
    public void setCloseOnCommit(boolean closeOnCommit) {
        this.closeOnCommit = closeOnCommit;
    }

    /**
     * Return default property to avoid discover new objects in unit of work if
     * application always uses persist.
     */
    public boolean getPersistOnCommit() {
        return persistOnCommit;
    }

    /**
     * Return interface providing access to utility methods for the persistence
     * unit.
     * 
     * @return PersistenceUnitUtil interface
     * @throws IllegalStateException
     *             if the entity manager factory has been closed.
     */
    public PersistenceUnitUtil getPersistenceUnitUtil() {
        return this;
    }

    /**
     * Set default property to avoid discover new objects in unit of work if
     * application always uses persist.
     */
    public void setPersistOnCommit(boolean persistOnCommit) {
        this.persistOnCommit = persistOnCommit;
    }

    /**
     * Return default property to avoid discover new objects in unit of work if
     * application always uses persist.
     */
    public boolean getCommitWithoutPersistRules() {
        return commitWithoutPersistRules;
    }

    /**
     * Set default property to avoid discover new objects in unit of work if
     * application always uses persist.
     */
    public void setCommitWithoutPersistRules(boolean commitWithoutPersistRules) {
        this.commitWithoutPersistRules = commitWithoutPersistRules;
    }

    /**
     * Return the default FlashClearCache mode to be used. Relevant only in case
     * call to flush method followed by call to clear method.
     * 
     * @see org.eclipse.persistence.config.FlushClearCache
     */
    public String getFlushClearCache() {
        return flushClearCache;
    }

    /**
     * Set the default FlashClearCache mode to be used. Relevant only in case
     * call to flush method followed by call to clear method.
     * 
     * @see org.eclipse.persistence.config.FlushClearCache
     */
    public void setFlushClearCache(String flushClearCache) {
        this.flushClearCache = flushClearCache;
    }

    /**
     * Return the default to determine if does-exist should be performed on
     * persist.
     */
    public boolean shouldValidateExistence() {
        return shouldValidateExistence;
    }

    /**
     * Set the default to determine if does-exist should be performed on
     * persist.
     */
    public void setShouldValidateExistence(boolean shouldValidateExistence) {
        this.shouldValidateExistence = shouldValidateExistence;
    }

    /**
     * Access the cache that is associated with the entity manager 
     * factory (the "second level cache").
     * @return instance of the <code>Cache</code> interface
     * @throws IllegalStateException if the entity manager factory has been closed
     * @see javax.persistence.EntityManagerFactory#getCache()
     * @since Java Persistence 2.0
     */
    public Cache getCache() {
        verifyOpen();
        if (null == myCache) {
            myCache = new CacheImpl(this);
        }
        return myCache;
    }

    /**
     * @see javax.persistence.EntityManagerFactory#getProperties()
     * @since Java Persistence API 2.0
     */
    public Map<String, Object> getProperties() {
        if (!this.isOpen()) {
            throw new IllegalStateException(ExceptionLocalization.buildMessage("operation_on_closed_entity_manager_factory"));
        }
        return Collections.unmodifiableMap(EntityManagerFactoryProvider.mergeMaps(properties, this.getServerSession().getProperties()));
    }

    /**
     * @see javax.persistence.EntityManagerFactory#getCriteriaBuilder()
     * @since Java Persistence 2.0
     */
    public CriteriaBuilder getCriteriaBuilder() {
        return new CriteriaBuilderImpl(this.getMetamodel());
    }

    /**
     * Return an instance of Metamodel interface for access to the metamodel of
     * the persistence unit.
     * 
     * @return Metamodel instance
     * @throws IllegalStateException
     *             if the entity manager factory has been closed.
     * @see javax.persistence.EntityManagerFactory#getMetamodel()
     * @since Java Persistence 2.0
     */
    public Metamodel getMetamodel() {
        if (!this.isOpen()) {
            throw new IllegalStateException(ExceptionLocalization.buildMessage("operation_on_closed_entity_manager_factory"));
        }
        return this.setupImpl.getMetamodel();
    }

    /**
     * INTERNAL: Convenience function to allow us to reset the Metamodel in the
     * possible case that we want to regenerate it. This function is outside of
     * the JPA 2.0 specification.
     * 
     * @param aMetamodel
     * @since Java Persistence 2.0
     */
    public void setMetamodel(Metamodel aMetamodel) {
        if (!this.isOpen()) {
            throw new IllegalStateException(ExceptionLocalization.buildMessage("operation_on_closed_entity_manager_factory"));
        }
        this.setupImpl.setMetamodel(aMetamodel);
    }

    /**
     * Determine the load state of a given persistent attribute of an entity
     * belonging to the persistence unit.
     * 
     * @param entity
     *            containing the attribute
     * @param attributeName
     *            name of attribute whose load state is to be determined
     * @return false if entity's state has not been loaded or if the attribute
     *         state has not been loaded, otherwise true
     */
    public boolean isLoaded(Object entity, String attributeName) {
        if (EntityManagerFactoryImpl.isLoaded(entity, attributeName, serverSession).equals(Boolean.valueOf(true))) {
            return true;
        }
        return false;
    }

    /**
     * Determine the load state of an entity belonging to the persistence unit.
     * This method can be used to determine the load state of an entity passed
     * as a reference. An entity is considered loaded if all attributes for
     * which FetchType EAGER has been specified have been loaded. The
     * isLoaded(Object, String) method should be used to determine the load
     * state of an attribute. Not doing so might lead to unintended loading of
     * state.
     * 
     * @param entity
     *            whose load state is to be determined
     * @return false if the entity has not been loaded, else true.
     */
    public boolean isLoaded(Object entity) {
        if (EntityManagerFactoryImpl.isLoaded(entity, serverSession).equals(Boolean.valueOf(true))) {
            return true;
        }
        return false;
    }

    /**
     * Determine the load state of a given persistent attribute of an entity
     * belonging to the persistence unit.
     * 
     * @param entity
     *            containing the attribute
     * @param attributeName
     *            name of attribute whose load state is to be determined
     * @return false if entity's state has not been loaded or if the attribute
     *         state has not been loaded, otherwise true
     */
    public static Boolean isLoaded(Object entity, String attributeName, AbstractSession session) {
        ClassDescriptor descriptor = session.getDescriptor(entity);
        if (descriptor == null) {
            return null;
        }
        DatabaseMapping mapping = descriptor.getMappingForAttributeName(attributeName);
        if (mapping == null) {
            return null;
        }
        return isLoaded(entity, attributeName, mapping);
    }

    /**
     * Check whether a named attribute on a given entity with a given mapping
     * has been loaded.
     * 
     * This method will check the valueholder or indirect collection for LAZY
     * ForeignReferenceMappings to see if has been instantiated and otherwise
     * check the fetch group.
     * 
     * @param entity
     * @param attributeName
     * @param mapping
     * @return
     */
    public static boolean isLoaded(Object entity, String attributeName, DatabaseMapping mapping) {
        if (mapping.isForeignReferenceMapping()) {
            if (((ForeignReferenceMapping) mapping).isLazy()) {
                Object value = mapping.getAttributeValueFromObject(entity);
                IndirectionPolicy policy = ((ForeignReferenceMapping) mapping).getIndirectionPolicy();
                return policy.objectIsInstantiated(value);
            }
        }

        if (entity instanceof FetchGroupTracker) {
            return ((FetchGroupTracker) entity)._persistence_isAttributeFetched(attributeName);
        } else {
            return true;
        }
    }

    /**
     * Determine the load state of an entity belonging to the persistence unit.
     * This method can be used to determine the load state of an entity passed
     * as a reference. An entity is considered loaded if all attributes for
     * which FetchType EAGER has been specified have been loaded. The
     * isLoaded(Object, String) method should be used to determine the load
     * state of an attribute. Not doing so might lead to unintended loading of
     * state.
     * 
     * @param entity
     *            whose load state is to be determined
     * @return false if the entity has not been loaded, else true.
     */
    public static Boolean isLoaded(Object entity, AbstractSession session) {
        ClassDescriptor descriptor = session.getDescriptor(entity);
        if (descriptor == null) {
            return null;
        }
        List<DatabaseMapping> mappings = descriptor.getMappings();
        Iterator<DatabaseMapping> i = mappings.iterator();
        while (i.hasNext()) {
            DatabaseMapping mapping = i.next();
            if (!mapping.isLazy() && !isLoaded(entity, mapping.getAttributeName(), mapping)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the id of the entity. A generated id is not guaranteed to be
     * available until after the database insert has occurred. Returns null if
     * the entity does not yet have an id
     * 
     * @param entity
     * @return id of the entity
     * @throws IllegalStateException
     *             if the entity is found not to be an entity.
     */
    public Object getIdentifier(Object entity) {
        return EntityManagerFactoryImpl.getIdentifier(entity, serverSession);
    }
    
    /**
     * Returns the id of the entity. A generated id is not guaranteed to be
     * available until after the database insert has occurred. Returns null if
     * the entity does not yet have an id
     * 
     * @param entity
     * @return id of the entity
     * @throws IllegalStateException
     *             if the entity is found not to be an entity.
     */
    public static Object getIdentifier(Object entity, AbstractSession session) {
        ClassDescriptor descriptor = session.getDescriptor(entity);
        if (descriptor == null) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("jpa_persistence_util_non_persistent_class", new Object[] { entity }));
        }
        if (descriptor.getCMPPolicy() != null) {
            return descriptor.getCMPPolicy().createPrimaryKeyInstance(entity, session);
        } else {
            // 308950: Alternatively, CacheImpl.getId(entity) handles a null CMPPolicy case for weaved and unweaved domain object
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("jpa_persistence_util_non_persistent_class", new Object[] { entity }));
        }
    }

    /**
     * ADVANCED:
     * Return if updates should be ordered by primary key to avoid possible database deadlocks.
     */
    public boolean shouldOrderUpdates() {
        return shouldOrderUpdates;
    }

    /**
     * ADVANCED:
     * Set updates should be ordered by primary key to avoid possible database deadlocks.
     */
    public void setShouldOrderUpdates(boolean shouldOrderUpdates) {
        this.shouldOrderUpdates = shouldOrderUpdates;
    }
}
