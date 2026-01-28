/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 1998, 2024 IBM Corporation and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
//
//     05/28/2008-1.0M8 Andrei Ilitchev
//        - 224964: Provide support for Proxy Authentication through JPA.
//        Added setProperties method to be used in case properties couldn't be passed to createEM method.
//        The properties now set to the uow's parent - not to the uow itself.
//        In case there's no active transaction, close method now releases uow.
//        UowImpl was amended to allow value holders instantiation even after it has been released,
//        the parent ClientSession is released, too.
//     03/19/2009-2.0 Michael O'Brien
//       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
//     07/13/2009-2.0 Guy Pelletier
//       - 277039: JPA 2.0 Cache Usage Settings
//     02/08/2012-2.4 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     14/05/2012-2.4 Guy Pelletier
//       - 376603: Provide for table per tenant support for multitenant applications
//     06/20/2012-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     08/24/2012-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     09/13/2012-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     08/11/2012-2.5 Guy Pelletier
//       - 393867: Named queries do not work when using EM level Table Per Tenant Multitenancy.
//     02/16/2017-2.6 Jody Grassel
//       - 512255: Eclipselink JPA/Auditing capablity in EE Environment fails with JNDI name parameter type
//     07/16/2019-2.7 Jody Grassel
//       - 547173: EntityManager.unwrap(Connection.class) returns null
//     09/02/2019-2.7 Alexandre Jacob
//        - 527415: Fix code when locale is tr, az or lt
//     08/23/2023: Tomas Kraus
//       - New Jakarta Persistence 3.2 Features
package org.eclipse.persistence.internal.jpa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;

import javax.sql.DataSource;

import jakarta.persistence.CacheRetrieveMode;
import jakarta.persistence.CacheStoreMode;
import jakarta.persistence.ConnectionConsumer;
import jakarta.persistence.ConnectionFunction;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.FindOption;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.LockModeType;
import jakarta.persistence.LockOption;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.PessimisticLockException;
import jakarta.persistence.Query;
import jakarta.persistence.RefreshOption;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.persistence.SynchronizationType;
import jakarta.persistence.TransactionRequiredException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.TypedQueryReference;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaSelect;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.metamodel.Metamodel;
import org.eclipse.persistence.annotations.CacheKeyType;
import org.eclipse.persistence.config.EntityManagerProperties;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.config.ReferenceMode;
import org.eclipse.persistence.descriptors.CMPPolicy;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.VersionLockingPolicy;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.exceptions.JPQLException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.descriptors.OptimisticLockingPolicy;
import org.eclipse.persistence.internal.helper.BasicTypeHelperImpl;
import org.eclipse.persistence.internal.identitymaps.CacheId;
import org.eclipse.persistence.internal.jpa.querydef.CriteriaDeleteImpl;
import org.eclipse.persistence.internal.jpa.querydef.CriteriaQueryImpl;
import org.eclipse.persistence.internal.jpa.querydef.CriteriaSelectInternal;
import org.eclipse.persistence.internal.jpa.querydef.CriteriaUpdateImpl;
import org.eclipse.persistence.internal.jpa.transaction.EntityTransactionImpl;
import org.eclipse.persistence.internal.jpa.transaction.EntityTransactionWrapper;
import org.eclipse.persistence.internal.jpa.transaction.JTATransactionWrapper;
import org.eclipse.persistence.internal.jpa.transaction.TransactionWrapper;
import org.eclipse.persistence.internal.jpa.transaction.TransactionWrapperImpl;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.internal.sessions.PropertiesHandler;
import org.eclipse.persistence.internal.sessions.RepeatableWriteUnitOfWork;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.queries.AttributeGroup;
import org.eclipse.persistence.queries.Call;
import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.FetchGroupTracker;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.ResultSetMappingQuery;
import org.eclipse.persistence.queries.SQLResultSetMapping;
import org.eclipse.persistence.queries.StoredProcedureCall;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.DatasourceLogin;
import org.eclipse.persistence.sessions.DefaultConnector;
import org.eclipse.persistence.sessions.JNDIConnector;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.sessions.UnitOfWork.CommitOrderType;
import org.eclipse.persistence.sessions.broker.SessionBroker;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.server.ConnectionPolicy;
import org.eclipse.persistence.sessions.server.Server;
import org.eclipse.persistence.sessions.server.ServerSession;

/**
 * <p>
 * <b>Purpose</b>: Contains the implementation of the EntityManager.
 * <p>
 * <b>Description</b>: This class provides the implementation for the combined
 * EclipseLink and JPA EntityManager class.
 * <p>
 * <b>Responsibilities</b>: It is responsible for tracking transaction state and
 * the objects within that transaction.
 *
 * @see jakarta.persistence.EntityManager
 * @see org.eclipse.persistence.jpa.JpaEntityManager
 *
 * @author gyorke
 * @since TopLink Essentials - JPA 1.0
 */
public class EntityManagerImpl implements org.eclipse.persistence.jpa.JpaEntityManager {
    protected enum OperationType {FIND, REFRESH, LOCK}

    /** Allows transparent transactions across JTA and local transactions. */
    private TransactionWrapperImpl transaction;

    /** Store if this entity manager has been closed. */
    protected boolean isOpen;

    /** Stores the UnitOfWork representing the persistence context. */
    protected RepeatableWriteUnitOfWork extendedPersistenceContext;

    /** Stores a session used for read-only queries. */
    protected AbstractSession readOnlySession;

    /**
     * References the DatabaseSession that this deployment is using.
     */
    protected AbstractSession databaseSession;

    /**
     * References to the parent factory that has created this entity manager.
     * Ensures that the factory is not garbage collected.
     */
    protected EntityManagerFactoryDelegate factory;

    /**
     * Join existing transaction property, allows reading through write
     * connection.
     */
    protected boolean beginEarlyTransaction;

    /** Local properties passed from createEntityManager. */
    protected Map<String, Object> properties;

    /** Flush mode property, allows flush before query to be avoided. */
    protected FlushModeType flushMode;

    /**
     * Reference mode property, allows weak unit of work references to allow
     * garbage collection during a transaction.
     */
    protected ReferenceMode referenceMode;

    /**
     * Connection policy used to create ClientSession, allows using a different
     * pool/connection/exclusive connections.
     * Not used in SessionBroker case (composite persistence unit case).
     */
    protected ConnectionPolicy connectionPolicy;

    /**
     * In case of composite persistence unit this map is used instead of connectionPolicy attribute.
     * Member sessions' ConnectionPolicies keyed by sessions' names (composite members' persistence unit names).
     * Used only in SessionBroker case (composite persistence unit case): in that case guaranteed to be always non null.
     */
    protected Map<String, ConnectionPolicy> connectionPolicies;

    /**
     * Keep a list of openQueries that are executed in this entity manager.
     */
    protected WeakHashMap<QueryImpl, QueryImpl> openQueriesMap;

    /**
     * Property to avoid resuming unit of work if going to be closed on commit
     * anyway.
     */
    protected boolean closeOnCommit;

    /**
     * Property to avoid discover new objects in unit of work if application
     * always uses persist.
     */
    protected boolean persistOnCommit;

    /**
     * Property to avoid writing to the cache on commit (merge)
     */
    protected boolean cacheStoreBypass;

    /**
     * The FlashClearCache mode to be used. Relevant only in case call to flush
     * method followed by call to clear method.
     *
     * @see org.eclipse.persistence.config.FlushClearCache
     */
    protected String flushClearCache;

    /** Determine if does-exist should be performed on persist. */
    protected boolean shouldValidateExistence;

    /** Allow updates to be ordered by id to avoid possible deadlocks. */
    protected org.eclipse.persistence.sessions.UnitOfWork.CommitOrderType commitOrder;

    protected boolean commitWithoutPersistRules;

    /** Tracks if this EntityManager should automatically associate with the transaction or not*/
    protected SynchronizationType syncType;

    abstract static class PropertyProcessor {
        abstract void process(String name, Object value, EntityManagerImpl em);
    }
    static Map<String, PropertyProcessor> processors = new HashMap<>() {
        {
            put(EntityManagerProperties.JOIN_EXISTING_TRANSACTION, new PropertyProcessor() {
            @Override
            void process(String name, Object value, EntityManagerImpl em) {
                em.beginEarlyTransaction = "true".equalsIgnoreCase(getPropertiesHandlerProperty(name, (String)value));
            }});
            put(EntityManagerProperties.PERSISTENCE_CONTEXT_REFERENCE_MODE, new PropertyProcessor() {
            @Override
            void process(String name, Object value, EntityManagerImpl em) {
                em.referenceMode = ReferenceMode.valueOf(getPropertiesHandlerProperty(name, (String)value));
                if (em.hasActivePersistenceContext()) {
                    em.extendedPersistenceContext.log(SessionLog.WARNING, SessionLog.PROPERTIES, "entity_manager_sets_property_while_context_is_active", new Object[]{name});
                }
            }});
            put(EntityManagerProperties.PERSISTENCE_CONTEXT_FLUSH_MODE, new PropertyProcessor() {
            @Override
            void process(String name, Object value, EntityManagerImpl em) {
                em.flushMode = FlushModeType.valueOf(getPropertiesHandlerProperty(name, (String)value));
            }});
            put(EntityManagerProperties.PERSISTENCE_CONTEXT_CLOSE_ON_COMMIT, new PropertyProcessor() {
            @Override
            void process(String name, Object value, EntityManagerImpl em) {
                em.closeOnCommit = "true".equalsIgnoreCase(getPropertiesHandlerProperty(name, (String)value));
                if(em.hasActivePersistenceContext()) {
                    em.extendedPersistenceContext.setResumeUnitOfWorkOnTransactionCompletion(!em.closeOnCommit);
                }
            }});
            put(EntityManagerProperties.PERSISTENCE_CONTEXT_PERSIST_ON_COMMIT, new PropertyProcessor() {
            @Override
            void process(String name, Object value, EntityManagerImpl em) {
                em.persistOnCommit = "true".equalsIgnoreCase(getPropertiesHandlerProperty(name, (String)value));
                if(em.hasActivePersistenceContext()) {
                    em.extendedPersistenceContext.setShouldDiscoverNewObjects(em.persistOnCommit);
                }
            }});
            put(EntityManagerProperties.PERSISTENCE_CONTEXT_COMMIT_WITHOUT_PERSIST_RULES, new PropertyProcessor() {
            @Override
            void process(String name, Object value, EntityManagerImpl em) {
                em.commitWithoutPersistRules = "true".equalsIgnoreCase(getPropertiesHandlerProperty(name, (String)value));
                if (em.hasActivePersistenceContext()) {
                    em.extendedPersistenceContext.setDiscoverUnregisteredNewObjectsWithoutPersist(em.commitWithoutPersistRules);
                }
            }});
            put(EntityManagerProperties.VALIDATE_EXISTENCE, new PropertyProcessor() {
            @Override
            void process(String name, Object value, EntityManagerImpl em) {
                em.shouldValidateExistence = "true".equalsIgnoreCase(getPropertiesHandlerProperty(name, (String)value));
                if (em.hasActivePersistenceContext()) {
                    em.extendedPersistenceContext.setShouldValidateExistence(em.shouldValidateExistence);
                }
            }});
            put(EntityManagerProperties.ORDER_UPDATES, new PropertyProcessor() {
            @Override
            void process(String name, Object value, EntityManagerImpl em) {
                if ("true".equalsIgnoreCase(getPropertiesHandlerProperty(name, (String)value))) {
                    em.commitOrder = CommitOrderType.ID;
                } else {
                    em.commitOrder = CommitOrderType.NONE;
                }
                if (em.hasActivePersistenceContext()) {
                    em.extendedPersistenceContext.setCommitOrder(em.commitOrder);
                }
            }});
            put(EntityManagerProperties.PERSISTENCE_CONTEXT_COMMIT_ORDER, new PropertyProcessor() {
            @Override
            void process(String name, Object value, EntityManagerImpl em) {
                em.commitOrder = CommitOrderType.valueOf(getPropertiesHandlerProperty(name, (String)value).toUpperCase(Locale.ROOT));
                if (em.hasActivePersistenceContext()) {
                    em.extendedPersistenceContext.setCommitOrder(em.commitOrder);
                }
            }});
            put(EntityManagerProperties.FLUSH_CLEAR_CACHE, new PropertyProcessor() {
            @Override
            void process(String name, Object value, EntityManagerImpl em) {
                em.flushClearCache = getPropertiesHandlerProperty(name, (String)value);
                if( em.hasActivePersistenceContext()) {
                    em.extendedPersistenceContext.setFlushClearCache(em.flushClearCache);
                }
            }});
            put(QueryHints.CACHE_STORE_MODE, new PropertyProcessor() {
            @Override
            void process(String name, Object value, EntityManagerImpl em) {
                // This property could be a string or an enum.
                em.cacheStoreBypass = value.equals(CacheStoreMode.BYPASS) || value.equals(CacheStoreMode.BYPASS.name());
                if(em.hasActivePersistenceContext()) {
                    em.extendedPersistenceContext.setShouldStoreByPassCache(em.cacheStoreBypass);
                }
            }});

            PropertyProcessor connectionPolicyPropertyProcessor = new PropertyProcessor() {
            @Override
            void process(String name, Object value, EntityManagerImpl em) {
                // Property used to create ConnectionPolicy has changed - already existing ConnectionPolicy should be removed.
                // A new one will be created when the new active persistence context is created.
                em.connectionPolicy = null;
                if (em.hasActivePersistenceContext()) {
                    em.extendedPersistenceContext.log(SessionLog.WARNING, SessionLog.PROPERTIES, "entity_manager_sets_property_while_context_is_active", new Object[]{name});
                }
            }};
            put(EntityManagerProperties.EXCLUSIVE_CONNECTION_MODE, connectionPolicyPropertyProcessor);
            put(EntityManagerProperties.EXCLUSIVE_CONNECTION_IS_LAZY, connectionPolicyPropertyProcessor);
            put(EntityManagerProperties.JTA_DATASOURCE, connectionPolicyPropertyProcessor);
            put(EntityManagerProperties.NON_JTA_DATASOURCE, connectionPolicyPropertyProcessor);
            put(EntityManagerProperties.JDBC_DRIVER, connectionPolicyPropertyProcessor);
            put(EntityManagerProperties.JDBC_URL, connectionPolicyPropertyProcessor);
            put(EntityManagerProperties.JDBC_USER, connectionPolicyPropertyProcessor);
            put(EntityManagerProperties.JDBC_PASSWORD, connectionPolicyPropertyProcessor);
            put(EntityManagerProperties.CONNECTION_POLICY, connectionPolicyPropertyProcessor);

            put(EntityManagerProperties.ORACLE_PROXY_TYPE, new PropertyProcessor() {
            @Override
            void process(String name, Object value, EntityManagerImpl em) {
                if (em.hasActivePersistenceContext()) {
                    em.extendedPersistenceContext.log(SessionLog.WARNING, SessionLog.PROPERTIES, "entity_manager_sets_property_while_context_is_active", new Object[]{name});
                }
            }});

            put(EntityManagerProperties.COMPOSITE_UNIT_PROPERTIES, new PropertyProcessor() {
            @Override
            void process(String name, Object value, EntityManagerImpl em) {
                if( em.connectionPolicies != null) {
                    Map<String, ?> mapOfProperties = (Map<String, ?>) value;
                    Iterator<String> it = mapOfProperties.keySet().iterator();
                    while (it.hasNext()) {
                        String sessionName = it.next();
                        if (em.connectionPolicies.containsKey(sessionName)) {
                            // Property used to create ConnectionPolicy has changed - already existing ConnectionPolicy should be removed.
                            // A new one will be created when the new active persistence context is created.
                            em.connectionPolicies.put(sessionName, null);
                        }
                    }
                    if (em.hasActivePersistenceContext()) {
                        em.extendedPersistenceContext.log(SessionLog.WARNING, SessionLog.PROPERTIES, "entity_manager_sets_property_while_context_is_active", new Object[]{name});
                    }
                }
            }});
        }
    };

