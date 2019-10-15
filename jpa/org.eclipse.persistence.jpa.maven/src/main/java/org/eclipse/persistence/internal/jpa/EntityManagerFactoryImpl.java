/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     tware - initial implementation as part of extensibility feature
//     01/11/2013-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support
//     01/16/2013-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support
//     02/04/2013-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support
//     05/26/2016-2.7 Tomas Kraus
//       - 494610: Session Properties map should be Map<String, Object>
package org.eclipse.persistence.internal.jpa;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

import org.eclipse.persistence.config.ReferenceMode;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.PersistenceUnitLoadingException;
import org.eclipse.persistence.internal.indirection.IndirectionPolicy;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.internal.sessions.coordination.MetadataRefreshCommand;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.queries.AttributeGroup;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.FetchGroupTracker;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork.CommitOrderType;
import org.eclipse.persistence.sessions.broker.SessionBroker;
import org.eclipse.persistence.sessions.coordination.CommandManager;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.server.Server;
import org.eclipse.persistence.sessions.server.ServerSession;

/**
 * Wraps our implementation of EntityManagerFactory
 * Most operations are forwarded to the delegate.  This wrapper is used to enable
 * the refreshMetadata functionality which allows you to switch the underlying metadata for
 * an EMF after deploy time.
 * @author tware
 *
 */
