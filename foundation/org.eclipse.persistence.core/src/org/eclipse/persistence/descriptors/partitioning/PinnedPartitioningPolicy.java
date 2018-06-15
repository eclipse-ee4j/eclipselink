/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     James Sutherland (Oracle) - initial API and implementation
package org.eclipse.persistence.descriptors.partitioning;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DatabaseQuery;

/**
 * PUBLIC:
 * PinnedPartitionPolicy pins requests to a single connection pool.
 * @author James Sutherland
 * @since EclipseLink 2.2
 */
public class PinnedPartitioningPolicy extends PartitioningPolicy {

    protected String connectionPool;

    public PinnedPartitioningPolicy() {
        super();
    }

    public PinnedPartitioningPolicy(String pool) {
        this();
        this.connectionPool = pool;
    }

    /**
     * PUBLIC:
     * Return the connection pool to pin queries to.
     */
    public String getConnectionPool() {
        return connectionPool;
    }

    /**
     * PUBLIC:
     * Set the connection pool to pin queries to.
     */
    public void setConnectionPool(String connectionPool) {
        this.connectionPool = connectionPool;
    }

    /**
     * INTERNAL:
     * Get a connection from each pool.
     */
    @Override
    public List<Accessor> getConnectionsForQuery(AbstractSession session, DatabaseQuery query, AbstractRecord arguments) {
        if (session.getPlatform().hasPartitioningCallback()) {
            // UCP support.
            session.getPlatform().getPartitioningCallback().setPartitionId(this.connectionPool.hashCode());
            return null;
        }
        List<Accessor> accessors = new ArrayList<Accessor>(1);
        accessors.add(getAccessor(this.connectionPool, session, query, false));
        return accessors;
    }

}
