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

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.CacheRetrieveMode;
import jakarta.persistence.CacheStoreMode;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.LockModeType;
import jakarta.persistence.Parameter;
import jakarta.persistence.QueryFlushMode;
import jakarta.persistence.Statement;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Timeout;
import jakarta.persistence.metamodel.Type;

import org.eclipse.persistence.queries.DatabaseQuery;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * <b>Purpose</b>: Implements the Jakarta Persistence {@link Statement} contract.
 * <p>
 * <b>Description</b>: Jakarta Persistence 4.0 splits update/delete execution out of
 * {@code Query} into {@link Statement}. {@code Statement} and {@code TypedQuery} both
 * declare {@code getOptions()} with mutually incompatible return types, so a single
 * class cannot implement both; this delegates to an {@link EJBQueryImpl} rather than
 * extending it, reusing the existing {@code DatabaseQuery} execution machinery.
 *
 * @see jakarta.persistence.Statement
 * @since EclipseLink 6.0
 */
public class StatementImpl implements Statement {

    private final EJBQueryImpl<?> query;

    public StatementImpl(DatabaseQuery databaseQuery, EntityManagerImpl entityManager) {
        this.query = new EJBQueryImpl<>(databaseQuery, entityManager);
    }

    /**
     * Returns the query this statement delegates to.
     */
    public EJBQueryImpl<?> getDelegate() {
        return query;
    }

    @Override
    public int execute() {
        return query.executeUpdate();
    }

    @Override
    public int executeUpdate() {
        return query.executeUpdate();
    }

    @Override
    public List getResultList() {
        return query.getResultList();
    }

    @Override
    public Object getSingleResult() {
        return query.getSingleResult();
    }

    @Override
    public Object getSingleResultOrNull() {
        return query.getSingleResultOrNull();
    }

    @Override
    public Statement setHint(String hintName, Object value) {
        query.setHint(hintName, value);
        return this;
    }

    @Override
    public Map<String, Object> getHints() {
        return query.getHints();
    }

    @Override
    public Statement setTimeout(Integer timeout) {
        query.setTimeout(timeout);
        return this;
    }

    @Override
    public Statement setTimeout(Timeout timeout) {
        query.setTimeout(timeout);
        return this;
    }

    @Override
    public Integer getTimeout() {
        return query.getTimeout();
    }

    @Override
    public Statement setQueryFlushMode(QueryFlushMode flushMode) {
        query.setQueryFlushMode(flushMode);
        return this;
    }

    @Override
    public QueryFlushMode getQueryFlushMode() {
        return query.getQueryFlushMode();
    }

    @Override
    public Statement setFlushMode(FlushModeType flushMode) {
        query.setFlushMode(flushMode);
        return this;
    }

    @Override
    public FlushModeType getFlushMode() {
        return query.getFlushMode();
    }

    @Override
    public Statement setMaxResults(int maxResult) {
        query.setMaxResults(maxResult);
        return this;
    }

    @Override
    public int getMaxResults() {
        return query.getMaxResults();
    }

    @Override
    public Statement setFirstResult(int startPosition) {
        query.setFirstResult(startPosition);
        return this;
    }

    @Override
    public int getFirstResult() {
        return query.getFirstResult();
    }

    @Override
    public Statement setLockMode(LockModeType lockMode) {
        query.setLockMode(lockMode);
        return this;
    }

    @Override
    public LockModeType getLockMode() {
        return query.getLockMode();
    }

    @Override
    public Statement setCacheRetrieveMode(CacheRetrieveMode cacheRetrieveMode) {
        query.setCacheRetrieveMode(cacheRetrieveMode);
        return this;
    }

    @Override
    public CacheRetrieveMode getCacheRetrieveMode() {
        return query.getCacheRetrieveMode();
    }

    @Override
    public Statement setCacheStoreMode(CacheStoreMode cacheStoreMode) {
        query.setCacheStoreMode(cacheStoreMode);
        return this;
    }

    @Override
    public CacheStoreMode getCacheStoreMode() {
        return query.getCacheStoreMode();
    }

    @Override
    public <T> Statement setParameter(Parameter<T> parameter, T value) {
        query.setParameter(parameter, value);
        return this;
    }

    @Override
    public Statement setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType) {
        query.setParameter(param, value, temporalType);
        return this;
    }

    @Override
    public Statement setParameter(Parameter<Date> param, Date value, TemporalType temporalType) {
        query.setParameter(param, value, temporalType);
        return this;
    }

    @Override
    public Statement setParameter(String name, Object value) {
        query.setParameter(name, value);
        return this;
    }

    @Override
    public <P> Statement setParameter(String name, P value, Class<P> type) {
        query.setParameter(name, value, type);
        return this;
    }

    @Override
    public <P> Statement setParameter(String name, P value, Type<P> type) {
        query.setParameter(name, value, type);
        return this;
    }

    @Override
    public <P> Statement setConvertedParameter(String name, P value, Class<? extends AttributeConverter<P, ?>> converter) {
        query.setConvertedParameter(name, value, converter);
        return this;
    }

    @Override
    public Statement setParameter(String name, Calendar value, TemporalType temporalType) {
        query.setParameter(name, value, temporalType);
        return this;
    }

    @Override
    public Statement setParameter(String name, Date value, TemporalType temporalType) {
        query.setParameter(name, value, temporalType);
        return this;
    }

    @Override
    public Statement setParameter(int position, Object value) {
        query.setParameter(position, value);
        return this;
    }

    @Override
    public <P> Statement setParameter(int position, P value, Class<P> type) {
        query.setParameter(position, value, type);
        return this;
    }

    @Override
    public <P> Statement setParameter(int position, P value, Type<P> type) {
        query.setParameter(position, value, type);
        return this;
    }

    @Override
    public <P> Statement setConvertedParameter(int position, P value, Class<? extends AttributeConverter<P, ?>> converter) {
        query.setConvertedParameter(position, value, converter);
        return this;
    }

    @Override
    public Statement setParameter(int position, Calendar value, TemporalType temporalType) {
        query.setParameter(position, value, temporalType);
        return this;
    }

    @Override
    public Statement setParameter(int position, Date value, TemporalType temporalType) {
        query.setParameter(position, value, temporalType);
        return this;
    }

    @Override
    public Statement setParameters(Object... arguments) {
        query.setParameters(arguments);
        return this;
    }

    @Override
    public Set<Parameter<?>> getParameters() {
        return query.getParameters();
    }

    @Override
    public Parameter<?> getParameter(String name) {
        return query.getParameter(name);
    }

    @Override
    public <T> Parameter<T> getParameter(String name, Class<T> type) {
        return query.getParameter(name, type);
    }

    @Override
    public Parameter<?> getParameter(int position) {
        return query.getParameter(position);
    }

    @Override
    public <T> Parameter<T> getParameter(int position, Class<T> type) {
        return query.getParameter(position, type);
    }

    @Override
    public boolean isBound(Parameter<?> parameter) {
        return query.isBound(parameter);
    }

    @Override
    public <T> T getParameterValue(Parameter<T> parameter) {
        return query.getParameterValue(parameter);
    }

    @Override
    public Object getParameterValue(String name) {
        return query.getParameterValue(name);
    }

    @Override
    public Object getParameterValue(int position) {
        return query.getParameterValue(position);
    }

    @Override
    public Statement addOption(Option option) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Set<Option> getOptions() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public <T> T unwrap(Class<T> type) {
        if (type != null && type.isInstance(this)) {
            return type.cast(this);
        }
        return query.unwrap(type);
    }
}
