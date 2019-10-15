/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.models.jpa.partitioned;

import java.util.List;

import org.eclipse.persistence.descriptors.partitioning.ReplicationPartitioningPolicy;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DatabaseQuery;

/**
 * Used to test a custom policy.
 */
public class EmployeePartitioningPolicy extends ReplicationPartitioningPolicy {
    protected boolean replicate = true;

    public EmployeePartitioningPolicy() {
        addConnectionPool("default");
        addConnectionPool("node2");
        addConnectionPool("node3");
    }

    /**
     * Allow replicate to be disabled on RAC.
     */
    public List<Accessor> getConnectionsForQuery(AbstractSession session, DatabaseQuery query, AbstractRecord arguments) {
        if (!this.replicate) {
            return null;
        }
        return super.getConnectionsForQuery(session, query, arguments);
    }

    public boolean getReplicate() {
        return replicate;
    }

    public void setReplicate(boolean replicate) {
        this.replicate = replicate;
    }
}
