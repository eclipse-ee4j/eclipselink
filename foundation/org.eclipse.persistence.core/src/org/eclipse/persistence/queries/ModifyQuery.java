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
package org.eclipse.persistence.queries;

import org.eclipse.persistence.internal.sessions.AbstractRecord;

/**
 * <p><b>Purpose</b>:
 * Abstract class for all modify queries.
 * Currently contains no behavior.
 *
 * @author Yvon Lavoie
 * @since TOPLink/Java 1.0
 */
public abstract class ModifyQuery extends DatabaseQuery {
    protected AbstractRecord modifyRow;
    
    // needed to allow the user to force SQL to database when batch writing is used. bug:4104613
    protected boolean forceBatchStatementExecution = false;
    
    /**
     * Define if this query is compatible with batch writing.
     * Some queries, such as DDL are not compatible.
     */
    protected boolean isBatchExecutionSupported = true;

    /**
     * INTERNAL:
     * Return the modify row
     */
    public AbstractRecord getModifyRow() {
        return modifyRow;
    }

    /**
     * PUBLIC:
     * Return if this is a modify query.
     */
    @Override
    public boolean isModifyQuery() {
        return true;
    }

    /**
     * INTERNAL:
     * Set the modify row
     */
    public void setModifyRow(AbstractRecord row) {
        modifyRow = row;
    }
    
    /**
     * PUBLIC:
     * Allow setting this query to be the last statement added to a batch statement 
     * and ensure it is flushed on execution.  Setting to true will cause the batch
     * statement to be sent to the database.  Default setting of false causes the batch 
     * statement execution to be delayed to allow additional statements to
     * be added.  Setting to true reduces the efficiency of batch writing.  
     * 
     * This has no effect if batch writing is not enabled.   
     */     
    public void setForceBatchStatementExecution(boolean value) {
        this.forceBatchStatementExecution = value;
    }
    
    /**
     * PUBLIC:
     * Returns if this query has been set to flush on execution.
     * @see #setForceBatchStatementExecution(boolean)
     */     
    public boolean forceBatchStatementExecution() {
        return forceBatchStatementExecution;
    }

    /**
     * PUBLIC:
     * Return if this query is compatible with batch writing.
     * Some queries, such as DDL are not compatible.
     */
    public boolean isBatchExecutionSupported() {
        return isBatchExecutionSupported;
    }

    /**
     * PUBLIC:
     * Set if this query is compatible with batch writing.
     * Some queries, such as DDL are not compatible.
     */
    public void setIsBatchExecutionSupported(boolean isBatchExecutionSupported) {
        this.isBatchExecutionSupported = isBatchExecutionSupported;
        setIsPrepared(false);
    }
}