    /**
     * Constructor returns an EntityManager assigned to the a particular
     * DatabaseSession.
     *
     * @param sessionName
     *            the DatabaseSession name that should be used. This constructor
     *            can potentially throw EclipseLink exceptions regarding the
     *            existence, or errors with the specified session.
     */
    public EntityManagerImpl(String sessionName) {
        this(SessionManager.getManager().getSession(sessionName), null, SynchronizationType.SYNCHRONIZED);
    }

    /**
     * Return the weak reference to the open queries.
     */
    protected Map<QueryImpl, QueryImpl> getOpenQueriesMap() {
        if (openQueriesMap == null) {
            openQueriesMap = new WeakHashMap<>();
        }

        return openQueriesMap;
    }

    /**
     * Return the weak reference to the open queries.
     */
    protected Set<QueryImpl> getOpenQueriesSet() {
        return getOpenQueriesMap().keySet();
    }

    /**
     * Queries that leave the connection and are executed against this entity
     * manager will be added here. On rollback or commit any left over open
     * queries should be closed.
     *
     */
    public void addOpenQuery(QueryImpl query) {
        getOpenQueriesMap().put(query, query);

        // If there is an open entity transaction, tag the query to it to be closed
        // on commit or rollback.
        Object transaction = checkForTransaction(false);
        if (transaction != null && transaction instanceof EntityTransactionImpl) {
            ((EntityTransactionImpl) transaction).addOpenQuery(query);
        }
    }

    /**
     * Constructor called from the EntityManagerFactory to create an
     * EntityManager
     *
     * @param databaseSession
     *            the databaseSession assigned to this deployment.
     */
    public EntityManagerImpl(AbstractSession databaseSession, SynchronizationType syncType) {
        this(databaseSession, null, syncType);
    }

    /**
     * Constructor called from the EntityManagerFactory to create an
     * EntityManager
     *
     * @param databaseSession
     *            the databaseSession assigned to this deployment. Note: The
     *            properties argument is provided to allow properties to be
     *            passed into this EntityManager, but there are currently no
     *            such properties implemented
     */
    public EntityManagerImpl(AbstractSession databaseSession, Map properties, SynchronizationType syncType) {
        this(new EntityManagerFactoryImpl(databaseSession).unwrap(), properties, syncType);
    }

    /**
     * Constructor called from the EntityManagerFactory to create an
     * EntityManager
     *
     * @param factory
     *            the EntityMangerFactoryImpl that created this entity manager.
     *            Note: The properties argument is provided to allow properties
     *            to be passed into this EntityManager, but there are currently
     *            no such properties implemented
     */
    public EntityManagerImpl(EntityManagerFactoryDelegate factory, Map properties, SynchronizationType syncType) {
        this.factory = factory;
        this.databaseSession = factory.getAbstractSession();
        this.beginEarlyTransaction = factory.getBeginEarlyTransaction();
        this.closeOnCommit = factory.getCloseOnCommit();
        this.flushMode = factory.getFlushMode();
        this.persistOnCommit = factory.getPersistOnCommit();
        this.commitWithoutPersistRules = factory.getCommitWithoutPersistRules();
        this.referenceMode = factory.getReferenceMode();
        this.flushClearCache = factory.getFlushClearCache();
        this.shouldValidateExistence = factory.shouldValidateExistence();
        this.commitOrder = factory.getCommitOrder();
        this.isOpen = true;
        this.cacheStoreBypass = false;
        this.syncType = syncType;
        initialize(properties);
    }

