/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 * Copyright (c) 2021, 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.internal.jpa;

import jakarta.persistence.CacheRetrieveMode;
import jakarta.persistence.CacheStoreMode;
import jakarta.persistence.ConnectionConsumer;
import jakarta.persistence.ConnectionFunction;
import jakarta.persistence.EntityAgent;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.FindOption;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.LockModeType;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.PersistenceUnitTransactionType;
import jakarta.persistence.PessimisticLockScope;
import jakarta.persistence.PessimisticLockException;
import jakarta.persistence.QueryTimeoutException;
import jakarta.persistence.RollbackException;
import jakarta.persistence.Statement;
import jakarta.persistence.StatementOrTypedQuery;
import jakarta.persistence.StatementReference;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.persistence.Timeout;
import jakarta.persistence.TransactionRequiredException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.TypedQueryReference;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaSelect;
import jakarta.persistence.criteria.CriteriaStatement;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.persistence.sql.ResultSetMapping;
import jakarta.transaction.Status;
import jakarta.transaction.Synchronization;
import jakarta.transaction.SystemException;
import jakarta.transaction.Transaction;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.VersionLockingPolicy;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.jpa.metadata.listeners.PersistenceUnitListenerRegistry;
import org.eclipse.persistence.internal.jpa.querydef.CommonAbstractCriteriaImpl;
import org.eclipse.persistence.internal.helper.EntityAgentProperties;
import org.eclipse.persistence.internal.helper.BasicTypeHelperImpl;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.CommitManager;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.queries.AttributeGroup;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.DeleteObjectQuery;
import org.eclipse.persistence.queries.InsertObjectQuery;
import org.eclipse.persistence.queries.ObjectLevelModifyQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.ResultSetMappingQuery;
import org.eclipse.persistence.queries.SQLResultSetMapping;
import org.eclipse.persistence.queries.StoredProcedureCall;
import org.eclipse.persistence.queries.UpdateObjectQuery;
import org.eclipse.persistence.sessions.CopyGroup;
import org.eclipse.persistence.sessions.server.ClientSession;
import org.eclipse.persistence.transaction.AbstractTransactionController;

/**
 * Immediate entity operations on an owned client session. No unit of work or
 * persistence context is created, and returned objects are detached copies.
 */
public class EntityAgentImpl implements EntityAgent, QueryExecutionContext {
    static final String OPERATION = EntityAgentProperties.OPERATION;
    private final EntityManagerFactoryDelegate factory;
    private final ClientSession session;
    private final Map<String, Object> properties = new HashMap<>();
    private final Set<QueryImpl> queries = Collections.newSetFromMap(new IdentityHashMap<>());
    private final AgentTransaction transaction = new AgentTransaction();
    private final Map<EntityKey, ReadLock> readLocks = new LinkedHashMap<>();
    private final Set<EntityKey> cacheInvalidations = new LinkedHashSet<>();
    private final Set<EntityKey> cacheRefreshes = new LinkedHashSet<>();
    private final Set<Class<?>> cacheClassInvalidations = new LinkedHashSet<>();
    private boolean refreshingResults;
    private final boolean jta;
    private Object joinedTransaction;
    private boolean open = true;
    private boolean poisoned;
    private boolean disposed;
    private boolean rollbackCleanupRequired;
    private PersistenceUnitListenerRegistry persistenceUnitListeners;
    private boolean lifecycleClosing;
    private boolean lifecycleCloseNotified;
    private boolean lifecycleReleased;

    EntityAgentImpl completeCreation(PersistenceUnitListenerRegistry listeners) {
        if (listeners != null) {
            try {
                listeners.retainAgent();
                persistenceUnitListeners = listeners;
                listeners.postCreate(EntityAgent.class, this);
            } catch (RuntimeException | Error failure) {
                if (open || poisoned) {
                    cleanup(failure, this::close);
                }
                throw failure;
            }
        }
        return this;
    }

    private void releaseLifecycleOwnership() {
        if (persistenceUnitListeners != null && !lifecycleReleased && disposed) {
            lifecycleReleased = true;
            persistenceUnitListeners.releaseAgent();
        }
    }

    EntityAgentImpl(EntityManagerFactoryDelegate factory, Map<?, ?> supplied) {
        this.factory = Objects.requireNonNull(factory);
        factory.verifyOpen();
        this.jta = factory.getTransactionType() == PersistenceUnitTransactionType.JTA;
        this.properties.putAll(factory.getProperties());
        if (supplied != null) {
            supplied.forEach((key, value) -> {
                if (key instanceof String name && QueryHintsHandler.getSupportedHints().contains(name)) {
                    properties.put(name, value);
                }
            });
        }
        this.session = factory.getServerSession().acquireClientSession(properties);
        try {
            factory.bindEntityListener(session);
            session.setCommitManager(new AgentCommitManager(session));
            session.setProperty(EntityAgentProperties.ENTITY_AGENT, Boolean.TRUE);
            setCacheRetrieveMode(mode(properties.get(QueryHints.CACHE_RETRIEVE_MODE), CacheRetrieveMode.class, CacheRetrieveMode.USE));
            setCacheStoreMode(mode(properties.get(QueryHints.CACHE_STORE_MODE), CacheStoreMode.class, CacheStoreMode.USE));
        } catch (RuntimeException | Error failure) {
            try {
                session.release();
            } catch (RuntimeException | Error cleanup) {
                suppress(failure, cleanup);
            }
            throw failure;
        }
    }

    private static <E extends Enum<E>> E mode(Object value, Class<E> type, E fallback) {
        if (value == null) {
            return fallback;
        }
        if (type.isInstance(value)) {
            return type.cast(value);
        }
        if (value instanceof String text) {
            return Enum.valueOf(type, text);
        }
        throw new IllegalArgumentException("Invalid " + type.getSimpleName());
    }

    @Override
    public void verifyOpen() {
        if (!open || poisoned) {
            throw new IllegalStateException("Entity agent is closed or its transaction resources are unusable");
        }
    }

