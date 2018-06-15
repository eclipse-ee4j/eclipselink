/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates, IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     03/19/2009-2.0 Michael O'Brien - 266912: JPA 2.0 Metamodel API (part
//                      of the JSR-317 EJB 3.1 Criteria API)
//     08/17/2010-2.2 Michael O'Brien
//        - 322585: Login the session on the first call to getMetamodel() or getCriteriaBuilder()
//                       after EMF predeploy() completes.  This will do a DB login that calls
//                       initializeDescriptors() so we have real Classes and not just class names for
//                       MappedSuperclass metamodel descriptors.  This is provided for
//                       implementations that use the metamodel before the 1st EntityManager creation.
//                       Login will continue to only be called in EM deploy for users
//                       that do not request the Metamodel
//     11/17/2010-2.2 Guy Pelletier
//       - 329008: Support dynamic context creation without persistence.xml
//     03/22/2016-2.6_WAS Nathan Rauh
//       - 489787: Fixed NullPointerException when specifying non-entity object to PersistenceUnitUtil.isLoaded
package org.eclipse.persistence.internal.jpa;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Cache;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceException;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.Query;
import javax.persistence.SynchronizationType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.metamodel.Metamodel;

import org.eclipse.persistence.config.EntityManagerProperties;
import org.eclipse.persistence.config.FlushClearCache;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.ReferenceMode;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.internal.jpa.querydef.CriteriaBuilderImpl;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.internal.sessions.PropertiesHandler;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.queries.AttributeGroup;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork.CommitOrderType;
import org.eclipse.persistence.sessions.broker.SessionBroker;
import org.eclipse.persistence.sessions.server.Server;
import org.eclipse.persistence.sessions.server.ServerSession;

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
 * @see org.eclipse.persistence.jpa.JpaEntityManagerFactory
 *
 * @author gyorke
 * @since TopLink Essentials - JPA 1.0
 *
 */
public class EntityManagerFactoryDelegate implements EntityManagerFactory, PersistenceUnitUtil, JpaEntityManagerFactory {
    /** Reference to Cache Interface. */
    protected Cache myCache;
    /** Reference to the ServerSession for this deployment. */
    protected volatile AbstractSession session;
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

    /** Order updates by id to avoid potential deadlocks. Default is true. */
    protected CommitOrderType commitOrder = CommitOrderType.ID;

    protected boolean commitWithoutPersistRules;

    /** Pointer to the EntityManagerFactoryImpl that created me */
    protected JpaEntityManagerFactory owner = null;

    /**
     * Will return an instance of the Factory. Should only be called by
     * EclipseLink.
     *
     * @param databaseSession
     */
    public EntityManagerFactoryDelegate(AbstractSession databaseSession, JpaEntityManagerFactory owner) {
        this.session = databaseSession;
        this.owner = owner;
        processProperties(databaseSession.getProperties());
    }

    public EntityManagerFactoryDelegate(EntityManagerSetupImpl setupImpl, Map properties, JpaEntityManagerFactory owner) {
        this.setupImpl = setupImpl;
        this.owner = owner;
        this.properties = properties;
    }

    /**
     * Create a dynamic persistence unit which does not use the persistence.xml.
     * Instead all configuration is driven from the provided persistence unit
     * properties and descriptors.
     */
    public EntityManagerFactoryDelegate(String persistenceUnitName, Map<String, Object> properties, List<ClassDescriptor> descriptors, JpaEntityManagerFactory owner) {
        this.properties = properties;
        this.owner = owner;

        SEPersistenceUnitInfo info = new SEPersistenceUnitInfo();
        info.setClassLoader((ClassLoader) properties.get(PersistenceUnitProperties.CLASSLOADER));
        info.setPersistenceUnitName(persistenceUnitName);
        info.getProperties().putAll(properties);

        this.setupImpl = new EntityManagerSetupImpl();
        this.setupImpl.predeploy(info, null);
        this.setupImpl.getDatabaseSession().addDescriptors(descriptors);
    }

    /**
     * INTERNAL: Returns the ServerSession that the Factory will be using and
     * initializes it if it is not available. This method makes use of the
     * partially constructed session stored in our setupImpl and completes its
     * construction
     */
    @Override
    public DatabaseSessionImpl getDatabaseSession() {
        return (DatabaseSessionImpl)getAbstractSession();
    }