    /**
     * Initialize the state after construction.
     */
    protected void initialize(Map properties) {
        detectTransactionWrapper();
        // Cache default ConnectionPolicy. If ConnectionPolicy property(ies) specified
        // then connectionPolicy will be set to null and re-created when when active persistence context is created.
        if(this.databaseSession.isServerSession()) {
            this.connectionPolicy = ((ServerSession)this.databaseSession).getDefaultConnectionPolicy();
        } else if (this.databaseSession.isBroker()) {
            SessionBroker broker = (SessionBroker)this.databaseSession;
            this.connectionPolicies = new HashMap<>(broker.getSessionsByName().size());
            Iterator<Map.Entry<String, AbstractSession>> it = broker.getSessionsByName().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, AbstractSession> entry = it.next();
                this.connectionPolicies.put(entry.getKey(), ((ServerSession)entry.getValue()).getDefaultConnectionPolicy());
            }
        }
        // bug 236249: In JPA session.setProperty() throws
        // UnsupportedOperationException.
        if (properties != null) {
            this.properties = new HashMap<>(properties);
            if(!properties.isEmpty()) {
                processProperties();
            }
        }
    }

    /**
     * Clear the persistence context, causing all managed entities to become
     * detached. Changes made to entities that have not been flushed to the
     * database will not be persisted.
     */
    @Override
    public void clear() {
        try {
            verifyOpen();
            if (this.extendedPersistenceContext != null) {
                if (checkForTransaction(false) == null) {
                    //     259993: WebSphere 7.0 during a JPAEMPool.putEntityManager() afterCompletion callback
                    // may attempt to clear an entityManager in lifecyle/state 4 with a transaction commit active
                    // that is in the middle of a commit for an insert or update by calling em.clear(true).
                    //     We only clear the entityManager if we are in the states
                    // (Birth == 0, WriteChangesFailed==3, Death==5 or AfterExternalTransactionRolledBack==6).
                    // If we are in one of the following *Pending states (1,2 and 4) we defer the clear() to the release() call later.
                    // Note: the single state CommitTransactionPending==2 may never happen as a result of an em.clear
                    if(this.extendedPersistenceContext.isSynchronized() &&
                            ( this.extendedPersistenceContext.isCommitPending()
                            || this.extendedPersistenceContext.isAfterWriteChangesButBeforeCommit()
                            || this.extendedPersistenceContext.isMergePending())) {
                        // when jta transaction afterCompleteion callback will have completed merge,
                        // the uow will be released.
                        // Change sets will be cleared, but the cache will be kept.
                        // uow still could be used for instantiating of ValueHolders
                        // after it's released.
                        extendedPersistenceContext.setResumeUnitOfWorkOnTransactionCompletion(false);
                    } else {
                        // clear all change sets and cache
                        this.extendedPersistenceContext.clearForClose(true);
                        this.extendedPersistenceContext.release();
                        this.extendedPersistenceContext.getParent().release();
                    }
                    this.extendedPersistenceContext = null;
                } else {
                    // clear all change sets created after the last flush and
                    // cache
                    this.extendedPersistenceContext.clear(true);
                }
            }
        } catch (RuntimeException exception) {
            setRollbackOnly();
            throw exception;
        }
    }

    /**
     * Internal method called by EntityTransactionImpl class in case of
     * transaction rollback. The caller is responsible for releasing
     * extendedPersistenceContext and it's parent.
     */
    public void removeExtendedPersistenceContext() {
        this.extendedPersistenceContext = null;
    }

    /**
     * If in a transaction this method will check for existence and register the
     * object if it is new. The instance of the entity provided will become
     * managed.
     *
     * @throws IllegalArgumentException
     *             if the given Object is not an entity.
     */
    @Override
    public void persist(Object entity) {
        try {
            verifyOpen();
            if (entity == null) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("not_an_entity", new Object[] { null }));
            }
            try {
                getActivePersistenceContext(checkForTransaction(false)).registerNewObjectForPersist(entity, new IdentityHashMap());
            } catch (RuntimeException exception) {
                if (exception instanceof ValidationException) {
                    throw new EntityExistsException(exception.getLocalizedMessage(), exception);
                }
                throw exception;
            }
        } catch (RuntimeException exception) {
            setRollbackOnly();
            throw exception;
        }
    }

    /**
     * Merge the state of the given entity into the current persistence context,
     * using the unqualified class name as the entity name.
     *
     * @return the instance that the state was merged to
     */
    @Override
    public <T> T merge(T entity) {
        try {
            verifyOpen();
            return (T) mergeInternal(entity);
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * Merge the state of the given entity into the current persistence context,
     * using the unqualified class name as the entity name.
     *
     * @return the instance that the state was merged to
     * @throws IllegalArgumentException
     *             if given Object is not an entity or is a removed entity
     */
    protected Object mergeInternal(Object entity) {
        if (entity == null) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("not_an_entity", new Object[] { null }));
        }
        Object merged = null;
        UnitOfWorkImpl context = getActivePersistenceContext(checkForTransaction(false));
        try {
            merged = context.mergeCloneWithReferences(entity, MergeManager.CASCADE_BY_MAPPING, true);
        } catch (org.eclipse.persistence.exceptions.OptimisticLockException ole) {
            throw new jakarta.persistence.OptimisticLockException(ole);
        }
        return merged;
    }

    /**
     * Remove the instance.
     *
     * @throws IllegalArgumentException
     *             if Object passed in is not an entity
     */
    @Override
    public void remove(Object entity) {
        try {
            verifyOpen();
            if (entity == null) { // gf732 - check for null
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("not_an_entity", new Object[] { null }));
            }
            try {
                getActivePersistenceContext(checkForTransaction(false)).performRemove(entity, new IdentityHashMap());
            } catch (RuntimeException e) {
                throw e;
            }
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * Find by primary key.
     *
     * @param entityClass
     *            - the entity class to find.
     * @param primaryKey
     *            - the entity primary key value, or primary key class, or a
     *            List of primary key values.
     * @return the found entity instance or null if the entity does not exist
     * @throws IllegalArgumentException
     *             if the first argument does not denote an entity type or the
     *             second argument is not a valid type for that entity's primary
     *             key.
     */
    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey) {
        return find(entityClass, primaryKey, null, getQueryHints(entityClass, OperationType.FIND));
    }

    /**
     * Find by primary key, using the specified properties. Search for an entity
     * of the specified class and primary key. If the entity instance is
     * contained in the persistence context it is returned from there. If a
     * vendor-specific property or hint is not recognized, it is silently
     * ignored.
     *
     * @param properties
     *            standard and vendor-specific properties
     * @return the found entity instance or null if the entity does not exist
     * @throws IllegalArgumentException
     *             if the first argument does not denote an entity type or the
     *             second argument is is not a valid type for that entity's
     *             primary key or is null
     *
     * @since Java Persistence API 2.0
     */
    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
        return find(entityClass, primaryKey, null, properties);
    }

    /**
     * Find by primary key and lock. Search for an entity of the specified class
     * and primary key and lock it with respect to the specified lock type. If
     * the entity instance is contained in the persistence context it is
     * returned from there. If the entity is found within the persistence
     * context and the lock mode type is pessimistic and the entity has a
     * version attribute, the persistence provider must perform optimistic
     * version checks when obtaining the database lock. If these checks fail,
     * the OptimisticLockException will be thrown. If the lock mode type is
     * pessimistic and the entity instance is found but cannot be locked: - the
     * PessimisticLockException will be thrown if the database locking failure
     * causes transaction-level rollback. - the LockTimeoutException will be
     * thrown if the database locking failure causes only statement-level
     * rollback
     *
     * @return the found entity instance or null if the entity does not exist
     * @throws IllegalArgumentException
     *             if the first argument does not denote an entity type or the
     *             second argument is not a valid type for that entity's primary
     *             key or is null
     * @throws TransactionRequiredException
     *             if there is no transaction and a lock mode other than NONE is
     *             set
     * @throws OptimisticLockException
     *             if the optimistic version check fails
     * @throws PessimisticLockException
     *             if pessimistic locking fails and the transaction is rolled
     *             back
     * @throws LockTimeoutException
     *             if pessimistic locking fails and only the statement is rolled
     *             back
     * @throws PersistenceException
     *             if an unsupported lock call is made
     */
    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode) {
        return find(entityClass, primaryKey, lockMode, getQueryHints(entityClass, OperationType.FIND));
    }

    /**
     * Find by primary key and lock. Search for an entity of the specified class
     * and primary key and lock it with respect to the specified lock type. If
     * the entity instance is contained in the persistence context it is
     * returned from there. If the entity is found within the persistence
     * context and the lock mode type is pessimistic and the entity has a
     * version attribute, the persistence provider must perform optimistic
     * version checks when obtaining the database lock. If these checks fail,
     * the OptimisticLockException will be thrown. If the lock mode type is
     * pessimistic and the entity instance is found but cannot be locked: - the
     * PessimisticLockException will be thrown if the database locking failure
     * causes transaction-level rollback. - the LockTimeoutException will be
     * thrown if the database locking failure causes only statement-level
     * rollback If a vendor-specific property or hint is not recognized, it is
     * silently ignored. Portable applications should not rely on the standard
     * timeout hint. Depending on the database in use and the locking mechanisms
     * used by the provider, the hint may or may not be observed.
     *
     * @param properties
     *            standard and vendor-specific properties and hints
     * @return the found entity instance or null if the entity does not exist
     * @throws IllegalArgumentException
     *             if the first argument does not denote an entity type or the
     *             second argument is not a valid type for that entity's primary
     *             key or is null
     * @throws TransactionRequiredException
     *             if there is no transaction and a lock mode other than NONE is
     *             set
     * @throws OptimisticLockException
     *             if the optimistic version check fails
     * @throws PessimisticLockException
     *             if pessimistic locking fails and the transaction is rolled
     *             back
     * @throws LockTimeoutException
     *             if pessimistic locking fails and only the statement is rolled
     *             back
     * @throws PersistenceException
     *             if an unsupported lock call is made
     */
    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties) {
        try {
            verifyOpen();
            if (lockMode != null && !lockMode.equals(LockModeType.NONE)){
                checkForTransaction(true);
            }
            AbstractSession session = this.databaseSession;
            ClassDescriptor descriptor = session.getDescriptor(entityClass);
            // PERF: Avoid uow creation for read-only.
            if (descriptor == null || descriptor.isDescriptorTypeAggregate()) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("unknown_bean_class", new Object[] { entityClass }));
            }
            if (!descriptor.shouldBeReadOnly() || !descriptor.isSharedIsolation()) {
                session = (AbstractSession) getActiveSession();
            } else {
                session = (AbstractSession) getReadOnlySession();
            }

            // Be sure to use the descriptor from the active session.
            if (descriptor.hasTablePerMultitenantPolicy()) {
                descriptor = session.getDescriptor(entityClass);
            }
            return (T) findInternal(descriptor, session, primaryKey, lockMode, properties);
        } catch (LockTimeoutException e) {
            throw e;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    // TODO-API-3.2 - Missing tests
    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, FindOption... options) {
        // Passing default find query hints, may be overwritten by options
        FindOptionUtils.Options parsedOptions = FindOptionUtils.parse(getQueryHints(entityClass, OperationType.FIND), options);
        return find(entityClass, primaryKey, parsedOptions.lockModeType(), parsedOptions.properties());
    }

    // TODO-API-3.2 - Missing tests
    @Override
    public <T> T find(EntityGraph<T> entityGraph, Object primaryKey, FindOption... options) {
        Class<T> entityClass = ((EntityGraphImpl<T>)entityGraph).getClassType();
        Map<String, Object> properties = getQueryHints(entityClass, OperationType.FIND);
        properties.put(QueryHints.JPA_FETCH_GRAPH, entityGraph);
        FindOptionUtils.Options parsedOptions = FindOptionUtils.parse(getQueryHints(entityClass, OperationType.FIND), options);
        return find(entityClass, primaryKey, parsedOptions.lockModeType(), parsedOptions.properties());
    }

    /**
     * Find by primary key.
     *
     * @param entityName
     *            - the entity class to find.
     * @param primaryKey
     *            - the entity primary key value, or primary key class, or a
     *            List of primary key values.
     * @return the found entity instance or null, if the entity does not exist.
     * @throws IllegalArgumentException
     *             if the first argument does not indicate an entity or if the
     *             second argument is not a valid type for that entity's
     *             primaryKey.
     */
    public Object find(String entityName, Object primaryKey) {
        try {
            verifyOpen();
            AbstractSession session = (AbstractSession) getActiveSession();
            ClassDescriptor descriptor = session.getDescriptorForAlias(entityName);
            if (descriptor == null || descriptor.isDescriptorTypeAggregate()) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("unknown_entitybean_name", new Object[] { entityName }));
            }
            return findInternal(descriptor, session, primaryKey, null, null);
        } catch (LockTimeoutException e) {
            throw e;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * Find by primary key.
     *
     * @param descriptor
     *            - the entity class to find.
     * @param id
     *            - the entity primary key value, or primary key class, or a
     *            List of primary key values.
     * @return the found entity instance or null, if the entity does not exist.
     * @throws IllegalArgumentException
     *             if the first argument does not denote an entity type or the
     *             second argument is not a valid type for that entity's primary
     *             key.
     */
    protected Object findInternal(ClassDescriptor descriptor, AbstractSession session, Object id, LockModeType lockMode, Map<String, Object> properties) {
        if (id == null) { // gf721 - check for null PK
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("null_pk"));
        }

        Object primaryKey;
        if (id instanceof List) {
            if (descriptor.getCacheKeyType() == CacheKeyType.ID_VALUE) {
                if (((List)id).isEmpty()) {
                    primaryKey = null;
                } else {
                    primaryKey = ((List)id).get(0);
                }
            } else {
                primaryKey = new CacheId(((List)id).toArray());
            }
        } else if (id instanceof CacheId) {
            primaryKey = id;
        } else {
            CMPPolicy policy = descriptor.getCMPPolicy();
            Class<Object> pkClass = policy.getPKClass();
            if ((pkClass != null) && (pkClass != id.getClass()) && (!BasicTypeHelperImpl.getInstance().isStrictlyAssignableFrom(pkClass, id.getClass()))) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("invalid_pk_class", new Object[] { descriptor.getCMPPolicy().getPKClass(), id.getClass() }));
            }
            primaryKey = policy.createPrimaryKeyFromId(id, session);
        }

        // If the LockModeType is PESSIMISTIC*, check the unitofwork cache and return the entity if it has previously been locked
        // Must avoid using the new JPA 2.0 Enum values directly to allow JPA 1.0 jars to still work.
        if (lockMode != null && (lockMode.name().equals(ObjectLevelReadQuery.PESSIMISTIC_READ) || lockMode.name().equals(ObjectLevelReadQuery.PESSIMISTIC_WRITE)
                || lockMode.name().equals(ObjectLevelReadQuery.PESSIMISTIC_FORCE_INCREMENT))) {
            // PERF: check if the UnitOfWork has pessimistically locked objects to avoid a cache query
            if (session.isUnitOfWork() && ((UnitOfWorkImpl)session).hasPessimisticLockedObjects()) {
                ReadObjectQuery query = new ReadObjectQuery();
                query.setReferenceClass(descriptor.getJavaClass());
                query.setSelectionId(primaryKey);
                query.checkCacheOnly();
                Object cachedEntity = session.executeQuery(query);
                if (cachedEntity != null && ((UnitOfWorkImpl)session).isPessimisticLocked(cachedEntity)) {
                    return cachedEntity;
                }
            }
        }

        // Translate deprecated properties to the current names
        EntityManagerFactoryProvider.translateOldProperties(properties, this.databaseSession);

        // Get the read object query and apply the properties to it.
        // PERF: use descriptor defined query to avoid extra query creation.
        ReadObjectQuery query = descriptor.getQueryManager().getReadObjectQuery();

        if (query == null) {
            // The properties/query hints and setIsExecutionClone etc. is set
            // in the getReadObjectQuery.
            query = getReadObjectQuery(descriptor.getJavaClass(), primaryKey, properties);
        } else {
            query.checkPrepare(session, null);
            query = (ReadObjectQuery) query.clone();

            // Apply the properties if there are some.
            QueryHintsHandler.apply(properties, query, session.getLoader(), session);

            query.setIsExecutionClone(true);
            query.setSelectionId(primaryKey);
        }

        // Apply any EclipseLink defaults if they haven't been set through
        // the properties.
        if (properties == null ||
                ( !properties.containsKey(QueryHints.CACHE_USAGE)
                          && !properties.containsKey(QueryHints.CACHE_RETRIEVE_MODE)
                          && !properties.containsKey(QueryHints.CACHE_STORE_MODE))) {
            query.conformResultsInUnitOfWork();
        }

        return executeQuery(query, lockMode, session);
    }

    /**
     * Synchronize the persistence context with the underlying database.
     */
    @Override
    public void flush() {
        try {
            // Based on spec definition 3 possible exceptions are thrown
            // IllegalState by verifyOpen,
            // TransactionRequired by check for transaction
            // PersistenceException for all others.
            // but there is a tck test that checks for illegal state exception
            // and the
            // official statement is that the spec 'intended' for
            // IllegalStateException to be raised.

            verifyOpen();
            try {
                try {
                    getActivePersistenceContext(checkForTransaction(true)).writeChanges();
                } catch (org.eclipse.persistence.exceptions.OptimisticLockException eclipselinkOLE) {
                    throw new OptimisticLockException(eclipselinkOLE);
                }
            } catch (EclipseLinkException e) {
                throw new PersistenceException(e);
            }
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    protected void detectTransactionWrapper() {
        if (this.databaseSession.hasExternalTransactionController()) {
            setJTATransactionWrapper();
        } else {
            setEntityTransactionWrapper();
        }
    }

    /**
     * Execute the locking query.
     */
    private Object executeQuery(ReadObjectQuery query, LockModeType lockMode, AbstractSession session) {
        // Make sure we set the lock mode type if there is one. It will
        // be handled in the query prepare statement. Setting the lock mode
        // will validate that a valid locking policy is in place if needed. If
        // a true value is returned it indicates that we were unable to set the
        // lock mode, throw an exception.
        if (lockMode != null && query.setLockModeType(lockMode.name(), session)) {
            throw new PersistenceException(ExceptionLocalization.buildMessage("ejb30-wrong-lock_called_without_version_locking-index", null));
        }

        try {
            return session.executeQuery(query);
        } catch (DatabaseException e) {
            if (!isPessimistic(lockMode)) {
                throw e;
            }

            // If we catch a database exception as a result of executing a
            // pessimistic locking query we need to ask the platform which
            // JPA 2.0 locking exception we should throw. It will be either
            // be a PessimisticLockException or a LockTimeoutException (if
            // the query was executed using a wait timeout value)

            if (isUsingWaitTimeout(query) && isLockTimeoutException(query, e)) {
                throw new LockTimeoutException(e);
            }

            throw new PessimisticLockException(e);
        }
    }

    private boolean isPessimistic(LockModeType lockMode) {
        return lockMode != null && lockMode.name().contains(ObjectLevelReadQuery.PESSIMISTIC_);
    }

    private boolean isUsingWaitTimeout(ReadObjectQuery query) {
        Integer waitTimeout = query.getEffectiveWaitTimeout();

        return waitTimeout != null && waitTimeout > 0;
    }

    /**
     * Ask the platform if it is a lock timeout
     *
     * @param query the query for which the exception was thrown
     * @param e the thrown exception
     * @return true for a lock timeout, false otherwise
     */
    private boolean isLockTimeoutException(ReadObjectQuery query, DatabaseException e) {
        return query.getExecutionSession().getPlatform().isLockTimeoutException(e);
    }

    /**
     * Refresh the state of the instance from the database.
     *
     * @param entity
     *            instance registered in the current persistence context.
     */
    @Override
    public void refresh(Object entity) {
        refresh(entity, null, getQueryHints(entity, OperationType.REFRESH));
    }

    /**
     * Refresh the state of the instance from the database, using the specified
     * properties, and overwriting changes made to the entity, if any. If a
     * vendor-specific property or hint is not recognized, it is silently
     * ignored.
     *
     * @param properties
     *            standard and vendor-specific properties
     * @throws IllegalArgumentException
     *             if the instance is not an entity or the entity is not managed
     * @throws TransactionRequiredException
     *             if invoked on a container-managed entity manager of type
     *             PersistenceContextType.TRANSACTION and there is no
     *             transaction.
     * @throws EntityNotFoundException
     *             if the entity no longer exists in the database
     *
     * @since Java Persistence API 2.0
     */
    @Override
    public void refresh(Object entity, Map<String, Object> properties) {
        refresh(entity, null, properties);
    }

    /**
     * Refresh the state of the instance from the database, overwriting changes
     * made to the entity, if any, and lock it with respect to given lock mode
     * type. If the lock mode type is pessimistic and the entity instance is
     * found but cannot be locked: - the PessimisticLockException will be thrown
     * if the database locking failure causes transaction-level rollback. - the
     * LockTimeoutException will be thrown if the database locking failure
     * causes only statement-level rollback.
     *
     * @throws IllegalArgumentException
     *             if the instance is not an entity or the entity is not managed
     * @throws TransactionRequiredException
     *             if there is no transaction
     * @throws EntityNotFoundException
     *             if the entity no longer exists in the database
     * @throws PessimisticLockException
     *             if pessimistic locking fails and the transaction is rolled
     *             back
     * @throws LockTimeoutException
     *             if pessimistic locking fails and only the statement is rolled
     *             back
     * @throws PersistenceException
     *             if an unsupported lock call is made
     */
    @Override
    public void refresh(Object entity, LockModeType lockMode) {
        refresh(entity, lockMode, getQueryHints(entity, OperationType.REFRESH));
    }

    /**
     * Refresh the state of the instance from the database, overwriting changes
     * made to the entity, if any, and lock it with respect to given lock mode
     * type. If the lock mode type is pessimistic and the entity instance is
     * found but cannot be locked: - the PessimisticLockException will be thrown
     * if the database locking failure causes transaction-level rollback. - the
     * LockTimeoutException will be thrown if the database locking failure
     * causes only statement-level rollback If a vendor-specific property or
     * hint is not recognized, it is silently ignored. Portable applications
     * should not rely on the standard timeout hint. Depending on the database
     * in use and the locking mechanisms used by the provider, the hint may or
     * may not be observed.
     *
     * @param properties
     *            standard and vendor-specific properties and hints
     * @throws IllegalArgumentException
     *             if the instance is not an entity or the entity is not managed
     * @throws TransactionRequiredException
     *             if there is no transaction
     * @throws EntityNotFoundException
     *             if the entity no longer exists in the database
     * @throws PessimisticLockException
     *             if pessimistic locking fails and the transaction is rolled
     *             back
     * @throws LockTimeoutException
     *             if pessimistic locking fails and only the statement is rolled
     *             back
     * @throws PersistenceException
     *             if an unsupported lock call is made
     */
    @Override
    public void refresh(Object entity, LockModeType lockMode, Map<String, Object> properties) {
        try {
            verifyOpen();
            boolean validateExistence =  (lockMode != null && !lockMode.equals(LockModeType.NONE));
            UnitOfWorkImpl uow = getActivePersistenceContext(checkForTransaction(validateExistence));
            if (!contains(entity, uow)) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("cant_refresh_not_managed_object", new Object[] { entity }));
            }

            // Get the read object query and apply the properties to it.
            ReadObjectQuery query = getReadObjectQuery(entity, properties);

            // Apply any EclipseLink defaults if they haven't been set through
            // the properties.
            if (properties == null || !properties.containsKey(QueryHints.REFRESH)) {
                query.refreshIdentityMapResult();
            }

            if (properties == null || !properties.containsKey(QueryHints.REFRESH_CASCADE)) {
                query.cascadeByMapping();
            }

            Object refreshedEntity = executeQuery(query, lockMode, uow);
            if (refreshedEntity == null) {
                // bug5955326, ReadObjectQuery will now ensure the object is
                // invalidated if refresh returns null.
                throw new EntityNotFoundException(ExceptionLocalization.buildMessage("entity_no_longer_exists_in_db", new Object[] { entity }));
            }
        } catch (LockTimeoutException e) {
            throw e;
        } catch (RuntimeException exception) {
            setRollbackOnly();
            throw exception;
        }
    }

    // TODO-API-3.2 - Missing tests
    @Override
    public void refresh(Object entity, RefreshOption... options) {
        RefreshOptionUtils.Options parsedOptions = RefreshOptionUtils.parse(options);
        refresh(entity, parsedOptions.lockModeType(), parsedOptions.properties());
    }

    /**
     * Check if the instance belongs to the current persistence context.
     *
     * @throws IllegalArgumentException
     *             if given Object is not an entity
     */
    @Override
    public boolean contains(Object entity) {
        try {
            verifyOpen();
            if (entity == null) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("not_an_entity", new Object[] { null }));
            }
            ClassDescriptor descriptor = this.databaseSession.getDescriptors().get(entity.getClass());
            if (descriptor == null || descriptor.isDescriptorTypeAggregate()) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("not_an_entity", new Object[] { entity }));
            }

            if ((!hasActivePersistenceContext())) {
                return false;
            }

            return contains(entity, getActivePersistenceContext(checkForTransaction(false)));
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * Check if the instance belongs to the current persistence context.
     */
    protected boolean contains(Object entity, UnitOfWork uow) {
        return uow.isObjectRegistered(entity) && !((UnitOfWorkImpl) uow).isObjectDeleted(entity);
    }

    @Override
    public jakarta.persistence.Query createDescriptorNamedQuery(String queryName, Class<?> descriptorClass) {
        return createDescriptorNamedQuery(queryName, descriptorClass, null);
    }

    @Override
    public jakarta.persistence.Query createDescriptorNamedQuery(String queryName, Class<?> descriptorClass, List argumentTypes) {
        try {
            verifyOpen();
            ClassDescriptor descriptor = this.databaseSession.getDescriptor(descriptorClass);
            if (descriptor != null) {
                DatabaseQuery query = descriptor.getQueryManager().getLocalQueryByArgumentTypes(queryName, argumentTypes);
                if (query != null) {
                    return new EJBQueryImpl(query, this);
                }
            }
            return null;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * Create an instance of Query for executing a named query (in EJBQL or
     * native SQL).
     *
     * @param name
     *            the name of a query defined in metadata
     * @return the new query instance
     */
    @Override
    public Query createNamedQuery(String name) {
        return createNamedQueryInternal(name, null);
    }

    /**
     * Create an instance of TypedQuery for executing a
     * named query (in the Java Persistence query language
     * or in native SQL).
     * @param name the name of a query defined in metadata
     * @param resultClass the type of the query result
     * @return the new query instance
     * @throws IllegalArgumentException if a query has not been
     *      defined with the given name or if the query string is
     *      found to be invalid
     */
    @Override
    public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
        return createNamedQueryInternal(name, null);
    }

    @Override
    public <T> TypedQuery<T> createQuery(TypedQueryReference<T> typedQueryReference) {
        Objects.requireNonNull(typedQueryReference, ExceptionLocalization.buildMessage("typed_query_reference_is_null"));
        return createNamedQueryInternal(typedQueryReference.getName(), typedQueryReference.getHints());
    }

    /**
     * Create an instance of {@link TypedQuery} for executing a named query written in the Jakarta Persistence query language
     * or in native SQL.
     *
     * @param name the name of the query
     * @param hints a {@link Map} with query hints or {@code null} to pass no hints
     * @return the new {@link TypedQuery} instance
     * @param <T> the query result type
     */
    private <T> TypedQuery<T> createNamedQueryInternal(String name, Map<String,Object> hints) {
        try {
            verifyOpen();
            EJBQueryImpl<T> query = new EJBQueryImpl<>(name, this, true);
            query.getDatabaseQueryInternal();
            if (hints != null) {
                hints.forEach(query::setHint);
            }
            return query;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * Create an instance of StoredProcedureQuery for executing a
     * stored procedure in the database.
     * @param name name assigned to the stored procedure query
     * in metadata
     * @return the new stored procedure query instance
     * @throws IllegalArgumentException if a query has not been
     * defined with the given name
     */
    @Override
    public StoredProcedureQuery createNamedStoredProcedureQuery(String name) {
        try {
            verifyOpen();
            StoredProcedureQueryImpl query = new StoredProcedureQueryImpl(name, this);
            query.getDatabaseQueryInternal();
            return query;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * Create an instance of Query for executing a native SQL query.
     *
     * @param sqlString
     *            a native SQL query string
     * @return the new query instance
     */
    @Override
    public Query createNativeQuery(String sqlString) {
        try {
            verifyOpen();
            return new EJBQueryImpl(EJBQueryImpl.buildSQLDatabaseQuery(sqlString, this.databaseSession.getLoader(), this.databaseSession), this);
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * This method is used to create a query using SQL. The class, must be the
     * expected return type.
     */
    @Override
    @SuppressWarnings({"rawtypes"})
    public Query createNativeQuery(String sqlString, Class resultType) {
        try {
            verifyOpen();
            return new EJBQueryImpl(EJBQueryImpl.buildSQLDatabaseQuery(resultType, sqlString, this.databaseSession.getLoader(), this.databaseSession), this);
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * Create an instance of Query for executing a native SQL query.
     *
     * @param sqlString
     *            a native SQL query string
     * @param resultSetMapping
     *            the name of the result set mapping
     * @return the new query instance
     * @throws IllegalArgumentException
     *             if query string is not valid
     */
    @Override
    public Query createNativeQuery(String sqlString, String resultSetMapping) {
        try {
            verifyOpen();
            ResultSetMappingQuery query = new ResultSetMappingQuery();
            query.setSQLResultSetMappingName(resultSetMapping);
            query.setSQLString(sqlString);
            query.setIsUserDefined(true);
            return new EJBQueryImpl(query, this);
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * This method returns the current session to the requestor. The current
     * session will be a the active UnitOfWork within a transaction and will be
     * a 'scrap' UnitOfWork outside of a transaction. The caller is concerned
     * about the results then the getSession() or getUnitOfWork() API should be
     * called.
     */
    @Override
    public Session getActiveSession() {
        return getActivePersistenceContext(checkForTransaction(false));
    }

    /**
     * This method returns the current session to the requestor. The current
     * session will be a the active UnitOfWork within a transaction and will be
     * a 'scrap' UnitOfWork outside of a transaction. The caller is concerned
     * about the results then the getSession() or getUnitOfWork() API should be
     * called.
     */
    public AbstractSession getActiveSessionIfExists() {
        // When requesting an active session, if there isn't one but we have
        // table per tenant descriptors, make sure we return one. The 'scrap'
        // session will not be initialized for table per tenant multitenancy.
        if (hasActivePersistenceContext() || getAbstractSession().hasTablePerTenantDescriptors()) {
            return (AbstractSession) getActiveSession();
        } else {
            return getAbstractSession();
        }
    }

    /**
     * Return the underlying provider object for the EntityManager, if
     * available. The result of this method is implementation specific.
     */
    @Override
    public Object getDelegate() {
        try {
            verifyOpen();
            return this;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * Get the flush mode that applies to all objects contained in the
     * persistence context.
     *
     * @return flushMode
     */
    @Override
    public FlushModeType getFlushMode() {
        try {
            verifyOpen();
            return flushMode;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * This method will return the active UnitOfWork
     */
    @Override
    public UnitOfWork getUnitOfWork() {
        return getActivePersistenceContext(checkForTransaction(false));
    }

    /**
     * This method will return a Session outside of a transaction and null
     * within a transaction.
     */
    @Override
    public Session getSession() {
        if (checkForTransaction(false) == null) {
            return this.databaseSession.acquireNonSynchronizedUnitOfWork(this.referenceMode);
        }
        return null;
    }

    /**
     * Returns the resource-level transaction object. The EntityTransaction
     * instance may be used serially to begin and commit multiple transactions.
     *
     * @return EntityTransaction instance
     * @throws IllegalStateException
     *             if invoked on a JTA EntityManager.
     */
    @Override
    public jakarta.persistence.EntityTransaction getTransaction() {
        try {
            return ((TransactionWrapper) transaction).getTransaction();
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    @Override
    public <C> void runWithConnection(ConnectionConsumer<C> action) {
        if (getAbstractSession().getAccessors().size() > 1) {
            getAbstractSession().log(SessionLog.WARNING, SessionLog.CONNECTION, "entity_manager_has_multiple_connections");
        }
        @SuppressWarnings("unchecked")
        C connection = (C) getAbstractSession().getAccessor().getDatasourceConnection();
        try {
            action.accept(connection);
        } catch (Exception e) {
            transaction.setRollbackOnlyInternal();
            throw new PersistenceException(
                    ExceptionLocalization.buildMessage(
                            "entity_manager_with_connection_failed", new String[] {e.getLocalizedMessage()}), e);
        }
    }

    @Override
    public <C, T> T callWithConnection(ConnectionFunction<C, T> function) {
        if (getAbstractSession().getAccessors().size() > 1) {
            getAbstractSession().log(SessionLog.WARNING, SessionLog.CONNECTION, "entity_manager_has_multiple_connections");
        }
        @SuppressWarnings("unchecked")
        C connection = (C) getAbstractSession().getAccessor().getDatasourceConnection();
        try {
            return function.apply(connection);
        } catch (Exception e) {
            transaction.setRollbackOnlyInternal();
            throw new PersistenceException(
                    ExceptionLocalization.buildMessage(
                            "entity_manager_with_connection_failed", new String[] {e.getLocalizedMessage()}), e);
        }
    }

    /**
     * The method search for user defined property passed in from EntityManager,
     * if it is not found then search for it from EntityManagerFactory
     * properties.
     *
     */
    public Object getProperty(String name) {
        Object propertyValue = null;
        if (name == null) {
            return null;
        }
        if (this.properties != null) {
            propertyValue = this.properties.get(name);
        }
        if (propertyValue == null) {
            propertyValue = this.factory.getAbstractSession().getProperty(name);
        }
        return propertyValue;
    }

    /**
     * Build a selection query for the primary key values.
     */
    protected ReadObjectQuery getReadObjectQuery(Class<?> referenceClass, Object primaryKey, Map properties) {
        ReadObjectQuery query = getReadObjectQuery(properties);
        query.setReferenceClass(referenceClass);
        query.setSelectionId(primaryKey);
        return query;
    }

    /**
     * Build a selection query using the given properties.
     */
    protected ReadObjectQuery getReadObjectQuery(Map properties) {
        ReadObjectQuery query = new ReadObjectQuery();

        // Apply the properties if there are some.
        QueryHintsHandler.apply(properties, query, this.databaseSession.getDatasourcePlatform().getConversionManager().getLoader(), this.databaseSession);
        query.setIsExecutionClone(true);
        return query;
    }

    /**
     * Build a selection query for the given entity.
     */
    protected ReadObjectQuery getReadObjectQuery(Object entity, Map properties) {
        ReadObjectQuery query = getReadObjectQuery(properties);
        query.setSelectionObject(entity);
        return query;
    }

    /**
     * Get an instance, whose state may be lazily fetched. If the requested
     * instance does not exist in the database, throws EntityNotFoundException
     * when the instance state is first accessed. (The container is permitted to
     * throw EntityNotFoundException when get is called.) The application should
     * not expect that the instance state will be available upon detachment,
     * unless it was accessed by the application while the entity manager was
     * open.
     *
     * @return the found entity instance.
     * @throws IllegalArgumentException
     *             if the first argument does not denote an entity type or the
     *             second argument is not a valid type for that entity's primary
     *             key.
     * @throws EntityNotFoundException
     *             if the entity state cannot be accessed.
     */
    @Override
    public <T> T getReference(Class<T> entityClass, Object primaryKey) {
        try {
            verifyOpen();
            UnitOfWork session = (UnitOfWork) getActiveSession();
            T reference = session.getReference(entityClass, primaryKey);
            if (reference == null) {
                Object[] args = { primaryKey };
                String message = ExceptionLocalization.buildMessage("no_entities_retrieved_for_get_reference", args);
                throw new jakarta.persistence.EntityNotFoundException(message);
            }
            return reference;
        } catch (RuntimeException exception) {
            setRollbackOnly();
            throw exception;
        }
    }
    @Override
    public <T> T getReference(T entity) {
        try {
            verifyOpen();
            UnitOfWork session = (UnitOfWork) getActiveSession();
            T reference = session.getReference(entity);
            if (reference == null) {
                throw new jakarta.persistence.EntityNotFoundException(
                        ExceptionLocalization.buildMessage("no_entities_retrieved_for_get_reference", new Object[] {entity}));
            }
            return reference;
        } catch (RuntimeException exception) {
            setRollbackOnly();
            throw exception;
        }
    }

    /**
     * Return a read-only session (client session) for read-only operations.
     */
    public Session getReadOnlySession() {
        if (this.extendedPersistenceContext != null && this.extendedPersistenceContext.isActive()) {
            return this.extendedPersistenceContext.getParent();
        }
        if (this.readOnlySession != null) {
            return this.readOnlySession;
        }
        if (this.databaseSession.isServerSession()) {
            this.readOnlySession = ((ServerSession)this.databaseSession).acquireClientSession(connectionPolicy, properties);
            return this.readOnlySession;
        } else if(this.databaseSession.isBroker()) {
            this.readOnlySession = ((SessionBroker)this.databaseSession).acquireClientSessionBroker(this.connectionPolicies, (Map)this.properties.get(EntityManagerProperties.COMPOSITE_UNIT_PROPERTIES));
            return this.readOnlySession;
        } else {
            // currently this can't happen - the databaseSession is either ServerSession or SessionBroker.
            return this.databaseSession;
        }
    }

    /**
     * Return the underlying database session
     */
    @Override
    public DatabaseSessionImpl getDatabaseSession() {
        return (DatabaseSessionImpl)this.databaseSession;
    }

    /**
     * Return the underlying database session
     */
    @Override
    public AbstractSession getAbstractSession() {
        return this.databaseSession;
    }

    /**
     * INTERNAL:
     * Set the underlying database session
     */
    public void setAbstractSession(AbstractSession session) {
        this.databaseSession = session;
    }

    /**
     * Return the underlying server session, throws ClassCastException if it's not a ServerSession.
     */
    @Override
    public ServerSession getServerSession() {
        return (ServerSession)this.databaseSession;
    }

    /**
     * Return the underlying session broker, throws ClassCastException if it's not a SessionBroker.
     */
    @Override
    public SessionBroker getSessionBroker() {
        return (SessionBroker)this.databaseSession;
    }

    /**
     * Return the member DatabaseSessionImpl that maps cls in session broker.
     * Return null if either not a session broker or cls is not mapped.
     * Session broker implement composite persistence unit.
     */
    @Override
    public DatabaseSessionImpl getMemberDatabaseSession(Class<?> cls) {
        if(this.databaseSession.isBroker()) {
            return (DatabaseSessionImpl) this.databaseSession.getSessionForClass(cls);
        } else {
            return null;
        }
    }

    /**
     * Return the member ServerSession that maps cls in session broker.
     * Return null if either not a session broker or cls is not mapped.
     * Session broker implement composite persistence unit.
     */
    @Override
    public ServerSession getMemberServerSession(Class<?> cls) {
        if(this.databaseSession.isBroker()) {
            return (ServerSession) this.databaseSession.getSessionForClass(cls);
        } else {
            return null;
        }
    }

    /**
     * Return the name of member session that maps cls.
     * Return null if either not a session broker or cls is not mapped.
     * Session broker implement composite persistence unit.
     */
    @Override
    public String getMemberSessionName(Class<?> cls) {
        if(this.databaseSession.isBroker()) {
            return this.databaseSession.getSessionForClass(cls).getName();
        } else {
            return null;
        }
    }

    /**
     * This method is used to create a query using a EclipseLink Expression and
     * the return type.
     */
    @Override
    public jakarta.persistence.Query createQuery(Expression expression, Class<?> resultType) {
        try {
            verifyOpen();
            DatabaseQuery query = createQueryInternal(expression, resultType);
            return new EJBQueryImpl(query, this);
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * This method is used to create a query using a EclipseLink DatabaseQuery.
     */
    @Override
    public jakarta.persistence.Query createQuery(DatabaseQuery databaseQuery) {
        try {
            verifyOpen();
            return new EJBQueryImpl(databaseQuery, this);
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    @Override
    public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
        try {
            verifyOpen();
            return new EJBQueryImpl<>(((CriteriaQueryImpl<T>) criteriaQuery).translate(), this);
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    @Override
    public <T> TypedQuery<T> createQuery(CriteriaSelect<T> selectQuery) {
        try {
            verifyOpen();
            return new EJBQueryImpl<>(((CriteriaSelectInternal<T>) selectQuery).translate(), this);
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * This method is used to create a query using a EclipseLink by example.
     */
    @Override
    public jakarta.persistence.Query createQueryByExample(Object exampleObject) {
        try {
            verifyOpen();
            ReadAllQuery query = new ReadAllQuery(exampleObject.getClass());
            query.setExampleObject(exampleObject);
            return new EJBQueryImpl(query, this);
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * This method is used to create a query using a EclipseLink Call.
     */
    @Override
    public jakarta.persistence.Query createQuery(Call call) {
        try {
            verifyOpen();
            DataReadQuery query = new DataReadQuery(call);
            return new EJBQueryImpl(query, this);
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * This method is used to create a query using a EclipseLink Call.
     */
    @Override
    public jakarta.persistence.Query createQuery(Call call, Class<?> entityClass) {
        try {
            verifyOpen();
            ReadAllQuery query = new ReadAllQuery(entityClass, call);
            return new EJBQueryImpl(query, this);
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * Create an instance of Query for executing an JPQL query.
     *
     * @param jpqlString
     *            an JPQL query string
     * @return the new query instance
     */
    @Override
    public Query createQuery(String jpqlString) {
        try {
            verifyOpen();
            EJBQueryImpl ejbqImpl;

            try {
                ejbqImpl = new EJBQueryImpl(jpqlString, this);
            } catch (JPQLException exception) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("wrap_ejbql_exception") + ": " + exception.getLocalizedMessage(), exception);
            }

            return ejbqImpl;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * Create an instance of TypedQuery for executing a
     * Java Persistence query language statement.
     * @param qlString a Java Persistence query string
     * @param resultClass the type of the query result
     * @return the new query instance
     * @throws IllegalArgumentException if the query string is found
     *         to be invalid
     */
    @Override
    public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass){
        try {
            verifyOpen();
            EJBQueryImpl ejbqImpl;
            try {
                ejbqImpl = new EJBQueryImpl(qlString, this, resultClass);
            } catch (JPQLException exception) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("wrap_ejbql_exception") + ": " + exception.getLocalizedMessage(), exception);
            }
            return (TypedQuery<T>)ejbqImpl;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * This method is used to create a query using a EclipseLink Expression and
     * the return type.
     */
    protected DatabaseQuery createQueryInternal(Expression expression, Class<?> resultType) {
        ReadAllQuery query = new ReadAllQuery(resultType);
        query.setSelectionCriteria(expression);
        return query;
    }

    /**
     * Create an instance of <code>StoredProcedureQuery</code> for executing a
     * stored procedure in the database.
     * <p>Parameters must be registered before the stored procedure can be
     * executed.
     * <p>If the stored procedure returns one or more result sets, any result
     * set will be returned as a list of type Object[].
     *
     * @param procedureName name of the stored procedure in the database
     * @return the new stored procedure query instance
     * @throws IllegalArgumentException if a stored procedure of the given name
     *         does not exist (or the query execution will fail)
     * @since EclipseLink 2.5/Java Persistence 2.1
     */
    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName) {
        try {
            verifyOpen();
            StoredProcedureCall call = new StoredProcedureCall();
            call.setProcedureName(procedureName);
            return new StoredProcedureQueryImpl(StoredProcedureQueryImpl.buildResultSetMappingQuery(new ArrayList<>(), call), this);
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * Create an instance of <code>StoredProcedureQuery</code> for executing a
     * stored procedure in the database.
     * <p>Parameters must be registered before the stored procedure can be
     * executed.
     * <p>The <code>resultClass</code> arguments must be specified in the order
     * in which the result sets will be returned by the stored procedure
     * invocation.
     *
     * @param procedureName name of the stored procedure in the database
     * @param resultClasses classes to which the result sets produced by the
     *        stored procedure are to be mapped
     * @return the new stored procedure query instance
     * @throws IllegalArgumentException if a stored procedure of the given name
     *         does not exist (or the query execution will fail)
     * @since EclipseLink 2.5/Java Persistence 2.1
     */
    @Override
    @SuppressWarnings({"rawtypes"})
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName, Class... resultClasses) {
        try {
            verifyOpen();
            StoredProcedureCall call = new StoredProcedureCall();
            call.setProcedureName(procedureName);
            call.setHasMultipleResultSets(resultClasses.length > 1);

            List<SQLResultSetMapping> sqlResultSetMappings = new ArrayList<>();
            for (Class<?> resultClass : resultClasses) {
                sqlResultSetMappings.add(new SQLResultSetMapping(resultClass));
            }

            return new StoredProcedureQueryImpl(StoredProcedureQueryImpl.buildResultSetMappingQuery(sqlResultSetMappings, call), this);
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * Create an instance of <code>StoredProcedureQuery</code> for executing a
     * stored procedure in the database.
     * <p>Parameters must be registered before the stored procedure can be
     * executed.
     * <p>The <code>resultSetMapping</code> arguments must be specified in the
     * order in which the result sets will be returned by the stored procedure
     * invocation.
     *
     * @param procedureName name of the stored procedure in the database
     * @param resultSetMappings the names of the result set mappings
     *        to be used in mapping result sets
     *        returned by the stored procedure
     * @return the new stored procedure query instance
     * @throws IllegalArgumentException if a stored procedure or
     *         result set mapping of the given name does not exist
     *         (or the query execution will fail)
     * @since EclipseLink 2.5/Java Persistence 2.1
     */
    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName, String... resultSetMappings) {
        try {
            verifyOpen();
            StoredProcedureCall call = new StoredProcedureCall();
            call.setProcedureName(procedureName);
            call.setHasMultipleResultSets(resultSetMappings.length > 1);

            List<String> sqlResultSetMappingNames = new ArrayList<>();
            Collections.addAll(sqlResultSetMappingNames, resultSetMappings);

            return new StoredProcedureQueryImpl(StoredProcedureQueryImpl.buildResultSetMappingNameQuery(sqlResultSetMappingNames, call), this);
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * <p>
     * Closes this EntityManager.
     *
     * <p>
     * After invoking this method, all methods on the instance will throw an
     * {@link IllegalStateException} except for {@link #isOpen}, which will
     * return <code>false</code> .
     * </p>
     *
     * <p>
     * This should be called when a method is finished with the EntityManager in
     * a bean-managed transaction environment or when executed outside a
     * container. Closing of the EntityManager is handled by the container when
     * using container-managed transactions.
     * </p>
     */
    @Override
    public void close() {
        try {
            verifyOpen();
            closeOpenQueries();
            this.isOpen = false;
            this.factory = null;
            this.databaseSession = null;
            // Do not invalidate the metaModel field
            // (a reopened emf will re-populate the same metaModel)
            // (a new persistence unit will generate a new metaModel)
            if (this.extendedPersistenceContext != null) {
                // bug210677, checkForTransaction returns null in
                // afterCompletion - in this case check for uow being
                // synchronized.
                if (checkForTransaction(false) == null && !this.extendedPersistenceContext.isSynchronized()) {
                    // uow.release clears change sets but keeps the cache.
                    // uow still could be used for instantiating of ValueHolders
                    // after it's released.
                    this.extendedPersistenceContext.release();
                    this.extendedPersistenceContext.getParent().release();
                } else {
                    // when commit will be called uow will be released, all
                    // change sets will be cleared, but the cache will be kept.
                    // uow still could be used for instantiating of ValueHolders
                    // after it's released.
                    this.extendedPersistenceContext.setResumeUnitOfWorkOnTransactionCompletion(false);
                }
                this.extendedPersistenceContext = null;
            }
            if (this.readOnlySession != null) {
                this.readOnlySession.release();
                this.readOnlySession = null;
            }
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * Close any open queries executed against this entity manager.0
     */
    protected void closeOpenQueries() {
        for (QueryImpl openQuery : getOpenQueriesSet()) {
            openQuery.close();
        }
    }

    /**
     * Internal method. Indicates whether flushMode is AUTO.
     *
     * @return boolean
     */
    public boolean isFlushModeAUTO() {
        return flushMode == FlushModeType.AUTO;
    }

    /**
     * Indicates whether or not this entity manager and its entity manager factory
     * are open. Returns
     * <code>true</code> until a call to {@link #close} is made.
     */
    @Override
    public boolean isOpen() {
        return isOpen && factory.isOpen();
    }

    /**
     * Set the lock mode for an entity object contained in the persistence
     * context.
     *
     * @throws PersistenceException
     *             if an unsupported lock call is made
     * @throws IllegalArgumentException
     *             if the instance is not an entity or is a detached entity
     * @throws jakarta.persistence.TransactionRequiredException
     *             if there is no transaction
     */
    @Override
    public void lock(Object entity, LockModeType lockMode) {
        lock(entity, lockMode, getQueryHints(entity, OperationType.LOCK));
    }

    /**
     * Set the lock mode for an entity object contained in the persistence
     * context.
     *
     * @throws PersistenceException
     *             if an unsupported lock call is made
     * @throws IllegalArgumentException
     *             if the instance is not an entity or is a detached entity
     * @throws jakarta.persistence.TransactionRequiredException
     *             if there is no transaction
     */
    @Override
    public void lock(Object entity, LockModeType lockMode, Map<String, Object> properties) {
        try {
            verifyOpen();

            if (entity == null) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("not_an_entity", new Object[] { null }));
            }

            UnitOfWorkImpl uow = getActivePersistenceContext(checkForTransaction(true)); // Throws TransactionRequiredException if no active transaction.

            if (!contains(entity, uow)) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("cant_lock_not_managed_object", new Object[] { entity }));
            }

            if (lockMode == null || lockMode.name().equals(ObjectLevelReadQuery.NONE)) {
                // Nothing to do
                return;
            }
            // Must avoid using the new JPA 2.0 Enum values directly to allow JPA 1.0 jars to still work.
            if (lockMode.name().equals(ObjectLevelReadQuery.PESSIMISTIC_READ) || lockMode.name().equals(ObjectLevelReadQuery.PESSIMISTIC_WRITE )
                    || lockMode.name().equals(ObjectLevelReadQuery.PESSIMISTIC_FORCE_INCREMENT)) {

                // return if the entity has previously been pessimistically locked
                if (uow.isPessimisticLocked(entity)) {
                    return;
                }

                // Get the read object query and apply the properties to it.
                ReadObjectQuery query = getReadObjectQuery(entity, properties);

                // Apply any EclipseLink defaults if they haven't been set
                // through
                // the properties.
                if (properties == null || !properties.containsKey(QueryHints.REFRESH)) {
                    query.refreshIdentityMapResult();
                }

                if (properties == null || !properties.containsKey(QueryHints.REFRESH_CASCADE)) {
                    query.cascadePrivateParts();
                }

                executeQuery(query, lockMode, getActivePersistenceContext(checkForTransaction(false)));
            } else {
                RepeatableWriteUnitOfWork context = getActivePersistenceContext(checkForTransaction(false));
                ClassDescriptor descriptor = context.getDescriptor(entity);
                OptimisticLockingPolicy lockingPolicy = descriptor.getOptimisticLockingPolicy();
                if ((lockingPolicy == null) || !(lockingPolicy instanceof VersionLockingPolicy)) {
                    throw new PersistenceException(ExceptionLocalization.buildMessage("ejb30-wrong-lock_called_without_version_locking-index", null));
                }

                context.forceUpdateToVersionField(entity, (lockMode == LockModeType.WRITE || lockMode.name().equals(ObjectLevelReadQuery.OPTIMISTIC_FORCE_INCREMENT)));
            }
        } catch (LockTimeoutException e) {
            throw e;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    // EntityManager#lock(Object,LockModeType,LockOption...) has LockModeType as standalone parameter
    // and LockModeType does not implement LockOption. This is probably bug in the jakarta.persistence API.
    // TODO-API-3.2 - Missing tests
    @Override
    public void lock(Object entity, LockModeType lockMode, LockOption... options) {
        LockOptionUtils.Options parsedOptions = LockOptionUtils.parse(lockMode, options);
        refresh(entity, parsedOptions.lockModeType(), parsedOptions.properties());
    }

    public void verifyOpen() {
        if (!this.isOpen || !this.factory.isOpen()) {
            throw new IllegalStateException(ExceptionLocalization.buildMessage("operation_on_closed_entity_manager"));
        }
    }

    /**
     * used to save having to constantly use a try/catch to call setRollbackOnly
     */
    public void verifyOpenWithSetRollbackOnly() {
        if (!this.isOpen || !this.factory.isOpen()) {
            setRollbackOnly();
            throw new IllegalStateException(ExceptionLocalization.buildMessage("operation_on_closed_entity_manager"));
        }
    }

    public RepeatableWriteUnitOfWork getActivePersistenceContext(Object txn) {
        // use local uow as it will be local to this EM and not on the txn
        if (this.extendedPersistenceContext == null || !this.extendedPersistenceContext.isActive()) {
            AbstractSession client;
            if(this.databaseSession.isServerSession()) {
                createConnectionPolicy();
                client = ((ServerSession)this.databaseSession).acquireClientSession(this.connectionPolicy, this.properties);
            } else if(this.databaseSession.isBroker()) {
                Map mapOfProperties = null;
                if (properties != null) {
                    mapOfProperties = (Map)this.properties.get(EntityManagerProperties.COMPOSITE_UNIT_PROPERTIES);
                }
                createConnectionPolicies(mapOfProperties);
                client = ((SessionBroker)this.databaseSession).acquireClientSessionBroker(this.connectionPolicies, mapOfProperties);
            } else {
                // currently this can't happen - the databaseSession is either ServerSession or SessionBroker.
                client = this.databaseSession;
            }
            this.extendedPersistenceContext = client.acquireRepeatableWriteUnitOfWork(this.referenceMode);
            this.extendedPersistenceContext.setResumeUnitOfWorkOnTransactionCompletion(!this.closeOnCommit);
            this.extendedPersistenceContext.setShouldDiscoverNewObjects(this.persistOnCommit);
            this.extendedPersistenceContext.setDiscoverUnregisteredNewObjectsWithoutPersist(this.commitWithoutPersistRules);
            this.extendedPersistenceContext.setFlushClearCache(this.flushClearCache);
            this.extendedPersistenceContext.setShouldValidateExistence(this.shouldValidateExistence);
            this.extendedPersistenceContext.setCommitOrder(this.commitOrder);
            this.extendedPersistenceContext.setShouldCascadeCloneToJoinedRelationship(true);
            this.extendedPersistenceContext.setShouldStoreByPassCache(this.cacheStoreBypass);
            if (txn != null) {
                // if there is a txn, it means we have been marked to join with it.
                // All that is left is to register the UOW with the transaction
                transaction.registerIfRequired(this.extendedPersistenceContext);
            }
            if (client.shouldLog(SessionLog.FINER, SessionLog.TRANSACTION)) {
                client.log(SessionLog.FINER, SessionLog.TRANSACTION, "acquire_unit_of_work_with_argument", String.valueOf(System.identityHashCode(this.extendedPersistenceContext)));
            }
        }
        if (this.beginEarlyTransaction && txn != null && !this.extendedPersistenceContext.isInTransaction()) {
            // gf3334, force persistence context early transaction
            this.extendedPersistenceContext.beginEarlyTransaction();
        }
        return this.extendedPersistenceContext;
    }

    /**
     * Use this method to set properties into existing EntityManager that are
     * normally passed to createEntityManager method. Note that if the method
     * called when active persistence context already exists then properties
     * used to create persistence context will be ignored until the new
     * persistence context is created (that happens either after transaction
     * rolled back or after clear method was called).
     */
    public void setProperties(Map properties) {
        verifyOpenWithSetRollbackOnly();
        this.properties = properties;
        if(this.hasActivePersistenceContext()) {
            this.extendedPersistenceContext.getParent().setProperties(properties);
        }
        if(properties == null || properties.isEmpty()) {
            return;
        }
        processProperties();
    }


    /**
     * @see EntityManager#setProperty(java.lang.String, java.lang.Object)
     */
    @Override
    public void setProperty(String propertyName, Object value) {
        verifyOpenWithSetRollbackOnly();
        if(propertyName == null || value == null) {
            return;
        }

        if (this.properties == null) {
            if(this.hasActivePersistenceContext()) {
                // getProperties method always returns non-null Map
                this.properties = this.extendedPersistenceContext.getParent().getProperties();
            } else {
                this.properties = new HashMap<>();
            }
        }

        properties.put(propertyName, value);

        // If there is an extended persistence context update table per tenant descriptors.
        if (hasActivePersistenceContext()) {
            extendedPersistenceContext.updateTablePerTenantDescriptors(propertyName, value);
        }

        // Re-process the property.
        PropertyProcessor processor = processors.get(propertyName);
        if(processor != null) {
            processor.process(propertyName, value, this);
        }
    }

    /**
     * This method is used in contains to check if we already have a persistence
     * context. If there is no active persistence context the method returns
     * false
     */
    public boolean hasActivePersistenceContext() {
        if (this.extendedPersistenceContext == null || !this.extendedPersistenceContext.isActive()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Return the current, joined transaction object. If validateExistence is true throw
     * an error if there is no joined transaction, otherwise return null.
     */
    protected Object checkForTransaction(boolean validateExistence) {
        Object txn = this.transaction.checkForTransaction(validateExistence);
        //use transaction.isJoinedToTransaction EM is open verification.
        if ((txn != null) && !transaction.isJoinedToTransaction(this.extendedPersistenceContext)) {
            if (validateExistence) {
                throw new TransactionRequiredException(ExceptionLocalization.buildMessage("cannot_use_transaction_on_unsynced_pc"));
            }
            return null;
        }
        return txn;
    }

    public boolean shouldFlushBeforeQuery() {
        return (checkForTransaction(false)!= null);
    }

    /**
     * Indicate the early transaction should be forced to start. This allows for
     * reading through the write connection. As a side effect, this will also
     * prevent anything from being cached.
     */
    public boolean shouldBeginEarlyTransaction() {
        return this.beginEarlyTransaction;
    }

    /**
     * Indicate to the EntityManager that a JTA transaction is active. This
     * method should be called on a JTA application managed EntityManager that
     * was created outside the scope of the active transaction to associate it
     * with the current JTA transaction.
     *
     * @throws jakarta.persistence.TransactionRequiredException
     *             if there is no transaction.
     */
    @Override
    public void joinTransaction() {
        try {
            verifyOpen();
            //An EntityTransactionWrapper throws an exception, while
            //if using JTA and extendedPersistenceContext is active, then this will have the UOW register with the transaction.
            //If there is no context, the JTATransactionWrapper will register a listener with the transaction to keep track of when
            //it completes.  Any UOW created while the transaction is still active will then automatically register/join with it.
            transaction.registerIfRequired(this.extendedPersistenceContext);
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * Internal method. Sets transaction to rollback only.
     */
    protected void setRollbackOnly() {
        this.transaction.setRollbackOnlyInternal();
    }

    /**
     * Process the local EntityManager properties only. The persistence unit
     * properties are processed by the factory.
     */
    protected void processProperties() {
        Iterator<Map.Entry<String, Object>> it = this.properties.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            PropertyProcessor processor = processors.get(entry.getKey());
            if(processor != null) {
                processor.process(entry.getKey(), entry.getValue(), this);
            }
        }
    }

    /**
     * Get the local EntityManager property from the properties Map. This only
     * searches the local Map. The persistence unit properties are processed by
     * the EntityManagerFactory.
     */
    protected String getPropertiesHandlerProperty(String name) {
        return PropertiesHandler.getPropertyValue(name, this.properties, false);
    }

    /**
     * Verifies and (if required) translates the value.
     */
    protected static String getPropertiesHandlerProperty(String name, String value) {
        return PropertiesHandler.getPropertyValue(name, value);
    }

    protected void setEntityTransactionWrapper() {
        transaction = new EntityTransactionWrapper(this);
    }

    /**
     * Set the flush mode that applies to all objects contained in the
     * persistence context.
     *
     */
    @Override
    public void setFlushMode(FlushModeType flushMode) {
        try {
            verifyOpen();
            this.flushMode = flushMode;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    protected void setJTATransactionWrapper() {
        transaction = new JTATransactionWrapper(this);
        //if this is not unsynchronized and there is a transaction, this EM needs to join it
        if (syncType == null || syncType.equals(SynchronizationType.SYNCHRONIZED)) {
            //use the wrapper's checkForTransaction as this.checkForTransaction does an unnecessary isJoined check
            if (transaction.checkForTransaction(false) != null) {
                //extendedPersistenceContext should be null, which will force the wrapper to register with the transaction
                transaction.registerIfRequired(this.extendedPersistenceContext);
            }
        }
    }

    /**
     * Create connection policy using properties.
     * Default connection policy created if no connection properties specified.
     * Should be called only in case this.databaseSession is a ServerSession.
     */
    protected void createConnectionPolicy() {
        if (this.connectionPolicy == null) {
            this.connectionPolicy = createConnectionPolicy((ServerSession)this.databaseSession, this.properties);
        }
    }

    /**
     * Create connection policy using properties.
     * Default connection policy created if no connection properties specified.
     * Should be called only in case this.databaseSession is a SessionBroker.
     */
    protected void createConnectionPolicies(Map mapOfProperties) {
        // Because the method called only in SessionBroker case this.connectionPolicies is guaranteed to be non null.
        Iterator<Map.Entry<String, ConnectionPolicy>> it = this.connectionPolicies.entrySet().iterator();
        while (it.hasNext()) {
            // key - sessionName, value - ConnectionPolicy
            Map.Entry<String, ConnectionPolicy> entry = it.next();
            if (entry.getValue() == null) {
                // ConnectionPolicy is null - should be recreated
                Map properties = null;
                if (mapOfProperties != null) {
                    properties = (Map)mapOfProperties.get(entry.getKey());
                }
                ConnectionPolicy connectionPolicy = createConnectionPolicy((ServerSession)this.databaseSession.getSessionForName(entry.getKey()), properties);
                this.connectionPolicies.put(entry.getKey(), connectionPolicy);
            }
        }
    }

    /**
     * Create connection policy using properties.
     * Default connection policy created if no connection properties specified.
     */
    protected static ConnectionPolicy createConnectionPolicy(ServerSession serverSession, Map properties) {
        ConnectionPolicy policy = serverSession.getDefaultConnectionPolicy();

        if (properties == null || properties.isEmpty()) {
            return policy;
        }

        // Search only the properties map - serverSession's properties have been
        // already processed.
        ConnectionPolicy policyFromProperties = (ConnectionPolicy) properties.get(EntityManagerProperties.CONNECTION_POLICY);
        if (policyFromProperties != null) {
            policy = policyFromProperties;
        }

        // Note that serverSession passed into the methods below only because it
        // carries the SessionLog into which the debug info should be written.
        // The property is search for in the passed properties map only (not in
        // serverSession, not in System.properties).
        ConnectionPolicy newPolicy = null;
        String isLazyString = EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(EntityManagerProperties.EXCLUSIVE_CONNECTION_IS_LAZY, properties, serverSession, false);
        if (isLazyString != null) {
            boolean isLazy = Boolean.parseBoolean(isLazyString);
            if (policy.isLazy() != isLazy) {
                if (newPolicy == null) {
                    newPolicy = (ConnectionPolicy) policy.clone();
                }
                newPolicy.setIsLazy(isLazy);
            }
        }
        ConnectionPolicy.ExclusiveMode exclusiveMode = EntityManagerSetupImpl.getConnectionPolicyExclusiveModeFromProperties(properties, serverSession, false);
        if (exclusiveMode != null) {
            if (!exclusiveMode.equals(policy.getExclusiveMode())) {
                if (newPolicy == null) {
                    newPolicy = (ConnectionPolicy) policy.clone();
                }
                newPolicy.setExclusiveMode(exclusiveMode);
            }
        }

        String user = EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(EntityManagerProperties.JDBC_USER, properties, serverSession, false);
        String password = EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(EntityManagerProperties.JDBC_PASSWORD, properties, serverSession, false);
        String driver = EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(EntityManagerProperties.JDBC_DRIVER, properties, serverSession, false);
        String connectionString = EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(EntityManagerProperties.JDBC_URL, properties, serverSession, false);

        // find the jta datasource
        Object jtaDataSourceObj = EntityManagerFactoryProvider.getConfigPropertyLogDebug(EntityManagerProperties.JTA_DATASOURCE, properties, serverSession, false);
        DataSource jtaDataSource = null;
        String jtaDataSourceName = null;
        if (jtaDataSourceObj != null) {
            if (jtaDataSourceObj instanceof DataSource) {
                jtaDataSource = (DataSource) jtaDataSourceObj;
            } else if (jtaDataSourceObj instanceof String) {
                jtaDataSourceName = (String) jtaDataSourceObj;
            }
        }

        // find the non jta datasource
        Object nonjtaDataSourceObj = EntityManagerFactoryProvider.getConfigPropertyLogDebug(EntityManagerProperties.NON_JTA_DATASOURCE, properties, serverSession, false);
        DataSource nonjtaDataSource = null;
        String nonjtaDataSourceName = null;
        if (nonjtaDataSourceObj != null) {
            if (nonjtaDataSourceObj instanceof DataSource) {
                nonjtaDataSource = (DataSource) nonjtaDataSourceObj;
            } else if (nonjtaDataSourceObj instanceof String) {
                nonjtaDataSourceName = (String) nonjtaDataSourceObj;
            }
        }

        if (user != null || password != null || driver != null || connectionString != null || jtaDataSourceObj != null || nonjtaDataSourceObj != null) {
            // Validation: Can't specify jdbcDriver, connectionString with a
            // DataSource
            boolean isDefaultConnectorRequired = isPropertyToBeAdded(driver) || isPropertyToBeAdded(connectionString);
            boolean isJNDIConnectorRequired = isPropertyToBeAdded(jtaDataSource, jtaDataSourceName) || isPropertyToBeAdded(nonjtaDataSource, nonjtaDataSourceName);
            if (isDefaultConnectorRequired && isJNDIConnectorRequired) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("entity_manager_properties_conflict_default_connector_vs_jndi_connector", new Object[] {}));
            }

            DatasourceLogin login = (DatasourceLogin) policy.getLogin();
            if (login == null) {
                if (policy.getPoolName() != null) {
                    login = (DatasourceLogin) serverSession.getConnectionPool(policy.getPoolName()).getLogin();
                } else {
                    login = (DatasourceLogin) serverSession.getDatasourceLogin();
                }
            }

            // Validation: Can't specify jdbcDriver, connectionString if
            // externalTransactionController is used - this requires
            // externalConnectionPooling
            if (login.shouldUseExternalTransactionController() && isDefaultConnectorRequired) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("entity_manager_properties_conflict_default_connector_vs_external_transaction_controller", new Object[] {}));
            }

            javax.sql.DataSource dataSource = null;
            String dataSourceName = null;
            if (isJNDIConnectorRequired) {
                if (login.shouldUseExternalTransactionController()) {
                    if (isPropertyToBeAdded(jtaDataSource, jtaDataSourceName)) {
                        dataSource = jtaDataSource;
                        dataSourceName = jtaDataSourceName;
                    }
                    // validation: Can't change externalTransactionController
                    // state - will ignore data source that doesn't match the
                    // flag.
                    if (isPropertyToBeAdded(nonjtaDataSource, nonjtaDataSourceName)) {
                        serverSession.log(SessionLog.WARNING, SessionLog.PROPERTIES, "entity_manager_ignores_nonjta_data_source");
                    }
                } else {
                    if (isPropertyToBeAdded(nonjtaDataSource, nonjtaDataSourceName)) {
                        dataSource = nonjtaDataSource;
                        dataSourceName = nonjtaDataSourceName;
                    }
                    // validation: Can't change externalTransactionController
                    // state - will ignore data source that doesn't match the
                    // flag.
                    if (isPropertyToBeAdded(jtaDataSource, jtaDataSourceName)) {
                        serverSession.log(SessionLog.WARNING, SessionLog.PROPERTIES, "entity_manager_ignores_jta_data_source");
                    }
                }
            }

            // isNew...Required == null means no change required; TRUE -
            // newValue substitute oldValue by newValue; FALSE - remove
            // oldValue.
            Boolean isNewUserRequired = isPropertyValueToBeUpdated(login.getUserName(), user);
            // if isNewUserRequired==null then isNewPasswordRequired==null, too:
            // don't create a new ConnectionPolicy if the same user/password passed to both createEMF and createEM
            Boolean isNewPasswordRequired = null;
            // if user name should be removed from properties then password
            // should be removed, too.
            if (isNewUserRequired != null) {
                if(isNewUserRequired) {
                    if(password != null) {
                        // can't compare the passed (un-encrypted) password with the existing encrypted one, therefore
                        // use the new password if it's not an empty string.
                        isNewPasswordRequired = !password.isEmpty();
                    }
                } else {
                    // user should be removed -> remove password as well
                    isNewPasswordRequired = Boolean.FALSE;
                }
            }
            DefaultConnector oldDefaultConnector = null;
            if (login.getConnector() instanceof DefaultConnector) {
                oldDefaultConnector = (DefaultConnector) login.getConnector();
            }
            boolean isNewDefaultConnectorRequired = oldDefaultConnector == null && isDefaultConnectorRequired;
            JNDIConnector oldJNDIConnector = null;
            if (login.getConnector() instanceof JNDIConnector) {
                oldJNDIConnector = (JNDIConnector) login.getConnector();
            }
            boolean isNewJNDIConnectorRequired = oldJNDIConnector == null && isJNDIConnectorRequired;
            Boolean isNewDriverRequired = null;
            Boolean isNewConnectionStringRequired = null;
            if (isNewDefaultConnectorRequired) {
                isNewDriverRequired = isPropertyValueToBeUpdated(null, driver);
                isNewConnectionStringRequired = isPropertyValueToBeUpdated(null, connectionString);
            } else {
                if (oldDefaultConnector != null) {
                    isNewDriverRequired = isPropertyValueToBeUpdated(oldDefaultConnector.getDriverClassName(), driver);
                    isNewConnectionStringRequired = isPropertyValueToBeUpdated(oldDefaultConnector.getConnectionString(), connectionString);
                }
            }
            Boolean isNewDataSourceRequired = null;
            if (isNewJNDIConnectorRequired) {
                isNewDataSourceRequired = Boolean.TRUE;
            } else {
                if (oldJNDIConnector != null) {
                    if (dataSource != null) {
                        if (!dataSource.equals(oldJNDIConnector.getDataSource())) {
                            isNewDataSourceRequired = Boolean.TRUE;
                        }
                    } else if (dataSourceName != null) {
                        if (!dataSourceName.equals(oldJNDIConnector.getName())) {
                            isNewDataSourceRequired = Boolean.TRUE;
                        }
                    }
                }
            }

            if (isNewUserRequired != null || isNewPasswordRequired != null || isNewDriverRequired != null || isNewConnectionStringRequired != null || isNewDataSourceRequired != null) {
                // a new login required - so a new policy required, too.
                if (newPolicy == null) {
                    newPolicy = (ConnectionPolicy) policy.clone();
                }
                // the new policy must have a new login - not to override the
                // existing one in the original ConnectionPolicy that is likely
                // shared.
                DatasourceLogin newLogin = (DatasourceLogin) newPolicy.getLogin();
                // sometimes ConnectionPolicy.clone clones the login , too -
                // sometimes it doesn't.
                if (newPolicy.getLogin() == null || newPolicy.getLogin() == policy.getLogin()) {
                    newLogin = login.clone();
                    newPolicy.setLogin(newLogin);
                }
                // because it uses a new login the connection policy should not
                // be pooled.
                newPolicy.setPoolName(null);

                if (isNewUserRequired != null) {
                    if (isNewUserRequired) {
                        newLogin.setProperty("user", user);
                    } else {
                        newLogin.getProperties().remove("user");
                    }
                }
                if (isNewPasswordRequired != null) {
                    if (isNewPasswordRequired) {
                        newLogin.setProperty("password", password);
                    } else {
                        newLogin.getProperties().remove("password");
                    }
                }
                if (isNewDefaultConnectorRequired) {
                    newLogin.setConnector(new DefaultConnector());
                    newLogin.setUsesExternalConnectionPooling(false);
                } else if (isNewJNDIConnectorRequired) {
                    newLogin.setConnector(new JNDIConnector());
                    newLogin.setUsesExternalConnectionPooling(true);
                }
                if (isDefaultConnectorRequired) {
                    DefaultConnector defaultConnector = (DefaultConnector) newLogin.getConnector();
                    if (isNewDriverRequired != null) {
                        if (isNewDriverRequired) {
                            defaultConnector.setDriverClassName(driver);
                        } else {
                            defaultConnector.setDriverClassName(null);
                        }
                    }
                    if (isNewConnectionStringRequired != null) {
                        if (isNewConnectionStringRequired) {
                            defaultConnector.setDatabaseURL(connectionString);
                        } else {
                            defaultConnector.setDatabaseURL(null);
                        }
                    }
                } else if (isNewDataSourceRequired != null) {
                    JNDIConnector jndiConnector = (JNDIConnector) newLogin.getConnector();
                    if (isNewDataSourceRequired) {
                        if (dataSource != null) {
                            jndiConnector.setDataSource(dataSource);
                        } else {
                            // dataSourceName != null
                            jndiConnector.setDataSource(null);
                            jndiConnector.setName(dataSourceName);
                        }
                    }
                }
            }
        }

        if (newPolicy != null) {
            return newPolicy;
        } else {
            return policy;
        }
    }

    /**
     * Indicates whether the underlying session is a session broker.
     * Session broker implement composite persistence unit.
     */
    @Override
    public boolean isBroker() {
        return this.databaseSession.isBroker();
    }

    /**
     * Property value is to be added if it's non null and not an empty string.
     */
    protected static boolean isPropertyToBeAdded(String value) {
        return value != null && !value.isEmpty();
    }

    protected static boolean isPropertyToBeAdded(DataSource ds, String dsName) {
        return ds != null || (dsName != null && !dsName.isEmpty());
    }

    /**
     * Property value of an empty string indicates that the existing property
     * should be removed.
     */
    protected static boolean isPropertyToBeRemoved(String value) {
        return value != null && value.isEmpty();
    }

    /**
     * @return null: no change; TRUE: substitute oldValue by newValue; FALSE:
     *         remove oldValue
     */
    protected static Boolean isPropertyValueToBeUpdated(String oldValue, String newValue) {
        if (newValue == null) {
            // no new value - no change
            return null;
        } else {
            // new value is a non empty string
            if (!newValue.isEmpty()) {
                if (oldValue != null) {
                    if (newValue.equals(oldValue)) {
                        // new and old values are equal - no change.
                        return null;
                    } else {
                        // new and old values are different - change old value
                        // for new value.
                        return Boolean.TRUE;
                    }
                } else {
                    // no old value - change for new value.
                    return Boolean.TRUE;
                }
            } else {
                // new value is an empty string - if old value exists it should
                // be substituted with new value..
                if (oldValue != null) {
                    return Boolean.FALSE;
                } else {
                    return null;
                }
            }
        }
    }

    /**
     * Remove the given entity from the persistence context, causing a managed
     * entity to become detached. Unflushed changes made to the entity if any
     * (including removal of the entity), will not be synchronized to the
     * database. Entities which previously referenced the detached entity will
     * continue to reference it.
     *
     * @throws IllegalArgumentException
     *             if the instance is not an entity
     *
     * @since Java Persistence 2.0
     */
    @Override
    public void detach(Object entity) {
        try {
            verifyOpen();
            if (entity == null) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("not_an_entity", new Object[] { null }));
            }
            ClassDescriptor descriptor = this.databaseSession.getDescriptors().get(entity.getClass());
            if (descriptor == null || descriptor.isDescriptorTypeAggregate()) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("not_an_entity", new Object[] { entity }));
            }
            UnitOfWorkImpl uowImpl = (UnitOfWorkImpl) getUnitOfWork();
            uowImpl.unregisterObject(entity, 0, true);
        } catch (RuntimeException exception) {
            setRollbackOnly();
            throw exception;
        }

    }

    /**
     * Return an instance of CriteriaBuilder for the creation of
     * Criteria API Query objects.
     * @return CriteriaBuilder instance
     * @throws IllegalStateException if the entity manager has
     * been closed.
     * @see jakarta.persistence.EntityManager#getCriteriaBuilder()
     * @since Java Persistence 2.0
     */
    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        verifyOpenWithSetRollbackOnly();
        // defer to the parent entityManagerFactory
        return this.factory.getCriteriaBuilder();
    }

    /**
     * Before any find or refresh operation, gather any persistence unit
     * properties that should be applied to the query.
     */
    protected HashMap<String, Object> getQueryHints(Object entity, OperationType operation) {
        HashMap<String, Object> queryHints = null;

        // If the entity is null or there are no properties just return null.
        // Individual methods will handle the entity = null case, although we
        // could likely do it here as well.
        if (entity != null && properties != null) {
            queryHints = new HashMap<>();

            if (properties.containsKey(QueryHints.PESSIMISTIC_LOCK_TIMEOUT)) {
                queryHints.put(QueryHints.PESSIMISTIC_LOCK_TIMEOUT, properties.get(QueryHints.PESSIMISTIC_LOCK_TIMEOUT));
            }
            if (properties.containsKey(QueryHints.PESSIMISTIC_LOCK_TIMEOUT_UNIT)) {
                queryHints.put(QueryHints.PESSIMISTIC_LOCK_TIMEOUT_UNIT, properties.get(QueryHints.PESSIMISTIC_LOCK_TIMEOUT_UNIT));
            }
            // Ignore the JPA cache settings if the eclipselink setting has
            // been specified.
            if (! properties.containsKey(QueryHints.CACHE_USAGE)) {
                // If the descriptor is isolated then it is not cacheable so ignore
                // the properties. A null descriptor case will be handled in the
                // individual operation methods so no need to worry about it here.
                Class<?> cls = entity instanceof Class<?> ? (Class) entity : entity.getClass();
                ClassDescriptor descriptor = getActiveSession().getDescriptor(cls);

                if (descriptor != null && ! descriptor.isIsolated()) {
                    if (operation != OperationType.LOCK) {
                        // For a find operation, apply the jakarta.persistence.cache.retrieveMode
                        if (operation == OperationType.FIND) {
                            if (properties.containsKey(QueryHints.CACHE_RETRIEVE_MODE)) {
                                queryHints.put(QueryHints.CACHE_RETRIEVE_MODE, properties.get(QueryHints.CACHE_RETRIEVE_MODE));
                            } else if (properties.containsKey("jakarta.persistence.cacheRetrieveMode")) { // support legacy property
                                Session activeSession = getActiveSession();
                                if (activeSession != null) {
                                    // log deprecation info
                                    String[] properties = new String[] { QueryHints.CACHE_RETRIEVE_MODE, "jakarta.persistence.cacheRetrieveMode" };
                                    ((AbstractSession)activeSession).log(SessionLog.INFO, SessionLog.TRANSACTION, "deprecated_property", properties);
                                }
                                queryHints.put(QueryHints.CACHE_RETRIEVE_MODE, properties.get("jakarta.persistence.cacheRetrieveMode"));
                            }
                        }

                        // For both find and refresh operations, apply jakarta.persistence.cache.storeMode
                        if (properties.containsKey(QueryHints.CACHE_STORE_MODE)) {
                            queryHints.put(QueryHints.CACHE_STORE_MODE, properties.get(QueryHints.CACHE_STORE_MODE));
                        } else if (properties.containsKey("jakarta.persistence.cacheStoreMode")) { // support legacy property
                            Session activeSession = getActiveSession();
                            if (activeSession != null) {
                                // log deprecation info
                                String[] properties = new String[] { QueryHints.CACHE_STORE_MODE, "jakarta.persistence.cacheStoreMode" };
                                ((AbstractSession)activeSession).log(SessionLog.INFO, SessionLog.TRANSACTION, "deprecated_property", properties);
                            }
                            queryHints.put(QueryHints.CACHE_STORE_MODE, properties.get("jakarta.persistence.cacheStoreMode"));
                        }
                    }
                }
            }
        }

        return queryHints;
    }

    /**
     * Return an instance of Metamodel interface for access to the
     * metamodel of the persistence unit.
     * @return Metamodel instance
     * @throws IllegalStateException if the entity manager has
     * been closed.
     * @see jakarta.persistence.EntityManager#getMetamodel()
     * @since Java Persistence 2.0
     */
    @Override
    public Metamodel getMetamodel() {
        verifyOpenWithSetRollbackOnly();
        // defer to the parent entityManagerFactory
        return this.factory.getMetamodel();
    }

    /**
     * Return the entity manager factory for the entity manager.
     *
     * @return EntityManagerFactory instance
     * @throws IllegalStateException
     *             if the entity manager has been closed.
     *
     * @since Java Persistence API 2.0
     */
    @Override
    public EntityManagerFactory getEntityManagerFactory() {
        try {
            verifyOpen();
            return factory;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * @see jakarta.persistence.EntityManager#getLockMode(java.lang.Object)
     * @since Java Persistence API 2.0
     */
    @Override
    public LockModeType getLockMode(Object entity) {
        try {
            verifyOpen();
            checkForTransaction(true);
            UnitOfWorkImpl uowImpl = getActivePersistenceContext(checkForTransaction(false));
            LockModeType lockMode = LockModeType.NONE;

            if (!contains(entity, uowImpl)) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("cant_getLockMode_of_not_managed_object", new Object[] { entity }));
            }
            Boolean optimistickLock = (Boolean) uowImpl.getOptimisticReadLockObjects().get(entity);
            if (optimistickLock != null) {
                if (optimistickLock.equals(Boolean.FALSE)) {
                    lockMode = LockModeType.OPTIMISTIC;
                } else {
                    // The entity is present in the map and its version is
                    // marked for increment.
                    // The lockMode can be OPTIMISTIC_FORCE_INCREMENT ||
                    // PESSIMISTIC_FORCE_INCREMENT
                    if (uowImpl.getPessimisticLockedObjects().get(entity) != null) {
                        lockMode = LockModeType.PESSIMISTIC_FORCE_INCREMENT;
                    } else {
                        lockMode = LockModeType.OPTIMISTIC_FORCE_INCREMENT;
                    }
                }
            } else { // Not optimistically locked
                if (uowImpl.getPessimisticLockedObjects().get(entity) != null) {
                    lockMode = LockModeType.PESSIMISTIC_WRITE;
                }
            }
            return lockMode;
        } catch (RuntimeException exception) {
            setRollbackOnly();
            throw exception;
        }
    }

    @Override
    public CacheRetrieveMode getCacheRetrieveMode() {
        return FindOptionUtils.getCacheRetrieveMode(getAbstractSession(), properties);
    }

    @Override
    public void setCacheRetrieveMode(CacheRetrieveMode cacheRetrieveMode) {
        if (this.properties == null) {
            this.properties = new HashMap<>();
        }
        FindOptionUtils.setCacheRetrieveMode(properties, cacheRetrieveMode);
    }

    @Override
    public CacheStoreMode getCacheStoreMode() {
        return FindOptionUtils.getCacheStoreMode(getAbstractSession(), properties);
    }

    @Override
    public void setCacheStoreMode(CacheStoreMode cacheStoreMode) {
        if (this.properties == null) {
            this.properties = new HashMap<>();
        }
        FindOptionUtils.setCacheStoreMode(properties, cacheStoreMode);
    }

    /**
     * Get the properties and associated values that are in effect for the
     * entity manager. Changing the contents of the map does not change the
     * configuration in effect.
     *
     * @since Java Persistence API 2.0
     */
    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> sessionMap = new HashMap<>(getAbstractSession().getProperties());
        if (this.properties != null) {
            sessionMap.putAll(this.properties);
        }
        return Collections.unmodifiableMap(sessionMap);

    }

    /**
     * Get the names of the properties that are supported for use with the
     * entity manager. These correspond to properties and hints that may be
     * passed to the methods of the EntityManager interface that take a
     * properties argument or used with the PersistenceContext annotation. These
     * properties include all standard entity manager hints and properties as
     * well as vendor-specific ones supported by the provider. These properties
     * may or may not currently be in effect.
     *
     * @return property names
     *
     * @since Java Persistence API 2.0
     */
    public Set<String> getSupportedProperties() {

        return EntityManagerProperties.getSupportedProperties();
    }

    /**
     * Return an object of the specified type to allow access to the
     * provider-specific API. If the provider's EntityManager implementation
     * does not support the specified class, the PersistenceException is thrown.
     *
     * @param cls
     *            the class of the object to be returned. This is normally
     *            either the underlying EntityManager implementation class or an
     *            interface that it implements.
     * @return an instance of the specified class
     * @throws PersistenceException
     *             if the provider does not support the call.
     *
     * @since Java Persistence API 2.0
     */
    @Override
    public <T> T unwrap(Class<T> cls) {
        try {
            if (cls.equals(UnitOfWork.class) || cls.equals(UnitOfWorkImpl.class) || cls.equals(RepeatableWriteUnitOfWork.class)) {
                return (T) this.getUnitOfWork();
            } else if (cls.equals(JpaEntityManager.class) || cls.equals(EntityManagerImpl.class)) {
                return (T) this;
            } else if (cls.equals(Session.class) || cls.equals(AbstractSession.class)) {
                return (T) this.getAbstractSession();
            } else if (cls.equals(DatabaseSession.class) || cls.equals(DatabaseSessionImpl.class)) {
                return (T) this.getDatabaseSession();
            } else if (cls.equals(Server.class) || cls.equals(ServerSession.class)) {
                return (T) this.getServerSession();
            } else if (cls.equals(SessionBroker.class)) {
                return (T) this.getSessionBroker();
            } else if (cls.equals(java.sql.Connection.class)) {
                final UnitOfWorkImpl unitOfWork = (UnitOfWorkImpl) this.getUnitOfWork();
                Accessor accessor = unitOfWork.getAccessor();
                if (unitOfWork.getParent().isExclusiveIsolatedClientSession()) {
                    // If the ExclusiveIsolatedClientSession hasn't serviced a query prior to the unwrap,
                    // there will be no available Connection.
                    java.sql.Connection conn = accessor.getConnection();
                    if (conn == null) {
                        final boolean uowInTran = unitOfWork.isInTransaction();
                        final boolean activeTran = checkForTransaction(false) != null;
                        if (uowInTran || activeTran) {
                            if (activeTran) {
                                unitOfWork.beginEarlyTransaction();
                            }
                            accessor.incrementCallCount(unitOfWork.getParent());
                            accessor.decrementCallCount();
                            conn = accessor.getConnection();
                        } 
                        // if not in a tx, still return null
                    }
                    
                    return (T) conn;
                } else if (unitOfWork.isInTransaction()) {
                    return (T) unitOfWork.getAccessor().getConnection();
                }
                
                if (checkForTransaction(false) != null) {
                    unitOfWork.beginEarlyTransaction();
                    accessor = unitOfWork.getAccessor();
                    // Ensure external connection is acquired.
                    accessor.incrementCallCount(unitOfWork.getParent());
                    accessor.decrementCallCount();
                    return (T) accessor.getConnection();
                }
                return null;
            } else if (cls.getName().equals("jakarta.resource.cci.Connection")) {
                UnitOfWorkImpl unitOfWork = (UnitOfWorkImpl) this.getUnitOfWork();
                if(unitOfWork.isInTransaction() || unitOfWork.getParent().isExclusiveIsolatedClientSession()) {
                    return (T) unitOfWork.getAccessor().getConnection();
                }
                if (checkForTransaction(false) != null) {
                    unitOfWork.beginEarlyTransaction();
                    Accessor accessor = unitOfWork.getAccessor();
                    // Ensure external connection is acquired.
                    accessor.incrementCallCount(unitOfWork.getParent());
                    accessor.decrementCallCount();
                    return (T) accessor.getDatasourceConnection();
                }
                return null;
            }
            throw new PersistenceException(ExceptionLocalization.buildMessage("unable_to_unwrap_jpa", new String[]{EntityManager.class.getName(), cls.getName()}));

        } catch (RuntimeException e) {
            throw e;
        }
    }

    /**
     * This method will load the passed entity or collection of entities using the passed AttributeGroup.
     * In case of collection all members should be either entities of the same type
     * or have a common inheritance hierarchy mapped root class.
     * The AttributeGroup should correspond to the entity type.
     *
     */
    @Override
    public void load(Object entityOrEntities, AttributeGroup group) {
        verifyOpen();
        getActivePersistenceContext(checkForTransaction(false)).load(entityOrEntities, group);
    }

    /**
     * This method will return copy the passed entity using the passed AttributeGroup.
     * In case of collection all members should be either entities of the same type
     * or have a common inheritance hierarchy mapped root class.
     * The AttributeGroup should correspond to the entity type.
     *
     */
    @Override
    public Object copy(Object entityOrEntities, AttributeGroup group) {
        verifyOpen();
        return getActivePersistenceContext(checkForTransaction(false)).copy(entityOrEntities, group);
    }

    /**
     * INTERNAL:
     * Load/fetch the unfetched object.  This method is used by the ClassWaver..
     */
    public static void processUnfetchedAttribute(FetchGroupTracker entity, String attributeName) {
        String errorMsg = entity._persistence_getFetchGroup().onUnfetchedAttribute(entity, attributeName);
        if(errorMsg != null) {
            throw new jakarta.persistence.EntityNotFoundException(errorMsg);
        }
    }

    /**
     * INTERNAL:
     * Load/fetch the unfetched object.  This method is used by the ClassWeaver.
     */
    public static void processUnfetchedAttributeForSet(FetchGroupTracker entity, String attributeName) {
        String errorMsg = entity._persistence_getFetchGroup().onUnfetchedAttributeForSet(entity, attributeName);
        if(errorMsg != null) {
            throw new jakarta.persistence.EntityNotFoundException(errorMsg);
        }
    }

    @Override
    public Query createQuery(CriteriaUpdate updateQuery) {
        try{
            verifyOpen();
            return new EJBQueryImpl(((CriteriaUpdateImpl)updateQuery).translate(), this);
        }catch (RuntimeException e){
            setRollbackOnly();
            throw e;
        }
    }

    @Override
    public Query createQuery(CriteriaDelete deleteQuery) {
        try{
            verifyOpen();
            return new EJBQueryImpl(((CriteriaDeleteImpl)deleteQuery).translate(), this);
        }catch (RuntimeException e){
            setRollbackOnly();
            throw e;
        }
    }

    @Override
    public boolean isJoinedToTransaction() {
        verifyOpen();
        return transaction.isJoinedToTransaction(this.extendedPersistenceContext);
    }

    @Override
    public <T> EntityGraph<T> createEntityGraph(Class<T> rootType) {
        ClassDescriptor descriptor = getAbstractSession().getDescriptor(rootType);
        if (descriptor == null || descriptor.isAggregateDescriptor()){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("unknown_bean_class", new Object[]{rootType.getName()}));
        }
        return new EntityGraphImpl<>(new AttributeGroup(null, rootType, true), descriptor);
    }

    @Override
    public EntityGraph<?> createEntityGraph(String graphName) {
        AttributeGroup group = this.getAbstractSession().getAttributeGroups().get(graphName);
        if (group == null){
            return null;
        }
        ClassDescriptor descriptor = this.getAbstractSession().getDescriptor(group.getType());
        return new EntityGraphImpl<>(group.clone(), descriptor);
    }

    @Override
    public EntityGraph<?> getEntityGraph(String graphName) {
        AttributeGroup group = this.getAbstractSession().getAttributeGroups().get(graphName);
        if (group == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("no_entity_graph_of_name", new Object[]{graphName}));
        }
        return new EntityGraphImpl<>(group);
    }

    @Override
    public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> entityClass) {
        ClassDescriptor descriptor = getAbstractSession().getDescriptor(entityClass);
        if (descriptor == null || descriptor.isAggregateDescriptor()){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("unknown_bean_class", new Object[]{entityClass.getName()}));
        }
        List<EntityGraph<? super T>> result = new ArrayList<>();
        for (AttributeGroup group : descriptor.getAttributeGroups().values()){
            result.add(new EntityGraphImpl<>(group));
        }
        if (descriptor.hasInheritance()){
            while(descriptor.getInheritancePolicy().getParentDescriptor() != null){
                descriptor = descriptor.getInheritancePolicy().getParentDescriptor();
                for (AttributeGroup group : descriptor.getAttributeGroups().values()){
                    result.add(new EntityGraphImpl<>(group));
                }
            }
        }
        return result;
    }

    /**
     * INTERNAL:
     * Tracks if this EntityManager should automatically associate with the transaction or not
     * @return the syncType
     */
    public SynchronizationType getSyncType() {
        return syncType;
    }
}