    @Override
    public void verifyOpenWithSetRollbackOnly() {
        verifyOpen();
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public AbstractSession getSession() {
        verifyOpen();
        return session;
    }

    @Override
    public AbstractSession getExecutionSession(DatabaseQuery query) {
        verifyOpen();
        joinTransaction();
        return session;
    }

    @Override
    public boolean isEntityAgent() {
        return true;
    }

    @Override
    public FlushModeType getFlushMode() {
        verifyOpen();
        return FlushModeType.COMMIT;
    }

    @Override
    public boolean shouldFlush() {
        verifyOpen();
        return false;
    }

    @Override
    public void flush() {
        verifyOpen();
    }

    @Override
    public void registerQuery(QueryImpl query) {
        verifyOpen();
        queries.add(query);
    }

    @Override
    public void unregisterQuery(QueryImpl query) {
        // Disposal also runs after the owner is closed or poisoned.
        queries.remove(query);
    }

    private int depth() {
        return session.getTransactionMutex().getDepth();
    }

    @Override
    public boolean hasActiveTransaction() {
        verifyOpen();
        if (!jta) {
            return transaction.active;
        }
        return new JtaSupport().hasActiveTransaction();
    }

    @Override
    public void requireTransaction() {
        if (!hasActiveTransaction()) {
            throw new TransactionRequiredException("An active entity agent transaction is required");
        }
        joinTransaction();
    }

    @Override
    public void setRollbackOnly() {
        verifyOpen();
        if (jta) {
            if (externalTransaction() != null) {
                session.getExternalTransactionController().markTransactionForRollback();
            }
        } else if (transaction.active) {
            transaction.rollbackOnly = true;
        }
    }

    private Object externalTransaction() {
        if (session.getExternalTransactionController() instanceof AbstractTransactionController controller) {
            return controller.getTransaction();
        }
        return null;
    }

    void joinTransaction() {
        if (jta) {
            new JtaSupport().join();
        }
    }

    /** Keeps optional Jakarta Transactions types off the resource-local linkage path. */
    private final class JtaSupport {
        boolean hasActiveTransaction() {
            Object current = externalTransaction();
            if (current instanceof Transaction tx) {
                try {
                    return tx.getStatus() == Status.STATUS_ACTIVE;
                } catch (SystemException failure) {
                    throw new PersistenceException(failure);
                }
            }
            return current != null;
        }

        void join() {
            Object current = externalTransaction();
            if (current == null || current == joinedTransaction) {
                return;
            }
            if (!(current instanceof Transaction tx)) {
                throw new PersistenceException("Entity agent requires a Jakarta Transactions transaction controller");
            }
            if (joinedTransaction != null) {
                throw new IllegalStateException("Entity agent is already associated with another transaction");
            }
            try {
                tx.registerSynchronization(new Synchronization() {
                    @Override
                    public void beforeCompletion() {
                        try {
                            releaseQueryResources();
                            validateReadLocks();
                        } catch (RuntimeException | Error failure) {
                            try {
                                session.getExternalTransactionController().markTransactionForRollback();
                            } catch (RuntimeException | Error cleanup) {
                                suppress(failure, cleanup);
                            }
                            throw failure;
                        }
                    }

                    @Override
                    public void afterCompletion(int status) {
                        Throwable failure = cleanup(null, EntityAgentImpl.this::releaseQueryResources);
                        failure = cleanup(failure, () -> {
                            while (depth() > 0) {
                                session.getTransactionMutex().setActiveThread(Thread.currentThread());
                                session.getTransactionMutex().release();
                            }
                        });
                        failure = cleanup(failure, session::releaseJTSConnection);
                        failure = cleanup(failure, () -> session.getCommitManager().reinitialize());
                        session.setSynchronized(false);
                        joinedTransaction = null;
                        readLocks.clear();
                        if (status == Status.STATUS_COMMITTED) {
                            failure = cleanup(failure, EntityAgentImpl.this::completeCacheChanges);
                        } else {
                            discardCacheChanges();
                        }
                        if (!open) {
                            failure = cleanup(failure, session::release);
                            disposed = failure == null;
                            failure = cleanup(failure, EntityAgentImpl.this::releaseLifecycleOwnership);
                        }
                        if (failure != null) {
                            poisoned = true;
                            rethrow(failure);
                        }
                    }
                });
                joinedTransaction = tx;
                session.setSynchronized(true);
                session.beginTransaction();
            } catch (Exception failure) {
                poisoned = true;
                throw new PersistenceException("Could not associate entity agent transaction", failure);
            }
        }

    }

    private void unwind(int target) {
        while (depth() > target) {
            int before = depth();
            try {
                session.rollbackTransaction();
            } catch (RuntimeException | Error failure) {
                // Session event failures can release logical nesting without rolling
                // back the physical connection. Keep that cleanup obligation explicit.
                rollbackCleanupRequired = true;
                throw failure;
            }
            if (depth() >= before) {
                throw new IllegalStateException("Could not release entity agent transaction nesting");
            }
        }
        if (target == 0 && rollbackCleanupRequired) {
            // Retry only this client's still-owned connections, bypassing a failed
            // transaction event. Failed physical rollback discards the accessor.
            Throwable failure = null;
            for (Accessor accessor : session.getWriteConnections().values()) {
                try {
                    accessor.rollbackTransaction(session);
                } catch (RuntimeException | Error cleanup) {
                    accessor.setIsValid(false);
                    if (failure == null) {
                        failure = cleanup;
                    } else if (failure != cleanup) {
                        suppress(failure, cleanup);
                    }
                }
            }
            if (failure != null) {
                // Discard an accessor whose physical rollback remains uncertain.
                // Invalidating alone is insufficient while it is still checked out.
                for (Accessor accessor : session.getWriteConnections().values()) {
                    accessor.setIsValid(false);
                }
                failure = cleanup(failure, () -> session.getParent().releaseClientSession(session));
                rollbackCleanupRequired = session.hasWriteConnection();
                rethrow(failure);
            }
            rollbackCleanupRequired = false;
        }
    }

    private <T> T operation(boolean write, Supplier<T> work) {
        verifyOpen();
        joinTransaction();
        boolean owned = write && depth() == 0;
        int baseline = depth();
        try {
            if (owned) {
                session.beginTransaction();
            }
            T result = work.get();
            if (owned) {
                session.commitTransaction();
            }
            if (depth() == 0) {
                completeCacheChanges();
            }
            return result;
        } catch (RuntimeException | Error failure) {
            if (owned) {
                discardCacheChanges();
            } else if (failure instanceof DatabaseException database
                    && database.getInternalException() instanceof SQLException sql
                    && (session.getPlatform().isPostgreSQL()
                        || sql.getSQLState() != null && sql.getSQLState().startsWith("40"))) {
                // These failures abort the database transaction. Optimistic row-count
                // failures and statement-only failures on other platforms remain usable.
                try {
                    setRollbackOnly();
                } catch (RuntimeException | Error cleanup) {
                    suppress(failure, cleanup);
                }
            }
            try {
                unwind(owned || failure instanceof Error ? 0 : baseline);
                if (failure instanceof Error || failure instanceof DatabaseException database && database.isCommunicationFailure()) {
                    poisoned = true;
                }
            } catch (RuntimeException | Error cleanup) {
                poisoned = true;
                suppress(failure, cleanup);
            }
            throw failure;
        } finally {
            session.getCommitManager().reinitialize();
        }
    }

    private RuntimeException translate(RuntimeException failure, Object entity, boolean insert) {
        if (failure instanceof org.eclipse.persistence.exceptions.OptimisticLockException optimistic) {
            return new OptimisticLockException(optimistic.getMessage(), optimistic, entity == null ? optimistic.getObject() : entity);
        }
        if (failure instanceof DatabaseException database) {
            if (insert && session.getPlatform().isUniqueConstraintViolation(database)) {
                return new EntityExistsException("Entity insert violated a uniqueness constraint", database);
            }
            return new PersistenceException(database);
        }
        if (failure instanceof org.eclipse.persistence.exceptions.EclipseLinkException) {
            return new PersistenceException(failure);
        }
        return failure;
    }

    private ClassDescriptor descriptor(Class<?> type) {
        verifyOpen();
        if (type == null) {
            throw new IllegalArgumentException("Entity type must not be null");
        }
        ClassDescriptor descriptor = session.getDescriptor(type);
        if (descriptor == null || descriptor.isAggregateDescriptor() || descriptor.isAggregateCollectionDescriptor()) {
            throw new IllegalArgumentException("Not an entity type: " + type.getName());
        }
        return descriptor;
    }

    private ClassDescriptor descriptor(Object entity) {
        if (entity == null) {
            verifyOpen();
            throw new IllegalArgumentException("Entity must not be null");
        }
        return descriptor(entity.getClass());
    }

    private Object key(ClassDescriptor descriptor, Object id) {
        if (id == null) {
            throw new IllegalArgumentException("Entity identifier must not be null");
        }
        Class<?> type = descriptor.getCMPPolicy().getPKClass();
        if (type != null && !BasicTypeHelperImpl.getInstance().isStrictlyAssignableFrom(type, id.getClass())) {
            throw new IllegalArgumentException("Invalid identifier type: " + id.getClass().getName());
        }
        return descriptor.getCMPPolicy().createPrimaryKeyFromId(id, session);
    }

    private Object entityKey(ClassDescriptor descriptor, Object entity) {
        Object id = descriptor.getObjectBuilder().extractPrimaryKeyFromObject(entity, session);
        if (id == null) {
            throw new IllegalArgumentException("Entity identifier must not be null");
        }
        return id;
    }

    private FindOptionUtils.Options findOptions(FindOption... options) {
        if (options == null) {
            throw new IllegalArgumentException("Find options must not be null");
        }
        List<FindOption> known = new ArrayList<>();
        for (FindOption option : options) {
            if (option == null) {
                throw new IllegalArgumentException("Find option must not be null");
            }
            if (option instanceof LockModeType || option instanceof CacheRetrieveMode || option instanceof CacheStoreMode
                    || option instanceof PessimisticLockScope || option instanceof Timeout) {
                known.add(option);
            }
        }
        return FindOptionUtils.parse(new HashMap<>(properties), known.toArray(FindOption[]::new));
    }

    @Override
    public <T> T find(Class<T> type, Object id) {
        return find(type, id, new FindOption[0]);
    }

    @Override
    public <T> T find(Class<T> type, Object id, FindOption... options) {
        return find(type, null, id, options);
    }

    private <T> T find(Class<T> type, EntityGraph<T> graph, Object id, FindOption... options) {
        ClassDescriptor descriptor = descriptor(type);
        Object primaryKey = key(descriptor, id);
        FindOptionUtils.Options parsed = findOptions(options);
        ReadObjectQuery query = new ReadObjectQuery(type);
        query.setSelectionId(primaryKey);
        Map<String, Object> hints = new HashMap<>(parsed.properties());
        if (graph != null) {
            hints.put(QueryHints.JPA_LOAD_GRAPH, graph);
        }
        QueryHintsHandler.apply(hints, query, session.getLoader(), session);
        return type.cast(executeQuery(query, List.of(), parsed.lockModeType()));
    }

    private <T> Class<T> graphType(EntityGraph<T> graph) {
        verifyOpen();
        if (!(graph instanceof EntityGraphImpl<T> implementation)) {
            throw new IllegalArgumentException("Entity graph was not created by this provider");
        }
        descriptor(implementation.getClassType());
        return implementation.getClassType();
    }

    @Override
    public <T> T find(EntityGraph<T> graph, Object id, FindOption... options) {
        return find(graphType(graph), graph, id, options);
    }

    private static <T> T present(T value) {
        if (value == null) {
            throw new EntityNotFoundException("No entity exists for the requested identifier");
        }
        return value;
    }

    @Override
    public <T> T get(Class<T> type, Object id) {
        return present(find(type, id));
    }

    @Override
    public <T> T get(Class<T> type, Object id, FindOption... options) {
        return present(find(type, id, options));
    }

    @Override
    public <T> T get(EntityGraph<T> graph, Object id, FindOption... options) {
        return present(find(graph, id, options));
    }

    private <T> List<T> multiple(List<?> ids, Function<Object, T> operation) {
        verifyOpen();
        if (ids == null) {
            throw new IllegalArgumentException("Identifiers must not be null");
        }
        List<T> result = new ArrayList<>(ids.size());
        for (Object id : ids) {
            result.add(operation.apply(id));
        }
        return result;
    }

    @Override
    public <T> List<T> findMultiple(Class<T> type, List<?> ids, FindOption... options) {
        descriptor(type);
        return multiple(ids, id -> find(type, id, options));
    }

    @Override
    public <T> List<T> findMultiple(EntityGraph<T> graph, List<?> ids, FindOption... options) {
        graphType(graph);
        return multiple(ids, id -> find(graph, id, options));
    }

    @Override
    public <T> List<T> getMultiple(Class<T> type, List<?> ids, FindOption... options) {
        descriptor(type);
        return multiple(ids, id -> get(type, id, options));
    }

    @Override
    public <T> List<T> getMultiple(EntityGraph<T> graph, List<?> ids, FindOption... options) {
        graphType(graph);
        return multiple(ids, id -> get(graph, id, options));
    }

    @Override
    public Object executeQuery(DatabaseQuery original, List<Object> parameters, LockModeType lockMode) {
        return executeQuery(original, parameters, lockMode, null);
    }

    private <T> T materialize(DatabaseQuery query, Supplier<T> read) {
        Map<ClassDescriptor, Map<Object, org.eclipse.persistence.internal.identitymaps.CacheKey>> context = new HashMap<>();
        Object previous = query.getProperty(EntityAgentProperties.READ_CONTEXT);
        query.setProperty(EntityAgentProperties.ENTITY_AGENT, Boolean.TRUE);
        query.setProperty(EntityAgentProperties.READ_CONTEXT, context);
        try (var scope = org.eclipse.persistence.internal.queries.FetchedLockContext.open(query)) {
            if (scope.context() != null) {
                scope.context().attach(query);
            }
            return read.get();
        } finally {
            context.clear();
            if (previous == null) {
                query.removeProperty(EntityAgentProperties.READ_CONTEXT);
            } else {
                query.setProperty(EntityAgentProperties.READ_CONTEXT, previous);
            }
        }
    }

    private Object executeQuery(DatabaseQuery original, List<Object> parameters, LockModeType lockMode,
            org.eclipse.persistence.internal.sessions.AbstractRecord translationRow) {
        return executeQuery(original, parameters, lockMode, translationRow, Function.identity());
    }

    private Object executeQuery(DatabaseQuery original, List<Object> parameters, LockModeType lockMode,
            org.eclipse.persistence.internal.sessions.AbstractRecord translationRow, Function<Object, Object> completion) {
        verifyOpen();
        LockModeType lock = lockMode == null ? LockModeType.NONE : lockMode;
        if (lock != LockModeType.NONE || original.isModifyQuery()
                || Boolean.TRUE.equals(original.getProperty(EntityAgentProperties.MAPPING_LOCK))) {
            requireTransaction();
        }
        DatabaseQuery query = (DatabaseQuery) original.clone();
        query.setProperty(EntityAgentProperties.ENTITY_AGENT, Boolean.TRUE);
        query.dontMaintainCache();
        if (query instanceof org.eclipse.persistence.queries.ReadQuery readQuery) {
            readQuery.setQueryResultsCachePolicy(null);
        }
        boolean refresh = false;
        if (query instanceof ObjectLevelReadQuery read) {
            // Include basic state without discarding explicitly requested graph
            // traversal. Unrequested relationship holders remain detached below.
            read.prepareForDetachedRead();
            Map<String, Object> hints = QueryHintsHandler.get(original);
            Object storeMode = hints != null && hints.containsKey(QueryHints.CACHE_STORE_MODE)
                    ? hints.get(QueryHints.CACHE_STORE_MODE) : getCacheStoreMode();
            refresh = read.shouldRefreshIdentityMapResult() || mode(storeMode, CacheStoreMode.class, CacheStoreMode.USE) == CacheStoreMode.REFRESH;
            // Return an uncached copy. A separate callback-free refresh populates
            // the shared cache only after this transaction has committed.
            read.setShouldRefreshIdentityMapResult(false);
            read.dontCheckCache();
            if (lock != LockModeType.NONE) {
                if (read.setLockModeType(lock.name(), session)) {
                    throw new PersistenceException("The requested lock requires an entity version");
                }
                if (lock == LockModeType.OPTIMISTIC || lock == LockModeType.READ
                        || lock == LockModeType.OPTIMISTIC_FORCE_INCREMENT || lock == LockModeType.WRITE) {
                    read.setLockModeType(LockModeType.NONE.name(), session);
                }
                // Pessimistic mode enables refresh for managed reads. This read
                // already bypasses the cache and returns a detached copy.
                read.setShouldRefreshIdentityMapResult(false);
            }
        }
        if (query instanceof org.eclipse.persistence.queries.ModifyAllQuery modification) {
            modification.setCacheUsage(org.eclipse.persistence.queries.ModifyAllQuery.NO_CACHE);
        }
        query.setProperty(EntityAgentProperties.REFRESH_RESULTS, refresh);
        boolean previousRefresh = refreshingResults;
        refreshingResults = refresh;
        try {
            return operation(false, () -> materialize(query, () -> {
                if (query.isModifyQuery() && !readLocks.isEmpty()) {
                    // Establish the original optimistic obligations before our SQL
                    // changes their rows. These locks also prevent an intervening writer.
                    validateReadLocks();
                }
                Object result = translationRow == null ? session.executeQuery(query, parameters)
                        : session.executeQuery(query, translationRow);
                if (query.isModifyQuery()) {
                    reconcileOwnMutationReadLocks();
                    if (query.getReferenceClass() != null) {
                        cacheClassInvalidations.add(query.getReferenceClass());
                    } else if (getCacheStoreMode() != CacheStoreMode.BYPASS) {
                        // Native DML has no affected-entity metadata. Invalidation is
                        // conservative and deferred until successful completion.
                        for (ClassDescriptor descriptor : session.getDescriptors().values()) {
                            cacheClassInvalidations.add(descriptor.getJavaClass());
                        }
                    }
                }
                AgentCopyGroup group = new AgentCopyGroup();
                group.setCopies(new AgentCopies());
                try {
                    Object previous = session.getProperty(EntityAgentProperties.SUPPRESSED_COPY_SOURCE);
                    boolean internal = Boolean.TRUE.equals(query.getProperty(EntityAgentProperties.INTERNAL_READ));
                    Object detached;
                    try {
                        if (internal) {
                            session.setProperty(EntityAgentProperties.SUPPRESSED_COPY_SOURCE, result);
                        }
                        detached = detach(result, group);
                    } finally {
                        if (internal) {
                            if (previous == null) {
                                session.removeProperty(EntityAgentProperties.SUPPRESSED_COPY_SOURCE);
                            } else {
                                session.setProperty(EntityAgentProperties.SUPPRESSED_COPY_SOURCE, previous);
                            }
                        }
                    }
                    // Refresh callbacks run on the supplied object while deferred
                    // copies can still resolve and FETCHED locking is active.
                    Object completed = completion.apply(detached);
                    if (lock != LockModeType.NONE) {
                        registerReadLocks(completed, lock);
                    }
                    return completed;
                } finally {
                    group.finish();
                }
            }));
        } catch (RuntimeException failure) {
            throw queryFailure(failure, query, lock);
        } finally {
            refreshingResults = previousRefresh;
        }
    }

    private RuntimeException queryFailure(RuntimeException failure, DatabaseQuery query, LockModeType lock) {
        if (failure instanceof DatabaseException database && !database.isCommunicationFailure()
                && database.getInternalException() instanceof SQLException sql) {
            String state = sql.getSQLState();
            boolean timeout = sql instanceof SQLTimeoutException || "57014".equals(state)
                    || "HYT00".equals(state) || "HYT01".equals(state) || "HY008".equals(state)
                    || "S1T00".equals(state);
            boolean aborted = jta ? joinedTransaction != null && !new JtaSupport().hasActiveTransaction()
                    : transaction.active && transaction.rollbackOnly;
            boolean pessimistic = lock == LockModeType.PESSIMISTIC_READ || lock == LockModeType.PESSIMISTIC_WRITE
                    || lock == LockModeType.PESSIMISTIC_FORCE_INCREMENT
                    || query instanceof ObjectLevelReadQuery read && read.isLockQuery();
            if (pessimistic && (timeout || session.getPlatform().isLockTimeoutException(database)
                    || "55P03".equals(state) || sql.getErrorCode() == 50200
                    || state != null && state.startsWith("40"))) {
                return aborted ? new PessimisticLockException(database) : new LockTimeoutException(database);
            }
            if (timeout && !aborted) {
                return new QueryTimeoutException(database);
            }
        }
        return translate(failure, null, false);
    }

    @Override
    public void lockResult(Object entity, LockModeType mode) {
        if (mode == null || mode == LockModeType.NONE || entity == null) {
            return;
        }
        requireTransaction();
        ClassDescriptor descriptor = descriptor(entity);
        if (mode == LockModeType.PESSIMISTIC_READ || mode == LockModeType.PESSIMISTIC_WRITE
                || mode == LockModeType.PESSIMISTIC_FORCE_INCREMENT) {
            ReadObjectQuery read = new ReadObjectQuery(descriptor.getJavaClass());
            read.setSelectionId(entityKey(descriptor, entity));
            read.setProperty(EntityAgentProperties.ENTITY_AGENT, Boolean.TRUE);
            read.dontMaintainCache();
            read.dontCheckCache();
            read.setProperty(EntityAgentProperties.INTERNAL_READ, Boolean.TRUE);
            read.setLockModeType(LockModeType.PESSIMISTIC_WRITE.name(), session);
            try {
                operation(false, () -> present(materialize(read, () -> session.executeQuery(read))));
            } catch (RuntimeException failure) {
                throw queryFailure(failure, read, mode);
            }
        }
        registerReadLocks(entity, mode);
    }

    @Override
    public Object materializeCursorRow(DatabaseQuery query, Supplier<Object> build, LockModeType lockMode) {
        verifyOpen();
        LockModeType lock = lockMode == null ? LockModeType.NONE : lockMode;
        if (lock != LockModeType.NONE) {
            requireTransaction();
        }
        boolean previousRefresh = refreshingResults;
        refreshingResults = Boolean.TRUE.equals(query.getProperty(EntityAgentProperties.REFRESH_RESULTS));
        try {
            return operation(false, () -> materialize(query, () -> {
                Object detached = detachResult(build.get());
                if (lock != LockModeType.NONE) {
                    registerReadLocks(detached, lock);
                }
                return detached;
            }));
        } catch (RuntimeException failure) {
            throw queryFailure(failure, query, lock);
        } finally {
            refreshingResults = previousRefresh;
        }
    }

    @Override
    public Object prepareResult(Object value) {
        verifyOpen();
        return detachResult(value);
    }

    private Object detachResult(Object value) {
        AgentCopyGroup group = new AgentCopyGroup();
        group.setCopies(new AgentCopies());
        try {
            return detach(value, group);
        } finally {
            group.finish();
        }
    }

    private final class AgentCopyGroup extends CopyGroup {
        private final List<DetachedValueHolder> holders = new ArrayList<>();
        private final List<DetachedReferenceGroup> references = new ArrayList<>();

        private void finish() {
            holders.forEach(holder -> holder.resolver = null);
            references.forEach(group -> group.resolver = null);
            holders.clear();
            references.clear();
        }

        @Override
        public Object createCopy(Object original) {
            AgentCopies copies = (AgentCopies) getCopies();
            Object reference = copies.references.remove(copies.key(original));
            if (reference != null) {
                ClassDescriptor descriptor = descriptor(reference);
                descriptor.getFetchGroupManager().setObjectFetchGroup(reference, null, session);
                descriptor.getObjectBuilder().copyInto(original, reference, true);
            }
            return reference;
        }

        @Override
        public boolean copyUnfetchedAttribute(DatabaseMapping mapping, Object original, Object copy) {
            if (!(mapping instanceof org.eclipse.persistence.mappings.ForeignReferenceMapping reference)
                    || !reference.usesIndirection()) {
                return false;
            }
            Object attribute = mapping.getAttributeValueFromObject(original);
            org.eclipse.persistence.indirection.ValueHolderInterface<?> holder =
                    attribute instanceof org.eclipse.persistence.indirection.IndirectContainer container
                            ? container.getValueHolder()
                            : attribute instanceof org.eclipse.persistence.indirection.ValueHolderInterface<?> value ? value : null;
            if (holder == null || holder.isInstantiated()) {
                return false;
            }
            while (holder instanceof org.eclipse.persistence.internal.indirection.DatabaseValueHolder<?> database
                    && database.getWrappedValueHolder() != null) {
                holder = database.getWrappedValueHolder();
            }
            if (!(holder instanceof org.eclipse.persistence.internal.indirection.DatabaseValueHolder<?> database)
                    || database.getRow() == null) {
                return false;
            }
            org.eclipse.persistence.indirection.ValueHolderInterface<?> detached;
            if (mapping.isObjectReferenceMapping() && reference.getReferenceDescriptor().hasFetchGroupManager()) {
                Object value = detachedReference(reference, database.getRow(), this);
                detached = new org.eclipse.persistence.indirection.ValueHolder<>(value);
            } else {
                DetachedValueHolder value = new DetachedValueHolder(mapping.getDescriptor().getJavaClass(),
                        mapping.getAttributeName(), database.getRow());
                value.resolver = () -> loadAssociation(reference, value.getRow(), this);
                holders.add(value);
                detached = value;
            }
            mapping.setAttributeValueInObject(copy, reference.getIndirectionPolicy().buildIndirectObject(detached));
            return true;
        }
    }

    /** A deferred association retains data only, never an agent, query or session. */
    private static final class DetachedValueHolder extends org.eclipse.persistence.internal.indirection.DatabaseValueHolder<Object> {
        private static final long serialVersionUID = 1L;
        private final Class<?> sourceClass;
        private final String attribute;
        private transient Supplier<Object> resolver;

        private DetachedValueHolder(Class<?> sourceClass, String attribute,
                org.eclipse.persistence.internal.sessions.AbstractRecord sourceRow) {
            this.sourceClass = sourceClass;
            this.attribute = attribute;
            this.row = (org.eclipse.persistence.internal.sessions.AbstractRecord) sourceRow.clone();
        }

        @Override
        protected Object instantiate() {
            if (resolver != null) {
                return resolver.get();
            }
            throw new IllegalStateException("Use an open entity agent to fetch this detached association");
        }

        @Override
        public Object instantiateForUnitOfWorkValueHolder(
                org.eclipse.persistence.internal.indirection.UnitOfWorkValueHolder<Object> holder) {
            return instantiate();
        }

        @Override
        public boolean isPessimisticLockingValueHolder() {
            return false;
        }
    }

    /** The temporary resolver is cleared before a detached result leaves copying. */
    private static final class DetachedReferenceGroup extends org.eclipse.persistence.internal.queries.EntityFetchGroup {
        private static final long serialVersionUID = 1L;
        private transient Consumer<Object> resolver;

        private DetachedReferenceGroup(Collection<String> attributes) {
            super(attributes);
        }

        @Override
        public String onUnfetchedAttribute(org.eclipse.persistence.queries.FetchGroupTracker entity, String attribute) {
            if (resolver != null) {
                resolver.accept(entity);
                return null;
            }
            return super.onUnfetchedAttribute(entity, attribute);
        }
    }

    /** Inspect an agent identity reference without resolving or loading it. */
    static boolean isUnfetchedReference(Object entity) {
        return entity instanceof org.eclipse.persistence.queries.FetchGroupTracker tracker
                && tracker._persistence_getFetchGroup() instanceof DetachedReferenceGroup;
    }

    private Object loadAssociation(org.eclipse.persistence.mappings.ForeignReferenceMapping mapping,
            org.eclipse.persistence.internal.sessions.AbstractRecord row, AgentCopyGroup copies) {
        org.eclipse.persistence.queries.ReadQuery query = (org.eclipse.persistence.queries.ReadQuery) mapping.getSelectionQuery().clone();
        query = mapping.prepareFetchedQuery(query, row, session,
                org.eclipse.persistence.internal.queries.FetchedLockContext.current());
        return loadForCopy(query, row, copies);
    }

    private Object loadForCopy(org.eclipse.persistence.queries.ReadQuery query,
            org.eclipse.persistence.internal.sessions.AbstractRecord row, AgentCopyGroup copies) {
        query.setProperty(EntityAgentProperties.ENTITY_AGENT, Boolean.TRUE);
        query.dontMaintainCache();
        query.setQueryResultsCachePolicy(null);
        if (query instanceof ObjectLevelReadQuery read) {
            read.prepareForDetachedRead();
            read.dontCheckCache();
            read.setShouldRefreshIdentityMapResult(false);
        }
        return materialize(query, () -> detach(session.executeQuery(query, row), copies));
    }

    private Object detachedReference(org.eclipse.persistence.mappings.ForeignReferenceMapping mapping,
            org.eclipse.persistence.internal.sessions.AbstractRecord row, AgentCopyGroup group) {
        AgentCopies copies = (AgentCopies) group.getCopies();
        ClassDescriptor target = mapping.getReferenceDescriptor();
        Object reference;
        if (mapping instanceof org.eclipse.persistence.mappings.OneToOneMapping one
                && one.isForeignKeyRelationship() && !target.hasInheritance()
                && one.getTargetToSourceKeyFields().keySet().containsAll(target.getPrimaryKeyFields())) {
            boolean hasKey = target.getPrimaryKeyFields().stream()
                    .anyMatch(field -> row.get(one.getTargetToSourceKeyFields().get(field)) != null);
            if (!hasKey) {
                return null;
            }
            Object key = one.extractPrimaryKeysForReferenceObjectFromRow(row);
            if (key == null) {
                return null;
            }
            Object existing = copies.entities.get(new EntityKey(target.getJavaClass(), key));
            if (existing != null) {
                return existing;
            }
            org.eclipse.persistence.internal.descriptors.ObjectBuilder builder = target.getObjectBuilder();
            reference = builder.buildNewInstance();
            builder.buildPrimaryKeyAttributesIntoObject(reference,
                    builder.buildRowFromPrimaryKeyValues(key, session), new ReadObjectQuery(), session);
        } else {
            // Retain custom selection criteria/calls and discriminator handling,
            // but read only identity/minimal state, never a recursive target graph.
            ObjectLevelReadQuery read = (ObjectLevelReadQuery) mapping.getSelectionQuery().clone();
            read.setShouldUseDefaultFetchGroup(false);
            read.setLoadGroup(null);
            org.eclipse.persistence.queries.FetchGroup identity = new org.eclipse.persistence.queries.FetchGroup();
            for (String name : target.getFetchGroupManager().getIdEntityFetchGroup().getAttributeNames()) {
                identity.addAttribute(name);
            }
            identity.setShouldLoad(false);
            read.setFetchGroup(identity);
            read.dontMaintainCache();
            read.dontCheckCache();
            read.setProperty(EntityAgentProperties.ENTITY_AGENT, Boolean.TRUE);
            read.setProperty(EntityAgentProperties.INTERNAL_READ, Boolean.TRUE);
            reference = materialize(read, () -> session.executeQuery(read, row));
            if (reference == null) {
                return null;
            }
            target = descriptor(reference);
            Object existing = copies.entities.get(new EntityKey(target.getJavaClass(), entityKey(target, reference)));
            if (existing != null) {
                return existing;
            }
        }
        EntityKey key = new EntityKey(target.getJavaClass(), entityKey(target, reference));
        Object previous = copies.references.putIfAbsent(key, reference);
        if (previous != null) {
            return previous;
        }
        DetachedReferenceGroup identity = new DetachedReferenceGroup(
                target.getFetchGroupManager().getIdEntityFetchGroup().getAttributeNames());
        identity.resolver = entity -> {
            ReadObjectQuery read = new ReadObjectQuery(key.type());
            read.setSelectionId(key.key());
            var context = org.eclipse.persistence.internal.queries.FetchedLockContext.current();
            if (context != null && context.isActive()) {
                read.setLockMode(context.getLockMode());
                read.setShouldLockFetchedRelationships(true);
                context.attach(read);
            }
            present(loadForCopy(read, new org.eclipse.persistence.sessions.DatabaseRecord(), group));
        };
        identity.setOnEntity(reference, null);
        group.references.add(identity);
        return reference;
    }

    /** Operation-local graph identity, discarded with the detached result assembly. */
    private final class AgentCopies extends IdentityHashMap<Object, Object> {
        private static final long serialVersionUID = 1L;
        private final Map<EntityKey, Object> entities = new HashMap<>();
        private final Map<EntityKey, Object> references = new HashMap<>();

        private EntityKey key(Object source) {
            ClassDescriptor descriptor = source == null ? null : session.getDescriptor(source);
            if (descriptor == null || descriptor.isAggregateDescriptor() || descriptor.isAggregateCollectionDescriptor()) {
                return null;
            }
            Object primaryKey = entityKey(descriptor, source);
            return primaryKey == null ? null : new EntityKey(descriptor.getJavaClass(), primaryKey);
        }

        @Override
        public Object get(Object source) {
            EntityKey key = key(source);
            return key == null ? super.get(source) : entities.get(key);
        }

        @Override
        public Object put(Object source, Object copy) {
            EntityKey key = key(source);
            Object previous = super.put(source, copy);
            return key == null ? previous : entities.put(key, copy);
        }
    }

    private Object detach(Object value, CopyGroup copies) {
        if (value == null) {
            return null;
        }
        copies.setSession(session);
        copies.cascadeAllParts();
        if (value instanceof Collection<?> collection) {
            Collection<Object> result = value instanceof Set<?> ? new LinkedHashSet<>()
                    : value instanceof java.util.Vector<?> ? new java.util.Vector<>(collection.size()) : new ArrayList<>(collection.size());
            for (Object item : collection) {
                result.add(detach(item, copies));
            }
            return result;
        }
        if (value instanceof Map<?, ?> map) {
            Map<Object, Object> result = value instanceof java.util.Hashtable<?, ?> ? new java.util.Hashtable<>() : new LinkedHashMap<>();
            map.forEach((key, item) -> result.put(detach(key, copies), detach(item, copies)));
            return result;
        }
        if (value instanceof Object[] row) {
            Object[] result = row.clone();
            for (int i = 0; i < result.length; i++) {
                result[i] = detach(result[i], copies);
            }
            return result;
        }
        ClassDescriptor descriptor = session.getDescriptor(value);
        if (descriptor == null) {
            return value;
        }
        if (refreshingResults && descriptor.getCachePolicy().isSharedIsolation()) {
            cacheRefreshes.add(new EntityKey(descriptor.getJavaClass(), entityKey(descriptor, value)));
        }
        return session.copyInternal(value, copies);
    }

    private void registerReadLocks(Object result, LockModeType mode) {
        if (result instanceof Collection<?> collection) {
            collection.forEach(value -> registerReadLocks(value, mode));
        } else if (result instanceof Object[] row) {
            for (Object value : row) {
                registerReadLocks(value, mode);
            }
        } else if (result != null) {
            ClassDescriptor descriptor = session.getDescriptor(result);
            if (descriptor != null && descriptor.getOptimisticLockingPolicy() instanceof VersionLockingPolicy policy) {
                Object key = entityKey(descriptor, result);
                Object version = policy.getVersionMapping().getAttributeValueFromObject(result);
                readLocks.putIfAbsent(new EntityKey(descriptor.getJavaClass(), key), new ReadLock(descriptor, key, version, result));
                if (mode == LockModeType.WRITE || mode == LockModeType.OPTIMISTIC_FORCE_INCREMENT
                        || mode == LockModeType.PESSIMISTIC_FORCE_INCREMENT) {
                    forceIncrement(descriptor, key, result, policy);
                }
            }
        }
    }

    private void forceIncrement(ClassDescriptor descriptor, Object key, Object entity, VersionLockingPolicy policy) {
        Object before = policy.getVersionMapping().getAttributeValueFromObject(entity);
        UpdateObjectQuery query = new UpdateObjectQuery(entity);
        query.setProperty(OPERATION, "LOCK");
        query.dontMaintainCache();
        query.dontCascadeParts();
        try {
            session.executeQuery(query);
            cacheInvalidations.add(new EntityKey(descriptor.getJavaClass(), key));
            readLocks.put(new EntityKey(descriptor.getJavaClass(), key),
                    new ReadLock(descriptor, key, policy.getVersionMapping().getAttributeValueFromObject(entity), entity));
        } catch (RuntimeException failure) {
            policy.getVersionMapping().setAttributeValueInObject(entity, before);
            throw translate(failure, entity, false);
        }
    }

    private Object readLockedRow(ReadLock lock) {
        ReadObjectQuery query = new ReadObjectQuery(lock.descriptor().getJavaClass());
        query.setSelectionId(lock.key());
        query.setProperty(EntityAgentProperties.ENTITY_AGENT, Boolean.TRUE);
        query.dontMaintainCache();
        query.dontCheckCache();
        query.setLockModeType(LockModeType.PESSIMISTIC_WRITE.name(), session);
        query.setProperty(EntityAgentProperties.INTERNAL_READ, Boolean.TRUE);
        return materialize(query, () -> session.executeQuery(query));
    }

    private void validateReadLocks() {
        for (ReadLock lock : readLocks.values()) {
            Object current = readLockedRow(lock);
            VersionLockingPolicy policy = (VersionLockingPolicy) lock.descriptor().getOptimisticLockingPolicy();
            if (current == null || !Objects.equals(lock.version(), policy.getVersionMapping().getAttributeValueFromObject(current))) {
                throw new OptimisticLockException("Entity changed after an optimistic read", null, lock.entity());
            }
        }
    }

    /** The original obligations were checked and locked before this successful bulk mutation. */
    private void reconcileOwnMutationReadLocks() {
        for (var iterator = readLocks.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<EntityKey, ReadLock> entry = iterator.next();
            ReadLock lock = entry.getValue();
            Object current = readLockedRow(lock);
            if (current == null) {
                iterator.remove();
            } else {
                VersionLockingPolicy policy = (VersionLockingPolicy) lock.descriptor().getOptimisticLockingPolicy();
                entry.setValue(new ReadLock(lock.descriptor(), lock.key(),
                        policy.getVersionMapping().getAttributeValueFromObject(current), lock.entity()));
            }
        }
    }

    private static final class AgentCommitManager extends CommitManager {
        AgentCommitManager(AbstractSession session) {
            super(session);
        }

        void beginOperation() {
            this.isActive = true;
        }

        void completeOwnedData() {
            while (hasDeferredCalls()) {
                var pending = new LinkedHashMap<>(getDeferredCalls());
                getDeferredCalls().clear();
                for (List<Object[]> calls : pending.values()) {
                    for (Object[] call : calls) {
                        ((org.eclipse.persistence.internal.queries.DatabaseQueryMechanism) call[1])
                                .executeDeferredCall((org.eclipse.persistence.internal.databaseaccess.DatasourceCall) call[0]);
                    }
                }
            }
            while (hasDataModifications()) {
                Map<DatabaseMapping, List<Object[]>> pending = new LinkedHashMap<>(getDataModifications());
                getDataModifications().clear();
                for (Map.Entry<DatabaseMapping, List<Object[]>> entry : pending.entrySet()) {
                    for (Object[] event : entry.getValue()) {
                        entry.getKey().performDataModificationEvent(event, getSession());
                    }
                }
            }
        }
    }

    private record EntityKey(Class<?> type, Object key) { }
    private record ReadLock(ClassDescriptor descriptor, Object key, Object version, Object entity) { }

    private void write(Object entity, String operation) {
        ClassDescriptor descriptor = descriptor(entity);
        if (!operation.equals("INSERT")) {
            entityKey(descriptor, entity);
        }
        VersionLockingPolicy version = descriptor.getOptimisticLockingPolicy() instanceof VersionLockingPolicy policy ? policy : null;
        Object before = version == null ? null : version.getVersionMapping().getAttributeValueFromObject(entity);
        boolean[] written = {false};
        Object previousOperation = session.getProperty(OPERATION);
        Object previousWriteSucceeded = session.getProperty(EntityAgentProperties.WRITE_SUCCEEDED);
        try {
            operation(true, () -> {
                ObjectLevelModifyQuery query;
                if (operation.equals("UPSERT")) {
                    ReadObjectQuery read = new ReadObjectQuery(descriptor.getJavaClass());
                    read.setSelectionId(entityKey(descriptor, entity));
                    read.dontMaintainCache();
                    read.dontCheckCache();
                    read.setProperty(EntityAgentProperties.INTERNAL_READ, Boolean.TRUE);
                    Object existing = materialize(read, () -> session.executeQuery(read));
                    query = existing == null ? new InsertObjectQuery(entity) : new UpdateObjectQuery(entity);
                } else {
                    query = switch (operation) {
                        case "INSERT" -> new InsertObjectQuery(entity);
                        case "UPDATE" -> new UpdateObjectQuery(entity);
                        case "DELETE" -> new DeleteObjectQuery(entity);
                        default -> throw new IllegalArgumentException(operation);
                    };
                }
                query.setProperty(OPERATION, operation);
                if (query.isUpdateObjectQuery()) {
                    ReadObjectQuery read = new ReadObjectQuery(descriptor.getJavaClass());
                    read.setSelectionId(entityKey(descriptor, entity));
                    read.setShouldUseDefaultFetchGroup(false);
                    read.dontMaintainCache();
                    read.dontCheckCache();
                    read.setProperty(EntityAgentProperties.INTERNAL_READ, Boolean.TRUE);
                    query.setProperty(EntityAgentProperties.ORIGINAL, materialize(read, () -> session.executeQuery(read)));
                }
                session.setProperty(OPERATION, operation);
                session.setProperty(EntityAgentProperties.WRITE_SUCCEEDED, Boolean.FALSE);
                query.dontMaintainCache();
                query.cascadeOnlyDependentParts();
                ((AgentCommitManager) session.getCommitManager()).beginOperation();
                session.executeQuery(query);
                written[0] = true;
                ((AgentCommitManager) session.getCommitManager()).completeOwnedData();
                cacheInvalidations.add(new EntityKey(descriptor.getJavaClass(), entityKey(descriptor, entity)));
                if (version != null) {
                    EntityKey key = new EntityKey(descriptor.getJavaClass(), entityKey(descriptor, entity));
                    if (operation.equals("DELETE")) {
                        readLocks.remove(key);
                    } else if (readLocks.containsKey(key)) {
                        readLocks.put(key, new ReadLock(descriptor, key.key(), version.getVersionMapping().getAttributeValueFromObject(entity), entity));
                    }
                }
                return null;
            });
        } catch (RuntimeException failure) {
            if (!written[0] && !Boolean.TRUE.equals(session.getProperty(EntityAgentProperties.WRITE_SUCCEEDED)) && version != null) {
                version.getVersionMapping().setAttributeValueInObject(entity, before);
            }
            throw translate(failure, entity, operation.equals("INSERT"));
        } finally {
            if (previousOperation == null) {
                session.removeProperty(OPERATION);
            } else {
                session.setProperty(OPERATION, previousOperation);
            }
            if (previousWriteSucceeded == null) {
                session.removeProperty(EntityAgentProperties.WRITE_SUCCEEDED);
            } else {
                session.setProperty(EntityAgentProperties.WRITE_SUCCEEDED, previousWriteSucceeded);
            }
        }
    }

    @Override
    public void insert(Object entity) { write(entity, "INSERT"); }
    @Override
    public void update(Object entity) { write(entity, "UPDATE"); }
    @Override
    public void delete(Object entity) { write(entity, "DELETE"); }
    @Override
    public void upsert(Object entity) { write(entity, "UPSERT"); }

    private void writeMultiple(List<?> entities, Consumer<Object> action) {
        verifyOpen();
        if (entities == null) {
            throw new IllegalArgumentException("Entities must not be null");
        }
        entities.forEach(action);
    }

    @Override
    public void insertMultiple(List<?> entities) { writeMultiple(entities, this::insert); }
    @Override
    public void updateMultiple(List<?> entities) { writeMultiple(entities, this::update); }
    @Override
    public void deleteMultiple(List<?> entities) { writeMultiple(entities, this::delete); }
    @Override
    public void upsertMultiple(List<?> entities) { writeMultiple(entities, this::upsert); }

    @Override
    public void refresh(Object entity) { refresh(entity, LockModeType.NONE); }

    @Override
    public void refresh(Object entity, LockModeType lock) {
        ClassDescriptor descriptor = descriptor(entity);
        ReadObjectQuery query = new ReadObjectQuery(descriptor.getJavaClass());
        query.setSelectionId(entityKey(descriptor, entity));
        QueryHintsHandler.apply(findOptions(Objects.requireNonNull(lock)).properties(), query, session.getLoader(), session);
        query.setProperty(EntityAgentProperties.INTERNAL_READ, Boolean.TRUE);
        executeQuery(query, List.of(), lock, null, value -> {
            Object current = present(value);
            if (descriptor.hasFetchGroupManager()) {
                // Clear partial state only after a successful read, before woven
                // setters can access the former session.
                descriptor.getFetchGroupManager().setObjectFetchGroup(entity, null, session);
            }
            for (DatabaseMapping mapping : descriptor.getMappings()) {
                mapping.setAttributeValueInObject(entity, mapping.getAttributeValueFromObject(current));
            }
            org.eclipse.persistence.descriptors.DescriptorEvent event = new org.eclipse.persistence.descriptors.DescriptorEvent(entity);
            event.setEventCode(org.eclipse.persistence.descriptors.DescriptorEventManager.PostRefreshEvent);
            event.setSession(session);
            descriptor.getEventManager().executeEvent(event);
            return entity;
        });
    }

    @Override
    public void refreshMultiple(List<?> entities) { writeMultiple(entities, this::refresh); }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public <T> T fetch(T association) {
        verifyOpen();
        if (association instanceof org.eclipse.persistence.indirection.IndirectContainer container) {
            if (!container.isInstantiated()) {
                // Replacing an unfetched detached container's holder must not invoke
                // the old UnitOfWork holder's change-tracking setter and source read.
                container.setValueHolder(new org.eclipse.persistence.indirection.ValueHolder(
                        fetchValue(container.getValueHolder())));
            }
        } else if (association instanceof org.eclipse.persistence.indirection.ValueHolderInterface<?> holder) {
            fetch(holder);
        } else if (association instanceof org.eclipse.persistence.queries.FetchGroupTracker
                && !factory.getPersistenceUnitUtil().isLoaded(association)) {
            refresh(association);
        }
        return association;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void fetch(org.eclipse.persistence.indirection.ValueHolderInterface holder) {
        if (holder.isInstantiated()) {
            return;
        }
        holder.setValue(fetchValue(holder));
    }

    private Object fetchValue(org.eclipse.persistence.indirection.ValueHolderInterface<?> holder) {
        org.eclipse.persistence.indirection.ValueHolderInterface<?> source = holder;
        while (source instanceof org.eclipse.persistence.internal.indirection.DatabaseValueHolder<?> database
                && database.getWrappedValueHolder() != null) {
            source = database.getWrappedValueHolder();
        }
        if (source instanceof DetachedValueHolder detached) {
            DatabaseMapping mapping = descriptor(detached.sourceClass).getMappingForAttributeName(detached.attribute);
            if (!(mapping instanceof org.eclipse.persistence.mappings.ForeignReferenceMapping reference)) {
                throw new IllegalArgumentException("Detached association is not mapped by this persistence unit");
            }
            return executeQuery(reference.getSelectionQuery(), List.of(), LockModeType.NONE, detached.getRow());
        }
        if (source instanceof org.eclipse.persistence.internal.indirection.QueryBasedValueHolder<?> queryHolder) {
            return executeQuery(queryHolder.getQueryForIndependentInstantiation(), List.of(), LockModeType.NONE,
                    queryHolder.getRow());
        }
        return operation(false, holder::getValue);
    }

    @Override
    public void setCacheRetrieveMode(CacheRetrieveMode mode) {
        verifyOpen();
        properties.put(QueryHints.CACHE_RETRIEVE_MODE, Objects.requireNonNull(mode));
    }

    @Override
    public void setCacheStoreMode(CacheStoreMode mode) {
        verifyOpen();
        properties.put(QueryHints.CACHE_STORE_MODE, Objects.requireNonNull(mode));
    }

    @Override
    public CacheRetrieveMode getCacheRetrieveMode() {
        verifyOpen();
        return (CacheRetrieveMode) properties.get(QueryHints.CACHE_RETRIEVE_MODE);
    }

    @Override
    public CacheStoreMode getCacheStoreMode() {
        verifyOpen();
        return (CacheStoreMode) properties.get(QueryHints.CACHE_STORE_MODE);
    }

    @Override
    public void setProperty(String name, Object value) {
        verifyOpen();
        Objects.requireNonNull(name);
        if (name.equals(QueryHints.CACHE_RETRIEVE_MODE)) {
            setCacheRetrieveMode(mode(value, CacheRetrieveMode.class, CacheRetrieveMode.USE));
        } else if (name.equals(QueryHints.CACHE_STORE_MODE)) {
            setCacheStoreMode(mode(value, CacheStoreMode.class, CacheStoreMode.USE));
        } else if (QueryHintsHandler.getSupportedHints().contains(name)) {
            properties.put(name, value);
        }
    }

    @Override
    public Map<String, Object> getProperties() { return new HashMap<>(properties); }

    @Override
    public void addOption(Option option) {
        verifyOpen();
        Objects.requireNonNull(option);
        if (option instanceof CacheRetrieveMode retrieve) {
            setCacheRetrieveMode(retrieve);
        } else if (option instanceof CacheStoreMode store) {
            setCacheStoreMode(store);
        } else {
            throw new IllegalArgumentException("Unrecognized entity agent option");
        }
    }

    @Override
    public Set<Option> getOptions() {
        verifyOpen();
        return new HashSet<>(List.of(getCacheRetrieveMode(), getCacheStoreMode()));
    }

    @Override
    public EntityTransaction getTransaction() {
        if (jta) {
            throw new IllegalStateException("JTA entity agents do not expose resource-local transactions");
        }
        return transaction;
    }

    @Override
    public EntityManagerFactory getEntityManagerFactory() { verifyOpen(); return factory; }
    @Override
    public CriteriaBuilder getCriteriaBuilder() { verifyOpen(); return factory.getCriteriaBuilder(); }
    @Override
    public Metamodel getMetamodel() { verifyOpen(); return factory.getMetamodel(); }

    @Override
    public <T> T unwrap(Class<T> type) {
        verifyOpen();
        if (type != null && type.isInstance(this)) {
            return type.cast(this);
        }
        if (type != null && type.isInstance(session)) {
            return type.cast(session);
        }
        throw new PersistenceException("Unsupported entity agent unwrap type");
    }

    @Override
    public <T> EntityGraph<T> createEntityGraph(Class<T> type) {
        return new EntityGraphImpl<>(new AttributeGroup(null, type, true), descriptor(type), getMetamodel());
    }

    @Override
    public EntityGraph<?> getEntityGraph(String name) {
        verifyOpen();
        AttributeGroup group = factory.getServerSession().getAttributeGroups().get(name);
        if (group == null) {
            throw new IllegalArgumentException("No entity graph named " + name);
        }
        return new EntityGraphImpl<>(group.clone(), descriptor(group.getType()), getMetamodel());
    }

    @Override
    public <T> EntityGraph<T> getEntityGraph(Class<T> type, String name) {
        descriptor(type);
        EntityGraph<?> graph = getEntityGraph(name);
        if (type != ((EntityGraphImpl<?>) graph).getClassType()) {
            throw new IllegalArgumentException("Entity graph has an incompatible type");
        }
        @SuppressWarnings("unchecked") EntityGraph<T> result = (EntityGraph<T>) graph;
        return result;
    }

    @Override
    public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> type) {
        ClassDescriptor descriptor = descriptor(type);
        List<EntityGraph<? super T>> result = new ArrayList<>();
        do {
            for (AttributeGroup group : descriptor.getAttributeGroups().values()) {
                result.add(new EntityGraphImpl<>(group.clone(), descriptor, getMetamodel()));
            }
            descriptor = descriptor.hasInheritance() ? descriptor.getInheritancePolicy().getParentDescriptor() : null;
        } while (descriptor != null);
        return result;
    }

    @Override
    public <C> void runWithConnection(ConnectionConsumer<C> action) {
        this.<C, Void>callWithConnection(connection -> { action.accept(connection); return null; });
    }

    @Override
    public <C, T> T callWithConnection(ConnectionFunction<C, T> function) {
        verifyOpen();
        Objects.requireNonNull(function);
        return operation(true, () -> {
            Accessor accessor = session.getAccessor();
            accessor.incrementCallCount(session);
            Throwable primary = null;
            try {
                @SuppressWarnings("unchecked") C connection = (C) accessor.getDatasourceConnection();
                return function.apply(connection);
            } catch (Exception | Error failure) {
                primary = failure;
                try {
                    setRollbackOnly();
                } catch (RuntimeException | Error cleanup) {
                    suppress(failure, cleanup);
                }
                if (failure instanceof Error error) {
                    throw error;
                }
                throw failure instanceof RuntimeException runtime ? runtime : new PersistenceException(failure);
            } finally {
                try {
                    accessor.decrementCallCount();
                } catch (RuntimeException | Error cleanup) {
                    if (primary == null) {
                        throw cleanup;
                    }
                    suppress(primary, cleanup);
                }
            }
        });
    }

    private void discardCacheChanges() {
        cacheInvalidations.clear();
        cacheRefreshes.clear();
        cacheClassInvalidations.clear();
    }

    private void completeCacheChanges() {
        var shared = factory.getServerSession();
        Set<EntityKey> refreshes = new LinkedHashSet<>(cacheRefreshes);
        try {
            for (Class<?> type : cacheClassInvalidations) {
                shared.getIdentityMapAccessor().invalidateClass(type);
            }
            for (EntityKey key : cacheInvalidations) {
                shared.getIdentityMapAccessor().invalidateObject(key.key(), key.type());
            }
        } finally {
            discardCacheChanges();
        }
        for (EntityKey key : refreshes) {
            ReadObjectQuery refresh = new ReadObjectQuery(key.type());
            refresh.setSelectionId(key.key());
            refresh.retrieveBypassCache();
            refresh.refreshIdentityMapResult();
            refresh.setProperty(EntityAgentProperties.INTERNAL_READ, Boolean.TRUE);
            shared.executeQuery(refresh);
        }
    }

    private static void suppress(Throwable failure, Throwable cleanup) {
        if (failure != cleanup) {
            failure.addSuppressed(cleanup);
        }
    }

    private static Throwable cleanup(Throwable failure, Runnable action) {
        try {
            action.run();
        } catch (RuntimeException | Error cleanup) {
            if (failure == null) {
                return cleanup;
            }
            suppress(failure, cleanup);
        }
        return failure;
    }

    private static void rethrow(Throwable failure) {
        if (failure instanceof Error error) {
            throw error;
        }
        throw (RuntimeException) failure;
    }

    private void releaseQueryResources() {
        Throwable failure = null;
        for (QueryImpl query : new ArrayList<>(queries)) {
            failure = cleanup(failure, () -> {
                query.releaseResources();
                queries.remove(query);
            });
        }
        if (failure != null) {
            rethrow(failure);
        }
    }

    @Override
    public void close() {
        if (lifecycleClosing) {
            return;
        }
        if (!open && !poisoned) {
            throw new IllegalStateException("Entity agent is already closed");
        }
        lifecycleClosing = true;
        Throwable failure = null;
        try {
            if (persistenceUnitListeners != null && !lifecycleCloseNotified) {
                lifecycleCloseNotified = true;
                failure = persistenceUnitListeners.preClose(EntityAgent.class, this);
            }
            open = false;
            for (QueryImpl query : new ArrayList<>(queries)) {
                try {
                    query.releaseResources();
                    queries.remove(query);
                } catch (RuntimeException | Error cleanup) {
                    if (failure == null) { failure = cleanup; } else { suppress(failure, cleanup); }
                }
            }
            if (joinedTransaction == null && !disposed) {
                boolean transactionClean = false;
                try {
                    unwind(0);
                    discardCacheChanges();
                    transactionClean = true;
                } catch (RuntimeException | Error cleanup) {
                    if (failure == null) { failure = cleanup; } else { suppress(failure, cleanup); }
                }
                try {
                    session.getCommitManager().reinitialize();
                    if (transactionClean) {
                        session.release();
                        disposed = depth() == 0;
                        transaction.active = false;
                    }
                } catch (RuntimeException | Error cleanup) {
                    if (failure == null) { failure = cleanup; } else { suppress(failure, cleanup); }
                }
            }
            failure = cleanup(failure, this::releaseLifecycleOwnership);
            if (failure != null) {
                poisoned = true;
                if (failure instanceof Error error) { throw error; }
                throw (RuntimeException) failure;
            }
            poisoned = false;
        } finally {
            lifecycleClosing = false;
        }
    }

    private final class AgentTransaction implements EntityTransaction {
        private boolean active;
        private boolean rollbackOnly;
        private Integer timeout;

        @Override
        public void begin() {
            verifyOpen();
            if (active) { throw new IllegalStateException("Transaction is already active"); }
            operation(false, () -> { session.beginTransaction(); return null; });
            active = true;
            rollbackOnly = false;
        }

        @Override
        public void commit() {
            requireActive();
            if (rollbackOnly) {
                rollback();
                throw new RollbackException("Transaction was marked for rollback");
            }
            boolean committing = false;
            try {
                releaseQueryResources();
                validateReadLocks();
                committing = true;
                session.commitTransaction();
                completeCacheChanges();
            } catch (RuntimeException | Error failure) {
                poisoned |= committing;
                discardCacheChanges();
                try { unwind(0); } catch (RuntimeException | Error cleanup) { poisoned = true; suppress(failure, cleanup); }
                if (failure instanceof Error error) { throw error; }
                throw new RollbackException("Could not commit entity agent transaction", failure);
            } finally {
                active = false;
                rollbackOnly = false;
                readLocks.clear();
                session.getCommitManager().reinitialize();
            }
        }

        @Override
        public void rollback() {
            requireActive();
            Throwable failure = cleanup(null, EntityAgentImpl.this::releaseQueryResources);
            failure = cleanup(failure, () -> unwind(0));
            active = false;
            rollbackOnly = false;
            readLocks.clear();
            discardCacheChanges();
            failure = cleanup(failure, () -> session.getCommitManager().reinitialize());
            if (failure != null) {
                poisoned = true;
                rethrow(failure);
            }
        }

        private void requireActive() {
            verifyOpen();
            if (!active) { throw new IllegalStateException("Transaction is not active"); }
        }

        @Override
        public void setRollbackOnly() { requireActive(); rollbackOnly = true; }
        @Override
        public boolean getRollbackOnly() { requireActive(); return rollbackOnly; }
        @Override
        public boolean isActive() { verifyOpen(); return active; }
        @Override
        public void setTimeout(Integer timeout) {
            verifyOpen();
            if (timeout != null && timeout < 0) { throw new IllegalArgumentException("Negative transaction timeout"); }
            this.timeout = timeout;
        }
        @Override
        public Integer getTimeout() { verifyOpen(); return timeout; }
    }

    @Override
    public StatementOrTypedQuery createQuery(String text) {
        verifyOpen();
        try {
            return new EJBQueryImpl<>(Objects.requireNonNull(text), this);
        } catch (org.eclipse.persistence.exceptions.JPQLException failure) {
            throw new IllegalArgumentException(failure.getMessage(), failure);
        }
    }

    @Override
    public <T> TypedQuery<T> createQuery(String text, Class<T> type) {
        verifyOpen();
        try {
            return new EJBQueryImpl<>(Objects.requireNonNull(text), this, Objects.requireNonNull(type));
        } catch (org.eclipse.persistence.exceptions.JPQLException failure) {
            throw new IllegalArgumentException(failure.getMessage(), failure);
        }
    }

    @Override
    public <T> TypedQuery<T> createQuery(String text, EntityGraph<T> graph) {
        graphType(graph);
        return createQuery(text).withEntityGraph(graph);
    }

    @Override
    public <T> TypedQuery<T> createQuery(CriteriaSelect<T> criteria) {
        verifyOpen();
        if (!(criteria instanceof CommonAbstractCriteriaImpl<?> implementation)) {
            throw new IllegalArgumentException("Criteria query was not created by this provider");
        }
        return new EJBQueryImpl<>(implementation.translate(), this);
    }

    @Override
    public Statement createStatement(String text) { return createQuery(text).asStatement(); }

    @Override
    public Statement createStatement(CriteriaStatement<?> criteria) {
        verifyOpen();
        if (!(criteria instanceof CommonAbstractCriteriaImpl<?> implementation)) {
            throw new IllegalArgumentException("Criteria statement was not created by this provider");
        }
        return new StatementImpl(implementation.translate(), this);
    }

    @Override
    public StatementOrTypedQuery createNamedQuery(String name) {
        verifyOpen();
        return new EJBQueryImpl<>(Objects.requireNonNull(name), this, true);
    }

    @Override
    public <T> TypedQuery<T> createNamedQuery(String name, Class<T> type) {
        verifyOpen();
        return new EJBQueryImpl<>(Objects.requireNonNull(name), this, true, Objects.requireNonNull(type));
    }

    @Override
    public Statement createNamedStatement(String name) { return createNamedQuery(name).asStatement(); }

    @Override
    public Statement createStatement(StatementReference reference) {
        verifyOpen();
        Objects.requireNonNull(reference);
        DatabaseQuery template = AugmentedQueryReference.executable(reference, factory.getServerSession());
        Statement statement;
        if (template == null) {
            statement = createNamedStatement(reference.getName());
        } else {
            EJBQueryImpl<?> executable = new EJBQueryImpl<>(template, this);
            executable.initializeFromDatabaseQuery();
            statement = executable.asStatement();
        }
        ReferenceBinder.bind(statement, reference);
        if (reference.getOptions() != null) {
            reference.getOptions().forEach(statement::addOption);
        }
        return statement;
    }

    @Override
    public <T> TypedQuery<T> createQuery(TypedQueryReference<T> reference) {
        verifyOpen();
        Objects.requireNonNull(reference);
        @SuppressWarnings("unchecked") Class<T> type = (Class<T>) reference.getResultType();
        DatabaseQuery template = AugmentedQueryReference.executable(reference, factory.getServerSession());
        TypedQuery<T> query;
        if (template == null) {
            @SuppressWarnings("unchecked")
            TypedQuery<T> named = type == null ? (TypedQuery<T>) createNamedQuery(reference.getName())
                    : createNamedQuery(reference.getName(), type);
            query = named;
        } else {
            EJBQueryImpl<T> executable = new EJBQueryImpl<>(template, this);
            executable.initializeFromDatabaseQuery();
            query = type == null ? executable : executable.ofType(type);
        }
        if (reference.getEntityGraphName() != null) {
            @SuppressWarnings("unchecked")
            EntityGraph<T> graph = type == null ? (EntityGraph<T>) getEntityGraph(reference.getEntityGraphName())
                    : getEntityGraph(type, reference.getEntityGraphName());
            query.setHint(QueryHints.JPA_LOAD_GRAPH, graph);
        }
        ReferenceBinder.bind(query, reference);
        if (reference.getOptions() != null) {
            reference.getOptions().forEach(query::addOption);
        }
        return query;
    }

    @Override
    public StatementOrTypedQuery createNativeQuery(String text) {
        verifyOpen();
        return new EJBQueryImpl<>(EJBQueryImpl.buildSQLDatabaseQuery(Objects.requireNonNull(text), session.getLoader(), session), this);
    }

    @Override
    public <T> TypedQuery<T> createNativeQuery(String text, Class<T> type) {
        verifyOpen();
        return new EJBQueryImpl<>(EJBQueryImpl.buildSQLDatabaseQuery(Objects.requireNonNull(type),
                Objects.requireNonNull(text), session.getLoader(), session), this);
    }

    @Override
    public StatementOrTypedQuery createNativeQuery(String text, String mapping) {
        verifyOpen();
        ResultSetMappingQuery query = new ResultSetMappingQuery();
        query.setSQLResultSetMappingName(Objects.requireNonNull(mapping));
        query.setSQLString(Objects.requireNonNull(text));
        query.setIsUserDefined(true);
        return new EJBQueryImpl<>(query, this);
    }

    @Override
    public <T> TypedQuery<T> createNativeQuery(String text, ResultSetMapping<T> mapping) {
        return createNativeQuery(text).withResultSetMapping(Objects.requireNonNull(mapping));
    }

    @Override
    public Statement createNativeStatement(String text) { return createNativeQuery(text).asStatement(); }

    @Override
    public StoredProcedureQuery createNamedStoredProcedureQuery(String name) {
        verifyOpen();
        return new StoredProcedureQueryImpl(Objects.requireNonNull(name), this);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String name) {
        return createStoredProcedureQuery(name, new Class<?>[0]);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String name, Class<?>... types) {
        verifyOpen();
        StoredProcedureCall call = new StoredProcedureCall();
        call.setProcedureName(Objects.requireNonNull(name));
        call.setHasMultipleResultSets(types.length > 1);
        List<SQLResultSetMapping> mappings = new ArrayList<>();
        for (Class<?> type : types) {
            mappings.add(ResultSetMappingTranslator.forResultClass(Objects.requireNonNull(type), this));
        }
        return new StoredProcedureQueryImpl(StoredProcedureQueryImpl.buildResultSetMappingQuery(mappings, call), this);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String name, String... mappings) {
        verifyOpen();
        StoredProcedureCall call = new StoredProcedureCall();
        call.setProcedureName(Objects.requireNonNull(name));
        call.setHasMultipleResultSets(mappings.length > 1);
        return new StoredProcedureQueryImpl(StoredProcedureQueryImpl.buildResultSetMappingNameQuery(List.of(mappings), call), this);
    }
}
