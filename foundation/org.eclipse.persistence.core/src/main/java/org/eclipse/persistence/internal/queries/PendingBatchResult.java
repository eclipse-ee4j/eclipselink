/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
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
//     09/27/2026 - Andreas Lemmer
//       - 2885: build batch fetched objects on demand to avoid a StackOverflowError
package org.eclipse.persistence.internal.queries;

import java.util.List;

import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.ReadAllQuery;

/**
 * INTERNAL:
 * Rows selected by a batch fetch query whose objects have not been built yet.
 * <p>
 * Building every object of a batch as soon as it was selected made eager batch fetching
 * in both directions of a relationship recurse once per object: building the first object
 * of a batch fetched the next batch and built its first object, and so on, until the stack
 * overflowed. Objects are therefore only built when a source object asks for them.
 *
 * @see org.eclipse.persistence.mappings.ForeignReferenceMapping#extractResultFromBatchQuery
 */
public class PendingBatchResult {
    /** The executed query, it still holds the state (e.g. nested batch data results) needed to build the objects. */
    private final ReadAllQuery query;
    /** The session the query was executed on. */
    private final AbstractSession session;
    private final List<AbstractRecord> rows;
    private final CacheKey parentCacheKey;

    public PendingBatchResult(ReadAllQuery query, AbstractSession session, List<AbstractRecord> rows, CacheKey parentCacheKey) {
        this.query = query;
        this.session = session;
        this.rows = rows;
        this.parentCacheKey = parentCacheKey;
    }

    /**
     * Return a pending result for a subset of the rows, e.g. the rows of one source object.
     */
    public PendingBatchResult forRows(List<AbstractRecord> rows, CacheKey parentCacheKey) {
        return new PendingBatchResult(this.query, this.session, rows, parentCacheKey);
    }

    public ReadAllQuery getQuery() {
        return query;
    }

    public AbstractSession getSession() {
        return session;
    }

    public List<AbstractRecord> getRows() {
        return rows;
    }

    public CacheKey getParentCacheKey() {
        return parentCacheKey;
    }
}
