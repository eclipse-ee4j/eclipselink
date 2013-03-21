/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.databaseaccess;

import java.io.Serializable;

import org.eclipse.persistence.descriptors.DescriptorQueryManager;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * INTERNAL:
 *    BatchWritingMechanism is a private interface, used by the DatabaseAccessor. it provides the required
 *  behavior for batching statements, for write.<p>
 *    There are currently two types of the Mechanism implemented, one to handle the tradition dynamic SQL
 *  batching and another to handle Parameterized SQL.  Depending on what is passed to these mechanisms
 *  they may decide to switch the current one out to the alternative type.<p>
 *    In bug# 214910 this interface was switched to an abstract class<p>
 *
 *    @since OracleAS TopLink 10<i>g</i> (9.0.4)
 */
public abstract class BatchWritingMechanism implements Cloneable, Serializable {

    /**
     * This member variable stores the reference to the DatabaseAccessor that is
     * using this Mechanism to handle the batch writing
     */
    protected DatabaseAccessor databaseAccessor;
	
    /**
     * INTERNAL:
     * This variable is used to temporarily cache the largest queryTimeout among 
     * a batch of queries for a particular mechanism.
     * The default is NoTimeout.
     */
    protected int queryTimeoutCache = DescriptorQueryManager.NoTimeout;
    
    //bug 4241441: used to keep track of the values returned from the driver via addBatch and executeStatment
    protected int executionCount;
    //bug 4241441: increments with each addBatch call.  Used to compare against value returned from driver for 
    //  optimistic locking
    protected int statementCount;
    
    /** Allow for the batch size to be set as many database have strict limits. **/
    protected int maxBatchSize;

    public int getMaxBatchSize() {
        return maxBatchSize;
    }

    public void setMaxBatchSize(int maxBatchSize) {
        this.maxBatchSize = maxBatchSize;
    }

    /**
     * INTERNAL:
     * This function caches the largest query timeout encountered within all the calls in this batch,
     * or uses the parent timeout if one of the calls references the parent.
     * @param session
     * @param dbCall
     */
    protected void cacheQueryTimeout(AbstractSession session, DatabaseCall dbCall) {
    	int callTimeout = dbCall.getQueryTimeout();
    	/*
    	 * Object queries that reference their parent will already be resolved .
    	 * Data queries with a parent reference will be ignored.
    	 * NoTimeout values will be ignored
    	 */
    	if(callTimeout == DescriptorQueryManager.DefaultTimeout || callTimeout == DescriptorQueryManager.NoTimeout) {
    		return;
    	} else {
    		// Cache the highest individual query timeout
    		if(callTimeout > queryTimeoutCache) {
    			queryTimeoutCache = callTimeout;
    		}
    	}
    }
    
    /**
     * INTERNAL:
     * Clear the cached timeout after the statement has been executed.
     */
    protected void clearCacheQueryTimeout() {
    	queryTimeoutCache = DescriptorQueryManager.NoTimeout;
    }
    
    /**
     * INTERNAL:
     * Sets the accessor that this mechanism will use
     */
    public void setAccessor(DatabaseAccessor accessor, AbstractSession session) {
        databaseAccessor = accessor;
    }
	

    /**
     * INTERNAL:
     * This method is called by the DatabaseAccessor to add this statement to the list of statements
     * being batched.  This call may result in the Mechanism executing the batched statements and
     * possibly, switching out the mechanisms
     */
    public abstract void appendCall(AbstractSession session, DatabaseCall call);

    /**
     * INTERNAL:
     * This method is used to clear the batched statements without the need to execute the statements first
     * This is used in the case of rollback.
     */
    public abstract void clear();

    /**
     * INTERNAL:
     * This method is used by the DatabaseAccessor to clear the batched statements in the
     * case that a non batchable statement is being execute
     */
    public abstract void executeBatchedStatements(AbstractSession session);
    
    /**
     * INTERNAL:
     * The mechanism will be cloned to be set into each accessor.
     */
    public BatchWritingMechanism clone() {
        try {
            return (BatchWritingMechanism)super.clone();
        } catch (CloneNotSupportedException notPossible) {
            throw new InternalError(notPossible.toString());
        }
    }

    /**
     * INTERNAL:
     * Allow initialization with the session after login.
     */
    public void initialize(AbstractSession session) {
        
    }

}
