/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 * Copyright (c) 2021, 2023 Oracle and/or its affiliates. All rights reserved.
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
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.FindOption;
import jakarta.persistence.LockModeType;
import jakarta.persistence.Statement;
import jakarta.persistence.StatementOrTypedQuery;
import jakarta.persistence.StatementReference;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.TypedQueryReference;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaSelect;
import jakarta.persistence.criteria.CriteriaStatement;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.persistence.sql.ResultSetMapping;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An {@link EntityAgent} backed by an {@link EntityManager}.
 * <p>
 * An entity agent has no persistence context and works only with detached instances: every operation
 * hits the database immediately, and changes to the instances handed back are never detected
 * automatically - {@link #update} is explicit. The agent is nevertheless built on an entity manager,
 * because that is where EclipseLink's query machinery, transaction handling and metadata live. The
 * persistence context that comes with it is an implementation detail this class is responsible for
 * keeping invisible, by detaching what it returns.
 * <p>
 * The agent owns the entity manager it is given and closes it in {@link #close()}, so an entity
 * manager must not be shared between an agent and its creator.
 */
public class EntityAgentImpl implements EntityAgent {

    private final EntityManager entityManager;
    private Map<?, ?> properties;

    /**
     * @param entityManager the entity manager this agent works through, and closes when it is closed
     */
    EntityAgentImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public <T> T get(Class<T> entityClass, Object id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> T get(Class<T> entityClass, Object id, FindOption... options) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> T get(EntityGraph<T> graph, Object id, FindOption... options) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> List<T> getMultiple(Class<T> entityClass, List<?> ids, FindOption... options) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> List<T> getMultiple(EntityGraph<T> graph, List<?> ids, FindOption... options) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> T find(Class<T> entityClass, Object id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> T find(Class<T> entityClass, Object id, FindOption... options) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> T find(EntityGraph<T> graph, Object id, FindOption... options) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> List<T> findMultiple(Class<T> entityClass, List<?> ids, FindOption... options) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> List<T> findMultiple(EntityGraph<T> graph, List<?> ids, FindOption... options) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setCacheRetrieveMode(CacheRetrieveMode cacheRetrieveMode) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setCacheStoreMode(CacheStoreMode cacheStoreMode) {
        // TODO Auto-generated method stub

    }

    @Override
    public CacheRetrieveMode getCacheRetrieveMode() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CacheStoreMode getCacheStoreMode() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setProperty(String propertyName, Object value) {
        // TODO Auto-generated method stub

    }

    @Override
    public Map<String, Object> getProperties() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Statement createStatement(String qlString) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StatementOrTypedQuery createQuery(String qlString) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> TypedQuery<T> createQuery(CriteriaSelect<T> selectQuery) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Statement createStatement(CriteriaStatement<?> statement) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> TypedQuery<T> createQuery(String qlString, EntityGraph<T> resultGraph) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Statement createNamedStatement(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StatementOrTypedQuery createNamedQuery(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Statement createStatement(StatementReference reference) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> TypedQuery<T> createQuery(TypedQueryReference<T> reference) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Statement createNativeStatement(String sqlString) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StatementOrTypedQuery createNativeQuery(String sqlString) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> TypedQuery<T> createNativeQuery(String sqlString, Class<T> resultClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StatementOrTypedQuery createNativeQuery(String sqlString, String resultSetMapping) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> TypedQuery<T> createNativeQuery(String sqlString, ResultSetMapping<T> resultSetMapping) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StoredProcedureQuery createNamedStoredProcedureQuery(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName, Class<?>... resultClasses) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName, String... resultSetMappings) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> type) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void close() {
        entityManager.close();
    }

    @Override
    public boolean isOpen() {
        return entityManager.isOpen();
    }

    /**
     * The transaction of the underlying entity manager.
     * <p>
     * An agent has no transaction of its own to expose. It reads and writes through its entity
     * manager, so the transaction that governs its work is that entity manager's, and handing back
     * anything else would let a caller commit one transaction while the agent works in another.
     */
    @Override
    public EntityTransaction getTransaction() {
        return entityManager.getTransaction();
    }

    @Override
    public EntityManagerFactory getEntityManagerFactory() {
        return entityManager.getEntityManagerFactory();
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        return entityManager.getCriteriaBuilder();
    }

    @Override
    public Metamodel getMetamodel() {
        return entityManager.getMetamodel();
    }

    @Override
    public <T> EntityGraph<T> createEntityGraph(Class<T> rootType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public EntityGraph<?> getEntityGraph(String graphName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> EntityGraph<T> getEntityGraph(Class<T> rootType, String graphName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> entityClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <C> void runWithConnection(ConnectionConsumer<C> action) {
        // TODO Auto-generated method stub

    }

    @Override
    public <C, T> T callWithConnection(ConnectionFunction<C, T> function) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void insert(Object entity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void insertMultiple(List<?> entities) {
        // TODO Auto-generated method stub

    }

    @Override
    public void update(Object entity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateMultiple(List<?> entities) {
        // TODO Auto-generated method stub

    }

    @Override
    public void delete(Object entity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteMultiple(List<?> entities) {
        // TODO Auto-generated method stub

    }

    @Override
    public void upsert(Object entity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void upsertMultiple(List<?> entities) {
        // TODO Auto-generated method stub

    }

    @Override
    public void refresh(Object entity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void refreshMultiple(List<?> entities) {
        // TODO Auto-generated method stub

    }

    @Override
    public void refresh(Object entity, LockModeType lockMode) {
        // TODO Auto-generated method stub

    }

    @Override
    public <T> T fetch(T association) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addOption(Option option) {
        // TODO Auto-generated method stub

    }

    @Override
    public Set<Option> getOptions() {
        // TODO Auto-generated method stub
        return null;
    }

}
