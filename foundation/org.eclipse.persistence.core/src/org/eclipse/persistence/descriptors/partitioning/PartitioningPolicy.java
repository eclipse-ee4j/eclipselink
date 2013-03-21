/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James Sutherland (Oracle) - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.descriptors.partitioning;

import java.util.List;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.sessions.server.ClientSession;
import org.eclipse.persistence.sessions.server.ConnectionPool;
import org.eclipse.persistence.sessions.server.ServerSession;

/**
 * PUBLIC:
 * A PartitioningPolicy is used to partition the data for a class across multiple difference databases
 * or across a database cluster such as Oracle RAC.
 * Partitioning can provide improved scalability by allowing multiple database machines to service requests.
 * <p>
 * If multiple partitions are used to process a single transaction, JTA should be used for proper XA transaction support.
 * @author James Sutherland
 * @since EclipseLink 2.2
 */
public abstract class PartitioningPolicy implements java.io.Serializable {
    /** The persistent unit unique name for the policy. */
    protected String name;

    public abstract List<Accessor> getConnectionsForQuery(AbstractSession session, DatabaseQuery query, AbstractRecord arguments);

    /**
     * INTERNAL:
     * Allow for the persist call to assign the partition.
     */
    public void partitionPersist(AbstractSession session, Object object, ClassDescriptor descriptor) { }
    
    /**
     * INTERNAL:
     * Initialize the policy.
     */
    public void initialize(AbstractSession session) { }
    
    /**
     * INTERNAL:
     * Convert all the class-name-based settings to actual class-based
     * settings. This method is used when converting a project that has been built
     * with class names to a project with classes.
     */
    public void convertClassNamesToClasses(ClassLoader classLoader) { }
    
    /**
     * INTERNAL:
     * Return an accessor from the pool.
     */
    public Accessor acquireAccessor(String poolName, ServerSession session, DatabaseQuery query, boolean returnNullIfDead) {
        ConnectionPool pool = session.getConnectionPool(poolName);
        if (pool == null) {
            throw QueryException.missingConnectionPool(poolName, query);
        }
        if (returnNullIfDead && pool.isDead()) {
            return null;
        }
        return pool.acquireConnection();
    }
    
    /**
     * INTERNAL:
     * Return an accessor from the pool for the session.
     * For a client session the accessor is stored for the duration of the transaction.
     */
    public Accessor getAccessor(String poolName, AbstractSession session, DatabaseQuery query, boolean returnNullIfDead) {
        Accessor accessor = null;
        if (session.isClientSession()) {
            ClientSession client = (ClientSession)session;
            // If the client session is exclusive and has a connection then just use the existing connection.
            if (client.isExclusiveIsolatedClientSession() && client.hasWriteConnection()) {
                accessor = client.getWriteConnection();
            } else {
                accessor = client.getWriteConnections().get(poolName);
                if (accessor == null) {
                    accessor = acquireAccessor(poolName, client.getParent(), query, returnNullIfDead);
                    // Assign a write connection for the duration of the transaction.
                    if (client.isExclusiveIsolatedClientSession() || session.isInTransaction()) {
                        accessor = client.addWriteConnection(poolName, accessor);
                    }
                }
            }
        } else if (session.isServerSession()) {
            accessor = acquireAccessor(poolName, (ServerSession)session, query, returnNullIfDead);
        } else {
            throw QueryException.partitioningNotSupported(session, query);
        }
        return accessor;
    }

    /**
     * PUBLIC:
     * Return the name of the policy.
     * The name must be unique for the persistence unit.
     * The name allows the policy to be shared among multiple descriptors, queries.
     */
    public String getName() {
        return name;
    }

    /**
     * PUBLIC:
     * Set the name of the policy.
     * The name must be unique for the persistence unit.
     * The name allows the policy to be shared among multiple descriptors, queries.
     */
    public void setName(String name) {
        this.name = name;
    }

}
