/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Guy Pelletier - initial API and implementation
package org.eclipse.persistence.internal.jpa.config.classes;

import java.util.ArrayList;

import org.eclipse.persistence.internal.jpa.config.additionalcriteria.AdditionalCriteriaImpl;
import org.eclipse.persistence.internal.jpa.config.cache.CacheImpl;
import org.eclipse.persistence.internal.jpa.config.cache.CacheIndexImpl;
import org.eclipse.persistence.internal.jpa.config.cache.CacheInterceptorImpl;
import org.eclipse.persistence.internal.jpa.config.columns.PrimaryKeyImpl;
import org.eclipse.persistence.internal.jpa.config.listeners.EntityListenerImpl;
import org.eclipse.persistence.internal.jpa.config.locking.OptimisticLockingImpl;
import org.eclipse.persistence.internal.jpa.config.multitenant.MultitenantImpl;
import org.eclipse.persistence.internal.jpa.config.queries.FetchGroupImpl;
import org.eclipse.persistence.internal.jpa.config.queries.NamedNativeQueryImpl;
import org.eclipse.persistence.internal.jpa.config.queries.NamedPlsqlStoredFunctionQueryImpl;
import org.eclipse.persistence.internal.jpa.config.queries.NamedPlsqlStoredProcedureQueryImpl;
import org.eclipse.persistence.internal.jpa.config.queries.NamedQueryImpl;
import org.eclipse.persistence.internal.jpa.config.queries.NamedStoredFunctionQueryImpl;
import org.eclipse.persistence.internal.jpa.config.queries.NamedStoredProcedureQueryImpl;
import org.eclipse.persistence.internal.jpa.config.queries.QueryRedirectorsImpl;
import org.eclipse.persistence.internal.jpa.config.queries.SqlResultSetMappingImpl;
import org.eclipse.persistence.internal.jpa.config.sequencing.SequenceGeneratorImpl;
import org.eclipse.persistence.internal.jpa.config.sequencing.TableGeneratorImpl;
import org.eclipse.persistence.internal.jpa.config.sequencing.UuidGeneratorImpl;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.MappedSuperclassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.cache.CacheIndexMetadata;
import org.eclipse.persistence.internal.jpa.metadata.listeners.EntityListenerMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.FetchGroupMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedNativeQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedPLSQLStoredFunctionQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedPLSQLStoredProcedureQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedStoredFunctionQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedStoredProcedureQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.SQLResultSetMappingMetadata;
import org.eclipse.persistence.jpa.config.AdditionalCriteria;
import org.eclipse.persistence.jpa.config.Cache;
import org.eclipse.persistence.jpa.config.CacheIndex;
import org.eclipse.persistence.jpa.config.CacheInterceptor;
import org.eclipse.persistence.jpa.config.EntityListener;
import org.eclipse.persistence.jpa.config.FetchGroup;
import org.eclipse.persistence.jpa.config.Multitenant;
import org.eclipse.persistence.jpa.config.NamedNativeQuery;
import org.eclipse.persistence.jpa.config.NamedPlsqlStoredFunctionQuery;
import org.eclipse.persistence.jpa.config.NamedPlsqlStoredProcedureQuery;
import org.eclipse.persistence.jpa.config.NamedQuery;
import org.eclipse.persistence.jpa.config.NamedStoredFunctionQuery;
import org.eclipse.persistence.jpa.config.NamedStoredProcedureQuery;
import org.eclipse.persistence.jpa.config.OptimisticLocking;
import org.eclipse.persistence.jpa.config.PrimaryKey;
import org.eclipse.persistence.jpa.config.QueryRedirectors;
import org.eclipse.persistence.jpa.config.SequenceGenerator;
import org.eclipse.persistence.jpa.config.SqlResultSetMapping;
import org.eclipse.persistence.jpa.config.TableGenerator;
import org.eclipse.persistence.jpa.config.UuidGenerator;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
@SuppressWarnings("unchecked")
public abstract class AbstractMappedClassImpl<T extends MappedSuperclassAccessor, R> extends AbstractClassImpl<T, R> {