public class EntityManagerFactoryImpl implements EntityManagerFactory, PersistenceUnitUtil, JpaEntityManagerFactory {
    protected EntityManagerFactoryDelegate delegate;


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
        if (descriptor.hasFetchGroupManager()){
            if (!descriptor.getFetchGroupManager().isAttributeFetched(entity, attributeName)){
                return false;
            }
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
     * Will return an instance of the Factory. Should only be called by
     * EclipseLink.
     *
     * @param serverSession
     */
    public EntityManagerFactoryImpl(AbstractSession serverSession) {
        delegate = new EntityManagerFactoryDelegate(serverSession, this);
    }

    public EntityManagerFactoryImpl(EntityManagerSetupImpl setupImpl, Map properties) {
        delegate = new EntityManagerFactoryDelegate(setupImpl, properties, this);
    }

    /**
     * Create a dynamic persistence unit which does not use the persistence.xml.
     * Instead all configuration is driven from the provided persistence unit
     * properties and descriptors.
     */
    public EntityManagerFactoryImpl(String persistenceUnitName, Map<String, Object> properties, List<ClassDescriptor> descriptors) {
        delegate = new EntityManagerFactoryDelegate(persistenceUnitName, properties, descriptors, this);
    }

    /**
     * ADVANCED:
     * Re-bootstrap this factory.  This method will rebuild the EntityManagerFactory.  It should be used
     * in conjunction with a MetadataSource to allow mappings to be changed in a running system.  All existing
     * EntityMangers will continue to function with the old metadata, but new factories will use the new metadata.
     * @param properties
     */
    @Override
    public void refreshMetadata(Map<String, Object> properties){
        EntityManagerSetupImpl setupImpl = delegate.getSetupImpl();
        if (setupImpl == null){
            throw PersistenceUnitLoadingException.cannotRefreshEntityManagerFactoryCreatedFromSession(delegate.getServerSession().getName());
        }
        String sessionName = setupImpl.getSessionName();
        Map existingProperties = delegate.getProperties();
        Map deployProperties = new HashMap();
        deployProperties.putAll(existingProperties);
        if (properties != null){
            deployProperties.putAll(properties);
        }
        EntityManagerSetupImpl newSetupImpl = setupImpl.refreshMetadata(properties);
        EntityManagerFactoryDelegate oldDelegate = delegate;
        delegate = new EntityManagerFactoryDelegate(newSetupImpl, deployProperties, this);
        // This code has been added to allow validation to occur without actually calling createEntityManager
        // RCM refresh command requires the DEPLOY_ON_STARTUP property is set to true so the listener can be added to the session.
        try{
            if (newSetupImpl.shouldGetSessionOnCreateFactory(deployProperties)) {
                ServerSession session = getServerSession();
                CommandManager rcm = session.getCommandManager();
                if (rcm != null && newSetupImpl.shouldSendMetadataRefreshCommand(deployProperties)) {
                    MetadataRefreshCommand command = new MetadataRefreshCommand(properties);
                    rcm.propagateCommand(command);
                }
                session.setRefreshMetadataListener(newSetupImpl);
            }
        } catch (RuntimeException ex) {
            if(delegate != null) {
                delegate.close();
            } else {
                newSetupImpl.undeploy();
            }
            synchronized(EntityManagerFactoryProvider.emSetupImpls){
                // bring back the old emSetupImpl and session
                EntityManagerFactoryProvider.emSetupImpls.put(sessionName, setupImpl);
                SessionManager.getManager().getSessions().put(sessionName, setupImpl.getSession());
                setupImpl.setIsMetadataExpired(false);
            }
            delegate = oldDelegate;
            throw ex;
        }
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
        return delegate.getSessionBroker();
    }

    /**
     * INTERNAL: Returns the ServerSession that the Factory will be using and
     * initializes it if it is not available. This method makes use of the
     * partially constructed session stored in our setupImpl and completes its
     * construction
     */
    @Override
    public ServerSession getServerSession() {
        return delegate.getServerSession();
    }

    /**
     * Closes this factory, releasing any resources that might be held by this
     * factory. After invoking this method, all methods on the instance will
     * throw an {@link IllegalStateException}, except for {@link #isOpen}, which
     * will return <code>false</code>.
     */
    @Override
    public synchronized void close() {
        delegate.close();
    }

    /**
     * Indicates whether or not this factory is open. Returns <code>true</code>
     * until a call to {@link #close} is made.
     */
    @Override
    public boolean isOpen() {
        return delegate.isOpen();
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
    public EntityManager createEntityManager(SynchronizationType synchronizationType) {
        return createEntityManagerImpl(null, synchronizationType);
    }

    @Override
    public EntityManager createEntityManager(SynchronizationType synchronizationType, Map map) {
        return createEntityManagerImpl(map, synchronizationType);
    }

    protected EntityManagerImpl createEntityManagerImpl(Map properties, SynchronizationType syncType) {
        EntityManagerSetupImpl setupImpl = delegate.getSetupImpl();

        if (setupImpl != null) {
            if (setupImpl.isMetadataExpired()) {
                String sessionName = setupImpl.getSessionName();
                EntityManagerSetupImpl storedImpl = null;
                synchronized (EntityManagerFactoryProvider.emSetupImpls){
                    storedImpl = EntityManagerFactoryProvider.emSetupImpls.get(sessionName);
                }
                if (storedImpl != null) {
                    delegate = new EntityManagerFactoryDelegate(storedImpl, delegate.getProperties(), this);
                }
            }
        }

        return delegate.createEntityManagerImpl(properties, syncType);
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
        return delegate;
    }

    protected void verifyOpen() {
        delegate.verifyOpen();
    }

    /**
     * The method return user defined property passed in from
     * EntityManagerFactory.
     */
    public Object getProperty(String name) {
        return delegate.getProperty(name);
    }

    /**
     * Return default join existing transaction property, allows reading through
     * write connection.
     */
    public boolean getBeginEarlyTransaction() {
        return delegate.getBeginEarlyTransaction();
    }

    /**
     * Set default join existing transaction property, allows reading through
     * write connection.
     */
    public void setBeginEarlyTransaction(boolean beginEarlyTransaction) {
        delegate.setBeginEarlyTransaction(beginEarlyTransaction);
    }

    /**
     * Return default property, allows flush before query to be avoided.
     */
    public FlushModeType getFlushMode() {
        return delegate.getFlushMode();
    }

    /**
     * Set default property, allows flush before query to be avoided.
     */
    public void setFlushMode(FlushModeType flushMode) {
        delegate.setFlushMode(flushMode);
    }

    /**
     * Return default property, allows weak unit of work references.
     */
    public ReferenceMode getReferenceMode() {
        return delegate.getReferenceMode();
    }

    /**
     * Set default property, allows weak unit of work references.
     */
    public void setReferenceMode(ReferenceMode referenceMode) {
        delegate.setReferenceMode(referenceMode);
    }

    /**
     * Return default property to avoid resuming unit of work if going to be
     * closed on commit anyway.
     */
    public boolean getCloseOnCommit() {
        return delegate.getCloseOnCommit();
    }

    /**
     * Set default property to avoid resuming unit of work if going to be closed
     * on commit anyway.
     */
    public void setCloseOnCommit(boolean closeOnCommit) {
        delegate.setCloseOnCommit(closeOnCommit);
    }

    /**
     * Return default property to avoid discover new objects in unit of work if
     * application always uses persist.
     */
    public boolean getPersistOnCommit() {
        return delegate.getPersistOnCommit();
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
        return delegate.getPersistenceUnitUtil();
    }

    /**
     * Set default property to avoid discover new objects in unit of work if
     * application always uses persist.
     */
    public void setPersistOnCommit(boolean persistOnCommit) {
        delegate.setPersistOnCommit(persistOnCommit);
    }

    /**
     * Return default property to avoid discover new objects in unit of work if
     * application always uses persist.
     */
    public boolean getCommitWithoutPersistRules() {
        return delegate.getCommitWithoutPersistRules();
    }

    /**
     * Set default property to avoid discover new objects in unit of work if
     * application always uses persist.
     */
    public void setCommitWithoutPersistRules(boolean commitWithoutPersistRules) {
        delegate.setCommitWithoutPersistRules(commitWithoutPersistRules);
    }

    /**
     * Return the default FlashClearCache mode to be used. Relevant only in case
     * call to flush method followed by call to clear method.
     *
     * @see org.eclipse.persistence.config.FlushClearCache
     */
    public String getFlushClearCache() {
        return delegate.getFlushClearCache();
    }

    /**
     * Set the default FlashClearCache mode to be used. Relevant only in case
     * call to flush method followed by call to clear method.
     *
     * @see org.eclipse.persistence.config.FlushClearCache
     */
    public void setFlushClearCache(String flushClearCache) {
        delegate.setFlushClearCache(flushClearCache);
    }

    /**
     * Return the default to determine if does-exist should be performed on
     * persist.
     */
    public boolean shouldValidateExistence() {
        return delegate.shouldValidateExistence();
    }

    /**
     * Set the default to determine if does-exist should be performed on
     * persist.
     */
    public void setShouldValidateExistence(boolean shouldValidateExistence) {
        delegate.setShouldValidateExistence(shouldValidateExistence);
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
        return delegate.getCache();
    }

    /**
     * @see javax.persistence.EntityManagerFactory#getProperties()
     * @since Java Persistence API 2.0
     */
    @Override
    public Map<String, Object> getProperties() {
        return delegate.getProperties();
    }

    @Override
    public DatabaseSessionImpl getDatabaseSession() {
        return delegate.getDatabaseSession();
    }
    /**
     * @see javax.persistence.EntityManagerFactory#getCriteriaBuilder()
     * @since Java Persistence 2.0
     */
    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        return delegate.getCriteriaBuilder();
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
        return delegate.getMetamodel();
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
        delegate.setMetamodel(aMetamodel);
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
        return delegate.isLoaded(entity, attributeName);
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
        return delegate.isLoaded(entity);
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
    @Override
    public Object getIdentifier(Object entity) {
        return delegate.getIdentifier(entity);
    }

    /**
     * Return if updates should be ordered by primary key to avoid possible database deadlocks.
     */
    public CommitOrderType getCommitOrder() {
        return delegate.getCommitOrder();
    }

    /**
     * Set updates should be ordered by primary key to avoid possible database deadlocks.
     */
    public void setCommitOrder(CommitOrderType commitOrder) {
        delegate.setCommitOrder(commitOrder);
    }

    @Override
    public void addNamedQuery(String name, Query query) {
        QueryImpl queryImpl = query.unwrap(QueryImpl.class);
        DatabaseQuery unwrapped = (DatabaseQuery) queryImpl.getDatabaseQueryInternal().clone();
        if (queryImpl.lockMode != null){
            ((ObjectLevelReadQuery)unwrapped).setLockModeType(queryImpl.lockMode.name(), getServerSession());
        }
        if (unwrapped.isReadQuery()){
            ((ReadQuery)unwrapped).setInternalMax((queryImpl.getMaxResultsInternal()));
            ((ReadQuery)unwrapped).setFirstResult((queryImpl.getFirstResult()));
        }
        this.getServerSession().addQuery(name, unwrapped, true);
    }

    @Override
    public <T> T unwrap(Class<T> cls) {
        if (cls.equals(JpaEntityManagerFactory.class) || cls.equals(EntityManagerFactoryImpl.class)) {
            return (T) this;
        }else if (cls.equals(EntityManagerFactoryDelegate.class)) {
            return (T) this.delegate;
        }else if (cls.equals(Session.class) || cls.equals(AbstractSession.class)) {
            return (T) this.delegate.getAbstractSession();
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
        this.getServerSession().getAttributeGroups().put(graphName, group);
        this.getServerSession().getDescriptor(((EntityGraphImpl)entityGraph).getClassType()).addAttributeGroup(group);
    }

}
