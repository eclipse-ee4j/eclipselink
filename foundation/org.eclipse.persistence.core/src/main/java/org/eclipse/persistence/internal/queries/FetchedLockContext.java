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
package org.eclipse.persistence.internal.queries;

import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.queries.ReadQuery;

/**
 * INTERNAL: Lifetime of relationship locks acquired while materializing a FETCHED read.
 * A retained token contains no session, query, or entity and becomes inert on return.
 */
public final class FetchedLockContext {
    private static final String PROPERTY = "eclipselink.fetched-lock-context";
    private static final ThreadLocal<Scope> CURRENT = new ThreadLocal<>();
    private short lockMode;
    private boolean active = true;

    private FetchedLockContext(short lockMode) {
        this.lockMode = lockMode;
    }

    public boolean isActive() {
        return active;
    }

    public short getLockMode() {
        return lockMode;
    }

    public static FetchedLockContext current() {
        Scope scope = CURRENT.get();
        return scope == null || scope.context == null || !scope.context.active ? null : scope.context;
    }

    public static FetchedLockContext get(DatabaseQuery query) {
        Object value = query.getProperty(PROPERTY);
        return value instanceof FetchedLockContext context && context.active ? context : null;
    }

    /** Only private execution/selection clones may receive an operation token. */
    public void attach(DatabaseQuery query) {
        query.setProperty(PROPERTY, this);
    }

    public static ReadQuery attachToSelection(ReadQuery selection, DatabaseQuery source) {
        FetchedLockContext context = current();
        if (context == null || !(source instanceof ObjectLevelReadQuery read)
                || !read.shouldLockFetchedRelationships() || !read.isLockQuery()) {
            return selection;
        }
        context.lockMode = read.getLockMode();
        ReadQuery clone = (ReadQuery) selection.clone();
        clone.setIsExecutionClone(true);
        context.attach(clone);
        return clone;
    }

    public static Scope open(DatabaseQuery query) {
        Scope previous = CURRENT.get();
        FetchedLockContext context = get(query);
        boolean owner = false;
        if (context == null && query instanceof ObjectLevelReadQuery read
                && read.shouldLockFetchedRelationships() && (read.isLockQuery()
                || read.getLockModeType() != null && read.getLockModeType().startsWith(ObjectLevelReadQuery.PESSIMISTIC_))) {
            if (previous != null && previous.query == query && previous.context != null
                    && previous.context.active) {
                context = previous.context;
            } else {
                context = new FetchedLockContext(read.isLockQuery() ? read.getLockMode() : ObjectBuildingQuery.LOCK);
                owner = true;
            }
        }
        Scope scope = new Scope(previous, query, context, owner);
        CURRENT.set(scope);
        return scope;
    }

    public static final class Scope implements AutoCloseable {
        private Scope previous;
        private DatabaseQuery query;
        private final FetchedLockContext context;
        private final boolean owner;
        private boolean closed;

        private Scope(Scope previous, DatabaseQuery query, FetchedLockContext context, boolean owner) {
            this.previous = previous;
            this.query = query;
            this.context = context;
            this.owner = owner;
        }

        public FetchedLockContext context() {
            return context;
        }

        public boolean ownsContext() {
            return owner;
        }

        @Override
        public void close() {
            if (!closed) {
                closed = true;
                if (owner) {
                    context.active = false;
                }
                if (previous == null) {
                    CURRENT.remove();
                } else {
                    CURRENT.set(previous);
                }
                previous = null;
                query = null;
            }
        }
    }
}