    /**
     * INTERNAL: Returns the ServerSession that the Factory will be using and
     * initializes it if it is not available. This method makes use of the
     * partially constructed session stored in our setupImpl and completes its
     * construction
     */
    public AbstractSession getAbstractSession() {
        if (this.session == null) {
            // PERF: Avoid synchronization.
            synchronized (this) {
                // DCL ok as isLoggedIn is volatile boolean, set after login is
                // complete.
                if (this.session == null) {
                    ClassLoader realLoader = setupImpl.getPersistenceUnitInfo().getClassLoader();
                    // splitProperties[0] contains
                    // supportedNonServerSessionProperties; [1] - all the rest.
                    Map[] splitProperties = EntityManagerFactoryProvider.splitSpecifiedProperties(properties, supportedNonServerSessionProperties);
                    Map serverSessionProperties = splitProperties[1];
                    // the call to setupImpl.deploy() finishes the session creation
                    AbstractSession tempSession = setupImpl.deploy(realLoader, serverSessionProperties);
                    // discard all but non server session properties from server
                    // session properties.
                    Map tempProperties = EntityManagerFactoryProvider.keepSpecifiedProperties(tempSession.getProperties(), supportedNonServerSessionProperties);
                    // keep only non server session properties - the rest will
                    // be either cached in the server session or ignored
                    properties = splitProperties[0];
                    // properties override server session properties
                    Map propertiesToProcess = EntityManagerFactoryProvider.mergeMaps(properties, tempProperties);
                    processProperties(propertiesToProcess);
                    this.session = tempSession;
                }
            }
        }
        return this.session;
    }

    /**
     * INTERNAL:
     * Return the EntityManagerSetupImpl associated with this factory
     * @return
     */
    public EntityManagerSetupImpl getSetupImpl(){
        return setupImpl;
    }

    /**
     * INTERNAL: Returns the ServerSession that the Factory will be using and
     * initializes it if it is not available. This method makes use of the
     * partially constructed session stored in our setupImpl and completes its
     * construction
     * TODO: should throw IllegalStateException if not ServerSession
     */
    @Override
    public ServerSession getServerSession() {
        return (ServerSession)getAbstractSession();
    }

    /**
     * INTERNAL: Returns the SessionBroker that the Factory will be using and
     * initializes it if it is not available. This method makes use of the
     * partially constructed session stored in our setupImpl and completes its
     * construction
     * TODO: should throw IllegalStateException if not SessionBroker
     */
    @Override
    public SessionBroker getSessionBroker() {
        return (SessionBroker)getAbstractSession();
    }

