/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.internal.jpa.transaction;

import org.eclipse.persistence.internal.jpa.*;
import org.eclipse.persistence.internal.sessions.RepeatableWriteUnitOfWork;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;

public abstract class TransactionWrapperImpl  {

    protected EntityManagerImpl entityManager = null;
        
    //This attribute will store a reference to the non transactional UnitOfWork used
    // for queries outside of a transaction
    protected RepeatableWriteUnitOfWork localUOW;
        
    //used to cache the transactional UnitOfWork so that we do not need to look it up each time.
    protected Object txnKey;
    
    
    public TransactionWrapperImpl(EntityManagerImpl entityManager){
        this.entityManager = entityManager;
    }
        
    /**
     * INTERNAL:
     * This method will be used to check for a transaction and throws exception if none exists.
     * If this method returns without exception then a transaction exists.
     * This method must be called before accessing the localUOW.
     */
    public abstract Object checkForTransaction(boolean validateExistence);
 
    /**
     * INTERNAL:
     * Clears the transactional UnitOfWork
     */
    public void clear(){
        if (this.localUOW != null){
            // all change sets and cache are cleared
            this.localUOW.clear(true);
        }
    }
    
    public abstract void registerUnitOfWorkWithTxn(UnitOfWorkImpl uow);
    
    public abstract void verifyRegisterUnitOfWorkWithTxn();
    
    public UnitOfWorkImpl getLocalUnitOfWork(){
        return localUOW;
    }

    public void setLocalUnitOfWork(RepeatableWriteUnitOfWork uow){
        this.localUOW = uow;
    }

    /**
    * Mark the current transaction so that the only possible
    * outcome of the transaction is for the transaction to be
    * rolled back.
    * This is an internal method and if the txn is not active will do nothing
    */
    public abstract void setRollbackOnlyInternal();
    
    /**
     * This method will be called when a query is executed.  If changes in the entity manager
     * should be flushed this method should return true
     */
    public abstract boolean shouldFlushBeforeQuery(UnitOfWorkImpl uow);
    
}
