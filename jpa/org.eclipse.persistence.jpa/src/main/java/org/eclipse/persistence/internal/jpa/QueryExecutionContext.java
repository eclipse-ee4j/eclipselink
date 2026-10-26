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
import java.util.function.Supplier;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DatabaseQuery;

/**
 * The owner services used by query objects. An entity agent supplies its own
 * execution and resource boundary without creating a persistence context.
 */
interface QueryExecutionContext {
    void verifyOpen();

    void verifyOpenWithSetRollbackOnly();

    AbstractSession getSession();

    AbstractSession getExecutionSession(DatabaseQuery query);

    boolean hasActiveTransaction();

    void requireTransaction();

    FlushModeType getFlushMode();

    boolean shouldFlush();

    void flush();

    void setRollbackOnly();

    Map<String, Object> getProperties();

    void registerQuery(QueryImpl query);

    void unregisterQuery(QueryImpl query);

    boolean isEntityAgent();

    void lockResult(Object entity, LockModeType lockMode);

    default Object prepareResult(Object result) {
        return result;
    }

    default Object materializeCursorRow(DatabaseQuery query, Supplier<Object> build, LockModeType lockMode) {
        return build.get();
    }

    Object executeQuery(DatabaseQuery query, List<Object> parameters, LockModeType lockMode);
}