    /**
     * Closes this factory, releasing any resources that might be held by this
     * factory. After invoking this method, all methods on the instance will
     * throw an {@link IllegalStateException}, except for {@link #isOpen}, which
     * will return <code>false</code>.
     */
    @Override
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
        owner = null;
    }

    /**
     * Indicates whether or not this factory is open. Returns <code>true</code>
     * until a call to {@link #close} is made.
     */
    @Override
    public boolean isOpen() {
        return isOpen;
    }

    /**
     * PUBLIC: Returns an EntityManager for this deployment.
     */
    @Override
    public EntityManager createEntityManager() {
        return createEntityManagerImpl(null, null);
    }

    /**
     * PUBLIC: Returns an EntityManager for this deployment.
     */
    @Override
    public EntityManager createEntityManager(Map properties) {
        return createEntityManagerImpl(properties, null);
    }

    @Override
    public EntityManager createEntityManager(SynchronizationType synchronizationType, Map map) {
        return createEntityManagerImpl(map, SynchronizationType.SYNCHRONIZED);
    }

    @Override
    public EntityManager createEntityManager(SynchronizationType synchronizationType) {
        return createEntityManagerImpl(null, synchronizationType);
    }

    protected EntityManagerImpl createEntityManagerImpl(Map properties, SynchronizationType syncType) {
        verifyOpen();
        AbstractSession session = getAbstractSession();
        if (session.isDatabaseSession()) {
            DatabaseSessionImpl databaseSession = (DatabaseSessionImpl)session;
            if (!databaseSession.isLoggedIn()) {
                // PERF: Avoid synchronization.
                synchronized (databaseSession) {
                    // DCL ok as isLoggedIn is volatile boolean, set after login is
                    // complete.
                    if (!databaseSession.isLoggedIn()) {
                        databaseSession.login();
                    }
                }
            }
        }
        if (syncType != null && !session.hasExternalTransactionController()){
            throw new IllegalStateException(ExceptionLocalization.buildMessage("pu_configured_for_resource_local"));
        }
        return new EntityManagerImpl(this, properties, syncType);
    }


    /**
     * Gets the underlying implementation of the EntityManagerFactory.
     * This method will return a version of EntityManagerFactory that is
     * based on the available metadata at the time it is called.  Future calls
     * to refresh will not affect that metadata on this EntityManagerFactory.
     * @return
     */
    @Override
    public EntityManagerFactoryDelegate unwrap(){
        return this;
    }

    protected void verifyOpen() {
        if (!this.isOpen) {
            throw new IllegalStateException(ExceptionLocalization.buildMessage("operation_on_closed_entity_manager_factory"));
        }
    }

    @Override
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
        AbstractSession tempSession = this.session;
        if (tempSession != null) {
            return tempSession.getProperty(name);
        } else {
            return null;
        }
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
        String beginEarlyTransactionProperty = PropertiesHandler.getPropertyValueLogDebug(EntityManagerProperties.JOIN_EXISTING_TRANSACTION, properties, this.session, true);
        if (beginEarlyTransactionProperty != null) {
            this.beginEarlyTransaction = "true".equalsIgnoreCase(beginEarlyTransactionProperty);
        }
        String referenceMode = PropertiesHandler.getPropertyValueLogDebug(EntityManagerProperties.PERSISTENCE_CONTEXT_REFERENCE_MODE, properties, this.session, true);
        if (referenceMode != null) {
            this.referenceMode = ReferenceMode.valueOf(referenceMode);
        }
        String flushMode = PropertiesHandler.getPropertyValueLogDebug(EntityManagerProperties.PERSISTENCE_CONTEXT_FLUSH_MODE, properties, this.session, true);
        if (flushMode != null) {
            this.flushMode = FlushModeType.valueOf(flushMode);
        }
        String closeOnCommit = PropertiesHandler.getPropertyValueLogDebug(EntityManagerProperties.PERSISTENCE_CONTEXT_CLOSE_ON_COMMIT, properties, this.session, true);
        if (closeOnCommit != null) {
            this.closeOnCommit = "true".equalsIgnoreCase(closeOnCommit);
        }
        String persistOnCommit = PropertiesHandler.getPropertyValueLogDebug(EntityManagerProperties.PERSISTENCE_CONTEXT_PERSIST_ON_COMMIT, properties, this.session, true);
        if (persistOnCommit != null) {
            this.persistOnCommit = "true".equalsIgnoreCase(persistOnCommit);
        }
        String commitWithoutPersist = PropertiesHandler.getPropertyValueLogDebug(EntityManagerProperties.PERSISTENCE_CONTEXT_COMMIT_WITHOUT_PERSIST_RULES, properties, this.session, true);
        if (commitWithoutPersist != null) {
            this.commitWithoutPersistRules = "true".equalsIgnoreCase(commitWithoutPersist);
        }
        String shouldValidateExistence = PropertiesHandler.getPropertyValueLogDebug(EntityManagerProperties.VALIDATE_EXISTENCE, properties, this.session, true);
        if (shouldValidateExistence != null) {
            this.shouldValidateExistence = "true".equalsIgnoreCase(shouldValidateExistence);
        }
        String shouldOrderUpdates = PropertiesHandler.getPropertyValueLogDebug(EntityManagerProperties.ORDER_UPDATES, properties, this.session, true);
        if (shouldOrderUpdates != null) {
            if ("true".equalsIgnoreCase(shouldOrderUpdates)) {
                this.commitOrder = CommitOrderType.ID;
            } else {
                this.commitOrder = CommitOrderType.NONE;
            }
        }
        String commitOrder = PropertiesHandler.getPropertyValueLogDebug(EntityManagerProperties.PERSISTENCE_CONTEXT_COMMIT_ORDER, properties, this.session, true);
        if (commitOrder != null) {
            this.commitOrder = CommitOrderType.valueOf(commitOrder.toUpperCase());
        }
        String flushClearCache = PropertiesHandler.getPropertyValueLogDebug(EntityManagerProperties.FLUSH_CLEAR_CACHE, properties, this.session, true);
        if (flushClearCache != null) {
            this.flushClearCache = flushClearCache;
        }
    }

    /**
     * ADVANCED:
     * Re-bootstrap this factory.  This method will rebuild the EntityManagerFactory.  It should be used
     * in conjunction with a MetadataSource to allow mappings to be changed in a running system.  All existing
     * EntityMangers will continue to function with the old metadata, but new factories will use the new metadata.
     *
     * This call will throw an exception when called on EntityManagerFactoryImplDelegate
     * @param properties
     */
    @Override
    public void refreshMetadata(Map properties){
        throw new UnsupportedOperationException();
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
    @Override
    public PersistenceUnitUtil getPersistenceUnitUtil() {
        if (!isOpen()){
            throw new IllegalStateException(ExceptionLocalization.buildMessage("getpersistenceunitutil_called_on_closed_emf"));
        }
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
    @Override
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
    @Override
    public Map<String, Object> getProperties() {
        if (!this.isOpen()) {
            throw new IllegalStateException(ExceptionLocalization.buildMessage("operation_on_closed_entity_manager_factory"));
        }
        AbstractSession tempSession = this.session;
        if (tempSession != null) {
            return Collections.unmodifiableMap(EntityManagerFactoryProvider.mergeMaps(properties, tempSession.getProperties()));
        } else {
            return Collections.unmodifiableMap(properties);
        }
    }

    /**
     * @see javax.persistence.EntityManagerFactory#getCriteriaBuilder()
     * @since Java Persistence 2.0
     */
    @Override
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
    @Override
    public Metamodel getMetamodel() {
        if (!this.isOpen()) {
            throw new IllegalStateException(ExceptionLocalization.buildMessage("operation_on_closed_entity_manager_factory"));
        }
        /**
         * Login the session and initialize descriptors - if not already, subsequent calls will just return the session
         * 322585: Login the session on the first call to getMetamodel() or getCriteriaBuilder()
         * after EMF predeploy() completes.  This will do a DB login that calls
         * initializeDescriptors() so we have real Classes and not just class names for
         * MappedSuperclass metamodel descriptors.  This is provided for
         * implementations that use the metamodel before the 1st EntityManager creation.
         */
        getAbstractSession();
        return this.setupImpl.getMetamodel(session.getLoader());
    }

    /**
     * INTERNAL:
     * Get the EntityManagerFactoryImpl that created this
     * @return
     */
    public JpaEntityManagerFactory getOwner() {
        return owner;
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
    @Override
    public boolean isLoaded(Object entity, String attributeName) {
        if (Boolean.TRUE.equals(EntityManagerFactoryImpl.isLoaded(entity, attributeName, session))) {
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
    @Override
    public boolean isLoaded(Object entity) {
        if (Boolean.TRUE.equals(EntityManagerFactoryImpl.isLoaded(entity, session))) {
            return true;
        }
        return false;
    }

    /**
     * Returns the id of the entity. A generated id is not guaranteed to be
     * available until after the database insert has occurred. Returns null if
     * the entity does not yet have an id
     *
     * @param entity
     * @return id of the entity
     * @throws IllegalArgumentException
     *             if the entity is found not to be an entity.
     */
    @Override
    public Object getIdentifier(Object entity) {
        try{
            return EntityManagerFactoryImpl.getIdentifier(entity, session);
        } catch (IllegalArgumentException iae) {
            throw iae;
        } catch (Exception e){
            throw new PersistenceException(e);
        }
    }

    /**
     * Return if updates should be ordered by primary key, to avoid potential database deadlocks.
     */
    public CommitOrderType getCommitOrder() {
        return commitOrder;
    }

    /**
     * Set update ordering by primary key, to avoid potential database deadlocks.
     */
    public void setCommitOrder(CommitOrderType commitOrder) {
        this.commitOrder = commitOrder;
    }

    @Override
    public void addNamedQuery(String name, Query query) {
        DatabaseQuery unwrapped = (DatabaseQuery) query.unwrap(DatabaseQuery.class).clone();
        QueryImpl originalQuery = query.unwrap(QueryImpl.class);
        if (originalQuery.lockMode != null){
            ((ObjectLevelReadQuery)unwrapped).setLockModeType(originalQuery.lockMode.name(), session);
        }
        if (unwrapped.isReadQuery()){
            ((ReadQuery)unwrapped).setInternalMax((originalQuery.getMaxResultsInternal()));
            if (query.getFirstResult() != QueryImpl.UNDEFINED){
                ((ReadQuery)unwrapped).setFirstResult(query.getFirstResult());
            }
        }
        this.getServerSession().addQuery(name, unwrapped, true);
    }

    @Override
    public <T> T unwrap(Class<T> cls) {
        if (cls.equals(JpaEntityManagerFactory.class) || cls.equals(EntityManagerFactoryImpl.class)) {
            return (T) this;
        }else if (cls.equals(EntityManagerFactoryDelegate.class)) {
            return (T) this;
        }else if (cls.equals(Session.class) || cls.equals(AbstractSession.class)) {
            return (T) this.getAbstractSession();
        } else if (cls.equals(DatabaseSession.class) || cls.equals(DatabaseSessionImpl.class)) {
            return (T) this.getDatabaseSession();
        } else if (cls.equals(Server.class) || cls.equals(ServerSession.class)) {
            return (T) this.getServerSession();
        } else if (cls.equals(SessionBroker.class)) {
            return (T) this.getSessionBroker();
        }
        throw new PersistenceException(ExceptionLocalization.buildMessage("unable_to_unwrap_jpa", new String[]{EntityManagerFactory.class.getName(),cls.getName()}));
    }

    @Override
    public <T> void addNamedEntityGraph(String graphName, EntityGraph<T> entityGraph) {
        AttributeGroup group = ((EntityGraphImpl)entityGraph).getAttributeGroup().clone();
        group.setName(graphName);
        this.getAbstractSession().getAttributeGroups().put(graphName, group);
        this.getAbstractSession().getDescriptor(((EntityGraphImpl)entityGraph).getClassType()).addAttributeGroup(group);
    }

}
