/*******************************************************************************
 * Copyright (c) 2010, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James Sutherland - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.platform.database.oracle.ucp;

import java.sql.SQLException;

import javax.sql.DataSource;

import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.oracle.DataBasedConnectionAffinityCallback;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.platform.database.partitioning.DataPartitioningCallback;
import org.eclipse.persistence.sessions.Session;

/**
 * PUBLIC:
 * Integrates with Oracle Universal ConnectionPool's data affinity support.
 * 
 * @see org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy
 * @author James Sutherland
 * @since EclipseLink 2.3
 */
public class UCPDataPartitioningCallback implements DataPartitioningCallback, DataBasedConnectionAffinityCallback {
    /** The id is stored in a thread local. */
    protected ThreadLocal partitionId = new ThreadLocal();
    
    public void register(DataSource datSource, Session session) {
        try {
            ((PoolDataSource)datSource).registerConnectionAffinityCallback(this);
        } catch (SQLException exception) {
            throw DatabaseException.sqlException(exception, (AbstractSession)session, false);
        }
    }
    
    /**
     * Set the partition id for this thread.
     */
    public void setPartitionId(int id) {
        this.partitionId.set(id);
    }
    
    public int getPartitionId() {
        Integer id = (Integer)this.partitionId.get();
        if (id == null) {
            return 0;
        }
        return id;
    }
    
    public boolean setDataKey(Object key) {
        // Not used.
        return true;
    }
    
    public boolean setConnectionAffinityContext(Object context) {
        // Not used for data affinity.
        return false;
    }
    
    public void setAffinityPolicy(AffinityPolicy policy) {
        // Not used.        
    }
    
    public Object getConnectionAffinityContext() {
        // Not used for data affinity.
        return null;
    }
    
    public AffinityPolicy getAffinityPolicy() {
        return AffinityPolicy.DATA_BASED_AFFINITY;
    }
        
}