    public AbstractMappedClassImpl(T t) {
        super(t);

        getMetadata().setCacheIndexes(new ArrayList<CacheIndexMetadata>());
        getMetadata().setEntityListeners(new ArrayList<EntityListenerMetadata>());
        getMetadata().setFetchGroups(new ArrayList<FetchGroupMetadata>());
        getMetadata().setNamedNativeQueries(new ArrayList<NamedNativeQueryMetadata>());
        getMetadata().setNamedQueries(new ArrayList<NamedQueryMetadata>());
        getMetadata().setNamedStoredFunctionQueries(new ArrayList<NamedStoredFunctionQueryMetadata>());
        getMetadata().setNamedStoredProcedureQueries(new ArrayList<NamedStoredProcedureQueryMetadata>());
        getMetadata().setNamedPLSQLStoredFunctionQueries(new ArrayList<NamedPLSQLStoredFunctionQueryMetadata>());
        getMetadata().setNamedPLSQLStoredProcedureQueries(new ArrayList<NamedPLSQLStoredProcedureQueryMetadata>());
        getMetadata().setSqlResultSetMappings(new ArrayList<SQLResultSetMappingMetadata>());
    }

    public CacheIndex addCacheIndex() {
        CacheIndexImpl cacheIndex = new CacheIndexImpl();
        getMetadata().getCacheIndexes().add(cacheIndex.getMetadata());
        return cacheIndex;
    }

    public EntityListener addEntityListener() {
        EntityListenerImpl listener = new EntityListenerImpl();
        getMetadata().getEntityListeners().add(listener.getMetadata());
        return listener;
    }

    public FetchGroup addFetchGroup() {
        FetchGroupImpl fetchGroup = new FetchGroupImpl();
        getMetadata().getFetchGroups().add(fetchGroup.getMetadata());
        return fetchGroup;
    }

    public NamedNativeQuery addNamedNativeQuery() {
        NamedNativeQueryImpl query = new NamedNativeQueryImpl();
        getMetadata().getNamedNativeQueries().add(query.getMetadata());
        return query;
    }

    public NamedPlsqlStoredFunctionQuery addNamedPLSQLStoredFunctionQuery() {
        NamedPlsqlStoredFunctionQueryImpl query = new NamedPlsqlStoredFunctionQueryImpl();
        getMetadata().getNamedPLSQLStoredFunctionQueries().add(query.getMetadata());
        return query;
    }

    public NamedPlsqlStoredProcedureQuery addNamedPLSQLStoredProcedureQuery() {
        NamedPlsqlStoredProcedureQueryImpl query = new NamedPlsqlStoredProcedureQueryImpl();
        getMetadata().getNamedPLSQLStoredProcedureQueries().add(query.getMetadata());
        return query;
    }

    public NamedQuery addNamedQuery() {
        NamedQueryImpl namedQuery = new NamedQueryImpl();
        getMetadata().getNamedQueries().add(namedQuery.getMetadata());
        return namedQuery;
    }

    public NamedStoredFunctionQuery addNamedStoredFunctionQuery() {
        NamedStoredFunctionQueryImpl query = new NamedStoredFunctionQueryImpl();
        getMetadata().getNamedStoredFunctionQueries().add(query.getMetadata());
        return query;
    }

    public NamedStoredProcedureQuery addNamedStoredProcedureQuery() {
        NamedStoredProcedureQueryImpl query = new NamedStoredProcedureQueryImpl();
        getMetadata().getNamedStoredProcedureQueries().add(query.getMetadata());
        return query;
    }

    public SqlResultSetMapping addSqlResultSetMapping() {
        SqlResultSetMappingImpl sqlResultSetMapping = new SqlResultSetMappingImpl();
        getMetadata().getSqlResultSetMappings().add(sqlResultSetMapping.getMetadata());
        return sqlResultSetMapping;
    }

