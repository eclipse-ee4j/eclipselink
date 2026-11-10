/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Parameter;
import jakarta.persistence.QueryFlushMode;
import jakarta.persistence.StatementOrTypedQuery;
import jakarta.persistence.Timeout;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.metamodel.Type;

import java.util.Collection;

import org.eclipse.persistence.queries.Cursor;
import org.eclipse.persistence.queries.DatabaseQuery;

/**
 * PUBLIC:
 * EclipseLInk specific JPA query interface.  Provides the functionality defined in
 * jakarta.persistence.Query and adds access to the underlying database query for EclipseLink specific
 * functionality.
 */
public interface JpaQuery<X> extends TypedQuery<X>, StatementOrTypedQuery {

    @Nonnull
    @Override
    JpaQuery<X> setHint(@Nonnull String hintName, @Nullable Object value);

    @Nonnull
    @Override
    <T> JpaQuery<X> setParameter(@Nonnull Parameter<T> parameter, @Nullable T value);

    @Nonnull
    @Override
    JpaQuery<X> setParameter(@Nonnull String name, @Nullable Object value);

    @Nonnull
    @Override
    <P> JpaQuery<X> setParameter(@Nonnull String name, @Nullable P value, @Nonnull Class<P> type);

    @Nonnull
    @Override
    <P> JpaQuery<X> setParameter(@Nonnull String name, @Nullable P value, @Nonnull Type<P> type);

    @Nonnull
    @Override
    <P> JpaQuery<X> setConvertedParameter(@Nonnull String name, @Nullable P value,
                                                    @Nonnull Class<? extends AttributeConverter<P, ?>> converter);

    @Nonnull
    @Override
    JpaQuery<X> setParameter(int position, @Nullable Object value);

    @Nonnull
    @Override
    <P> JpaQuery<X> setParameter(int position, @Nullable P value, @Nonnull Class<P> type);

    @Nonnull
    @Override
    <P> JpaQuery<X> setParameter(int position, @Nullable P value, @Nonnull Type<P> type);

    @Nonnull
    @Override
    <P> JpaQuery<X> setConvertedParameter(int position, @Nullable P value,
                                                    @Nonnull Class<? extends AttributeConverter<P, ?>> converter);

    @Nonnull
    @Override
    JpaQuery<X> setParameters(@Nonnull Object... arguments);

    @Nonnull
    @Override
    JpaQuery<X> setQueryFlushMode(@Nonnull QueryFlushMode flushMode);

    @Nonnull
    @Override
    JpaQuery<X> setTimeout(@Nullable Integer timeout);

    @Nonnull
    @Override
    JpaQuery<X> setTimeout(@Nullable Timeout timeout);


    /**
     * PUBLIC:
     * Return the cached database query for this query.  If the query is
     * a named query and it has not yet been looked up, the query will be looked up
     * and stored as the cached query.
     */
    DatabaseQuery getDatabaseQuery();

    /**
     * PUBLIC:
     * return the EntityManager for this query
     */
    JpaEntityManager getEntityManager();

    /**
     * PUBLIC:
     * Non-standard method to return results of a ReadQuery that has a containerPolicy
     * that returns objects as a collection rather than a List
     * @return Collection of results
     */
    Collection getResultCollection();

    /**
     * PUBLIC:
     * Non-standard method to return results of a ReadQuery that uses a Cursor.
     * @return Cursor on results, either a CursoredStream, or ScrollableCursor
     */
    Cursor getResultCursor();

    /**
     * PUBLIC:
     * Replace the cached query with the given query.
     */
    void setDatabaseQuery(DatabaseQuery query);

}
