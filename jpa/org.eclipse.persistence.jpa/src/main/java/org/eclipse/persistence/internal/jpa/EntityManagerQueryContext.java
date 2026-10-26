/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
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

import jakarta.persistence.FlushModeType;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Map;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ResultSetMappingQuery;
import org.eclipse.persistence.queries.UpdateAllQuery;

/** Preserves the EntityManager policy behind the shared query machinery. */
final class EntityManagerQueryContext implements QueryExecutionContext {
    static final String NATIVE_STATEMENT_PROPERTY = "eclipselink.statement.native-cache-invalidation";
    private final EntityManagerImpl entityManager;

    EntityManagerQueryContext(EntityManagerImpl entityManager) {
        this.entityManager = entityManager;
    }

    EntityManagerImpl getEntityManager() {
        return entityManager;
    }

    @Override
    public void verifyOpen() {
        entityManager.verifyOpen();
    }

    @Override
    public void verifyOpenWithSetRollbackOnly() {
        entityManager.verifyOpenWithSetRollbackOnly();
    }

    @Override
    public AbstractSession getSession() {
        return entityManager.getActiveSessionIfExists();
    }

    @Override
    public AbstractSession getExecutionSession(DatabaseQuery query) {
        if (query.isObjectLevelReadQuery() && ((ObjectLevelReadQuery) query).isReadOnly()) {
            return (AbstractSession) entityManager.getReadOnlySession();
        }
        return (AbstractSession) entityManager.getActiveSession();
    }

    @Override
    public boolean hasActiveTransaction() {
        return entityManager.checkForTransaction(false) != null;
    }

    @Override
    public void requireTransaction() {
        entityManager.checkForTransaction(true);
    }

    @Override
    public FlushModeType getFlushMode() {
        return entityManager.getFlushMode();
    }

    @Override
    public boolean shouldFlush() {
        return entityManager.shouldFlushBeforeQuery();
    }

    @Override
    public void flush() {
        entityManager.flush();
    }

    @Override
    public void setRollbackOnly() {
        entityManager.setRollbackOnly();
    }

    @Override
    public Map<String, Object> getProperties() {
        return entityManager.properties;
    }

    @Override
    public void registerQuery(QueryImpl query) {
        entityManager.addOpenQuery(query);
    }

    @Override
    public void unregisterQuery(QueryImpl query) {
        // EntityManager transaction completion owns its existing open-query map.
    }

    @Override
    public boolean isEntityAgent() {
        return false;
    }

    @Override
    public void lockResult(Object entity, LockModeType lockMode) {
        entityManager.lock(entity, lockMode);
    }

    @Override
    public Object executeQuery(DatabaseQuery query, List<Object> parameters, LockModeType lockMode) {
        if (query instanceof ResultSetMappingQuery) {
            query = (DatabaseQuery) query.clone();
            query.setProperty(ResultSetMappingTranslator.EXECUTION_CONTEXT_PROPERTY, this);
        }
        AbstractSession session = getExecutionSession(query);
        Object result = session.executeQuery(query, parameters);
        if (query.isDataModifyQuery() && Boolean.TRUE.equals(query.getProperty(NATIVE_STATEMENT_PROPERTY))
                && !query.shouldStoreBypassCache() && result instanceof Number count && count.longValue() > 0
                && session instanceof UnitOfWorkImpl unitOfWork) {
            unitOfWork.storeModifyAllQuery(new NativeStatementCacheInvalidation());
        }
        return result;
    }

    /**
     * Native SQL has no known affected entity types. Use the existing transaction's
     * completion list to invalidate the shared cache only after a successful commit.
     * This completion entry never executes SQL and is discarded on rollback.
     */
    private static final class NativeStatementCacheInvalidation extends UpdateAllQuery {
        @Override
        public void mergeChangesIntoSharedCache() {
            getSession().getIdentityMapAccessor().invalidateAll();
            getSession().getIdentityMapAccessor().clearQueryCache();
        }
    }
}