    public AdditionalCriteria setAdditionalCriteria() {
        AdditionalCriteriaImpl additionalCriteria = new AdditionalCriteriaImpl();
        getMetadata().setAdditionalCriteria(additionalCriteria.getMetadata());
        return additionalCriteria;
    }

    public Cache setCache() {
        CacheImpl cache = new CacheImpl();
        getMetadata().setCache(cache.getMetadata());
        return cache;
    }

    public R setCacheable(Boolean cacheable) {
        getMetadata().setCacheable(cacheable);
        return (R) this;
    }

    public CacheInterceptor setCacheInterceptor() {
        CacheInterceptorImpl cacheInterceptor = new CacheInterceptorImpl();
        getMetadata().setCacheInterceptor(cacheInterceptor.getMetadata());
        return cacheInterceptor;
    }

    public R setExcludeDefaultListeners(Boolean excludeDefaultListeners) {
        getMetadata().setExcludeDefaultListeners(excludeDefaultListeners);
        return (R) this;
    }

    public R setExcludeSuperclassListeners(Boolean excludeSuperclassListeners) {
        getMetadata().setExcludeSuperclassListeners(excludeSuperclassListeners);
        return (R) this;
    }

    public R setExistenceChecking(String existenceChecking) {
        getMetadata().setExistenceChecking(existenceChecking);
        return (R) this;
    }

    public R setIdClass(String idClass) {
        getMetadata().setIdClassName(idClass);
        return (R) this;
    }

    public Multitenant setMultitenant() {
        MultitenantImpl multitenant = new MultitenantImpl();
        getMetadata().setMultitenant(multitenant.getMetadata());
        return multitenant;
    }

    public OptimisticLocking setOptimisticLocking() {
        OptimisticLockingImpl optimisticLocking = new OptimisticLockingImpl();
        getMetadata().setOptimisticLocking(optimisticLocking.getMetadata());
        return optimisticLocking;
    }

    public R setPostLoad(String methodName) {
        getMetadata().setPostLoad(methodName);
        return (R) this;
    }

    public R setPostPersist(String methodName) {
        getMetadata().setPostPersist(methodName);
        return (R) this;
    }

    public R setPostRemove(String methodName) {
        getMetadata().setPostRemove(methodName);
        return (R) this;
    }

    public R setPostUpdate(String methodName) {
        getMetadata().setPostUpdate(methodName);
        return (R) this;
    }

    public R setPrePersist(String methodName) {
        getMetadata().setPrePersist(methodName);
        return (R) this;
    }

    public R setPreRemove(String methodName) {
        getMetadata().setPreRemove(methodName);
        return (R) this;
    }

    public R setPreUpdate(String methodName) {
        getMetadata().setPreUpdate(methodName);
        return (R) this;
    }

    public PrimaryKey setPrimaryKey() {
        PrimaryKeyImpl primaryKey = new PrimaryKeyImpl();
        getMetadata().setPrimaryKey(primaryKey.getMetadata());
        return primaryKey;
    }

    public QueryRedirectors setQueryRedirectors() {
        QueryRedirectorsImpl queryRedirectors = new QueryRedirectorsImpl();
        getMetadata().setQueryRedirectors(queryRedirectors.getMetadata());
        return queryRedirectors;
    }

    public R setReadOnly(Boolean readOnly) {
        getMetadata().setReadOnly(readOnly);
        return (R) this;
    }

    public SequenceGenerator setSequenceGenerator() {
        SequenceGeneratorImpl sequenceGenerator = new SequenceGeneratorImpl();
        getMetadata().setSequenceGenerator(sequenceGenerator.getMetadata());
        return sequenceGenerator;
    }

    public TableGenerator setTableGenerator() {
        TableGeneratorImpl tableGenerator = new TableGeneratorImpl();
        getMetadata().setTableGenerator(tableGenerator.getMetadata());
        return tableGenerator;
    }

    public UuidGenerator setUuidGenerator() {
        UuidGeneratorImpl uuidGenerator = new UuidGeneratorImpl();
        getMetadata().setUuidGenerator(uuidGenerator.getMetadata());
        return uuidGenerator;
    }
}
